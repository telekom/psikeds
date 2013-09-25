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
public class Variant extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String id;
  private List<String> featureIds;

  public Variant() {
    this(null, null, null);
  }

  public Variant(final String label, final String description, final String id) {
    this(label, description, id, null);
  }

  public Variant(final String label, final String description, final String id, final List<String> featureIds) {
    super();
    this.label = label;
    this.description = description;
    this.id = id;
    this.featureIds = featureIds;
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

  public String getId() {
    return this.id;
  }

  public void setId(final String value) {
    this.id = value;
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

  public void addFeatureId(final String featureId) {
    getFeatureIds().add(featureId);
  }

  public void addFeature(final Feature feature) {
    if (feature != null) {
      getFeatureIds().add(feature.getId());
    }
  }
}
