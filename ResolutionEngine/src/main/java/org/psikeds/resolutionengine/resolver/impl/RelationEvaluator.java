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
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;
import org.psikeds.resolutionengine.util.KnowledgeHelper;

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
      // Knowledge is clean so far
      raeh.setKnowledgeDirty(false);
      // Check Relations, i.e. apply or expire Relations depending on their Context and Condition
      stable = checkRelations(knowledge, raeh, metadata);
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
          markRelationObsolete(r, raeh, metadata);
          throw new ResolutionException("Invalid Relation!");
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
        }
        // check root / nexus
        final KnowledgeEntities root = KnowledgeHelper.findRoot(r, knowledge);
        if ((root == null) || root.isEmpty()) {
          LOGGER.debug("Nexus {} of Relation {} is not included in the current Knowledge yet. Skipping Evaluation.", rootVariantId, relationId);
          continue;
        }
        // evaluate relation
        // TODO
        stable = true;
      }
      return stable;
    }
    finally {
      LOGGER.trace("<-- checkRelations(); stable = {}", stable);
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
      raeh.setKnowledgeDirty(true);
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
      raeh.setObsolete(r);
      if ((metadata != null) && LOGGER.isInfoEnabled()) {
        final String key = "R_" + r.getRelationID() + "_obsolete";
        final String msg = String.valueOf(r);
        metadata.addInfo(key, msg);
      }
    }
  }
}
