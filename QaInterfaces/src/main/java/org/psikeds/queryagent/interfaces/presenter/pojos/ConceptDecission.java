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
 * A Decission made: Which Concept was selected for a Variant?
 * 
 * Note: VariantID and ConceptID must reference existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ConceptDecission")
public class ConceptDecission extends Decission implements Serializable {

  private static final long serialVersionUID = 1L;

  private String variantID;
  private String conceptID;

  public ConceptDecission() {
    this((String) null, (String) null);
  }

  public ConceptDecission(final Variant variant, final Concept concept) {
    this(variant.getVariantID(), concept.getConceptID());
  }

  public ConceptDecission(final String variantID, final String conceptID) {
    super(variantID, conceptID);
    setVariantID(variantID);
    setConceptID(conceptID);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public String getConceptID() {
    return this.conceptID;
  }

  public void setConceptID(final String conceptID) {
    this.conceptID = conceptID;
  }
}
