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
package org.psikeds.resolutionengine.resolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;

/**
 * All Rules relevant for current State of Resolution
 * 
 * @author marco@juliano.de
 * 
 */
public class RelevantRules extends ConcurrentHashMap<String, List<Rule>> implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String KEY_FORMAT_TRIGGER = "TriggerEvent:%s";
  private static final String KEY_FORMAT_PREMISE = "PremiseEvent:%s";
  private static final String KEY_FORMAT_CONCLUSION = "ConclusionEvent:%s";
  private static final String KEY_FORMAT_VARIANT = "Variant:%s";
  private static final String KEY_FORMAT_RULE = "Rule:%s";

  public RelevantRules() {
    this(null);
  }

  public RelevantRules(final Knowledge knowledge) {
    init(knowledge);
  }

  // ------------------------------------------------------

  public Rule getRuleById(final String ruleId) {
    final List<Rule> lst = (StringUtils.isEmpty(ruleId) ? null : get(String.format(KEY_FORMAT_RULE, ruleId)));
    return (((lst == null) || lst.isEmpty()) ? null : lst.get(0));
  }

  public List<Rule> getRulesByVariant(final Variant v) {
    return (v == null ? null : getRulesByVariant(v.getId()));
  }

  public List<Rule> getRulesByVariant(final String variantId) {
    return (StringUtils.isEmpty(variantId) ? null : get(String.format(KEY_FORMAT_VARIANT, variantId)));
  }

  public List<Rule> getRulesByTriggerEvent(final Event te) {
    return getRulesByEvent(KEY_FORMAT_TRIGGER, te);
  }

  public List<Rule> getRulesByTriggerEvent(final String teid) {
    return getRulesByEvent(KEY_FORMAT_TRIGGER, teid);
  }

  public List<Rule> getRulesByPremiseEvent(final Event pe) {
    return getRulesByEvent(KEY_FORMAT_PREMISE, pe);
  }

  public List<Rule> getRulesByPremiseEvent(final String peid) {
    return getRulesByEvent(KEY_FORMAT_PREMISE, peid);
  }

  public List<Rule> getRulesByConclusionEvent(final Event ce) {
    return getRulesByEvent(KEY_FORMAT_CONCLUSION, ce);
  }

  public List<Rule> getRulesByConclusionEvent(final String ceid) {
    return getRulesByEvent(KEY_FORMAT_CONCLUSION, ceid);
  }

  private List<Rule> getRulesByEvent(final String keyformat, final Event e) {
    return (e == null ? null : getRulesByEvent(keyformat, e.getId()));
  }

  private List<Rule> getRulesByEvent(final String keyformat, final String eventId) {
    return ((StringUtils.isEmpty(keyformat) || StringUtils.isEmpty(eventId)) ? null : get(String.format(keyformat, eventId)));
  }

  //------------------------------------------------------

  public void addRule(final Rule r) {
    addRuleById(r);
    addRuleByVariant(r);
    addRuleByTriggerEvent(r);
    addRuleByPremiseEvent(r);
    addRuleByConclusionEvent(r);
  }

  private void addRuleByVariant(final Rule r) {
    final String variantId = (r == null ? null : r.getVariantID());
    if (!StringUtils.isEmpty(variantId)) {
      List<Rule> lst = getRulesByVariant(variantId);
      if (lst == null) {
        lst = new ArrayList<Rule>();
      }
      lst.add(r);
      put(String.format(KEY_FORMAT_VARIANT, variantId), lst);
    }
  }

  private void addRuleByTriggerEvent(final Rule r) {
    final String eventId = (r == null ? null : r.getTriggerEventID());
    if (!StringUtils.isEmpty(eventId)) {
      List<Rule> lst = getRulesByTriggerEvent(eventId);
      if (lst == null) {
        lst = new ArrayList<Rule>();
      }
      lst.add(r);
      put(String.format(KEY_FORMAT_TRIGGER, eventId), lst);
    }
  }

  private void addRuleByPremiseEvent(final Rule r) {
    final String eventId = (r == null ? null : r.getPremiseEventID());
    if (!StringUtils.isEmpty(eventId)) {
      List<Rule> lst = getRulesByPremiseEvent(eventId);
      if (lst == null) {
        lst = new ArrayList<Rule>();
      }
      lst.add(r);
      put(String.format(KEY_FORMAT_PREMISE, eventId), lst);
    }
  }

  private void addRuleByConclusionEvent(final Rule r) {
    final String eventId = (r == null ? null : r.getConclusionEventID());
    if (!StringUtils.isEmpty(eventId)) {
      List<Rule> lst = getRulesByConclusionEvent(eventId);
      if (lst == null) {
        lst = new ArrayList<Rule>();
      }
      lst.add(r);
      put(String.format(KEY_FORMAT_CONCLUSION, eventId), lst);
    }
  }

  private void addRuleById(final Rule r) {
    final String ruleId = (r == null ? null : r.getRuleID());
    if (!StringUtils.isEmpty(ruleId)) {
      List<Rule> lst = get(String.format(KEY_FORMAT_RULE, ruleId));
      // there might be several rules per event or variant
      // but never two rules with the same id!
      if (lst == null) {
        lst = new ArrayList<Rule>();
        lst.add(r);
        put(String.format(KEY_FORMAT_RULE, ruleId), lst);
      }
    }
  }

  // ------------------------------------------------------

  private void init(final Knowledge knowledge) {
    clear();
    if (knowledge != null) {
      // TODO
    }
  }
}
