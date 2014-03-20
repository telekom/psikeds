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
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;

/**
 * This Resolver will evaluate the possible Events und apply all Rules
 * that were triggered. Decission will be ignored, Metadata is optional
 * and will be used for Information about Changes to the State of Events
 * and Rules, if present.
 * 
 * WARNING: This Resolver is currently NOT WORKING!!!
 * 
 * @author marco@juliano.de
 */
public class RulesEvaluator implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesEvaluator.class);

  public static final Transformer DEFAULT_TRANSFORMER = new Vo2PojoTransformer();
  public static final boolean DEFAULT_CREATE_CONCLUSION_PATH = true;
  public static final boolean DEFAULT_CREATE_NON_CHOOSABLE_ENTITIES = false;
  public static final boolean DEFAULT_KEEP_MODUS_TOLLENS_FOR_LATER = true;
  public static final boolean DEFAULT_REPEAT_AFTER_RULES_APPLIED = false;

  private KnowledgeBase kb;
  private Transformer trans;
  private boolean autoCreateConclusionPath;
  private boolean createNonChoosableEntities;
  private boolean keepModusTollensForLater;
  private boolean repeatAfterRulesApplied;

  public RulesEvaluator() {
    this(null);
  }

  public RulesEvaluator(final KnowledgeBase kb) {
    this(kb, DEFAULT_TRANSFORMER);
  }

  public RulesEvaluator(final KnowledgeBase kb, final Transformer trans) {
    this(kb, trans, DEFAULT_CREATE_CONCLUSION_PATH, DEFAULT_CREATE_NON_CHOOSABLE_ENTITIES, DEFAULT_KEEP_MODUS_TOLLENS_FOR_LATER, DEFAULT_REPEAT_AFTER_RULES_APPLIED);
  }

  public RulesEvaluator(final KnowledgeBase kb, final Transformer trans, final boolean createConclusion, final boolean createNonChoosable, final boolean keepForLater, final boolean repeatAfterRules) {
    this.kb = kb;
    this.trans = trans;
    this.autoCreateConclusionPath = createConclusion;
    this.createNonChoosableEntities = createNonChoosable;
    this.keepModusTollensForLater = keepForLater;
    this.repeatAfterRulesApplied = repeatAfterRules;
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
    if (LOGGER.isInfoEnabled()) {
      final StringBuilder sb = new StringBuilder("Config: Auto-Creation of Conclusion-Path: {}\n");
      sb.append("Creation of not choosable Knowledge-Entities: {}\n");
      sb.append("Keeping Modus Tollens for later: {}\n");
      sb.append("Repeat after Rules were applied: {}");
      LOGGER.info(sb.toString(), this.autoCreateConclusionPath, this.createNonChoosableEntities, this.keepModusTollensForLater, this.repeatAfterRulesApplied);
    }
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.trans, "No Transformer!");
  }

  // ----------------------------------------------------------------

  /**
   * @param knowledge
   *          current Knowledge
   * @param decission
   *          Decission (ignored!)
   * @param raeh
   *          all Rules and Events currently relevant (mandatory!)
   * @param metadata
   *          Metadata (optional, can be null)
   * @return Knowledge
   *         resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final RulesAndEventsHandler raeh, final Metadata metadata) throws ResolutionException {
    final boolean ok = false;
    final boolean stable = true;
    try {
      LOGGER.debug("Evaluating Events and Rules ...");
      if ((knowledge == null) || (raeh == null)) {
        final String errmsg = "Knowledge or RulesAndEventsHandler missing!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }

      // TODO: enable when fixed!!!
      LOGGER.warn("DISABLED!!!");
      return knowledge;

//      // knowledge is clean so far
//      raeh.setKnowledgeDirty(false);
//      // Part 1: Check Events, i.e. check whether an Event is still relevant, triggered or already obsolete
//      checkAllRelevantEvents(knowledge, raeh, metadata);
//      // Part 2: Check Rules, i.e. apply or expire Rules depending on their Context, Premise or Conclusion
//      stable = checkRules(knowledge, raeh, metadata);
//      if (!stable) {
//        // A rule was applied and it is neccessary to execute the full Resolver-Chain once again
//        LOGGER.debug("Knowledge is not stable, need another Iteration of all Resolvers!");
//        knowledge.setStable(false);
//      }
//      ok = true;
//      if (this.repeatAfterRulesApplied && raeh.isKnowledgeDirty()) {
//        // A rule was applied and we need to check Events and apply remaining Rules once again!
//        LOGGER.debug("Rule applied, repeating checks of Events and Rules.");
//        return resolve(knowledge, decission, raeh, metadata);
//      }
//      // done
//      return knowledge;
    }
    finally {
      RulesAndEventsHandler.logContents(raeh);
      LOGGER.debug("... finished evaluating Events and Rules, Result is " + (stable ? "" : "NOT ") + "stable and " + (ok ? "OK." : "with ERROR(S)!"));
    }
  }

  // ------------------------------------------------------

  // We only check Events that are still relevant, i.e. obsolete Events or
  // Events that were already triggered will be ignored.
  // Also note, that all check...() Methods are fail fast, i.e. as soon as
  // an Event is not possible any more, the Check will abort.
  // ==> Performance

  private void checkAllRelevantEvents(final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    try {
      LOGGER.trace("--> checkAllRelevantEvents()");
      final List<Event> events = raeh.getRelevantEvents();
      for (final Event e : events) {
        // Check every possible Event
        checkEvent(e, knowledge, raeh, metadata);
      }
    }
    finally {
      LOGGER.trace("<-- checkAllRelevantEvents()");
    }
  }

  private boolean checkEvent(final Event e, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    try {
      LOGGER.trace("--> checkEvent(); Event = {}", e);
      final List<String> ctx = (e == null ? null : e.getContext());
      if ((ctx == null) || ctx.isEmpty()) {
        final String errmsg = "Event or Context missing!";
        LOGGER.warn(errmsg);
        // markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(errmsg);
      }
      // TODO: check type of event and get trigger
      final String triggerId = null;
      if (StringUtils.isEmpty(triggerId)) {
        final String errmsg = "Trigger missing!";
        LOGGER.warn(errmsg);
        // markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(errmsg);
      }
      // Part 1 - Step 1:
      // Check whether we find the Root-Variant of the Event within our current Knowledge
      final List<KnowledgeEntity> root = findRoot(e, knowledge);
      if ((root == null) || root.isEmpty()) {
        LOGGER.debug("Nothing to do. Event {} is attached to Variant {} which is not (yet) included in the current Knowledge.", e.getEventID(), e.getVariantID());
        stillPossible = true;
        return stillPossible;
      }
      // Part 1 - Step 2:
      // Analyze the Context Path (including Trigger!) of the Event starting from
      // the Root(s). This reduces overhead, because we do not need to traverse
      // the full graph.
      ctx.add(triggerId);
      // TODO: check trigger separately to support not-events!
      stillPossible = checkListOfEntities(e, ctx, root, true, raeh, metadata);
      if (!stillPossible && raeh.isRelevant(e)) {
        // Whether an Event is obsolete can only be decided on the highest Level,
        // i.e. when none of the Knowledge-Entities and its Child-Entities signaled
        // that this Event could still be possible!
        LOGGER.debug("Event is not possible any more: {}", e.getEventID());
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
      LOGGER.trace("--> checkListOfEntities(); E = {}, Path = {}", e.getEventID(), path);
      // Check each Knowledge Entity
      for (final KnowledgeEntity ke : entities) {
        if (!raeh.isRelevant(e)) {
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
      LOGGER.trace("<-- checkListOfEntities(); E = {}, Path = {}; Still possible = {}", e.getEventID(), path, stillPossible);
    }
  }

  private boolean checkKnowledgeEntity(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    boolean matching = false;
    try {
      LOGGER.trace("--> checkKnowledgeEntity(); E = {}; Path = {}\nKE = {}", e.getEventID(), path, ke);
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
        // TODO handle possible Features!
        stillPossible = checkChoices(e, currentPathElement, ke.getPossibleVariants(), isVariant, raeh, metadata);
        if (stillPossible) {
          if ((len == 1) && !isVariant) {
            // Match: This Event is triggered by a matching Feature (or Purpose).
            LOGGER.debug("Path {} of Event {} ends with Feature or Purpose available as a Choice of this KE --> Trigger!", path, e.getEventID());
            triggerEvent(e, raeh, metadata);
            matching = true;
            stillPossible = false;
            return stillPossible;
          }
          else {
            LOGGER.debug("Current PE {} of Event {} is matching to Choice of this KE.", currentPathElement, e.getEventID());
          }
        }
        else {
          LOGGER.debug("Event {} is matching neither KE nor any Choice.", e.getEventID());
        }
      }
      else { // matching
        // Current Element was matching, Event is still possible ...
        if (len == 1) {
          // ... and this was the last Element. Gotcha!
          LOGGER.debug("Path {} of Event {} ends at this KE --> Trigger!", path, e.getEventID());
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
              // Match: This Event is triggered by a matching Feature (or Purpose).
              // Note: We checked the next PE, so current PE must be a Variant!
              LOGGER.debug("Path {} of Event {} ends with Feature or Purpose available as a Choice of this KE --> Trigger!", path, e.getEventID());
              triggerEvent(e, raeh, metadata);
              matching = true;
              stillPossible = false;
              return stillPossible;
            }
            else {
              // Path > 2 or Events is not ending with a Purpose
              // --> Event is still possible but not matching/triggered.
              LOGGER.debug("Next PE of Event {} is also matching to Choice of this KE --> not matching but still possible.", e.getEventID());
              matching = false;
            }
          }
          else {
            // So we must dig deeper into this part of the Knowledge-Graph.
            // Let's see whether next part is matching to this KE, too.
            stillPossible = checkKnowledgeEntity(e, path.subList(1, len), ke, !isVariant, raeh, metadata);
            if (stillPossible) {
              if (len > 2) {
                LOGGER.debug("Next PE of Event {} is also matching to this KE --> Recursion!", e.getEventID());
                matching = false;
                stillPossible = checkListOfEntities(e, path.subList(2, len), ke.getChildren(), isVariant, raeh, metadata);
              }
              else {
                LOGGER.debug("Path {} of Event {} ends at this KE --> Trigger!", path, e.getEventID());
                triggerEvent(e, raeh, metadata);
                matching = true;
                stillPossible = false;
                return stillPossible;
              }
            }
            else {
              // Let's do some Recursion ;-)
              LOGGER.debug("PE of Event {} matching to this KE --> Recursion!", e.getEventID());
              matching = false;
              stillPossible = checkListOfEntities(e, path.subList(1, len), ke.getChildren(), !isVariant, raeh, metadata);
            }
          }
        }
      }
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkKnowledgeEntity(); E = {}; Path = {}; matching = {}; stillPossible = {}", e.getEventID(), path, matching, stillPossible);
    }
  }

  private boolean checkCurrentPathElement(final Event e, final String currentPathElement, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matching = false;
    try {
      LOGGER.trace("--> checkCurrentPathElement(); E = {}; PE = {}; isVariant = {}", e.getEventID(), currentPathElement, isVariant);
      if (StringUtils.isEmpty(currentPathElement)) {
        final String errmsg = "Current Path Element is empty!";
        LOGGER.warn(errmsg);
        markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(errmsg);
      }
      // we are expecting a variant
      if (isVariant) {
        LOGGER.trace("Checking whether {} is matching to Variant.", currentPathElement);
        final Variant v = ke == null ? null : ke.getVariant();
        final String vid = v == null ? null : v.getVariantID();
        if (currentPathElement.equals(vid)) {
          LOGGER.debug("Current PE is matching to Variant-ID: {}", vid);
          matching = true;
        }
      }
      // we are expecting either a purpose or a feature
      else {
        // check purpose first ... it's more probable
        LOGGER.trace("Checking whether {} is matching to Purpose.", currentPathElement);
        final Purpose p = ke == null ? null : ke.getPurpose();
        final String pid = p == null ? null : p.getPurposeID();
        if (currentPathElement.equals(pid)) {
          LOGGER.debug("Current PE is matching to Purpose-ID: {}", pid);
          matching = true;
        }
        // if this is a feature-event also check for feature
//        else if (e.isFeatureEvent()) {
        LOGGER.trace("Checking whether {} is matching to Feature of this KE.", currentPathElement);
        final FeatureValues feats = ke.getFeatures();
        for (final FeatureValue fv : feats) {
          final String fid = fv == null ? null : fv.getFeatureID();
          if (currentPathElement.equals(fid)) {
            LOGGER.debug("Current PE is matching to Feature-ID: {}", fid);
            matching = true;
            break;
          }
        }
//        }
      }
      return matching;
    }
    finally {
      LOGGER.trace("<-- checkCurrentPathElement(); E = {}; PE = {}; isVariant = {}; matching = {}", e.getEventID(), currentPathElement, isVariant, matching);
    }
  }

  // ------------------------------------------------------

  private boolean checkChoices(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    final String currentPathElement = path.get(0);
    final VariantChoices choices = ke.getPossibleVariants();
    return checkChoices(e, currentPathElement, choices, isVariant, raeh, metadata);
  }

  // TODO handle VariantChoices and FeatureChoices correctly

  private boolean checkChoices(final Event e, final String currentPathElement, final VariantChoices choices, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matchingChoice = false;
    try {
      LOGGER.trace("--> checkChoices(); E = {}; PE = {}; isVariant = {}", e.getEventID(), currentPathElement, isVariant);
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
      LOGGER.trace("<-- checkChoices(); E = {}; PE = {}; matchingChoice = {}", e.getEventID(), currentPathElement, matchingChoice);
    }
  }

  private boolean checkChoice(final Event e, final String currentPathElement, final Choice c, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matchingChoice = false;
    try {
      LOGGER.trace("--> checkChoice(); PE = {}; isVariant = {}\nChoice = {}", currentPathElement, isVariant, c);
      if (c instanceof VariantChoice) {
        final VariantChoice vc = (VariantChoice) c;
        if (isVariant) {
          // Is Path Element matching any selectable Variant of this Choice?
          final List<Variant> lst = vc.getVariants();
          if (lst != null) {
            for (final Variant v : lst) {
              final String vid = (v == null ? null : v.getVariantID());
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
          final Purpose p = vc.getPurpose();
          final String pid = (p == null ? null : p.getPurposeID());
          matchingChoice = currentPathElement.equals(pid);
          if (matchingChoice) {
            LOGGER.debug("PE {} is matching Purpose of Choice {}", currentPathElement, c);
            return matchingChoice;
          }
        }
        return matchingChoice;
      }
      if (c instanceof FeatureChoice) {
        if (isVariant) {
          // A Variant is expected next, therefore Path Element cannot point to a Feature
          LOGGER.trace("Variant expected, skipping check for Feature.");
        }
        else {
          // Is Path Element matching Feature of this Choice?
          final FeatureChoice fc = (FeatureChoice) c;
          final String fid = fc.getFeatureID();
          matchingChoice = currentPathElement.equals(fid);
          if (matchingChoice && LOGGER.isDebugEnabled()) {
            LOGGER.debug("PE {} is matching Feature of Choice {}", currentPathElement, c);
          }
        }
        return matchingChoice;
      }
      // if we reach this point something was really wrong!
      final String errmsg = "Unexpected type of Choice: " + String.valueOf(c);
      LOGGER.warn(errmsg);
      throw new ResolutionException(errmsg);
    }
    finally {
      LOGGER.trace("<-- checkChoice(); PE = {}; isVariant = {}; matchingChoice = {}", currentPathElement, isVariant, matchingChoice);
    }
  }

  // ------------------------------------------------------

  private void triggerEvent(final Event e, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if ((e != null) && raeh.isRelevant(e)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("TRIGGERED: {}", e);
      }
      else {
        LOGGER.debug("TRIGGERED: {}", e.getEventID());
      }
      raeh.setTriggered(e);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "E_" + e.getEventID() + "_triggered";
        final String msg = String.valueOf(e);
        metadata.addInfo(key, msg);
      }
    }
  }

  private void markEventObsolete(final Event e, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if ((e != null) && raeh.isRelevant(e)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("OBSOLETE: {}", e);
      }
      else {
        LOGGER.debug("OBSOLETE: {}", e.getEventID());
      }
      raeh.setObsolete(e);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "E_" + e.getEventID() + "_obsolete";
        final String msg = String.valueOf(e);
        metadata.addInfo(key, msg);
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
        LOGGER.debug("TRIGGERED: {}", r.getRuleID());
      }
      raeh.setTriggered(r);
      raeh.setKnowledgeDirty(true);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRuleID() + suffix;
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }

  private void markRuleObsolete(final Rule r, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("OBSOLETE: {}", r);
      }
      else {
        LOGGER.debug("OBSOLETE: {}", r.getRuleID());
      }
      raeh.setObsolete(r);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRuleID() + "_obsolete";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }

  // ------------------------------------------------------

  private boolean checkRules(final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRules()");
      final List<Rule> rules = raeh.getRelevantRules();
      for (final Rule r : rules) {
        final String ruleId = (r == null ? null : r.getRuleID());
        if (!StringUtils.isEmpty(ruleId)) {
          LOGGER.trace("Checking Rule: {}", r);

          // Part 2 - Step A: For each Rule check State of Premise- and Conclusion-Event
          // premise
          final String peid = r.getPremiseEventID();
          final boolean premiseObsolete = raeh.isObsolete(peid);
          final boolean premiseTriggered = raeh.isTriggered(peid);
          // conclusion
          final String ceid = r.getConclusionEventID();
          final boolean conclusionObsolete = raeh.isObsolete(ceid);
          final boolean conclusionTriggered = raeh.isTriggered(ceid);

          // Part 2 - Step B: Analyze Rule depending on State of Events
          if (premiseObsolete) {
            LOGGER.debug("Premise {} obsolete/impossible --> mark also Rule {} as obsolete.", peid, ruleId);
            markRuleObsolete(r, raeh, metadata);
          }
          else if (conclusionTriggered) {
            LOGGER.debug("Conclusion {} already triggered/selected --> mark also Rule {} as triggered.", ceid, ruleId);
            triggerRule(r, raeh, metadata);
          }
          else if (premiseTriggered) {
            LOGGER.debug("Premise {} triggered/selected --> MP: fire Rule {}.", peid, ruleId);
            applyRuleModusPonens(r, knowledge, raeh, metadata);
            stable = false;
          }
          else if (conclusionObsolete) {
            LOGGER.debug("Conclusion {} obsolete/impossible --> MT: fire Rule {}.", ceid, ruleId);
            applyRuleModusTollens(r, knowledge, raeh, metadata);
            stable = false;
          }
          else if (LOGGER.isTraceEnabled()) {
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

  // TODO: extend feature events to feature values and create feature with corresponding value!

  private boolean applyConclusionEvent(final List<KnowledgeEntity> root, final String eventId) {
    boolean applied = false;
    if ((root != null) && !root.isEmpty()) {
      LOGGER.trace("Triggering Event: {}", eventId);
      final Event e = this.kb.getEvent(eventId);
      if ((e != null) && (this.autoCreateConclusionPath /* || !e.isFeatureEvent() */)) {
        final List<String> ctx = (e == null ? null : e.getContext());
        if (((ctx != null) && !ctx.isEmpty())) {
          // TODO check type of event and get trigger
          final String trigger = null;
          ctx.add(trigger);
          for (final KnowledgeEntity ke : root) {
            final boolean created = createPathEntries(ke, ctx);
            applied = applied || created;
          }
        }
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
      for (final KnowledgeEntity ke : currentKE.getChildren()) {
        if (ke != null) {
          final Purpose p = ke.getPurpose();
          final Variant v = ke.getVariant();
          if ((p != null) && (v != null)) {
            final String pid = p.getPurposeID();
            final String vid = v.getVariantID();
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
            final String errmsg = "Auto-Creation disabled but intermediate Path-Elements of Conclusion-Event missing! Check your Config and Premises in KB!";
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
    final VariantChoices choices = ke.getPossibleVariants();
    final Iterator<VariantChoice> citer = choices.iterator();
    // TODO check VariantChoices and FeatureChoices separately
    while (citer.hasNext()) {
      final Choice c = citer.next();
      if (c instanceof VariantChoice) {
        final VariantChoice vc = (VariantChoice) c;
        final Purpose p = vc.getPurpose();
        if ((p != null) && purposeId.equals(p.getPurposeID())) {
          if (StringUtils.isEmpty(variantId)) {
            LOGGER.debug("Removing all Choices for Purpose {}", purposeId);
            citer.remove();
          }
          else {
            final List<Variant> variants = vc.getVariants();
            final Iterator<Variant> viter = variants.iterator();
            while (viter.hasNext()) {
              final Variant v = viter.next();
              if ((v != null) && variantId.equals(v.getVariantID())) {
                LOGGER.debug("Removing Variant {} from Choices for Purpose {}", variantId, purposeId);
                viter.remove();
              }
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
        long qty = 1;
        // TODO check VariantChoices and FeatureChoices separately
        for (final Choice c : parent.getPossibleVariants()) {
          if (c instanceof VariantChoice) {
            final VariantChoice vc = (VariantChoice) c;
            qty = vc.getQuantity();
            final Purpose p = vc.getPurpose();
            if ((p != null) && purposeId.equals(p.getPurposeID())) {
              // matching purpose found
              expectedPurp = p;
              for (final Variant v : vc.getVariants()) {
                if ((v != null) && variantId.equals(v.getVariantID())) {
                  // matching variant found
                  expectedVar = v;
                }
              }
            }
          }
        }
        if ((expectedPurp != null) && (expectedVar != null)) {
          // perfect, the KE we want to create is really a choosable Combination of P and V!
          LOGGER.debug("Found expected Choice for {} and {}", purposeId, variantId);
          nextKE = regularlyCreateNewEntity(parent, expectedPurp, expectedVar, qty);
        }
        else {
          // hmmm ... the KE we want to create is not a choosable Combination of P and V?!?!
          LOGGER.debug("Expected Choice for {} and {} not found!", purposeId, variantId);
          if (this.createNonChoosableEntities) {
            // ok, this is actually not choosable at the moment, but config says, we do it nevertheless
            nextKE = forceCreateNextEntity(parent, purposeId, variantId, qty);
          }
          else {
            // Definition of Rules and Events in KB seems to be strange
            final StringBuilder sb = new StringBuilder();
            sb.append("Not a choosable Combination! Cannot create new KE with P = ");
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

  private KnowledgeEntity regularlyCreateNewEntity(final KnowledgeEntity parent, final Purpose purp, Variant var, final long qty) {
    // ensure clean data, therefore lookup variant from knowledge base (again)
    final org.psikeds.resolutionengine.datalayer.vo.Variant variant = this.kb.getVariant(var.getVariantID());
    var = this.trans.valueObject2Pojo(variant); // TODO: get all features of variant! 
    // get next choices for new KE
    final VariantChoices newVariantChoices = getNewVariantChoices(variant);
    final FeatureChoices newFeatureChoices = getNewFeatureChoices(variant);

    // create new KE including choices
    final KnowledgeEntity nextKE = new KnowledgeEntity(qty, purp, var, newVariantChoices, newFeatureChoices);
    // create new KE including choices
    // attach new KE to parent KE
    addNewKnowledgeEntity(parent, nextKE);
    return nextKE;
  }

  // TODO rethink creation of entities! does this make sense?

  private KnowledgeEntity forceCreateNextEntity(final KnowledgeEntity parent, final String purposeId, final String variantId, final long qty) {
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
        sb.append("Unknow IDs! Cannot force Creation of new KE with P = ");
        sb.append(purposeId);
        sb.append(" and V = ");
        sb.append(variantId);
        sb.append(" for Parent ");
        sb.append(parent);
        final String errmsg = sb.toString();
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
      return regularlyCreateNewEntity(parent, p, v, qty);
    }
    finally {
      LOGGER.trace("<-- forceCreateNextEntity(); P = {}, V = {}", purposeId, variantId);
    }
  }

  private VariantChoices getNewVariantChoices(final org.psikeds.resolutionengine.datalayer.vo.Variant parentVariant) {
    final VariantChoices choices = new VariantChoices();
    final String parentVariantID = (parentVariant == null ? null : parentVariant.getVariantID());
    try {
      LOGGER.trace("--> getNewVariantChoices(); Variant = {}", parentVariantID);
      // get all purposes constituting parent-variant ...
      final org.psikeds.resolutionengine.datalayer.vo.Purposes newpurps = this.kb.getConstitutingPurposes(parentVariantID);
      // ... and create for every purpose ...
      for (final org.psikeds.resolutionengine.datalayer.vo.Purpose p : newpurps.getPurpose()) {
        // ... a new VariantChoice-POJO for the Client
        final org.psikeds.resolutionengine.datalayer.vo.Fulfills ff = this.kb.getFulfills(p.getPurposeID());
        final long qty = ff.getQuantity();
        final List<Variant> variants = new ArrayList<Variant>();
        for (final String vid : ff.getVariantID()) {
          final org.psikeds.resolutionengine.datalayer.vo.Variant v = this.kb.getVariant(vid);
          final org.psikeds.resolutionengine.datalayer.vo.Features feats = this.kb.getFeatures(vid);
          variants.add(this.trans.valueObject2Pojo(v, feats));
        }
        final VariantChoice vc = this.trans.valueObject2Pojo(parentVariantID, p, variants, qty);
        LOGGER.trace("Adding new Variant-Choice: {}", vc);
        choices.add(vc);
      }
      // return list of all new choices
      return choices;
    }
    finally {
      LOGGER.trace("<-- getNewVariantChoices(); Variant = {}\nChoices = {}", parentVariantID, choices);
    }
  }

  private FeatureChoices getNewFeatureChoices(final org.psikeds.resolutionengine.datalayer.vo.Variant v) {
    final FeatureChoices choices = new FeatureChoices();
    final String variantId = (v == null ? null : v.getVariantID());
    try {
      LOGGER.trace("--> getNewFeatureChoices(); Variant = {}", v);
      // get all features of this variant ...
      final org.psikeds.resolutionengine.datalayer.vo.Features feats = this.kb.getFeatures(variantId);
      // ... and create for every feature ...
      for (final org.psikeds.resolutionengine.datalayer.vo.Feature<?> f : feats.getFeature()) {
        // ... a new FeatureChoice-POJO for the Client
        final FeatureChoice fc = this.trans.valueObject2Pojo(variantId, f);
        LOGGER.trace("Adding new Feature-Choice: {}", fc);
        choices.add(fc);
      }
      // return list of all new choices
      return choices;
    }
    finally {
      LOGGER.trace("<-- getNewFeatureChoices(); Variant = {}\nChoices = {}", variantId, choices);
    }
  }

  private void addNewKnowledgeEntity(final KnowledgeEntity parent, final KnowledgeEntity newke) {
    final KnowledgeEntities entities = (parent == null ? null : parent.getChildren());
    addNewKnowledgeEntity(entities, newke);
  }

  private void addNewKnowledgeEntity(final KnowledgeEntities entities, final KnowledgeEntity newke) {
    if ((entities != null) && (newke != null)) {
      for (final KnowledgeEntity ke : entities) {
        final String pid1 = ke.getPurpose().getPurposeID();
        final String pid2 = newke.getPurpose().getPurposeID();
        final String vid1 = ke.getVariant().getVariantID();
        final String vid2 = newke.getVariant().getVariantID();
        if (pid1.equals(pid2) && vid1.equals(vid2)) {
          LOGGER.debug("addNewKnowledgeEntity(): Entity-List already contains KnowledgeEntity: {}", newke);
          return;
        }
      }
      LOGGER.trace("addNewKnowledgeEntity(): Adding new KnowledgeEntity: {}", newke);
      entities.add(newke);
    }
  }

  // ------------------------------------------------------

  // Part 2 - Step D: Modus Tollens: disable Trigger
  private void applyRuleModusTollens(final Rule r, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    LOGGER.info("MODUS TOLLENS - disabling Trigger-Event of Rule: {}", r);
    final List<KnowledgeEntity> root = findRoot(r, knowledge);
    final String eventId = r.getPremiseEventID();
    if (disablePremiseEvent(root, eventId)) {
      triggerRule(r, raeh, metadata, "_modus_tollens");
    }
  }

  private boolean disablePremiseEvent(final List<KnowledgeEntity> root, final String eventId) {
    boolean disabled = false;
    if ((root != null) && !root.isEmpty()) {
      LOGGER.trace("Disabling Event: {}", eventId);
      final Event e = this.kb.getEvent(eventId);
      final List<String> ctx = (e == null ? null : e.getContext());
      if ((ctx != null) && !ctx.isEmpty()) {
        // TODO: check kind of event and get trigger
        final String trigger = null;
        ctx.add(trigger);
        for (final KnowledgeEntity ke : root) {
          final boolean removed = removePathFromChoices(ke, ctx);
          disabled = disabled || removed;
        }
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
      final String currentVariantId = (currentVariant == null ? null : currentVariant.getVariantID());
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
      for (final KnowledgeEntity ke : currentKE.getChildren()) {
        if (ke != null) {
          final Purpose p = ke.getPurpose();
          final Variant v = ke.getVariant();
          if ((p != null) && (v != null)) {
            final String pid = p.getPurposeID();
            final String vid = v.getVariantID();
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

  //TODO: performance optimization: cache pointers to root of variant in raeh, search only once!

  private List<KnowledgeEntity> findRoot(final Event e, final Knowledge knowledge) {
    final String rootVariantId = e.getVariantID();
    final List<KnowledgeEntity> root = findRoot(rootVariantId, knowledge);
    LOGGER.trace("Root of Event {} is: {}", e.getEventID(), root);
    return root;
  }

  private List<KnowledgeEntity> findRoot(final Rule r, final Knowledge knowledge) {
    final String rootVariantId = r.getVariantID();
    final List<KnowledgeEntity> root = findRoot(rootVariantId, knowledge);
    LOGGER.trace("Root of Rule {} is: {}", r.getRuleID(), root);
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
      if (rootVariantId.equals(ke.getVariant().getVariantID())) {
        result.add(ke);
      }
      findRoot(result, rootVariantId, ke.getChildren());
    }
  }

  // TODO: handling of choices is not correct!

  // TODO: handling of triggers is not correct!

  // TODO: refactoring: this class is too large and too complex; extract some functionality to separate classes; split events and rules
}
