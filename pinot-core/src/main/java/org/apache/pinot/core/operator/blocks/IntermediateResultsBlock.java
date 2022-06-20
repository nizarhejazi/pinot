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
package org.apache.pinot.core.operator.blocks;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.pinot.common.exception.QueryException;
import org.apache.pinot.common.response.ProcessingException;
import org.apache.pinot.common.utils.DataSchema;
import org.apache.pinot.common.utils.DataSchema.ColumnDataType;
import org.apache.pinot.common.utils.DataTable;
import org.apache.pinot.common.utils.DataTable.MetadataKey;
import org.apache.pinot.core.common.Block;
import org.apache.pinot.core.common.BlockDocIdSet;
import org.apache.pinot.core.common.BlockDocIdValueSet;
import org.apache.pinot.core.common.BlockMetadata;
import org.apache.pinot.core.common.BlockValSet;
import org.apache.pinot.core.common.datatable.DataTableBuilder;
import org.apache.pinot.core.common.datatable.DataTableFactory;
import org.apache.pinot.core.data.table.IntermediateRecord;
import org.apache.pinot.core.data.table.Record;
import org.apache.pinot.core.data.table.Table;
import org.apache.pinot.core.query.aggregation.function.AggregationFunction;
import org.apache.pinot.core.query.aggregation.groupby.AggregationGroupByResult;
import org.apache.pinot.core.query.selection.SelectionOperatorUtils;
import org.apache.pinot.spi.data.FieldSpec;
import org.apache.pinot.spi.utils.ByteArray;
import org.roaringbitmap.RoaringBitmap;


/**
 * The <code>IntermediateResultsBlock</code> class is the holder of the server side inter-segment results.
 */
@SuppressWarnings("rawtypes")
public class IntermediateResultsBlock implements Block {
  private DataSchema _dataSchema;
  private boolean _isNullHandlingEnabled;
  private Collection<Object[]> _selectionResult;
  private AggregationFunction[] _aggregationFunctions;
  private List<Object> _aggregationResult;
  private AggregationGroupByResult _aggregationGroupByResult;
  private List<ProcessingException> _processingExceptions;
  private Collection<IntermediateRecord> _intermediateRecords;
  private long _numDocsScanned;
  private long _numEntriesScannedInFilter;
  private long _numEntriesScannedPostFilter;
  private long _numTotalDocs;
  private int _numSegmentsProcessed;
  private int _numSegmentsMatched;
  private boolean _numGroupsLimitReached;
  private int _numResizes;
  private long _resizeTimeMs;
  private long _executionThreadCpuTimeNs;
  private int _numServerThreads;

  private Table _table;

  public IntermediateResultsBlock() {
  }

  /**
   * Constructor for selection result.
   */
  public IntermediateResultsBlock(DataSchema dataSchema, Collection<Object[]> selectionResult,
      boolean isNullHandlingEnabled) {
    _dataSchema = dataSchema;
    _selectionResult = selectionResult;
    _isNullHandlingEnabled = isNullHandlingEnabled;
  }

  /**
   * Constructor for aggregation result.
   * <p>For aggregation only, the result is a list of values.
   * <p>For aggregation group-by, the result is a list of maps from group keys to aggregation values.
   */
  public IntermediateResultsBlock(AggregationFunction[] aggregationFunctions, List<Object> aggregationResult,
      boolean isNullHandlingEnabled) {
    _aggregationFunctions = aggregationFunctions;
    _aggregationResult = aggregationResult;
    _isNullHandlingEnabled = isNullHandlingEnabled;
  }

  /**
   * Constructor for aggregation result.
   * <p>For aggregation only, the result is a list of values.
   * <p>For aggregation group-by, the result is a list of maps from group keys to aggregation values.
   */
  public IntermediateResultsBlock(AggregationFunction[] aggregationFunctions, List<Object> aggregationResult,
      DataSchema dataSchema, boolean isNullHandlingEnabled) {
    _aggregationFunctions = aggregationFunctions;
    _aggregationResult = aggregationResult;
    _dataSchema = dataSchema;
    _isNullHandlingEnabled = isNullHandlingEnabled;
  }

  /**
   * Constructor for aggregation group-by order-by result with {@link AggregationGroupByResult}.
   */
  public IntermediateResultsBlock(AggregationFunction[] aggregationFunctions,
      @Nullable AggregationGroupByResult aggregationGroupByResults, DataSchema dataSchema,
      boolean isNullHandlingEnabled) {
    _aggregationFunctions = aggregationFunctions;
    _aggregationGroupByResult = aggregationGroupByResults;
    _dataSchema = dataSchema;
    _isNullHandlingEnabled = isNullHandlingEnabled;
  }

  /**
   * Constructor for aggregation group-by order-by result with {@link AggregationGroupByResult} and
   * with a collection of intermediate records.
   */
  public IntermediateResultsBlock(AggregationFunction[] aggregationFunctions,
      Collection<IntermediateRecord> intermediateRecords, DataSchema dataSchema, boolean isNullHandlingEnabled) {
    _aggregationFunctions = aggregationFunctions;
    _dataSchema = dataSchema;
    _intermediateRecords = intermediateRecords;
    _isNullHandlingEnabled = isNullHandlingEnabled;
  }

  public IntermediateResultsBlock(Table table) {
    _table = table;
    if (_table != null) {
      _dataSchema = table.getDataSchema();
    }
    // TODO: set based on whether null handling is enabled in GroupKeyGenerator.
    _isNullHandlingEnabled = true;
  }

  /**
   * Constructor for exception block.
   */
  public IntermediateResultsBlock(ProcessingException processingException, Exception e) {
    _processingExceptions = new ArrayList<>();
    _processingExceptions.add(QueryException.getException(processingException, e));
    _isNullHandlingEnabled = false;
  }

  /**
   * Constructor for exception block.
   */
  public IntermediateResultsBlock(Exception e) {
    this(QueryException.QUERY_EXECUTION_ERROR, e);
  }

  @Nullable
  public DataSchema getDataSchema() {
    return _dataSchema;
  }

  public void setDataSchema(DataSchema dataSchema) {
    _dataSchema = dataSchema;
  }

  public boolean isNullHandlingEnabled() {
    return _isNullHandlingEnabled;
  }

  @Nullable
  public Collection<Object[]> getSelectionResult() {
    return _selectionResult;
  }

  public void setSelectionResult(Collection<Object[]> rowEventsSet) {
    _selectionResult = rowEventsSet;
  }

  @Nullable
  public AggregationFunction[] getAggregationFunctions() {
    return _aggregationFunctions;
  }

  public void setAggregationFunctions(AggregationFunction[] aggregationFunctions) {
    _aggregationFunctions = aggregationFunctions;
  }

  @Nullable
  public List<Object> getAggregationResult() {
    return _aggregationResult;
  }

  public void setAggregationResults(List<Object> aggregationResults) {
    _aggregationResult = aggregationResults;
  }

  @Nullable
  public AggregationGroupByResult getAggregationGroupByResult() {
    return _aggregationGroupByResult;
  }

  @Nullable
  public List<ProcessingException> getProcessingExceptions() {
    return _processingExceptions;
  }

  public void setProcessingExceptions(List<ProcessingException> processingExceptions) {
    _processingExceptions = processingExceptions;
  }

  public void addToProcessingExceptions(ProcessingException processingException) {
    if (_processingExceptions == null) {
      _processingExceptions = new ArrayList<>();
    }
    _processingExceptions.add(processingException);
  }

  public void setNumResizes(int numResizes) {
    _numResizes = numResizes;
  }

  public void setResizeTimeMs(long resizeTimeMs) {
    _resizeTimeMs = resizeTimeMs;
  }

  public long getExecutionThreadCpuTimeNs() {
    return _executionThreadCpuTimeNs;
  }

  public void setExecutionThreadCpuTimeNs(long executionThreadCpuTimeNs) {
    _executionThreadCpuTimeNs = executionThreadCpuTimeNs;
  }

  public int getNumServerThreads() {
    return _numServerThreads;
  }

  public void setNumServerThreads(int numServerThreads) {
    _numServerThreads = numServerThreads;
  }

  @VisibleForTesting
  public long getNumDocsScanned() {
    return _numDocsScanned;
  }

  public void setNumDocsScanned(long numDocsScanned) {
    _numDocsScanned = numDocsScanned;
  }

  @VisibleForTesting
  public long getNumEntriesScannedInFilter() {
    return _numEntriesScannedInFilter;
  }

  public void setNumEntriesScannedInFilter(long numEntriesScannedInFilter) {
    _numEntriesScannedInFilter = numEntriesScannedInFilter;
  }

  @VisibleForTesting
  public long getNumEntriesScannedPostFilter() {
    return _numEntriesScannedPostFilter;
  }

  public void setNumEntriesScannedPostFilter(long numEntriesScannedPostFilter) {
    _numEntriesScannedPostFilter = numEntriesScannedPostFilter;
  }

  @VisibleForTesting
  public int getNumSegmentsProcessed() {
    return _numSegmentsProcessed;
  }

  public void setNumSegmentsProcessed(int numSegmentsProcessed) {
    _numSegmentsProcessed = numSegmentsProcessed;
  }

  @VisibleForTesting
  public int getNumSegmentsMatched() {
    return _numSegmentsMatched;
  }

  public void setNumSegmentsMatched(int numSegmentsMatched) {
    _numSegmentsMatched = numSegmentsMatched;
  }

  @VisibleForTesting
  public long getNumTotalDocs() {
    return _numTotalDocs;
  }

  public void setNumTotalDocs(long numTotalDocs) {
    _numTotalDocs = numTotalDocs;
  }

  @VisibleForTesting
  public boolean isNumGroupsLimitReached() {
    return _numGroupsLimitReached;
  }

  public void setNumGroupsLimitReached(boolean numGroupsLimitReached) {
    _numGroupsLimitReached = numGroupsLimitReached;
  }

  /**
   * Get the collection of intermediate records
   */
  @Nullable
  public Collection<IntermediateRecord> getIntermediateRecords() {
    return _intermediateRecords;
  }

  public DataTable getDataTable()
      throws Exception {

    if (_table != null) {
      return getResultDataTable();
    }

    // TODO: remove all these ifs once every operator starts using {@link Table}
    if (_selectionResult != null) {
      return getSelectionResultDataTable();
    }

    if (_aggregationResult != null) {
      return getAggregationResultDataTable();
    }

    return getMetadataDataTable();
  }

  private DataTable getResultDataTable()
      throws IOException {
    DataTableBuilder dataTableBuilder = DataTableFactory.getDataTableBuilder(_dataSchema);
    ColumnDataType[] storedColumnDataTypes = _dataSchema.getStoredColumnDataTypes();
    int numColumns = _dataSchema.size();
    Iterator<Record> iterator = _table.iterator();
    RoaringBitmap[] nullBitmaps = null;
    if (_isNullHandlingEnabled) {
      nullBitmaps = new RoaringBitmap[numColumns];
      Object[] colDefaultNullValues = new Object[numColumns];
      for (int colId = 0; colId < numColumns; colId++) {
        if (storedColumnDataTypes[colId] != ColumnDataType.OBJECT) {
          colDefaultNullValues[colId] = FieldSpec.getDefaultNullValue(FieldSpec.FieldType.METRIC,
              storedColumnDataTypes[colId].toDataType(), null);
        }
        nullBitmaps[colId] = new RoaringBitmap();
      }
      int rowId = 0;
      while (iterator.hasNext()) {
        Object[] values = iterator.next().getValues();
        dataTableBuilder.startRow();
        for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
          Object value = values[columnIndex];
          if (value == null) {
            value = colDefaultNullValues[columnIndex];
            nullBitmaps[columnIndex].add(rowId);
          }
          setDataTableColumn(storedColumnDataTypes[columnIndex], dataTableBuilder, columnIndex, value);
        }
        dataTableBuilder.finishRow();
        rowId++;
      }
    } else {
      while (iterator.hasNext()) {
        Record record = iterator.next();
        dataTableBuilder.startRow();
        int columnIndex = 0;
        for (Object value : record.getValues()) {
          setDataTableColumn(storedColumnDataTypes[columnIndex], dataTableBuilder, columnIndex, value);
          columnIndex++;
        }
        dataTableBuilder.finishRow();
      }
    }
    if (_isNullHandlingEnabled && DataTableFactory.getDataTableVersion() >= DataTableFactory.VERSION_4) {
      for (int colId = 0; colId < numColumns; colId++) {
        dataTableBuilder.setNullRowIds(nullBitmaps[colId]);
      }
    }
    DataTable dataTable = dataTableBuilder.build();
    return attachMetadataToDataTable(dataTable);
  }

  private void setDataTableColumn(ColumnDataType columnDataType, DataTableBuilder dataTableBuilder, int columnIndex,
      Object value)
      throws IOException {
    switch (columnDataType) {
      case INT:
        dataTableBuilder.setColumn(columnIndex, (int) value);
        break;
      case LONG:
        dataTableBuilder.setColumn(columnIndex, (long) value);
        break;
      case FLOAT:
        dataTableBuilder.setColumn(columnIndex, (float) value);
        break;
      case DOUBLE:
        dataTableBuilder.setColumn(columnIndex, (double) value);
        break;
      case BIG_DECIMAL:
        dataTableBuilder.setColumn(columnIndex, (BigDecimal) value);
        break;
      case STRING:
        dataTableBuilder.setColumn(columnIndex, (String) value);
        break;
      case BYTES:
        dataTableBuilder.setColumn(columnIndex, (ByteArray) value);
        break;
      case OBJECT:
        dataTableBuilder.setColumn(columnIndex, value);
        break;
      case INT_ARRAY:
        dataTableBuilder.setColumn(columnIndex, (int[]) value);
        break;
      case LONG_ARRAY:
        dataTableBuilder.setColumn(columnIndex, (long[]) value);
        break;
      case FLOAT_ARRAY:
        dataTableBuilder.setColumn(columnIndex, (float[]) value);
        break;
      case DOUBLE_ARRAY:
        dataTableBuilder.setColumn(columnIndex, (double[]) value);
        break;
      case STRING_ARRAY:
        dataTableBuilder.setColumn(columnIndex, (String[]) value);
        break;
      default:
        throw new IllegalStateException();
    }
  }

  private DataTable getSelectionResultDataTable()
      throws Exception {
    return attachMetadataToDataTable(SelectionOperatorUtils.getDataTableFromRows(
        _selectionResult, _dataSchema, _isNullHandlingEnabled));
  }

  private DataTable getAggregationResultDataTable()
      throws Exception {
    // Extract result column name and type from each aggregation function
    int numAggregationFunctions = _aggregationFunctions.length;
    String[] columnNames = new String[numAggregationFunctions];
    ColumnDataType[] columnDataTypes = new ColumnDataType[numAggregationFunctions];
    for (int i = 0; i < numAggregationFunctions; i++) {
      AggregationFunction aggregationFunction = _aggregationFunctions[i];
      columnNames[i] = aggregationFunction.getColumnName();
      columnDataTypes[i] = aggregationFunction.getIntermediateResultColumnType();
    }
    Object[] colDefaultNullValues = null;
    RoaringBitmap[] nullBitmaps = null;
    if (_isNullHandlingEnabled) {
      for (int i = 0; i < numAggregationFunctions; i++) {
        if (colDefaultNullValues == null) {
          colDefaultNullValues = new Object[numAggregationFunctions];
          nullBitmaps = new RoaringBitmap[numAggregationFunctions];
        }
        if (columnDataTypes[i] != ColumnDataType.OBJECT) {
          colDefaultNullValues[i] = FieldSpec.getDefaultNullValue(FieldSpec.FieldType.METRIC,
              columnDataTypes[i].toDataType(), null);
        }
        nullBitmaps[i] = new RoaringBitmap();
      }
    }

    // Build the data table.
    DataTableBuilder dataTableBuilder =
        DataTableFactory.getDataTableBuilder(new DataSchema(columnNames, columnDataTypes));
    dataTableBuilder.startRow();
    for (int i = 0; i < numAggregationFunctions; i++) {
      Object value = _aggregationResult.get(i);
      // OBJECT (e.g. DistinctTable) calls toBytes() (e.g. DistinctTable.toBytes()) which takes care of replacing nulls
      // with default values, and building presence vector and serializing both.
      if (_isNullHandlingEnabled && columnDataTypes[i] != ColumnDataType.OBJECT) {
        if (value == null) {
          value = colDefaultNullValues[i];
          nullBitmaps[i].add(0);
        }
      }

      switch (columnDataTypes[i]) {
        case LONG:
          dataTableBuilder.setColumn(i, ((Number) value).longValue());
          break;
        case DOUBLE:
          dataTableBuilder.setColumn(i, ((Double) value).doubleValue());
          break;
        case OBJECT:
          dataTableBuilder.setColumn(i, value);
          break;
        default:
          throw new UnsupportedOperationException(
              "Unsupported aggregation column data type: " + columnDataTypes[i] + " for column: " + columnNames[i]);
      }
    }
    dataTableBuilder.finishRow();
    if (_isNullHandlingEnabled && DataTableFactory.getDataTableVersion() >= DataTableFactory.VERSION_4) {
      for (int i = 0; i < numAggregationFunctions; i++) {
        dataTableBuilder.setNullRowIds(nullBitmaps[i]);
      }
    }
    DataTable dataTable = dataTableBuilder.build();

    return attachMetadataToDataTable(dataTable);
  }

  private DataTable getMetadataDataTable() {
    return attachMetadataToDataTable(DataTableFactory.getEmptyDataTable());
  }

  private DataTable attachMetadataToDataTable(DataTable dataTable) {
    dataTable.getMetadata().put(MetadataKey.NUM_DOCS_SCANNED.getName(), String.valueOf(_numDocsScanned));
    dataTable.getMetadata()
        .put(MetadataKey.NUM_ENTRIES_SCANNED_IN_FILTER.getName(), String.valueOf(_numEntriesScannedInFilter));
    dataTable.getMetadata()
        .put(MetadataKey.NUM_ENTRIES_SCANNED_POST_FILTER.getName(), String.valueOf(_numEntriesScannedPostFilter));
    dataTable.getMetadata().put(MetadataKey.NUM_SEGMENTS_PROCESSED.getName(), String.valueOf(_numSegmentsProcessed));
    dataTable.getMetadata().put(MetadataKey.NUM_SEGMENTS_MATCHED.getName(), String.valueOf(_numSegmentsMatched));
    dataTable.getMetadata().put(MetadataKey.NUM_RESIZES.getName(), String.valueOf(_numResizes));
    dataTable.getMetadata().put(MetadataKey.RESIZE_TIME_MS.getName(), String.valueOf(_resizeTimeMs));

    dataTable.getMetadata().put(MetadataKey.TOTAL_DOCS.getName(), String.valueOf(_numTotalDocs));
    if (_numGroupsLimitReached) {
      dataTable.getMetadata().put(MetadataKey.NUM_GROUPS_LIMIT_REACHED.getName(), "true");
    }
    if (_processingExceptions != null && !_processingExceptions.isEmpty()) {
      for (ProcessingException exception : _processingExceptions) {
        dataTable.addException(exception);
      }
    }
    return dataTable;
  }

  @Override
  public BlockDocIdSet getBlockDocIdSet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockValSet getBlockValueSet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockDocIdValueSet getBlockDocIdValueSet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockMetadata getMetadata() {
    throw new UnsupportedOperationException();
  }
}
