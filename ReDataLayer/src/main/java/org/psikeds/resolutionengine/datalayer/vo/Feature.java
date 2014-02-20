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
package org.psikeds.resolutionengine.datalayer.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This Object defines a Feature, i.e. Type and possible Values of an Attribute
 * that can be assigned to a Variant.
 * 
 * Note: Feature-ID must be globally unique!
 * 
 * @author marco@juliano.de
 * 
 */
public class Feature<T> extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private Class<? extends T> valueType;
  private List<T> values;

  public Feature() {
    this(null, null, null, null);
  }

  public Feature(final String label, final String description, final String featureID, final Class<? extends T> valueType) {
    this(label, description, featureID, valueType, null);
  }

  public Feature(final String label, final String description, final String featureID, final Class<? extends T> valueType, final List<T> values) {
    super(featureID);
    this.label = label;
    this.description = description;
    this.valueType = valueType;
    this.values = values;
  }

  // ----------------------------------------------------------------

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<T> getValues() {
    if (this.values == null) {
      this.values = new ArrayList<T>();
    }
    return this.values;
  }

  public void addValue(final T value) {
    getValues().add(value);
  }

  public void setValues(final List<T> values) {
    this.values = values;
  }

  public Class<? extends T> getValueType() {
    return this.valueType;
  }

  public void setValueType(final Class<? extends T> valueType) {
    this.valueType = valueType;
  }

  // ----------------------------------------------------------------

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType, final List<String> values) {
    if ("integer".equalsIgnoreCase(valueType)) {
      final Feature<Integer> fi = new Feature<Integer>(label, description, featureID, Integer.class);
      for (final String val : values) {
        try {
          final Integer i = new Integer(Integer.parseInt(val.trim()));
          fi.addValue(i);
        }
        catch (final Exception ex) {
          throw new IllegalArgumentException("Not an Integer Value: " + val, ex);
        }
      }
      return fi;
    }
    else if ("float".equalsIgnoreCase(valueType)) {
      final Feature<Float> ff = new Feature<Float>(label, description, featureID, Float.class);
      for (final String val : values) {
        try {
          final Float f = new Float(Float.parseFloat(val.trim()));
          ff.addValue(f);
        }
        catch (final Exception ex) {
          throw new IllegalArgumentException("Not a Float Value: " + val, ex);
        }
      }
      return ff;
    }
    else if ("string".equalsIgnoreCase(valueType)) {
      return new Feature<String>(label, description, featureID, String.class, values);
    }
    else {
      throw new IllegalArgumentException("Unsupported Value Type: " + valueType);
    }
  }

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType, final String minInclusive, final String maxExclusive) {
    return getFeature(label, description, featureID, valueType, minInclusive, maxExclusive, null);
  }

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType, final String minInclusive, final String maxExclusive,
      final String step) {
    if ("integer".equalsIgnoreCase(valueType)) {
      final Feature<Integer> fi = new Feature<Integer>(label, description, featureID, Integer.class);
      int inc;
      int min;
      int max;
      try {
        inc = Integer.parseInt(step.trim());
        if (inc <= 0) {
          throw new IllegalArgumentException();
        }
      }
      catch (final Exception ex) {
        inc = 1; // default
      }
      try {
        min = Integer.parseInt(minInclusive.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not an Integer Value: " + minInclusive, ex);
      }
      try {
        max = Integer.parseInt(maxExclusive.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not an Integer Value: " + maxExclusive, ex);
      }
      if (max < min) {
        throw new IllegalArgumentException("maxExclusive must not be smaller than minInclusive!");
      }
      for (int i = min; i < max; i = i + inc) {
        fi.addValue(new Integer(i));
      }
      return fi;
    }
    else if ("float".equalsIgnoreCase(valueType)) {
      final Feature<Float> ff = new Feature<Float>(label, description, featureID, Float.class);
      float inc;
      float min;
      float max;
      try {
        inc = Float.parseFloat(step.trim());
        if (inc <= 0.0f) {
          throw new IllegalArgumentException();
        }
      }
      catch (final Exception ex) {
        inc = 1.0f; // default
      }
      try {
        min = Float.parseFloat(minInclusive.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not a Float Value: " + minInclusive, ex);
      }
      try {
        max = Float.parseFloat(maxExclusive.trim());
      }
      catch (final Exception ex) {
        throw new IllegalArgumentException("Not a Float Value: " + maxExclusive, ex);
      }
      if (max < min) {
        throw new IllegalArgumentException("maxExclusive must not be smaller than minInclusive!");
      }
      for (float f = min; f < max; f = f + inc) {
        ff.addValue(new Float(f));
      }
      return ff;
    }
    else if ("string".equalsIgnoreCase(valueType)) {
      throw new IllegalArgumentException("Value Type String is not allowed for Ranges!");
    }
    else {
      throw new IllegalArgumentException("Unsupported Value Type: " + valueType);
    }
  }
}
