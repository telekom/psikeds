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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for handling FeatureValues
 * 
 * @author marco@juliano.de
 * 
 */
public class FeatureValueHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureValueHelper.class);

  public static final String RET_FV_POSSIBLE = "possible";
  public static final String RET_FV_NOT_POSSIBLE = "impossible";
  public static final String RET_FV_ASSIGNED = "assigned";

  // ----------------------------------------------------------------

  public static String checkFeatureValue(final KnowledgeBase kb, final Transformer trans, final KnowledgeEntity ke, final String featureValueID) {
    String ret = RET_FV_POSSIBLE;
    try {
      LOGGER.trace("--> checkFeatureValue(); FV = {}\nKE = {}", featureValueID, ke);
      // ensure clean data from kb
      final FeatureValue fv = (StringUtils.isEmpty(featureValueID) ? null : trans.valueObject2Pojo(kb.getFeatureValue(featureValueID)));
      final String featureID = (fv == null ? null : fv.getFeatureID());
      if (StringUtils.isEmpty(featureID)) {
        throw new IllegalArgumentException("No Feature-Value!");
      }
      // first check features of ke
      final FeatureValues fvlst = (ke == null ? null : ke.getFeatures());
      if ((fvlst != null) && !fvlst.isEmpty()) {
        for (final FeatureValue val : fvlst) {
          if (featureID.equals(val.getFeatureID())) {
            if (featureValueID.equals(val.getFeatureValueID())) {
              LOGGER.debug("Found matching Feature-Value: {}", val);
              ret = RET_FV_ASSIGNED;
              return ret;
            }
            else {
              LOGGER.debug("Feature has another Value: {}", val);
              ret = RET_FV_NOT_POSSIBLE;
              return ret;
            }
          }
        }
      }
      // second check still possible features of ke
      final FeatureChoices fclst = (ke == null ? null : ke.getPossibleFeatures());
      if ((fclst != null) && !fclst.isEmpty()) {
        for (final FeatureChoice fc : fclst) {
          if (featureID.equals(fc.getFeatureID())) {
            final FeatureValues vals = fc.getPossibleValues();
            if ((vals != null) && vals.contains(fv)) {
              LOGGER.debug("Found matching Feature-Value within Feature-Choice: {}", fc);
              ret = RET_FV_POSSIBLE;
              return ret;
            }
            else {
              LOGGER.debug("There are Choices for the Feature, but the desired Value is not possible: {}", fc);
              ret = RET_FV_NOT_POSSIBLE;
              return ret;
            }
          }
        }
      }
      // third check still possible concepts of ke
      final ConceptChoices cclst = (ke == null ? null : ke.getPossibleConcepts());
      if ((cclst != null) && !cclst.isEmpty()) {
        for (final ConceptChoice cc : cclst) {
          for (final Concept con : cc.getConcepts()) {
            final List<String> fids = con.getFeatureIds();
            if ((fids != null) && fids.contains(featureID)) {
              final FeatureValues vals = con.getValues();
              if ((vals != null) && vals.contains(fv)) {
                LOGGER.debug("Found matching Feature-Value within Concept-Choice: {}", cc);
                ret = RET_FV_POSSIBLE;
                return ret;
              }
              else {
                LOGGER.debug("Possible Concepts contain the Feature, but the desired Value is not possible: {}", cc);
                ret = RET_FV_NOT_POSSIBLE;
                return ret;
              }
            }
          }
        }
      }
      // at this point, the fv was neither assigned nor within any concept or choice,
      // i.e. it is not possible any more!
      ret = RET_FV_NOT_POSSIBLE;
      return ret;
    }
    catch (final ResolutionException re) {
      ret = RET_FV_NOT_POSSIBLE;
      throw re;
    }
    catch (final Exception ex) {
      ret = RET_FV_NOT_POSSIBLE;
      throw new ResolutionException("Cannot check Feature-Value!", ex);
    }
    finally {
      LOGGER.trace("<-- checkFeatureValue(); FV = {}; RET = {}", featureValueID, ret);
    }
  }

  // ----------------------------------------------------------------

  public static void applyFeatureValue(final KnowledgeEntity ke, final FeatureValue fv) {
    if ((ke != null) && (fv != null)) {
      LOGGER.trace("Adding new FeatureValue: {}", fv);
      ke.addFeature(fv);
    }
  }

  public static void applyConcept(final KnowledgeEntity ke, final Concept con) {
    if ((ke != null) && (con != null)) {
      LOGGER.trace("Setting FeatureValues according to Concept: {}", con);
      final FeatureValues values = con.getValues();
      for (final FeatureValue fv : values) {
        applyFeatureValue(ke, fv);
      }
    }
  }

  // ----------------------------------------------------------------

  public static boolean removeImpossibleFeatureValues(final KnowledgeEntity ke) {
    boolean removed = false;
    try {
      LOGGER.trace("--> removeImpossibleFeatureValues(); KE = {}", ke);
      if (ke != null) {
        for (final FeatureValue fv : ke.getFeatures()) {
          // remove all choices of this variant for the feature belonging to the value
          if (ChoicesHelper.cleanupFeatureChoices(ke, ke.getVariant().getVariantID(), fv.getFeatureID())) {
            removed = true;
          }
        }
      }
      return removed;
    }
    finally {
      LOGGER.trace("<-- removeImpossibleFeatureValues(); removed = {}; KE = {}", removed, ke);
    }
  }
}
