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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;

/**
 * Based on the made Concept-Decission this Resolver will reduce the
 * Concept-Choices to just one single Concept so that afterwards the
 * corresponding Feature-Values will be created by the AutoCompletion-Resolver.
 * 
 * If no Decission is supplied, nothing happens. Metadata is optional.
 * 
 * Note: Must run before AutoCompletion-Resolver!
 * 
 * @author marco@juliano.de
 */
public class ConceptDecissionEvaluator implements Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptDecissionEvaluator.class);

  /**
   * @param knowledge
   *          current Knowledge
   * @param decission
   *          some Decission, ignored if not a ConceptDecission
   * @param raeh
   *          all Rules and Events currently relevant (ignored!)
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
    boolean found = false;
    try {
      LOGGER.debug("Evaluating made Decission ...");
      if (decission instanceof ConceptDecission) {
        final ConceptDecission cd = (ConceptDecission) decission;
        found = updateKnowledge(knowledge, cd, metadata);
      }
      else {
        LOGGER.debug("Skipping Evaluation, no Concept-Decission.");
      }
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished evaluating made Decission: " + (found ? "" : "NOT") + " found, " + (ok ? "OK." : "ERROR!"));
    }
  }

  // ----------------------------------------------------------------

  private boolean updateKnowledge(final Knowledge knowledge, final ConceptDecission cd, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateKnowledge()\nConcept-Decission = {}\nKnowledge = {}", cd, knowledge);
      for (final KnowledgeEntity ke : knowledge.getEntities()) {
        if (updateKnowledgeEntity(ke, cd, metadata)) {
          found = true;
        }
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateKnowledge(); Found = {}\nKnowledge = {}", found, knowledge);
    }
  }

  private boolean updateKnowledgeEntity(final KnowledgeEntity ke, final ConceptDecission cd, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateKnowledgeEntity()");
      found = updateConceptChoices(ke.getPossibleConcepts(), cd, metadata);
      for (final KnowledgeEntity child : ke.getChildren()) {
        if (updateKnowledgeEntity(child, cd, metadata)) {
          found = true;
        }
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateKnowledgeEntity(); Found = {}", found);
    }
  }

  private boolean updateConceptChoices(final ConceptChoices choices, final ConceptDecission cd, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateConceptChoices()");
      for (final ConceptChoice cc : choices) {
        LOGGER.trace("Checking: {}", cc);
        final Concept con = (cc == null ? null : cc.matches(cd));
        if (con != null) {
          LOGGER.debug("Found Concept {} of Choice {}", con.getConceptID(), cc);
          found = true;
          cc.setConcept(con);
          decissionMessage(metadata, cd, cc);
        }
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateConceptChoices(); Found = {}", found);
    }
  }

  // ----------------------------------------------------------------

  private void decissionMessage(final Metadata metadata, final ConceptDecission cd, final ConceptChoice cc) {
    LOGGER.info("Applying Decission for Concept {} for Variant {}", cd.getConceptID(), cd.getVariantID());
    if (metadata != null) {
      final String key = String.format("Decission_CD_%s_%s", cd.getVariantID(), cd.getConceptID());
      final String msg = String.format("Found Concept-Choice matching Decission.\nDecission = %s\nChoice = %s", cd, cc);
      metadata.addInfo(key, msg);
    }
  }
}
