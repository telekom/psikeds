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
 * List of Relations.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Relations")
public class Relations extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Relation> relation;

  public Relations() {
    this(null);
  }

  public Relations(final List<Relation> relation) {
    super();
    setRelation(relation);
  }

  public List<Relation> getRelation() {
    if (this.relation == null) {
      this.relation = new ArrayList<Relation>();
    }
    return this.relation;
  }

  public boolean addRelation(final Relation value) {
    return getRelation().add(value);
  }

  public void setRelation(final List<Relation> relation) {
    this.relation = relation;
  }
}
