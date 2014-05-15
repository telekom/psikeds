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
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;

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
        final KnowledgeEntities root = findRoot(r, knowledge);
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
            LOGGER.debug("Conclusion {} of Rule {} is obsolete/impossible, but Premises are not decided yet.", ceid, ruleId);
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

  // Part 2 - Step C: Modus Ponens: trigger Conclusion
  private boolean applyRuleModusPonens(final Rule r, final String conclusionEventID, final KnowledgeEntities root, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    final boolean stable = true;
    final String ruleId = (r == null ? null : r.getRuleID());
    LOGGER.info("MODUS PONENS - applying Conclusion-Event {} of Rule {}", conclusionEventID, ruleId);
//    final String eventId = r.getConclusionEventID();
//    if (applyConclusionEvent(root, eventId)) {
//      triggerRule(r, raeh, metadata, "_modus_ponens");
//    }
    return stable;
  }

  // TODO: extend feature events to feature values and create feature with corresponding value!

//  private boolean applyConclusionEvent(final List<KnowledgeEntity> root, final String eventId) {
//    boolean applied = false;
//    if ((root != null) && !root.isEmpty()) {
//      LOGGER.trace("Triggering Event: {}", eventId);
//      final Event e = this.kb.getEvent(eventId);
//      if ((e != null) && (this.autoCreateConclusionPath /* || !e.isFeatureEvent() */)) {
//        final List<String> ctx = (e == null ? null : e.getContext());
//        if (((ctx != null) && !ctx.isEmpty())) {
//          // TODO check type of event and get trigger
//          final String trigger = null;
//          ctx.add(trigger);
//          for (final KnowledgeEntity ke : root) {
//            final boolean created = createPathEntries(ke, ctx);
//            applied = applied || created;
//          }
//        }
//      }
//    }
//    return applied;
//  }
//
//  private boolean createPathEntries(final KnowledgeEntity currentKE, final List<String> path) {
//    boolean created = false;
//    KnowledgeEntity nextKE = null;
//    try {
//      LOGGER.trace("--> createPathEntries(); Path = {}; KE = {}", path, currentKE);
//      // Note: The first element in the Path is always the Variant of the current KE.
//      // The second element must be one of its Purposes and the third Element is the
//      // next Variant. So if the Path has less then 3 elements, we have nothing more
//      // to do.
//      final int len = (path == null ? 0 : path.size());
//      if ((currentKE == null) || (len < 3)) {
//        LOGGER.debug("Short or empty Path: Nothing to do!");
//        created = true;
//        return created;
//      }
//      final String nextPurpId = path.get(1);
//      final String nextVarId = path.get(2);
//      // check whether next knowledge entity already exists
//      for (final KnowledgeEntity ke : currentKE.getChildren()) {
//        if (ke != null) {
//          final Purpose p = ke.getPurpose();
//          final Variant v = ke.getVariant();
//          if ((p != null) && (v != null)) {
//            final String pid = p.getPurposeID();
//            final String vid = v.getVariantID();
//            if (nextPurpId.equals(pid) && nextVarId.equals(vid)) {
//              nextKE = ke;
//            }
//          }
//        }
//      }
//      if (nextKE != null) {
//        if (len > 3) {
//          // next ke found and still some path elements left
//          created = createPathEntries(nextKE, path.subList(3, len));
//        }
//        else {
//          LOGGER.debug("Last KE of Event already existing: Nothing to do!");
//          created = true;
//          return created;
//        }
//      }
//      else {
//        if (len > 3) {
//          if (this.autoCreateConclusionPath) {
//            // create next KE and continue with next path elements
//            nextKE = createNewEntity(currentKE, nextPurpId, nextVarId);
//            created = createPathEntries(nextKE, path.subList(3, len));
//          }
//          else {
//            created = false;
//            final String errmsg = "Auto-Creation disabled but intermediate Path-Elements of Conclusion-Event missing! Check your Config and Premises in KB!";
//            LOGGER.warn(errmsg);
//            throw new ResolutionException(errmsg);
//          }
//        }
//        else {
//          nextKE = createNewEntity(currentKE, nextPurpId, nextVarId);
//          created = (nextKE != null);
//          LOGGER.debug("Finished! Created final KE: {}", nextKE);
//        }
//      }
//      return created;
//    }
//    finally {
//      LOGGER.trace("<-- createPathEntries(); Path = {}; created = {}\nNext KE = {}", path, created, nextKE);
//    }
//  }
//
//  private void cleanupChoices(final KnowledgeEntity ke, final String purposeId) {
//    cleanupChoices(ke, purposeId, null);
//  }
//
//  private void cleanupChoices(final KnowledgeEntity ke, final String purposeId, final String variantId) {
//    final VariantChoices choices = ke.getPossibleVariants();
//    final Iterator<VariantChoice> citer = choices.iterator();
//    // TODO check VariantChoices and FeatureChoices separately
//    while (citer.hasNext()) {
//      final Choice c = citer.next();
//      if (c instanceof VariantChoice) {
//        final VariantChoice vc = (VariantChoice) c;
//        final Purpose p = vc.getPurpose();
//        if ((p != null) && purposeId.equals(p.getPurposeID())) {
//          if (StringUtils.isEmpty(variantId)) {
//            LOGGER.debug("Removing all Choices for Purpose {}", purposeId);
//            citer.remove();
//          }
//          else {
//            final List<Variant> variants = vc.getVariants();
//            final Iterator<Variant> viter = variants.iterator();
//            while (viter.hasNext()) {
//              final Variant v = viter.next();
//              if ((v != null) && variantId.equals(v.getVariantID())) {
//                LOGGER.debug("Removing Variant {} from Choices for Purpose {}", variantId, purposeId);
//                viter.remove();
//              }
//            }
//          }
//        }
//      }
//    }
//  }
//
//  private KnowledgeEntity createNewEntity(final KnowledgeEntity parent, final String purposeId, final String variantId) {
//    KnowledgeEntity nextKE = null;
//    try {
//      LOGGER.trace("--> createNewEntity(); PID = {}; VID = {}\nParent KE = {}", purposeId, variantId, parent);
//      if (parent != null) {
//        // check whether parent KE has a corresponding Choice for P and V
//        Purpose expectedPurp = null;
//        Variant expectedVar = null;
//        long qty = 1;
//        // TODO check VariantChoices and FeatureChoices separately
//        for (final Choice c : parent.getPossibleVariants()) {
//          if (c instanceof VariantChoice) {
//            final VariantChoice vc = (VariantChoice) c;
//            qty = vc.getQuantity();
//            final Purpose p = vc.getPurpose();
//            if ((p != null) && purposeId.equals(p.getPurposeID())) {
//              // matching purpose found
//              expectedPurp = p;
//              for (final Variant v : vc.getVariants()) {
//                if ((v != null) && variantId.equals(v.getVariantID())) {
//                  // matching variant found
//                  expectedVar = v;
//                }
//              }
//            }
//          }
//        }
//        if ((expectedPurp != null) && (expectedVar != null)) {
//          // perfect, the KE we want to create is really a choosable Combination of P and V!
//          LOGGER.debug("Found expected Choice for {} and {}", purposeId, variantId);
//          nextKE = regularlyCreateNewEntity(parent, expectedPurp, expectedVar, qty);
//        }
//        else {
//          // hmmm ... the KE we want to create is not a choosable Combination of P and V?!?!
//          LOGGER.debug("Expected Choice for {} and {} not found!", purposeId, variantId);
//          if (this.createNonChoosableEntities) {
//            // ok, this is actually not choosable at the moment, but config says, we do it nevertheless
//            nextKE = forceCreateNextEntity(parent, purposeId, variantId, qty);
//          }
//          else {
//            // Definition of Rules and Events in KB seems to be strange
//            final StringBuilder sb = new StringBuilder();
//            sb.append("Not a choosable Combination! Cannot create new KE with P = ");
//            sb.append(purposeId);
//            sb.append(" and V = ");
//            sb.append(variantId);
//            sb.append(" for Parent ");
//            sb.append(parent);
//            final String errmsg = sb.toString();
//            LOGGER.warn(errmsg);
//            throw new ResolutionException(errmsg);
//          }
//        }
//        // remove all choices for our purpose from parent KE
//        cleanupChoices(parent, purposeId);
//      }
//      return nextKE;
//    }
//    finally {
//      LOGGER.trace("<-- createNewEntity(); PID = {}; VID = {}\nNext KE = {}", purposeId, variantId, nextKE);
//    }
//  }
//
//  private KnowledgeEntity regularlyCreateNewEntity(final KnowledgeEntity parent, final Purpose purp, Variant var, final long qty) {
//    // ensure clean data, therefore lookup variant from knowledge base (again)
//    final org.psikeds.resolutionengine.datalayer.vo.Variant variant = this.kb.getVariant(var.getVariantID());
//    var = this.trans.valueObject2Pojo(variant); // TODO: get all features of variant! 
//    // get next choices for new KE
//    final VariantChoices newVariantChoices = getNewVariantChoices(variant);
//    final FeatureChoices newFeatureChoices = getNewFeatureChoices(variant);
//    final ConceptChoices newConceptChoices = getNewConceptChoices(variant);
//
//    // create new KE including choices
//    final KnowledgeEntity nextKE = new KnowledgeEntity(qty, purp, var, newVariantChoices, newFeatureChoices, newConceptChoices);
//    // create new KE including choices
//    // attach new KE to parent KE
//    addNewKnowledgeEntity(parent, nextKE);
//    return nextKE;
//  }
//
//  // TODO rethink creation of entities! does this make sense?
//
//  private KnowledgeEntity forceCreateNextEntity(final KnowledgeEntity parent, final String purposeId, final String variantId, final long qty) {
//    try {
//      LOGGER.trace("--> forceCreateNextEntity(); P = {}, V = {}", purposeId, variantId);
//      // lookup purpose in kb
//      final org.psikeds.resolutionengine.datalayer.vo.Purpose purp = this.kb.getPurpose(purposeId);
//      final Purpose p = (purp == null ? null : this.trans.valueObject2Pojo(purp));
//      // lookup variant in kb
//      final org.psikeds.resolutionengine.datalayer.vo.Variant var = this.kb.getVariant(variantId);
//      final Variant v = (var == null ? null : this.trans.valueObject2Pojo(var));
//      if ((p == null) || (v == null)) {
//        // KB is corrupt ... where is the Validator?
//        final StringBuilder sb = new StringBuilder();
//        sb.append("Unknow IDs! Cannot force Creation of new KE with P = ");
//        sb.append(purposeId);
//        sb.append(" and V = ");
//        sb.append(variantId);
//        sb.append(" for Parent ");
//        sb.append(parent);
//        final String errmsg = sb.toString();
//        LOGGER.warn(errmsg);
//        throw new ResolutionException(errmsg);
//      }
//      return regularlyCreateNewEntity(parent, p, v, qty);
//    }
//    finally {
//      LOGGER.trace("<-- forceCreateNextEntity(); P = {}, V = {}", purposeId, variantId);
//    }
//  }
//
//  private VariantChoices getNewVariantChoices(final org.psikeds.resolutionengine.datalayer.vo.Variant v) {
//    final VariantChoices choices = new VariantChoices();
//    final String parentVariantID = (v == null ? null : v.getVariantID());
//    try {
//      LOGGER.trace("--> getNewVariantChoices(); Variant = {}", parentVariantID);
//      // get all components/purposes constituting parent-variant ...
//      final org.psikeds.resolutionengine.datalayer.vo.Constitutes consts = this.kb.getConstitutes(parentVariantID);
//      final List<org.psikeds.resolutionengine.datalayer.vo.Component> comps = (consts == null ? null : consts.getComponents());
//      if ((comps != null) && !comps.isEmpty()) {
//        // ... and create for every existing component/purpose ...
//        for (final org.psikeds.resolutionengine.datalayer.vo.Component c : comps) {
//          if (c != null) {
//            final long qty = c.getQuantity();
//            final String pid = c.getPurposeID();
//            final org.psikeds.resolutionengine.datalayer.vo.Purpose p = this.kb.getPurpose(pid);
//            // ... a new VariantChoice-POJO for the Client
//            final org.psikeds.resolutionengine.datalayer.vo.Fulfills ff = this.kb.getFulfills(pid);
//            final org.psikeds.resolutionengine.datalayer.vo.Variants variants = new org.psikeds.resolutionengine.datalayer.vo.Variants();
//            for (final String vid : ff.getVariantID()) {
//              variants.addVariant(this.kb.getVariant(vid));
//            }
//            final VariantChoice vc = this.trans.valueObject2Pojo(parentVariantID, p, variants, qty);
//            LOGGER.trace("Adding new Variant-Choice: {}", vc);
//            choices.add(vc);
//          }
//        }
//      }
//      // return list of all new variant choices
//      return choices;
//    }
//    finally {
//      LOGGER.trace("<-- getNewVariantChoices(); Variant = {}\nChoices = {}", parentVariantID, choices);
//    }
//  }
//
//  private FeatureChoices getNewFeatureChoices(final org.psikeds.resolutionengine.datalayer.vo.Variant v) {
//    final FeatureChoices choices = new FeatureChoices();
//    final String parentVariantID = (v == null ? null : v.getVariantID());
//    try {
//      LOGGER.trace("--> getNewFeatureChoices(); Variant = {}", parentVariantID);
//      // get all features of this variant ...
//      final org.psikeds.resolutionengine.datalayer.vo.Features newfeats = this.kb.getFeatures(parentVariantID);
//      final List<org.psikeds.resolutionengine.datalayer.vo.Feature> feats = (newfeats == null ? null : newfeats.getFeature());
//      if ((feats != null) && !feats.isEmpty()) {
//        // ... and create for every feature ...
//        for (final org.psikeds.resolutionengine.datalayer.vo.Feature f : feats) {
//          // ... a new FeatureChoice-POJO for the Client
//          // TODO this is wrong!!! we must get the values allowed for this variant, not all possible values of the feature!!!
//          final FeatureChoice fc = this.trans.valueObject2Pojo(parentVariantID, f.getValues());
//          LOGGER.trace("Adding new Feature-Choice: {}", fc);
//          choices.add(fc);
//        }
//      }
//      // return list of all new feature choices
//      return choices;
//    }
//    finally {
//      LOGGER.trace("<-- getNewFeatureChoices(); Variant = {}\nChoices = {}", parentVariantID, choices);
//    }
//  }
//
//  private ConceptChoices getNewConceptChoices(final org.psikeds.resolutionengine.datalayer.vo.Variant v) {
//    // TODO implement!
//    return null;
//  }
//
//  private void addNewKnowledgeEntity(final KnowledgeEntity parent, final KnowledgeEntity newke) {
//    final KnowledgeEntities entities = (parent == null ? null : parent.getChildren());
//    addNewKnowledgeEntity(entities, newke);
//  }
//
//  private void addNewKnowledgeEntity(final KnowledgeEntities entities, final KnowledgeEntity newke) {
//    if ((entities != null) && (newke != null)) {
//      for (final KnowledgeEntity ke : entities) {
//        final String pid1 = ke.getPurpose().getPurposeID();
//        final String pid2 = newke.getPurpose().getPurposeID();
//        final String vid1 = ke.getVariant().getVariantID();
//        final String vid2 = newke.getVariant().getVariantID();
//        if (pid1.equals(pid2) && vid1.equals(vid2)) {
//          LOGGER.debug("addNewKnowledgeEntity(): Entity-List already contains KnowledgeEntity: {}", newke);
//          return;
//        }
//      }
//      LOGGER.trace("addNewKnowledgeEntity(): Adding new KnowledgeEntity: {}", newke);
//      entities.add(newke);
//    }
//  }

  // ----------------------------------------------------------------

  // Part 2 - Step D: Modus Tollens: disable Trigger
  private boolean applyRuleModusTollens(final Rule r, final String premiseEventID, final KnowledgeEntities root, final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    final boolean stable = true;
    final String ruleId = (r == null ? null : r.getRuleID());
    LOGGER.info("MODUS TOLLENS - disabling Premise-Event {} of Rule {}", premiseEventID, ruleId);
//    // TODO handle list of premises
//    // TODO handle self-fulfilling rules
//    final String eventId = r.getPremiseEventID().get(0);
//    if (disablePremiseEvent(root, eventId)) {
//      triggerRule(r, raeh, metadata, "_modus_tollens");
//    }
    return stable;
  }

//  private boolean disablePremiseEvent(final List<KnowledgeEntity> root, final String eventId) {
//    boolean disabled = false;
//    if ((root != null) && !root.isEmpty()) {
//      LOGGER.trace("Disabling Event: {}", eventId);
//      final Event e = this.kb.getEvent(eventId);
//      final List<String> ctx = (e == null ? null : e.getContext());
//      if ((ctx != null) && !ctx.isEmpty()) {
//        // TODO: check kind of event and get trigger
//        final String trigger = null;
//        ctx.add(trigger);
//        for (final KnowledgeEntity ke : root) {
//          final boolean removed = removePathFromChoices(ke, ctx);
//          disabled = disabled || removed;
//        }
//      }
//    }
//    return disabled;
//  }
//
//  private boolean removePathFromChoices(final KnowledgeEntity currentKE, final List<String> path) {
//    boolean removed = false;
//    KnowledgeEntity nextKE = null;
//    try {
//      LOGGER.trace("--> removePathFromChoices(); Path = {}; KE = {}", path, currentKE);
//      // Note: The first element in the Path is always the Variant of the current KE.
//      // The second element must be one of its Purposes and the third Element is the
//      // next Variant. So if the Path has less then 2 elements, we have nothing more
//      // to do.
//      final int len = (path == null ? 0 : path.size());
//      if ((currentKE == null) || (len <= 1)) {
//        LOGGER.debug("Short or empty Path: Nothing to do!");
//        removed = true;
//        return removed;
//      }
//      // Check that first Path Element is really matching to current KE
//      final Variant currentVariant = currentKE.getVariant();
//      final String currentVariantId = (currentVariant == null ? null : currentVariant.getVariantID());
//      final String expectedVariantId = path.get(0);
//      if (StringUtils.isEmpty(expectedVariantId) || !expectedVariantId.equals(currentVariantId)) {
//        final String errmsg = "LOGICAL ERROR: Path not matching to current KE!";
//        LOGGER.warn(errmsg);
//        throw new ResolutionException(errmsg);
//      }
//
//      if (len == 2) {
//        // There is just one additional PE left, we remove all Choices for this Purpose
//        final String purposeId = path.get(1);
//        cleanupChoices(currentKE, purposeId);
//        removed = true;
//        return removed;
//      }
//      if (len == 3) {
//        // There are two more PEs left, so we remove that Variant from Choices for this Purpose
//        final String purposeId = path.get(1);
//        final String variantId = path.get(2);
//        cleanupChoices(currentKE, purposeId, variantId);
//        removed = true;
//        return removed;
//
//      }
//      // Path has more than 3 Elements: Check whether next KnowledgeEntity exists
//      final String nextPurpId = path.get(1);
//      final String nextVarId = path.get(2);
//      for (final KnowledgeEntity ke : currentKE.getChildren()) {
//        if (ke != null) {
//          final Purpose p = ke.getPurpose();
//          final Variant v = ke.getVariant();
//          if ((p != null) && (v != null)) {
//            final String pid = p.getPurposeID();
//            final String vid = v.getVariantID();
//            if (nextPurpId.equals(pid) && nextVarId.equals(vid)) {
//              nextKE = ke;
//            }
//          }
//        }
//      }
//      if (nextKE != null) {
//        if (LOGGER.isTraceEnabled()) {
//          LOGGER.trace("Recursion: Path {} matching next KE {}", path, nextKE);
//        }
//        else {
//          LOGGER.debug("Next KE for Path {} found --> Recursion!", path);
//        }
//        removed = removePathFromChoices(nextKE, path.subList(3, len));
//        return removed;
//      }
//      else {
//        removed = false;
//        LOGGER.trace("Cannot remove Path {} from current KE {}", path, currentKE);
//        if (this.keepModusTollensForLater) {
//          LOGGER.debug("Keeping Rule for later.");
//          return removed;
//        }
//        final String errmsg = "LOGICAL ERROR: Cannot apply Modus Tollens, Trigger-Event cannot be disabled! Check your Config and Rules in KB!";
//        LOGGER.warn(errmsg);
//        throw new ResolutionException(errmsg);
//      }
//    }
//    finally {
//      LOGGER.trace("<-- removePathFromChoices(); Path = {}; created = {}\nNext KE = {}", path, removed, nextKE);
//    }
//  }

  // ----------------------------------------------------------------

  // In a regular Tree each Variant is used just once. However in a general
  // Knowledge-Graph a Variant could be (re)used several times for different
  // Purposes. Therefore the following Methods do not stop after the first
  // Hit and return a List of all Entities containing the desired Variant.

  // TODO: extract findRoot() to Helper
  // TODO: performance optimization: cache pointers to root of variant in raeh, search only once!

  private KnowledgeEntities findRoot(final Rule r, final Knowledge knowledge) {
    final String rootVariantId = r.getVariantID();
    final KnowledgeEntities root = findRoot(rootVariantId, knowledge);
    LOGGER.trace("Root of Rule {} is: {}", r.getRuleID(), root);
    return root;
  }

  private KnowledgeEntities findRoot(final String rootVariantId, final Knowledge knowledge) {
    final KnowledgeEntities result = new KnowledgeEntities();
    findRoot(result, rootVariantId, knowledge.getEntities());
    return result;
  }

  private void findRoot(final KnowledgeEntities result, final String rootVariantId, final KnowledgeEntities entities) {
    for (final KnowledgeEntity ke : entities) {
      if (rootVariantId.equals(ke.getVariant().getVariantID())) {
        result.add(ke);
      }
      findRoot(result, rootVariantId, ke.getChildren());
    }
  }
}
