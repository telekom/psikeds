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
package org.psikeds.resolutionengine.datalayer.knowledgebase.util;

import java.math.BigDecimal;
import java.util.Comparator;

import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;

public class FeatureValueComparator implements Comparator<FeatureValue> {

  /**
   * Compares the first FeatureValue with the second one. Returns a
   * negative number, zero, or a positive number if the Value of
   * the first Object is less than, equal to, or greater than the
   * second Object.
   * 
   * Note: Different from the Comparable-Implementation of the
   * FeatureValue-Class itself, this method will just check the Values
   * and irgnore any IDs or Types!
   * 
   * @see java.util.Comparator#compare(Object, Object)
   * 
   */
  @Override
  public int compare(final FeatureValue fv1, final FeatureValue fv2) {
    // 1. comparison of values as numbers
    BigDecimal bd1 = null;
    BigDecimal bd2 = null;
    try {
      bd1 = fv1.toBigDecimal();
    }
    catch (final Exception ex) {
      bd1 = null;
    }
    try {
      bd2 = fv2.toBigDecimal();
    }
    catch (final Exception ex) {
      bd2 = null;
    }
    if ((bd1 != null) && (bd2 != null)) {
      // mathematically compare numbers
      return bd1.compareTo(bd2);
    }
    if ((bd1 == null) && (bd2 != null)) {
      return -1;
    }
    if ((bd1 != null) && (bd2 == null)) {
      return 1;
    }
    // 2. fallback to comparison of values as strings
    String val1 = null;
    try {
      val1 = fv1.getValue().trim();
    }
    catch (final Exception ex) {
      val1 = null;
    }
    String val2 = null;
    try {
      val2 = fv2.getValue().trim();
    }
    catch (final Exception ex) {
      val2 = null;
    }
    if ((val1 != null) && (val2 != null)) {
      // alphabetically compare strings
      return val1.compareTo(val2);
    }
    if ((val1 == null) && (val2 != null)) {
      return -1;
    }
    if ((val1 != null) && (val2 == null)) {
      return 1;
    }
    // null equals null
    return 0;
  }
}
