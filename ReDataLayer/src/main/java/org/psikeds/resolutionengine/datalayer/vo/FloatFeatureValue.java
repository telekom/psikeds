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

  protected BigDecimal floatValue;
  protected int scale;

  public FloatFeatureValue() {
    super(null, null, Feature.VALUE_TYPE_FLOAT, null);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final String val) {
    super(featureID, featureValueID, Feature.VALUE_TYPE_FLOAT, null);
    this.setValue(val);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final BigDecimal val) {
    super(featureID, featureValueID, Feature.VALUE_TYPE_FLOAT, null);
    this.setValue(val);
  }

  public FloatFeatureValue(final String featureID, final String featureValueID, final float val, final int scale) {
    super(featureID, featureValueID, Feature.VALUE_TYPE_FLOAT, null);
    this.setValue(val, scale);
  }

  public int getScale() {
    return this.scale;
  }

  public void setScale(final int newScale) {
    if (this.scale != newScale) {
      this.scale = newScale;
      if ((this.floatValue != null) && (this.scale > 0)) {
        final BigDecimal newFloatValue = this.floatValue.setScale(this.scale, BigDecimal.ROUND_HALF_UP);
        this.setValue(newFloatValue); // mathematically round to new scale
      }
    }
  }

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#getValue()
   */
  @Override
  public String getValue() {
    if (this.floatValue == null) {
      // no value, nothing to do
      return null;
    }
    if (this.scale > 0) {
      final StringBuilder formatString = new StringBuilder("0.");
      for (int i = 0; i < this.scale; i++) {
        formatString.append('0');
      }
      // format value according to scale
      final DecimalFormat valueFormat = new DecimalFormat(formatString.toString());
      final String str = valueFormat.format(this.floatValue);
      return str.replaceAll(",", "."); // BD cannot handle colons
    }
    // no scale --> default string representation
    return this.floatValue.toString();
  }

  /**
   * @see org.psikeds.resolutionengine.datalayer.vo.FeatureValue#setValue(java.lang.String)
   */
  @Override
  public void setValue(final String val) {
    BigDecimal bd = null;
    try {
      bd = new BigDecimal(val.replaceAll(",", ".")); // BD cannot handle colons
    }
    catch (final Exception ex) {
      bd = null;
    }
    this.setValue(bd);
  }

  @JsonIgnore
  public void setValue(final BigDecimal val) {
    this.value = null; // string represenation of value is never used for float-implementation!
    this.floatValue = val;
    this.scale = (this.floatValue == null ? 0 : this.floatValue.scale());
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
    if (this.floatValue == null) {
      throw new IllegalArgumentException("Empty Float-Feature-Value!");
    }
    try {
      return this.floatValue.floatValue();
    }
    catch (final Exception ex) {
      throw new NumberFormatException("Illegal Float-Feature-Value: " + ex.getMessage());
    }
  }
}
