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
 * Note 2: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureDecission")
public class FeatureDecission extends POJO implements Decission, Serializable {

  private static final long serialVersionUID = 1L;

  private String variantID;
  private String featureID;
  private String featureValue;

  public FeatureDecission() {
    this((Variant) null, (Feature) null, null);
  }

  public FeatureDecission(final FeatureChoice choice, final int index) {
    this(choice, choice.getValues().get(index));
  }

  public FeatureDecission(final FeatureChoice choice, final String featureValue) {
    this(choice.getParentVariant().getId(), choice.getFeature().getId(), featureValue);
  }

  public FeatureDecission(final String variantID, final String featureID, final String featureValue) {
    super(variantID, featureID);
    setVariantID(variantID);
    setFeatureID(featureID);
    setFeatureValue(featureValue);
  }

  public FeatureDecission(final Variant variant, final Feature feature, final String featureValue) {
    super(variant, feature);
    setVariant(variant);
    setFeature(feature);
    setFeatureValue(featureValue);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public void setVariant(final Variant variant) {
    this.variantID = (variant == null ? null : variant.getId());
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }

  public void setFeature(final Feature feature) {
    this.variantID = (feature == null ? null : feature.getId());
  }

  public String getFeatureValue() {
    return this.featureValue;
  }

  public void setFeatureValue(final String featureValue) {
    this.featureValue = featureValue;
  }
}
