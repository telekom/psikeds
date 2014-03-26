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

import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * Base-Object representing a Feature, either FeatureDescription or FeatureValue.
 * 
 * Note 1: Feature-ID must be globally unique!
 * 
 * Note 2: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlSeeAlso({ FeatureValue.class, FeatureDescription.class })
@JsonSubTypes({ @JsonSubTypes.Type(value = FeatureValue.class, name = "FeatureValue"), @JsonSubTypes.Type(value = FeatureDescription.class, name = "FeatureDescription"), })
public abstract class Feature extends POJO {

  private static final long serialVersionUID = 1L;

  public static final String VALUE_TYPE_STRING = "string";
  public static final String VALUE_TYPE_INTEGER = "integer";
  public static final String VALUE_TYPE_FLOAT = "float";

  private String label;
  private String description;
  private String valueType;

  public Feature() {
    this(null, null, null, null);
  }

  public Feature(final Feature feature) {
    this(feature.getLabel(), feature.getDescription(), feature.getFeatureID(), feature.getValueType());
  }

  public Feature(final String label, final String description, final String featureID, final String valueType) {
    super(featureID);
    setLabel(label);
    setDescription(description);
    setValueType(valueType);
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
