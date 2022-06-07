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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.pinot.common.response.broker.BrokerResponseNative;
import org.apache.pinot.common.response.broker.ResultTable;
import org.apache.pinot.common.utils.DataSchema;
import org.apache.pinot.common.utils.DataSchema.ColumnDataType;
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
import org.apache.pinot.spi.utils.CommonConstants;
import org.apache.pinot.spi.utils.ReadMode;
import org.apache.pinot.spi.utils.builder.TableConfigBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


/**
 * Queries test for INT, LONG, FLOAT, DOUBLE and BIG_DECIMAL data types with nullHandlingEnabled
 * where all input values are nulls.
 */
public class AllNullQueriesTest extends BaseQueriesTest {
  private static final File INDEX_DIR = new File(FileUtils.getTempDirectory(), "AllNullQueriesTest");
  private static final String RAW_TABLE_NAME = "testTable";
  private static final String SEGMENT_NAME = "testSegment";

  private static final int NUM_RECORDS = 1000;
  private static List<GenericRow> _records;
  private static BigDecimal _sumPrecision;
  private static double _sum;

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

  @BeforeClass
  public void setUp()
      throws Exception {
    FileUtils.deleteDirectory(INDEX_DIR);

    _records = new ArrayList<>(NUM_RECORDS);
    for (int i = 0; i < NUM_RECORDS; i++) {
      GenericRow record = new GenericRow();
      record.putValue(COLUMN_NAME, null);
      _records.add(record);
    }
    _sumPrecision = BigDecimal.ZERO;
    _sum = 0;
  }

  private void setUp(TableConfig tableConfig, DataType dataType)
      throws Exception {
    FileUtils.deleteDirectory(INDEX_DIR);

    Schema SCHEMA;
    if (dataType == DataType.BIG_DECIMAL) {
      SCHEMA = new Schema.SchemaBuilder().addMetric(COLUMN_NAME, dataType).build();
    } else {
      SCHEMA = new Schema.SchemaBuilder().addSingleValueDimension(COLUMN_NAME, dataType).build();
    }

    SegmentGeneratorConfig segmentGeneratorConfig = new SegmentGeneratorConfig(tableConfig, SCHEMA);
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
  public void testQueriesWithDictLongColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.LONG;
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 1)
  public void testQueriesWithNoDictLongColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.LONG;
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 2)
  public void testQueriesWithDictFloatColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.FLOAT;
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 3)
  public void testQueriesWithNoDictFloatColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.FLOAT;
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 4)
  public void testQueriesWithDictDoubleColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.DOUBLE;
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 5)
  public void testQueriesWithNoDictDoubleColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.DOUBLE;
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 6)
  public void testQueriesWithDictIntColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.INT;
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 7)
  public void testQueriesWithNoDictIntColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.INT;
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 8)
  public void testQueriesWithDictBigDecimalColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.BIG_DECIMAL;
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  @Test(priority = 9)
  public void testQueriesWithNoDictBigDecimalColumn()
      throws Exception {
    ColumnDataType columnDataType = ColumnDataType.BIG_DECIMAL;
    List<String> noDictionaryColumns = new ArrayList<String>();
    noDictionaryColumns.add(COLUMN_NAME);
    TableConfig tableConfig = new TableConfigBuilder(TableType.OFFLINE)
        .setTableName(RAW_TABLE_NAME)
        .setNoDictionaryColumns(noDictionaryColumns)
        .build();
    setUp(tableConfig, columnDataType.toDataType());
    testQueries(columnDataType);
  }

  public void testQueries(ColumnDataType columnDataType) {
    DataType dataType = columnDataType.toDataType();
    Map<String, Object> pinotConfigProperties = new HashMap<>();
    pinotConfigProperties.put(CommonConstants.Server.CONFIG_OF_CURRENT_DATA_TABLE_VERSION, 4);
    {
      String query = String.format(
          "SELECT count(*) as count1, count(%s) as count2, min(%s) as min, max(%s) as max FROM testTable", COLUMN_NAME,
          COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"count1", "count2", "min", "max"}, new ColumnDataType[]{
          ColumnDataType.LONG, ColumnDataType.LONG, ColumnDataType.DOUBLE, ColumnDataType.DOUBLE}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      Object[] row = rows.get(0);
      assertEquals(row.length, 4);
      // Note: count(*) returns total number of docs (nullable and non-nullable).
      assertEquals((long) row[0], 1000 * 4);
      // count(col) returns the count of non-nullable docs.
      assertEquals((long) row[1], 0);
      // TODO: this should return null similar to Presto.
      if (dataType == DataType.FLOAT) {
        assertEquals(((Double) row[2]).floatValue(), Float.POSITIVE_INFINITY);
      } else if (dataType == DataType.LONG) {
        assertEquals(((Double) row[2]).longValue(), Long.MAX_VALUE);
      }
      // TODO: this should return null similar to Presto.
      if (dataType == DataType.FLOAT) {
        assertEquals(((Double) row[3]).floatValue(), Float.NEGATIVE_INFINITY);
      } else if (dataType == DataType.LONG) {
        assertEquals(((Double) row[3]).longValue(), Long.MIN_VALUE);
      }
    }
    {
      String query = "SELECT *, 1 FROM testTable";
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME, "'1'"},
          new ColumnDataType[]{columnDataType, ColumnDataType.INT}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 10);
      for (int i = 0; i < 10; i++) {
        Object[] row = rows.get(i);
        assertEquals(row.length, 2);
        if (row[0] != null) {
          assertEquals(row[0], i);
          assertEquals(row[1], 1);
        }
      }
    }
    {
      String query = String.format("SELECT * FROM testTable ORDER BY %s DESC LIMIT 4000", COLUMN_NAME);
      // getBrokerResponseForSqlQuery(query) runs SQL query on multiple index segments. The result should be equivalent
      // to querying 4 identical index segments.
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 4000);
      for (int i = 0; i < 4000; i += 4) {
        for (int j = 0; j < 4; j++) {
          Object[] values = rows.get(i + j);
          assertEquals(values.length, 1);
          assertNull(values[0]);
        }
      }
    }
    {
      String query = String.format("SELECT DISTINCT %s FROM testTable ORDER BY %s", COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      for (Object[] row : rows) {
        assertEquals(row.length, 1);
        assertNull(row[0]);
      }
    }
    {
      int limit = 40;
      String query = String.format("SELECT DISTINCT %s FROM testTable ORDER BY %s LIMIT %d", COLUMN_NAME, COLUMN_NAME,
          limit);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      for (Object[] row : rows) {
        assertEquals(row.length, 1);
        assertNull(row[0]);
      }
    }
    {
      // This test case was added to validate path-code for distinct w/o order by.
      int limit = 40;
      String query = String.format("SELECT DISTINCT %s FROM testTable LIMIT %d", COLUMN_NAME, limit);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
    }
    {
      String query = String.format("SELECT COUNT(%s) AS count, MIN(%s) AS min, MAX(%s) AS max, AVG(%s) AS avg,"
              + " SUM(%s) AS sum FROM testTable LIMIT 1000", COLUMN_NAME, COLUMN_NAME, COLUMN_NAME, COLUMN_NAME,
          COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"count", "min", "max", "avg", "sum"}, new ColumnDataType[]{
          ColumnDataType.LONG, ColumnDataType.DOUBLE, ColumnDataType.DOUBLE, ColumnDataType.DOUBLE,
          ColumnDataType.DOUBLE}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      assertEquals((long) rows.get(0)[0], 0);
      // TODO: this should return null similar to Presto.
      if (dataType == DataType.INT) {
        assertEquals(((Double) rows.get(0)[1]).longValue(), Integer.MAX_VALUE);
      } else {
        assertEquals(((Double) rows.get(0)[1]).longValue(), Long.MAX_VALUE);
      }
      // TODO: this should return null similar to Presto.
      if (dataType == DataType.INT) {
        assertEquals(((Double) rows.get(0)[2]).longValue(), Integer.MIN_VALUE);
      } else {
        assertEquals(((Double) rows.get(0)[2]).longValue(), Long.MIN_VALUE);
      }
      double avg = _sum / (double) _records.size();
      assertEquals(rows.get(0)[3], avg);
      assertEquals(rows.get(0)[4], 4 * _sum);
    }
    {
      String query = String.format("SELECT %s FROM testTable GROUP BY %s ORDER BY %s DESC", COLUMN_NAME, COLUMN_NAME,
          COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      assertEquals(rows.get(0).length, 1);
      assertNull(rows.get(0)[0]);
    }
    {
      String query = String.format(
          "SELECT COUNT(*) AS count, %s FROM testTable GROUP BY %s ORDER BY %s DESC LIMIT 1000", COLUMN_NAME,
          COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"count", COLUMN_NAME},
          new ColumnDataType[]{ColumnDataType.LONG, columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      Object[] row = rows.get(0);
      assertEquals(row[0], 4000L);
      assertNull(row[1]);
    }
    {
      String query = String.format("SELECT SUMPRECISION(%s) AS sum FROM testTable", COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"sum"}, new ColumnDataType[]{ColumnDataType.STRING}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      assertEquals(new BigDecimal((String) rows.get(0)[0]), _sumPrecision.multiply(BigDecimal.valueOf(4)));
    }
    {
      // Note: in Presto, inequality, equality, and IN comparison with nulls always returns false:
      long lowerLimit = 69;
      String query = String.format("SELECT %s FROM testTable WHERE %s > '%s' LIMIT 50", COLUMN_NAME, COLUMN_NAME,
          lowerLimit);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      // Pinot loops through the column values from smallest to biggest. Null comparison always returns false.
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 0);
    }
    {
      String query = String.format("SELECT %s FROM testTable WHERE %s = '%s'", COLUMN_NAME, COLUMN_NAME, 68);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 0);
    }
    {
      String query = String.format("SELECT %s FROM testTable WHERE %s = '%s'", COLUMN_NAME, COLUMN_NAME, 69);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema,
          new DataSchema(new String[]{COLUMN_NAME}, new ColumnDataType[]{columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 0);
    }
    {
      String query = String.format("SELECT COUNT(%s) AS count, MIN(%s) AS min, MAX(%s) AS max, SUM(%s) AS sum"
          + " FROM testTable GROUP BY %s HAVING max < %s ORDER BY max", COLUMN_NAME, COLUMN_NAME, COLUMN_NAME,
          COLUMN_NAME, COLUMN_NAME, 20);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      assertTrue(resultTable == null || resultTable.getRows().size() == 0);
    }
    {
      String query = String.format("SELECT COUNT(%s) AS count, MIN(%s) AS min, MAX(%s) AS max, SUM(%s) AS sum"
              + " FROM testTable GROUP BY %s ORDER BY max", COLUMN_NAME, COLUMN_NAME, COLUMN_NAME, COLUMN_NAME,
          COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"count", "min", "max", "sum"}, new ColumnDataType[]{
          ColumnDataType.LONG, ColumnDataType.DOUBLE, ColumnDataType.DOUBLE, ColumnDataType.DOUBLE
      }));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      Object[] row = rows.get(0);
      assertEquals(row.length, 4);
      assertEquals(row[0], 4000L);
      assertNull(row[1]);
      assertNull(row[2]);
      assertNull(row[3]);
    }
    {
      String query = String.format(
          "SELECT AVG(%s) AS avg FROM testTable GROUP BY %s ORDER BY avg LIMIT 20", COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"avg"}, new ColumnDataType[]{ColumnDataType.DOUBLE}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      Object[] row = rows.get(0);
      assertEquals(row.length, 1);
      assertNull(row[0]);
    }
    {
      // MODE cannot handle BIG_DECIMAL yet.
      if (dataType != DataType.BIG_DECIMAL) {
        String query = String.format("SELECT AVG(%s) AS avg, MODE(%s) AS mode, DISTINCTCOUNT(%s) as distinct_count" +
                " FROM testTable GROUP BY %s ORDER BY %s LIMIT 200", COLUMN_NAME, COLUMN_NAME, COLUMN_NAME, COLUMN_NAME,
            COLUMN_NAME);
        BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
        ResultTable resultTable = brokerResponse.getResultTable();
        DataSchema dataSchema = resultTable.getDataSchema();
        assertEquals(dataSchema, new DataSchema(new String[]{"avg", "mode", "distinct_count"}, new ColumnDataType[]{ColumnDataType.DOUBLE, ColumnDataType.DOUBLE, ColumnDataType.INT}));
        List<Object[]> rows = resultTable.getRows();
        assertEquals(rows.size(), 1);
        Object[] row = rows.get(0);
        assertEquals(row.length, 3);
        assertNull(row[0]);
        // TODO: this should return null instead of default value.
        if (dataType == DataType.DOUBLE || dataType == DataType.FLOAT) {
          assertEquals(row[1], Double.NEGATIVE_INFINITY);
        } else if (dataType == DataType.LONG) {
          assertEquals(((Double) row[1]).longValue(), Long.MIN_VALUE);
        }
        assertEquals(row[2], 1);
      }
    }
    {
      // If updated limit to include all records, I get back results unsorted.
      String query = String.format("SELECT MAX(%s) AS max, %s FROM testTable GROUP BY %s ORDER BY max LIMIT 501",
          COLUMN_NAME, COLUMN_NAME, COLUMN_NAME);
      BrokerResponseNative brokerResponse = getBrokerResponse(query, pinotConfigProperties);
      ResultTable resultTable = brokerResponse.getResultTable();
      DataSchema dataSchema = resultTable.getDataSchema();
      assertEquals(dataSchema, new DataSchema(new String[]{"max", COLUMN_NAME},
          new ColumnDataType[]{ColumnDataType.DOUBLE, columnDataType}));
      List<Object[]> rows = resultTable.getRows();
      assertEquals(rows.size(), 1);
      assertNull(rows.get(0)[0]);
    }
  }

  @AfterClass
  public void tearDown()
      throws IOException {
    _indexSegment.destroy();
    FileUtils.deleteDirectory(INDEX_DIR);
  }
}
