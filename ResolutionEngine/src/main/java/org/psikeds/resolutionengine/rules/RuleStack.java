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
package org.psikeds.resolutionengine.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import org.psikeds.resolutionengine.cache.LimitedHashMap;
import org.psikeds.resolutionengine.datalayer.vo.Rule;

/**
 * A stack of Rules; Depending on the current State of Resolution and
 * the corresponding Knowledge, all Rules are either on the Possible-
 * or the Obsolete- or the Triggered-Stack.
 * 
 * @author marco@juliano.de
 * 
 */
public class RuleStack extends LimitedHashMap<String, Rule> implements Serializable {

  private static final long serialVersionUID = 1L;

  public RuleStack() {
    super();
  }

  public RuleStack(final int maxSize) {
    super(maxSize);
  }

  public List<Rule> getRules() {
    return new ArrayList<Rule>(this.values());
  }

  public void setRules(final List<Rule> rules) {
    clear();
    if (rules != null) {
      for (final Rule r : rules) {
        addRule(r);
      }
    }
  }

  public Rule addRule(final Rule r) {
    final String id = (r == null ? null : r.getId());
    return (StringUtils.isEmpty(id) ? null : this.put(id, r));
  }

  public Rule removeRule(final Rule r) {
    final String id = (r == null ? null : r.getId());
    return removeRule(id);
  }

  public Rule removeRule(final String id) {
    return (StringUtils.isEmpty(id) ? null : this.remove(id));
  }

  // ------------------------------------------------------

  public boolean containsRule(final Rule r) {
    return containsRule(r == null ? null : r.getId());
  }

  public boolean containsRule(final String id) {
    return (StringUtils.isEmpty(id) ? false : this.containsKey(id));
  }

  public Rule move2stack(final Rule r, final RuleStack destination) {
    final Rule orig = removeRule(r);
    return ((orig == null) || (destination == null) ? null : destination.addRule(r));
  }

  public Rule move2stack(final String ruleId, final RuleStack destination) {
    final Rule r = removeRule(ruleId);
    return ((r == null) || (destination == null) ? null : destination.addRule(r));
  }

  // ------------------------------------------------------

  public StringBuilder dumpRules(final StringBuilder sb, final boolean verbose) {
    sb.append("#Rules = ");
    sb.append(size());
    for (final Rule r : getRules()) {
      if (verbose) {
        sb.append('\n');
        sb.append(r);
      }
      else {
        sb.append(", ");
        sb.append(r.getId());
      }
    }
    sb.append('\n');
    return sb;
  }
}
