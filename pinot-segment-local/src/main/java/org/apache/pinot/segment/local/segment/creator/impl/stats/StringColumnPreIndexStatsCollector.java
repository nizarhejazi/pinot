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
package org.apache.pinot.segment.local.segment.creator.impl.stats;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Arrays;
import org.apache.pinot.segment.spi.creator.StatsCollectorConfig;

import static java.nio.charset.StandardCharsets.UTF_8;


public class StringColumnPreIndexStatsCollector extends AbstractColumnStatisticsCollector {
  private final ObjectSet<String> _values = new ObjectOpenHashSet<>(INITIAL_HASH_SET_SIZE);

  private int _minLength = Integer.MAX_VALUE;
  private int _maxLength = 0;
  private int _maxRowLength = 0;
  private String[] _sortedValues;
  private boolean _sealed = false;
  private boolean _hasNull = false;

  public StringColumnPreIndexStatsCollector(String column, StatsCollectorConfig statsCollectorConfig) {
    super(column, statsCollectorConfig);
  }

  @Override
  public void collect(Object entry) {
    if (entry instanceof Object[]) {
      Object[] values = (Object[]) entry;
      int rowLength = 0;
      for (Object obj : values) {
        String value = (String) obj;
        _values.add(value);

        int length = value.getBytes(UTF_8).length;
        _minLength = Math.min(_minLength, length);
        _maxLength = Math.max(_maxLength, length);
        rowLength += length;
      }

      _maxNumberOfMultiValues = Math.max(_maxNumberOfMultiValues, values.length);
      _maxRowLength = Math.max(_maxRowLength, rowLength);
      updateTotalNumberOfEntries(values);
    } else {
      String value = (String) entry;
      addressSorted(value);
      if (_values.add(value)) {
        updatePartition(value);
        int valueLength = value.getBytes(UTF_8).length;
        _minLength = Math.min(_minLength, valueLength);
        _maxLength = Math.max(_maxLength, valueLength);
        _maxRowLength = _maxLength;
      }
      _totalNumberOfEntries++;
    }
  }

  @Override
  public String getMinValue() {
    if (_sealed) {
      return _sortedValues[0];
    }
    throw new IllegalStateException("you must seal the collector first before asking for min value");
  }

  @Override
  public String getMaxValue() {
    if (_sealed) {
      return _sortedValues[_sortedValues.length - 1];
    }
    throw new IllegalStateException("you must seal the collector first before asking for max value");
  }

  @Override
  public Object[] getUniqueValuesSet() {
    if (_sealed) {
      return _sortedValues;
    }
    throw new IllegalStateException("you must seal the collector first before asking for unique values set");
  }

  @Override
  public int getLengthOfLargestElement() {
    if (_sealed) {
      return _maxLength;
    }
    throw new IllegalStateException("you must seal the collector first before asking for longest value");
  }

  @Override
  public int getMaxRowLengthInBytes() {
    return _maxRowLength;
  }

  @Override
  public int getCardinality() {
    if (_sealed) {
      return _sortedValues.length;
    }
    throw new IllegalStateException("you must seal the collector first before asking for cardinality");
  }

  @Override
  public void seal() {
    _sortedValues = _values.toArray(new String[0]);
    Arrays.sort(_sortedValues);
    _sealed = true;
  }
}
