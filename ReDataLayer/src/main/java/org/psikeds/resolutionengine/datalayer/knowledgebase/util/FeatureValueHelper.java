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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.FloatFeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.IntegerFeatureValue;

/**
 * Helper for handling Features, Values and Ranges.
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class FeatureValueHelper {

  public static final int DEFAULT_RANGE_STEP = 1;
  public static final int DEFAULT_MAXIMUM_SIZE = 500;

  public static final FeatureValueComparator COMPARATOR = new FeatureValueComparator();

  private FeatureValueHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static boolean isValid(final FeatureValue fv) {
    if ((fv == null) || StringUtils.isEmpty(fv.getFeatureID()) || StringUtils.isEmpty(fv.getFeatureValueID()) || StringUtils.isEmpty(fv.getType()) || StringUtils.isEmpty(fv.getValue())) {
      return false;
    }
    if (fv.isFloatValue()) {
      try {
        fv.toFloatValue();
        return true;
      }
      catch (final Exception ex) {
        return false;
      }
    }
    else if (fv.isIntegerValue()) {
      try {
        fv.toIntegerValue();
        return true;
      }
      catch (final Exception ex) {
        return false;
      }
    }
    return true;
  }

  public static boolean isValid(final Feature f) {
    if ((f == null) || StringUtils.isEmpty(f.getFeatureID()) || StringUtils.isEmpty(f.getType())) {
      return false;
    }
    final List<FeatureValue> values = f.getValues();
    if ((values == null) || values.isEmpty()) {
      return false;
    }
    for (final FeatureValue fv : values) {
      if (!isValid(fv) || !fv.getType().equals(f.getType()) || !fv.getFeatureID().equals(f.getFeatureID())) {
        return false;
      }
    }
    return true;
  }

  // ----------------------------------------------------------------

  public static boolean isCompatible(final Feature f1, final Feature f2) {
    return ((f1 != null) && (f2 != null) && isCompatible(f1.getType(), f2.getType()));
  }

  public static boolean isCompatible(final FeatureValue fv1, final FeatureValue fv2) {
    return ((fv1 != null) && (fv2 != null) && isCompatible(fv1.getType(), fv2.getType()));
  }

  public static boolean isCompatible(final Feature f1, final FeatureValue fv2) {
    return ((f1 != null) && (fv2 != null) && isCompatible(f1.getType(), fv2.getType()));
  }

  public static boolean isCompatible(final FeatureValue fv1, final Feature f2) {
    return isCompatible(f2, fv1);
  }

  public static boolean isCompatible(final String type1, final String type2) {
    if (StringUtils.isEmpty(type1) || StringUtils.isEmpty(type2)) {
      // empty type are never compatible
      return false;
    }
    if (type1.equals(type2)) {
      // equal types are always compatible
      return true;
    }
    if (Feature.VALUE_TYPE_STRING.equals(type1) || Feature.VALUE_TYPE_STRING.equals(type2)) {
      // string and number are not compatible
      return false;
    }
    // two numbers (either float or int) are compatible
    return true;
  }

  // ----------------------------------------------------------------

  public static List<IntegerFeatureValue> calculateIntegerRange(final String featureID, final String rangeID, final long min, final long max, final long inc) {
    return calculateIntegerRange(featureID, rangeID, min, max, inc, DEFAULT_MAXIMUM_SIZE);
  }

  public static List<IntegerFeatureValue> calculateIntegerRange(final String featureID, final String rangeID, final long min, final long max, long inc, final long maxSize) {
    if (inc == 0) {
      inc = DEFAULT_RANGE_STEP;
    }
    if ((inc > 0) && (max < min)) {
      throw new IllegalArgumentException("Maximum of Range " + rangeID + " must not be smaller than Minimum!");
    }
    if ((inc < 0) && (max > min)) {
      throw new IllegalArgumentException("Minimum of Range " + rangeID + " must not be smaller than Maximum!");
    }
    final List<IntegerFeatureValue> lst = new ArrayList<IntegerFeatureValue>();
    long count = 1;
    for (long l = min; (l <= max) && (count <= maxSize); l = l + inc) {
      final String featureValueID = rangeID + "_I" + String.valueOf(count);
      final IntegerFeatureValue ifv = new IntegerFeatureValue(featureID, featureValueID, l);
      lst.add(ifv);
      count++;
    }
    return lst;
  }

  // ----------------------------------------------------------------

  public static List<FloatFeatureValue> calculateFloatRange(final String featureID, final String rangeID, final BigDecimal min, final BigDecimal max, final BigDecimal inc) {
    return calculateFloatRange(featureID, rangeID, min, max, inc, FloatFeatureValue.MIN_FLOAT_SCALE, FloatFeatureValue.DEFAULT_ROUNDING_MODE);
  }

  public static List<FloatFeatureValue> calculateFloatRange(final String featureID, final String rangeID, final BigDecimal min, final BigDecimal max, final BigDecimal inc,
      final int scale, final int roundingMode) {
    return calculateFloatRange(featureID, rangeID, min, max, inc, scale, roundingMode, DEFAULT_MAXIMUM_SIZE);
  }

  public static List<FloatFeatureValue> calculateFloatRange(final String featureID, final String rangeID, final BigDecimal min, final BigDecimal max, final BigDecimal inc,
      int scale, final int roundingMode, final int maxSize) {
    float finc = (inc == null ? 0.0f : inc.floatValue());
    if (finc == 0.0f) {
      finc = DEFAULT_RANGE_STEP;
    }
    else if (scale == FloatFeatureValue.MIN_FLOAT_SCALE) {
      // scale is based on the increment, if not explicitly specified otherwise
      scale = inc.scale();
    }
    final float fmin = (min == null ? 0.0f : min.floatValue());
    final float fmax = (max == null ? 0.0f : max.floatValue());
    if ((finc > 0.0f) && (fmax < fmin)) {
      throw new IllegalArgumentException("Maximum of Range " + rangeID + " must not be smaller than Minimum!");
    }
    if ((finc < 0.0f) && (fmax > fmin)) {
      throw new IllegalArgumentException("Minimum of Range " + rangeID + " must not be smaller than Maximum!");
    }
    final List<FloatFeatureValue> lst = new ArrayList<FloatFeatureValue>();
    int count = 1;
    for (float f = fmin; (f <= fmax) && (count <= maxSize); f = f + finc) {
      final String featureValueID = rangeID + "_F" + String.valueOf(count);
      final FloatFeatureValue ffv = new FloatFeatureValue(featureID, featureValueID, f, scale, roundingMode);
      lst.add(ffv);
      count++;
    }
    return lst;
  }

  // ----------------------------------------------------------------

  public static boolean isWithinRange(final String featureId, final String rangeID, final FeatureValue val) {
    if ((val == null) || StringUtils.isEmpty(featureId) || StringUtils.isEmpty(rangeID) || StringUtils.isEmpty(val.getFeatureValueID())) {
      return false;
    }
    // when we generate values for ranges the feature-value-id will always start with the correpsonding range-id
    return (featureId.equals(val.getFeatureID()) && val.getFeatureValueID().startsWith(rangeID));
  }

  // ----------------------------------------------------------------

  public static boolean isEqual(final FeatureValue fv1, final FeatureValue fv2) {
    return (COMPARATOR.compare(fv1, fv2) == 0);
  }

  public static boolean notEqual(final FeatureValue fv1, final FeatureValue fv2) {
    return !isEqual(fv1, fv2);
  }

  public static boolean greaterThan(final FeatureValue fv1, final FeatureValue fv2) {
    return (COMPARATOR.compare(fv1, fv2) > 0);
  }

  public static boolean lessThan(final FeatureValue fv1, final FeatureValue fv2) {
    return (COMPARATOR.compare(fv1, fv2) < 0);
  }

  public static boolean lessOrEqual(final FeatureValue fv1, final FeatureValue fv2) {
    return !greaterThan(fv1, fv2);
  }

  public static boolean greaterOrEqual(final FeatureValue fv1, final FeatureValue fv2) {
    return !lessThan(fv1, fv2);
  }

  // ----------------------------------------------------------------

  public static List<FeatureValue> isEqual(final List<FeatureValue> lst, final FeatureValue ref) {
    List<FeatureValue> result = null;
    if ((ref != null) && (lst != null) && !lst.isEmpty()) {
      result = new ArrayList<FeatureValue>();
      for (final FeatureValue fv : lst) {
        if (isEqual(fv, ref)) {
          result.add(fv);
        }
      }
    }
    return result;
  }

  public static List<FeatureValue> notEqual(final List<FeatureValue> lst, final FeatureValue ref) {
    List<FeatureValue> result = null;
    if (ref == null) {
      // everything is not equal to null
      result = lst;
    }
    else if ((lst != null) && !lst.isEmpty()) {
      result = new ArrayList<FeatureValue>();
      for (final FeatureValue fv : lst) {
        if (notEqual(fv, ref)) {
          result.add(fv);
        }
      }
    }
    return result;
  }

  public static List<FeatureValue> lessThan(final List<FeatureValue> lst, final FeatureValue ref) {
    List<FeatureValue> result = null;
    final int len = (lst == null ? 0 : lst.size());
    if ((ref != null) && (len > 0)) {
      sort(lst); // find requires sorted list
      final int idx = find(lst, ref);
      if (idx > 0) {
        // ref is contained in list
        result = new ArrayList<FeatureValue>(lst.subList(0, idx));
      }
      else {
        // ref is not contained in list
        final FeatureValue min = min(lst);
        result = (lessThan(ref, min) ? null : lst);
      }
    }
    return result;
  }

  public static List<FeatureValue> lessOrEqual(final List<FeatureValue> lst, final FeatureValue ref) {
    List<FeatureValue> result = null;
    final int len = (lst == null ? 0 : lst.size());
    if ((ref != null) && (len > 0)) {
      sort(lst); // find requires sorted list
      final int idx = find(lst, ref);
      if (idx >= 0) {
        // ref is contained in list
        result = new ArrayList<FeatureValue>(lst.subList(0, idx + 1));
      }
      else {
        // ref is not contained in list
        final FeatureValue min = min(lst);
        result = (lessThan(ref, min) ? null : lst);
      }
    }
    return result;
  }

  public static List<FeatureValue> greaterThan(final List<FeatureValue> lst, final FeatureValue ref) {
    List<FeatureValue> result = null;
    if (ref == null) {
      // everything is greater than null
      result = lst;
    }
    else {
      final int len = (lst == null ? 0 : lst.size());
      if (len > 0) {
        sort(lst); // find requires sorted list
        final int idx = find(lst, ref);
        if ((idx >= 0) && (idx < (len - 2))) {
          // ref is contained in list
          result = new ArrayList<FeatureValue>(lst.subList(idx + 1, len));
        }
        else {
          // ref is not contained in list
          final FeatureValue max = max(lst);
          result = (greaterThan(ref, max) ? null : lst);
        }
      }
    }
    return result;
  }

  public static List<FeatureValue> greaterOrEqual(final List<FeatureValue> lst, final FeatureValue ref) {
    List<FeatureValue> result = null;
    if (ref == null) {
      // everything is greater or equal null
      result = lst;
    }
    else {
      final int len = (lst == null ? 0 : lst.size());
      if (len > 0) {
        sort(lst); // find requires sorted list
        final int idx = find(lst, ref);
        if ((idx >= 0) && (idx < (len - 1))) {
          // ref is contained in list
          result = new ArrayList<FeatureValue>(lst.subList(idx, len));
        }
        else {
          // ref is not contained in list
          final FeatureValue max = max(lst);
          result = (greaterThan(ref, max) ? null : lst);
        }
      }
    }
    return result;
  }

  // ----------------------------------------------------------------

  public static void sort(final List<FeatureValue> lst) {
    if ((lst != null) && !lst.isEmpty()) {
      Collections.sort(lst, COMPARATOR);
    }
  }

  public static void reverse(final List<FeatureValue> lst) {
    if ((lst != null) && !lst.isEmpty()) {
      Collections.reverse(lst);
    }
  }

  public static void shuffle(final List<FeatureValue> lst) {
    if ((lst != null) && !lst.isEmpty()) {
      Collections.shuffle(lst);
    }
  }

  public static FeatureValue min(final List<FeatureValue> randomList) {
    return ((randomList == null) || randomList.isEmpty() ? null : Collections.min(randomList, COMPARATOR));
  }

  public static FeatureValue max(final List<FeatureValue> randomList) {
    return ((randomList == null) || randomList.isEmpty() ? null : Collections.max(randomList, COMPARATOR));
  }

  public static int find(final List<FeatureValue> sortedList, final FeatureValue fv) {
    if ((fv == null) || (sortedList == null) || sortedList.isEmpty()) {
      return -1;
    }
    // note: binary search requires sorted list. otherwise
    // method will not fail, however result is undefined!
    return Collections.binarySearch(sortedList, fv, COMPARATOR);
  }
}
