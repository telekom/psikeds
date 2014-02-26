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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Interface object representing a Knowledge-Entity, "one piece" of Knowledge.
 * A KE is a selected Variant for a certain Purpose. It can have siblings, i.e.
 * other KEs constituting this KE. There might also be Choices, i.e. Purposes
 * for which a constituting Variant must yet be selected.
 * 
 * Note: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "KnowledgeEntity")
public class KnowledgeEntity extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final long MINIMUM_QUANTITY = 0L; // nothing selected yet ==> quantity = 0
  public static final long DEFAULT_QUANTITY = 1L; // default: just one variant for fulfilling the purpose

  private long quantity;
  private Purpose purpose;
  private Variant variant;
  private FeatureValues features;
  private KnowledgeEntities children;
  private Choices choices;

  public KnowledgeEntity() {
    this(MINIMUM_QUANTITY, null, null, null, null, null);
  }

  public KnowledgeEntity(final Purpose purpose, final Variant variant) {
    this(purpose, variant, null);
  }

  public KnowledgeEntity(final Purpose purpose, final Variant variant, final Choices choices) {
    this(DEFAULT_QUANTITY, purpose, variant, choices);
  }

  public KnowledgeEntity(final long qty, final Purpose purpose, final Variant variant, final Choices choices) {
    this(qty, purpose, variant, null, null, choices);
  }

  public KnowledgeEntity(final long qty, final Purpose purpose, final Variant variant, final FeatureValues features, final KnowledgeEntities children, final Choices choices) {
    super(purpose, variant);
    setPurpose(purpose);
    setVariant(variant);
    setQuantity(qty);
    setFeatures(features);
    setChildren(children);
    setChoices(choices);
  }

  public Purpose getPurpose() {
    return this.purpose;
  }

  public void setPurpose(final Purpose purpose) {
    this.purpose = purpose;
  }

  public Variant getVariant() {
    return this.variant;
  }

  public void setVariant(final Variant variant) {
    this.variant = variant;
  }

  public long getQuantity() {
    return (this.variant == null ? MINIMUM_QUANTITY : this.quantity); // no variant, no quantity
  }

  public void setQuantity(final long qty) {
    this.quantity = (qty < MINIMUM_QUANTITY ? MINIMUM_QUANTITY : qty);
  }

  public KnowledgeEntities getChildren() {
    if (this.children == null) {
      this.children = new KnowledgeEntities();
    }
    return this.children;
  }

  public void setChildren(final KnowledgeEntities children) {
    this.children = children;
  }

  public void addChild(final KnowledgeEntity child) {
    if (child != null) {
      getChildren().add(child);
    }
  }

  // ----------------------------------------------------------------

  public FeatureValues getFeatures() {
    if (this.features == null) {
      this.features = new FeatureValues();
    }
    return this.features;
  }

  public void setFeatures(final FeatureValues features) {
    clearFeatures();
    this.features = features;
  }

  public void addFeature(final FeatureValue feature) {
    if (feature != null) {
      getFeatures().add(feature);
    }
  }

  public void addAllFeature(final FeatureValues features) {
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

  // ----------------------------------------------------------------

  public Choices getChoices() {
    if (this.choices == null) {
      this.choices = new Choices();
    }
    return this.choices;
  }

  public void setChoices(final Choices choices) {
    clearChoices();
    this.choices = choices;
  }

  public void addChoice(final Choice choice) {
    if (choice != null) {
      getChoices().add(choice);
    }
  }

  public void addAllChoices(final Choices choices) {
    if ((choices != null) && !choices.isEmpty()) {
      getChoices().addAll(choices);
    }
  }

  public void clearChoices() {
    if (this.choices != null) {
      this.choices.clear();
      this.choices = null;
    }
  }

  // ----------------------------------------------------------------

  @JsonIgnore
  public boolean isResolved() {
    return ((this.purpose != null) && (this.variant != null) && ((this.choices == null) || this.choices.isEmpty()));
  }
}
