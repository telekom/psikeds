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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * This is a Feature-Definition/Declaration, i.e. Type and possible Values of an
 * Attribute that can be assigned to a Variant.
 * 
 * Assumption is that we have a discrete number of values within a reasonable size,
 * i.e. a quite small number of values. Might need a second thought and refactoring
 * if large value ranges and mathematical funtions must be supported in the future!
 * 
 * Note: Feature-ID must be globally unique!
 * 
 * @author marco@juliano.de
 * 
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = StringFeature.class, name = "StringFeature"),
    @JsonSubTypes.Type(value = IntegerFeature.class, name = "IntegerFeature"),
    @JsonSubTypes.Type(value = FloatFeature.class, name = "FloatFeature"), })
public abstract class Feature<T> extends ValueObject {

  private static final long serialVersionUID = 1L;

  public static final String VALUE_TYPE_STRING = "string";
  public static final String VALUE_TYPE_INTEGER = "integer";
  public static final String VALUE_TYPE_FLOAT = "float";

  public static final int DEFAULT_RANGE_STEP = 1;
  public static final int MINIMUM_RANGE_STEP = 0;
  public static final int MAXIMUM_RANGE_SIZE = 100;

  protected String label;
  protected String description;
  protected List<T> values;

  protected Feature(final String label, final String description, final String featureID, final List<T> values) {
    super(featureID);
    setLabel(label);
    setDescription(description);
    setValues(values);
  }

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

  public String getFeatureID() {
    return getId();
  }

  public void setFeatureID(final String featureID) {
    setId(featureID);
  }

  public List<T> getValues() {
    if (this.values == null) {
      this.values = new ArrayList<T>();
    }
    return this.values;
  }

  public void addValue(final T value) {
    if (value != null) {
      getValues().add(value);
    }
  }

  public void addValue(final List<T> values) {
    if ((values != null) && !values.isEmpty()) {
      getValues().addAll(values);
    }
  }

  public void setValues(final List<T> values) {
    this.values = values;
  }

  public void clearValues() {
    if (this.values != null) {
      this.values.clear();
      this.values = null;
    }
  }

  public abstract String getValueType();

  // ----------------------------------------------------------------

  /**
   * @return true if Object is an Feature with same ID and same Value-Type; false else
   * @see jorg.psikeds.resolutionengine.datalayer.vo.ValueObject#equals(Object obj)
   */
  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Feature) || (this.compareTo(obj) != 0)) {
      return false;
    }
    final Feature<?> f = (Feature<?>) obj;
    return this.getValueType().equals(f.getValueType());
  }

  // ----------------------------------------------------------------

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType, final List<String> values) {
    if (VALUE_TYPE_INTEGER.equalsIgnoreCase(valueType)) {
      return getIntegerFeature(label, description, featureID, values);
    }
    else if (VALUE_TYPE_FLOAT.equalsIgnoreCase(valueType)) {
      return getFloatFeature(label, description, featureID, values);
    }
    else if (VALUE_TYPE_STRING.equalsIgnoreCase(valueType)) {
      return getStringFeature(label, description, featureID, values);
    }
    else {
      throw new IllegalArgumentException("Unsupported Value Type: " + valueType);
    }
  }

  public static IntegerFeature getIntegerFeature(final String label, final String description, final String featureID, final List<String> values) {
    final IntegerFeature intfeat = new IntegerFeature(label, description, featureID);
    if (values != null) {
      for (final String val : values) {
        try {
          final Integer i = new Integer(Integer.parseInt(val.trim()));
          intfeat.addValue(i);
        }
        catch (final Exception ex) {
          throw new IllegalArgumentException("Not an Integer Value: " + val, ex);
        }
      }
    }
    return intfeat;
  }

  public static FloatFeature getFloatFeature(final String label, final String description, final String featureID, final List<String> values) {
    final FloatFeature floatfeat = new FloatFeature(label, description, featureID);
    if (values != null) {
      for (final String val : values) {
        try {
          final Float f = new Float(Float.parseFloat(val.trim()));
          floatfeat.addValue(f);
        }
        catch (final Exception ex) {
          throw new IllegalArgumentException("Not a Float Value: " + val, ex);
        }
      }
    }
    return floatfeat;
  }

  public static StringFeature getStringFeature(final String label, final String description, final String featureID, final List<String> values) {
    return new StringFeature(label, description, featureID, values);
  }

  // ----------------------------------------------------------------

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType, final String minInclusive, final String maxExclusive) {
    return getFeature(label, description, featureID, valueType, minInclusive, maxExclusive, null, MAXIMUM_RANGE_SIZE);
  }

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType,
      final String minInclusive, final String maxExclusive, final String step) {
    return getFeature(label, description, featureID, valueType, minInclusive, maxExclusive, step, MAXIMUM_RANGE_SIZE);
  }

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType,
      final String minInclusive, final String maxExclusive, final int maxSize) {
    return getFeature(label, description, featureID, valueType, minInclusive, maxExclusive, null, maxSize);
  }

  public static Feature<?> getFeature(final String label, final String description, final String featureID, final String valueType,
      final String minInclusive, final String maxExclusive, final String step, final int maxSize) {
    if (VALUE_TYPE_INTEGER.equalsIgnoreCase(valueType)) {
      int inc;
      int min;
      int max;
      try {
        inc = Integer.parseInt(step.trim());
      }
      catch (final Exception ex) {
        inc = DEFAULT_RANGE_STEP;
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
      return getIntegerFeature(label, description, featureID, min, max, inc, maxSize);
    }
    else if (VALUE_TYPE_FLOAT.equalsIgnoreCase(valueType)) {
      float inc;
      float min;
      float max;
      try {
        inc = Float.parseFloat(step.trim());
      }
      catch (final Exception ex) {
        inc = DEFAULT_RANGE_STEP;
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
      return getFloatFeature(label, description, featureID, min, max, inc, maxSize);
    }
    else if (VALUE_TYPE_STRING.equalsIgnoreCase(valueType)) {
      throw new IllegalArgumentException("Value Type String is not allowed for Ranges!");
    }
    else {
      throw new IllegalArgumentException("Unsupported Value Type: " + valueType);
    }
  }

  public static IntegerFeature getIntegerFeature(final String label, final String description, final String featureID,
      final int minInclusive, final int maxExclusive, final int step) {
    return getIntegerFeature(label, description, featureID, minInclusive, maxExclusive, step, MAXIMUM_RANGE_SIZE);
  }

  public static IntegerFeature getIntegerFeature(final String label, final String description, final String featureID,
      final int minInclusive, final int maxExclusive, final int step, final int maxSize) {
    if (maxExclusive < minInclusive) {
      throw new IllegalArgumentException("maxExclusive must not be smaller than minInclusive!");
    }
    if (MINIMUM_RANGE_STEP >= step) {
      throw new IllegalArgumentException("step must be larger than: " + MINIMUM_RANGE_STEP);
    }
    final IntegerFeature intfeat = new IntegerFeature(label, description, featureID);
    int count = 0;
    for (int i = minInclusive; (i < maxExclusive) && (count < maxSize); i = i + step) {
      intfeat.addValue(new Integer(i));
      count++;
    }
    return intfeat;
  }

  public static FloatFeature getFloatFeature(final String label, final String description, final String featureID,
      final float minInclusive, final float maxExclusive, final float step) {
    return getFloatFeature(label, description, featureID, minInclusive, maxExclusive, step, MAXIMUM_RANGE_SIZE);
  }

  public static FloatFeature getFloatFeature(final String label, final String description, final String featureID,
      final float minInclusive, final float maxExclusive, final float step, final float maxSize) {
    if (maxExclusive < minInclusive) {
      throw new IllegalArgumentException("maxExclusive must not be smaller than minInclusive!");
    }
    if (MINIMUM_RANGE_STEP >= step) {
      throw new IllegalArgumentException("step must be larger than: " + MINIMUM_RANGE_STEP);
    }
    final FloatFeature floatfeat = new FloatFeature(label, description, featureID);
    int count = 0;
    for (float f = minInclusive; (f < maxExclusive) && (count < maxSize); f = f + step) {
      floatfeat.addValue(new Float(f));
      count++;
    }
    return floatfeat;
  }
}
