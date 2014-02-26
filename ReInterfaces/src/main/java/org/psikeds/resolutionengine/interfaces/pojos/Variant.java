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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a single Variant. Variants can optionally
 * have certain Features, i.e. hold a List of IDs of the referenced Features.
 * 
 * Note 1: ID must be globally unique.
 * 
 * Note 2: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Variant")
public class Variant extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private Features features;

  public Variant() {
    this(null, null, null);
  }

  public Variant(final String label, final String description, final String variantID) {
    this(label, description, variantID, null);
  }

  public Variant(final String label, final String description, final String variantID, final Features features) {
    super(variantID);
    this.label = label;
    this.description = description;
    this.features = features;
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

  // ----------------------------------------------------------------

  public Features getFeatures() {
    if (this.features == null) {
      this.features = new Features();
    }
    return this.features;
  }

  public void setFeatures(final Features features) {
    clearFeatures();
    this.features = features;
  }

  public void addFeature(final FeatureDescription feature) {
    if (feature != null) {
      getFeatures().add(feature);
    }
  }

  public void addAllFeature(final Features features) {
    if ((features != null) && !features.isEmpty()) {
      getFeatures().addAll(features);
    }
  }

  public void clearFeatures() {
    if (this.features != null) {
      this.features.clear();
      this.features = null;
    }
  }

  public String getVariantID() {
    return getId();
  }

  public void setVariantID(final String variantID) {
    setId(variantID);
  }
}
