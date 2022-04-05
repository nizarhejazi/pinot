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
 * Instance Request
 * 
 */
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.15.0)", date = "2022-03-28")
public class InstanceRequest implements org.apache.thrift.TBase<InstanceRequest, InstanceRequest._Fields>, java.io.Serializable, Cloneable, Comparable<InstanceRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("InstanceRequest");

  private static final org.apache.thrift.protocol.TField REQUEST_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("requestId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField QUERY_FIELD_DESC = new org.apache.thrift.protocol.TField("query", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField SEARCH_SEGMENTS_FIELD_DESC = new org.apache.thrift.protocol.TField("searchSegments", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField ENABLE_TRACE_FIELD_DESC = new org.apache.thrift.protocol.TField("enableTrace", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField BROKER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("brokerId", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new InstanceRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new InstanceRequestTupleSchemeFactory();

  private long requestId; // required
  private @org.apache.thrift.annotation.Nullable BrokerRequest query; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<java.lang.String> searchSegments; // optional
  private boolean enableTrace; // optional
  private @org.apache.thrift.annotation.Nullable java.lang.String brokerId; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    REQUEST_ID((short)1, "requestId"),
    QUERY((short)2, "query"),
    SEARCH_SEGMENTS((short)3, "searchSegments"),
    ENABLE_TRACE((short)4, "enableTrace"),
    BROKER_ID((short)5, "brokerId");

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
        case 1: // REQUEST_ID
          return REQUEST_ID;
        case 2: // QUERY
          return QUERY;
        case 3: // SEARCH_SEGMENTS
          return SEARCH_SEGMENTS;
        case 4: // ENABLE_TRACE
          return ENABLE_TRACE;
        case 5: // BROKER_ID
          return BROKER_ID;
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
  private static final int __REQUESTID_ISSET_ID = 0;
  private static final int __ENABLETRACE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.SEARCH_SEGMENTS,_Fields.ENABLE_TRACE,_Fields.BROKER_ID};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REQUEST_ID, new org.apache.thrift.meta_data.FieldMetaData("requestId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.QUERY, new org.apache.thrift.meta_data.FieldMetaData("query", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, BrokerRequest.class)));
    tmpMap.put(_Fields.SEARCH_SEGMENTS, new org.apache.thrift.meta_data.FieldMetaData("searchSegments", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.ENABLE_TRACE, new org.apache.thrift.meta_data.FieldMetaData("enableTrace", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.BROKER_ID, new org.apache.thrift.meta_data.FieldMetaData("brokerId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(InstanceRequest.class, metaDataMap);
  }

  public InstanceRequest() {
  }

  public InstanceRequest(
    long requestId,
    BrokerRequest query)
  {
    this();
    this.requestId = requestId;
    setRequestIdIsSet(true);
    this.query = query;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public InstanceRequest(InstanceRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this.requestId = other.requestId;
    if (other.isSetQuery()) {
      this.query = new BrokerRequest(other.query);
    }
    if (other.isSetSearchSegments()) {
      java.util.List<java.lang.String> __this__searchSegments = new java.util.ArrayList<java.lang.String>(other.searchSegments);
      this.searchSegments = __this__searchSegments;
    }
    this.enableTrace = other.enableTrace;
    if (other.isSetBrokerId()) {
      this.brokerId = other.brokerId;
    }
  }

  public InstanceRequest deepCopy() {
    return new InstanceRequest(this);
  }

  @Override
  public void clear() {
    setRequestIdIsSet(false);
    this.requestId = 0;
    this.query = null;
    this.searchSegments = null;
    setEnableTraceIsSet(false);
    this.enableTrace = false;
    this.brokerId = null;
  }

  public long getRequestId() {
    return this.requestId;
  }

  public void setRequestId(long requestId) {
    this.requestId = requestId;
    setRequestIdIsSet(true);
  }

  public void unsetRequestId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __REQUESTID_ISSET_ID);
  }

  /** Returns true if field requestId is set (has been assigned a value) and false otherwise */
  public boolean isSetRequestId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __REQUESTID_ISSET_ID);
  }

  public void setRequestIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __REQUESTID_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public BrokerRequest getQuery() {
    return this.query;
  }

  public void setQuery(@org.apache.thrift.annotation.Nullable BrokerRequest query) {
    this.query = query;
  }

  public void unsetQuery() {
    this.query = null;
  }

  /** Returns true if field query is set (has been assigned a value) and false otherwise */
  public boolean isSetQuery() {
    return this.query != null;
  }

  public void setQueryIsSet(boolean value) {
    if (!value) {
      this.query = null;
    }
  }

  public int getSearchSegmentsSize() {
    return (this.searchSegments == null) ? 0 : this.searchSegments.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.String> getSearchSegmentsIterator() {
    return (this.searchSegments == null) ? null : this.searchSegments.iterator();
  }

  public void addToSearchSegments(java.lang.String elem) {
    if (this.searchSegments == null) {
      this.searchSegments = new java.util.ArrayList<java.lang.String>();
    }
    this.searchSegments.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.lang.String> getSearchSegments() {
    return this.searchSegments;
  }

  public void setSearchSegments(@org.apache.thrift.annotation.Nullable java.util.List<java.lang.String> searchSegments) {
    this.searchSegments = searchSegments;
  }

  public void unsetSearchSegments() {
    this.searchSegments = null;
  }

  /** Returns true if field searchSegments is set (has been assigned a value) and false otherwise */
  public boolean isSetSearchSegments() {
    return this.searchSegments != null;
  }

  public void setSearchSegmentsIsSet(boolean value) {
    if (!value) {
      this.searchSegments = null;
    }
  }

  public boolean isEnableTrace() {
    return this.enableTrace;
  }

  public void setEnableTrace(boolean enableTrace) {
    this.enableTrace = enableTrace;
    setEnableTraceIsSet(true);
  }

  public void unsetEnableTrace() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ENABLETRACE_ISSET_ID);
  }

  /** Returns true if field enableTrace is set (has been assigned a value) and false otherwise */
  public boolean isSetEnableTrace() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ENABLETRACE_ISSET_ID);
  }

  public void setEnableTraceIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ENABLETRACE_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getBrokerId() {
    return this.brokerId;
  }

  public void setBrokerId(@org.apache.thrift.annotation.Nullable java.lang.String brokerId) {
    this.brokerId = brokerId;
  }

  public void unsetBrokerId() {
    this.brokerId = null;
  }

  /** Returns true if field brokerId is set (has been assigned a value) and false otherwise */
  public boolean isSetBrokerId() {
    return this.brokerId != null;
  }

  public void setBrokerIdIsSet(boolean value) {
    if (!value) {
      this.brokerId = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case REQUEST_ID:
      if (value == null) {
        unsetRequestId();
      } else {
        setRequestId((java.lang.Long)value);
      }
      break;

    case QUERY:
      if (value == null) {
        unsetQuery();
      } else {
        setQuery((BrokerRequest)value);
      }
      break;

    case SEARCH_SEGMENTS:
      if (value == null) {
        unsetSearchSegments();
      } else {
        setSearchSegments((java.util.List<java.lang.String>)value);
      }
      break;

    case ENABLE_TRACE:
      if (value == null) {
        unsetEnableTrace();
      } else {
        setEnableTrace((java.lang.Boolean)value);
      }
      break;

    case BROKER_ID:
      if (value == null) {
        unsetBrokerId();
      } else {
        setBrokerId((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case REQUEST_ID:
      return getRequestId();

    case QUERY:
      return getQuery();

    case SEARCH_SEGMENTS:
      return getSearchSegments();

    case ENABLE_TRACE:
      return isEnableTrace();

    case BROKER_ID:
      return getBrokerId();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case REQUEST_ID:
      return isSetRequestId();
    case QUERY:
      return isSetQuery();
    case SEARCH_SEGMENTS:
      return isSetSearchSegments();
    case ENABLE_TRACE:
      return isSetEnableTrace();
    case BROKER_ID:
      return isSetBrokerId();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof InstanceRequest)
      return this.equals((InstanceRequest)that);
    return false;
  }

  public boolean equals(InstanceRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_requestId = true;
    boolean that_present_requestId = true;
    if (this_present_requestId || that_present_requestId) {
      if (!(this_present_requestId && that_present_requestId))
        return false;
      if (this.requestId != that.requestId)
        return false;
    }

    boolean this_present_query = true && this.isSetQuery();
    boolean that_present_query = true && that.isSetQuery();
    if (this_present_query || that_present_query) {
      if (!(this_present_query && that_present_query))
        return false;
      if (!this.query.equals(that.query))
        return false;
    }

    boolean this_present_searchSegments = true && this.isSetSearchSegments();
    boolean that_present_searchSegments = true && that.isSetSearchSegments();
    if (this_present_searchSegments || that_present_searchSegments) {
      if (!(this_present_searchSegments && that_present_searchSegments))
        return false;
      if (!this.searchSegments.equals(that.searchSegments))
        return false;
    }

    boolean this_present_enableTrace = true && this.isSetEnableTrace();
    boolean that_present_enableTrace = true && that.isSetEnableTrace();
    if (this_present_enableTrace || that_present_enableTrace) {
      if (!(this_present_enableTrace && that_present_enableTrace))
        return false;
      if (this.enableTrace != that.enableTrace)
        return false;
    }

    boolean this_present_brokerId = true && this.isSetBrokerId();
    boolean that_present_brokerId = true && that.isSetBrokerId();
    if (this_present_brokerId || that_present_brokerId) {
      if (!(this_present_brokerId && that_present_brokerId))
        return false;
      if (!this.brokerId.equals(that.brokerId))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(requestId);

    hashCode = hashCode * 8191 + ((isSetQuery()) ? 131071 : 524287);
    if (isSetQuery())
      hashCode = hashCode * 8191 + query.hashCode();

    hashCode = hashCode * 8191 + ((isSetSearchSegments()) ? 131071 : 524287);
    if (isSetSearchSegments())
      hashCode = hashCode * 8191 + searchSegments.hashCode();

    hashCode = hashCode * 8191 + ((isSetEnableTrace()) ? 131071 : 524287);
    if (isSetEnableTrace())
      hashCode = hashCode * 8191 + ((enableTrace) ? 131071 : 524287);

    hashCode = hashCode * 8191 + ((isSetBrokerId()) ? 131071 : 524287);
    if (isSetBrokerId())
      hashCode = hashCode * 8191 + brokerId.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(InstanceRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetRequestId(), other.isSetRequestId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRequestId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.requestId, other.requestId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetQuery(), other.isSetQuery());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetQuery()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.query, other.query);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetSearchSegments(), other.isSetSearchSegments());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSearchSegments()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.searchSegments, other.searchSegments);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetEnableTrace(), other.isSetEnableTrace());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEnableTrace()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.enableTrace, other.enableTrace);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetBrokerId(), other.isSetBrokerId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBrokerId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.brokerId, other.brokerId);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("InstanceRequest(");
    boolean first = true;

    sb.append("requestId:");
    sb.append(this.requestId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("query:");
    if (this.query == null) {
      sb.append("null");
    } else {
      sb.append(this.query);
    }
    first = false;
    if (isSetSearchSegments()) {
      if (!first) sb.append(", ");
      sb.append("searchSegments:");
      if (this.searchSegments == null) {
        sb.append("null");
      } else {
        sb.append(this.searchSegments);
      }
      first = false;
    }
    if (isSetEnableTrace()) {
      if (!first) sb.append(", ");
      sb.append("enableTrace:");
      sb.append(this.enableTrace);
      first = false;
    }
    if (isSetBrokerId()) {
      if (!first) sb.append(", ");
      sb.append("brokerId:");
      if (this.brokerId == null) {
        sb.append("null");
      } else {
        sb.append(this.brokerId);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetRequestId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'requestId' is unset! Struct:" + toString());
    }

    if (!isSetQuery()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'query' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (query != null) {
      query.validate();
    }
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

  private static class InstanceRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public InstanceRequestStandardScheme getScheme() {
      return new InstanceRequestStandardScheme();
    }
  }

  private static class InstanceRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<InstanceRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, InstanceRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // REQUEST_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.requestId = iprot.readI64();
              struct.setRequestIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // QUERY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.query = new BrokerRequest();
              struct.query.read(iprot);
              struct.setQueryIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SEARCH_SEGMENTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list138 = iprot.readListBegin();
                struct.searchSegments = new java.util.ArrayList<java.lang.String>(_list138.size);
                @org.apache.thrift.annotation.Nullable java.lang.String _elem139;
                for (int _i140 = 0; _i140 < _list138.size; ++_i140)
                {
                  _elem139 = iprot.readString();
                  struct.searchSegments.add(_elem139);
                }
                iprot.readListEnd();
              }
              struct.setSearchSegmentsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ENABLE_TRACE
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.enableTrace = iprot.readBool();
              struct.setEnableTraceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // BROKER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.brokerId = iprot.readString();
              struct.setBrokerIdIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, InstanceRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(REQUEST_ID_FIELD_DESC);
      oprot.writeI64(struct.requestId);
      oprot.writeFieldEnd();
      if (struct.query != null) {
        oprot.writeFieldBegin(QUERY_FIELD_DESC);
        struct.query.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.searchSegments != null) {
        if (struct.isSetSearchSegments()) {
          oprot.writeFieldBegin(SEARCH_SEGMENTS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.searchSegments.size()));
            for (java.lang.String _iter141 : struct.searchSegments)
            {
              oprot.writeString(_iter141);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetEnableTrace()) {
        oprot.writeFieldBegin(ENABLE_TRACE_FIELD_DESC);
        oprot.writeBool(struct.enableTrace);
        oprot.writeFieldEnd();
      }
      if (struct.brokerId != null) {
        if (struct.isSetBrokerId()) {
          oprot.writeFieldBegin(BROKER_ID_FIELD_DESC);
          oprot.writeString(struct.brokerId);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class InstanceRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public InstanceRequestTupleScheme getScheme() {
      return new InstanceRequestTupleScheme();
    }
  }

  private static class InstanceRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<InstanceRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, InstanceRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI64(struct.requestId);
      struct.query.write(oprot);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetSearchSegments()) {
        optionals.set(0);
      }
      if (struct.isSetEnableTrace()) {
        optionals.set(1);
      }
      if (struct.isSetBrokerId()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetSearchSegments()) {
        {
          oprot.writeI32(struct.searchSegments.size());
          for (java.lang.String _iter142 : struct.searchSegments)
          {
            oprot.writeString(_iter142);
          }
        }
      }
      if (struct.isSetEnableTrace()) {
        oprot.writeBool(struct.enableTrace);
      }
      if (struct.isSetBrokerId()) {
        oprot.writeString(struct.brokerId);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, InstanceRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.requestId = iprot.readI64();
      struct.setRequestIdIsSet(true);
      struct.query = new BrokerRequest();
      struct.query.read(iprot);
      struct.setQueryIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list143 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRING);
          struct.searchSegments = new java.util.ArrayList<java.lang.String>(_list143.size);
          @org.apache.thrift.annotation.Nullable java.lang.String _elem144;
          for (int _i145 = 0; _i145 < _list143.size; ++_i145)
          {
            _elem144 = iprot.readString();
            struct.searchSegments.add(_elem144);
          }
        }
        struct.setSearchSegmentsIsSet(true);
      }
      if (incoming.get(1)) {
        struct.enableTrace = iprot.readBool();
        struct.setEnableTraceIsSet(true);
      }
      if (incoming.get(2)) {
        struct.brokerId = iprot.readString();
        struct.setBrokerIdIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

