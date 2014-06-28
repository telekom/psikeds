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
import java.util.Collection;
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
public abstract class FeatureValueHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureValueHelper.class);

  public static final String RET_FV_POSSIBLE = "possible";
  public static final String RET_FV_NOT_POSSIBLE = "impossible";
  public static final String RET_FV_ASSIGNED = "assigned";

  private FeatureValueHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static String checkFeatureValue(final KnowledgeBase kb, final Transformer trans, final KnowledgeEntity ke, final String featureValueID) {
    String ret = RET_FV_POSSIBLE;
    try {
      LOGGER.trace("--> checkFeatureValue(); KE = {}; FV = {}", shortDisplayKE(ke), featureValueID);
      // Step 0: ensure clean data from kb
      final FeatureValue fv = (StringUtils.isEmpty(featureValueID) ? null : trans.valueObject2Pojo(kb.getFeatureValue(featureValueID)));
      final String featureID = (fv == null ? null : fv.getFeatureID());
      if (StringUtils.isEmpty(featureID)) {
        LOGGER.warn("No Feature found for Feature-Value {}", featureValueID);
        ret = RET_FV_NOT_POSSIBLE;
        return ret;
      }
      // Step 1: check features and assigned values of ke
      LOGGER.debug("Checking assigned Features of KE {} regarding Feature {} and Value {}", shortDisplayKE(ke), featureID, featureValueID);
      final FeatureValues fvlst = (ke == null ? null : ke.getFeatures());
      if ((fvlst != null) && !fvlst.isEmpty()) {
        for (final FeatureValue val : fvlst) {
          if (featureID.equals(val.getFeatureID())) {
            if (featureValueID.equals(val.getFeatureValueID())) {
              LOGGER.debug("Found matching Feature-Value {} assigned to KE {}", featureValueID, shortDisplayKE(ke));
              ret = RET_FV_ASSIGNED;
              return ret;
            }
            else {
              LOGGER.debug("Feature {} of KE {} has another assigned Value {}", featureID, shortDisplayKE(ke), featureValueID);
              ret = RET_FV_NOT_POSSIBLE;
              return ret;
            }
          }
        }
      }
      // Step 2: check still possible choices for features of ke
      LOGGER.debug("Checking possible Feature-Values of KE {} regarding Feature {} and Value {}", shortDisplayKE(ke), featureID, featureValueID);
      final FeatureChoices fclst = (ke == null ? null : ke.getPossibleFeatures());
      if ((fclst != null) && !fclst.isEmpty()) {
        for (final FeatureChoice fc : fclst) {
          if (featureID.equals(fc.getFeatureID())) {
            final FeatureValues vals = fc.getPossibleValues();
            if ((vals != null) && vals.contains(fv)) {
              LOGGER.debug("Found matching Feature-Value {} within Feature-Choice of KE {}", featureValueID, shortDisplayKE(ke));
              ret = RET_FV_POSSIBLE;
              return ret;
            }
            else {
              LOGGER.debug("There are Choices for Feature {}, but the desired Value {} is not possible: {}", featureID, featureValueID, fc);
              ret = RET_FV_NOT_POSSIBLE;
              return ret;
            }
          }
        }
      }
      // Step 3: check still possible choices for concepts of ke
      final ConceptChoices cclst = (ke == null ? null : ke.getPossibleConcepts());
      if ((cclst != null) && !cclst.isEmpty()) {
        for (final ConceptChoice cc : cclst) {
          for (final Concept con : cc.getConcepts()) {
            for (final FeatureValue val : con.getValues()) {
              if (featureID.equals(val.getFeatureID()) && featureValueID.equals(val.getFeatureValueID())) {
                LOGGER.debug("Possible Concept {} contains matching Value {}", con.getConceptID(), featureValueID);
                ret = RET_FV_POSSIBLE;
                return ret;
              }
            }
          }
        }
      }
      // at this point, the fv was neither assigned nor within any concept or choice,
      // i.e. it is not possible any more!
      LOGGER.debug("Feature-Value {} is not possible for Feature {} of KE {}", featureValueID, featureID, shortDisplayKE(ke));
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
      LOGGER.trace("<-- checkFeatureValue(); KE = {}; FV = {}; RET = {}", shortDisplayKE(ke), featureValueID, ret);
    }
  }

  // ----------------------------------------------------------------

  public static void applyFeatureValue(final KnowledgeEntity ke, final FeatureValue fv) {
    if ((ke != null) && (fv != null)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Adding new FeatureValue {}\nto KE {}", fv, ke);
      }
      else {
        LOGGER.debug("Adding new FeatureValue {} to KE {}", fv.getFeatureValueID(), shortDisplayKE(ke));
      }
      ke.addFeature(fv);
    }
  }

  public static void applyConcept(final KnowledgeEntity ke, final Concept con) {
    if ((ke != null) && (con != null)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Setting FeatureValues for KE {}\naccording to Concept {}", ke, con);
      }
      else {
        LOGGER.debug("Setting FeatureValues for KE {} according to Concept {}", shortDisplayKE(ke), con.getConceptID());
      }
      final FeatureValues values = con.getValues();
      for (final FeatureValue fv : values) {
        applyFeatureValue(ke, fv);
      }
    }
  }

  // ----------------------------------------------------------------

  public static org.psikeds.resolutionengine.datalayer.vo.FeatureValue findFeatureValue(final KnowledgeBase kb, final KnowledgeEntity ke, final String featureId) {
    if ((ke != null) && (featureId != null)) {
      for (final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue fv : ke.getFeatures()) {
        if (featureId.equals(fv.getFeatureID())) {
          // note: a ke has at most one value for a feature
          final org.psikeds.resolutionengine.datalayer.vo.FeatureValue ret = pojo2ValueObject(kb, fv);
          LOGGER.trace("Found Feature-Value: {}", ret);
          return ret;
        }
      }
    }
    return null;
  }

  public static boolean removeImpossibleFeatureValues(final KnowledgeEntity ke) {
    boolean removed = false;
    try {
      LOGGER.trace("--> removeImpossibleFeatureValues(); KE = {}", ke);
      if (ke != null) {
        for (final FeatureValue fv : ke.getFeatures()) {
          // remove all choices of this variant for the feature belonging to the value
          if (ChoicesHelper.cleanupFeatureChoices(ke, ke.getVariant().getVariantID(), fv.getFeatureID())) {
            LOGGER.debug("Removed impossible Feature-Values of KE: {}", shortDisplayKE(ke));
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

  // ----------------------------------------------------------------

  public static org.psikeds.resolutionengine.datalayer.vo.FeatureValue pojo2ValueObject(final KnowledgeBase kb, final FeatureValue pojo) {
    final String featureValueId = (pojo == null ? null : pojo.getFeatureValueID());
    return (((kb == null) || StringUtils.isEmpty(featureValueId)) ? null : kb.getFeatureValue(featureValueId)); // lookup fv from kb to ensure clean data
  }

  public static List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> pojo2ValueObject(final KnowledgeBase kb, final Collection<? extends FeatureValue> pojo) {
    List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> lst = null;
    if (pojo != null) {
      lst = new ArrayList<org.psikeds.resolutionengine.datalayer.vo.FeatureValue>();
      for (final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue pfv : pojo) {
        final org.psikeds.resolutionengine.datalayer.vo.FeatureValue vofv = pojo2ValueObject(kb, pfv);
        if (vofv != null) {
          lst.add(vofv);
        }
      }
    }
    return lst;
  }

  // ----------------------------------------------------------------

  public static boolean addAllDistinct(
      final List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> targetList,
      final Collection<? extends org.psikeds.resolutionengine.datalayer.vo.FeatureValue> additionalElements) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.FeatureValueHelper.addAllDistinct(targetList, additionalElements);
  }

  public static boolean addAllDistinct(final FeatureValues targetList, final Collection<? extends FeatureValue> additionalElements) {
    boolean changed = false;
    if ((targetList != null) && (additionalElements != null)) {
      for (final FeatureValue elem : additionalElements) {
        if (!targetList.contains(elem)) {
          changed = true;
          targetList.add(elem);
          LOGGER.trace("Added additional Element: {}", elem);
        }
      }
    }
    return changed;
  }

  public static FeatureValues combineValues(final Collection<? extends FeatureValue> list1, final Collection<? extends FeatureValue> list2) {
    return combineValues(new FeatureValues(), list1, list2);
  }

  public static FeatureValues combineValues(final FeatureValues targetList, final Collection<? extends FeatureValue> list1, final Collection<? extends FeatureValue> list2) {
    if (targetList != null) {
      if (list1 != null) {
        addAllDistinct(targetList, list1);
      }
      if (list2 != null) {
        addAllDistinct(targetList, list2);
      }
    }
    LOGGER.trace("Combined Values = {}", targetList);
    return targetList;
  }

  // ----------------------------------------------------------------

  public static String shortDisplayKE(final KnowledgeEntity ke) {
    return KnowledgeEntityHelper.shortDisplayKE(ke);
  }
}
