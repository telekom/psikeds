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
 * List of Alternatives, i.e. of all Fulfills-Relations.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Alternatives")
public class Alternatives extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Fulfills> fulfills;

  public Alternatives() {
    this(null);
  }

  public Alternatives(final List<Fulfills> fulfills) {
    super();
    setFulfills(fulfills);
  }

  public List<Fulfills> getFulfills() {
    if (this.fulfills == null) {
      this.fulfills = new ArrayList<Fulfills>();
    }
    return this.fulfills;
  }

  public boolean addFulfills(final Fulfills value) {
    return ((value != null) && getFulfills().add(value));
  }

  public void setFulfills(final List<Fulfills> lst) {
    this.fulfills = lst;
  }
}
