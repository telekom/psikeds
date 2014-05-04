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
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Interface object representing a Knowledge-Entity, i.e. "one piece" of
 * Knowledge. A Knowledge-Entity is always a selected Variant for a certain
 * Purpose. Optionally a Quantity can be specified (e.g. 4 wheels for driving)
 * 
 * A Knowledge-Entity may also have Features if the selected Variant declares
 * them. Initially only possible Feature-Choices exist being transformed
 * into Feature-Values with every Decission of the Client.
 * 
 * Also Concepts can be choosen. A Concept is a subsumption of Feature-Values,
 * i.e. selecting a Concept is like selecting several Feature-Values.
 * 
 * Additionally a KE may have Children, i.e. other KEs constituting this KE.
 * Initially these are possible Variant-Choices, i.e. Purposes for which a
 * constituting Variant must yet be selected.
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
  private VariantChoices possibleVariants;
  private FeatureChoices possibleFeatures;
  private ConceptChoices possibleConcepts;

  public KnowledgeEntity() {
    this(MINIMUM_QUANTITY, null, null, null, null, null, null, null);
  }

  public KnowledgeEntity(final Purpose purpose, final Variant variant) {
    this(DEFAULT_QUANTITY, purpose, variant);
  }

  public KnowledgeEntity(final long qty, final Purpose purpose, final Variant variant) {
    this(qty, purpose, variant, null, null, null, null, null);
  }

  public KnowledgeEntity(final Purpose purpose, final Variant variant,
      final VariantChoices possibleVariants, final FeatureChoices possibleFeatures, final ConceptChoices possibleConcepts) {
    this(DEFAULT_QUANTITY, purpose, variant, possibleVariants, possibleFeatures, possibleConcepts);
  }

  public KnowledgeEntity(final long qty, final Purpose purpose, final Variant variant,
      final VariantChoices possibleVariants, final FeatureChoices possibleFeatures, final ConceptChoices possibleConcepts) {
    this(qty, purpose, variant, null, null, possibleVariants, possibleFeatures, possibleConcepts);
  }

  public KnowledgeEntity(final long qty, final Purpose purpose, final Variant variant,
      final FeatureValues features, final KnowledgeEntities children,
      final VariantChoices possibleVariants, final FeatureChoices possibleFeatures, final ConceptChoices possibleConcepts) {
    super(purpose, variant);
    setPurpose(purpose);
    setVariant(variant);
    setQuantity(qty);
    setFeatures(features);
    setChildren(children);
    setPossibleVariants(possibleVariants);
    setPossibleFeatures(possibleFeatures);
    setPossibleConcepts(possibleConcepts);
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
    return (((this.variant == null) || (this.purpose == null)) ? MINIMUM_QUANTITY : this.quantity); // nothing selected, no quantity
  }

  public void setQuantity(final long qty) {
    this.quantity = (qty < MINIMUM_QUANTITY ? MINIMUM_QUANTITY : qty);
  }

  // ----------------------------------------------------------------

  public KnowledgeEntities getChildren() {
    if (this.children == null) {
      this.children = new KnowledgeEntities();
    }
    return this.children;
  }

  public void setChildren(final KnowledgeEntities children) {
    clearChildren();
    this.children = children;
  }

  public void addChild(final KnowledgeEntity child) {
    if (child != null) {
      getChildren().add(child);
    }
  }

  public void addAllChildren(final Collection<? extends KnowledgeEntity> c) {
    if ((c != null) && !c.isEmpty()) {
      getChildren().addAll(c);
    }
  }

  public void clearChildren() {
    if (this.children != null) {
      this.children.clear();
      this.children = null;
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

  public void addAllFeature(final Collection<? extends FeatureValue> c) {
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

  public VariantChoices getPossibleVariants() {
    if (this.possibleVariants == null) {
      this.possibleVariants = new VariantChoices();
    }
    return this.possibleVariants;
  }

  public void setPossibleVariants(final VariantChoices choices) {
    clearPossibleVariants();
    if (choices != null) {
      choices.setParents((this.variant == null ? null : this.variant.getVariantID()), this.purpose);
      this.possibleVariants = choices;
    }
  }

  public void addPossibleVariant(final VariantChoice choice) {
    if (choice != null) {
      choice.setParentVariantID((this.variant == null ? null : this.variant.getVariantID()));
      choice.setPurpose(this.purpose);
      getPossibleVariants().add(choice);
    }
  }

  public void addAllPossibleVariants(final VariantChoices choices) {
    if ((choices != null) && !choices.isEmpty()) {
      choices.setParents((this.variant == null ? null : this.variant.getVariantID()), this.purpose);
      getPossibleVariants().addAll(choices);
    }
  }

  public void clearPossibleVariants() {
    if (this.possibleVariants != null) {
      this.possibleVariants.clear();
      this.possibleVariants = null;
    }
  }

  // ----------------------------------------------------------------

  public FeatureChoices getPossibleFeatures() {
    if (this.possibleFeatures == null) {
      this.possibleFeatures = new FeatureChoices();
    }
    return this.possibleFeatures;
  }

  public void setPossibleFeatures(final FeatureChoices choices) {
    clearPossibleFeatures();
    this.possibleFeatures = choices;
  }

  public void addPossibleFeature(final FeatureChoice choice) {
    if (choice != null) {
      getPossibleFeatures().add(choice);
    }
  }

  public void addAllPossibleFeatures(final Collection<? extends FeatureChoice> c) {
    if ((c != null) && !c.isEmpty()) {
      getPossibleFeatures().addAll(c);
    }
  }

  public void clearPossibleFeatures() {
    if (this.possibleFeatures != null) {
      this.possibleFeatures.clear();
      this.possibleFeatures = null;
    }
  }

  // ----------------------------------------------------------------

  public ConceptChoices getPossibleConcepts() {
    if (this.possibleConcepts == null) {
      this.possibleConcepts = new ConceptChoices();
    }
    return this.possibleConcepts;
  }

  public void setPossibleConcepts(final ConceptChoices choices) {
    clearPossibleConcepts();
    this.possibleConcepts = choices;
  }

  public void addPossibleConcept(final ConceptChoice choice) {
    if (choice != null) {
      getPossibleConcepts().add(choice);
    }
  }

  public void addAllPossibleConcepts(final Collection<? extends ConceptChoice> c) {
    if ((c != null) && !c.isEmpty()) {
      getPossibleConcepts().addAll(c);
    }
  }

  public void clearPossibleConcepts() {
    if (this.possibleConcepts != null) {
      this.possibleConcepts.clear();
      this.possibleConcepts = null;
    }
  }

  // ----------------------------------------------------------------

  @JsonIgnore
  public boolean isResolved() {
    return ((this.purpose != null)
        && (this.variant != null)
        // TODO: enable features and concepts here
//        && ((this.possibleConcepts == null) || this.possibleConcepts.isEmpty())
//        && ((this.possibleFeatures == null) || this.possibleFeatures.isEmpty())
        && ((this.possibleVariants == null) || this.possibleVariants.isEmpty()));
  }

  @JsonIgnore
  public boolean isRoot() {
    return ((this.purpose != null) && this.purpose.isRoot());
  }
}
