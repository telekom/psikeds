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
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This is a Float-Feature-Value, i.e. a possible decimal Number that can
 * be assigned to an Attribute.
 * 
 * A Scale larger than zero controls the String-Representation of the
 * Float-Value, i.e. the number of digits to the right of the decimal point.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FloatFeatureValue")
public class FloatFeatureValue extends FeatureValue implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final int ROUNDING_MATHEMATICAL = BigDecimal.ROUND_HALF_UP;
  public static final int ROUNDING_TRUNCATE = BigDecimal.ROUND_DOWN;
  public static final int ROUNDING_EXACT = BigDecimal.ROUND_UNNECESSARY; // throws exception if rounding looses information!

  public static final int DEFAULT_ROUNDING_MODE = ROUNDING_MATHEMATICAL;
  public static final int MIN_FLOAT_SCALE = 0;

  protected BigDecimal decimalValue;
  protected int scale = MIN_FLOAT_SCALE;
  protected int roundingMode = DEFAULT_ROUNDING_MODE;

  public FloatFeatureValue() {
    this(null, null, ROUNDING_MATHEMATICAL);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final int roundingMode) {
    super(featureID, featureValueID, Feature.VALUE_TYPE_FLOAT, null);
    this.setRoundingMode(roundingMode);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final String val) {
    this(featureID, featureValueID, val, DEFAULT_ROUNDING_MODE);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final String val, final int roundingMode) {
    this(featureID, featureValueID, roundingMode);
    this.setValue(val);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final BigDecimal val) {
    this(featureID, featureValueID, val, DEFAULT_ROUNDING_MODE);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final BigDecimal val, final int roundingMode) {
    this(featureID, featureValueID, roundingMode);
    this.setValue(val);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final float val, final int scale) {
    this(featureID, featureValueID, val, scale, DEFAULT_ROUNDING_MODE);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final float val, final int scale, final int roundingMode) {
    this(featureID, featureValueID, roundingMode);
    this.setValue(val, scale);
  }

  public int getRoundingMode() {
    return this.roundingMode;
  }

  /**
   * Rounding Mode as defined in BigDecimal
   * 
   * @param roundingMode
   * @see java.math.BigDecimal
   */
  public void setRoundingMode(final int roundingMode) {
    this.roundingMode = (roundingMode < 0 ? DEFAULT_ROUNDING_MODE : roundingMode);
  }

  public int getScale() {
    return this.scale;
  }

  public void setScale(final int newScale) {
    if (this.scale != newScale) {
      this.scale = newScale;
      if ((this.decimalValue != null) && (this.scale > MIN_FLOAT_SCALE)) {
        final BigDecimal bd = this.decimalValue.setScale(this.scale, this.roundingMode);
        this.setValue(bd);
      }
    }
  }

  // ----------------------------------------------------------------

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#getValue()
   */
  @Override
  public String getValue() {
    if (this.decimalValue == null) {
      // no value, nothing to do
      return null;
    }
    if (this.scale > MIN_FLOAT_SCALE) {
      final StringBuilder formatString = new StringBuilder("0.");
      for (int i = 0; i < this.scale; i++) {
        formatString.append('0');
      }
      // format value according to scale
      final DecimalFormat valueFormat = new DecimalFormat(formatString.toString());
      final String str = valueFormat.format(this.decimalValue);
      return str.replaceAll(",", "."); // BD cannot handle colons :-(
    }
    // no scale --> default string representation
    return this.decimalValue.toString();
  }

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#setValue(java.lang.String)
   */
  @Override
  public void setValue(final String val) {
    BigDecimal bd = null;
    try {
      bd = new BigDecimal(val.replaceAll(",", ".")); // BD cannot handle colons :-(
    }
    catch (final Exception ex) {
      bd = null;
    }
    this.setValue(bd);
  }

  @JsonIgnore
  public void setValue(final BigDecimal val) {
    this.value = null; // string represenation of value is never used for float-implementation!
    this.decimalValue = val;
    this.scale = (this.decimalValue == null ? MIN_FLOAT_SCALE : this.decimalValue.scale());
  }

  @JsonIgnore
  public void setValue(final float val, final int scale) {
    this.setValue(String.valueOf(val));
    this.setScale(scale);
  }

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#toFloatValue()
   */
  @Override
  @JsonIgnore
  public float toFloatValue() {
    try {
      return this.decimalValue.floatValue();
    }
    catch (final Exception ex) {
      throw new NumberFormatException("Illegal Float-Value: " + ex.getMessage());
    }
  }

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#toIntegerValue()
   */
  @Override
  @JsonIgnore
  public long toIntegerValue() {
    return toIntegerValue(this.decimalValue, this.roundingMode);
  }

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#toBigDecimal()
   */
  @Override
  @JsonIgnore
  public BigDecimal toBigDecimal() {
    return this.decimalValue;
  }

  // ----------------------------------------------------------------

  public static long toIntegerValue(final BigDecimal val, final int rounding) {
    try {
      final BigDecimal bd = val.setScale(MIN_FLOAT_SCALE, rounding); // no scale, no fraction part
      return bd.longValue();
    }
    catch (final Exception ex) {
      throw new NumberFormatException("Illegal Integer-Value: " + ex.getMessage());
    }
  }
}
