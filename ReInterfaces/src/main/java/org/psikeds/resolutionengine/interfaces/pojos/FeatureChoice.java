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
package org.psikeds.resolutionengine.interfaces.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A possible Choice: Which Values can be choosen for a certain Feature?
 * 
 * Note 1: PurposeIDs and VariantIDs must reference existing Objects!
 * 
 * Note 2: If the Purpose is a Root-Purpose, there is no Parent-Variant,
 * i.e. Parent-Variant is null.
 * 
 * Note 3: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureChoice")
public class FeatureChoice extends Choice implements Serializable {

  private static final long serialVersionUID = 1L;

  private Variant parentVariant;
  private Feature feature;
  private List<String> values;

  public FeatureChoice() {
    this(null, null, null);
  }

  public FeatureChoice(final Variant parentVariant, final FeatureValue value) {
    this(parentVariant, value, null);
    addValue(value.getValue());
  }

  public FeatureChoice(final Variant parentVariant, final Feature feature, final List<String> values) {
    super(parentVariant, feature);
    this.parentVariant = parentVariant;
    this.feature = feature;
    this.values = values;
  }

  public Variant getParentVariant() {
    return this.parentVariant;
  }

  public void setParentVariant(final Variant parentVariant) {
    this.parentVariant = parentVariant;
  }

  public Feature getFeature() {
    return this.feature;
  }

  public void setFeature(final Feature feature) {
    this.feature = feature;
  }

  public List<String> getValues() {
    if (this.values == null) {
      this.values = new ArrayList<String>();
    }
    return this.values;
  }

  public void setValues(final List<String> values) {
    this.values = values;
  }

  public void addValue(final String value) {
    if (value != null) {
      getValues().add(value);
    }
  }

  public void setValue(final String value) {
    getValues().clear();
    addValue(value);
  }

  @JsonIgnore
  public void setFeatureValue(final FeatureValue fv) {
    if (fv != null) {
      setFeature(fv);
      setValue(fv.getValue());
      // update internal id of this pojo
      setId(composeId(this.getParentVariant(), this.getFeature()));
    }
  }

  /**
   * Check whether a made Decission matches to this Choice, i.e. whether
   * the Client selected one of the allowed Values for the Feature.
   * 
   * @param decission
   * @return feature an value if matching, null else
   */
  @Override
  public FeatureValue matches(final Decission decission) {
    try {
      final FeatureDecission fd = (FeatureDecission) decission;
      final String vid = this.parentVariant.getId();
      if (vid.equals(fd.getVariantID())) {
        final String fid = this.feature.getId();
        if (fid.equals(fd.getFeatureID())) {
          for (final String v : getValues()) {
            final String val = fd.getFeatureValue();
            if (val.equals(v)) {
              if (this.feature instanceof FeatureValue) {
                final FeatureValue fv = (FeatureValue) this.feature;
                fv.setValue(val);
                return fv;
              }
              else {
                return new FeatureValue(this.feature, val);
              }
            }
          }
        }
      }
    }
    catch (final Exception ex) {
      // Either not a FeatureDecission or one of the Objects was NULL
    }
    return null;
  }
}
