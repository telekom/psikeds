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
package org.psikeds.resolutionengine.interfaces.pojos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a single Feature / Attribute of a Variant.
 * 
 * Note 1: ID must be globally unique!
 * 
 * Note 2: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Feature")
public class Feature extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String minValue;
  private String maxValue;
  private FeatureValueType valueType;
  private boolean range;

  /**
   * Default constructor: use Setters for Initialization
   * 
   */
  public Feature() {
    this(null, null, null, null, null, null, false);
  }

  /**
   * Constructor for a Feature with a discrete Value
   * 
   * @param label
   * @param description
   * @param id
   * @param value
   * @param fvt
   * 
   */
  public Feature(final String label, final String description, final String id, final String value, final FeatureValueType fvt) {
    this(label, description, id, value, null, fvt, false);
  }

  /**
   * Constructor for a Feature with a Range from minValue to maxValue
   * 
   * @param label
   * @param description
   * @param id
   * @param minValue
   * @param maxValue
   * @param fvt
   * 
   */
  public Feature(final String label, final String description, final String id, final String minValue, final String maxValue, final FeatureValueType fvt) {
    this(label, description, id, minValue, maxValue, fvt, true);
  }

  /**
   * Internal constructor (used by Transformer)
   * 
   * @param label
   * @param description
   * @param id
   * @param minValue
   * @param maxValue
   * @param fvt
   * @param range
   */
  public Feature(final String label, final String description, final String id, final String minValue, final String maxValue, final FeatureValueType fvt, final boolean range) {
    super(id);
    this.label = label;
    this.description = description;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.valueType = fvt == null ? FeatureValueType.STRING : fvt;
    this.range = range;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String value) {
    this.label = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String value) {
    this.description = value;
  }

  public String getMinValue() {
    return this.minValue;
  }

  public String getValue() {
    return this.minValue;
  }

  public void setMinValue(final String minValue) {
    this.minValue = minValue;
    this.range = true;
  }

  public void setValue(final String value) {
    this.minValue = value;
    this.range = false;
  }

  public String getMaxValue() {
    return this.maxValue;
  }

  public void setMaxValue(final String maxValue) {
    this.maxValue = maxValue;
    this.range = true;
  }

  public FeatureValueType getValueType() {
    return this.valueType;
  }

  public void setValueType(final FeatureValueType valueType) {
    this.valueType = valueType;
  }

  public boolean isRange() {
    return this.range;
  }

  public void setRange(final boolean range) {
    this.range = range;
    if (!this.range) {
      this.maxValue = null;
    }
  }
}
