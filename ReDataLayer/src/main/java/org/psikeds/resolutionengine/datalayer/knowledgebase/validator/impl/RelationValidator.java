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
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.Relations;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Basic Validator checking that Relations are valid.
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
          final String rid = (r == null ? null : r.getId());
          final String vid = (r == null ? null : r.getVariantID());
          final RelationOperator op = (r == null ? null : r.getOperator());
          if (StringUtils.isEmpty(rid) || StringUtils.isEmpty(vid) || (op == null)) {
            valid = false;
            LOGGER.warn("Relation contains no or unknown IDs: {}", r);
            continue;
          }
          else if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Relation: {}", r);
          }
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !vid.equals(v.getId())) {
            valid = false;
            LOGGER.warn("Variant {} referenced by Relation {} does not exists.", vid, rid);
          }
          // --- Step 2: check left side of operation/relation ---
          final String left = r.getLeftPartnerEventID();
          final Event le = (StringUtils.isEmpty(left) ? null : kb.getEvent(left));
          if ((le == null) || !left.equals(le.getId())) {
            valid = false;
            LOGGER.warn("Left-Partner-Event {} referenced by Relation {} does not exists.", left, rid);
          }
          else {
            final String levid = le.getVariantId();
            if (StringUtils.isEmpty(levid) || !vid.equals(levid)) {
              valid = false;
              LOGGER.warn("Left-Partner-Event {} and Relation {} are not attached to the same Variant: {} vs. {}", left, rid, levid, vid);
            }
            if (!le.isFeatureEvent()) {
              valid = false;
              LOGGER.warn("Left-Partner-Event {} of Relation {} is not a Feature-Event. Relations must be based on Features!", left, rid);
            }
          }
          // --- Step 3: check right side of operation/relation ---
          final String right = r.getRightPartnerEventID();
          final Event re = (StringUtils.isEmpty(right) ? null : kb.getEvent(right));
          if ((re == null) || !right.equals(re.getId())) {
            valid = false;
            LOGGER.warn("Right-Partner-Event {} referenced by Relation {} does not exists.", right, rid);
          }
          else {
            final String revid = re.getVariantId();
            if (StringUtils.isEmpty(revid) || !vid.equals(revid)) {
              valid = false;
              LOGGER.warn("Right-Partner-Event {} and Relation {} are not attached to the same Variant: {} vs. {}", right, rid, revid, vid);
            }
            if (!re.isFeatureEvent()) {
              valid = false;
              LOGGER.warn("Right-Partner-Event {} of Relation {} is not a Feature-Event. Relations must be based on Features!", right, rid);
            }
          }
          // --- Step 4: check that both sides of the relation are features of the same type
          // Note: Validity of Features is checked by FeatureValidator, here we only avoid NPEs!
          final String leftFeatureId = (le == null ? null : le.getTrigger());
          final Feature<?> leftFeature = (StringUtils.isEmpty(leftFeatureId) ? null : kb.getFeature(leftFeatureId));
          final Class<?> leftValueType = (leftFeature == null ? null : leftFeature.getValueType());
          final String rightFeatureId = (re == null ? null : re.getTrigger());
          final Feature<?> rightFeature = (StringUtils.isEmpty(rightFeatureId) ? null : kb.getFeature(rightFeatureId));
          final Class<?> rightValueType = (rightFeature == null ? null : rightFeature.getValueType());
          if ((leftValueType == null) || (rightValueType == null) || !rightValueType.equals(leftValueType)) {
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
}
