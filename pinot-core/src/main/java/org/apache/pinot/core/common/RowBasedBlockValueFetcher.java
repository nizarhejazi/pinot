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
package org.apache.pinot.core.common;

import java.math.BigDecimal;
import org.apache.pinot.spi.data.FieldSpec.DataType;
import org.apache.pinot.spi.utils.ByteArray;
import org.roaringbitmap.RoaringBitmap;


public class RowBasedBlockValueFetcher {
  private final ValueFetcher[] _valueFetchers;

  public RowBasedBlockValueFetcher(BlockValSet[] blockValSets) {
    int numColumns = blockValSets.length;
    _valueFetchers = new ValueFetcher[numColumns];
    for (int i = 0; i < numColumns; i++) {
      _valueFetchers[i] = createFetcher(blockValSets[i]);
    }
  }

  public Object[] getRow(int docId) {
    int numColumns = _valueFetchers.length;
    Object[] row = new Object[numColumns];
    for (int i = 0; i < numColumns; i++) {
      row[i] = _valueFetchers[i].getValue(docId);
    }
    return row;
  }

  public RoaringBitmap getColumnNullBitmap(int colId) {
    // TODO: If null handling is enabled, we can directly set null in getRow() instead of relying on the caller to fill
    //  the null values
    if (SingleValueFetcher.class.isAssignableFrom(_valueFetchers[colId].getClass())) {
      return ((SingleValueFetcher) _valueFetchers[colId]).getNullBitmap();
    }
    return null;
  }

  public void getRow(int docId, Object[] buffer, int startIndex) {
    for (ValueFetcher valueFetcher : _valueFetchers) {
      buffer[startIndex++] = valueFetcher.getValue(docId);
    }
  }

  private ValueFetcher createFetcher(BlockValSet blockValSet) {
    DataType storedType = blockValSet.getValueType().getStoredType();
    if (blockValSet.isSingleValue()) {
      switch (storedType) {
        case INT:
          return new IntSingleValueFetcher(blockValSet.getIntValuesSV(), blockValSet.getNullBitmap());
        case LONG:
          return new LongSingleValueFetcher(blockValSet.getLongValuesSV(), blockValSet.getNullBitmap());
        case FLOAT:
          return new FloatSingleValueFetcher(blockValSet.getFloatValuesSV(), blockValSet.getNullBitmap());
        case DOUBLE:
          return new DoubleSingleValueFetcher(blockValSet.getDoubleValuesSV(), blockValSet.getNullBitmap());
        case BIG_DECIMAL:
          return new BigDecimalValueFetcher(blockValSet.getBigDecimalValuesSV(), blockValSet.getNullBitmap());
        case STRING:
          return new StringSingleValueFetcher(blockValSet.getStringValuesSV(), blockValSet.getNullBitmap());
        case BYTES:
          return new BytesValueFetcher(blockValSet.getBytesValuesSV(), blockValSet.getNullBitmap());
        default:
          throw new IllegalStateException("Unsupported value type: " + storedType + " for single-value column");
      }
    } else {
      switch (storedType) {
        case INT:
          return new IntMultiValueFetcher(blockValSet.getIntValuesMV());
        case LONG:
          return new LongMultiValueFetcher(blockValSet.getLongValuesMV());
        case FLOAT:
          return new FloatMultiValueFetcher(blockValSet.getFloatValuesMV());
        case DOUBLE:
          return new DoubleMultiValueFetcher(blockValSet.getDoubleValuesMV());
        case STRING:
          return new StringMultiValueFetcher(blockValSet.getStringValuesMV());
        default:
          throw new IllegalStateException("Unsupported value type: " + storedType + " for multi-value column");
      }
    }
  }

  private interface ValueFetcher {
    Object getValue(int docId);
  }

  private static abstract class SingleValueFetcher implements ValueFetcher {
    private final RoaringBitmap _nullBitmap;

    public SingleValueFetcher(RoaringBitmap nullBitmap) {
      _nullBitmap = nullBitmap;
    }

    public boolean isNull(int docId) {
      return _nullBitmap.contains(docId);
    }

    public RoaringBitmap getNullBitmap() {
      return _nullBitmap;
    }

    abstract public int getRowCount();
  }

  private static class IntSingleValueFetcher extends SingleValueFetcher {
    private final int[] _values;

    IntSingleValueFetcher(int[] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public Integer getValue(int docId) {
      return _values[docId];
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class LongSingleValueFetcher extends SingleValueFetcher {
    private final long[] _values;

    LongSingleValueFetcher(long[] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public Long getValue(int docId) {
      return _values[docId];
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class FloatSingleValueFetcher extends SingleValueFetcher {
    private final float[] _values;

    FloatSingleValueFetcher(float[] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public Float getValue(int docId) {
      return _values[docId];
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class DoubleSingleValueFetcher extends SingleValueFetcher {
    private final double[] _values;

    DoubleSingleValueFetcher(double[] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public Double getValue(int docId) {
      return _values[docId];
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class BigDecimalValueFetcher extends SingleValueFetcher {
    private final BigDecimal[] _values;

    BigDecimalValueFetcher(BigDecimal[] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public BigDecimal getValue(int docId) {
      return _values[docId];
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class StringSingleValueFetcher extends SingleValueFetcher {
    private final String[] _values;

    StringSingleValueFetcher(String[] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public String getValue(int docId) {
      return _values[docId];
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class BytesValueFetcher extends SingleValueFetcher {
    private final byte[][] _values;

    BytesValueFetcher(byte[][] values, RoaringBitmap nullBitmap) {
      super(nullBitmap);
      _values = values;
    }

    public ByteArray getValue(int docId) {
      return new ByteArray(_values[docId]);
    }

    @Override
    public int getRowCount() {
      return _values.length;
    }
  }

  private static class IntMultiValueFetcher implements ValueFetcher {
    private final int[][] _values;

    IntMultiValueFetcher(int[][] values) {
      _values = values;
    }

    public int[] getValue(int docId) {
      return _values[docId];
    }
  }

  private static class LongMultiValueFetcher implements ValueFetcher {
    private final long[][] _values;

    LongMultiValueFetcher(long[][] values) {
      _values = values;
    }

    public long[] getValue(int docId) {
      return _values[docId];
    }
  }

  private static class FloatMultiValueFetcher implements ValueFetcher {
    private final float[][] _values;

    FloatMultiValueFetcher(float[][] values) {
      _values = values;
    }

    public float[] getValue(int docId) {
      return _values[docId];
    }
  }

  private static class DoubleMultiValueFetcher implements ValueFetcher {
    private final double[][] _values;

    DoubleMultiValueFetcher(double[][] values) {
      _values = values;
    }

    public double[] getValue(int docId) {
      return _values[docId];
    }
  }

  private static class StringMultiValueFetcher implements ValueFetcher {
    private final String[][] _values;

    StringMultiValueFetcher(String[][] values) {
      _values = values;
    }

    public String[] getValue(int docId) {
      return _values[docId];
    }
  }
}
