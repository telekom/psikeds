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

  public static final String SELECT_REGEXP = String.valueOf(POJO.COMPOSE_ID_SEPARATOR);

  public static String createSelectionString(final String... ids) {
    return POJO.composeId(ids);
  }

  public static String createSelectionString(final POJO... pojos) {
    return POJO.composeId(pojos);
  }

  public static Decission getDecissionFromString(final String selected) {
    Decission d = null;
    try {
      final String[] parts = selected.split(SELECT_REGEXP);
      if (parts.length == 2) {
        d = getVariantDecissionFromString(parts);
      }
      else if (parts.length == 3) {
        d = getFeatureDecissionFromString(parts);
      }
    }
    catch (final Exception ex) {
      // string is null or not in the expected format
      d = null;
    }
    return d;
  }

  // ----------------------------------------------------------------

  private static VariantDecission getVariantDecissionFromString(final String[] parts) {
    VariantDecission vd = null;
    try {
      final String purposeID = parts[0].trim();
      final String variantID = parts[1].trim();
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
      final String variantID = parts[0].trim();
      final String featureID = parts[1].trim();
      final String featureValue = parts[2].trim();
      fd = new FeatureDecission(variantID, featureID, featureValue);
    }
    catch (final Exception ex) {
      // string is null or not in the expected format
      fd = null;
    }
    return fd;
  }

  // ----------------------------------------------------------------

  private SelectionHelper() {
    // prevent instantiation
  }
}
