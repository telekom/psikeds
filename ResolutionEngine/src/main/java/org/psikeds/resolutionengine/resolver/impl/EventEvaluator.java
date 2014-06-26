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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
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
import org.psikeds.resolutionengine.util.ConceptHelper;
import org.psikeds.resolutionengine.util.FeatureValueHelper;
import org.psikeds.resolutionengine.util.KnowledgeEntityHelper;
import org.psikeds.resolutionengine.util.KnowledgeHelper;

/**
 * This Resolver will evaluate all possible Events and check whether one
 * of them was triggered or is meanwhile obsolete.
 * 
 * Decission will be ignored, Metadata is optional and will be used for
 * Information about Changes to the State of Events.
 * 
 * @author marco@juliano.de
 * 
 */
public class EventEvaluator implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventEvaluator.class);

  public static final Transformer DEFAULT_TRANSFORMER = new Vo2PojoTransformer();
  public static final boolean DEFAULT_UPDATE_CONDITIONAL_RELATIONS = true;

  private KnowledgeBase kb;
  private Transformer trans;
  private boolean updateConditionalRelations;

  public EventEvaluator() {
    this(null);
  }

  public EventEvaluator(final KnowledgeBase kb) {
    this(kb, DEFAULT_TRANSFORMER, DEFAULT_UPDATE_CONDITIONAL_RELATIONS);
  }

  public EventEvaluator(final KnowledgeBase kb, final Transformer trans, final boolean updateConditionalRelations) {
    this.kb = kb;
    this.trans = trans;
    this.updateConditionalRelations = updateConditionalRelations;
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

  public boolean isUpdateConditionalRelations() {
    return this.updateConditionalRelations;
  }

  public void setUpdateConditionalRelations(final boolean updateConditionalRelations) {
    this.updateConditionalRelations = updateConditionalRelations;
  }

  // ----------------------------------------------------------------

  /**
   * Check that Event-Evaluator was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    LOGGER.info("Config: Update conditional Relations = {}", this.updateConditionalRelations);
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
    boolean ok = false;
    try {
      LOGGER.debug("Evaluating Events ...");
      if ((knowledge == null) || (raeh == null)) {
        throw new ResolutionException("Knowledge or RulesAndEventsHandler missing!");
      }
      for (final Event e : raeh.getRelevantEvents()) {
        // Check every relevant Event, i.e. neither obsolete nor triggered yet
        checkEvent(e, knowledge, raeh, metadata);
      }
      if (this.updateConditionalRelations) {
        LOGGER.debug("Updating all conditional Relations according to latest Events.");
        raeh.updateAllConditionalRelations();
      }
      // done
      ok = true;
      return knowledge;
    }
    catch (final ResolutionException re) {
      LOGGER.warn("Could not evaluate Events: " + re.getMessage(), re);
      throw re;
    }
    catch (final Exception ex) {
      LOGGER.warn("Unexpected Error while evaluating Events: " + ex.getMessage(), ex);
      throw new ResolutionException("Could not evaluate Events", ex);
    }
    finally {
      RulesAndEventsHandler.logContents(raeh);
      if (ok) {
        LOGGER.debug("... successfully finished evaluating Events.");
      }
      else {
        LOGGER.debug("... finished evaluating Events with ERROR(S)!!!");
      }
    }
  }

  // ----------------------------------------------------------------

  private boolean checkEvent(final Event e, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    final String eid = (e == null ? null : e.getEventID());
    try {
      LOGGER.trace("--> checkEvent(); E = {}", eid);
      final List<String> ctx = (e == null ? null : e.getContext());
      if (StringUtils.isEmpty(eid) || (ctx == null) || ctx.isEmpty()) {
        final String msg = "Invalid Event: " + eid;
        LOGGER.debug(msg);
        markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(msg);
      }
      if (StringUtils.isEmpty(e.getTriggerID()) || StringUtils.isEmpty(e.getTriggerType())) {
        final String msg = "No Trigger in Event: " + eid;
        LOGGER.debug(msg);
        markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(msg);
      }
      final KnowledgeEntities root = KnowledgeHelper.findRoot(e, knowledge);
      if ((root == null) || root.isEmpty()) {
        LOGGER.debug("Nothing to do. Nexus {} of Event {} is not included in the current Knowledge.", e.getVariantID(), eid);
        stillPossible = true;
        return stillPossible;
      }
      LOGGER.debug("Nexus {} of Event {} is: {}", e.getVariantID(), eid, shortDisplayKE(root));
      stillPossible = checkKnowledgeEntities(e, ctx, root, true, raeh, metadata);
      if (raeh.isRelevant(e)) {
        if (!stillPossible) {
          // Whether an Event is obsolete can only be decided on the highest Level,
          // i.e. when none of the Knowledge-Entities and its Child-Entities signaled
          // that this Event could still be possible!
          LOGGER.debug("Event {} is obsolete.", eid);
          markEventObsolete(e, raeh, metadata);
        }
        else {
          LOGGER.debug("Event {} is still possible.", eid);
        }
      }
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkEvent(); E = {}; Still possible = {}", eid, stillPossible);
    }
  }

  private boolean checkKnowledgeEntities(final Event e, final List<String> path, final KnowledgeEntities entities, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    final String eid = (e == null ? null : e.getEventID());
    try {
      LOGGER.trace("--> checkKnowledgeEntities(); E = {}; Entities = {}", eid, shortDisplayKE(entities));
      for (final KnowledgeEntity ke : entities) {
        if (!raeh.isRelevant(e)) {
          // Fail fast: Event was already triggered or made obsolete
          stillPossible = false;
          return stillPossible;
        }
        if (ke != null) {
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
      }
      // Nothing special happend, but Event is not possible in this Part
      // of the Knowledge-Graph any more
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkKnowledgeEntities(); E = {}; Entities = {}; Still possible = {}", eid, shortDisplayKE(entities), stillPossible);
    }
  }

  private boolean checkKnowledgeEntity(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stillPossible = false;
    boolean matching = false;
    final String eid = (e == null ? null : e.getEventID());
    try {
      LOGGER.trace("--> checkKnowledgeEntity(); E = {}; KE = {}; Path = {}", eid, shortDisplayKE(ke), path);
      final int len = (path == null ? 0 : path.size());
      if (len <= 0) {
        // we walked the complete path
        matching = checkTrigger(e, ke, isVariant, raeh, metadata);
        stillPossible = false;
        return stillPossible;
      }
      // Check the current ID in our Context Path
      final String currentPathElement = path.get(0);
      matching = checkCurrentPathElement(e, currentPathElement, ke, isVariant, raeh, metadata);
      if (!matching) {
        // Current Path Element was not matching, but
        // let's see whether there is a matching Choice
        stillPossible = checkChoices(e, currentPathElement, ke.getPossibleVariants(), isVariant, raeh, metadata);
        if (stillPossible) {
          LOGGER.debug("Path {} of Event {} is matching to Choice of this KE: {}", path, eid, shortDisplayKE(ke));
        }
        else {
          LOGGER.debug("Path {} of Event {} is matching neither KE nor any Choice: {}", path, eid, shortDisplayKE(ke));
        }
        return stillPossible;
      }
      // Current Element was matching, Event is still possible ...  
      if (len == 1) {
        // ... and this was the last Element.
        LOGGER.debug("Path {} of Event {} ends at this KE: {}", path, eid, shortDisplayKE(ke));
        matching = checkTrigger(e, ke, isVariant, raeh, metadata);
        stillPossible = false;
        return stillPossible;
      }
      // Length > 1, i.e. Event is still possible but there is still some portion of the
      // Context Path left. Let's see whether next part is matching a Choice, too.
      stillPossible = checkChoices(e, path.subList(1, len), ke, !isVariant, raeh, metadata);
      if (stillPossible) {
        LOGGER.debug("Next PE of Event {} is also matching to Choice of KE {} --> not matching but still possible.", eid, shortDisplayKE(ke));
        matching = false;
        return stillPossible;
      }
      // So the Event is still possible but not matching one of the Choices.
      // Now we must dig deeper into this part of the Knowledge-Graph.
      // Let's see whether next part is matching to this KE, too.
      stillPossible = checkKnowledgeEntity(e, path.subList(1, len), ke, !isVariant, raeh, metadata);
      if (stillPossible) {
        if (len > 2) {
          LOGGER.debug("Next PE of Event {} is also matching to KE {} --> Recursion!", eid, shortDisplayKE(ke));
          matching = false;
          stillPossible = checkKnowledgeEntities(e, path.subList(2, len), ke.getChildren(), isVariant, raeh, metadata);
        }
        else { // len <= 2
          LOGGER.debug("Path {} of Event {} ends at this KE: {}", path, eid, shortDisplayKE(ke));
          matching = checkTrigger(e, ke, !isVariant, raeh, metadata);
          stillPossible = false;
        }
        return stillPossible;
      }
      // Let's do some Recursion ;-)
      LOGGER.debug("PE of Event {} matching to this KE: {} --> Recursion!", eid, shortDisplayKE(ke));
      matching = false;
      stillPossible = checkKnowledgeEntities(e, path.subList(1, len), ke.getChildren(), !isVariant, raeh, metadata);
      return stillPossible;
    }
    finally {
      LOGGER.trace("<-- checkKnowledgeEntity(); E = {}; KE = {}; Path = {}; matching = {}; stillPossible = {}", eid, shortDisplayKE(ke), path, matching, stillPossible);
    }
  }

  private boolean checkCurrentPathElement(final Event e, final String currentPathElement, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matching = false;
    final String eid = (e == null ? null : e.getEventID());
    try {
      LOGGER.trace("--> checkCurrentPathElement(); E = {}; KE = {}; PE = {}; isVariant = {}", eid, shortDisplayKE(ke), currentPathElement, isVariant);
      if (StringUtils.isEmpty(currentPathElement)) {
        final String msg = "Current Path Element is empty!";
        LOGGER.debug(msg);
        markEventObsolete(e, raeh, metadata);
        throw new ResolutionException(msg);
      }
      if (isVariant) {
        // we are expecting a variant
        LOGGER.trace("Checking whether {} is matching to Variant.", currentPathElement);
        final Variant v = (ke == null ? null : ke.getVariant());
        final String vid = (v == null ? null : v.getVariantID());
        if (currentPathElement.equals(vid)) {
          LOGGER.debug("Current PE is matching to Variant-ID: {}", vid);
          matching = true;
        }
      }
      else {
        // we are expecting a purpose
        LOGGER.trace("Checking whether {} is matching to Purpose.", currentPathElement);
        final Purpose p = (ke == null ? null : ke.getPurpose());
        final String pid = (p == null ? null : p.getPurposeID());
        if (currentPathElement.equals(pid)) {
          LOGGER.debug("Current PE is matching to Purpose-ID: {}", pid);
          matching = true;
        }
      }
      return matching;
    }
    finally {
      LOGGER.trace("<-- checkCurrentPathElement(); E = {}; KE = {}; PE = {}; isVariant = {}; matching = {}", eid, shortDisplayKE(ke), currentPathElement, isVariant, matching);
    }
  }

  // ----------------------------------------------------------------

  private boolean checkTrigger(final Event e, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matching = false;
    boolean undecided = true;
    final boolean notEvent = (e == null ? false : e.isNotEvent());
    final String eid = (e == null ? null : e.getEventID());
    final String tid = (e == null ? null : e.getTriggerID());
    final String type = (e == null ? null : e.getTriggerType());
    try {
      LOGGER.trace("--> checkTrigger(); E = {}; KE = {}; TID = {}; TYP = {}; NOT = {}; VAR = {}", eid, shortDisplayKE(ke), tid, type, notEvent, isVariant);
      if (isVariant) {
        // current/last element of context path was a variant, so trigger could be a feature-value or a concept
        if (Event.TRIGGER_TYPE_FEATURE_VALUE.equals(type)) {
          LOGGER.debug("Checking Trigger {} of Feature-Value-Event {}", tid, eid);
          final String fid = tid;
          final String ret = FeatureValueHelper.checkFeatureValue(this.kb, this.trans, ke, fid);
          if (FeatureValueHelper.RET_FV_ASSIGNED.equalsIgnoreCase(ret)) {
            LOGGER.debug("Found matching Feature-Value: {}", fid);
            matching = true;
            undecided = false;
          }
          else if (FeatureValueHelper.RET_FV_NOT_POSSIBLE.equalsIgnoreCase(ret)) {
            LOGGER.debug("Feature-Value {} is not possible any more!", fid);
            matching = false;
            undecided = false;
          }
          else {
            LOGGER.debug("Feature-Value {} is still undecided, i.e. not assigned yet but still possible.", fid);
            matching = false;
            undecided = true;
          }
        }
        else if (Event.TRIGGER_TYPE_CONCEPT.equals(type)) {
          LOGGER.debug("Checking Trigger {} of Concept-Event {}", tid, eid);
          final String cid = tid;
          final String ret = ConceptHelper.checkConcept(this.kb, this.trans, ke, cid);
          if (ConceptHelper.RET_CONCEPT_FULFILLED.equals(ret)) {
            LOGGER.debug("Found matching Concept: {}", cid);
            matching = true;
            undecided = false;
          }
          else if (ConceptHelper.RET_CONCEPT_NOT_POSSIBLE.equals(ret)) {
            LOGGER.debug("Concept {} is not possible any more!", cid);
            matching = false;
            undecided = false;
          }
          else {
            LOGGER.debug("Concept {} is still undecided, i.e. not fulfilled yet but still possible.", cid);
            matching = false;
            undecided = true;
          }
        }
        else {
          LOGGER.debug("Cannot check Trigger ... invalid Event: {}", e);
          matching = false;
          undecided = true;
          throw new ResolutionException("Invalid Event " + eid + " -- Expected a Feature- or Concept-Trigger, but encountered: " + type);
        }
      }
      else {
        // current/last element of context path was a purpose, so only reasonable trigger is a variant
        if (Event.TRIGGER_TYPE_VARIANT.equals(type)) {
          LOGGER.debug("Checking Trigger {} of Variant-Event {}", tid, eid);
          final Variant v = (ke == null ? null : ke.getVariant());
          final String vid = (v == null ? null : v.getVariantID());
          if (tid.equals(vid)) {
            LOGGER.debug("Found matching Variant: {}", vid);
            matching = true;
            undecided = false;
          }
        }
        else {
          LOGGER.debug("Cannot check Trigger ... invalid Event: {}", e);
          matching = false;
          undecided = true;
          throw new ResolutionException("Invalid Event " + eid + " -- Expected a Variant-Trigger, but encountered: " + type);
        }
      }
      if (notEvent) {
        if (matching) {
          matching = false;
          LOGGER.debug("Trigger {} was matching but Event {} is a NOT-Event!", tid, eid);
        }
        else {
          matching = true;
          LOGGER.debug("Trigger {} was NOT matching but Event {} is a NOT-Event!", tid, eid);
        }
      }
      if (!undecided) {
        if (matching) {
          LOGGER.debug("Event {} is not undecided and matching --> Triggering {} ", eid, tid);
          triggerEvent(e, raeh, metadata);
        }
        else {
          LOGGER.debug("Event {} is not undecided and also not matching --> Obsolete!", eid);
          markEventObsolete(e, raeh, metadata);
        }
      }
      else {
        LOGGER.debug("Trigger {} of Event {} is still undecided.", tid, eid);
      }
      return matching;
    }
    finally {
      LOGGER.trace("<-- checkTrigger(); E = {}; KE = {}; TID = {}; TYP = {}; NOT = {}; VAR = {}; matching = {}; undecided = {}", eid, shortDisplayKE(ke), tid, type, notEvent, isVariant, matching,
          undecided);
    }
  }

  // ----------------------------------------------------------------

//  private boolean checkChoices(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
//    final String currentPathElement = path.get(0);
//    if (Event.TRIGGER_TYPE_VARIANT.equals(e.getTriggerType())) {
//      final VariantChoices choices = ke.getPossibleVariants();
//      return checkChoices(e, currentPathElement, choices, isVariant, raeh, metadata);
//    }
//    // features and concepts cannot be part of the context path
//    return false;
//  }

  private boolean checkChoices(final Event e, final List<String> path, final KnowledgeEntity ke, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    return checkChoices(e, path.get(0), ke.getPossibleVariants(), isVariant, raeh, metadata);
  }

  private boolean checkChoices(final Event e, final String currentPathElement, final VariantChoices choices, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matchingChoice = false;
    final String eid = (e == null ? null : e.getEventID());
    try {
      LOGGER.trace("--> checkChoices(); E = {}; PE = {}; isVariant = {}\nChoices = {}", eid, currentPathElement, isVariant, choices);
      for (final VariantChoice vc : choices) {
        // Loop over all Choices and as soon as one is matching we are finished
        matchingChoice = checkChoice(e, currentPathElement, vc, isVariant, raeh, metadata);
        if (matchingChoice) {
          // fail/succeed fast
          return matchingChoice;
        }
      }
      LOGGER.debug("PE {} of Event {} is not matching to any of the Variant-Choices.", currentPathElement, eid);
      return matchingChoice;
    }
    finally {
      LOGGER.trace("<-- checkChoices(); E = {}; PE = {}; isVariant = {}; matchingChoice = {}", eid, currentPathElement, isVariant, matchingChoice);
    }
  }

  private boolean checkChoice(final Event e, final String currentPathElement, final VariantChoice vc, final boolean isVariant, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean matchingChoice = false;
    final String eid = (e == null ? null : e.getEventID());
    try {
      LOGGER.trace("--> checkChoice(); E = {}; PE = {}; isVariant = {}\nVariantChoice = {}", eid, currentPathElement, isVariant, vc);
      if (isVariant) {
        // Is Path Element matching any selectable Variant of this Choice?
        final List<Variant> lst = vc.getVariants();
        if (lst != null) {
          for (final Variant v : lst) {
            final String vid = (v == null ? null : v.getVariantID());
            matchingChoice = currentPathElement.equals(vid);
            if (matchingChoice) {
              LOGGER.debug("PE {} is matching Variant {} of VariantChoice {}", currentPathElement, vid, vc);
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
          LOGGER.debug("PE {} is matching Purpose {} of VariantChoice {}", currentPathElement, pid, vc);
          return matchingChoice;
        }
      }
      return matchingChoice;
    }
    finally {
      LOGGER.trace("<-- checkChoice(); E = {}; PE = {}; isVariant = {}; matchingChoice = {}", eid, currentPathElement, isVariant, matchingChoice);
    }
  }

  // ----------------------------------------------------------------

  private void triggerEvent(final Event e, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if ((e != null) && raeh.isRelevant(e)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("TRIGGERED: {}", e);
      }
      else {
        LOGGER.debug("TRIGGERED: {}", e.getEventID());
      }
      LOGGER.debug(e.getEventID(), new Exception()); // TODO remove
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
      LOGGER.debug(e.getEventID(), new Exception()); // TODO remove
      raeh.setObsolete(e);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "E_" + e.getEventID() + "_obsolete";
        final String msg = String.valueOf(e);
        metadata.addInfo(key, msg);
      }
    }
  }

  // ----------------------------------------------------------------

  public static String shortDisplayKE(final KnowledgeEntity ke) {
    return KnowledgeEntityHelper.shortDisplayKE(ke);
  }

  public static String shortDisplayKE(final KnowledgeEntities entities) {
    return KnowledgeEntityHelper.shortDisplayKE(entities);
  }
}
