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
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;

/**
 * Object handling all Rules and Events; Depending on the current State of
 * Resolution and the corresponding Knowledge, Rules and Events are in one
 * of the States Relevant, Obsolete or Triggered.
 * 
 * @author marco@juliano.de
 * 
 */
public class RulesAndEventsHandler implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesAndEventsHandler.class);

  private static final int MAX_NUM_EVENTS = EventStack.DEFAULT_MAX_MAP_SIZE * 2;
  private static final int MAX_NUM_RULES = RuleStack.DEFAULT_MAX_MAP_SIZE;

  private final EventStack relevantEvents;
  private final EventStack obsoleteEvents;
  private final EventStack triggeredEvents;

  private final RuleStack relevantRules;
  private final RuleStack obsoleteRules;
  private final RuleStack triggeredRules;

  private boolean knowledgeDirty;

  private RulesAndEventsHandler(final List<Event> relevantEvents, final List<Rule> relevantRules) {
    this(relevantEvents, MAX_NUM_EVENTS, relevantRules, MAX_NUM_RULES);
  }

  private RulesAndEventsHandler(final List<Event> relevantEvents, final int maxEvents, final List<Rule> relevantRules, final int maxRules) {
    this.obsoleteEvents = new EventStack(maxEvents);
    this.triggeredEvents = new EventStack(maxEvents);
    this.relevantEvents = new EventStack(maxEvents);
    this.relevantEvents.setEvents(relevantEvents);
    this.obsoleteRules = new RuleStack(maxRules);
    this.triggeredRules = new RuleStack(maxRules);
    this.relevantRules = new RuleStack(maxRules);
    this.relevantRules.setRules(relevantRules);
    this.knowledgeDirty = false;
  }

  public boolean isKnowledgeDirty() {
    return this.knowledgeDirty;
  }

  public void setKnowledgeDirty(final boolean knowledgeDirty) {
    this.knowledgeDirty = knowledgeDirty;
  }

  // ----------------------------------------------------------------

  public List<Event> getRelevantEvents() {
    return this.relevantEvents.getEvents();
  }

  public void setRelevantEvents(final Collection<? extends Event> events) {
    this.relevantEvents.setEvents(events);
  }

  public void addRelevantEvents(final Collection<? extends Event> events) {
    this.relevantEvents.addEvents(events);
  }

  public boolean isRelevant(final String eid) {
    return this.relevantEvents.containsEvent(eid);
  }

  public boolean isRelevant(final Event e) {
    return this.relevantEvents.containsEvent(e);
  }

  public boolean isObsolete(final String eid) {
    return this.obsoleteEvents.containsEvent(eid);
  }

  public boolean isObsolete(final Event e) {
    return this.obsoleteEvents.containsEvent(e);
  }

  public boolean isTriggered(final String eid) {
    return this.triggeredEvents.containsEvent(eid);
  }

  public boolean isTriggered(final Event e) {
    return this.triggeredEvents.containsEvent(e);
  }

  public Event setObsolete(final Event e) {
    return this.relevantEvents.move2stack(e, this.obsoleteEvents);
  }

  public Event setTriggered(final Event e) {
    return this.relevantEvents.move2stack(e, this.triggeredEvents);
  }

  // ----------------------------------------------------------------

  public List<Rule> getRelevantRules() {
    return this.relevantRules.getRules();
  }

  public void setRelevantRules(final Collection<? extends Rule> rules) {
    this.relevantRules.setRules(rules);
  }

  public void addRelevantRules(final Collection<? extends Rule> rules) {
    this.relevantRules.addRules(rules);
  }

  public Rule setObsolete(final Rule r) {
    return this.relevantRules.move2stack(r, this.obsoleteRules);
  }

  public Rule setTriggered(final Rule r) {
    return this.relevantRules.move2stack(r, this.triggeredRules);
  }

  // ----------------------------------------------------------------

  /**
   * Mark all Events and Rules attached to a Variant as obsolete.
   * 
   * @param variant
   */
  public void setObsolete(final Variant variant) {
    final String variantId = (variant == null ? null : variant.getVariantID());
    setObsolete(variantId);
  }

  /**
   * Mark all Events and Rules attached to a Variant as obsolete.
   * 
   * @param variantId
   */
  public void setObsolete(final String variantId) {
    if (!StringUtils.isEmpty(variantId)) {
      for (final Event e : getRelevantEvents()) {
        final String vid = (e == null ? null : e.getVariantID());
        if (variantId.equals(vid)) {
          setObsolete(e);
        }
      }
      for (final Rule r : getRelevantRules()) {
        final String vid = (r == null ? null : r.getVariantID());
        if (variantId.equals(vid)) {
          setObsolete(r);
        }
      }
    }
  }

  // ----------------------------------------------------------------

  public static RulesAndEventsHandler init(final KnowledgeBase kb) {
    final Events events = kb == null ? null : kb.getEvents();
    final Rules rules = kb == null ? null : kb.getRules();
    // simple initialization: all rules and events are relevant by default
    return init(events, rules);
  }

  public static RulesAndEventsHandler init(final KnowledgeBase kb, final Knowledge knowledge) {
    // sophisticated more performant initialization: only rules and events
    // currently "visible" in our knowledge are relevant
    // TODO: implement!!!
    return init(kb); // fallback to simple init
  }

  public static RulesAndEventsHandler init(final Events relevantEvents, final Rules relevantRules) {
    final List<Event> events = relevantEvents == null ? null : relevantEvents.getEvent();
    final List<Rule> rules = relevantRules == null ? null : relevantRules.getRule();
    return init(events, rules);
  }

  public static RulesAndEventsHandler init(final List<Event> relevantEvents, final List<Rule> relevantRules) {
    final RulesAndEventsHandler raeh = new RulesAndEventsHandler(relevantEvents, relevantRules);
    logContents(raeh);
    return raeh;
  }

  // ----------------------------------------------------------------

  public static void logContents(final RulesAndEventsHandler raeh) {
    if (raeh != null) {
      raeh.logContents();
    }
  }

  public void logContents() {
    if (LOGGER.isDebugEnabled()) {
      final boolean verbose = LOGGER.isTraceEnabled();
      final StringBuilder sb = new StringBuilder();
      dumpAllObjects(sb, verbose);
      if (verbose) {
        LOGGER.trace(sb.toString());
      }
      else {
        LOGGER.debug(sb.toString());
      }
    }
  }

  public void dumpAllObjects(final StringBuilder sb, final boolean verbose) {
    if (sb != null) {
      if (verbose) {
        sb.append(toString());
      }
      if (isKnowledgeDirty()) {
        sb.append("State of Knowledge is dirty.");
      }
      else {
        sb.append("Knowledge is stable.");
      }
      if (verbose) {
        sb.append("\n------------------------------------------------------------\n");
      }
      dumpEvents(sb, verbose);
      if (verbose) {
        sb.append("------------------------------------------------------------\n");
      }
      dumpRules(sb, verbose);
    }
  }

  public void dumpEvents(final StringBuilder sb, final boolean verbose) {
    if (sb != null) {
      sb.append("RELEVANT: ");
      this.relevantEvents.dumpEvents(sb, verbose);
      sb.append("TRIGGERED: ");
      this.triggeredEvents.dumpEvents(sb, verbose);
      sb.append("OBSOLETE: ");
      this.obsoleteEvents.dumpEvents(sb, verbose);
    }
  }

  public void dumpRules(final StringBuilder sb, final boolean verbose) {
    if (sb != null) {
      sb.append("RELEVANT: ");
      this.relevantRules.dumpRules(sb, verbose);
      sb.append("TRIGGERED: ");
      this.triggeredRules.dumpRules(sb, verbose);
      sb.append("OBSOLETE: ");
      this.obsoleteRules.dumpRules(sb, verbose);
    }
  }
}
