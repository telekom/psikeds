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
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

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
  private FeatureValues possibleValues;

  public FeatureChoice() {
    this(null, null, null);
  }

  public FeatureChoice(final String parentVariantID) {
    this(parentVariantID, null, null);
  }

  public FeatureChoice(final String parentVariantID, final String featureID) {
    this(parentVariantID, featureID, null);
  }

  public FeatureChoice(final String parentVariantID, final String featureID, final FeatureValues possibleValues) {
    super(parentVariantID);
    setFeatureID(featureID);
    setPossibleValues(possibleValues);
  }

  public FeatureChoice(final Variant parentVariant, final Feature feature) {
    this(parentVariant.getVariantID(), feature.getFeatureID());
  }

  public FeatureChoice(final String parentVariantID, final FeatureValue value) {
    this(parentVariantID);
    setValue(value);
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }

  public FeatureValues getPossibleValues() {
    if (this.possibleValues == null) {
      this.possibleValues = new FeatureValues();
    }
    return this.possibleValues;
  }

  public void setPossibleValues(final FeatureValues possibleValues) {
    this.possibleValues = possibleValues;
  }

  public void addPossibleValues(final Collection<? extends FeatureValue> col) {
    if ((col != null) && !col.isEmpty()) {
      for (final FeatureValue fv : col) {
        addPossibleValue(fv);
      }
    }
  }

  public void addPossibleValue(final FeatureValue value) {
    if (value != null) {
      getPossibleValues().add(value);
      setFeatureID(value.getFeatureID());
    }
  }

  public void clearPossibleValues() {
    if (this.possibleValues != null) {
      this.possibleValues.clear();
      this.possibleValues = null;
    }
  }

  @JsonIgnore
  public void setValue(final FeatureValue value) {
    clearPossibleValues();
    addPossibleValue(value);
  }
}
