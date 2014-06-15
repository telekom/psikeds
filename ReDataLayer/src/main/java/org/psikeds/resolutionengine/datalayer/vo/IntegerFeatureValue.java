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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This is an Integer-Feature-Value, i.e. a possible Number that can be
 * assigned to an Attribute.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "IntegerFeatureValue")
public class IntegerFeatureValue extends FeatureValue implements Serializable {

  private static final long serialVersionUID = 1L;

  public IntegerFeatureValue() {
    this(null, null, null);
  }

  public IntegerFeatureValue(final String featureID, final String featureValueID, final String val) {
    super(featureID, featureValueID, Feature.VALUE_TYPE_INTEGER, val);
  }

  public IntegerFeatureValue(final String featureID, final String featureValueID, final long val) {
    this(featureID, featureValueID, null);
    setValue(val);
  }

  @JsonIgnore
  public void setValue(final long val) {
    this.value = String.valueOf(val);
  }
}
