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
 * Interface object representing a Description of a Feature, i.e.
 * ID, Label, Description, Type, Unit ... but no Values!!
 * 
 * Note: Feature-ID must be globally unique!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Feature")
public class Feature extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String VALUE_TYPE_STRING = "string";
  public static final String VALUE_TYPE_INTEGER = "integer";
  public static final String VALUE_TYPE_FLOAT = "float";

  private String label;
  private String description;
  private String valueType;
  private String unit;

  public Feature() {
    this(null, null);
  }

  public Feature(final String featureID, final String valueType) {
    this(featureID, null, featureID, valueType, null);
  }

  public Feature(final String label, final String description, final String featureID, final String valueType, final String unit) {
    super(featureID);
    setLabel(label);
    setDescription(description);
    setValueType(valueType);
    setUnit(unit);
  }

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

  public String getUnit() {
    return this.unit;
  }

  public void setUnit(final String unit) {
    this.unit = unit;
  }

  public String getValueType() {
    return this.valueType;
  }

  public void setValueType(final String valueType) {
    if (VALUE_TYPE_INTEGER.equalsIgnoreCase(valueType)) {
      this.valueType = VALUE_TYPE_INTEGER;
    }
    else if (VALUE_TYPE_FLOAT.equalsIgnoreCase(valueType)) {
      this.valueType = VALUE_TYPE_FLOAT;
    }
    else {
      this.valueType = VALUE_TYPE_STRING;
    }
  }

  public String getFeatureID() {
    return getId();
  }

  public void setFeatureID(final String featureID) {
    setId(featureID);
  }
}
