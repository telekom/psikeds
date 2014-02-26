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
 * Interface object representing a resolved Feature, i.e.
 * ID, Label, Description ... and also an assigned Value!
 * 
 * Note 1: Feature-ID must be globally unique!
 * 
 * Note 2: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureValue")
public class FeatureValue extends Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  private String value;

  public FeatureValue() {
    super();
    this.value = null;
  }

  public FeatureValue(final String label, final String description, final String featureID, final String valueType, final String value) {
    super(label, description, featureID, valueType);
    this.value = value;
  }

  public FeatureValue(final Feature feature, final String value) {
    super(feature);
    this.value = value;
  }

  public FeatureValue(final Feature feature, final FeatureDecission decission) {
    this(feature, decission.getFeatureValue());
    if (!this.getFeatureID().equals(decission.getFeatureID())) {
      throw new IllegalArgumentException("Feature and Decission are not matching to each other!");
    }
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
