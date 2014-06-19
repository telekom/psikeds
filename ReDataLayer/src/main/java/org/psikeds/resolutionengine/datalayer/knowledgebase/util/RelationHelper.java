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
package org.psikeds.resolutionengine.datalayer.knowledgebase.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Helper for handling Relations and Parameters.
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class RelationHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationHelper.class);

  private RelationHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static boolean checkRelationParameter(final KnowledgeBase kb, final Relation rel, final boolean left) {
    final String relationId = (rel == null ? null : rel.getRelationID());
    if (StringUtils.isEmpty(relationId)) {
      LOGGER.warn("Illegal Relation: {}", rel);
      return false;
    }
    return checkRelationParameter(kb, relationId, rel.getVariantID(), (left ? rel.getLeftSide() : rel.getRightSide()));
  }

  public static boolean checkRelationParameter(final KnowledgeBase kb, final String relationId, final String rootVariantId, final RelationParameter rp) {
    final String parameterID = (rp == null ? null : rp.getParameterID());
    if (StringUtils.isEmpty(parameterID)) {
      LOGGER.warn("Relation {} has illegal Relation-Parameter: {}", relationId, rp);
      return false;
    }
    // double check parameter
    final RelationParameter lookup = kb.getRelationParameter(parameterID);
    if ((lookup == null) || !parameterID.equals(lookup.getParameterID())) {
      LOGGER.warn("Relation-Parameter {} referenced by Relation {} does not exist!", parameterID, relationId);
      return false;
    }
    final String paramValue = rp.getParameterValue();
    if (StringUtils.isEmpty(paramValue)) {
      LOGGER.warn("Relation-Parameter {} of Relation {} has no Value.", paramValue, relationId);
      return false;
    }
    // constants are just references to feature values
    if (rp.isConstant()) {
      final FeatureValue fv = kb.getFeatureValue(paramValue);
      if ((fv == null) || !paramValue.equals(fv.getFeatureValueID())) {
        LOGGER.warn("Constant Feature-Value {} referenced by Relation-Parameter {} of Relation {} does not exist!", paramValue, parameterID, relationId);
        return false;
      }
      return true;
    }
    // variable parameters are references to features with a given context
    final Feature f = kb.getFeature(paramValue);
    if ((f == null) || !paramValue.equals(f.getFeatureID())) {
      LOGGER.warn("Variable Feature-ID {} referenced by Relation-Parameter {} of Relation {} does not exist!", paramValue, parameterID, relationId);
      return false;
    }
    final List<String> ctx = rp.getContext();
    if ((ctx == null) || ctx.isEmpty()) {
      LOGGER.warn("Relation-Parameter {} of Relation {} has no Context-Path.", parameterID, relationId);
      return false;
    }
    return checkContextPath(kb, relationId, rootVariantId, ctx, paramValue);
  }

  // ----------------------------------------------------------------

  public static boolean checkContextPath(final KnowledgeBase kb, final Relation rel, final boolean left) {
    final String relationId = (rel == null ? null : rel.getRelationID());
    if (StringUtils.isEmpty(relationId)) {
      LOGGER.warn("Illegal Relation: {}", rel);
      return false;
    }
    return checkContextPath(kb, relationId, rel.getVariantID(), (left ? rel.getLeftSide() : rel.getRightSide()));
  }

  public static boolean checkContextPath(final KnowledgeBase kb, final String relationId, final String rootVariantId, final RelationParameter rp) {
    // context exists only for features, i.e. non-constant-parameters
    final String paramValue = ((rp == null) || rp.isConstant()) ? null : rp.getParameterValue();
    final Feature f = StringUtils.isEmpty(paramValue) ? null : kb.getFeature(paramValue);
    final String featureId = f == null ? null : f.getFeatureID(); // unknown feature
    if (StringUtils.isEmpty(featureId)) {
      LOGGER.warn("Illegal Relation-Parameter: {}", rp);
      return false;
    }
    return checkContextPath(kb, relationId, rootVariantId, rp.getContext(), featureId);
  }

  private static boolean checkContextPath(final KnowledgeBase kb, final String relationId, final String rootVariantId, final List<String> ctx, final String featureId) {
    final String lastPathElement = checkContextPath(kb, relationId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      // some error occured and was already logged
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

  private static String checkContextPath(final KnowledgeBase kb, final String relationId, final String rootVariantId, final List<String> ctx) {
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

  // ----------------------------------------------------------------

  public static boolean isCompatible(final KnowledgeBase kb, final Relation rel) {
    final RelationParameter rp1 = (rel == null ? null : rel.getLeftSide());
    final RelationParameter rp2 = (rel == null ? null : rel.getRightSide());
    return isCompatible(kb, rp1, rp2);
  }

  public static boolean isCompatible(final KnowledgeBase kb, final RelationParameter rp1, final RelationParameter rp2) {
    final String type1 = getType(kb, rp1);
    final String type2 = getType(kb, rp2);
    return FeatureValueHelper.isCompatible(type1, type2);
  }

  private static String getType(final KnowledgeBase kb, final RelationParameter rp) {
    String type = null;
    try {
      if (rp.isConstant()) {
        final FeatureValue fv = kb.getFeatureValue(rp.getParameterValue());
        type = fv.getType();
      }
      else {
        final Feature f = kb.getFeature(rp.getParameterValue());
        type = f.getType();
      }
    }
    catch (final Exception ex) {
      LOGGER.warn("Cannot determine Type of Relation-Partner: " + String.valueOf(rp), ex);
      type = null;
    }
    return type;
  }
}
