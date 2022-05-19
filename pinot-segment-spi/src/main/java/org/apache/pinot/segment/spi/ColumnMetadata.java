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
package org.apache.pinot.segment.spi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.pinot.segment.spi.partition.PartitionFunction;
import org.apache.pinot.spi.annotations.InterfaceAudience;
import org.apache.pinot.spi.data.FieldSpec;
import org.apache.pinot.spi.data.FieldSpec.DataType;
import org.apache.pinot.spi.data.FieldSpec.FieldType;


/**
 * The <code>ColumnMetadata</code> class holds the column level management information and data statistics.
 */
@InterfaceAudience.Private
@SuppressWarnings("rawtypes")
public interface ColumnMetadata {

  FieldSpec getFieldSpec();

  default String getColumnName() {
    return getFieldSpec().getName();
  }

  default FieldType getFieldType() {
    return getFieldSpec().getFieldType();
  }

  default DataType getDataType() {
    return getFieldSpec().getDataType();
  }

  default boolean isSingleValue() {
    return getFieldSpec().isSingleValueField();
  }

  int getTotalDocs();

  /**
   * NOTE: When a realtime segment has no-dictionary columns, the cardinality for those columns will be set to
   * {@link Constants#UNKNOWN_CARDINALITY}.
   */
  int getCardinality();

  boolean isSorted();

  Comparable getMinValue();

  Comparable getMaxValue();

  boolean hasNull();

  @JsonProperty
  boolean hasDictionary();

  int getColumnMaxLength();

  char getPaddingCharacter();

  int getBitsPerElement();

  int getMaxNumberOfMultiValues();

  int getTotalNumberOfEntries();

  @Nullable
  PartitionFunction getPartitionFunction();

  @Nullable
  Set<Integer> getPartitions();

  boolean isAutoGenerated();
}
