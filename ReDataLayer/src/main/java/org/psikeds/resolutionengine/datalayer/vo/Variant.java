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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

/**
 * A single Variant. Variants can optionally have certain Features,
 * i.e. hold a List of IDs of the referenced Features.
 * 
 * Note 1: ID must be globally unique.
 * 
 * Note 2: FeatureIDs must reference existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Variant")
public class Variant extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private List<String> featureIds;
  private String defaultFeatureValue;

  public Variant() {
    this(null);
  }

  public Variant(final String variantID) {
    this(variantID, null, variantID);
  }

  public Variant(final String variantID, final List<String> featureIds) {
    this(variantID);
    this.featureIds = featureIds;
  }

  public Variant(final String label, final String description, final String variantID) {
    this(label, description, variantID, null, null);
  }

  public Variant(final String label, final String description, final String variantID, final List<String> featureIds, final String defaultFeatureValue) {
    super(variantID);
    this.label = label;
    this.description = description;
    this.featureIds = featureIds;
    this.defaultFeatureValue = defaultFeatureValue;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String value) {
    this.label = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String value) {
    this.description = value;
  }

  public String getVariantID() {
    return getId();
  }

  public void setVariantID(final String variantID) {
    setId(variantID);
  }

  public List<String> getFeatureIds() {
    if (this.featureIds == null) {
      this.featureIds = new ArrayList<String>();
    }
    return this.featureIds;
  }

  public void setFeatureIds(final List<String> featureIds) {
    this.featureIds = featureIds;
  }

  public boolean addFeatureId(final String featureId) {
    return (!StringUtils.isEmpty(featureId) && getFeatureIds().add(featureId));
  }

  public boolean addFeature(final Feature<?> feature) {
    return ((feature != null) && addFeatureId(feature.getId()));
  }

  public String getDefaultFeatureValue() {
    return this.defaultFeatureValue;
  }

  public void setDefaultFeatureValue(final String defaultFeatureValue) {
    this.defaultFeatureValue = defaultFeatureValue;
  }
}
