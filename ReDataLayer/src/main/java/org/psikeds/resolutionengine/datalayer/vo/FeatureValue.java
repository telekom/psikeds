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
public class FeatureValue extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String featureID;
  private String type;
  private String value;

  public FeatureValue() {
    this(null, null, null, null);
  }

  public FeatureValue(final String featureID, final String featureValueID, final String type, final String value) {
    super(featureValueID);
    setFeatureID(featureID);
    setType(type);
    setValue(value);
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

  public void setValue(final String value) {
    this.value = value;
  }
}