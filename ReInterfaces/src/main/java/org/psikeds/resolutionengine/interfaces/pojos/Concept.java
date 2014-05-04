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
 * Interface object representing a Concept, i.e. a subsumption
 * of Features-Values for a Variant.
 * 
 * Note 1: Concept-ID and Feature-Value-IDs must be globally unique!
 * 
 * Note 2: Feature-Value-IDs must reference distinct Features.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Concept")
public class Concept extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private FeatureValues values;

  public Concept() {
    this(null, null);
  }

  public Concept(final String conceptID, final FeatureValues values) {
    super(conceptID);
    setValues(values);
  }

  public String getConceptID() {
    return getId();
  }

  public void setConceptID(final String conceptID) {
    setId(conceptID);
  }

  public FeatureValues getValues() {
    if (this.values == null) {
      this.values = new FeatureValues();
    }
    return this.values;
  }

  public void setValues(final FeatureValues values) {
    this.values = values;
  }

  public boolean addValue(final FeatureValue value) {
    return ((value != null) && getValues().add(value));
  }

  public void clearValues() {
    if (this.values != null) {
      this.values.clear();
      this.values = null;
    }
  }
}
