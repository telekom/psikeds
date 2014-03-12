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
package org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.RelationPartner;
import org.psikeds.resolutionengine.datalayer.vo.Relations;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Validator checking that Relations are valid and consistent.
 * 
 * @author marco@juliano.de
 * 
 */
public class RelationValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Relations ...");
      validateRelations(kb);
      LOGGER.debug("... finished validating KnowledgeBase regarding Relations ... OK.");
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Relations ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... could not validate KnowledgeBase regarding Relations!", ex);
      throw new ValidationException("Could not validate KnowledgeBase regarding Relations!", ex);
    }
  }

  private void validateRelations(final KnowledgeBase kb) throws ValidationException {
    boolean valid = true;
    try {
      // Note: Relations are optional, i.e. this Node can be null!
      final Relations relations = (kb == null ? null : kb.getRelations());
      final List<Relation> lst = (relations == null ? null : relations.getRelation());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Relation r : lst) {
          // --- Step 1: check basics ---
          final String rid = (r == null ? null : r.getRelationID());
          if (StringUtils.isEmpty(rid)) {
            valid = false;
            LOGGER.warn("Relation has no ID: {}", r);
            continue;
          }
          final String vid = (r == null ? null : r.getVariantID());
          if (StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("Relation is not attached to a Variant: {}", r);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Relation: {}", r);
          }
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !vid.equals(v.getVariantID())) {
            valid = false;
            LOGGER.warn("Variant {} referenced by Relation {} does not exists.", vid, rid);
          }
          final RelationOperator op = r.getOperator();
          if (op == null) {
            valid = false;
            LOGGER.warn("Relation {} contains no Relation-Operator!", rid);
          }
          // --- Step 2: check left and right relation partner ---
          final RelationPartner left = r.getLeftSide();
          final RelationPartner right = r.getRightSide();
          if (!checkRelationPartner(kb, rid, vid, left) || !checkRelationPartner(kb, rid, vid, right)) {
            valid = false;
            continue;
          }
          // --- Step 3: check that both sides of the relation are features of the same type
          final String leftFeatureId = left.getFeatureID();
          final Feature<?> leftFeature = (StringUtils.isEmpty(leftFeatureId) ? null : kb.getFeature(leftFeatureId));
          final String leftValueType = (leftFeature == null ? null : leftFeature.getValueType());
          final String rightFeatureId = right.getFeatureID();
          final Feature<?> rightFeature = (StringUtils.isEmpty(rightFeatureId) ? null : kb.getFeature(rightFeatureId));
          final String rightValueType = (rightFeature == null ? null : rightFeature.getValueType());
          if (StringUtils.isEmpty(leftValueType) || StringUtils.isEmpty(rightValueType) || !rightValueType.equals(leftValueType)) {
            valid = false;
            LOGGER.warn("Left and right side of Relation {} have incompatible Types: {} vs. {}", rid, leftValueType, rightValueType);
          }
        }
      }
    }
    catch (final Exception ex) {
      valid = false;
      throw new ValidationException("Could not validate Relations", ex);
    }
    if (!valid) {
      throw new ValidationException("Invalid Relations in KnowledgeBase!");
    }
  }

  //----------------------------------------------------------------

  private boolean checkRelationPartner(final KnowledgeBase kb, final String relationId, final String rootVariantId, final RelationPartner rp) {
    if (rp == null) {
      LOGGER.warn("Relation-Partner of Relation {} is missing.", relationId);
      return false;
    }
    final String featureId = rp.getFeatureID();
    final Feature<?> feature = (StringUtils.isEmpty(featureId) ? null : kb.getFeature(featureId));
    if (feature == null) {
      LOGGER.warn("Feature {} referenced by Relation-Partner of Relation {} does not exist!", featureId, relationId);
      return false;
    }
    final String type = feature.getValueType();
    if (StringUtils.isEmpty(type)) {
      LOGGER.warn("Feature {} referenced by Relation-Partner of Relation {} has no Value-Type!", featureId, relationId);
      return false;
    }
    final List<String> ctx = rp.getContext();
    if ((ctx == null) || ctx.isEmpty()) {
      LOGGER.warn("Relation-Partner of Relation {} has no Context.", relationId);
      return false;
    }
    return checkContextPath(kb, relationId, rootVariantId, ctx, featureId);
  }

  private boolean checkContextPath(final KnowledgeBase kb, final String relationId, final String rootVariantId, final List<String> ctx, final String featureId) {
    final String lastPathElement = checkContextPath(kb, relationId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      return false;
    }
    final Variant v = kb.getVariant(lastPathElement);
    if (v == null) {
      LOGGER.warn("Last Element {} of Context of Relation-Partner of Relation {} is not a Variant!", lastPathElement, relationId);
      return false;
    }
    final List<String> feats = v.getFeatureIds();
    if ((feats == null) || !feats.contains(featureId)) {
      LOGGER.warn("Feature {} referenced by Relation-Partner of Relation {} is not a valid Feature for Variant {}", featureId, relationId, lastPathElement);
      return false;
    }
    return true;
  }

  private String checkContextPath(final KnowledgeBase kb, final String relationId, final String rootVariantId, final List<String> ctx) {
    // --- Step A: check that first element of context matches to root/parent-variant ---
    String lastPathElement = null;
    String firstPathElement = null;
    try {
      firstPathElement = ctx.get(0);
    }
    catch (final Exception ex) {
      firstPathElement = null;
    }
    if (StringUtils.isEmpty(rootVariantId) || StringUtils.isEmpty(firstPathElement) || !rootVariantId.equals(firstPathElement)) {
      LOGGER.warn("Context of Relation-Partner of Relation {} does not start with Root-Variant-ID: {}", relationId, rootVariantId);
      return null;
    }
    // --- Step B: walk along the path and check consistency ---
    boolean variant = true;
    Object previous = null;
    for (final String id : ctx) {
      lastPathElement = id;
      if (StringUtils.isEmpty(id)) {
        LOGGER.warn("Context of Relation-Partner of Relation {} contains an empty ID!", relationId);
        return null;
      }
      if (variant) {
        final Variant v = kb.getVariant(id);
        if ((v == null) || !id.equals(v.getVariantID())) {
          LOGGER.warn("Context of Relation-Partner of Relation {} contains unknown Variant-ID: {}", relationId, id);
          return null;
        }
        if (previous != null) {
          final Purpose p = (Purpose) previous;
          final String pid = (p == null ? null : p.getPurposeID());
          if (!kb.isFulfilledBy(pid, id)) {
            LOGGER.warn("Context of Relation-Partner of Relation {} contains illegal Edge from {} to {}", relationId, pid, id);
            return null;
          }
        }
        previous = v;
      }
      else {
        final Purpose p = kb.getPurpose(id);
        if ((p == null) || !id.equals(p.getPurposeID())) {
          LOGGER.warn("Context of Relation-Partner of Relation {} contains unknown Purpose-ID: {}", relationId, id);
          return null;
        }
        if (previous != null) {
          final Variant v = (Variant) previous;
          final String vid = (v == null ? null : v.getVariantID());
          if (!kb.isConstitutedBy(vid, id)) {
            LOGGER.warn("Context of Relation-Partner of Relation {} contains illegal Edge from {} to {}", relationId, vid, id);
            return null;
          }
        }
        previous = p;
      }
      variant = !variant;
    }
    // --- Done! ---
    return lastPathElement;
  }
}
