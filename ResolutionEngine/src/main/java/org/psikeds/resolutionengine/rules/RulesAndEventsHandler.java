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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;

/**
 * Object handling all Rules and Events; Depending on the current State of
 * Resolution and the corresponding Knowledge, Rules and Events are in one
 * of the Stats Possible, Obsolete or Triggered.
 * 
 * @author marco@juliano.de
 * 
 */
public class RulesAndEventsHandler implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesAndEventsHandler.class);

  private static final int MAX_NUM_EVENTS = 1024;
  private static final int MAX_NUM_RULES = 512;

  private final EventStack possibleEvents;
  private final EventStack obsoleteEvents;
  private final EventStack triggeredEvents;

  private final RuleStack possibleRules;
  private final RuleStack obsoleteRules;
  private final RuleStack triggeredRules;

  private boolean knowledgeDirty;

  private RulesAndEventsHandler(final List<Event> allEvents, final int maxEvents, final List<Rule> allRules, final int maxRules) {
    this.obsoleteEvents = new EventStack(maxEvents);
    this.triggeredEvents = new EventStack(maxEvents);
    this.possibleEvents = new EventStack(maxEvents);
    this.possibleEvents.setEvents(allEvents);
    this.obsoleteRules = new RuleStack(maxRules);
    this.triggeredRules = new RuleStack(maxRules);
    this.possibleRules = new RuleStack(maxRules);
    this.possibleRules.setRules(allRules);
    this.knowledgeDirty = false;
  }

  public boolean isKnowledgeDirty() {
    return this.knowledgeDirty;
  }

  public void setKnowledgeDirty(final boolean knowledgeDirty) {
    this.knowledgeDirty = knowledgeDirty;
  }

  // ------------------------------------------------------

  public List<Event> getPossibleEvents() {
    return this.possibleEvents.getEvents();
  }

  public boolean isPossible(final String id) {
    return this.possibleEvents.containsEvent(id);
  }

  public boolean isPossible(final Event e) {
    return this.possibleEvents.containsEvent(e);
  }

  public boolean isObsolete(final String id) {
    return this.obsoleteEvents.containsEvent(id);
  }

  public boolean isObsolete(final Event e) {
    return this.obsoleteEvents.containsEvent(e);
  }

  public boolean isTriggered(final String id) {
    return this.triggeredEvents.containsEvent(id);
  }

  public boolean isTriggered(final Event e) {
    return this.triggeredEvents.containsEvent(e);
  }

  public Event setObsolete(final Event e) {
    return this.possibleEvents.move2stack(e, this.obsoleteEvents);
  }

  public Event setTriggered(final Event e) {
    return this.possibleEvents.move2stack(e, this.triggeredEvents);
  }

  // ------------------------------------------------------

  public List<Rule> getPossibleRules() {
    return this.possibleRules.getRules();
  }

  public Rule setObsolete(final Rule r) {
    return this.possibleRules.move2stack(r, this.obsoleteRules);
  }

  public Rule setTriggered(final Rule r) {
    return this.possibleRules.move2stack(r, this.triggeredRules);
  }

  // ------------------------------------------------------

  public static RulesAndEventsHandler init(final KnowledgeBase kb) {
    final Events events = kb == null ? null : kb.getEvents();
    final Rules rules = kb == null ? null : kb.getRules();
    return init(events, rules);
  }

  public static RulesAndEventsHandler init(final Events allEvents, final Rules allRules) {
    final List<Event> events = allEvents == null ? null : allEvents.getEvent();
    final List<Rule> rules = allRules == null ? null : allRules.getRule();
    return init(events, rules);
  }

  // TODO: performance optimization: do not start with all rules and events but with "visible" ones

  public static RulesAndEventsHandler init(final List<Event> allEvents, final List<Rule> allRules) {
    final RulesAndEventsHandler raeh = new RulesAndEventsHandler(allEvents, MAX_NUM_EVENTS, allRules, MAX_NUM_RULES);
    logContents(raeh);
    return raeh;
  }

  // ------------------------------------------------------

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
      sb.append("POSSIBLE: ");
      this.possibleEvents.dumpEvents(sb, verbose);
      sb.append("TRIGGERED: ");
      this.triggeredEvents.dumpEvents(sb, verbose);
      sb.append("OBSOLETE: ");
      this.obsoleteEvents.dumpEvents(sb, verbose);
    }
  }

  public void dumpRules(final StringBuilder sb, final boolean verbose) {
    if (sb != null) {
      sb.append("POSSIBLE: ");
      this.possibleRules.dumpRules(sb, verbose);
      sb.append("TRIGGERED: ");
      this.triggeredRules.dumpRules(sb, verbose);
      sb.append("OBSOLETE: ");
      this.obsoleteRules.dumpRules(sb, verbose);
    }
  }
}
