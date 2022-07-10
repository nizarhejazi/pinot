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
package org.apache.pinot.queries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.pinot.common.response.broker.BrokerResponseNative;
import org.apache.pinot.common.response.broker.ResultTable;
import org.apache.pinot.common.utils.DataSchema;
import org.apache.pinot.common.utils.DataSchema.ColumnDataType;
import org.apache.pinot.core.common.datatable.DataTableFactory;
import org.apache.pinot.segment.local.indexsegment.immutable.ImmutableSegmentLoader;
import org.apache.pinot.segment.local.segment.creator.impl.SegmentIndexCreationDriverImpl;
import org.apache.pinot.segment.local.segment.readers.GenericRowRecordReader;
import org.apache.pinot.segment.spi.ImmutableSegment;
import org.apache.pinot.segment.spi.IndexSegment;
import org.apache.pinot.segment.spi.creator.SegmentGeneratorConfig;
import org.apache.pinot.spi.config.table.TableConfig;
import org.apache.pinot.spi.config.table.TableType;
import org.apache.pinot.spi.data.FieldSpec.DataType;
import org.apache.pinot.spi.data.Schema;
import org.apache.pinot.spi.data.readers.GenericRow;
import org.apache.pinot.spi.utils.ReadMode;
import org.apache.pinot.spi.utils.builder.TableConfigBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


/**
 * Queries test for FLOAT and DOUBLE data types with nullHandlingEnabled.
 */
public class NullEnabledQueriesTest extends BaseQueriesTest {
  private static final File INDEX_DIR = new File(FileUtils.getTempDirectory(), "NullEnabledQueriesTest");
  private static final String RAW_TABLE_NAME = "testTable";
  private static final String SEGMENT_NAME = "testSegment";
  private static final Random RANDOM = new Random();

  private static final int NUM_RECORDS = 1000;
  private static List<GenericRow> _records;

  private static final String COLUMN_NAME = "column";

  private IndexSegment _indexSegment;
  private List<IndexSegment> _indexSegments;

  @Override
  protected String getFilter() {
    return "";
  }

  @Override
  protected IndexSegment getIndexSegment() {
    return _indexSegment;
  }

  @Override
  protected List<IndexSegment> getIndexSegments() {
    return _indexSegments;
  }

  public void createRecords(Number baseValue)
      throws Exception {
    FileUtils.deleteDirectory(INDEX_DIR);

    _records = new ArrayList<>(NUM_RECORDS);
    for (int i = 0; i < NUM_RECORDS; i++) {
      GenericRow record = new GenericRow();
      double value = baseValue.doubleValue() + i;
      if (i % 2 == 0) {
        record.putValue(COLUMN_NAME, value);
      } else {
        record.putValue(COLUMN_NAME, null);
      }
      _records.add(record);
    }
  }

  private void setUp(TableConfig tableConfig, DataType dataType)
      throws Exception {
    FileUtils.deleteDirectory(INDEX_DIR);

    Schema schema;
    if (dataType == DataType.BIG_DECIMAL) {
      schema = new Schema.SchemaBuilder().addMetric(COLUMN_NAME, dataType).build();
    } else {
      schema = new Schema.SchemaBuilder().addSingleValueDimension(COLUMN_NAME, dataType).build();
    }

    SegmentGeneratorConfig segmentGeneratorConfig = new SegmentGeneratorConfig(tableConfig, schema);
    segmentGeneratorConfig.setTableName(RAW_TABLE_NAME);
    segmentGeneratorConfig.setSegmentName(SEGMENT_NAME);
    segmentGeneratorConfig.setNullHandlingEnabled(true);
    segmentGeneratorConfig.setOutDir(INDEX_DIR.getPath());

    SegmentIndexCreationDriverImpl driver = new SegmentIndexCreationDriverImpl();
    driver.init(segmentGeneratorConfig, new GenericRowRecordReader(_records));
    driver.build();

    ImmutableSegment immutableSegment = ImmutableSegmentLoader.load(new File(INDEX_DIR, SEGMENT_NAME), ReadMode.mmap);
    _indexSegment = immutableSegment;
    _indexSegments = Arrays.asList(immutableSegment, immutableSegment);
  }

  @Test
  public void testQueriesWithDictFloatColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.FLOAT;
    float baseValue = RANDOM.nextFloat();
    createRecords(baseValue);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(baseValue, columnDataType);
  }

  @Test(priority = 1)
  public void testQueriesWithNoDictFloatColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.FLOAT;
    float baseValue = RANDOM.nextFloat();
    createRecords(baseValue);
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(baseValue, columnDataType);
  }

  @Test(priority = 2)
  public void testQueriesWithDictDoubleColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.DOUBLE;
    double baseValue = RANDOM.nextDouble();
    createRecords(baseValue);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(baseValue, columnDataType);
  }

  @Test(priority = 3)
  public void testQueriesWithNoDictDoubleColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.DOUBLE;
    double baseValue = RANDOM.nextDouble();
    createRecords(baseValue);
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(baseValue, columnDataType);
  }

  public void testQueries(Number baseValue, ColumnDataType dataType) {
    DataTableFactory.setDataTableVersion(DataTableFactory.VERSION_4);
    Map<String, String> queryOptions = new HashMap<>();
    queryOptions.put("nullHandlingEnabled", "true");
    {
      String query = "SELECT *, 1 FROM testTable";
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME, "'1'"},
          new ColumnDataType[]{dataType, ColumnDataType.INT}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 10);
      for (int i = 0; i < 10; i++) {
        Object[] row = rows.get(i);
        assertEquals(row.length, 2);
        if (row[0] != null) {
          assertTrue(Math.abs(((Number) row[0]).doubleValue() - (baseValue.doubleValue() + i)) < 1e-1);
          assertTrue(Math.abs(((Number) row[0]).doubleValue() - (baseValue.doubleValue() + i)) < 1e-1);
          assertEquals(row[1], 1);
        }
      }
    }
    {
      String query = String.format("SELECT * FROM testTable ORDER BY %s DESC LIMIT 4000", COLUMN_NAME);
      // getBrokerResponseForSqlQuery(query) runs SQL query on multiple index segments. The result should be equivalent
      // to querying 4 identical index segments.
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 4000);
      int k = 0;
      for (int i = 0; i < 2000; i += 4) {
        // Null values are inserted at indices where: index % 2 equals 1. Skip null values.
        if ((NUM_RECORDS - 1 - k) % 2 == 1) {
          k++;
        }
        for (int j = 0; j < 4; j++) {
          Object[] values = rows.get(i + j);
          assertEquals(values.length, 1);
          assertTrue(Math.abs(((Number) values[0]).doubleValue() - (baseValue.doubleValue() + (NUM_RECORDS - 1 - k)))
              < 1e-1);
        }
        k++;
      }
      // Note 1: we inserted 500 nulls in _records, and since we query 4 identical index segments, the number of null
      //  values is: 500 * 4 = 2000.
      // Note 2: The default null ordering is 'NULLS LAST', regardless of the ordering direction.
      for (int i = 2000; i < 4000; i++) {
        Object[] values = rows.get(i);
        assertEquals(values.length, 1);
        assertNull(values[0]);
      }
    }
    {
      String query = String.format("SELECT DISTINCT %s FROM testTable ORDER BY %s", COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 10);
      int i = 0;
      int index = 0;
      while (index < rows.size() - 1) {
        Object[] row = rows.get(index);
        assertEquals(row.length, 1);
        // Null values are inserted at indices where: index % 2 equals 1. All null values are grouped into a single null
        // value (because of DISTINCT aggregation function).
        if (i % 2 == 1) {
          i++;
        }
        assertTrue(Math.abs(((Number) row[0]).doubleValue() - (baseValue.doubleValue() + i)) < 1e-1);
        i++;
        index++;
      }
      // The default null ordering is 'NULLS LAST'. Therefore, null will appear as the last record.
      assertNull(rows.get(rows.size() - 1)[0]);
    }
    {
      int limit = 40;
      String query = String.format("SELECT DISTINCT %s FROM testTable ORDER BY %s LIMIT %d", COLUMN_NAME, COLUMN_NAME,
          limit);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      int i = 0;
      int index = 0;
      while (index < rows.size() - 1) {
        Object[] row = rows.get(index);
        assertEquals(row.length, 1);
        // Null values are inserted at indices where: index % 2 equals 1. All null values are grouped into a single null
        // value (because of DISTINCT aggregation function).
        if (i % 2 == 1) {
          i++;
        }
        assertTrue(Math.abs(((Number) row[0]).doubleValue() - (baseValue.doubleValue() + i)) < 1e-1);
        i++;
        index++;
      }
      // The default null ordering is 'NULLS LAST'. Therefore, null will appear as the last record.
      assertNull(rows.get(rows.size() - 1)[0]);
    }
    {
      // This test case was added to validate path-code for distinct w/o order by.
      int limit = 40;
      String query = String.format("SELECT DISTINCT %s FROM testTable LIMIT %d", COLUMN_NAME, limit);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), limit);
    }
    {
      String query = String.format("SELECT %s FROM testTable GROUP BY %s ORDER BY %s DESC", COLUMN_NAME, COLUMN_NAME,
          COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 10);
      // The default null ordering is 'NULLS LAST'. Therefore, null will appear as the last record.
      assertNull(rows.get(0)[0]);
      int index = 1;
      int i = 0;
      while (index < rows.size()) {
        if ((NUM_RECORDS - i - 1) % 2 == 1) {
          i++;
        }
        Object[] row = rows.get(index);
        assertEquals(row.length, 1);
        assertTrue(Math.abs(((Number) row[0]).doubleValue() - (baseValue.doubleValue() + (NUM_RECORDS - i - 1)))
            < 1e-1);
        index++;
        i++;
      }
    }
    {
      String query = String.format(
          "SELECT COUNT(*) AS count, %s FROM testTable GROUP BY %s ORDER BY %s DESC LIMIT 1000", COLUMN_NAME,
          COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"count", COLUMN_NAME},
          new ColumnDataType[]{ColumnDataType.LONG, dataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 501);
      int i = 0;
      for (int index = 0; index < 500; index++) {
        Object[] row = rows.get(index);
        assertEquals(row.length, 2);
        if ((NUM_RECORDS - i - 1) % 2 == 1) {
          // Null values are inserted at: index % 2 == 1. All null values are grouped into a single null.
          i++;
        }
        assertEquals(row[0], 4L);
        assertTrue(Math.abs(((Number) row[1]).doubleValue() - (baseValue.doubleValue() + (NUM_RECORDS - i - 1)))
            < 1e-1);
        i++;
      }
      // The default null ordering is 'NULLS LAST'.
      Object[] row = rows.get(500);
      assertEquals(row[0], 2000L);
      assertNull(row[1]);
    }
    {
      String query = String.format("SELECT %s FROM testTable WHERE %s = '%s'", COLUMN_NAME, COLUMN_NAME,
          baseValue.doubleValue() + 68);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 4);
      for (int i = 0; i < 4; i++) {
        Object[] row = rows.get(i);
        assertEquals(row.length, 1);
        assertTrue(Math.abs(((Number) row[0]).doubleValue() - (baseValue.floatValue() + 68)) < 1e-1);
      }
    }
    {
      String query = String.format("SELECT %s FROM testTable WHERE %s = '%s'", COLUMN_NAME, COLUMN_NAME,
          baseValue.doubleValue() + 69);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{dataType}));
      List<Object[]> rows = resultTable.getRows();
      // 69 % 2 == 1 (and so a null was inserted instead of 69 + BASE_FLOAT).
      assertEquals(rows.size(), 0);
    }
    {
      int lowerLimit = 991;
      String query = String.format(
          "SELECT MAX(%s) AS max FROM testTable GROUP BY %s HAVING max > %s ORDER BY max", COLUMN_NAME, COLUMN_NAME,
          baseValue.doubleValue() + lowerLimit);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, queryOptions);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{"max"}, new ColumnDataType[]{ColumnDataType.DOUBLE}));
      List<Object[]> rows = resultTable.getRows();
      int i = lowerLimit;
      for (Object[] row : rows) {
        if (i % 2 == 1) {
          // Null values are inserted at: index % 2 == 1.
          i++;
        }
        assertEquals(row.length, 1);
        assertTrue(Math.abs((Double) row[0] - (baseValue.doubleValue() + i)) < 1e-1);
        i++;
      }
    }
    DataTableFactory.setDataTableVersion(DataTableFactory.VERSION_3);
  }

  @AfterClass
  public void tearDown()
      throws IOException {
    _indexSegment.destroy();
    FileUtils.deleteDirectory(INDEX_DIR);
  }
}
