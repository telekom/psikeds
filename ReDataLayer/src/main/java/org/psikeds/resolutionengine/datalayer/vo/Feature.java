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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a Feature-Definition/Declaration, i.e. Type and possible Values of an
 * Attribute that can be assigned to a Variant.
 * 
 * Note: Feature-ID must be globally unique!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Feature")
public class Feature extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String VALUE_TYPE_STRING = "string";
  public static final String VALUE_TYPE_INTEGER = "integer";
  public static final String VALUE_TYPE_FLOAT = "float";

  private String label;
  private String description;
  private String type;
  private String unit;
  private List<FeatureValue> values;

  public Feature() {
    this(null);
  }

  public Feature(final String featureID) {
    this(featureID, null, featureID, null);
  }

  public Feature(final String label, final String description, final String featureID, final String unit) {
    this(label, description, featureID, null, unit, null);
  }

  public Feature(final String label, final String description, final String featureID, final String type, final String unit, final Collection<? extends FeatureValue> values) {
    super(featureID);
    setLabel(label);
    setDescription(description);
    setType(type);
    setUnit(unit);
    addValue(values);
  }

  // ----------------------------------------------------------------

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getFeatureID() {
    return getId();
  }

  public void setFeatureID(final String featureID) {
    setId(featureID);
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    if (VALUE_TYPE_FLOAT.equalsIgnoreCase(type)) {
      this.type = VALUE_TYPE_FLOAT;
    }
    else if (VALUE_TYPE_INTEGER.equalsIgnoreCase(type)) {
      this.type = VALUE_TYPE_INTEGER;
    }
    else {
      this.type = VALUE_TYPE_STRING;
    }
  }

  public String getUnit() {
    return this.unit;
  }

  public void setUnit(final String unit) {
    this.unit = unit;
  }

  // ----------------------------------------------------------------

  public List<FeatureValue> getValues() {
    if (this.values == null) {
      this.values = new ArrayList<FeatureValue>();
    }
    return this.values;
  }

  public boolean addValue(final FeatureValue value) {
    return ((value != null) && getValues().add(value));
  }

  public boolean addValue(final Collection<? extends FeatureValue> values) {
    return ((values != null) && !values.isEmpty() && getValues().addAll(values));
  }

  public void setValues(final List<FeatureValue> values) {
    this.values = values;
  }

  public void clearValues() {
    if (this.values != null) {
      this.values.clear();
      this.values = null;
    }
  }
}
