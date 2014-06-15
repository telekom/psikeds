/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [x] GNU Affero General Public License
 * [ ] GNU General Public License
 * [ ] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.resolutionengine.datalayer.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * This is a single Feature-Value, i.e. a possible Value of an Attribute that can
 * be assigned to a Variant.
 * 
 * Note: Both Feature-ID and Feature-Value-ID must be globally unique!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureValue")
@XmlSeeAlso({ IntegerFeatureValue.class, FloatFeatureValue.class })
@JsonSubTypes({ @JsonSubTypes.Type(value = IntegerFeatureValue.class, name = "IntegerFeatureValue"),
    @JsonSubTypes.Type(value = FloatFeatureValue.class, name = "FloatFeatureValue"), })
public class FeatureValue extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String featureID;
  protected String type;
  protected String value;

  public FeatureValue() {
    this(null, null, null);
  }

  public FeatureValue(final String featureID, final String featureValueID, final String val) {
    this(featureID, featureValueID, Feature.VALUE_TYPE_STRING, val);
  }

  public FeatureValue(final String featureID, final String featureValueID, final String type, final String val) {
    super(featureValueID);
    setFeatureID(featureID);
    setType(type);
    setValue(val);
  }

  public String getFeatureValueID() {
    return getId();
  }

  public void setFeatureValueID(final String featureValueID) {
    setId(featureValueID);
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    if (Feature.VALUE_TYPE_FLOAT.equalsIgnoreCase(type)) {
      this.type = Feature.VALUE_TYPE_FLOAT;
    }
    else if (Feature.VALUE_TYPE_INTEGER.equalsIgnoreCase(type)) {
      this.type = Feature.VALUE_TYPE_INTEGER;
    }
    else {
      this.type = Feature.VALUE_TYPE_STRING;
    }
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String val) {
    this.value = val;
  }

  @JsonIgnore
  public float toFloatValue() {
    if (!isFloatValue()) {
      throw new IllegalArgumentException("Not a Float-Value: " + String.valueOf(this.type));
    }
    try {
      return Float.parseFloat(this.value);
    }
    catch (final Exception ex) {
      throw new NumberFormatException("Illegal Float-Value: " + String.valueOf(this.value));
    }
  }

  @JsonIgnore
  public long toIntegerValue() {
    if (!isIntegerValue()) {
      throw new IllegalArgumentException("Not an Integer-Value: " + String.valueOf(this.type));
    }
    try {
      return Long.parseLong(this.value);
    }
    catch (final Exception ex) {
      throw new IllegalArgumentException("Illegal Integer-Value: " + String.valueOf(this.value));
    }
  }

  @JsonIgnore
  public boolean isFloatValue() {
    return Feature.VALUE_TYPE_FLOAT.equals(this.type);
  }

  @JsonIgnore
  public boolean isIntegerValue() {
    return Feature.VALUE_TYPE_INTEGER.equals(this.type);
  }
}
