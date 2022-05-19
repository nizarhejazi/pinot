/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.core.operator.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.apache.pinot.common.request.context.ExpressionContext;
import org.apache.pinot.common.request.context.OrderByExpressionContext;
import org.apache.pinot.common.utils.DataSchema;
import org.apache.pinot.core.common.BlockValSet;
import org.apache.pinot.core.common.Operator;
import org.apache.pinot.core.common.RowBasedBlockValueFetcher;
import org.apache.pinot.core.operator.BaseOperator;
import org.apache.pinot.core.operator.BitmapDocIdSetOperator;
import org.apache.pinot.core.operator.ExecutionStatistics;
import org.apache.pinot.core.operator.ProjectionOperator;
import org.apache.pinot.core.operator.blocks.IntermediateResultsBlock;
import org.apache.pinot.core.operator.blocks.TransformBlock;
import org.apache.pinot.core.operator.transform.TransformOperator;
import org.apache.pinot.core.operator.transform.TransformResultMetadata;
import org.apache.pinot.core.query.request.context.QueryContext;
import org.apache.pinot.core.query.selection.SelectionOperatorUtils;
import org.apache.pinot.segment.spi.IndexSegment;
import org.apache.pinot.segment.spi.datasource.DataSource;
import org.apache.pinot.spi.data.FieldSpec;
import org.apache.pinot.spi.data.FieldSpec.DataType;
import org.apache.pinot.spi.utils.ByteArray;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.buffer.ImmutableRoaringBitmap;


/**
 * Operator for selection order-by queries.
 * <p>The operator uses a priority queue to sort the rows and return the top rows based on the order-by expressions.
 * <p>It is optimized to fetch only the values needed for the ordering purpose and the final result:
 * <ul>
 *   <li>
 *     When all the output expressions are ordered, the operator fetches all the output expressions and insert them into
 *     the priority queue because all the values are needed for ordering.
 *   </li>
 *   <li>
 *     Otherwise, the operator fetches only the order-by expressions and the virtual document id column and insert them
 *     into the priority queue. After getting the top rows, the operator does a second round scan only on the document
 *     ids for the top rows for the non-order-by output expressions. This optimization can significantly reduce the
 *     scanning and improve the query performance when most/all of the output expressions are not ordered (e.g. SELECT *
 *     FROM table ORDER BY col).
 *   </li>
 * </ul>
 */
public class SelectionOrderByOperator extends BaseOperator<IntermediateResultsBlock> {

  private static final String EXPLAIN_NAME = "SELECT_ORDERBY";

  private final IndexSegment _indexSegment;
  // Deduped order-by expressions followed by output expressions from SelectionOperatorUtils.extractExpressions()
  private final List<ExpressionContext> _expressions;
  private final TransformOperator _transformOperator;
  private final List<OrderByExpressionContext> _orderByExpressions;
  private final TransformResultMetadata[] _orderByExpressionMetadata;
  private final FieldSpec[] _orderByColumnFieldSpecs;
  private final int _numRowsToKeep;
  private final PriorityQueue<Object[]> _rows;
  private final boolean _allOrderByColsPreSorted;
  private final ImmutableRoaringBitmap[] _nullBitmaps;

  private int _numDocsScanned = 0;
  private long _numEntriesScannedPostFilter = 0;

  public SelectionOrderByOperator(IndexSegment indexSegment, QueryContext queryContext,
      List<ExpressionContext> expressions, TransformOperator transformOperator, boolean allOrderByColsPreSorted) {
    _indexSegment = indexSegment;
    _expressions = expressions;
    _transformOperator = transformOperator;
    _allOrderByColsPreSorted = allOrderByColsPreSorted;

    _orderByExpressions = queryContext.getOrderByExpressions();
    assert _orderByExpressions != null;
    int numOrderByExpressions = _orderByExpressions.size();
    _orderByExpressionMetadata = new TransformResultMetadata[numOrderByExpressions];
    _orderByColumnFieldSpecs = new FieldSpec[numOrderByExpressions];
    for (int i = 0; i < numOrderByExpressions; i++) {
      ExpressionContext expression = _orderByExpressions.get(i).getExpression();
      _orderByExpressionMetadata[i] = _transformOperator.getResultMetadata(expression);
      _orderByColumnFieldSpecs[i] = _transformOperator.getFieldSpec(expression);
    }

    _numRowsToKeep = queryContext.getOffset() + queryContext.getLimit();
    _rows = new PriorityQueue<>(Math.min(_numRowsToKeep, SelectionOperatorUtils.MAX_ROW_HOLDER_INITIAL_CAPACITY),
        getComparator());
    _nullBitmaps = new ImmutableRoaringBitmap[numOrderByExpressions];
  }

  @Override
  public String toExplainString() {
    StringBuilder stringBuilder = new StringBuilder(EXPLAIN_NAME).append("(selectList:");
    if (!_expressions.isEmpty()) {
      stringBuilder.append(_expressions.get(0));
      for (int i = 1; i < _expressions.size(); i++) {
        stringBuilder.append(", ").append(_expressions.get(i));
      }
    }
    return stringBuilder.append(')').toString();
  }

  private Comparator<Object[]> getComparator() {
    // Compare all single-value columns
    int numOrderByExpressions = _orderByExpressions.size();
    List<Integer> valueIndexList = new ArrayList<>(numOrderByExpressions);
    for (int i = 0; i < numOrderByExpressions; i++) {
      if (_orderByExpressionMetadata[i].isSingleValue()) {
        valueIndexList.add(i);
      }
    }

    int numValuesToCompare = valueIndexList.size();
    int[] valueIndices = new int[numValuesToCompare];
    DataType[] storedTypes = new DataType[numValuesToCompare];
    // Use multiplier -1 or 1 to control ascending/descending order
    int[] multipliers = new int[numValuesToCompare];
    for (int i = 0; i < numValuesToCompare; i++) {
      int valueIndex = valueIndexList.get(i);
      valueIndices[i] = valueIndex;
      storedTypes[i] = _orderByExpressionMetadata[valueIndex].getDataType().getStoredType();
      multipliers[i] = _orderByExpressions.get(valueIndex).isAsc() ? -1 : 1;
    }

    // todo(nhejazi): return diff. comparator that doesn't handle nulls for columns that don't require null handling.
    return (Object[] o1, Object[] o2) -> {
      for (int i = 0; i < numValuesToCompare; i++) {
        int index = valueIndices[i];

        // TODO: Evaluate the performance of casting to Comparable and avoid the switch
        Object v1 = o1[index];
        Object v2 = o2[index];

        if (v1 == null) {
          // The default null ordering is: 'NULLS LAST'.
          return v2 == null ? 0 : 1;
        } else if (v2 == null) {
          return -1;
        }

        int result;
        switch (storedTypes[i]) {
          case INT:
            result = ((Integer) v1).compareTo((Integer) v2);
            break;
          case LONG:
            result = ((Long) v1).compareTo((Long) v2);
            break;
          case FLOAT:
            result = ((Float) v1).compareTo((Float) v2);
            break;
          case DOUBLE:
            result = ((Double) v1).compareTo((Double) v2);
            break;
          case BIG_DECIMAL:
            result = ((BigDecimal) v1).compareTo((BigDecimal) v2);
            break;
          case STRING:
            result = ((String) v1).compareTo((String) v2);
            break;
          case BYTES:
            result = ((ByteArray) v1).compareTo((ByteArray) v2);
            break;
          // NOTE: Multi-value columns are not comparable, so we should not reach here
          default:
            throw new IllegalStateException();
        }
        if (result != 0) {
          return result * multipliers[i];
        }
      }
      return 0;
    };
  }

  @Override
  protected IntermediateResultsBlock getNextBlock() {
    if (_allOrderByColsPreSorted) {
      return computeAllPreSorted();
    } else if (_expressions.size() == _orderByExpressions.size()) {
      return computeAllOrdered();
    } else {
      return computePartiallyOrdered();
    }
  }

  private IntermediateResultsBlock computeAllPreSorted() {
    int numExpressions = _expressions.size();

    // Fetch all the expressions and insert them into the priority queue
    BlockValSet[] blockValSets = new BlockValSet[numExpressions];
    int numColumnsProjected = _transformOperator.getNumColumnsProjected();
    TransformBlock transformBlock;
    while (_numDocsScanned < _numRowsToKeep && (transformBlock = _transformOperator.nextBlock()) != null) {
      for (int i = 0; i < numExpressions; i++) {
        ExpressionContext expression = _expressions.get(i);
        blockValSets[i] = transformBlock.getBlockValueSet(expression);
      }
      RowBasedBlockValueFetcher blockValueFetcher = new RowBasedBlockValueFetcher(blockValSets);
      int numDocsFetched = transformBlock.getNumDocs();
      for (int i = 0; i < numExpressions; i++) {
        _nullBitmaps[i] = blockValueFetcher.getColumnNullBitmap(i);
      }

      for (int rowId = 0; rowId < numDocsFetched && (_numDocsScanned < _numRowsToKeep); rowId++) {
        Object[] row = blockValueFetcher.getRow(rowId);
        for (int colId = 0; colId < numExpressions; colId++) {
          if (_nullBitmaps[colId].contains(rowId)) {
            row[colId] = null;
          }
        }

        SelectionOperatorUtils.addToPriorityQueue(row, _rows, _numRowsToKeep);
        _numDocsScanned++;
      }
    }
    _numEntriesScannedPostFilter = (long) _numDocsScanned * numColumnsProjected;

    // Create the data schema
    String[] columnNames = new String[numExpressions];
    DataSchema.ColumnDataType[] columnDataTypes = new DataSchema.ColumnDataType[numExpressions];
    FieldSpec[] columnFieldSpecs = new FieldSpec[numExpressions];
    for (int i = 0; i < numExpressions; i++) {
      ExpressionContext expression = _expressions.get(i);
      columnNames[i] = expression.toString();
      TransformResultMetadata expressionMetadata = _transformOperator.getResultMetadata(expression);
      columnDataTypes[i] =
          DataSchema.ColumnDataType.fromDataType(expressionMetadata.getDataType(), expressionMetadata.isSingleValue());
      columnFieldSpecs[i] = _transformOperator.getFieldSpec(expression);
    }

    DataSchema dataSchema = new DataSchema(columnNames, columnDataTypes, columnFieldSpecs);


    return new IntermediateResultsBlock(dataSchema, _rows);
  }

  /**
   * Helper method to compute the result when all the output expressions are ordered.
   */
  private IntermediateResultsBlock computeAllOrdered() {
    int numExpressions = _expressions.size();

    // Fetch all the expressions and insert them into the priority queue
    BlockValSet[] blockValSets = new BlockValSet[numExpressions];
    int numColumnsProjected = _transformOperator.getNumColumnsProjected();
    TransformBlock transformBlock;
    while ((transformBlock = _transformOperator.nextBlock()) != null) {
      for (int i = 0; i < numExpressions; i++) {
        ExpressionContext expression = _expressions.get(i);
        blockValSets[i] = transformBlock.getBlockValueSet(expression);
      }
      RowBasedBlockValueFetcher blockValueFetcher = new RowBasedBlockValueFetcher(blockValSets);
      int numDocsFetched = transformBlock.getNumDocs();
      for (int i = 0; i < numExpressions; i++) {
        _nullBitmaps[i] = blockValueFetcher.getColumnNullBitmap(i);
      }

      for (int rowId = 0; rowId < numDocsFetched; rowId++) {
        Object[] row = blockValueFetcher.getRow(rowId);
        for (int colId = 0; colId < numExpressions; colId++) {
          if (_nullBitmaps[colId].contains(rowId)) {
            row[colId] = null;
          }
        }
        SelectionOperatorUtils.addToPriorityQueue(row, _rows, _numRowsToKeep);
      }
      _numDocsScanned += numDocsFetched;
    }
    _numEntriesScannedPostFilter = (long) _numDocsScanned * numColumnsProjected;

    // Create the data schema
    String[] columnNames = new String[numExpressions];
    DataSchema.ColumnDataType[] columnDataTypes = new DataSchema.ColumnDataType[numExpressions];
    for (int i = 0; i < numExpressions; i++) {
      columnNames[i] = _expressions.get(i).toString();
      TransformResultMetadata expressionMetadata = _orderByExpressionMetadata[i];
      columnDataTypes[i] =
          DataSchema.ColumnDataType.fromDataType(expressionMetadata.getDataType(), expressionMetadata.isSingleValue());
    }
    DataSchema dataSchema = new DataSchema(columnNames, columnDataTypes, _orderByColumnFieldSpecs);

    return new IntermediateResultsBlock(dataSchema, _rows);
  }

  /**
   * Helper method to compute the result when not all the output expressions are ordered.
   */
  private IntermediateResultsBlock computePartiallyOrdered() {
    int numExpressions = _expressions.size();
    int numOrderByExpressions = _orderByExpressions.size();

    // Fetch the order-by expressions and docIds and insert them into the priority queue
    BlockValSet[] blockValSets = new BlockValSet[numOrderByExpressions];
    int numColumnsProjected = _transformOperator.getNumColumnsProjected();
    TransformBlock transformBlock;
    while ((transformBlock = _transformOperator.nextBlock()) != null) {
      for (int i = 0; i < numOrderByExpressions; i++) {
        ExpressionContext expression = _orderByExpressions.get(i).getExpression();
        blockValSets[i] = transformBlock.getBlockValueSet(expression);
      }
      RowBasedBlockValueFetcher blockValueFetcher = new RowBasedBlockValueFetcher(blockValSets);
      int numDocsFetched = transformBlock.getNumDocs();
      for (int i = 0; i < numExpressions; i++) {
        _nullBitmaps[i] = blockValueFetcher.getColumnNullBitmap(i);
      }

      int[] docIds = transformBlock.getDocIds();
      for (int rowId = 0; rowId < numDocsFetched; rowId++) {
        // NOTE: We pre-allocate the complete row so that we can fill up the non-order-by output expression values later
        //       without creating extra rows or re-constructing the priority queue. We can change the values in-place
        //       because the comparator only compare the values for the order-by expressions.
        Object[] row = new Object[numExpressions];
        blockValueFetcher.getRow(rowId, row, 0);
        row[numOrderByExpressions] = docIds[rowId];
        // todo(nhejazi): when filling the non-order-by output expression values later in the row, you should also
        //  handle nulls.
        // todo(nhejazi): storing default value per column for now instead of relying on FieldSpec (metric vs. dimension)
        //  since passing through column field type requires lots of plumming. Will do in separate PR.
        boolean isMetric = true;
        for (int colId = 0; colId < numOrderByExpressions; colId++) {
          if (_nullBitmaps[colId].contains(rowId)) {
            row[colId] = null;
          }
        }
        SelectionOperatorUtils.addToPriorityQueue(row, _rows, _numRowsToKeep);
      }
      _numDocsScanned += numDocsFetched;
    }
    _numEntriesScannedPostFilter = (long) _numDocsScanned * numColumnsProjected;

    // Copy the rows (shallow copy so that any modification will also be reflected to the priority queue) into a list,
    // and store the document ids into a bitmap
    int numRows = _rows.size();
    List<Object[]> rowList = new ArrayList<>(numRows);
    RoaringBitmap docIds = new RoaringBitmap();
    for (Object[] row : _rows) {
      rowList.add(row);
      int docId = (int) row[numOrderByExpressions];
      docIds.add(docId);
    }

    // Sort the rows with docIds to match the order of the bitmap (bitmap always returns values in ascending order)
    rowList.sort(Comparator.comparingInt(o -> (int) o[numOrderByExpressions]));

    // Construct a new TransformOperator to fetch the non-order-by expressions for the top rows
    List<ExpressionContext> nonOrderByExpressions = _expressions.subList(numOrderByExpressions, numExpressions);
    Set<String> columns = new HashSet<>();
    for (ExpressionContext expressionContext : nonOrderByExpressions) {
      expressionContext.getColumns(columns);
    }
    int numColumns = columns.size();
    Map<String, DataSource> dataSourceMap = new HashMap<>();
    for (String column : columns) {
      dataSourceMap.put(column, _indexSegment.getDataSource(column));
    }
    ProjectionOperator projectionOperator =
        new ProjectionOperator(dataSourceMap, new BitmapDocIdSetOperator(docIds, numRows));
    TransformOperator transformOperator = new TransformOperator(projectionOperator, nonOrderByExpressions);

    // Fill the non-order-by expression values
    int numNonOrderByExpressions = nonOrderByExpressions.size();
    blockValSets = new BlockValSet[numNonOrderByExpressions];
    int rowBaseId = 0;
    while ((transformBlock = transformOperator.nextBlock()) != null) {
      for (int i = 0; i < numNonOrderByExpressions; i++) {
        ExpressionContext expression = nonOrderByExpressions.get(i);
        blockValSets[i] = transformBlock.getBlockValueSet(expression);
      }
      RowBasedBlockValueFetcher blockValueFetcher = new RowBasedBlockValueFetcher(blockValSets);
      int numDocsFetched = transformBlock.getNumDocs();
      for (int i = 0; i < numDocsFetched; i++) {
        blockValueFetcher.getRow(i, rowList.get(rowBaseId + i), numOrderByExpressions);
      }
      _numEntriesScannedPostFilter += (long) numDocsFetched * numColumns;
      rowBaseId += numDocsFetched;
    }

    // Create the data schema
    String[] columnNames = new String[numExpressions];
    DataSchema.ColumnDataType[] columnDataTypes = new DataSchema.ColumnDataType[numExpressions];
    FieldSpec[] columnFieldSpecs = new FieldSpec[numExpressions];
    for (int i = 0; i < numExpressions; i++) {
      columnNames[i] = _expressions.get(i).toString();
    }
    for (int i = 0; i < numOrderByExpressions; i++) {
      TransformResultMetadata expressionMetadata = _orderByExpressionMetadata[i];
      columnDataTypes[i] =
          DataSchema.ColumnDataType.fromDataType(expressionMetadata.getDataType(), expressionMetadata.isSingleValue());
      columnFieldSpecs[i] = _orderByColumnFieldSpecs[i];
    }
    for (int i = 0; i < numNonOrderByExpressions; i++) {
      TransformResultMetadata expressionMetadata = transformOperator.getResultMetadata(nonOrderByExpressions.get(i));
      columnDataTypes[numOrderByExpressions + i] =
          DataSchema.ColumnDataType.fromDataType(expressionMetadata.getDataType(), expressionMetadata.isSingleValue());
      columnFieldSpecs[numOrderByExpressions + i] = transformOperator.getFieldSpec(nonOrderByExpressions.get(i));
    }
    DataSchema dataSchema = new DataSchema(columnNames, columnDataTypes, columnFieldSpecs);

    return new IntermediateResultsBlock(dataSchema, _rows);
  }


  @Override
  public List<Operator> getChildOperators() {
    return Collections.singletonList(_transformOperator);
  }

  public IndexSegment getIndexSegment() {
    return _indexSegment;
  }

  @Override
  public ExecutionStatistics getExecutionStatistics() {
    long numEntriesScannedInFilter = _transformOperator.getExecutionStatistics().getNumEntriesScannedInFilter();
    int numTotalDocs = _indexSegment.getSegmentMetadata().getTotalDocs();
    return new ExecutionStatistics(_numDocsScanned, numEntriesScannedInFilter, _numEntriesScannedPostFilter,
        numTotalDocs);
  }
}
