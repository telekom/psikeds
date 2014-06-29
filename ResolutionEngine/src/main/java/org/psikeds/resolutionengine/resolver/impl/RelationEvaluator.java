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
import org.psikeds.resolutionengine.resolver.SessionState;
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
   * @param state
   *          current State of Resolution-Session
   * @param decission
   *          made Decission (can be null)
   * @return Knowledge
   *         resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final SessionState state, final Decission decission) throws ResolutionException {
    boolean ok = false;
    boolean stable = true;
    final RulesAndEventsHandler raeh = (state == null ? null : state.getRaeh());
    final Knowledge knowledge = (state == null ? null : state.getKnowledge());
    try {
      LOGGER.debug("Evaluating Relations ...");
      if ((knowledge == null) || (raeh == null)) {
        throw new ResolutionException("Illegal Session-State!");
      }
      // Check Relations, i.e. apply or expire Relations depending on their Context and Condition
      stable = checkRelations(knowledge, state);
      if (!stable) {
        // A Relation was applied and it is neccessary to execute the full Resolver-Chain once again
        LOGGER.debug("Knowledge is not stable, need another Iteration of all Resolvers!");
        knowledge.setStable(false);
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
      LOGGER.warn("Technical Error while evaluating Relations: " + ex.getMessage(), ex);
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

  private boolean checkRelations(final Knowledge knowledge, final SessionState state) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRelations()");
      final RulesAndEventsHandler raeh = state.getRaeh();
      final List<Relation> rels = raeh.getActiveRelations();
      for (final Relation r : rels) {
        final String relationId = (r == null ? null : r.getRelationID());
        final String rootVariantId = (r == null ? null : r.getVariantID());
        if (StringUtils.isEmpty(relationId) || StringUtils.isEmpty(rootVariantId)) {
          final String msg = "Invalid Relation: " + relationId;
          LOGGER.debug(msg);
          markRelationObsolete(r, state);
          throw new ResolutionException(msg);
        }
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Checking Relation: {}", r);
        }
        else if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Checking Relation: {}", relationId);
        }
        // check pre-Condition
        if (r.isConditional()) {
          final String ceid = r.getConditionalEventID();
          if (raeh.isObsolete(ceid)) {
            LOGGER.debug("Conditional Event {} of Relation {} is obsolete. Relation is therefore also obsolete.", ceid, relationId);
            markRelationObsolete(r, state);
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
          if (!checkRelation(r, root, state)) {
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

  private boolean checkRelation(final Relation r, final KnowledgeEntities root, final SessionState state) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRelation(); Relation = {}", r.getRelationID());
      for (final KnowledgeEntity ke : root) {
        if (!state.getRaeh().isActive(r)) {
          // Fail fast: Relation was already made obsolete
          return stable;
        }
        if (!checkRelation(r, ke, state)) {
          stable = false;
        }
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRelation(); stable = {}", stable);
    }
  }

  private boolean checkRelation(final Relation r, final KnowledgeEntity root, final SessionState state) {
    boolean stable = true;
    try {
      LOGGER.trace("--> checkRelation(); Relation = {}; root = {}", r.getRelationID(), shortDisplayKE(root));
      final RulesAndEventsHandler raeh = state.getRaeh();
      final RelationParameter left = r.getLeftSide();
      LOGGER.trace("Left RelationParameter = {}", left);
      final RelationParameter right = r.getRightSide();
      LOGGER.trace("Right RelationParameter = {}", right);
      if ((left == null) || (right == null)) {
        // no parameters? does not make sense!
        LOGGER.warn("Ignoring Relation {} because Parameters are missing!", r.getRelationID());
        markRelationObsolete(r, state);
        stable = true;
        return stable;
      }
      if (left.isConstant() && right.isConstant()) {
        // both sides are constants? does not make sense!
        LOGGER.warn("Ignoring Relation {} because both Parameters are Constants!", r.getRelationID());
        markRelationObsolete(r, state);
        stable = true;
        return stable;
      }
      final RelationOperator op = r.getOperator();
      LOGGER.debug("Operator of Relation {} is: {}", r.getRelationID(), op);
      if (!left.isConstant() && right.isConstant()) {
        // left side is a feature of an entity, right side is a constant
        final KnowledgeEntity leftEntity = getTargetEntity(r, root, true, state);
        if (leftEntity != null) {
          LOGGER.debug("Right side is a Constant, found left Target of Relation {} --> {}", r.getRelationID(), shortDisplayKE(leftEntity));
          if (raeh.isActive(r)) {
            final Feature leftParameter = RelationHelper.getFeatureVariable(this.kb, left);
            LOGGER.trace("Left Parameter = {}", leftParameter);
            final FeatureValue rightConstant = RelationHelper.getConstantFeatureValue(this.kb, right);
            LOGGER.trace("Right Constant = {}", rightConstant);
            if (evaluateSingleSidedRelation(r, leftEntity, leftParameter, op, rightConstant, state)) {
              stable = false;
              LOGGER.debug("Relation {} removed some Values/Choices of left Entity {}", r.getRelationID(), shortDisplayKE(leftEntity));
            }
          }
        }
        else {
          LOGGER.debug("Left Target of Relation {} is not contained in Knowledge (yet).", r.getRelationID());
          stable = true;
        }
        return stable;
      }
      if (left.isConstant() && !right.isConstant()) {
        // right side is a feature of an entity, left side is a constant
        final KnowledgeEntity rightEntity = getTargetEntity(r, root, false, state);
        if (rightEntity != null) {
          LOGGER.debug("Left side is a Constant, found right Target of Relation {} --> {}", r.getRelationID(), shortDisplayKE(rightEntity));
          if (raeh.isActive(r)) {
            final Feature rightParameter = RelationHelper.getFeatureVariable(this.kb, right);
            LOGGER.trace("Right Parameter = {}", rightParameter);
            final FeatureValue leftConstant = RelationHelper.getConstantFeatureValue(this.kb, left);
            LOGGER.trace("Left Constant = {}", leftConstant);
            // switch sides, invert operator and handle like the case above
            final RelationOperator comp = RelationHelper.getComplementaryOperator(op);
            LOGGER.debug("Changing Operator from {} to {}", op, comp);
            if (evaluateSingleSidedRelation(r, rightEntity, rightParameter, comp, leftConstant, state)) {
              stable = false;
              LOGGER.debug("Relation {} removed some Values/Choices of right Entity {}", r.getRelationID(), shortDisplayKE(rightEntity));
            }
          }
        }
        else {
          LOGGER.debug("Right Target of Relation {} is not contained in Knowledge (yet).", r.getRelationID());
          stable = true;
        }
        return stable;
      }
      // both sides of relation are feature values of entities
      final KnowledgeEntity leftEntity = getTargetEntity(r, root, true, state);
      final KnowledgeEntity rightEntity = getTargetEntity(r, root, false, state);
      if ((leftEntity != null) && (rightEntity != null)) {
        LOGGER.debug("Both sides are Variables, found both Targets of Relation {} --> L = {} ;  R = {}", r.getRelationID(), shortDisplayKE(leftEntity), shortDisplayKE(rightEntity));
        if (raeh.isActive(r)) {
          final Feature leftParameter = RelationHelper.getFeatureVariable(this.kb, left);
          LOGGER.trace("Left Parameter = {}", leftParameter);
          final Feature rightParameter = RelationHelper.getFeatureVariable(this.kb, right);
          LOGGER.trace("Right Parameter = {}", rightParameter);
          if (evaluateDoubleSidedRelation(r, leftEntity, leftParameter, op, rightEntity, rightParameter, state)) {
            stable = false;
            LOGGER.debug("Relation {} removed some Values/Choices of either left Entity {} or right Entity {}", r.getRelationID(), shortDisplayKE(leftEntity), shortDisplayKE(rightEntity));
          }
        }
      }
      else {
        LOGGER.debug("Skipping Relation {}. Either left or right Target is not contained in Knowledge (yet): L = {} ;  R = {}",
            r.getRelationID(), shortDisplayKE(leftEntity), shortDisplayKE(rightEntity));
        stable = true;
      }
      // done
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRelation(); Relation = {}; root = {}; stable = {}", r.getRelationID(), shortDisplayKE(root), stable);
    }
  }

  // ----------------------------------------------------------------

  private KnowledgeEntity getTargetEntity(final Relation r, final KnowledgeEntity root, final boolean left, final SessionState state) {
    KnowledgeEntity target = null;
    RelationParameter rp = null;
    List<String> ctx = null;
    try {
      LOGGER.trace("--> getTargetEntity(); Relation = {}; root = {}", r.getRelationID(), shortDisplayKE(root));
      rp = (left ? r.getLeftSide() : r.getRightSide());
      ctx = rp.getContext();
      target = RelationHelper.getTargetEntity(root, ctx); // throws exception if ctx is not possible
      if ((target == null) && LOGGER.isDebugEnabled()) {
        LOGGER.debug("Skipping evaluation of Relation {} because Context {} of {} Parameter {} is not fulfilled yet.", r.getRelationID(), ctx, (left ? "left" : "right"), rp.getParameterID());
      }
    }
    catch (final Exception ex) {
      target = null;
      LOGGER.debug("Relation {} is irrelevant/obsolete because Context {} of {} Parameter {} is not possible!", r.getRelationID(), ctx, (left ? "left" : "right"), rp.getParameterID());
      markRelationObsolete(r, state);
    }
    finally {
      LOGGER.trace("<-- getTargetEntity(); Relation = {}; root = {}; target = {}", r.getRelationID(), shortDisplayKE(root), shortDisplayKE(target));
    }
    return target;
  }

  // ----------------------------------------------------------------

  private boolean evaluateSingleSidedRelation(final Relation r,
      final KnowledgeEntity leftEntity, final Feature leftParameter,
      final RelationOperator op, final FeatureValue rightConstant,
      final SessionState state) {

    boolean removedValues = false;
    final String relationId = (r == null ? null : r.getRelationID());
    final String featureId = (leftParameter == null ? null : leftParameter.getFeatureID());
    final String constantValue = (rightConstant == null ? null : rightConstant.getValue());
    final String operator = String.valueOf(op);
    try {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("--> evaluateSingleSidedRelation(); Relation = {}; [ {}/{} {} {} ]", relationId, shortDisplayKE(leftEntity), featureId, operator, constantValue);
      }
      else {
        LOGGER.debug("Evaluating single sided Relation = {}; [ {}/{} {} {} ]", relationId, shortDisplayKE(leftEntity), featureId, operator, constantValue);
      }
      // Step 1: check whether a value was already assigned for this feature
      final FeatureValue found = FeatureValueHelper.findFeatureValue(this.kb, leftEntity, featureId);
      LOGGER.trace("Found = {}", found);
      boolean matching = (found == null ? true : RelationHelper.fulfillsOperation(found, op, rightConstant));
      if (found != null) {
        if (matching) {
          LOGGER.debug("Relation {} is obsolete. KE {} already has the matching Value {} assigned for Feature {}", relationId, shortDisplayKE(leftEntity), found, featureId);
          markRelationObsolete(r, state);
        }
        else {
          LOGGER.debug("Relation {} is unfulfillable. KE {} has the not matching Value {} assigned for Feature {}", relationId, shortDisplayKE(leftEntity), found, featureId);
          markRelationUnFulfillable(r, state);
        }
        removedValues = FeatureValueHelper.removeImpossibleFeatureValues(leftEntity); // just to be sure
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
            LOGGER.trace("{} old Feature-Values = {}", oldSize, oldFvs);
            // get all values that are fulfilling the relation
            LOGGER.debug("Checking possible Feature-Values of KE {} against {} {} = {}", shortDisplayKE(leftEntity), operator, rightConstant.getFeatureValueID(), rightConstant.getValue());
            final FeatureValues newFvs = RelationHelper.fulfillsOperation(this.kb, this.trans, oldFvs, op, rightConstant);
            final int newSize = (newFvs == null ? 0 : newFvs.size());
            LOGGER.trace("{} new Feature-Values = {}", newSize, newFvs);
            if (oldSize != newSize) {
              fc.setPossibleValues(newFvs);
              removedValues = true;
              LOGGER.debug("Applied Relation {} to Feature-Choices of KE {}. Removed {} possible Values: {}", relationId, shortDisplayKE(leftEntity), (oldSize - newSize), fc);
            }
            if (newSize <= 0) {
              LOGGER.debug("Relation {} is unfulfillable. No matching Feature-Choices left for KE {}", relationId, shortDisplayKE(leftEntity));
              markRelationUnFulfillable(r, state);
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
          LOGGER.trace("{} old Concepts = {}", oldSize, oldCons);
          final Concepts newCons = new Concepts();
          for (final Concept con : oldCons) {
            matching = true;
            if (con.getFeatureIds().contains(featureId)) {
              LOGGER.debug("Concept {} contains the desired Feature {}", con.getConceptID(), featureId);
              for (final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue fv : con.getValues()) {
                if (featureId.equals(fv.getFeatureID())) {
                  LOGGER.debug("Checking FV {}/{} = {} of Concept {} against {} {}/{} = {}",
                      fv.getFeatureID(), fv.getFeatureValueID(), fv.getValue(),
                      con.getConceptID(), operator,
                      rightConstant.getFeatureID(), rightConstant.getFeatureValueID(), rightConstant.getValue());
                  matching = RelationHelper.fulfillsOperation(this.kb, fv, op, rightConstant);
                  LOGGER.trace("{} {} {} = {}", fv, operator, rightConstant, matching);
                }
              }
            }
            else {
              LOGGER.debug("Concept {} is not relevant for Feature {}", con.getConceptID(), featureId);
              matching = true; // a concept without the desired feature is always matching, i.e. must not be removed
            }
            if (matching) {
              LOGGER.trace("Concept {} is matching.", con.getConceptID());
              newCons.add(con);
            }
            else {
              LOGGER.trace("Concept {} is not matching and will be removed.", con.getConceptID());
            }
          }
          final int newSize = (newCons == null ? 0 : newCons.size());
          LOGGER.trace("{} new Concepts = {}", newSize, newCons);
          if (oldSize != newSize) {
            cc.setConcepts(newCons);
            removedValues = true;
            LOGGER.debug("Applied Relation {} to Concept-Choices of KE {}. Removed {} possible Concepts: {}", relationId, shortDisplayKE(leftEntity), (oldSize - newSize), cc);
          }
          if (newSize <= 0) {
            LOGGER.debug("Relation {} is unfulfillable. No matching Concept-Choices left for KE {}", relationId, shortDisplayKE(leftEntity));
            markRelationUnFulfillable(r, state);
          }
        }
      }
      return removedValues;
    }
    finally {
      if (removedValues) {
        LOGGER.debug("Single sided Evaluation of Relation {} removed some possible Values of {}/{}", relationId, shortDisplayKE(leftEntity), featureId);
        applyRelation(r, state);
      }
      LOGGER.trace("<-- evaluateSingleSidedRelation(); Relation = {}; [ {}/{} {} {} ]; removedValues = {}; Resulting KE = {}",
          relationId, shortDisplayKE(leftEntity), featureId, operator, constantValue, removedValues, leftEntity);
    }
  }

  private boolean evaluateDoubleSidedRelation(final Relation r,
      final KnowledgeEntity leftEntity, final Feature leftParameter,
      final RelationOperator op,
      final KnowledgeEntity rightEntity, final Feature rightParameter,
      final SessionState state) {

    boolean removedValues = false;
    final String relationId = (r == null ? null : r.getRelationID());
    final String leftFeatureId = (leftParameter == null ? null : leftParameter.getFeatureID());
    final String rightFeatureId = (rightParameter == null ? null : rightParameter.getFeatureID());
    final String operator = String.valueOf(op);
    try {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("--> evaluateDoubleSidedRelation(); Relation = {}; [ {}/{} {} {}/{} ]",
            relationId, shortDisplayKE(leftEntity), leftFeatureId, operator, shortDisplayKE(rightEntity), rightFeatureId);
      }
      else {
        LOGGER.debug("Evaluating double sided Relation = {}; [ {}/{} {} {}/{} ]",
            relationId, shortDisplayKE(leftEntity), leftFeatureId, operator, shortDisplayKE(rightEntity), rightFeatureId);
      }
      // Step 1: check whether a value was already assigned for this feature
      final FeatureValue foundLeft = FeatureValueHelper.findFeatureValue(this.kb, leftEntity, leftFeatureId);
      LOGGER.trace("Found Left = {}", foundLeft);
      final FeatureValue foundRight = FeatureValueHelper.findFeatureValue(this.kb, rightEntity, rightFeatureId);
      LOGGER.trace("Found Right = {}", foundRight);
      if ((foundLeft == null) && (foundRight != null)) {
        LOGGER.debug("Feature {} has already an assigned Value {} = {} on right Side {} of Relation {}. Switching to single sided Evaluation. Operator remains {}",
            rightFeatureId, foundRight.getFeatureValueID(), foundRight.getValue(), shortDisplayKE(rightEntity), relationId, operator);
        removedValues = evaluateSingleSidedRelation(r, leftEntity, leftParameter, op, foundRight, state);
        return removedValues;
      }
      if ((foundLeft != null) && (foundRight == null)) {
        final RelationOperator comp = RelationHelper.getComplementaryOperator(op);
        LOGGER.debug("Feature {} has already an assigned Value {} = {} on left Side {} of Relation {}. Switching to complementary single sided Evaluation. Operator changes from {} to {}",
            leftFeatureId, foundLeft.getFeatureValueID(), foundLeft.getValue(), shortDisplayKE(leftEntity), relationId, operator, String.valueOf(comp));
        removedValues = evaluateSingleSidedRelation(r, rightEntity, rightParameter, comp, foundLeft, state);
        return removedValues;
      }
      if ((foundLeft != null) && (foundRight != null)) {
        final boolean matching = RelationHelper.fulfillsOperation(foundLeft, op, foundRight);
        if (matching) {
          LOGGER.debug("Relation {} is obsolete. There is already a matching Value assigned on both Sides: {}/{} = {} and {}/{} = {}",
              relationId, shortDisplayKE(leftEntity), leftFeatureId, foundLeft.getFeatureValueID(), shortDisplayKE(rightEntity), rightFeatureId, foundRight.getFeatureValueID());
          markRelationObsolete(r, state);
        }
        else {
          LOGGER.debug("Relation {} is unfulfillable. There is already a Value assigned on both Sides, but they are not matching: {}/{} = {} vs. {}/{} = {}",
              relationId, shortDisplayKE(leftEntity), leftFeatureId, foundLeft.getFeatureValueID(), shortDisplayKE(rightEntity), rightFeatureId, foundRight.getFeatureValueID());
          markRelationUnFulfillable(r, state);
        }
        // nothing happened
        removedValues = false;
        return removedValues;
      }
      // Step 2: lookup possible feature values of all choices on both sides
      LOGGER.debug("Getting choices of left Entity {} and right Entity {}", shortDisplayKE(leftEntity), shortDisplayKE(rightEntity));
      final FeatureValues leftChoiceFvs = ChoicesHelper.getFeatureValues(leftEntity, leftFeatureId);
      final int leftChoiceSize = (leftChoiceFvs == null ? 0 : leftChoiceFvs.size());
      LOGGER.trace("{} choosable Feature-Values on left Side: {}", leftChoiceSize, leftChoiceFvs);
      final FeatureValues rightChoiceFvs = ChoicesHelper.getFeatureValues(rightEntity, rightFeatureId);
      final int rightChoiceSize = (rightChoiceFvs == null ? 0 : rightChoiceFvs.size());
      LOGGER.trace("{} choosable Feature-Values on right Side: {}", rightChoiceSize, rightChoiceFvs);
      final Concepts leftConcepts = ChoicesHelper.getConcepts(leftEntity, leftFeatureId);
      final FeatureValues leftConceptFvs = ChoicesHelper.getFeatureValues(leftConcepts, leftFeatureId);
      final int leftConceptSize = (leftConceptFvs == null ? 0 : leftConceptFvs.size());
      LOGGER.trace("{} relevant Concepts on left Side. Feature-Values = {}", leftConceptSize, leftConceptFvs);
      final Concepts rightConcepts = ChoicesHelper.getConcepts(rightEntity, rightFeatureId);
      final FeatureValues rightConceptFvs = ChoicesHelper.getFeatureValues(rightConcepts, rightFeatureId);
      final int rightConceptSize = (rightConceptFvs == null ? 0 : rightConceptFvs.size());
      LOGGER.trace("{} relevant Concepts on right Side. Feature-Values = {}", rightConceptSize, rightConceptFvs);
      final boolean foundSomething = ((leftChoiceSize + rightChoiceSize + leftConceptSize + rightConceptSize) > 0);
      if (!foundSomething) {
        LOGGER.debug("Relation {} is unfulfillable! There are no possible Values on neither Side: {}/{} vs. {}/{}",
            relationId, shortDisplayKE(leftEntity), leftFeatureId, shortDisplayKE(rightEntity), rightFeatureId);
        markRelationUnFulfillable(r, state);
        // nothing happened
        removedValues = false;
        return removedValues;
      }
      // Step 3: compare all values from both sides against each other
      final FeatureValues allLeftFvs = FeatureValueHelper.combineValues(leftChoiceFvs, leftConceptFvs);
      final int oldLeftSize = (allLeftFvs == null ? 0 : allLeftFvs.size());
      LOGGER.debug("Total of {} old combined Feature-Values on left Side:\n{}", oldLeftSize, allLeftFvs);
      final FeatureValues allRightFvs = FeatureValueHelper.combineValues(rightChoiceFvs, rightConceptFvs);
      final int oldRightSize = (allRightFvs == null ? 0 : allRightFvs.size());
      LOGGER.debug("Total of {} old combined Feature-Values on right Side:\n{}", oldRightSize, allRightFvs);
      if (oldLeftSize > 0) {
        LOGGER.debug("Applying Relation {} to left KE {}", relationId, shortDisplayKE(leftEntity));
        final FeatureValues newLeftFvs = RelationHelper.fulfillsOperation(this.kb, this.trans, allLeftFvs, op, allRightFvs);
        final int newLeftSize = (newLeftFvs == null ? 0 : newLeftFvs.size());
        LOGGER.debug("Total of {} new matching Feature-Values on left Side:\n{}", newLeftSize, newLeftFvs);
        if (oldLeftSize != newLeftSize) {
          removedValues = true;
          KnowledgeEntityHelper.cleanupKnowledgeEntity(leftEntity, leftFeatureId, newLeftFvs);
          LOGGER.debug("Applied Relation {} to Concept- and Feature-Choices regarding Feature {} of left KE {}. Removed {} possible Values.", relationId, leftFeatureId, shortDisplayKE(leftEntity),
              (oldLeftSize - newLeftSize));
          LOGGER.trace("Resulting left KE = {}", leftEntity);
        }
        if (newLeftSize <= 0) {
          LOGGER.debug("Relation {} is unfulfillable. No matching Concept- or Feature-Choices regarding Feature {} left on left side KE {}", relationId, leftFeatureId, shortDisplayKE(leftEntity));
          markRelationUnFulfillable(r, state);
        }
      }
      else {
        LOGGER.debug("No relevant Feature-Values for Relation {} on left Side {}/{}", relationId, shortDisplayKE(leftEntity), leftFeatureId);
      }
      if (oldRightSize > 0) {
        LOGGER.debug("Applying Relation {} to right KE {}", relationId, shortDisplayKE(rightEntity));
        final FeatureValues newRightFvs = RelationHelper.fulfillsOperation(this.kb, this.trans, allRightFvs, RelationHelper.getComplementaryOperator(op), allLeftFvs);
        final int newRightSize = (newRightFvs == null ? 0 : newRightFvs.size());
        LOGGER.debug("Total of {} new matching Feature-Values on right Side:\n{}", newRightSize, newRightFvs);
        if (oldRightSize != newRightSize) {
          removedValues = true;
          KnowledgeEntityHelper.cleanupKnowledgeEntity(rightEntity, rightFeatureId, newRightFvs);
          LOGGER.debug("Applied Relation {} to Concept- and Feature-Choices regarding Feature {} of right KE {}. Removed {} possible Values.", relationId, rightFeatureId, shortDisplayKE(rightEntity),
              (oldRightSize - newRightSize));
          LOGGER.trace("Resulting right KE = {}", rightEntity);
        }
        if (newRightSize <= 0) {
          LOGGER.debug("Relation {} is unfulfillable. No matching Concept- or Feature-Choices regarding Feature {} left on right side KE {}", relationId, rightFeatureId, shortDisplayKE(rightEntity));
          markRelationUnFulfillable(r, state);
        }
      }
      else {
        LOGGER.debug("No relevant Feature-Values for Relation {} on right Side {}/{}", relationId, shortDisplayKE(rightEntity), rightFeatureId);
      }
      return removedValues;
    }
    finally {
      if (removedValues) {
        LOGGER.debug("Double sided Evaluation of Relation {} removed some possible Values of {}/{} and {}/{}",
            relationId, shortDisplayKE(leftEntity), leftFeatureId, shortDisplayKE(rightEntity), rightFeatureId);
        applyRelation(r, state);
      }
      LOGGER.trace("<-- evaluateDoubleSidedRelation(); Relation = {}; [ {}/{} {} {}/{} ]; removedValues = {}\nResulting lef Side = {}\nResulting right Side = {}",
          relationId, shortDisplayKE(leftEntity), leftFeatureId, operator, shortDisplayKE(rightEntity), rightFeatureId, removedValues, leftEntity, rightEntity);
    }
  }

  // ----------------------------------------------------------------

  private void applyRelation(final Relation r, final SessionState state) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("APPLIED: {}", r);
      }
      else {
        LOGGER.debug("APPLIED: {}", r.getRelationID());
      }
      final Metadata metadata = (state == null ? null : state.getMetadata());
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_applied";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }

  private void markRelationObsolete(final Relation r, final SessionState state) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("OBSOLETE: {}", r);
      }
      else {
        LOGGER.debug("OBSOLETE: {}", r.getRelationID());
      }
      final RulesAndEventsHandler raeh = state.getRaeh();
      raeh.setObsolete(r);
      final Metadata metadata = state.getMetadata();
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_obsolete";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }

  private void markRelationUnFulfillable(final Relation r, final SessionState state) {
    if (r != null) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("UNFULFILLABLE: {}", r);
      }
      else {
        LOGGER.debug("UNFULFILLABLE: {}", r.getRelationID());
      }
      final RulesAndEventsHandler raeh = state.getRaeh();
      raeh.setUnfulfillable(r);
      final Metadata metadata = state.getMetadata();
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_unfulfillable";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
      state.addWarning("Relation " + r.getRelationID() + " is unfulfillable!");
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
