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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a Concept-Definition/Declaration, i.e. a Combination/Bundle of
 * different Values for different Features.
 * 
 * Note 1: While all Values of a Feature must belong to this same Feature,
 * all Values of a Concept must be of distinct Features, i.e. exactly
 * one Value for each Feature.
 * 
 * Note 2: Concept-ID must be globally unique!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Concept")
public class Concept extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private List<FeatureValue> values;

  public Concept() {
    this(null);
  }

  public Concept(final String conceptID) {
    this(conceptID, null, conceptID, null);
  }

  public Concept(final String label, final String description, final String conceptID, final List<FeatureValue> values) {
    super(conceptID);
    setLabel(label);
    setDescription(description);
    setValues(values);
  }

  // ----------------------------------------------------------------

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

  public String getConceptID() {
    return getId();
  }

  public void setConceptID(final String conceptID) {
    setId(conceptID);
  }

  // ----------------------------------------------------------------

  public List<FeatureValue> getValues() {
    if (this.values == null) {
      this.values = new ArrayList<FeatureValue>();
    }
    return this.values;
  }

  public boolean addValue(final FeatureValue value) {
    return ((value != null) && getValues().add(value));
  }

  public boolean addValue(final Collection<? extends FeatureValue> values) {
    return ((values != null) && !values.isEmpty() && getValues().addAll(values));
  }

  public void setValues(final List<FeatureValue> values) {
    this.values = values;
  }

  public void clearValues() {
    if (this.values != null) {
      this.values.clear();
      this.values = null;
    }
  }
}
