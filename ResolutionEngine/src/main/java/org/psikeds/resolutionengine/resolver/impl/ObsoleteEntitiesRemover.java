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

import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.util.ConceptHelper;
import org.psikeds.resolutionengine.util.FeatureValueHelper;

/**
 * This Resolver removes obsolete Entities, i.e. impossible Feature-Values
 * or obsolete Concepts.
 * 
 * @author marco@juliano.de
 * 
 */
public class ObsoleteEntitiesRemover implements Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObsoleteEntitiesRemover.class);

  /**
   * @param knowledge
   *          current Knowledge
   * @param decission
   *          Decission made by Client (ignored!)
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
    boolean stable = true;
    try {
      LOGGER.debug("Removing obsolete Entities ...");
      stable = cleanKnowledge(knowledge);
      if (!stable) {
        // We removed some Entities, therefore it is neccessary to execute the full Resolver-Chain once again
        LOGGER.debug("Knowledge is not stable, need another Iteration of all Resolvers!");
        knowledge.setStable(false);
      }
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished removal of obsolete Entities. Knowledge is " + (stable ? "stable." : "NOT stable!"));
    }
  }

  // ----------------------------------------------------------------

  private boolean cleanKnowledge(final Knowledge knowledge) throws ResolutionException {
    try {
      LOGGER.trace("--> cleanKnowledge()\nKnowledge = {}", knowledge);
      return cleanKnowledgeEntities(null, knowledge.getEntities(), knowledge.getChoices());
    }
    finally {
      LOGGER.trace("<-- cleanKnowledge()\nKnowledge = {}", knowledge);
    }
  }

  private boolean cleanKnowledgeEntities(final KnowledgeEntity parentEntity, final KnowledgeEntities entities, final List<? extends Choice> choices) throws ResolutionException {
    boolean stable = true;
    try {
      LOGGER.trace("--> cleanKnowledgeEntities(); Choices = {}", choices);
      if (parentEntity != null) {
        if (FeatureValueHelper.removeImpossibleFeatureValues(parentEntity)) {
          LOGGER.debug("Removed impossible Feature-Value. KE = {}", parentEntity);
          stable = false;
        }
      }
      if ((choices != null) && !choices.isEmpty()) {
        for (final Choice c : choices) {
          if (c instanceof ConceptChoice) {
            final ConceptChoice cc = (ConceptChoice) c;
            if (ConceptHelper.removeObsoleteConcepts(parentEntity, cc)) {
              LOGGER.debug("Removed obsolete Concepts. KE = {}", parentEntity);
              stable = false;
            }
          }
        }
      }
      if ((entities != null) && !entities.isEmpty()) {
        for (final KnowledgeEntity ke : entities) {
          if (!cleanKnowledgeEntities(ke, ke.getChildren(), ke.getPossibleVariants())) {
            stable = false;
          }
          if (!cleanKnowledgeEntities(ke, ke.getChildren(), ke.getPossibleFeatures())) {
            stable = false;
          }
          if (!cleanKnowledgeEntities(ke, ke.getChildren(), ke.getPossibleConcepts())) {
            stable = false;
          }
        }
      }
      return stable;
    }
    catch (final Exception ex) {
      final String errmsg = "Cannot clean up Knowledge-Entities: " + ex.getMessage();
      LOGGER.warn(errmsg);
      throw new ResolutionException(errmsg, ex);
    }
    finally {
      LOGGER.trace("<-- cleanKnowledgeEntities(); stable = {} ", stable);
    }
  }
}
