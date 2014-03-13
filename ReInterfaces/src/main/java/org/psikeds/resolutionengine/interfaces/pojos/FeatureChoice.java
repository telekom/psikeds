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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import org.apache.cxf.common.util.StringUtils;

/**
 * A possible Choice: Which Values can be choosen for a Feature
 * of some (Parent-)Variant?
 * 
 * Note 1: A detailed Description of the Feature is "attached" to
 * the Variant-Object specified by the Parent-Variant-ID.
 * 
 * Note 2: Feature-ID and Variant-ID must point to existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureChoice")
public class FeatureChoice extends Choice implements Serializable {

  private static final long serialVersionUID = 1L;

  private String featureID;
  private List<String> possibleValues;

  public FeatureChoice() {
    this((String) null, (String) null);
  }

  public FeatureChoice(final Variant parentVariant, final FeatureDescription featureDescription) {
    this(parentVariant, featureDescription, null);
  }

  public FeatureChoice(final Variant parentVariant, final FeatureDescription featureDescription, final List<String> possibleValues) {
    this((parentVariant == null ? null : parentVariant.getVariantID()), (featureDescription == null ? null : featureDescription.getFeatureID()), possibleValues);
  }

  public FeatureChoice(final String parentVariantID, final String featureID) {
    this(parentVariantID, featureID, null);
  }

  public FeatureChoice(final String parentVariantID, final String featureID, final List<String> possibleValues) {
    super(parentVariantID);
    this.featureID = featureID;
    this.possibleValues = possibleValues;
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }

  public List<String> getPossibleValues() {
    if (this.possibleValues == null) {
      this.possibleValues = new ArrayList<String>();
    }
    return this.possibleValues;
  }

  public void setPossibleValues(final List<String> possibleValues) {
    this.possibleValues = possibleValues;
  }

  public void addPossibleValue(final String value) {
    if (!StringUtils.isEmpty(value)) {
      getPossibleValues().add(value);
    }
  }

  public void clearPossibleValues() {
    if (this.possibleValues != null) {
      this.possibleValues.clear();
      this.possibleValues = null;
    }
  }

  @JsonIgnore
  public void setValue(final String value) {
    clearPossibleValues();
    addPossibleValue(value);
  }

  @JsonIgnore
  public void setValue(final FeatureValue fv) {
    if (fv != null) {
      setFeatureID(fv.getFeatureID());
      setValue(fv.getValue());
    }
  }

  /**
   * Check whether a made Decission matches to this Choice, i.e. whether
   * the Client selected one of the allowed Values for the Feature.
   * 
   * @param decission
   * @return true if matching, false else
   */
  @Override
  public boolean matches(final Decission decission) {
    boolean ret;
    try {
      final FeatureDecission fd = (FeatureDecission) decission;
      ret = (this.parentVariantID.equals(fd.getVariantID()) && this.featureID.equals(fd.getFeatureID()) && this.possibleValues.contains(fd.getFeatureValue()));
    }
    catch (final Exception ex) {
      // Either not a FeatureDecission or one of the Objects was NULL
      ret = false;
    }
    return ret;
  }
}
