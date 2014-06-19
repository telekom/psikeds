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
package org.psikeds.resolutionengine.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;

/**
 * Helper for handling/traversing of Knowledge
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class KnowledgeHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeHelper.class);

  private KnowledgeHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  // In a regular Tree each Variant is used just once. However in a general
  // Knowledge-Graph a Variant could be (re)used several times for different
  // Purposes. Therefore this Helper does not stop after the first Hit but
  // returns a List of all Root-Entities containing the desired Variant.

  public static KnowledgeEntities findRoot(final Event e, final Knowledge knowledge) {
    final String eventId = (e == null ? null : e.getEventID());
    final String rootVariantId = (e == null ? null : e.getVariantID());
    final KnowledgeEntities root = findRoot(rootVariantId, knowledge);
    LOGGER.debug("Root of Event {} is: {}", eventId, root);
    return root;
  }

  public static KnowledgeEntities findRoot(final Rule r, final Knowledge knowledge) {
    final String ruleId = (r == null ? null : r.getRuleID());
    final String rootVariantId = (r == null ? null : r.getVariantID());
    final KnowledgeEntities root = findRoot(rootVariantId, knowledge);
    LOGGER.debug("Root of Rule {} is: {}", ruleId, root);
    return root;
  }

  public static KnowledgeEntities findRoot(final Relation r, final Knowledge knowledge) {
    final String relationId = (r == null ? null : r.getRelationID());
    final String rootVariantId = (r == null ? null : r.getVariantID());
    final KnowledgeEntities root = findRoot(rootVariantId, knowledge);
    LOGGER.debug("Root of Relation {} is: {}", relationId, root);
    return root;
  }

  // ----------------------------------------------------------------

  private static KnowledgeEntities findRoot(final String rootVariantId, final Knowledge knowledge) {
    final KnowledgeEntities result = new KnowledgeEntities();
    final KnowledgeEntities entities = (knowledge == null ? null : knowledge.getEntities());
    findRoot(result, rootVariantId, entities);
    return result;
  }

  private static KnowledgeEntities findRoot(final KnowledgeEntities result, final String rootVariantId, final KnowledgeEntities entities) {
    if ((entities != null) && !entities.isEmpty()) {
      for (final KnowledgeEntity ke : entities) {
        if (rootVariantId.equals(ke.getVariant().getVariantID())) {
          result.add(ke);
        }
        findRoot(result, rootVariantId, ke.getChildren());
      }
    }
    return result;
  }
}
