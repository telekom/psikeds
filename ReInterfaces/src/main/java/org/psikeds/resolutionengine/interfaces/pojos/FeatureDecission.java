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
 * A Decission made: Which Value was selected for which Feature of which Variant?
 * 
 * Note 1: VariantID and FeatureID must reference existing Objects!
 * 
 * Note 2: FeatureValueID must be one of the allowed Values of the Feature.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureDecission")
public class FeatureDecission extends Decission implements Serializable {

  private static final long serialVersionUID = 1L;

  private String variantID;
  private String featureID;
  private String featureValueID;

  public FeatureDecission() {
    this((String) null, (String) null, (String) null);
  }

  public FeatureDecission(final FeatureChoice choice, final int index) {
    this(choice, choice.getPossibleValues().get(index).getFeatureValueID());
  }

  public FeatureDecission(final FeatureChoice choice, final String featureValueID) {
    this(choice.getParentVariantID(), choice.getFeatureID(), featureValueID);
  }

  public FeatureDecission(final Variant variant, final FeatureValue value) {
    this(variant.getVariantID(), value.getFeatureID(), value.getFeatureValueID());
  }

  public FeatureDecission(final String variantID, final String featureID, final String featureValueID) {
    super(variantID, featureID);
    setVariantID(variantID);
    setFeatureID(featureID);
    setFeatureValueID(featureValueID);
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

  public String getFeatureValueID() {
    return this.featureValueID;
  }

  public void setFeatureValueID(final String featureValueID) {
    this.featureValueID = featureValueID;
  }
}
