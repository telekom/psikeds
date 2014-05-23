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
 * A List of Rules.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Rules")
public class Rules extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Rule> rule;

  public Rules() {
    this(null);
  }

  public Rules(final List<Rule> rule) {
    super();
    setRule(rule);
  }

  public List<Rule> getRule() {
    if (this.rule == null) {
      this.rule = new ArrayList<Rule>();
    }
    return this.rule;
  }

  public boolean addRule(final Rule value) {
    return ((value != null) && getRule().add(value));
  }

  public void setRule(final List<Rule> lst) {
    this.rule = lst;
  }
}
