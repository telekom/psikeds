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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A possible Choice: Which Concepts can be choosen for a (Parent-)Variant?
 * 
 * Note: A detailed Description of the Concept and its Feature(Value)s is
 * "attached" to the Variant-Object specified by the Parent-Variant-ID.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ConceptChoice")
public class ConceptChoice extends Choice implements Serializable {

  private static final long serialVersionUID = 1L;

  private Concepts concepts;

  public ConceptChoice() {
    this(null);
  }

  public ConceptChoice(final Variant parentVariant) {
    this(parentVariant, null);
  }

  public ConceptChoice(final Variant parentVariant, final Concept con) {
    this((parentVariant == null ? null : parentVariant.getVariantID()), null);
    setConcept(con);
  }

  public ConceptChoice(final String parentVariantID, final Concepts concepts) {
    super(parentVariantID);
    setConcepts(concepts);
  }

  public Concepts getConcepts() {
    if (this.concepts == null) {
      this.concepts = new Concepts();
    }
    return this.concepts;
  }

  public void setConcepts(final Concepts concepts) {
    this.concepts = concepts;
  }

  public boolean addConcept(final Concept con) {
    return ((con != null) && getConcepts().add(con));
  }

  public void clearConcepts() {
    if (this.concepts != null) {
      this.concepts.clear();
      this.concepts = null;
    }
  }

  @JsonIgnore
  public void setConcept(final Concept con) {
    clearConcepts();
    addConcept(con);
  }
}
