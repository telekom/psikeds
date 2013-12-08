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
package org.psikeds.resolutionengine.resolver.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.ContextPath;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * This Resolver will evaluate the possible Events und apply all Rules
 * that were triggered. Decission will be ignored, Metadata is optional
 * and will be used for Information about Changes to the State of Events
 * and Rules, if present.
 * 
 * @author marco@juliano.de
 */
public class RulesEvaluator implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesEvaluator.class);

  private KnowledgeBase kb;
  private Transformer trans;
  private boolean autoCreateConclusionPath;
  private boolean createNonChoosableEntities;
  private boolean keepModusTollensForLater;
  private boolean repeatAfterRulesApplied;

  public RulesEvaluator() {
    this(null, null);
  }

  public RulesEvaluator(final KnowledgeBase kb, final Transformer trans) {
    this.kb = kb;
    this.trans = trans;
    this.autoCreateConclusionPath = true;
    this.createNonChoosableEntities = false;
    this.keepModusTollensForLater = true;
    this.repeatAfterRulesApplied = false;
  }

  public boolean isRepeatAfterRulesApplied() {
    return this.repeatAfterRulesApplied;
  }

  public void setRepeatAfterRulesApplied(final boolean repeatAfterRulesApplied) {
    this.repeatAfterRulesApplied = repeatAfterRulesApplied;
  }

  public boolean isKeepModusTollensForLater() {
    return this.keepModusTollensForLater;
  }

  public void setKeepModusTollensForLater(final boolean keepModusTollensForLater) {
    this.keepModusTollensForLater = keepModusTollensForLater;
  }

  public boolean isCreateNonChoosableEntities() {
    return this.createNonChoosableEntities;
  }

  public void setCreateNonChoosableEntities(final boolean createNonChoosableEntities) {
    this.createNonChoosableEntities = createNonChoosableEntities;
  }

  public boolean isAutoCreateConclusionPath() {
    return this.autoCreateConclusionPath;
  }

  public void setAutoCreateConclusionPath(final boolean autoCreateConclusionPath) {
    this.autoCreateConclusionPath = autoCreateConclusionPath;
  }

  public KnowledgeBase getKnowledgeBase() {
    return this.kb;
  }

  public void setKnowledgeBase(final KnowledgeBase kb) {
    this.kb = kb;
  }

  public Transformer getTransformer() {
    return this.trans;
  }

  public void setTransformer(final Transformer trans) {
    this.trans = trans;
  }

  // ----------------------------------------------------------------

  /**
   * Check that Rules-Evaluator was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.trans, "No Transformer!");
  }

  // ----------------------------------------------------------------

  /**
   * @param knowledge
   *          current old Knowledge (mandatory)
   * @param decission
   *          Decission (ignored!)
   * @param raeh
   *          all Rules and Events currently relevant, i.e. still possible
   *          and not triggered yet. (mandatory)
   * @param metadata
   *          Metadata (optional, can be null)
   * @return Knowledge resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final RulesAndEventsHandler raeh, final Metadata metadata) throws ResolutionException {
    boolean ok = false;
    boolean stable = true;
    try {
      LOGGER.debug("Evaluating Events and Rules ...");
      if ((knowledge == null) || (raeh == null)) {
        final String errmsg = "LOGICAL ERROR: Cannot evaluate Events and Rules: Required object(s) missing!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
      // knowledge is clean so far
      raeh.setKnowledgeDirty(false);
      // Part 1: Check Events, i.e. check whether an Event is still possible, triggered or already obsolete
      checkAllPossibleEvents(knowledge, raeh, metadata);
      // Part 2: Check Rules, i.e. apply or expire Rules depending on their Events (Premise, Trigger or Conclusion)
      stable = checkRules(knowledge, raeh, metadata);
      if (!stable) {
        // A rule was applied and it is neccessary to execute the full Resolver-Chain once again
        LOGGER.debug("Knowledge is not stable, need another Iteration of all Resolvers!");
        knowledge.setStable(false);
      }
      ok = true;
      if (this.repeatAfterRulesApplied && raeh.isKnowledgeDirty()) {
        // A rule was applied and we need to check Events and apply remaining Rules once again!
        LOGGER.debug("Rule applied, repeating checks of Events and Rules.");
        return resolve(knowledge, decission, raeh, metadata);
      }
      else {
        // done
        return knowledge;
      }
    }
    finally {
      RulesAndEventsHandler.logContents(raeh);
      LOGGER.debug("... finished evaluating Events and Rules, Result is " + (stable ? "" : "NOT ") + "stable and " + (ok ? "OK." : "with ERROR(S)!"));
    }
  }

  // ------------------------------------------------------

  // We only check Events that are still possible, i.e. obsolete Events or
  // Events that were already triggered will be ignored.
  // Also note, that all check...() Methods are fail fast, i.e. as soon as
  // an Event is not possible any more, the Check will abort.
  // ==> Performance

  private void checkAllPossibleEvents(final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    try {
      LOGGER.trace("--> checkAllPossibleEvents()");
      final List<Event> events = raeh.getPossibleEvents();
      for (final Event e : events) {
        // Check every possible Event
        checkEvent(e, knowledge, raeh, metadata);
      }
    }
    finally {
      LOGGER.trace("<-- checkAllPossibleEvents()");
    }
  }

  private boolean checkEvent(final Event e, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    try {
      LOGGER.trace("--> checkEvent(); Event = {}", e);
      final ContextPath cp = (e == null ? null : e.getContextPath());
      final List<String> path = (cp == null ? null : cp.getPathIDs());
      if ((path == null) || path.isEmpty()) {
        final String errmsg = "LOGICAL ERROR: Event or ContextPath missing!";
        LOGGER.warn(errmsg);
        markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(errmsg);
      }
      // Part 1 - Step 1:
      // Check whether we find the Root-Variant of the Event within our current Knowledge
      final List<KnowledgeEntity> root = findRoot(e, knowledge);
      if ((root == null) || root.isEmpty()) {
        LOGGER.debug("Nothing to do. Event {} is attached to Variant {} which is not (yet) included in the current Knowledge.", e.getId(), e.getVariantId());
        stillPossible = true;
        return stillPossible;
      }
      // Part 1 - Step 2:
      // Analyze the Context Path of the Event starting from the Root(s).
      // This reduces overhead, because we do not need to traverse the full graph.
      stillPossible = checkListOfEntities(e, path, root, true, raeh, metadata);
      if (!stillPossible && raeh.isPossible(e)) {
        // If an Event can be marked as obsolete can only be decided on the
        // highest Level, i.e. when none of the Knowledge-Entities and its
        // Child-Entities signaled that this Event was still possible!
        LOGGER.debug("Event is not possible any more: {}", e.getId());
        markEventObsolete(e, raeh, metadata);
      }
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkEvent(); Event = {}\nStill possible = {}", e, stillPossible);
    }
  }

  private boolean checkListOfEntities(final Event e, final List<String> path, final List<KnowledgeEntity> entities, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    try {
      LOGGER.trace("--> checkListOfEntities(); E = {}, Path = {}", e.getId(), path);
      for (final KnowledgeEntity ke : entities) {
        if (!raeh.isPossible(e)) {
          // Fail fast: Event was already triggered or made obsolete
          stillPossible = false;
          return stillPossible;
        }
        if (checkKnowledgeEntity(e, path, ke, isVariant, raeh, metadata)) {
          // Succeed fast: Event is still possible in this Part of the Knowledge-Graph
          stillPossible = true;
          return stillPossible;
        }
        if (checkChoices(e, path, ke, isVariant, raeh, metadata)) {
          // Succeed fast: Event is still possible in this Part of the Knowledge-Graph
          stillPossible = true;
          return stillPossible;
        }
      }
      // Nothing special happend, but Event is not possible in this Part
      // of the Knowledge-Graph any more
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkListOfEntities(); E = {}, Path = {}; Still possible = {}", e.getId(), path, stillPossible);
    }
  }

  private boolean checkKnowledgeEntity(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    boolean matching = false;
    try {
      LOGGER.trace("--> checkKnowledgeEntity(); E = {}; Path = {}\nKE = {}", e.getId(), path, ke);
      final int len = (path == null ? 0 : path.size());
      if (len <= 0) {
        // We walked the complete path ==> Event is fulfilled/triggered
        triggerEvent(e, raeh, metadata);
        matching = true;
        stillPossible = false;
        return stillPossible;
      }
      // Check the current ID in our Context Path
      final String currentPathElement = path.get(0);
      matching = checkCurrentPathElement(e, currentPathElement, ke, isVariant, raeh, metadata);
      if (!matching) {
        // Current Path Element was not matching, but
        // let's see whether there is a matching Choice
        stillPossible = checkChoices(e, currentPathElement, ke.getChoices(), isVariant, raeh, metadata);
        if (stillPossible) {
          if ((len == 1) && !isVariant) {
            // This is a special Event. It is triggered by the Availability
            // of a Purpose. Therefore this is a Match!
            LOGGER.debug("Path {} of Event {} ends with Purpose available as a Choice of this KE --> Trigger!", path, e.getId());
            triggerEvent(e, raeh, metadata);
            matching = true;
            stillPossible = false;
            return stillPossible;
          }
          else {
            LOGGER.debug("Current PE {} of Event {} is matching to Choice of this KE.", currentPathElement, e.getId());
          }
        }
        else {
          LOGGER.debug("Event {} is matching neither KE nor any Choice.", e.getId());
        }
      }
      else { // matching
        // Current Element was matching, Event is still possible ...
        if (len == 1) {
          // ... and this was the last Element. Gotcha!
          LOGGER.debug("Path {} of Event {} ends at this KE --> Trigger!", path, e.getId());
          triggerEvent(e, raeh, metadata);
          stillPossible = false;
          return stillPossible;
        }
        else { // len > 1
          // ... and there is still some portion of the Context Path left.
          // Let's see whether next part is matching a Choice, too.
          stillPossible = checkChoices(e, path.subList(1, len), ke, !isVariant, raeh, metadata);
          if (stillPossible) {
            if ((len == 2) && isVariant) {
              // This is a special Event. It is triggered by the Availability
              // of a Purpose. Note: We checked the next PE, so this PE must be
              // a Variant!
              LOGGER.debug("Path {} of Event {} ends with Purpose available as a Choice of this KE --> Trigger!", path, e.getId());
              triggerEvent(e, raeh, metadata);
              matching = true;
              stillPossible = false;
              return stillPossible;
            }
            else {
              // Path > 2 or Events is not ending with a Purpose
              // --> Event is still possible but not matching/triggered.
              LOGGER.debug("Next PE of Event {} is also matching to Choice of this KE --> not matching but still possible.", e.getId());
              matching = false;
            }
          }
          else {
            // So we must dig deeper into this part of the Knowledge-Graph.
            // Let's see whether next part is matching to this KE, too.
            stillPossible = checkKnowledgeEntity(e, path.subList(1, len), ke, !isVariant, raeh, metadata);
            if (stillPossible) {
              if (len > 2) {
                LOGGER.debug("Next PE of Event {} is also matching to this KE --> Recursion!", e.getId());
                matching = false;
                stillPossible = checkListOfEntities(e, path.subList(2, len), ke.getSiblings(), isVariant, raeh, metadata);
              }
              else {
                LOGGER.debug("Path {} of Event {} ends at this KE --> Trigger!", path, e.getId());
                triggerEvent(e, raeh, metadata);
                matching = true;
                stillPossible = false;
                return stillPossible;
              }
            }
            else {
              // Let's do some Recursion ;-)
              LOGGER.debug("PE of Event {} matching to this KE --> Recursion!", e.getId());
              matching = false;
              stillPossible = checkListOfEntities(e, path.subList(1, len), ke.getSiblings(), !isVariant, raeh, metadata);
            }
          }
        }
      }
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkKnowledgeEntity(); E = {}; Path = {}; matching = {}; stillPossible = {}", e.getId(), path, matching, stillPossible);
    }
  }

  private boolean checkCurrentPathElement(final Event e, final String currentPathElement, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matching = false;
    try {
      LOGGER.trace("--> checkCurrentPathElement(); E = {}; PE = {}; isVariant = {}", e.getId(), currentPathElement, isVariant);
      if (StringUtils.isEmpty(currentPathElement)) {
        final String errmsg = "LOGICAL ERROR: Current Path Element is empty!";
        LOGGER.warn(errmsg);
        markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(errmsg);
      }
      if (isVariant) {
        LOGGER.trace("Checking whether {} is matching to Variant.", currentPathElement);
        final Variant v = ke == null ? null : ke.getVariant();
        final String vid = v == null ? null : v.getId();
        if (currentPathElement.equals(vid)) {
          LOGGER.debug("Current PE is matching to Variant-ID: {}", vid);
          matching = true;
        }
      }
      else {
        LOGGER.trace("Checking whether {} is matching to Purpose.", currentPathElement);
        final Purpose p = ke == null ? null : ke.getPurpose();
        final String pid = p == null ? null : p.getId();
        if (currentPathElement.equals(pid)) {
          LOGGER.debug("Current PE is matching to Purpose-ID: {}", pid);
          matching = true;
        }
      }
      return matching;
    }
    finally {
      LOGGER.trace("<-- checkCurrentPathElement(); E = {}; PE = {}; isVariant = {}; matching = {}", e.getId(), currentPathElement, isVariant, matching);
    }
  }

  // ------------------------------------------------------

  private boolean checkChoices(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    final String currentPathElement = path.get(0);
    final List<Choice> choices = ke.getChoices();
    return checkChoices(e, currentPathElement, choices, isVariant, raeh, metadata);
  }

  private boolean checkChoices(final Event e, final String currentPathElement, final List<Choice> choices, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matchingChoice = false;
    try {
      LOGGER.trace("--> checkChoices(); E = {}; PE = {}; isVariant = {}", e.getId(), currentPathElement, isVariant);
      for (final Choice c : choices) {
        // Loop over all Choices and as soon as one is matching we are finished
        matchingChoice = checkChoice(e, currentPathElement, c, isVariant, raeh, metadata);
        if (matchingChoice) {
          // fail/succeed fast
          return matchingChoice;
        }
      }
      return matchingChoice;
    }
    finally {
      LOGGER.trace("<-- checkChoices(); E = {}; PE = {}; matchingChoice = {}", e.getId(), currentPathElement, matchingChoice);
    }
  }

  private boolean checkChoice(final Event e, final String currentPathElement, final Choice c, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matchingChoice = false;
    try {
      LOGGER.trace("--> checkChoice(); PE = {}; isVariant = {}\nChoice = {}", currentPathElement, isVariant, c);
      if (isVariant) {
        // Is Path Element matching any selectable Variant of this Choice?
        final List<Variant> lst = c == null ? null : c.getVariants();
        if (lst != null) {
          for (final Variant v : lst) {
            final String vid = (v == null ? null : v.getId());
            matchingChoice = currentPathElement.equals(vid);
            if (matchingChoice) {
              LOGGER.debug("PE {} is matching Variant of Choice {}", currentPathElement, c);
              return matchingChoice;
            }
          }
        }
      }
      else {
        // Is Path Element matching Purpose of this Choice?
        final Purpose p = (c == null ? null : c.getPurpose());
        final String pid = (p == null ? null : p.getId());
        matchingChoice = currentPathElement.equals(pid);
        if (matchingChoice) {
          LOGGER.debug("PE {} is matching Purpose of Choice {}", currentPathElement, c);
          return matchingChoice;
        }
      }
      return matchingChoice;
    }
    finally {
      LOGGER.trace("<-- checkChoice(); PE = {}; isVariant = {}; matchingChoice = {}", currentPathElement, isVariant, matchingChoice);
    }
  }

  // ------------------------------------------------------

  private void triggerEvent(final Event e, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if ((e != null) && raeh.isPossible(e)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("TRIGGERED: {}", e);
      }
      else {
        LOGGER.debug("TRIGGERED: {}", e.getId());
      }
      raeh.setTriggered(e);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "E_" + e.getId() + "_triggered";
        final String msg = String.valueOf(e);
        metadata.saveInfo(key, msg);
      }
    }
  }

  private void markEventObsolete(final Event e, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if ((e != null) && raeh.isPossible(e)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("OBSOLETE: {}", e);
      }
      else {
        LOGGER.debug("OBSOLETE: {}", e.getId());
      }
      raeh.setObsolete(e);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "E_" + e.getId() + "_obsolete";
        final String msg = String.valueOf(e);
        metadata.saveInfo(key, msg);
      }
    }
  }

  private void triggerRule(final Rule r, final RulesAndEventsHandler raeh, final Metadata metadata) {
    triggerRule(r, raeh, metadata, "_triggered");
  }

  private void triggerRule(final Rule r, final RulesAndEventsHandler raeh, final Metadata metadata, final String suffix) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("TRIGGERED: {}", r);
      }
      else {
        LOGGER.debug("TRIGGERED: {}", r.getId());
      }
      raeh.setTriggered(r);
      raeh.setKnowledgeDirty(true);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getId() + suffix;
        final String msg = String.valueOf(r);
        metadata.saveInfo(key, msg);
      }
    }
  }

  private void markRuleObsolete(final Rule r, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("OBSOLETE: {}", r);
      }
      else {
        LOGGER.debug("OBSOLETE: {}", r.getId());
      }
      raeh.setObsolete(r);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getId() + "_obsolete";
        final String msg = String.valueOf(r);
        metadata.saveInfo(key, msg);
      }
    }
  }

  // ------------------------------------------------------

  private boolean checkRules(final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRules()");
      final List<Rule> rules = raeh.getPossibleRules();
      for (final Rule r : rules) {
        final String ruleId = (r == null ? null : r.getId());
        if (!StringUtils.isEmpty(ruleId)) {
          LOGGER.trace("Checking Rule: {}", r);

          // Part 2 - Step A: For each Rule check State of Premise-, Trigger- and Conclusion-Event
          // premise
          final String peid = r.getPremiseEventID();
          final boolean premiseObsolete = raeh.isObsolete(peid);
          final boolean premiseTriggered = raeh.isTriggered(peid);
          // trigger
          final String teid = r.getTriggerEventID();
          final boolean triggerObsolete = raeh.isObsolete(teid);
          final boolean triggerTriggered = raeh.isTriggered(teid);
          // conclusion
          final String ceid = r.getConclusionEventID();
          final boolean conclusionObsolete = raeh.isObsolete(ceid);
          final boolean conclusionTriggered = raeh.isTriggered(ceid);

          // Part 2 - Step B: Analyze Rule depending on State of Events
          if (premiseObsolete || triggerObsolete) {
            LOGGER.debug("Premise {} or Trigger {} obsolete --> mark also Rule {} obsolete.", peid, teid, ruleId);
            markRuleObsolete(r, raeh, metadata);
          }
          else if (conclusionTriggered) {
            LOGGER.debug("Conclusion {} already triggered --> mark also Rule {} triggered.", ceid, ruleId);
            triggerRule(r, raeh, metadata);
          }
          else if (triggerTriggered && premiseTriggered) {
            LOGGER.debug("Both Premise {} and Trigger {} triggered --> MP: fire Rule {}.", peid, teid, ruleId);
            applyRuleModusPonens(r, knowledge, raeh, metadata);
            stable = false;
          }
          else if (premiseTriggered && conclusionObsolete) {
            LOGGER.debug("Premise {} triggered and Conclusion {} obsolete --> MT: fire Rule {}.", peid, ceid, ruleId);
            applyRuleModusTollens(r, knowledge, raeh, metadata);
            stable = false;
          }
          else {
            LOGGER.trace("Keeping still possible Rule: {}", ruleId);
          }
        }
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRules(); stable = {}", stable);
    }
  }

  // ------------------------------------------------------

  // Part 2 - Step C: Modus Ponens: trigger Conclusion
  private void applyRuleModusPonens(final Rule r, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    LOGGER.info("MODUS PONENS - applying Conclusion-Event of Rule: {}", r);
    final List<KnowledgeEntity> root = findRoot(r, knowledge);
    final String eventId = r.getConclusionEventID();
    if (applyConclusionEvent(root, eventId)) {
      triggerRule(r, raeh, metadata, "_modus_ponens");
    }
  }

  private boolean applyConclusionEvent(final List<KnowledgeEntity> root, final String eventId) {
    boolean applied = false;
    if ((root != null) && !root.isEmpty()) {
      LOGGER.trace("Triggering Event: {}", eventId);
      final Event e = this.kb.getEvent(eventId);
      final ContextPath cp = e == null ? null : e.getContextPath();
      final List<String> path = cp == null ? null : cp.getPathIDs();
      for (final KnowledgeEntity ke : root) {
        final boolean created = createPathEntries(ke, path);
        applied = applied || created;
      }
    }
    return applied;
  }

  private boolean createPathEntries(final KnowledgeEntity currentKE, final List<String> path) {
    boolean created = false;
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> createPathEntries(); Path = {}; KE = {}", path, currentKE);
      // Note: The first element in the Path is always the Variant of the current KE.
      // The second element must be one of its Purposes and the third Element is the
      // next Variant. So if the Path has less then 3 elements, we have nothing more
      // to do.
      final int len = (path == null ? 0 : path.size());
      if ((currentKE == null) || (len < 3)) {
        LOGGER.debug("Short or empty Path: Nothing to do!");
        created = true;
        return created;
      }
      final String nextPurpId = path.get(1);
      final String nextVarId = path.get(2);
      // check whether next knowledge entity already exists
      for (final KnowledgeEntity ke : currentKE.getSiblings()) {
        if (ke != null) {
          final Purpose p = ke.getPurpose();
          final Variant v = ke.getVariant();
          if ((p != null) && (v != null)) {
            final String pid = p.getId();
            final String vid = v.getId();
            if (nextPurpId.equals(pid) && nextVarId.equals(vid)) {
              nextKE = ke;
            }
          }
        }
      }
      if (nextKE != null) {
        if (len > 3) {
          // next ke found and still some path elements left
          created = createPathEntries(nextKE, path.subList(3, len));
        }
        else {
          LOGGER.debug("Last KE of Event already existing: Nothing to do!");
          created = true;
          return created;
        }
      }
      else {
        if (len > 3) {
          if (this.autoCreateConclusionPath) {
            // create next KE and continue with next path elements
            nextKE = createNewEntity(currentKE, nextPurpId, nextVarId);
            created = createPathEntries(nextKE, path.subList(3, len));
          }
          else {
            created = false;
            final String errmsg = "LOGICAL ERROR: Auto-Creation disabled but intermediate Path-Elements of Conclusion-Event missing! Check your Config and Premises in KB!";
            LOGGER.warn(errmsg);
            throw new ResolutionException(errmsg);
          }
        }
        else {
          nextKE = createNewEntity(currentKE, nextPurpId, nextVarId);
          created = (nextKE != null);
          LOGGER.debug("Finished! Created final KE: {}", nextKE);
        }
      }
      return created;
    }
    finally {
      LOGGER.trace("<-- createPathEntries(); Path = {}; created = {}\nNext KE = {}", path, created, nextKE);
    }
  }

  private void cleanupChoices(final KnowledgeEntity ke, final String purposeId) {
    cleanupChoices(ke, purposeId, null);
  }

  private void cleanupChoices(final KnowledgeEntity ke, final String purposeId, final String variantId) {
    final List<Choice> choices = ke.getChoices();
    final Iterator<Choice> citer = choices.iterator();
    while (citer.hasNext()) {
      final Choice c = citer.next();
      final Purpose p = c.getPurpose();
      if ((p != null) && purposeId.equals(p.getId())) {
        if (StringUtils.isEmpty(variantId)) {
          LOGGER.debug("Removing all Choices for Purpose {}", purposeId);
          citer.remove();
        }
        else {
          final List<Variant> variants = c.getVariants();
          final Iterator<Variant> viter = variants.iterator();
          while (viter.hasNext()) {
            final Variant v = viter.next();
            if ((v != null) && variantId.equals(v.getId())) {
              LOGGER.debug("Removing Variant {} from Choices for Purpose {}", variantId, purposeId);
              viter.remove();
            }
          }
        }
      }
    }
  }

  private KnowledgeEntity createNewEntity(final KnowledgeEntity parent, final String purposeId, final String variantId) {
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> createNewEntity(); PID = {}; VID = {}\nParent KE = {}", purposeId, variantId, parent);
      if (parent != null) {
        // check whether parent KE has a corresponding Choice for P and V
        Purpose expectedPurp = null;
        Variant expectedVar = null;
        for (final Choice c : parent.getChoices()) {
          final Purpose p = c.getPurpose();
          if ((p != null) && purposeId.equals(p.getId())) {
            // matching purpose found
            expectedPurp = p;
            for (final Variant v : c.getVariants()) {
              if ((v != null) && variantId.equals(v.getId())) {
                // matching variant found
                expectedVar = v;
              }
            }
          }
        }
        if ((expectedPurp != null) && (expectedVar != null)) {
          // perfect, the KE we want to create is really a choosable Combination of P and V!
          LOGGER.debug("Found expected Choice for {} and {}", purposeId, variantId);
          nextKE = regularlyCreateNewEntity(parent, expectedPurp, expectedVar);
        }
        else {
          // hmmm ... the KE we want to create is not a choosable Combination of P and V?!?!
          LOGGER.debug("Expected Choice for {} and {} not found!", purposeId, variantId);
          if (this.createNonChoosableEntities) {
            // ok, this is actually not choosable at the moment, but config says, we do it nevertheless
            nextKE = forceCreateNextEntity(parent, purposeId, variantId);
          }
          else {
            // Definition of Rules and Events in KB seems to be strange
            final StringBuilder sb = new StringBuilder();
            sb.append("LOGICAL ERROR: Not a choosable Combination! Cannot create new KE with P = ");
            sb.append(purposeId);
            sb.append(" and V = ");
            sb.append(variantId);
            sb.append(" for Parent ");
            sb.append(parent);
            final String errmsg = sb.toString();
            LOGGER.warn(errmsg);
            throw new ResolutionException(errmsg);
          }
        }
        // remove all choices for our purpose from parent KE
        cleanupChoices(parent, purposeId);
      }
      return nextKE;
    }
    finally {
      LOGGER.trace("<-- createNewEntity(); PID = {}; VID = {}\nNext KE = {}", purposeId, variantId, nextKE);
    }
  }

  private KnowledgeEntity regularlyCreateNewEntity(final KnowledgeEntity parent, final Purpose purp, final Variant var) {
    // get next choices for new KE
    final List<Choice> newchoices = getNewChoices(var);
    // create new KE including choices
    final KnowledgeEntity nextKE = new KnowledgeEntity(purp, var, newchoices);
    // attach new KE to parent KE
    addNewEntity(parent, nextKE);
    return nextKE;
  }

  private KnowledgeEntity forceCreateNextEntity(final KnowledgeEntity parent, final String purposeId, final String variantId) {
    try {
      LOGGER.trace("--> forceCreateNextEntity(); P = {}, V = {}", purposeId, variantId);
      // lookup purpose in kb
      final org.psikeds.resolutionengine.datalayer.vo.Purpose purp = this.kb.getPurpose(purposeId);
      final Purpose p = (purp == null ? null : this.trans.valueObject2Pojo(purp));
      // lookup variant in kb
      final org.psikeds.resolutionengine.datalayer.vo.Variant var = this.kb.getVariant(variantId);
      final Variant v = (var == null ? null : this.trans.valueObject2Pojo(var));
      if ((p == null) || (v == null)) {
        // KB is corrupt ... where is the Validator?
        final StringBuilder sb = new StringBuilder();
        sb.append("LOGICAL ERROR: Unknow IDs! Cannot force Creation of new KE with P = ");
        sb.append(purposeId);
        sb.append(" and V = ");
        sb.append(variantId);
        sb.append(" for Parent ");
        sb.append(parent);
        final String errmsg = sb.toString();
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
      return regularlyCreateNewEntity(parent, p, v);
    }
    finally {
      LOGGER.trace("<-- forceCreateNextEntity(); P = {}, V = {}", purposeId, variantId);
    }
  }

  private List<Choice> getNewChoices(final Variant v) {
    final List<Choice> choices = new ArrayList<Choice>();
    try {
      LOGGER.trace("--> getNewChoices: Variant = {}", v);
      // ensure clean data, therefore lookup parent-variant from knowledge base (again)
      final String variantId = v.getId();
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent = this.kb.getVariant(variantId);
      // get all purposes constituting parent-variant
      final org.psikeds.resolutionengine.datalayer.vo.Purposes newpurps = this.kb.getConstitutingPurposes(parent);
      // get for every purpose ...
      for (final org.psikeds.resolutionengine.datalayer.vo.Purpose p : newpurps.getPurpose()) {
        // ... all fulfilling variants ...
        final org.psikeds.resolutionengine.datalayer.vo.Variants newvars = this.kb.getFulfillingVariants(p);
        // ... and transform parent-variant, purpose and new variant
        //     into a Choice-POJO for the Client
        final Choice c = this.trans.valueObject2Pojo(parent, p, newvars);
        LOGGER.debug("Adding new Choice: {}", c);
        choices.add(c);
      }
      // return list of all new choices
      return choices;
    }
    finally {
      LOGGER.trace("<-- getNewChoices: Variant = {}\nChoices = {}", v, choices);
    }
  }

  private void addNewEntity(final KnowledgeEntity parent, final KnowledgeEntity ke) {
    final List<KnowledgeEntity> entities = parent.getSiblings();
    for (final KnowledgeEntity e : entities) {
      final String pid1 = e.getPurpose().getId();
      final String pid2 = ke.getPurpose().getId();
      final String vid1 = e.getVariant().getId();
      final String vid2 = ke.getVariant().getId();
      if (pid1.equals(pid2) && vid1.equals(vid2)) {
        LOGGER.trace("Entity-List already contains KnowledgeEntity: {}", ke);
        return;
      }
    }
    LOGGER.trace("Adding new KnowledgeEntity: {}", ke);
    entities.add(ke);
  }

  // ------------------------------------------------------

  // Part 2 - Step D: Modus Tollens: disable Trigger
  private void applyRuleModusTollens(final Rule r, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    LOGGER.info("MODUS TOLLENS - disabling Trigger-Event of Rule: {}", r);
    final List<KnowledgeEntity> root = findRoot(r, knowledge);
    final String eventId = r.getTriggerEventID();
    if (disableTriggerEvent(root, eventId)) {
      triggerRule(r, raeh, metadata, "_modus_tollens");
    }
  }

  private boolean disableTriggerEvent(final List<KnowledgeEntity> root, final String eventId) {
    boolean disabled = false;
    if ((root != null) && !root.isEmpty()) {
      LOGGER.trace("Disabling Event: {}", eventId);
      final Event e = this.kb.getEvent(eventId);
      final ContextPath cp = e == null ? null : e.getContextPath();
      final List<String> path = cp == null ? null : cp.getPathIDs();
      for (final KnowledgeEntity ke : root) {
        final boolean removed = removePathFromChoices(ke, path);
        disabled = disabled || removed;
      }
    }
    return disabled;
  }

  private boolean removePathFromChoices(final KnowledgeEntity currentKE, final List<String> path) {
    boolean removed = false;
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> removePathFromChoices(); Path = {}; KE = {}", path, currentKE);
      // Note: The first element in the Path is always the Variant of the current KE.
      // The second element must be one of its Purposes and the third Element is the
      // next Variant. So if the Path has less then 2 elements, we have nothing more
      // to do.
      final int len = (path == null ? 0 : path.size());
      if ((currentKE == null) || (len <= 1)) {
        LOGGER.debug("Short or empty Path: Nothing to do!");
        removed = true;
        return removed;
      }
      // Check that first Path Element is really matching to current KE
      final Variant currentVariant = currentKE.getVariant();
      final String currentVariantId = (currentVariant == null ? null : currentVariant.getId());
      final String expectedVariantId = path.get(0);
      if (StringUtils.isEmpty(expectedVariantId) || !expectedVariantId.equals(currentVariantId)) {
        final String errmsg = "LOGICAL ERROR: Path not matching to current KE!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }

      if (len == 2) {
        // There is just one additional PE left, we remove all Choices for this Purpose
        final String purposeId = path.get(1);
        cleanupChoices(currentKE, purposeId);
        removed = true;
        return removed;
      }
      if (len == 3) {
        // There are two more PEs left, so we remove that Variant from Choices for this Purpose
        final String purposeId = path.get(1);
        final String variantId = path.get(2);
        cleanupChoices(currentKE, purposeId, variantId);
        removed = true;
        return removed;

      }
      // Path has more than 3 Elements: Check whether next KnowledgeEntity exists
      final String nextPurpId = path.get(1);
      final String nextVarId = path.get(2);
      for (final KnowledgeEntity ke : currentKE.getSiblings()) {
        if (ke != null) {
          final Purpose p = ke.getPurpose();
          final Variant v = ke.getVariant();
          if ((p != null) && (v != null)) {
            final String pid = p.getId();
            final String vid = v.getId();
            if (nextPurpId.equals(pid) && nextVarId.equals(vid)) {
              nextKE = ke;
            }
          }
        }
      }
      if (nextKE != null) {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Recursion: Path {} matching next KE {}", path, nextKE);
        }
        else {
          LOGGER.debug("Next KE for Path {} found --> Recursion!", path);
        }
        removed = removePathFromChoices(nextKE, path.subList(3, len));
        return removed;
      }
      else {
        removed = false;
        LOGGER.trace("Cannot remove Path {} from current KE {}", path, currentKE);
        if (this.keepModusTollensForLater) {
          LOGGER.debug("Keeping Rule for later.");
          return removed;
        }
        final String errmsg = "LOGICAL ERROR: Cannot apply Modus Tollens, Trigger-Event cannot be disabled! Check your Config and Rules in KB!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
    }
    finally {
      LOGGER.trace("<-- removePathFromChoices(); Path = {}; created = {}\nNext KE = {}", path, removed, nextKE);
    }
  }

  // ------------------------------------------------------

  // In a regular Tree each Variant is used just once. However in a general
  // Knowledge-Graph a Variant could be (re)used several times for different
  // Purposes. Therefore the following Methods do not stop after the first
  // Hit and return a List of all Entities containing the desired Variant.

  private List<KnowledgeEntity> findRoot(final Event e, final Knowledge knowledge) {
    final String rootVariantId = e.getVariantId();
    final List<KnowledgeEntity> root = findRoot(rootVariantId, knowledge);
    LOGGER.trace("Root of Event {} is: {}", e.getId(), root);
    return root;
  }

  private List<KnowledgeEntity> findRoot(final Rule r, final Knowledge knowledge) {
    final String rootVariantId = r.getVariantID();
    final List<KnowledgeEntity> root = findRoot(rootVariantId, knowledge);
    LOGGER.trace("Root of Rule {} is: {}", r.getId(), root);
    return root;
  }

  private List<KnowledgeEntity> findRoot(final String rootVariantId, final Knowledge knowledge) {
    final List<KnowledgeEntity> result = new ArrayList<KnowledgeEntity>();
    final List<KnowledgeEntity> entities = knowledge.getEntities();
    findRoot(result, rootVariantId, entities);
    return result;
  }

  private void findRoot(final List<KnowledgeEntity> result, final String rootVariantId, final List<KnowledgeEntity> entities) {
    for (final KnowledgeEntity ke : entities) {
      if (rootVariantId.equals(ke.getVariant().getId())) {
        result.add(ke);
      }
      findRoot(result, rootVariantId, ke.getSiblings());
    }
  }
}
