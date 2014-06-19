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

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for creating and handling Knowledge-Entites
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class KnowledgeEntityHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeEntityHelper.class);

  private KnowledgeEntityHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static KnowledgeEntity ceateNewEntity(final KnowledgeBase kb, final Transformer trans, final KnowledgeEntity parent, final Purpose purp, final Variant var) {
    return ceateNewEntity(kb, trans, parent, (purp == null ? null : purp.getPurposeID()), (var == null ? null : var.getVariantID()));
  }

  public static KnowledgeEntity ceateNewEntity(final KnowledgeBase kb, final Transformer trans, final KnowledgeEntity parent, final String purposeId, final String variantId) {
    KnowledgeEntity nextKE = null;
    try {
      LOGGER.trace("--> ceateNewEntity(); PID = {}; VID = {}", purposeId, variantId);
      if (StringUtils.isEmpty(purposeId) || StringUtils.isEmpty(variantId)) {
        throw new ResolutionException("Cannot create new Knowledge-Entity. Illegal Parameters!");
      }
      // ensure clean data, therefore lookup purpose, variant and quantity from knowledge base (again)
      final org.psikeds.resolutionengine.datalayer.vo.Purpose purpose = kb.getPurpose(purposeId);
      final Purpose p = (purpose == null ? null : trans.valueObject2Pojo(purpose));
      if (p == null) {
        throw new ResolutionException("Cannot create new Knowledge-Entity. Unknown Purpose-ID: " + purposeId);
      }
      final org.psikeds.resolutionengine.datalayer.vo.Variant variant = kb.getVariant(variantId);
      final Variant v = (variant == null ? null : trans.valueObject2Pojo(variant, kb.getFeatures(variantId), kb.getAttachedConcepts(variantId)));
      if (v == null) {
        throw new ResolutionException("Cannot create new Knowledge-Entity. Unknown Variant-ID: " + variantId);
      }
      final long qty = kb.getQuantity(variantId, purposeId);
      // get new choices for this variant
      final VariantChoices newVariantChoices = ChoicesHelper.getNewVariantChoices(kb, trans, variant);
      final FeatureChoices newFeatureChoices = ChoicesHelper.getNewFeatureChoices(kb, trans, variant);
      final ConceptChoices newConceptChoices = ChoicesHelper.getNewConceptChoices(kb, trans, variant);
      // create new knowledge entity
      nextKE = new KnowledgeEntity(qty, p, v, newVariantChoices, newFeatureChoices, newConceptChoices);
      KnowledgeEntityHelper.addNewKnowledgeEntity(parent, nextKE);
      // remove all variant-choices for our purpose from parent KE
      ChoicesHelper.cleanupVariantChoices(parent, purposeId);
      return nextKE;
    }
    finally {
      LOGGER.trace("<-- ceateNewEntity(); PID = {}; VID = {}\nNext KE = {}", purposeId, variantId, nextKE);
    }
  }

  // ----------------------------------------------------------------

  public static KnowledgeEntities addNewKnowledgeEntity(final KnowledgeEntity parent, final KnowledgeEntity newke) {
    final KnowledgeEntities entities = (parent == null ? null : parent.getChildren());
    return addNewKnowledgeEntity(entities, newke);
  }

  public static KnowledgeEntities addNewKnowledgeEntity(final KnowledgeEntities entities, final KnowledgeEntity newke) {
    try {
      LOGGER.trace("--> addNewKnowledgeEntity()");
      if ((entities != null) && (newke != null)) {
        for (final KnowledgeEntity ke : entities) {
          final Purpose p = (ke == null ? null : ke.getPurpose());
          final Variant v = (ke == null ? null : ke.getVariant());
          if ((p != null) && (v != null)) {
            final String pid1 = p.getPurposeID();
            final String pid2 = newke.getPurpose().getPurposeID();
            final String vid1 = v.getVariantID();
            final String vid2 = newke.getVariant().getVariantID();
            if (pid1.equals(pid2) && vid1.equals(vid2)) {
              LOGGER.debug("Entity-List already contains KnowledgeEntity: {}", newke);
              return entities;
            }
          }
        }
        LOGGER.debug("Adding new KnowledgeEntity: {}", newke);
        entities.add(newke);
      }
      return entities;
    }
    finally {
      LOGGER.trace("<-- addNewKnowledgeEntity()");
    }
  }
}
