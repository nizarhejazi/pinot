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
/**
 * Autogenerated by Thrift Compiler (0.15.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.apache.pinot.common.request;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
/**
 * AUTO GENERATED: DO NOT EDIT
 *  Query type
 * 
 */
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.15.0)", date = "2022-03-28")
public class QueryType implements org.apache.thrift.TBase<QueryType, QueryType._Fields>, java.io.Serializable, Cloneable, Comparable<QueryType> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("QueryType");

  private static final org.apache.thrift.protocol.TField HAS_SELECTION_FIELD_DESC = new org.apache.thrift.protocol.TField("hasSelection", org.apache.thrift.protocol.TType.BOOL, (short)1);
  private static final org.apache.thrift.protocol.TField HAS_FILTER_FIELD_DESC = new org.apache.thrift.protocol.TField("hasFilter", org.apache.thrift.protocol.TType.BOOL, (short)2);
  private static final org.apache.thrift.protocol.TField HAS_AGGREGATION_FIELD_DESC = new org.apache.thrift.protocol.TField("hasAggregation", org.apache.thrift.protocol.TType.BOOL, (short)3);
  private static final org.apache.thrift.protocol.TField HAS_GROUP_BY_FIELD_DESC = new org.apache.thrift.protocol.TField("hasGroup_by", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField HAS_HAVING_FIELD_DESC = new org.apache.thrift.protocol.TField("hasHaving", org.apache.thrift.protocol.TType.BOOL, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new QueryTypeStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new QueryTypeTupleSchemeFactory();

  private boolean hasSelection; // optional
  private boolean hasFilter; // optional
  private boolean hasAggregation; // optional
  private boolean hasGroup_by; // optional
  private boolean hasHaving; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    HAS_SELECTION((short)1, "hasSelection"),
    HAS_FILTER((short)2, "hasFilter"),
    HAS_AGGREGATION((short)3, "hasAggregation"),
    HAS_GROUP_BY((short)4, "hasGroup_by"),
    HAS_HAVING((short)5, "hasHaving");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // HAS_SELECTION
          return HAS_SELECTION;
        case 2: // HAS_FILTER
          return HAS_FILTER;
        case 3: // HAS_AGGREGATION
          return HAS_AGGREGATION;
        case 4: // HAS_GROUP_BY
          return HAS_GROUP_BY;
        case 5: // HAS_HAVING
          return HAS_HAVING;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __HASSELECTION_ISSET_ID = 0;
  private static final int __HASFILTER_ISSET_ID = 1;
  private static final int __HASAGGREGATION_ISSET_ID = 2;
  private static final int __HASGROUP_BY_ISSET_ID = 3;
  private static final int __HASHAVING_ISSET_ID = 4;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.HAS_SELECTION,_Fields.HAS_FILTER,_Fields.HAS_AGGREGATION,_Fields.HAS_GROUP_BY,_Fields.HAS_HAVING};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.HAS_SELECTION, new org.apache.thrift.meta_data.FieldMetaData("hasSelection", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.HAS_FILTER, new org.apache.thrift.meta_data.FieldMetaData("hasFilter", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.HAS_AGGREGATION, new org.apache.thrift.meta_data.FieldMetaData("hasAggregation", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.HAS_GROUP_BY, new org.apache.thrift.meta_data.FieldMetaData("hasGroup_by", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.HAS_HAVING, new org.apache.thrift.meta_data.FieldMetaData("hasHaving", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(QueryType.class, metaDataMap);
  }

  public QueryType() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public QueryType(QueryType other) {
    __isset_bitfield = other.__isset_bitfield;
    this.hasSelection = other.hasSelection;
    this.hasFilter = other.hasFilter;
    this.hasAggregation = other.hasAggregation;
    this.hasGroup_by = other.hasGroup_by;
    this.hasHaving = other.hasHaving;
  }

  public QueryType deepCopy() {
    return new QueryType(this);
  }

  @Override
  public void clear() {
    setHasSelectionIsSet(false);
    this.hasSelection = false;
    setHasFilterIsSet(false);
    this.hasFilter = false;
    setHasAggregationIsSet(false);
    this.hasAggregation = false;
    setHasGroup_byIsSet(false);
    this.hasGroup_by = false;
    setHasHavingIsSet(false);
    this.hasHaving = false;
  }

  public boolean isHasSelection() {
    return this.hasSelection;
  }

  public void setHasSelection(boolean hasSelection) {
    this.hasSelection = hasSelection;
    setHasSelectionIsSet(true);
  }

  public void unsetHasSelection() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __HASSELECTION_ISSET_ID);
  }

  /** Returns true if field hasSelection is set (has been assigned a value) and false otherwise */
  public boolean isSetHasSelection() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __HASSELECTION_ISSET_ID);
  }

  public void setHasSelectionIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __HASSELECTION_ISSET_ID, value);
  }

  public boolean isHasFilter() {
    return this.hasFilter;
  }

  public void setHasFilter(boolean hasFilter) {
    this.hasFilter = hasFilter;
    setHasFilterIsSet(true);
  }

  public void unsetHasFilter() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __HASFILTER_ISSET_ID);
  }

  /** Returns true if field hasFilter is set (has been assigned a value) and false otherwise */
  public boolean isSetHasFilter() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __HASFILTER_ISSET_ID);
  }

  public void setHasFilterIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __HASFILTER_ISSET_ID, value);
  }

  public boolean isHasAggregation() {
    return this.hasAggregation;
  }

  public void setHasAggregation(boolean hasAggregation) {
    this.hasAggregation = hasAggregation;
    setHasAggregationIsSet(true);
  }

  public void unsetHasAggregation() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __HASAGGREGATION_ISSET_ID);
  }

  /** Returns true if field hasAggregation is set (has been assigned a value) and false otherwise */
  public boolean isSetHasAggregation() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __HASAGGREGATION_ISSET_ID);
  }

  public void setHasAggregationIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __HASAGGREGATION_ISSET_ID, value);
  }

  public boolean isHasGroup_by() {
    return this.hasGroup_by;
  }

  public void setHasGroup_by(boolean hasGroup_by) {
    this.hasGroup_by = hasGroup_by;
    setHasGroup_byIsSet(true);
  }

  public void unsetHasGroup_by() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __HASGROUP_BY_ISSET_ID);
  }

  /** Returns true if field hasGroup_by is set (has been assigned a value) and false otherwise */
  public boolean isSetHasGroup_by() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __HASGROUP_BY_ISSET_ID);
  }

  public void setHasGroup_byIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __HASGROUP_BY_ISSET_ID, value);
  }

  public boolean isHasHaving() {
    return this.hasHaving;
  }

  public void setHasHaving(boolean hasHaving) {
    this.hasHaving = hasHaving;
    setHasHavingIsSet(true);
  }

  public void unsetHasHaving() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __HASHAVING_ISSET_ID);
  }

  /** Returns true if field hasHaving is set (has been assigned a value) and false otherwise */
  public boolean isSetHasHaving() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __HASHAVING_ISSET_ID);
  }

  public void setHasHavingIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __HASHAVING_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case HAS_SELECTION:
      if (value == null) {
        unsetHasSelection();
      } else {
        setHasSelection((java.lang.Boolean)value);
      }
      break;

    case HAS_FILTER:
      if (value == null) {
        unsetHasFilter();
      } else {
        setHasFilter((java.lang.Boolean)value);
      }
      break;

    case HAS_AGGREGATION:
      if (value == null) {
        unsetHasAggregation();
      } else {
        setHasAggregation((java.lang.Boolean)value);
      }
      break;

    case HAS_GROUP_BY:
      if (value == null) {
        unsetHasGroup_by();
      } else {
        setHasGroup_by((java.lang.Boolean)value);
      }
      break;

    case HAS_HAVING:
      if (value == null) {
        unsetHasHaving();
      } else {
        setHasHaving((java.lang.Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case HAS_SELECTION:
      return isHasSelection();

    case HAS_FILTER:
      return isHasFilter();

    case HAS_AGGREGATION:
      return isHasAggregation();

    case HAS_GROUP_BY:
      return isHasGroup_by();

    case HAS_HAVING:
      return isHasHaving();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case HAS_SELECTION:
      return isSetHasSelection();
    case HAS_FILTER:
      return isSetHasFilter();
    case HAS_AGGREGATION:
      return isSetHasAggregation();
    case HAS_GROUP_BY:
      return isSetHasGroup_by();
    case HAS_HAVING:
      return isSetHasHaving();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof QueryType)
      return this.equals((QueryType)that);
    return false;
  }

  public boolean equals(QueryType that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_hasSelection = true && this.isSetHasSelection();
    boolean that_present_hasSelection = true && that.isSetHasSelection();
    if (this_present_hasSelection || that_present_hasSelection) {
      if (!(this_present_hasSelection && that_present_hasSelection))
        return false;
      if (this.hasSelection != that.hasSelection)
        return false;
    }

    boolean this_present_hasFilter = true && this.isSetHasFilter();
    boolean that_present_hasFilter = true && that.isSetHasFilter();
    if (this_present_hasFilter || that_present_hasFilter) {
      if (!(this_present_hasFilter && that_present_hasFilter))
        return false;
      if (this.hasFilter != that.hasFilter)
        return false;
    }

    boolean this_present_hasAggregation = true && this.isSetHasAggregation();
    boolean that_present_hasAggregation = true && that.isSetHasAggregation();
    if (this_present_hasAggregation || that_present_hasAggregation) {
      if (!(this_present_hasAggregation && that_present_hasAggregation))
        return false;
      if (this.hasAggregation != that.hasAggregation)
        return false;
    }

    boolean this_present_hasGroup_by = true && this.isSetHasGroup_by();
    boolean that_present_hasGroup_by = true && that.isSetHasGroup_by();
    if (this_present_hasGroup_by || that_present_hasGroup_by) {
      if (!(this_present_hasGroup_by && that_present_hasGroup_by))
        return false;
      if (this.hasGroup_by != that.hasGroup_by)
        return false;
    }

    boolean this_present_hasHaving = true && this.isSetHasHaving();
    boolean that_present_hasHaving = true && that.isSetHasHaving();
    if (this_present_hasHaving || that_present_hasHaving) {
      if (!(this_present_hasHaving && that_present_hasHaving))
        return false;
      if (this.hasHaving != that.hasHaving)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetHasSelection()) ? 131071 : 524287);
    if (isSetHasSelection())
      hashCode = hashCode * 8191 + ((hasSelection) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSetHasFilter()) ? 131071 : 524287);
    if (isSetHasFilter())
      hashCode = hashCode * 8191 + ((hasFilter) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSetHasAggregation()) ? 131071 : 524287);
    if (isSetHasAggregation())
      hashCode = hashCode * 8191 + ((hasAggregation) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSetHasGroup_by()) ? 131071 : 524287);
    if (isSetHasGroup_by())
      hashCode = hashCode * 8191 + ((hasGroup_by) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSetHasHaving()) ? 131071 : 524287);
    if (isSetHasHaving())
      hashCode = hashCode * 8191 + ((hasHaving) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(QueryType other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetHasSelection(), other.isSetHasSelection());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHasSelection()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hasSelection, other.hasSelection);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetHasFilter(), other.isSetHasFilter());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHasFilter()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hasFilter, other.hasFilter);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetHasAggregation(), other.isSetHasAggregation());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHasAggregation()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hasAggregation, other.hasAggregation);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetHasGroup_by(), other.isSetHasGroup_by());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHasGroup_by()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hasGroup_by, other.hasGroup_by);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetHasHaving(), other.isSetHasHaving());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHasHaving()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hasHaving, other.hasHaving);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("QueryType(");
    boolean first = true;

    if (isSetHasSelection()) {
      sb.append("hasSelection:");
      sb.append(this.hasSelection);
      first = false;
    }
    if (isSetHasFilter()) {
      if (!first) sb.append(", ");
      sb.append("hasFilter:");
      sb.append(this.hasFilter);
      first = false;
    }
    if (isSetHasAggregation()) {
      if (!first) sb.append(", ");
      sb.append("hasAggregation:");
      sb.append(this.hasAggregation);
      first = false;
    }
    if (isSetHasGroup_by()) {
      if (!first) sb.append(", ");
      sb.append("hasGroup_by:");
      sb.append(this.hasGroup_by);
      first = false;
    }
    if (isSetHasHaving()) {
      if (!first) sb.append(", ");
      sb.append("hasHaving:");
      sb.append(this.hasHaving);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class QueryTypeStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public QueryTypeStandardScheme getScheme() {
      return new QueryTypeStandardScheme();
    }
  }

  private static class QueryTypeStandardScheme extends org.apache.thrift.scheme.StandardScheme<QueryType> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, QueryType struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // HAS_SELECTION
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.hasSelection = iprot.readBool();
              struct.setHasSelectionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // HAS_FILTER
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.hasFilter = iprot.readBool();
              struct.setHasFilterIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // HAS_AGGREGATION
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.hasAggregation = iprot.readBool();
              struct.setHasAggregationIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // HAS_GROUP_BY
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.hasGroup_by = iprot.readBool();
              struct.setHasGroup_byIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // HAS_HAVING
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.hasHaving = iprot.readBool();
              struct.setHasHavingIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, QueryType struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.isSetHasSelection()) {
        oprot.writeFieldBegin(HAS_SELECTION_FIELD_DESC);
        oprot.writeBool(struct.hasSelection);
        oprot.writeFieldEnd();
      }
      if (struct.isSetHasFilter()) {
        oprot.writeFieldBegin(HAS_FILTER_FIELD_DESC);
        oprot.writeBool(struct.hasFilter);
        oprot.writeFieldEnd();
      }
      if (struct.isSetHasAggregation()) {
        oprot.writeFieldBegin(HAS_AGGREGATION_FIELD_DESC);
        oprot.writeBool(struct.hasAggregation);
        oprot.writeFieldEnd();
      }
      if (struct.isSetHasGroup_by()) {
        oprot.writeFieldBegin(HAS_GROUP_BY_FIELD_DESC);
        oprot.writeBool(struct.hasGroup_by);
        oprot.writeFieldEnd();
      }
      if (struct.isSetHasHaving()) {
        oprot.writeFieldBegin(HAS_HAVING_FIELD_DESC);
        oprot.writeBool(struct.hasHaving);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class QueryTypeTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public QueryTypeTupleScheme getScheme() {
      return new QueryTypeTupleScheme();
    }
  }

  private static class QueryTypeTupleScheme extends org.apache.thrift.scheme.TupleScheme<QueryType> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, QueryType struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetHasSelection()) {
        optionals.set(0);
      }
      if (struct.isSetHasFilter()) {
        optionals.set(1);
      }
      if (struct.isSetHasAggregation()) {
        optionals.set(2);
      }
      if (struct.isSetHasGroup_by()) {
        optionals.set(3);
      }
      if (struct.isSetHasHaving()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetHasSelection()) {
        oprot.writeBool(struct.hasSelection);
      }
      if (struct.isSetHasFilter()) {
        oprot.writeBool(struct.hasFilter);
      }
      if (struct.isSetHasAggregation()) {
        oprot.writeBool(struct.hasAggregation);
      }
      if (struct.isSetHasGroup_by()) {
        oprot.writeBool(struct.hasGroup_by);
      }
      if (struct.isSetHasHaving()) {
        oprot.writeBool(struct.hasHaving);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, QueryType struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.hasSelection = iprot.readBool();
        struct.setHasSelectionIsSet(true);
      }
      if (incoming.get(1)) {
        struct.hasFilter = iprot.readBool();
        struct.setHasFilterIsSet(true);
      }
      if (incoming.get(2)) {
        struct.hasAggregation = iprot.readBool();
        struct.setHasAggregationIsSet(true);
      }
      if (incoming.get(3)) {
        struct.hasGroup_by = iprot.readBool();
        struct.setHasGroup_byIsSet(true);
      }
      if (incoming.get(4)) {
        struct.hasHaving = iprot.readBool();
        struct.setHasHavingIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

