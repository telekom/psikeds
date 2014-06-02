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

import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;

/**
 * Helper for handling FeatureValues
 * 
 * @author marco@juliano.de
 * 
 */
public class FeatureValueHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureValueHelper.class);

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

  public static void removeImpossibleFeatureValues(final KnowledgeEntity ke, final FeatureChoice fc) {
    try {
      LOGGER.trace("--> removeImpossibleFeatureValues(); FC = {}\nKE = {}", fc, ke);
      for (final FeatureValue fv : ke.getFeatures()) {
        // remove all choices of this variant for the feature belonging to the value
        ChoicesHelper.cleanupFeatureChoices(ke, ke.getVariant().getVariantID(), fv.getFeatureID());
      }
    }
    finally {
      LOGGER.trace("<-- removeImpossibleFeatureValues(); FC = {}\nKE = {}", fc, ke);
    }
  }
}
