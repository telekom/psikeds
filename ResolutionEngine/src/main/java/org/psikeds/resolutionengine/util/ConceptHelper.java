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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for handling Concepts
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class ConceptHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptHelper.class);

  public static final String RET_CONCEPT_POSSIBLE = "possible";
  public static final String RET_CONCEPT_NOT_POSSIBLE = "impossible";
  public static final String RET_CONCEPT_FULFILLED = "fulfilled";

  private ConceptHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static String checkConcept(final KnowledgeBase kb, final Transformer trans, final KnowledgeEntity ke, final String conceptId) {
    final Concept con = trans.valueObject2Pojo(kb.getConcept(conceptId));
    final String ret = checkConcept(ke, con);
    LOGGER.debug("Checked KE {} regardings Concept {}  ==>  {}", shortDisplayKE(ke), conceptId, ret);
    return ret;
  }

  public static String checkConcept(final KnowledgeEntity ke, final Concept con) {
    String ret = RET_CONCEPT_POSSIBLE;
    final String conceptId = (con == null ? null : con.getConceptID());
    try {
      LOGGER.trace("--> checkConcept(); KE = {}; CON = {}", shortDisplayKE(ke), conceptId);
      // Step 0: check prerequisites
      if ((ke == null) || StringUtils.isEmpty(conceptId)) {
        LOGGER.warn("No Concept/Concept-ID!!!");
        ret = RET_CONCEPT_NOT_POSSIBLE;
        return ret;
      }
      final FeatureValues convals = con.getValues();
      if ((convals == null) || convals.isEmpty()) {
        LOGGER.warn("Concept {} does not include any Feature-Values?!?");
        ret = RET_CONCEPT_FULFILLED;
        return ret;
      }
      // Step 1: check already assigned feature values of ke
      final FeatureValues kevals = ke.getFeatures();
      if ((kevals != null) && !kevals.isEmpty()) {
        if (kevals.containsAll(convals)) {
          LOGGER.debug("Concept {} is already fulfilled, i.e. KE {} contains all required FeatureValues.", conceptId, shortDisplayKE(ke));
          ret = RET_CONCEPT_FULFILLED;
          return ret;
        }
        for (final FeatureValue kefv : kevals) {
          for (final FeatureValue confv : convals) {
            if (kefv.getFeatureID().equals(confv.getFeatureID())) {
              if (kefv.getFeatureValueID().equals(confv.getFeatureValueID())) {
                LOGGER.trace("Found Feature-Value {} matching to Concept {}", kefv.getFeatureValueID(), conceptId);
              }
              else {
                LOGGER.debug("Concept {} is not possible any more. KE {} contains Value {} not Value {} for Feature {}", conceptId, shortDisplayKE(ke), kefv.getFeatureValueID(),
                    confv.getFeatureValueID());
                ret = RET_CONCEPT_NOT_POSSIBLE;
                return ret;
              }
            }
          }
        }
      }
      // Step 2: check possible concept choices of ke
      final ConceptChoices cclist = ke.getPossibleConcepts();
      if ((cclist != null) && !cclist.isEmpty()) {
        for (final ConceptChoice cc : cclist) {
          for (final Concept c : cc.getConcepts()) {
            if (con.equals(c)) {
              LOGGER.debug("Found Concept {} within possible Choices for KE {}", conceptId, shortDisplayKE(ke));
              ret = RET_CONCEPT_POSSIBLE;
              return ret;
            }
          }
        }
      }
      // at this point, the concept is neither fulfilled nor within any choice,
      // i.e. it is not possible any more!
      LOGGER.debug("Concept {} is not possible any more for KE {}", conceptId, shortDisplayKE(ke));
      ret = RET_CONCEPT_NOT_POSSIBLE;
      return ret;
    }
    catch (final ResolutionException re) {
      ret = RET_CONCEPT_NOT_POSSIBLE;
      throw re;
    }
    catch (final Exception ex) {
      ret = RET_CONCEPT_NOT_POSSIBLE;
      throw new ResolutionException("Cannot check Concept!", ex);
    }
    finally {
      LOGGER.trace("<-- checkConcept(); KE = {}; CON = {}; RET = {}", shortDisplayKE(ke), conceptId, ret);
    }
  }

  // ----------------------------------------------------------------

  public static boolean removeObsoleteConcepts(final KnowledgeEntity ke, final ConceptChoice cc) {
    boolean removed = false;
    try {
      LOGGER.trace("--> removeObsoleteConcepts(); KE = {}; CC = {}", shortDisplayKE(ke), cc);
      if ((ke != null) && (cc != null)) {
        final String vid = cc.getParentVariantID();
        final List<String> conceptIDs = new ArrayList<String>();
        for (final Concept con : cc.getConcepts()) {
          final String ret = checkConcept(ke, con);
          if (!RET_CONCEPT_POSSIBLE.equals(ret)) {
            // concept is either impossible or already fulfilled
            // --> remove it from possible choices for this variant
            final String cid = con.getConceptID();
            conceptIDs.add(cid);
            LOGGER.debug("Adding Concept {} to Removal-List for Variant {}", cid, vid);
          }
        }
        if (!conceptIDs.isEmpty()) {
          LOGGER.debug("Removing Concepts {} from KE {}", conceptIDs, shortDisplayKE(ke));
          removed = cleanupConceptChoices(ke, vid, conceptIDs);
        }
      }
      return removed;
    }
    finally {
      LOGGER.trace("<-- removeObsoleteConcepts(); KE = {}; removed = {}", shortDisplayKE(ke), removed);
    }
  }

  // ----------------------------------------------------------------

  public static void applyConcept(final KnowledgeEntity ke, final Concept con) {
    FeatureValueHelper.applyConcept(ke, con);
  }

  public static boolean cleanupConceptChoices(final KnowledgeEntity ke, final String variantId) {
    return ChoicesHelper.cleanupConceptChoices(ke, variantId);
  }

  public static boolean cleanupConceptChoices(final KnowledgeEntity ke, final String variantId, final String conceptId) {
    return ChoicesHelper.cleanupConceptChoices(ke, variantId, conceptId);
  }

  public static boolean cleanupConceptChoices(final KnowledgeEntity ke, final String variantId, final List<String> conceptIDs) {
    return ChoicesHelper.cleanupConceptChoices(ke, variantId, conceptIDs);
  }

  // ----------------------------------------------------------------

  public static String shortDisplayKE(final KnowledgeEntity ke) {
    return KnowledgeEntityHelper.shortDisplayKE(ke);
  }
}
