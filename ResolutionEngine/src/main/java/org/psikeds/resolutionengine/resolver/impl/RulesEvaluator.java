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
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;
import org.psikeds.resolutionengine.util.ChoicesHelper;
import org.psikeds.resolutionengine.util.KnowledgeEntityHelper;
import org.psikeds.resolutionengine.util.KnowledgeHelper;

/**
 * This Resolver will evaluate and apply all Rules that were triggered.
 * 
 * Decission will be ignored, Metadata is optional and will be used for
 * Information about Changes to the State of Rules, if present.
 * 
 * @author marco@juliano.de
 * 
 */
public class RulesEvaluator implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesEvaluator.class);

  public static final Transformer DEFAULT_TRANSFORMER = new Vo2PojoTransformer();
  public static final boolean DEFAULT_CREATE_CONCLUSION_PATH = true;
  public static final boolean DEFAULT_CREATE_NON_CHOOSABLE_ENTITIES = false;
  public static final boolean DEFAULT_KEEP_MODUS_TOLLENS_FOR_LATER = true;

  private KnowledgeBase kb;
  private Transformer trans;
  private boolean autoCreateConclusionPath;
  private boolean createNonChoosableEntities;
  private boolean keepModusTollensForLater;

  public RulesEvaluator() {
    this(null);
  }

  public RulesEvaluator(final KnowledgeBase kb) {
    this(kb, DEFAULT_TRANSFORMER);
  }

  public RulesEvaluator(final KnowledgeBase kb, final Transformer trans) {
    this(kb, trans, DEFAULT_CREATE_CONCLUSION_PATH, DEFAULT_CREATE_NON_CHOOSABLE_ENTITIES, DEFAULT_KEEP_MODUS_TOLLENS_FOR_LATER);
  }

  public RulesEvaluator(final KnowledgeBase kb, final Transformer trans, final boolean createConclusion, final boolean createNonChoosable, final boolean keepForLater) {
    this.kb = kb;
    this.trans = trans;
    this.autoCreateConclusionPath = createConclusion;
    this.createNonChoosableEntities = createNonChoosable;
    this.keepModusTollensForLater = keepForLater;
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
      sb.append("Keeping Modus Tollens for later: {}");
      LOGGER.info(sb.toString(), this.autoCreateConclusionPath, this.createNonChoosableEntities, this.keepModusTollensForLater);
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
    boolean ok = false;
    boolean stable = true;
    try {
      LOGGER.debug("Evaluating Rules ...");
      if ((knowledge == null) || (raeh == null)) {
        throw new ResolutionException("Knowledge or RulesAndEventsHandler missing!");
      }
      // Knowledge is clean so far
      raeh.setKnowledgeDirty(false);
      // Check Rules, i.e. apply or expire Rules depending on their Context, Premise or Conclusion
      stable = checkRules(knowledge, raeh, metadata);
      if (!stable) {
        // A rule was applied and it is neccessary to execute the full Resolver-Chain once again
        LOGGER.debug("Knowledge is not stable, need another Iteration of all Resolvers!");
        knowledge.setStable(false);
      }
      // done
      ok = true;
      return knowledge;
    }
    catch (final ResolutionException re) {
      LOGGER.warn("Could not evaluate Rules: " + re.getMessage(), re);
      throw re;
    }
    catch (final Exception ex) {
      LOGGER.warn("Unexpected Error while evaluating Rules: " + ex.getMessage(), ex);
      throw new ResolutionException("Could not evaluate Rules", ex);
    }
    finally {
      RulesAndEventsHandler.logContents(raeh);
      if (ok) {
        LOGGER.debug("... successfully finished evaluating Rules, Result is " + (stable ? "" : "NOT ") + "stable.");
      }
      else {
        LOGGER.debug("... finished evaluating Rules with ERROR(S)!!!");
      }
    }
  }

  // ----------------------------------------------------------------

  private boolean checkRules(final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRules()");
      final List<Rule> rules = raeh.getRelevantRules();
      for (final Rule r : rules) {
        final String ruleId = (r == null ? null : r.getRuleID());
        if (StringUtils.isEmpty(ruleId)) {
          markRuleObsolete(r, raeh, metadata);
          throw new ResolutionException("Invalid Rule!");
        }
        // check root / nexus
        final KnowledgeEntities root = KnowledgeHelper.findRoot(r, knowledge);
        if ((root == null) || root.isEmpty()) {
          LOGGER.debug("Nothing to do. Nexus {} of Rule {} is not included in the current Knowledge.", r.getVariantID(), ruleId);
          continue;
        }
        // check Conclusion
        final String ceid = r.getConclusionEventID();
        final boolean conclusionObsolete = raeh.isObsolete(ceid);
        final boolean conclusionTriggered = raeh.isTriggered(ceid);
        // check Premise(s)
        String possiblePremiseID = null;
        int numAllPremises = 0;
        int numPossiblePremises = 0;
        int numObsoletePremises = 0;
        int numTriggeredPremises = 0;
        if (r.isSelfFulfilling()) {
          LOGGER.debug("Rule {} is fulfilling itself.", ruleId);
        }
        else {
          LOGGER.debug("Checking Premise(s) of Rule {}", ruleId);
          final List<String> premises = r.getPremiseEventID();
          numAllPremises = (premises == null ? 0 : premises.size());
          if (numAllPremises > 0) {
            for (final String peid : premises) {
              if (raeh.isRelevant(peid)) {
                numPossiblePremises++;
                possiblePremiseID = peid;
              }
              if (raeh.isObsolete(peid)) {
                numObsoletePremises++;
              }
              if (raeh.isTriggered(peid)) {
                numTriggeredPremises++;
              }
            }
          }
        }
        final boolean onePremiseObsolete = (numObsoletePremises > 0);
        final boolean allPremisesTriggered = (numTriggeredPremises == numAllPremises);
        final boolean exactlyOnePremiseLeft = ((numAllPremises > 0) && (numPossiblePremises == 1) && !StringUtils.isEmpty(possiblePremiseID));
        // analyze and apply Rule depending on State of Events
        if (onePremiseObsolete) {
          LOGGER.debug("Premise(s) obsolete/impossible --> mark also Rule {} as obsolete.", ruleId);
          markRuleObsolete(r, raeh, metadata);
          continue;
        }
        if (conclusionTriggered) {
          LOGGER.debug("Conclusion {} already triggered/selected --> mark also Rule {} as triggered.", ceid, ruleId);
          triggerRule(r, raeh, metadata);
          continue;
        }
        if (allPremisesTriggered) {
          LOGGER.debug("All Premise(s) of Rule {} triggered/selected --> Modus Ponens!", ruleId);
          if (!applyRuleModusPonens(r, ceid, root, knowledge, raeh, metadata)) {
            stable = false;
          }
          continue;
        }
        if (conclusionObsolete) {
          if (exactlyOnePremiseLeft) {
            LOGGER.debug("Conclusion {} of Rule {} is obsolete/impossible and exactly one Premise left: {} --> Modus Tollens!", ceid, ruleId, possiblePremiseID);
            if (!applyRuleModusTollens(r, possiblePremiseID, root, knowledge, raeh, metadata)) {
              stable = false;
            }
          }
          else {
            LOGGER.debug("Keeping Rule {}, because Conclusion {} is obsolete/impossible but Premises are not decided yet.", ruleId, ceid);
          }
          continue;
        }
        LOGGER.debug("Keeping still possible Rule: {}", ruleId);
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRules(); stable = {}", stable);
    }
  }

  // ----------------------------------------------------------------

  private boolean applyRuleModusPonens(final Rule r, final String conclusionEventID, final KnowledgeEntities root, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    final String ruleId = (r == null ? null : r.getRuleID());
    LOGGER.info("MODUS PONENS - applying Conclusion-Event {} of Rule {}", conclusionEventID, ruleId);
    if (applyEvent(root, conclusionEventID)) {
      triggerRule(r, raeh, metadata, "_modus_ponens");
      stable = false;
    }
    return stable;
  }

  private boolean applyEvent(final KnowledgeEntities root, final String eventId) {
    boolean applied = false;
    try {
      LOGGER.trace("--> applyEvent(); E = {}", eventId);
      final Event e = this.kb.getEvent(eventId);
      final List<String> ctx = e.getContext();
      for (final KnowledgeEntity ke : root) {
        if (e.isNotEvent()) {
          if (removeTriggerFromChoices(e, ke, ctx)) {
            applied = true;
          }
        }
        else {
          if (createPathEntries(e, ke, ctx)) {
            applied = true;
          }
        }
      }
      return applied;
    }
    finally {
      LOGGER.trace("<-- applyEvent(); E = {}; applied = {}", eventId, applied);
    }
  }

  private boolean createPathEntries(final Event e, final KnowledgeEntity currentKE, final List<String> path) {
    boolean created = false;
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> createPathEntries(); E = {}; Path = {}; KE = {}", e.getEventID(), path, currentKE);
      // Note: The first element in the Path is always the Variant of the current KE.
      // The second element is one of its Purposes, the third Element is the next Variant
      // and so on.
      final int len = (path == null ? 0 : path.size());
      if (len <= 0) {
        // nothing to do
        return created;
      }
      final String currentVarId = path.get(0);
      if (len == 1) {
        // Context-Path contains just the current Variant-ID, so this must be a Feature- or Concept-Trigger
        created = applyFeatureTrigger(e, currentKE, currentVarId);
        return created;
      }
      final String nextPurpId = path.get(1);
      if (len == 2) {
        // Context-Path ends with a Purpose-ID, so this must be a Variant-Trigger
        created = applyVariantTrigger(e, currentKE, nextPurpId);
        return created;
      }
      // Context-Path has 3 or more Elements
      final String nextVarId = path.get(2);
      // Check whether next KnowledgeEntity already exists
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
          created = createPathEntries(e, nextKE, path.subList(3, len));
        }
        else {
          // Context-Path of Event ends at an existing KE, so try to apply Trigger
          created = applyTrigger(e, nextKE);
          return created;
        }
      }
      else {
        if (len > 3) {
          if (this.autoCreateConclusionPath) {
            // create next KE and continue with next path elements
            nextKE = createNewEntity(currentKE, nextPurpId, nextVarId);
            created = createPathEntries(e, nextKE, path.subList(3, len));
          }
          else {
            created = false;
            throw new ResolutionException("Auto-Creation disabled but intermediate Path-Elements of Conclusion-Event missing! Check your Config and Premises in KB!");
          }
        }
        else { // len == 3
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

  // ----------------------------------------------------------------

  private KnowledgeEntity createNewEntity(final KnowledgeEntity parent, final String purposeId, final String variantId) {
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> createNewEntity(); PID = {}; VID = {}\nParent KE = {}", purposeId, variantId, parent);
      if (parent != null) {
        // check whether parent KE has a corresponding Choice for P and V
        Purpose expectedPurp = null;
        Variant expectedVar = null;
        for (final VariantChoice vc : parent.getPossibleVariants()) {
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
        if ((expectedPurp != null) && (expectedVar != null)) {
          // perfect, the KE we want to create is really a currently choosable Combination of P and V!
          LOGGER.debug("Found expected Choice for {} and {}", purposeId, variantId);
          nextKE = KnowledgeEntityHelper.ceateNewEntity(this.kb, this.trans, parent, expectedPurp, expectedVar);
        }
        else {
          // Hmmmm ... the KE we want to create is not a currently choosable Combination of P and V?!?!
          LOGGER.debug("Expected Choice for {} and {} not found!", purposeId, variantId);
          if (this.createNonChoosableEntities) {
            // Ok, this is actually not choosable at the moment, but config says, we do it nevertheless
            LOGGER.debug("Creating a currently not valid Knowledge-Entity for {} and {}", purposeId, variantId);
            nextKE = KnowledgeEntityHelper.ceateNewEntity(this.kb, this.trans, parent, purposeId, variantId);
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
            throw new ResolutionException(sb.toString());
          }
        }
      }
      return nextKE;
    }
    finally {
      LOGGER.trace("<-- createNewEntity(); PID = {}; VID = {}\nNext KE = {}", purposeId, variantId, nextKE);
    }
  }

  // ----------------------------------------------------------------

  private boolean applyTrigger(final Event e, final KnowledgeEntity ke) {
    final Variant v = (ke == null ? null : ke.getVariant());
    final String vid = (v == null ? null : v.getVariantID());
    if (Event.TRIGGER_TYPE_VARIANT.equals(e.getTriggerType())) {
      if (!StringUtils.isEmpty(vid) && vid.equals(e.getTriggerID())) {
        // nothing to do, trigger is variant of this KE
        return false;
      }
      else {
        throw new ResolutionException("Cannot apply Trigger " + e.getTriggerID() + " of Event " + e.getEventID() + ". Inconsistent State of Knowledge!?!");
      }
    }
    else {
      // trigger must be a Feature- or Concept-Trigger for the Variant of this KE
      return applyFeatureTrigger(e, ke, vid);
    }
  }

  private boolean applyFeatureTrigger(final Event e, final KnowledgeEntity ke, final String variantId) {
    if (Event.TRIGGER_TYPE_FEATURE_VALUE.equals(e.getTriggerType())) {
      final String featureValueId = e.getTriggerID();
      final FeatureValue fv = this.trans.valueObject2Pojo(this.kb.getFeatureValue(featureValueId));
      final String featureId = fv.getFeatureID();
      // remove all Values for this Feature from the KE
      ChoicesHelper.cleanupFeatureChoices(ke, variantId, featureId);
      // set the triggered Value for this KE
      ke.addFeature(fv);
      return true;
    }
    else if (Event.TRIGGER_TYPE_CONCEPT.equals(e.getTriggerType())) {
      // TODO implement
      return false;
    }
    else {
      throw new ResolutionException("Cannot apply Feature-Trigger. Unexpected Trigger-Type: " + e.getTriggerType());
    }
  }

  private boolean applyVariantTrigger(final Event e, final KnowledgeEntity ke, final String purposeId) {
    if (Event.TRIGGER_TYPE_VARIANT.equals(e.getTriggerType())) {
      final KnowledgeEntity newKE = createNewEntity(ke, purposeId, e.getTriggerID());
      return (newKE != null);
    }
    else {
      throw new ResolutionException("Cannot apply Variant-Trigger. Unexpected Trigger-Type: " + e.getTriggerType());
    }
  }

  // ----------------------------------------------------------------

  private boolean applyRuleModusTollens(final Rule r, final String premiseEventID, final KnowledgeEntities root, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    final String ruleId = (r == null ? null : r.getRuleID());
    LOGGER.info("MODUS TOLLENS - Disabling Premise-Event {} of Rule {}", premiseEventID, ruleId);
    if (disableEvent(root, premiseEventID)) {
      triggerRule(r, raeh, metadata, "_modus_tollens");
      stable = false;
    }
    return stable;
  }

  private boolean disableEvent(final KnowledgeEntities root, final String eventId) {
    boolean removed = false;
    try {
      LOGGER.trace("--> disableEvent(); E = {}", eventId);
      final Event e = this.kb.getEvent(eventId);
      final List<String> ctx = e.getContext();
      for (final KnowledgeEntity ke : root) {
        if (e.isNotEvent()) {
          if (createPathEntries(e, ke, ctx)) {
            removed = true;
          }
        }
        else {
          if (removeTriggerFromChoices(e, ke, ctx)) {
            removed = true;
          }
        }
      }
      return removed;
    }
    finally {
      LOGGER.trace("<-- disableEvent(); E = {}; removed = {}", eventId, removed);
    }
  }

  private boolean removeTriggerFromChoices(final Event e, final KnowledgeEntity currentKE, final List<String> path) {
    boolean removed = false;
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> removeTriggerFromChoices(); Path = {}; KE = {}", path, currentKE);
      final int len = (path == null ? 0 : path.size());
      if (len < 1) {
        // Walked complete Context-Path, now check Trigger and cleanup corresponding Choices
        removed = cleanupChoices(e, currentKE);
        return removed;
      }
      // Check that first Path Element is really matching to current KE
      final Variant currentVariant = (currentKE == null ? null : currentKE.getVariant());
      final String currentVariantId = (currentVariant == null ? null : currentVariant.getVariantID());
      final String expectedVariantId = path.get(0);
      if (StringUtils.isEmpty(expectedVariantId) || !expectedVariantId.equals(currentVariantId)) {
        throw new ResolutionException("LOGICAL ERROR: Path not matching to current KE!!!");
      }
      if (len == 1) {
        // There is just one PE, which is the Variant of this KE
        // --> remove Trigger from Feature- and Concept-Choices
        removed = cleanupFeatureChoices(e, currentKE, expectedVariantId);
        return removed;
      }
      if (len == 2) {
        // There is one additional PE left, which is a Purpose
        // --> remove Trigger from all Variant-Choices for this Purpose
        final String purposeId = path.get(1);
        removed = cleanupVariantChoices(e, currentKE, purposeId);
        return removed;
      }
      // Path has 3 or more Elements: Check whether next KnowledgeEntity exists
      final String nextPurpId = path.get(1);
      final String nextVarId = path.get(2);
      for (final KnowledgeEntity ke : currentKE.getChildren()) {
        final Purpose p = (ke == null ? null : ke.getPurpose());
        final Variant v = (ke == null ? null : ke.getVariant());
        if ((p != null) && (v != null)) {
          final String pid = p.getPurposeID();
          final String vid = v.getVariantID();
          if (nextPurpId.equals(pid) && nextVarId.equals(vid)) {
            nextKE = ke;
          }
        }
      }
      if (nextKE != null) {
        LOGGER.debug("Next KE for Path {} found --> Recursion!", path);
        removed = removeTriggerFromChoices(e, nextKE, path.subList(2, len));
        return removed;
      }
      // Next KnowledgeEntity does not exist!?
      LOGGER.debug("Cannot remove Path {} from current KE {}", path, currentKE);
      if (this.keepModusTollensForLater) {
        removed = false;
        return removed;
      }
      else {
        throw new ResolutionException("LOGICAL ERROR: Cannot apply Modus Tollens, Trigger-Event cannot be disabled! Check your Config and Rules in KB!!!");
      }
    }
    finally {
      LOGGER.trace("<-- removeTriggerFromChoices(); removed = {}; Path = {}", removed, path);
    }
  }

  // ----------------------------------------------------------------

  private boolean cleanupChoices(final Event e, final KnowledgeEntity ke) {
    boolean removed = false;
    try {
      LOGGER.trace("--> cleanupChoices(); E = {}; KE = {}", e.getEventID(), ke);
      if (Event.TRIGGER_TYPE_VARIANT.equals(e.getTriggerType())) {
        removed = cleanupVariantChoices(e, ke, null);
      }
      else {
        removed = cleanupFeatureChoices(e, ke, null);
      }
      return removed;
    }
    finally {
      LOGGER.trace("<-- cleanupChoices(); E = {}; removed = {}", e.getEventID(), removed);
    }
  }

  private boolean cleanupVariantChoices(final Event e, final KnowledgeEntity ke, final String purposeId) {
    boolean removed = false;
    try {
      LOGGER.trace("--> cleanupVariantChoices(); PID = {}; TID = {}; E = {}; KE = {}", purposeId, e.getTriggerID(), e.getEventID(), ke);
      if (Event.TRIGGER_TYPE_VARIANT.equals(e.getTriggerType())) {
        removed = ChoicesHelper.cleanupVariantChoices(ke, purposeId, e.getTriggerID());
        return removed;
      }
      else {
        throw new ResolutionException("Cannot cleanup Variant-Choices. Unexpected Trigger-Type: " + e.getTriggerType());
      }
    }
    finally {
      LOGGER.trace("<-- cleanupVariantChoices(); PID = {}; TID = {}, E = {}; removed = {}", purposeId, e.getTriggerID(), e.getEventID(), removed);
    }
  }

  private boolean cleanupFeatureChoices(final Event e, final KnowledgeEntity ke, final String variantId) {
    boolean removed = false;
    try {
      LOGGER.trace("--> cleanupFeatureChoices(); VID = {}; TID = {}, E = {}; KE = {}", variantId, e.getTriggerID(), e.getEventID(), ke);
      if (Event.TRIGGER_TYPE_FEATURE_VALUE.equals(e.getTriggerType())) {
        final String featureValueId = e.getTriggerID();
        final org.psikeds.resolutionengine.datalayer.vo.FeatureValue fv = this.kb.getFeatureValue(featureValueId);
        final String featureId = fv.getFeatureID();
        removed = ChoicesHelper.cleanupFeatureChoices(ke, variantId, featureId, featureValueId);
        return removed;
      }
      else if (Event.TRIGGER_TYPE_CONCEPT.equals(e.getTriggerType())) {
        // TODO: implement Concepts
        return removed;
      }
      else {
        throw new ResolutionException("Cannot cleanup Feature-Choices. Unexpected Trigger-Type: " + e.getTriggerType());
      }
    }
    finally {
      LOGGER.trace("<-- cleanupFeatureChoices(); VID = {}; TID = {}, E = {}; removed = {}", variantId, e.getTriggerID(), e.getEventID(), removed);
    }
  }

  // ----------------------------------------------------------------

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
}
