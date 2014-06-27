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
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.Concepts;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;
import org.psikeds.resolutionengine.util.ChoicesHelper;
import org.psikeds.resolutionengine.util.FeatureValueHelper;
import org.psikeds.resolutionengine.util.KnowledgeEntityHelper;
import org.psikeds.resolutionengine.util.RelationHelper;

/**
 * This Resolver will evaluate all active Relations and update
 * FeatureValues and Concepts accordingly.
 * 
 * Decission will be ignored, Metadata is optional and will be used for
 * Information about Changes to the State of Relations, if present.
 * 
 * @author marco@juliano.de
 * 
 */
public class RelationEvaluator implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationEvaluator.class);

  public static final Transformer DEFAULT_TRANSFORMER = new Vo2PojoTransformer();

  private KnowledgeBase kb;
  private Transformer trans;

  public RelationEvaluator() {
    this(null);
  }

  public RelationEvaluator(final KnowledgeBase kb) {
    this(kb, DEFAULT_TRANSFORMER);
  }

  public RelationEvaluator(final KnowledgeBase kb, final Transformer trans) {
    this.kb = kb;
    this.trans = trans;
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
   * Check that Relation-Evaluator was configured/wired correctly.
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
      LOGGER.debug("Evaluating Relations ...");
      if ((knowledge == null) || (raeh == null)) {
        throw new ResolutionException("Knowledge or RulesAndEventsHandler missing!");
      }
      // Check Relations, i.e. apply or expire Relations depending on their Context and Condition
      stable = checkRelations(knowledge, raeh, metadata);
      if (!stable) {
        // A Relation was applied and it is neccessary to execute the full Resolver-Chain once again
        LOGGER.debug("Knowledge is not stable, need another Iteration of all Resolvers!");
//        knowledge.setStable(false); // TODO enable!!!
      }
      // done
      ok = true;
      return knowledge;
    }
    catch (final ResolutionException re) {
      LOGGER.warn("Could not evaluate Relations: " + re.getMessage(), re);
      throw re;
    }
    catch (final Exception ex) {
      LOGGER.warn("Unexpected Error while evaluating Relations: " + ex.getMessage(), ex);
      throw new ResolutionException("Could not evaluate Relations", ex);
    }
    finally {
      RulesAndEventsHandler.logContents(raeh);
      if (ok) {
        LOGGER.debug("... successfully finished evaluating Relations, Result is " + (stable ? "" : "NOT ") + "stable.");
      }
      else {
        LOGGER.debug("... finished evaluating Relations with ERROR(S)!!!");
      }
    }
  }

  // ----------------------------------------------------------------

  private boolean checkRelations(final Knowledge knowledge, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRelations()");
      final List<Relation> rels = raeh.getActiveRelations();
      for (final Relation r : rels) {
        final String relationId = (r == null ? null : r.getRelationID());
        final String rootVariantId = (r == null ? null : r.getVariantID());
        if (StringUtils.isEmpty(relationId) || StringUtils.isEmpty(rootVariantId)) {
          final String msg = "Invalid Relation: " + relationId;
          LOGGER.debug(msg);
          markRelationObsolete(r, raeh, metadata);
          throw new ResolutionException(msg);
        }
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Checking: {}", r);
        }
        else {
          LOGGER.debug("Checking Relation {}", relationId);
        }
        // check pre-Condition
        if (r.isConditional()) {
          final String ceid = r.getConditionalEventID();
          if (raeh.isObsolete(ceid)) {
            LOGGER.debug("Conditional Event {} of Relation {} is obsolete. Relation is therefore also obsolete.", ceid, relationId);
            markRelationObsolete(r, raeh, metadata);
            continue;
          }
          else if (!raeh.isTriggered(ceid)) {
            LOGGER.debug("Conditional Event {} of Relation {} is not triggered yet. Skipping Evaluation.", ceid, relationId);
            continue;
          }
          LOGGER.debug("Precondition (Event {}) of conditional Relation {} is true.", relationId, ceid);
        }
        // check root / nexus
        final KnowledgeEntities root = RelationHelper.findRoot(r, knowledge);
        if ((root == null) || root.isEmpty()) {
          LOGGER.debug("Nexus {} of Relation {} is not included in the current Knowledge yet. Skipping Evaluation.", rootVariantId, relationId);
          continue;
        }
        else {
          LOGGER.debug("Nexus {} of Relation {} is: {}", rootVariantId, relationId, shortDisplayKE(root));
          // evaluate relation
          if (!checkRelation(r, root, raeh, metadata)) {
            stable = false;
          }
        }
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRelations(); stable = {}", stable);
    }
  }

  private boolean checkRelation(final Relation r, final KnowledgeEntities root, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRelation(); Relation = {}", r);
      for (final KnowledgeEntity ke : root) {
        if (!raeh.isActive(r)) {
          // Fail fast: Relation was already made obsolete
          return stable;
        }
        if (!checkRelation(r, ke, raeh, metadata)) {
          stable = false;
        }
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRelation(); stable = {}", stable);
    }
  }

  private boolean checkRelation(final Relation r, final KnowledgeEntity root, final RulesAndEventsHandler raeh, final Metadata metadata) {
    boolean stable = true;
    final String relationId = (r == null ? null : r.getRelationID());
    try {
      LOGGER.trace("--> checkRelation(); relationId = {}; root = {}", relationId, shortDisplayKE(root));
      final RelationParameter left = r.getLeftSide();
      final RelationParameter right = r.getLeftSide();
      if (left.isConstant() && right.isConstant()) {
        // both sides are constants? does not make sense!
        LOGGER.debug("Ignoring Relation {} because both Parameters are Constants!", relationId);
        markRelationObsolete(r, raeh, metadata);
        stable = true;
        return stable;
      }
      final RelationOperator op = r.getOperator();
      if (!left.isConstant() && right.isConstant()) {
        // left side is a feature of an entity, right side is a constant
        final KnowledgeEntity leftEntity = getTargetEntity(r, root, true, raeh, metadata);
        if (leftEntity != null) {
          LOGGER.debug("Found left Target of Relation {} --> {}", relationId, shortDisplayKE(leftEntity));
          if (raeh.isActive(r)) {
            final Feature leftParameter = RelationHelper.getFeatureVariable(this.kb, left);
            final FeatureValue rightConstant = RelationHelper.getConstantFeatureValue(this.kb, right);
            stable = evaluateSingleSidedRelation(r, leftEntity, leftParameter, op, rightConstant, raeh, metadata);
          }
        }
        else {
          LOGGER.debug("Left Target of Relation {} not contained in Knowledge (yet).", relationId);
          stable = true;
        }
        return stable;
      }
      if (left.isConstant() && !right.isConstant()) {
        // right side is a feature of an entity, left side is a constant
        final KnowledgeEntity rightEntity = getTargetEntity(r, root, false, raeh, metadata);
        if (rightEntity != null) {
          LOGGER.debug("Found right Target of Relation {} --> {}", relationId, shortDisplayKE(rightEntity));
          if (raeh.isActive(r)) {
            final Feature rightParameter = RelationHelper.getFeatureVariable(this.kb, right);
            final FeatureValue leftConstant = RelationHelper.getConstantFeatureValue(this.kb, left);
            // switch sides, invert operator and handle like the case above
            stable = evaluateSingleSidedRelation(r, rightEntity, rightParameter, RelationHelper.getComplementaryOperator(op), leftConstant, raeh, metadata);
          }
        }
        else {
          LOGGER.debug("Right Target of Relation {} not contained in Knowledge (yet).", relationId);
          stable = true;
        }
        return stable;
      }
      // both sides of relation are feature values of entities
      final KnowledgeEntity leftEntity = getTargetEntity(r, root, true, raeh, metadata);
      final KnowledgeEntity rightEntity = getTargetEntity(r, root, false, raeh, metadata);
      if ((leftEntity != null) && (rightEntity != null)) {
        LOGGER.debug("Found both Targets of Relation {} --> L = {} ;  R = {}", relationId, shortDisplayKE(leftEntity), shortDisplayKE(rightEntity));
        if (raeh.isActive(r)) {
          final Feature leftParameter = RelationHelper.getFeatureVariable(this.kb, left);
          final Feature rightParameter = RelationHelper.getFeatureVariable(this.kb, right);
          stable = evaluateDoubleSidedRelation(r, leftEntity, leftParameter, op, rightEntity, rightParameter, raeh, metadata);
        }
      }
      else {
        LOGGER.debug("Skipping Relation {}. Either left or right Target not contained in Knowledge (yet).", relationId);
        stable = true;
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRelation(); relationId = {}; root = {}; stable = {}", relationId, shortDisplayKE(root), stable);
    }
  }

  // ----------------------------------------------------------------

  private KnowledgeEntity getTargetEntity(final Relation r, final KnowledgeEntity root, final boolean left, final RulesAndEventsHandler raeh, final Metadata metadata) {
    KnowledgeEntity target = null;
    final String relationId = (r == null ? null : r.getRelationID());
    try {
      LOGGER.trace("--> getTargetEntity(); relationId = {}; root = {}", relationId, shortDisplayKE(root));
      final RelationParameter rp = (left ? r.getLeftSide() : r.getRightSide());
      final List<String> ctx = rp.getContext();
      target = RelationHelper.getTargetEntity(root, ctx); // throws exception if ctx is not possible
      if ((target == null) && LOGGER.isDebugEnabled()) {
        LOGGER.debug("Skipping evaluation of Relation {} because Context of {} Parameter {} is not fulfilled yet.", relationId, (left ? "left" : "right"), rp.getParameterID());
      }
    }
    catch (final Exception ex) {
      target = null;
      LOGGER.debug("Relation {} is irrelevant/obsolete because Context of {} Parameter is not possible!", relationId, (left ? "left" : "right"));
      markRelationObsolete(r, raeh, metadata);
    }
    finally {
      LOGGER.trace("<-- getTargetEntity(); relationId = {}; root = {}; target = {}", relationId, shortDisplayKE(root), shortDisplayKE(target));
    }
    return target;
  }

  // ----------------------------------------------------------------

  private boolean evaluateSingleSidedRelation(final Relation r,
      final KnowledgeEntity leftEntity, final Feature leftParameter,
      final RelationOperator op, final FeatureValue rightConstant,
      final RulesAndEventsHandler raeh, final Metadata metadata) {

    boolean removedValues = false;
    final String relationId = (r == null ? null : r.getRelationID());
    final String featureId = (leftParameter == null ? null : leftParameter.getFeatureID());
    final String constantValue = (rightConstant == null ? null : rightConstant.getValue());
    final String operator = String.valueOf(op);
    try {
      LOGGER.trace("--> evaluateSingleSidedRelation(); Rel = {}; [ {} {} {} ]", relationId, featureId, operator, constantValue);
      // Step 1: check whether a value was already assigned for this feature
      final FeatureValue found = FeatureValueHelper.findFeatureValue(this.kb, leftEntity, featureId);
      boolean matching = (found == null ? true : RelationHelper.fulfillsOperation(found, op, rightConstant));
      if (found != null) {
        FeatureValueHelper.removeImpossibleFeatureValues(leftEntity); // just to be sure
        if (matching) {
          LOGGER.debug("Relation {} is obsolete. KE {} already has the matching Value {} assigned for Feature {}", relationId, shortDisplayKE(leftEntity), found, featureId);
          markRelationObsolete(r, raeh, metadata);
        }
        else {
          LOGGER.debug("Relation {} is unfulfillable. KE {} has the not matching Value {} assigned for Feature {}", relationId, shortDisplayKE(leftEntity), found, featureId);
          markRelationUnFulfillable(r, raeh, metadata);
        }
        removedValues = false;
        return removedValues;
      }
      // Step 2: check possible feature choices
      LOGGER.debug("Checking Feature-Choices of KE {} for Relation {}", shortDisplayKE(leftEntity), relationId);
      for (final FeatureChoice fc : leftEntity.getPossibleFeatures()) {
        if (featureId.equals(fc.getFeatureID())) {
          // this is a choice for the required feature
          final FeatureValues oldFvs = fc.getPossibleValues();
          final int oldSize = (oldFvs == null ? 0 : oldFvs.size());
          if (oldSize > 0) {
            // get all values that are fulfilling the relation
            final FeatureValues newFvs = RelationHelper.fulfillsOperation(this.kb, this.trans, oldFvs, op, rightConstant);
            final int newSize = (newFvs == null ? 0 : newFvs.size());
            if (oldSize != newSize) {
              fc.setPossibleValues(newFvs);
              removedValues = true;
              LOGGER.debug("Applied Relation {} to Feature-Choices of KE {}. Removed {} possible Values: {}", relationId, shortDisplayKE(leftEntity), (oldSize - newSize), fc);
            }
            if (newSize <= 0) {
              LOGGER.debug("Relation {} is unfulfillable. No matching Feature-Choices left for KE {}", relationId, shortDisplayKE(leftEntity));
              markRelationUnFulfillable(r, raeh, metadata);
            }
          }
        }
      }
      // Step 3: check possible concept choices
      LOGGER.debug("Checking Concept-Choices of KE {} for Relation {}", shortDisplayKE(leftEntity), relationId);
      for (final ConceptChoice cc : leftEntity.getPossibleConcepts()) {
        final Concepts oldCons = cc.getConcepts();
        final int oldSize = (oldCons == null ? 0 : oldCons.size());
        if (oldSize > 0) {
          final Concepts newCons = new Concepts();
          for (final Concept con : oldCons) {
            matching = true; // a concept without the desired feature is always matching
            if (con.getFeatureIds().contains(featureId)) {
              // concept does affect the desired feature
              for (final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue fv : con.getValues()) {
                if (featureId.equals(fv.getFeatureID())) {
                  LOGGER.debug("Checking FV {} of Concept {} against {}", fv.getFeatureValueID(), con.getConceptID(), rightConstant.getFeatureValueID());
                  matching = RelationHelper.fulfillsOperation(this.kb, fv, op, rightConstant);
                }
              }
            }
            if (matching) {
              newCons.add(con);
            }
          }
          final int newSize = (newCons == null ? 0 : newCons.size());
          if (oldSize != newSize) {
            cc.setConcepts(newCons);
            removedValues = true;
            LOGGER.debug("Applied Relation {} to Concept-Choices of KE {}. Removed {} possible Concepts: {}", relationId, shortDisplayKE(leftEntity), (oldSize - newSize), cc);
          }
          if (newSize <= 0) {
            LOGGER.debug("Relation {} is unfulfillable. No matching Concept-Choices left for KE {}", relationId, shortDisplayKE(leftEntity));
            markRelationUnFulfillable(r, raeh, metadata);
          }
        }
      }
      return removedValues;
    }
    finally {
      if (removedValues) {
        LOGGER.debug("Removed some Values of KE {} for Relation {}", shortDisplayKE(leftEntity), relationId);
        applyRelation(r, raeh, metadata);
      }
      LOGGER.trace("<-- evaluateSingleSidedRelation(); Rel = {}; removedValues = {}", relationId, removedValues);
    }
  }

  private boolean evaluateDoubleSidedRelation(final Relation r,
      final KnowledgeEntity leftEntity, final Feature leftParameter,
      final RelationOperator op,
      final KnowledgeEntity rightEntity, final Feature rightParameter,
      final RulesAndEventsHandler raeh, final Metadata metadata) {

    boolean removedValues = false;
    final String relationId = (r == null ? null : r.getRelationID());
    final String leftFeatureId = (leftParameter == null ? null : leftParameter.getFeatureID());
    final String rightFeatureId = (rightParameter == null ? null : rightParameter.getFeatureID());
    final String operator = String.valueOf(op);
    try {
      LOGGER.trace("--> evaluateDoubleSidedRelation(); Rel = {}; [ {} {} {} ]", relationId, leftFeatureId, operator, rightFeatureId);
      // Step 1: check whether a value was already assigned for this feature
      final FeatureValue foundLeft = FeatureValueHelper.findFeatureValue(this.kb, leftEntity, leftFeatureId);
      final FeatureValue foundRight = FeatureValueHelper.findFeatureValue(this.kb, rightEntity, rightFeatureId);
      if ((foundLeft == null) && (foundRight != null)) {
        LOGGER.debug("Feature {} has already an assigned Value {} on right Side of Relation {}", rightFeatureId, foundRight.getFeatureValueID(), relationId);
        removedValues = evaluateSingleSidedRelation(r, leftEntity, leftParameter, op, foundRight, raeh, metadata);
        return removedValues;
      }
      if ((foundLeft != null) && (foundRight == null)) {
        LOGGER.debug("Feature {} has already an assigned Value {} on left Side of Relation {}", leftFeatureId, foundLeft.getFeatureValueID(), relationId);
        removedValues = evaluateSingleSidedRelation(r, rightEntity, rightParameter, RelationHelper.getComplementaryOperator(op), foundLeft, raeh, metadata);
        return removedValues;
      }
      if ((foundLeft != null) && (foundRight != null)) {
        FeatureValueHelper.removeImpossibleFeatureValues(leftEntity); // just to be sure
        FeatureValueHelper.removeImpossibleFeatureValues(rightEntity); // just to be sure
        final boolean matching = RelationHelper.fulfillsOperation(foundLeft, op, foundRight);
        if (matching) {
          LOGGER.debug("Relation {} is obsolete. There is already a matching Value assigned on both Sides: {} vs. {}", relationId, foundLeft.getFeatureValueID(), foundRight.getFeatureValueID());
          markRelationObsolete(r, raeh, metadata);
        }
        else {
          LOGGER.debug("Relation {} is unfulfillable. There is already a Value assigned on both Sides, but is not matching: {} vs. {}", relationId, foundLeft.getFeatureValueID(),
              foundRight.getFeatureValueID());
          markRelationUnFulfillable(r, raeh, metadata);
        }
        removedValues = false;
        return removedValues;
      }
      // Step 2: lookup possible feature values of all choices on both sides
      LOGGER.debug("Checking choices of left Entity: {}", shortDisplayKE(leftEntity));
      LOGGER.debug("Checking choices of right Entity: {}", shortDisplayKE(rightEntity));
      final FeatureValues leftChoiceFvs = ChoicesHelper.getFeatureValues(leftEntity, leftFeatureId);
      final int leftChoiceSize = (leftChoiceFvs == null ? 0 : leftChoiceFvs.size());
      final FeatureValues rightChoiceFvs = ChoicesHelper.getFeatureValues(rightEntity, rightFeatureId);
      final int rightChoiceSize = (rightChoiceFvs == null ? 0 : rightChoiceFvs.size());
      final Concepts leftConcepts = ChoicesHelper.getConcepts(leftEntity, leftFeatureId);
      final FeatureValues leftConceptFvs = ChoicesHelper.getFeatureValues(leftConcepts, leftFeatureId);
      final int leftConceptSize = (leftConceptFvs == null ? 0 : leftConceptFvs.size());
      final Concepts rightConcepts = ChoicesHelper.getConcepts(rightEntity, rightFeatureId);
      final FeatureValues rightConceptFvs = ChoicesHelper.getFeatureValues(rightConcepts, rightFeatureId);
      final int rightConceptSize = (rightConceptFvs == null ? 0 : rightConceptFvs.size());
      final boolean foundSomething = ((leftChoiceSize + rightChoiceSize + leftConceptSize + rightConceptSize) > 0);
      if (!foundSomething) {
        LOGGER.debug("Relation {} is unfulfillable! There are no possible Values on neither Side: {} vs. {}", relationId, leftFeatureId, rightFeatureId);
        markRelationUnFulfillable(r, raeh, metadata);
        removedValues = false;
        return removedValues;
      }
      // Step 3: compare all values from both sides against each other
      final FeatureValues allLeftFvs = FeatureValueHelper.combineValues(leftChoiceFvs, leftConceptFvs);
      final int oldLeftSize = (allLeftFvs == null ? 0 : allLeftFvs.size());
      final FeatureValues allRightFvs = FeatureValueHelper.combineValues(rightChoiceFvs, rightConceptFvs);
      final int oldRightSize = (allRightFvs == null ? 0 : allRightFvs.size());
      if (oldLeftSize > 0) {
        LOGGER.debug("Applying Relation {} to left KE {}", relationId, shortDisplayKE(leftEntity));
        final FeatureValues newLeftFvs = RelationHelper.fulfillsOperation(this.kb, this.trans, allLeftFvs, op, allRightFvs);
        final int newLeftSize = (newLeftFvs == null ? 0 : newLeftFvs.size());
        if (oldLeftSize != newLeftSize) {
          removedValues = true;
          KnowledgeEntityHelper.cleanupKnowledgeEntity(leftEntity, newLeftFvs);
          LOGGER.debug("Applied Relation {} to Concept- and Feature-Choices of left KE {}. Removed {} possible Values.", relationId, shortDisplayKE(leftEntity), (oldLeftSize - newLeftSize));
        }
        if (newLeftSize <= 0) {
          LOGGER.debug("Relation {} is unfulfillable. No matching Concept- or Feature-Choices left on left side KE {}", relationId, shortDisplayKE(leftEntity));
          markRelationUnFulfillable(r, raeh, metadata);
        }
      }
      if (oldRightSize > 0) {
        LOGGER.debug("Applying Relation {} to right KE {}", relationId, shortDisplayKE(rightEntity));
        final FeatureValues newRightFvs = RelationHelper.fulfillsOperation(this.kb, this.trans, allRightFvs, RelationHelper.getComplementaryOperator(op), allLeftFvs);
        final int newRightSize = (newRightFvs == null ? 0 : newRightFvs.size());
        if (oldRightSize != newRightSize) {
          removedValues = true;
          KnowledgeEntityHelper.cleanupKnowledgeEntity(rightEntity, newRightFvs);
          LOGGER.debug("Applied Relation {} to Concept- and Feature-Choices of right KE {}. Removed {} possible Values.", relationId, shortDisplayKE(rightEntity), (oldRightSize - newRightSize));
        }
        if (newRightSize <= 0) {
          LOGGER.debug("Relation {} is unfulfillable. No matching Concept- or Feature-Choices left on right side KE {}", relationId, shortDisplayKE(rightEntity));
          markRelationUnFulfillable(r, raeh, metadata);
        }
      }
      return removedValues;
    }
    finally {
      if (removedValues) {
        LOGGER.debug("Removed some Values of either KE {} or KE {} for Relation {}", shortDisplayKE(leftEntity), shortDisplayKE(rightEntity), relationId);
        applyRelation(r, raeh, metadata);
      }
      LOGGER.trace("<-- evaluateDoubleSidedRelation(); Rel = {}; [ {} {} {} ] ; removedValues = {}", relationId, leftFeatureId, operator, rightFeatureId, removedValues);
    }
  }

  // ----------------------------------------------------------------

  private void applyRelation(final Relation r, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("APPLIED: {}", r);
      }
      else {
        LOGGER.debug("APPLIED: {}", r.getRelationID());
      }
      LOGGER.debug(r.getRelationID(), new Exception()); // TODO remove
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_applied";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }

  private void markRelationObsolete(final Relation r, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("OBSOLETE: {}", r);
      }
      else {
        LOGGER.debug("OBSOLETE: {}", r.getRelationID());
      }
      LOGGER.debug(r.getRelationID(), new Exception()); // TODO remove
      raeh.setObsolete(r);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_obsolete";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }

  private void markRelationUnFulfillable(final Relation r, final RulesAndEventsHandler raeh, final Metadata metadata) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("UNFULFILLABLE: {}", r);
      }
      else {
        LOGGER.debug("UNFULFILLABLE: {}", r.getRelationID());
      }
      LOGGER.debug(r.getRelationID(), new Exception()); // TODO remove
      raeh.setUnfulfillable(r);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_unfulfillable";
        final String msg = String.valueOf(r);
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
