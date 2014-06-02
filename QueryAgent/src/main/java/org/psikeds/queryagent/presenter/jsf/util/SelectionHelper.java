/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.jsf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.POJO;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantDecission;

/**
 * Helper for creating ID-Strings from Objects and vice versa.
 * Used by Views / XHTML-Pages for generating Links and References.
 * 
 * @author marco@juliano.de
 */
public final class SelectionHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(SelectionHelper.class);
  private static final String SELECT_REGEXP = String.valueOf(POJO.COMPOSE_ID_SEPARATOR);

  public static final String SELECTION_TYPE_VARIANT = "V";
  public static final String SELECTION_TYPE_CONCEPT = "C";
  public static final String SELECTION_TYPE_FEATURE_VALUE = "FV";

  public static String createSelectionString(final String type, final String... ids) {
    final String idstr = POJO.composeId(ids);
    return POJO.composeId(type, idstr);
  }

  public static String createSelectionString(final String type, final POJO... pojos) {
    final String idstr = POJO.composeId(pojos);
    return POJO.composeId(type, idstr);
  }

  public static Decission getDecissionFromString(final String selected) {
    Decission d = null;
    try {
      LOGGER.trace("--> getDecissionFromSelectionString( {} )", selected);
      final String[] parts = selected.split(SELECT_REGEXP);
      final String type = parts[0].trim();
      if (SELECTION_TYPE_VARIANT.equals(type)) {
        d = getVariantDecissionFromString(parts);
      }
      else if (SELECTION_TYPE_CONCEPT.equals(type)) {
        d = getConceptDecissionFromString(parts);
      }
      else if (SELECTION_TYPE_FEATURE_VALUE.equals(type)) {
        d = getFeatureDecissionFromString(parts);
      }
      else {
        LOGGER.warn("Invalid Selection-String: {}", selected);
      }
    }
    catch (final Exception ex) {
      // string is null or not in the expected format
      d = null;
    }
    finally {
      LOGGER.trace("<-- getDecissionFromSelectionString(); Decission = {}", d);
    }
    return d;
  }

  // ----------------------------------------------------------------

  private static VariantDecission getVariantDecissionFromString(final String[] parts) {
    VariantDecission vd = null;
    try {
      final String purposeID = parts[1].trim();
      final String variantID = parts[2].trim();
      vd = new VariantDecission(purposeID, variantID);
    }
    catch (final Exception ex) {
      // string is null or not in the expected format
      vd = null;
    }
    return vd;
  }

  private static FeatureDecission getFeatureDecissionFromString(final String[] parts) {
    FeatureDecission fd = null;
    try {
      final String variantID = parts[1].trim();
      final String featureID = parts[2].trim();
      final String featureValueID = parts[3].trim();
      fd = new FeatureDecission(variantID, featureID, featureValueID);
    }
    catch (final Exception ex) {
      // string is null or not in the expected format
      fd = null;
    }
    return fd;
  }

  private static ConceptDecission getConceptDecissionFromString(final String[] parts) {
    ConceptDecission cd = null;
    try {
      final String variantID = parts[1].trim();
      final String conceptID = parts[2].trim();
      cd = new ConceptDecission(variantID, conceptID);
    }
    catch (final Exception ex) {
      // string is null or not in the expected format
      cd = null;
    }
    return cd;
  }

  // ----------------------------------------------------------------

  private SelectionHelper() {
    // prevent instantiation
  }
}
