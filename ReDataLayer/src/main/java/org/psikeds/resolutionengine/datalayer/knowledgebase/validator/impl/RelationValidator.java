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
import org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
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
        long count = 0;
        for (final Relation r : lst) {
          count++;
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
          if (r.isConditional()) {
            final String ceid = r.getConditionalEventID();
            final Event e = kb.getEvent(ceid);
            if ((e == null) || !ceid.equals(e.getEventID())) {
              valid = false;
              LOGGER.warn("Conditional-Event-ID {} referenced by Relation {} does not exists.", ceid, rid);
            }
          }
          // --- Step 2: check left and right side of Relation  ---
          final RelationParameter left = r.getLeftSide();
          final RelationParameter right = r.getRightSide();
          if (!RelationHelper.checkRelationParameter(kb, rid, vid, left)) {
            valid = false;
            LOGGER.warn("Left side of Relation {} is not valid!", rid);
          }
          if (!RelationHelper.checkRelationParameter(kb, rid, vid, right)) {
            valid = false;
            LOGGER.warn("Right side of Relation {} is not valid!", rid);
          }
          if (left.isConstant() && right.isConstant()) {
            valid = false;
            LOGGER.warn("Both Parameters of Relation {} are Constants!", rid);
          }
          // --- Step 3: check that Parameters are compatible ---
          if (!RelationHelper.isCompatible(kb, left, right)) {
            valid = false;
            LOGGER.warn("Parameters of Relation {} are not compatible!", rid);
          }
        }
        LOGGER.debug("Checked {} Relations.", count);
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
