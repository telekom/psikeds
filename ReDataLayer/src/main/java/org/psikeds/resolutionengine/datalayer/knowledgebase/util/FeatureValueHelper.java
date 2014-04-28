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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;

/**
 * Helper for handling Features, Values and Ranges.
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class FeatureValueHelper {

  public static final int DEFAULT_RANGE_STEP = 1;
  public static final int MINIMUM_RANGE_STEP = 0;
  public static final int MAXIMUM_RANGE_SIZE = 500;

  private FeatureValueHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static boolean isValid(final FeatureValue fv) {
    if (fv == null) {
      return false;
    }
    if (StringUtils.isEmpty(fv.getFeatureID()) || StringUtils.isEmpty(fv.getFeatureValueID()) || StringUtils.isEmpty(fv.getType()) || StringUtils.isEmpty(fv.getValue())) {
      return false;
    }
    if (Feature.VALUE_TYPE_FLOAT.equalsIgnoreCase(fv.getType())) {
      try {
        final float f = Float.parseFloat(fv.getValue());
        return true;
      }
      catch (final Exception ex) {
        return false;
      }
    }
    else if (Feature.VALUE_TYPE_INTEGER.equalsIgnoreCase(fv.getType())) {
      try {
        final int i = Integer.parseInt(fv.getValue());
        return true;
      }
      catch (final Exception ex) {
        return false;
      }
    }
    return true;
  }

  public static boolean isValid(final Feature f) {
    if (f == null) {
      return false;
    }
    if (StringUtils.isEmpty(f.getFeatureID()) || StringUtils.isEmpty(f.getType())) {
      return false;
    }
    final List<FeatureValue> values = f.getValues();
    if ((values == null) || values.isEmpty()) {
      return false;
    }
    for (final FeatureValue fv : values) {
      if (!isValid(fv)) {
        return false;
      }
      if (!fv.getType().equals(f.getType())) {
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
    // two numbers (float or int) are compatible
    return true;
  }

  // ----------------------------------------------------------------

  public static List<FeatureValue> calculateRange(final String featureID, final String rangeID, final String type, final String min, final String max, final String inc) {
    return calculateRange(featureID, rangeID, type, min, max, inc, MAXIMUM_RANGE_SIZE);
  }

  public static List<FeatureValue> calculateRange(final String featureID, final String rangeID, final String type, final String min, final String max, final String inc, final int maxSize) {
    if (Feature.VALUE_TYPE_INTEGER.equalsIgnoreCase(type)) {
      int iInc;
      int iMin;
      int iMax;
      try {
        iInc = Integer.parseInt(inc.trim());
      }
      catch (final Exception ex) {
        iInc = DEFAULT_RANGE_STEP;
      }
      try {
        iMin = Integer.parseInt(min.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not an Integer Value: " + min, ex);
      }
      try {
        iMax = Integer.parseInt(max.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not an Integer Value: " + max, ex);
      }
      return calculateIntegerRange(featureID, rangeID, iMin, iMax, iInc, maxSize);
    }
    else if (Feature.VALUE_TYPE_FLOAT.equalsIgnoreCase(type)) {
      float fInc;
      float fMin;
      float fMax;
      try {
        fInc = Float.parseFloat(inc.trim());
      }
      catch (final Exception ex) {
        fInc = DEFAULT_RANGE_STEP;
      }
      try {
        fMin = Float.parseFloat(min.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not a Float Value: " + min, ex);
      }
      try {
        fMax = Float.parseFloat(max.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not a Float Value: " + max, ex);
      }
      return calculateFloatRange(featureID, rangeID, fMin, fMax, fInc, maxSize);
    }
    else if (Feature.VALUE_TYPE_STRING.equalsIgnoreCase(type)) {
      throw new IllegalArgumentException("Value Type String is not allowed for Ranges!");
    }
    else {
      throw new IllegalArgumentException("Unsupported Value Type: " + type);
    }
  }

  // ----------------------------------------------------------------

  public static List<FeatureValue> calculateIntegerRange(final String featureID, final String rangeID, final int min, final int max, final int inc) {
    return calculateIntegerRange(featureID, rangeID, min, max, inc, MAXIMUM_RANGE_SIZE);
  }

  public static List<FeatureValue> calculateIntegerRange(final String featureID, final String rangeID, final int min, final int max, final int inc, final int maxSize) {
    if (maxSize > MAXIMUM_RANGE_SIZE) {
      throw new IllegalArgumentException("Max-Size of Range must not be larger than: " + MAXIMUM_RANGE_SIZE);
    }
    if (max < min) {
      throw new IllegalArgumentException("Max must not be smaller than Min!");
    }
    if (MINIMUM_RANGE_STEP >= inc) {
      throw new IllegalArgumentException("Inc must be larger than: " + MINIMUM_RANGE_STEP);
    }
    final List<FeatureValue> lst = new ArrayList<FeatureValue>();
    int count = 1;
    for (int i = min; (i <= max) && (count <= maxSize); i = i + inc) {
      final String val = String.valueOf(i);
      final String num = String.valueOf(count);
      final String featureValueID = rangeID + "i" + num;
      final FeatureValue fv = new FeatureValue(featureID, featureValueID, Feature.VALUE_TYPE_INTEGER, val);
      lst.add(fv);
      count++;
    }
    return lst;
  }

  // ----------------------------------------------------------------

  public static List<FeatureValue> calculateFloatRange(final String featureID, final String rangeID, final float min, final float max, final float inc) {
    return calculateFloatRange(featureID, rangeID, min, max, inc, MAXIMUM_RANGE_SIZE);
  }

  public static List<FeatureValue> calculateFloatRange(final String featureID, final String rangeID, final float min, final float max, final float inc, final int maxSize) {
    if (maxSize > MAXIMUM_RANGE_SIZE) {
      throw new IllegalArgumentException("Max-Size of Range must not be larger than: " + MAXIMUM_RANGE_SIZE);
    }
    if (max < min) {
      throw new IllegalArgumentException("Max must not be smaller than Min!");
    }
    if (MINIMUM_RANGE_STEP >= inc) {
      throw new IllegalArgumentException("Inc must be larger than: " + MINIMUM_RANGE_STEP);
    }
    final List<FeatureValue> lst = new ArrayList<FeatureValue>();
    int count = 1;
    for (float f = min; (f <= max) && (count <= maxSize); f = f + inc) {
      final String val = String.valueOf(f);
      final String num = String.valueOf(count);
      final String featureValueID = rangeID + "f" + num;
      final FeatureValue fv = new FeatureValue(featureID, featureValueID, Feature.VALUE_TYPE_FLOAT, val);
      lst.add(fv);
      count++;
    }
    return lst;
  }
}
