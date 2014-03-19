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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

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
}
