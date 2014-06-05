/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Interface object representing a Feature-Value, i.e.
 * an assigned Value for a Feature.
 * 
 * Note 1: Feature-ID and Feature-Value-ID must be globally unique!
 * 
 * Note 2: Value must be allowed for Feature.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureValue")
public class FeatureValue extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String featureID;
  private String value;

  public FeatureValue() {
    this(null, null, null);
  }

  public FeatureValue(final String featureID, final String featureValueID, final float f) {
    this(featureID, featureValueID, null);
    setValue(f);
  }

  public FeatureValue(final String featureID, final String featureValueID, final String value) {
    super(featureValueID);
    setFeatureID(featureID);
    setValue(value);
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }

  public String getFeatureValueID() {
    return getId();
  }

  public void setFeatureValueID(final String featureValueID) {
    setId(featureValueID);
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @JsonIgnore
  public void setValue(final float f) {
    this.value = String.valueOf(f);
  }

  @JsonIgnore
  public float toFloatValue() {
    float f = 0.0f;
    try {
      f = Float.parseFloat(this.value);
    }
    catch (final Exception ex) {
      f = 0.0f;
    }
    return f;
  }
}
