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

  public static final boolean DEFAULT_IS_SINGLETON = false;
  public static final boolean DEFAULT_IS_IMPLICIT = false;

  private String label;
  private String description;
  private boolean singleton;
  private boolean implicit;
  private List<String> featureIds; // distinct features referenced by all values and concepts
  private List<FeatureValue> featureValues;
  private List<Concept> concepts;

  public Variant() {
    this(null);
  }

  public Variant(final String variantID) {
    this(variantID, null, variantID);
  }

  public Variant(final String label, final String description, final String variantID) {
    this(label, description, variantID, DEFAULT_IS_SINGLETON, DEFAULT_IS_IMPLICIT);
  }

  public Variant(final String label, final String description, final String variantID, final boolean singleton, final boolean implicit) {
    this(label, description, variantID, singleton, implicit, null, null);
  }

  public Variant(final String label, final String description, final String variantID,
      final boolean singleton, final boolean implicit,
      final List<String> featureIds, final List<FeatureValue> featureValues) {
    this(label, description, variantID, singleton, implicit, featureIds, featureValues, null);
  }

  public Variant(final String label, final String description, final String variantID,
      final boolean singleton, final boolean implicit,
      final List<String> featureIds, final List<FeatureValue> featureValues,
      final List<Concept> concepts) {
    super(variantID);
    setLabel(label);
    setDescription(description);
    setSingleton(singleton);
    setImplicit(implicit);
    setFeatureIds(featureIds);
    setFeatureValues(featureValues);
    setConcepts(concepts);
  }

  // ----------------------------------------------------------------

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

  public boolean isSingleton() {
    return this.singleton;
  }

  public void setSingleton(final boolean singleton) {
    this.singleton = singleton;
  }

  public boolean isImplicit() {
    return this.implicit;
  }

  public void setImplicit(final boolean implicit) {
    this.implicit = implicit;
  }

  // ----------------------------------------------------------------

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
    return (!StringUtils.isEmpty(featureId) && !getFeatureIds().contains(featureId) && getFeatureIds().add(featureId));
  }

  public void clearFeatureIds() {
    if (this.featureIds != null) {
      this.featureIds.clear();
      this.featureIds = null;
    }
  }

  // ----------------------------------------------------------------

  public List<FeatureValue> getFeatureValues() {
    if (this.featureValues == null) {
      this.featureValues = new ArrayList<FeatureValue>();
    }
    return this.featureValues;
  }

  public void setFeatureValues(final List<FeatureValue> featureValues) {
    this.featureValues = featureValues;
  }

  public boolean addFeatureValue(final FeatureValue value) {
    return ((value != null) && getFeatureValues().add(value));
  }

  public void clearFeatureValues() {
    if (this.featureValues != null) {
      this.featureValues.clear();
      this.featureValues = null;
    }
  }

  // ----------------------------------------------------------------

  public List<Concept> getConcepts() {
    if (this.concepts == null) {
      this.concepts = new ArrayList<Concept>();
    }
    return this.concepts;
  }

  public void setConcepts(final List<Concept> concepts) {
    this.concepts = concepts;
  }

  public boolean addConcept(final Concept value) {
    return ((value != null) && getConcepts().add(value));
  }

  public void clearConcepts() {
    if (this.concepts != null) {
      this.concepts.clear();
      this.concepts = null;
    }
  }
}
