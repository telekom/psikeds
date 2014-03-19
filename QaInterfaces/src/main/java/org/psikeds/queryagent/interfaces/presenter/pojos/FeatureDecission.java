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

/**
 * A Decission made: Which Value was selected for which Feature of which Variant?
 * 
 * Note 1: VariantID and FeatureID must reference existing Objects!
 * 
 * Note 2: Value must be a allowed one matching the Type of Feature.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureDecission")
public class FeatureDecission extends Decission implements Serializable {

  private static final long serialVersionUID = 1L;

  private String variantID;
  private String featureID;
  private String featureValue;

  public FeatureDecission() {
    this((String) null, (String) null, (String) null);
  }

  public FeatureDecission(final FeatureChoice choice, final int index) {
    this(choice, choice.getPossibleValues().get(index));
  }

  public FeatureDecission(final FeatureChoice choice, final String featureValue) {
    this(choice.getParentVariantID(), choice.getFeatureID(), featureValue);
  }

  public FeatureDecission(final Variant variant, final Feature feature, final String featureValue) {
    this(variant.getVariantID(), feature.getFeatureID(), featureValue);
  }

  public FeatureDecission(final String variantID, final String featureID, final String featureValue) {
    super(variantID, featureID);
    setVariantID(variantID);
    setFeatureID(featureID);
    setFeatureValue(featureValue);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }

  public String getFeatureValue() {
    return this.featureValue;
  }

  public void setFeatureValue(final String featureValue) {
    this.featureValue = featureValue;
  }
}
