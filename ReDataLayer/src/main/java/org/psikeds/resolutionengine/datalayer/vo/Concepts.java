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
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of Concepts.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Concepts")
public class Concepts extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Concept> concept;

  public Concepts() {
    this(null);
  }

  public Concepts(final List<Concept> lst) {
    super();
    setConcept(lst);
  }

  public List<Concept> getConcept() {
    if (this.concept == null) {
      this.concept = new ArrayList<Concept>();
    }
    return this.concept;
  }

  public boolean addConcept(final Concept value) {
    return ((value != null) && getConcept().add(value));
  }

  public void setConcept(final List<Concept> lst) {
    this.concept = lst;
  }
}
