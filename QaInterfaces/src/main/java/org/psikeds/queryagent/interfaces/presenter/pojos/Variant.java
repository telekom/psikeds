/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a single Variant. Variants can optionally
 * have Features or Concepts, i.e. Descriptions of Attributes of this Variant.
 * 
 * Note: Variant-ID must be globally unique.
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
  private Concepts concepts;

  public Variant() {
    this(null);
  }

  public Variant(final String variantID) {
    this(variantID, null, variantID);
  }

  public Variant(final String label, final String description, final String variantID) {
    this(label, description, variantID, null, null);
  }

  public Variant(final String label, final String description, final String variantID, final Features features, final Concepts concepts) {
    super(variantID);
    setLabel(label);
    setDescription(description);
    setFeatures(features);
    setConcepts(concepts);
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

  public void addFeature(final Feature feature) {
    if (feature != null) {
      getFeatures().add(feature);
    }
  }

  public void addAllFeatures(final Collection<? extends Feature> c) {
    if ((c != null) && !c.isEmpty()) {
      getFeatures().addAll(c);
    }
  }

  public void clearFeatures() {
    if (this.features != null) {
      this.features.clear();
      this.features = null;
    }
  }

  // ----------------------------------------------------------------

  public Concepts getConcepts() {
    if (this.concepts == null) {
      this.concepts = new Concepts();
    }
    return this.concepts;
  }

  public void setConcepts(final Concepts concepts) {
    clearConcepts();
    this.concepts = concepts;
  }

  public void addConcept(final Concept concept) {
    if (concept != null) {
      getConcepts().add(concept);
    }
  }

  public void addAllConcepts(final Collection<? extends Concept> c) {
    if ((c != null) && !c.isEmpty()) {
      getConcepts().addAll(c);
    }
  }

  public void clearConcepts() {
    if (this.concepts != null) {
      this.concepts.clear();
      this.concepts = null;
    }
  }
}
