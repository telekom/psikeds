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

/**
 * Interface object representing a List of all Rules.
 *
 * Note: Reading from and writing to JSON works out of the box.
 *       However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "Rules")
public class Rules extends POJO implements Serializable {

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
    return getRule().add(value);
  }

  public void setRule(final List<Rule> lst) {
    this.rule = lst;
  }
}
