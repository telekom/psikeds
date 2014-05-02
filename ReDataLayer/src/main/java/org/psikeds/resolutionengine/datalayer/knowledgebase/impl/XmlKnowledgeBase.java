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
package org.psikeds.resolutionengine.datalayer.knowledgebase.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.knowledgebase.xml.KBParserCallback;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.impl.Xml2VoTransformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.util.FeatureValueHelper;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Component;
import org.psikeds.resolutionengine.datalayer.vo.Concept;
import org.psikeds.resolutionengine.datalayer.vo.Concepts;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValues;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameters;
import org.psikeds.resolutionengine.datalayer.vo.Relations;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * This implementation of a KnowledgeBase acts as a Callback receiving Data
 * from an XML-Source.
 * 
 * Afterwards it encapsulates all Knowledge in a Map and provides Accessors
 * to it.
 * 
 * @see org.psikeds.knowledgebase.xml.KBParserCallback
 * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase
 * 
 * @author marco@juliano.de
 * 
 */
public class XmlKnowledgeBase implements KnowledgeBase, KBParserCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlKnowledgeBase.class);

  private static final String KEY_META_DATA = "kb.meta.data";

  private static final String KEY_ALL_FEATURES = "kb.all.features";
  private static final String KEY_ALL_FEATURE_VALUES = "kb.all.featurevalues";
  private static final String KEY_ALL_CONCEPTS = "kb.all.concepts";
  private static final String KEY_ALL_PURPOSES = "kb.all.purposes";
  private static final String KEY_ALL_VARIANTS = "kb.all.variants";
  private static final String KEY_ALL_ALTERNATIVES = "kb.all.alternatives";
  private static final String KEY_ALL_CONSTITUENTS = "kb.all.constituents";
  private static final String KEY_ALL_EVENTS = "kb.all.events";
  private static final String KEY_ALL_RULES = "kb.all.rules";
  private static final String KEY_ALL_RELATION_PARAMS = "kb.all.relparams";
  private static final String KEY_ALL_RELATIONS = "kb.all.relations";

  private static final String KEY_PREFIX_FEATURE = "kb.feature.";
  private static final String KEY_PREFIX_FEATURE_VALUE = "kb.featurevalue.";
  private static final String KEY_PREFIX_CONCEPT = "kb.concept.";
  private static final String KEY_PREFIX_PURPOSE = "kb.purpose.";
  private static final String KEY_PREFIX_VARIANT = "kb.variant.";
  private static final String KEY_PREFIX_EVENT = "kb.event.";
  private static final String KEY_PREFIX_RULE = "kb.rule.";
  private static final String KEY_PREFIX_RELATION_PARAM = "kb.relparam.";
  private static final String KEY_PREFIX_RELATION = "kb.relation.";

  private static final String KEY_PREFIX_FULFILLS = "kb.fulfills.";
  private static final String KEY_PREFIX_CONSTITUTES = "kb.constitutes.";

  private static final String KEY_PREFIX_ATTACHED_EVENTS = "kb.attached.events.";
  private static final String KEY_PREFIX_ATTACHED_RULES = "kb.attached.rules.";
  private static final String KEY_PREFIX_ATTACHED_RELATION_PARAMS = "kb.attached.relparams.";
  private static final String KEY_PREFIX_ATTACHED_RELATIONS = "kb.attached.relations.";

  private static final String KEY_ROOT_PURPOSES = "kb.root.purposes";

  private Map<String, Object> knowledge;
  private Transformer trans;
  private boolean valid;
  private boolean failOnUnexpected;
  private boolean ignoreImplicitVariants;
  private boolean ignoreSecondaryConcepts;

  public XmlKnowledgeBase() {
    this(null);
  }

  public XmlKnowledgeBase(final Transformer trans) {
    this(trans, null);
  }

  public XmlKnowledgeBase(final Transformer trans, final Map<String, Object> knowledge) {
    this(trans, knowledge, false, true, true, true);
  }

  public XmlKnowledgeBase(final Transformer trans, final Map<String, Object> knowledge, final boolean valid, final boolean failOnUnexpected, final boolean ignoreImplicitVariants,
      final boolean ignoreSecondaryConcepts) {
    setTransformer(trans);
    setKnowledge(knowledge);
    setValid(valid);
    setFailOnUnexpected(failOnUnexpected);
    setIgnoreImplicitVariants(ignoreImplicitVariants);
    setIgnoreSecondaryConcepts(ignoreSecondaryConcepts);
  }

  public Transformer getTransformer() {
    return this.trans;
  }

  public void setTransformer(final Transformer trans) {
    this.trans = (trans != null ? trans : new Xml2VoTransformer());
  }

  public Map<String, Object> getKnowledge() {
    return this.knowledge;
  }

  public void setKnowledge(final Map<String, Object> knowledge) {
    this.knowledge = (knowledge != null ? knowledge : new ConcurrentHashMap<String, Object>());
  }

  public void setValid(final boolean valid) {
    this.valid = valid;
  }

  public void setFailOnUnexpected(final boolean failOnUnexpected) {
    this.failOnUnexpected = failOnUnexpected;
  }

  public void setIgnoreImplicitVariants(final boolean ignoreImplicitVariants) {
    this.ignoreImplicitVariants = ignoreImplicitVariants;
  }

  public void setIgnoreSecondaryConcepts(final boolean ignoreSecondaryConcepts) {
    this.ignoreSecondaryConcepts = ignoreSecondaryConcepts;
  }

  // ----------------------------------------------------------------
  // Methods required for the Interface KnowledgeBase
  // ----------------------------------------------------------------

  /**
   * @return MetaData of this Knowledgebase
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getMetaData()
   */
  @Override
  public MetaData getMetaData() {
    return (MetaData) load(KEY_META_DATA);
  }

  /**
   * @return all Features
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures()
   */
  @Override
  public Features getFeatures() {
    return (Features) load(KEY_ALL_FEATURES);
  }

  /**
   * @return all FeatureValues
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValues()
   */
  @Override
  public FeatureValues getFeatureValues() {
    return (FeatureValues) load(KEY_ALL_FEATURE_VALUES);
  }

  /**
   * @return all Concepts
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConcepts()
   */
  @Override
  public Concepts getConcepts() {
    return (Concepts) load(KEY_ALL_CONCEPTS);
  }

  /**
   * @return all Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurposes()
   */
  @Override
  public Purposes getPurposes() {
    return (Purposes) load(KEY_ALL_PURPOSES);
  }

  /**
   * @return all Variants
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariants()
   */
  @Override
  public Variants getVariants() {
    return (Variants) load(KEY_ALL_VARIANTS);
  }

  /**
   * @return all Alternatives
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAlternatives()
   */
  @Override
  public Alternatives getAlternatives() {
    return (Alternatives) load(KEY_ALL_ALTERNATIVES);
  }

  /**
   * @return all Constituents
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstituents()
   */
  @Override
  public Constituents getConstituents() {
    return (Constituents) load(KEY_ALL_CONSTITUENTS);
  }

  /**
   * @return all Events
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvents()
   */
  @Override
  public Events getEvents() {
    return (Events) load(KEY_ALL_EVENTS);
  }

  /**
   * @return all Rules
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRules()
   */
  @Override
  public Rules getRules() {
    return (Rules) load(KEY_ALL_RULES);
  }

  /**
   * @return all RelationParameters
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelationParameters()
   */
  @Override
  public RelationParameters getRelationParameters() {
    return (RelationParameters) load(KEY_ALL_RELATION_PARAMS);
  }

  /**
   * @return all Relations
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelations()
   */
  @Override
  public Relations getRelations() {
    return (Relations) load(KEY_ALL_RELATIONS);
  }

  // ----------------------------------------------------------------

  /**
   * @param featureId
   * @return Feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature getFeature(final String featureId) {
    return (Feature) load(KEY_PREFIX_FEATURE, featureId);
  }

  /**
   * @param featureValueID
   * @return FeatureValue
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValue(java.lang.String)
   */
  @Override
  public FeatureValue getFeatureValue(final String featureValueID) {
    return (FeatureValue) load(KEY_PREFIX_FEATURE_VALUE, featureValueID);
  }

  /**
   * @param conceptID
   * @return Concept
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConcept(java.lang.String)
   */
  @Override
  public Concept getConcept(final String conceptID) {
    return (Concept) load(KEY_PREFIX_CONCEPT, conceptID);
  }

  /**
   * @param purposeId
   * @return Purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurpose(java.lang.String)
   */
  @Override
  public Purpose getPurpose(final String purposeId) {
    return (Purpose) load(KEY_PREFIX_PURPOSE, purposeId);
  }

  /**
   * @param variantId
   * @return Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariant(java.lang.String)
   */
  @Override
  public Variant getVariant(final String variantId) {
    return (Variant) load(KEY_PREFIX_VARIANT, variantId);
  }

  /**
   * @param eventId
   * @return Event
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvent(java.lang.String)
   */
  @Override
  public Event getEvent(final String eventId) {
    return (Event) load(KEY_PREFIX_EVENT, eventId);
  }

  /**
   * @param ruleId
   * @return Rule
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRule(java.lang.String)
   */
  @Override
  public Rule getRule(final String ruleId) {
    return (Rule) load(KEY_PREFIX_RULE, ruleId);
  }

  /**
   * @param parameterID
   * @return RelationParameter
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelationParameter(java.lang.String)
   */
  @Override
  public RelationParameter getRelationParameter(final String parameterID) {
    return (RelationParameter) load(KEY_PREFIX_RELATION_PARAM, parameterID);
  }

  /**
   * @param relationId
   * @return Relation
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelation(java.lang.String)
   */
  @Override
  public Relation getRelation(final String relationId) {
    return (Relation) load(KEY_PREFIX_RELATION, relationId);
  }

  // ----------------------------------------------------------------

  /**
   * @param purposeId
   * @return Fulfills
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfills(java.lang.String)
   */
  @Override
  public Fulfills getFulfills(final String purposeId) {
    return (Fulfills) load(KEY_PREFIX_FULFILLS, purposeId);
  }

  /**
   * @param variantId
   * @return Constitutes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutes(java.lang.String)
   */
  @Override
  public Constitutes getConstitutes(final String variantId) {
    return (Constitutes) load(KEY_PREFIX_CONSTITUTES, variantId);
  }

  /**
   * @param variantId
   * @param purposeId
   * @return long quantity
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getQuantity(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public long getQuantity(final String variantId, final String purposeId) {
    if (!StringUtils.isEmpty(variantId) && !StringUtils.isEmpty(purposeId)) {
      final Constitutes cons = getConstitutes(variantId);
      if (cons != null) {
        for (final Component comp : cons.getComponents()) {
          if ((comp != null) && purposeId.equals(comp.getPurposeID())) {
            return comp.getQuantity();
          }
        }
      }
    }
    return Component.DEFAULT_QUANTITY;
  }

  /**
   * @param variantId
   * @return all features of this variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures(java.lang.String)
   */
  @Override
  public Features getFeatures(final String variantId) {
    final Features feats = new Features();
    if (!StringUtils.isEmpty(variantId)) {
      final Variant variant = getVariant(variantId);
      if (variant != null) {
        final List<String> fids = variant.getFeatureIds();
        for (final String featureId : fids) {
          final Feature f = getFeature(featureId);
          if (f != null) {
            feats.addFeature(f);
          }
        }
      }
    }
    return feats;
  }

  /**
   * @param conceptID
   * @return all values bundled in this concept
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValuesOfConcept(java.lang.String)
   */
  @Override
  public FeatureValues getFeatureValuesOfConcept(final String conceptID) {
    final FeatureValues values = new FeatureValues();
    final Concept con = (StringUtils.isEmpty(conceptID) ? null : getConcept(conceptID));
    if (con != null) {
      for (final FeatureValue val : con.getValues()) {
        if (val != null) {
          values.addValue(val);
        }
      }
    }
    return values;
  }

  /**
   * @param rangeID
   * @param featureId
   * @return all values within a given range
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValuesWithinRange(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public FeatureValues getFeatureValuesWithinRange(final String featureId, final String rangeID) {
    final FeatureValues values = new FeatureValues();
    final Feature f = (StringUtils.isEmpty(featureId) ? null : getFeature(featureId));
    if (f != null) {
      for (final FeatureValue val : f.getValues()) {
        if (FeatureValueHelper.isWithinRange(featureId, rangeID, val)) {
          values.addValue(val);
        }
      }
    }
    return values;
  }

  /**
   * @param variantId
   * @return Events attached to Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedEvents(java.lang.String)
   */
  @Override
  public Events getAttachedEvents(final String variantId) {
    return (Events) load(KEY_PREFIX_ATTACHED_EVENTS, variantId);
  }

  /**
   * @param variantId
   * @return Rules attached to Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRules(java.lang.String)
   */
  @Override
  public Rules getAttachedRules(final String variantId) {
    return (Rules) load(KEY_PREFIX_ATTACHED_RULES, variantId);
  }

  /**
   * @param variantId
   * @return RelationParameters attached to Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRelationParameters(java.lang.String)
   */
  @Override
  public RelationParameters getAttachedRelationParameters(final String variantId) {
    return (RelationParameters) load(KEY_PREFIX_ATTACHED_RELATION_PARAMS, variantId);
  }

  /**
   * @param variantId
   * @return Relations attached to Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRelations(java.lang.String)
   */
  @Override
  public Relations getAttachedRelations(final String variantId) {
    return (Relations) load(KEY_PREFIX_ATTACHED_RELATIONS, variantId);
  }

  // ----------------------------------------------------------------

  /**
   * @return all Purposes flagged with "root" attribute
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRootPurposes()
   */
  @Override
  public Purposes getRootPurposes() {
    return (Purposes) load(KEY_ROOT_PURPOSES);
  }

  /**
   * @param purposeId
   * @return all variants fulfilling this purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfillingVariants(java.lang.String)
   */
  @Override
  public Variants getFulfillingVariants(final String purposeId) {
    final Variants fulvar = new Variants();
    if (!StringUtils.isEmpty(purposeId)) {
      final Fulfills f = getFulfills(purposeId);
      if (f != null) {
        final List<String> varids = f.getVariantID();
        for (final String variantId : varids) {
          final Variant v = getVariant(variantId);
          if (v != null) {
            fulvar.addVariant(v);
          }
        }
      }
    }
    return fulvar;
  }

  /**
   * @param purposeId
   * @param variantId
   * @return true if purpose is fulfilled by variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isFulfilledBy(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean isFulfilledBy(final String purposeId, final String variantId) {
    final Variants vars = getFulfillingVariants(purposeId);
    final List<Variant> lst = vars.getVariant();
    for (final Variant idx : lst) {
      if ((idx != null) && variantId.equals(idx.getVariantID())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param variantId
   * @return all purposes that constitute this variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutingPurposes(java.lang.String)
   */
  @Override
  public Purposes getConstitutingPurposes(final String variantId) {
    final Purposes conpurps = new Purposes();
    if (!StringUtils.isEmpty(variantId)) {
      final Constitutes consts = getConstitutes(variantId);
      if (consts != null) {
        for (final Component comp : consts.getComponents()) {
          final String purposeId = comp == null ? null : comp.getPurposeID();
          if (!StringUtils.isEmpty(purposeId)) {
            final Purpose p = getPurpose(purposeId);
            if (p != null) {
              conpurps.addPurpose(p);
            }
          }
        }
      }
    }
    return conpurps;
  }

  /**
   * @param variantId
   * @param purposeId
   * @return true if variant is constituted by purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isConstitutedBy(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean isConstitutedBy(final String variantId, final String purposeId) {
    final Purposes purps = getConstitutingPurposes(variantId);
    final List<Purpose> lst = purps.getPurpose();
    for (final Purpose idx : lst) {
      if ((idx != null) && purposeId.equals(idx.getPurposeID())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param variantId
   * @param featureId
   * @return true if variant has this feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#hasFeature(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean hasFeature(final String variantId, final String featureId) {
    final Features feats = getFeatures(variantId);
    final List<Feature> lst = feats.getFeature();
    for (final Feature idx : lst) {
      if ((idx != null) && featureId.equals(idx.getFeatureID())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param featureID
   * @param featureValueID
   * @return true if feature has this value
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#hasFeatureValue(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean hasFeatureValue(final String featureID, final String featureValueID) {
    final FeatureValue fv = getFeatureValue(featureValueID);
    return ((fv != null) && !StringUtils.isEmpty(fv.getFeatureID()) && fv.getFeatureID().equals(featureID));
  }

  /**
   * @param featureValueID
   * @param conceptID
   * @return true if concept includes this feature value
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isIncludedIn(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean isIncludedIn(final String featureValueID, final String conceptID) {
    if (!StringUtils.isEmpty(featureValueID) && !StringUtils.isEmpty(conceptID)) {
      final FeatureValues values = getFeatureValuesOfConcept(conceptID);
      final List<FeatureValue> lst = (values == null ? null : values.getValue());
      if ((lst != null) && !lst.isEmpty()) {
        for (final FeatureValue val : lst) {
          if ((val != null) && featureValueID.equals(val.getFeatureValueID())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @return true if knowledge base is valid or false if any problems were detected during
   *         loading/initialization
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isValid()
   */
  @Override
  public boolean isValid() {
    return this.valid;
  }

  // ----------------------------------------------------------------
  // Method required for the Interface KBParserCallback
  // ----------------------------------------------------------------

  /**
   * @param element
   * @see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object)
   */
  @Override
  public void handleElement(final Object element) {
    try {
      LOGGER.trace("--> handleElement()");
      if (element instanceof org.psikeds.knowledgebase.jaxb.Knowledgebase) {
        setKnowledgebase((org.psikeds.knowledgebase.jaxb.Knowledgebase) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Meta) {
        setMetaData((org.psikeds.knowledgebase.jaxb.Meta) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Data) {
        setData((org.psikeds.knowledgebase.jaxb.Data) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Sensors) {
        setSensors((org.psikeds.knowledgebase.jaxb.Sensors) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Concepts) {
        setConcepts((org.psikeds.knowledgebase.jaxb.Concepts) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Purposes) {
        setPurposes((org.psikeds.knowledgebase.jaxb.Purposes) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Variants) {
        setVariants((org.psikeds.knowledgebase.jaxb.Variants) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Alternatives) {
        setAlternatives((org.psikeds.knowledgebase.jaxb.Alternatives) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Constituents) {
        setConstituents((org.psikeds.knowledgebase.jaxb.Constituents) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Derivations) {
        setDerivations((org.psikeds.knowledgebase.jaxb.Derivations) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Events) {
        setEvents((org.psikeds.knowledgebase.jaxb.Events) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Parameters) {
        setParameters((org.psikeds.knowledgebase.jaxb.Parameters) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Rules) {
        setRules((org.psikeds.knowledgebase.jaxb.Rules) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Relations) {
        setRelations((org.psikeds.knowledgebase.jaxb.Relations) element);
      }
      else if (this.failOnUnexpected) {
        throw new ValidationException("Unexpected XML-Element!");
      }
      else {
        LOGGER.warn("Skipping unexpected XML-Element: {}", element);
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.error("Could not handle XML-Element: " + String.valueOf(element), vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      final String errmsg = "Could not handle XML-Element: " + String.valueOf(element);
      LOGGER.error(errmsg, ex);
      throw new ValidationException(errmsg, ex);
    }
    finally {
      LOGGER.trace("<-- handleElement()");
    }
  }

  // ----------------------------------------------------------------
  // Internal Helpers
  // ----------------------------------------------------------------

  private void setKnowledgebase(final org.psikeds.knowledgebase.jaxb.Knowledgebase kb) {
    if (kb != null) {
      setMetaData(kb.getMeta());
      setData(kb.getData());
    }
  }

  private void setMetaData(final org.psikeds.knowledgebase.jaxb.Meta meta) {
    if (meta != null) {
      setMetaData(this.trans.xml2ValueObject(meta));
    }
  }

  private void setMetaData(final MetaData meta) {
    save(KEY_META_DATA, meta);
  }

  private void setData(final org.psikeds.knowledgebase.jaxb.Data data) {
    if (data != null) {
      setSensors(data.getSensors());
      setConcepts(data.getConcepts()); // concepts depend on sensors, must be invoked afterwards!
      setPurposes(data.getPurposes());
      setVariants(data.getVariants()); // variants depend on both sensors and concepts, must be invoked afterwards!
      setAlternatives(data.getAlternatives());
      setConstituents(data.getConstituents());
      setDerivations(data.getDerivations());
      setEvents(data.getEvents());
      setParameters(data.getParameters());
      setRules(data.getRules());
      setRelations(data.getRelations()); // relations depend on parameters, must be invoked afterwards!
    }
  }

  private void setSensors(final org.psikeds.knowledgebase.jaxb.Sensors sensors) {
    final Features allFeatures = new Features();
    final FeatureValues allValues = new FeatureValues();
    final List<org.psikeds.knowledgebase.jaxb.Sensor> slist = (sensors == null ? null : sensors.getSensor());
    if ((slist != null) && !slist.isEmpty()) {
      for (final org.psikeds.knowledgebase.jaxb.Sensor sensor : slist) {
        final Feature f = this.trans.xml2ValueObject(sensor);
        final String fid = (f == null ? null : f.getFeatureID());
        if (!StringUtils.isEmpty(fid)) {
          allFeatures.addFeature(f);
          save(KEY_PREFIX_FEATURE, fid, f);
          final List<FeatureValue> vlist = f.getValues();
          for (final FeatureValue val : vlist) {
            final String fvid = (val == null ? null : val.getFeatureValueID());
            if (!StringUtils.isEmpty(fvid)) {
              allValues.addValue(val);
              save(KEY_PREFIX_FEATURE_VALUE, fvid, val);
            }
          }
        }
      }
    }
    save(KEY_ALL_FEATURES, allFeatures);
    save(KEY_ALL_FEATURE_VALUES, allValues);
  }

  private void setConcepts(final org.psikeds.knowledgebase.jaxb.Concepts concepts) {
    final Concepts allConcepts = new Concepts();
    final List<org.psikeds.knowledgebase.jaxb.Concept> clist = (concepts == null ? null : concepts.getConcept());
    if ((clist != null) && !clist.isEmpty()) {
      for (final org.psikeds.knowledgebase.jaxb.Concept concept : clist) {
        final String cid = (concept == null ? null : concept.getId());
        if (!StringUtils.isEmpty(cid)) {
          final String label = concept.getLabel();
          final String description = concept.getDescription();
          // we will not use the transformer ...
          final Concept c = new Concept(label, description, cid);
          final org.psikeds.knowledgebase.jaxb.Attributes attributes = concept.getAttributes();
          final List<org.psikeds.knowledgebase.jaxb.ComplexAttribute> calist = (attributes == null ? null : attributes.getComplexAttribute());
          if ((calist != null) && !calist.isEmpty()) {
            for (final org.psikeds.knowledgebase.jaxb.ComplexAttribute attrib : calist) {
              // ... because we need to look up existing references to feature-values here!
              final String featureValueID = (attrib == null ? null : attrib.getValue());
              final FeatureValue fv = (StringUtils.isEmpty(featureValueID) ? null : getFeatureValue(featureValueID));
              if ((fv == null) || !featureValueID.equals(fv.getFeatureValueID())) {
                throw new IllegalArgumentException("Illegal Reference from Concept " + cid + " to Feature-Value " + featureValueID);
              }
              final String featureID = attrib.getSensedByRef();
              if (StringUtils.isEmpty(featureID) || !featureID.equals(fv.getFeatureID())) {
                throw new IllegalArgumentException("Illegal Reference from Concept " + cid + " to Feature-ID " + featureID);
              }
              c.addValue(fv); // do not create a new object but save existing reference
            }
          }
          save(KEY_PREFIX_CONCEPT, cid, c);
        }
      }
    }
    save(KEY_ALL_CONCEPTS, allConcepts);
  }

  private void setPurposes(final org.psikeds.knowledgebase.jaxb.Purposes purps) {
    if (purps != null) {
      setPurposes(this.trans.xml2ValueObject(purps));
    }
  }

  private void setPurposes(final Purposes purps) {
    if (purps != null) {
      save(KEY_ALL_PURPOSES, purps);
      final Purposes root = new Purposes();
      final List<Purpose> lst = purps.getPurpose();
      for (final Purpose p : lst) {
        if (p != null) {
          save(KEY_PREFIX_PURPOSE, p.getPurposeID(), p);
          if (p.isRoot()) {
            root.addPurpose(p);
          }
        }
      }
      save(KEY_ROOT_PURPOSES, root);
    }
  }

  private void setVariants(final org.psikeds.knowledgebase.jaxb.Variants vars) {
    final Variants allVariants = new Variants();
    final List<org.psikeds.knowledgebase.jaxb.Variant> vlst = (vars == null ? null : vars.getVariant());
    if ((vlst != null) && !vlst.isEmpty()) {
      for (final org.psikeds.knowledgebase.jaxb.Variant var : vlst) {
        final String vid = (var == null ? null : var.getId());
        if (!StringUtils.isEmpty(vid)) {
          // we will not use the transformer ...
          final String label = var.getLabel();
          final String description = var.getDescription();
          final boolean singleton = (var.isSingleton() == null ? Variant.DEFAULT_IS_SINGLETON : var.isSingleton().booleanValue());
          final String variantType = (var.getType() == null ? null : var.getType().value());
          final boolean implicit = (StringUtils.isEmpty(variantType) ? Variant.DEFAULT_IS_IMPLICIT : org.psikeds.knowledgebase.jaxb.VarType.IMPLICIT.value().equals(variantType));
          final Variant v = new Variant(label, description, vid, singleton, implicit);
          // ... because we need to look up existing references to feature-values and concepts here!
          final org.psikeds.knowledgebase.jaxb.PrimarilyDenotedBy primary = var.getPrimarilyDenotedBy();
          if (primary != null) {
            // first: lookup of features (discrete values)
            final List<org.psikeds.knowledgebase.jaxb.OneOutOfTheseAttributes> attributes = primary.getOneOutOfTheseAttributes();
            if ((attributes != null) && !attributes.isEmpty()) {
              for (final org.psikeds.knowledgebase.jaxb.OneOutOfTheseAttributes attr : attributes) {
                final String featureID = (attr == null ? null : attr.getSensedByRef());
                final List<org.psikeds.knowledgebase.jaxb.Attribute> alist = attr.getAttribute();
                if (!StringUtils.isEmpty(featureID) && (alist != null) && !alist.isEmpty()) {
                  v.addFeatureId(featureID);
                  for (final org.psikeds.knowledgebase.jaxb.Attribute a : alist) {
                    final String featureValueID = (a == null ? null : a.getRef());
                    final FeatureValue fv = (StringUtils.isEmpty(featureValueID) ? null : getFeatureValue(featureValueID));
                    if ((fv == null) || !featureValueID.equals(fv.getFeatureValueID())) {
                      throw new IllegalArgumentException("Illegal Reference from Variant " + vid + " to Feature-Value " + featureValueID);
                    }
                    if (!featureID.equals(fv.getFeatureID())) {
                      throw new IllegalArgumentException("Illegal Reference from Variant " + vid + " to Feature-ID " + featureID);
                    }
                    v.addFeatureValue(fv);
                  }
                }
              }
            }
            // second: lookup of features (ranges)
            final List<org.psikeds.knowledgebase.jaxb.OneOutOfThisRange> ranges = primary.getOneOutOfThisRange();
            if ((ranges != null) && !ranges.isEmpty()) {
              for (final org.psikeds.knowledgebase.jaxb.OneOutOfThisRange range : ranges) {
                if (range != null) {
                  final String rangeID = range.getRangeRef();
                  final String featureID = range.getSensedByRef();
                  final FeatureValues values = getFeatureValuesWithinRange(featureID, rangeID);
                  final List<FeatureValue> fvlst = (values == null ? null : values.getValue());
                  if ((fvlst == null) || fvlst.isEmpty()) {
                    throw new IllegalArgumentException("Illegal Reference from Variant " + vid + " to Range " + rangeID + " of Feature " + featureID);
                  }
                  v.addFeatureId(featureID);
                  for (final FeatureValue fv : fvlst) {
                    v.addFeatureValue(fv);
                  }
                }
              }
            }
            // third: lookup of primary concepts
            final org.psikeds.knowledgebase.jaxb.OneOutOfTheseConcepts concepts = primary.getOneOutOfTheseConcepts();
            final List<org.psikeds.knowledgebase.jaxb.Subsumption> subsumptions = (concepts == null ? null : concepts.getSubsumption());
            if ((subsumptions != null) && !subsumptions.isEmpty()) {
              for (final org.psikeds.knowledgebase.jaxb.Subsumption sub : subsumptions) {
                final String conceptID = (sub == null ? null : sub.getRef());
                final Concept c = (StringUtils.isEmpty(conceptID) ? null : getConcept(conceptID));
                if ((c == null) || !conceptID.equals(c.getConceptID())) {
                  throw new IllegalArgumentException("Illegal Reference from Variant " + vid + " to primary Concept " + conceptID);
                }
                v.addConcept(c);
              }
            }
          }
          // fourth: lookup of secondary concepts
          final org.psikeds.knowledgebase.jaxb.SecondarilyDenotedBy secondary = var.getSecondarilyDenotedBy();
          final List<org.psikeds.knowledgebase.jaxb.Subsumption> subsumptions = (secondary == null ? null : secondary.getSubsumption());
          if ((subsumptions != null) && !subsumptions.isEmpty()) {
            if (this.ignoreSecondaryConcepts) {
              LOGGER.info("Secondary Concepts currently not supported. Skipping all secondary Subsumptions of Variant {}", vid);
            }
            else {
              LOGGER.info("Secondary Concepts currently not supported. Interpreting all secondary Subsumptions of Variant {} as primary ones!", vid);
              for (final org.psikeds.knowledgebase.jaxb.Subsumption sub : subsumptions) {
                final String conceptID = (sub == null ? null : sub.getRef());
                final Concept c = (StringUtils.isEmpty(conceptID) ? null : getConcept(conceptID));
                if ((c == null) || !conceptID.equals(c.getConceptID())) {
                  throw new IllegalArgumentException("Illegal Reference from Variant " + vid + " to secondary Concept " + conceptID);
                }
                v.addConcept(c);
              }
            }
          }
          if (!v.isImplicit()) {
            allVariants.addVariant(v);
            save(KEY_PREFIX_VARIANT, vid, v);
          }
          else {
            if (this.ignoreImplicitVariants) {
              LOGGER.info("Skipping implicit Variant {}", vid);
            }
            else {
              LOGGER.info("Implicit Variant {} will be interpreted as explicit!", vid);
              v.setImplicit(false);
              allVariants.addVariant(v);
              save(KEY_PREFIX_VARIANT, vid, v);
            }
          }
        }
      }
    }
    save(KEY_ALL_VARIANTS, allVariants);
  }

  private void setAlternatives(final org.psikeds.knowledgebase.jaxb.Alternatives alts) {
    if (alts != null) {
      setAlternatives(this.trans.xml2ValueObject(alts));
    }
  }

  private void setAlternatives(final Alternatives alts) {
    if (alts != null) {
      save(KEY_ALL_ALTERNATIVES, alts);
      final List<Fulfills> lst = alts.getFulfills();
      for (final Fulfills full : lst) {
        final String purposeId = (full == null ? null : full.getPurposeID());
        save(KEY_PREFIX_FULFILLS, purposeId, full);
      }
    }
  }

  private void setConstituents(final org.psikeds.knowledgebase.jaxb.Constituents cons) {
    if (cons != null) {
      setConstituents(this.trans.xml2ValueObject(cons));
    }
  }

  private void setConstituents(final Constituents cons) {
    if (cons != null) {
      save(KEY_ALL_CONSTITUENTS, cons);
      final List<Constitutes> lst = cons.getConstitutes();
      for (final Constitutes c : lst) {
        final String variantId = (c == null ? null : c.getVariantID());
        save(KEY_PREFIX_CONSTITUTES, variantId, cons);
      }
    }
  }

  private void setDerivations(final org.psikeds.knowledgebase.jaxb.Derivations derivations) {
    final List<org.psikeds.knowledgebase.jaxb.Setup> setup = (derivations == null ? null : derivations.getSetup());
    if ((setup != null) && !setup.isEmpty()) {
      if (this.ignoreImplicitVariants) {
        LOGGER.info("Implicit Variants and Derivations currently not supported, ignoring this XML-Section!");
      }
      else {
        LOGGER.info("Implicit Variants and Derivations currently not supported, interpreting all Variants as explicit and Derivations/Setup as normal Constituents/Constitutes!");
        setConstituents(this.trans.xml2ValueObject(derivations));
      }
    }
  }

  private void setEvents(final org.psikeds.knowledgebase.jaxb.Events evnts) {
    if (evnts != null) {
      setEvents(this.trans.xml2ValueObject(evnts));
    }
  }

  private void setEvents(final Events evnts) {
    if (evnts != null) {
      save(KEY_ALL_EVENTS, evnts);
      final List<Event> lst = evnts.getEvent();
      for (final Event e : lst) {
        if (e != null) {
          save(KEY_PREFIX_EVENT, e.getEventID(), e);
          attachEvent(e);
        }
      }
    }
  }

  private void setParameters(final org.psikeds.knowledgebase.jaxb.Parameters parameters) {
    final RelationParameters allParams = new RelationParameters();
    final List<org.psikeds.knowledgebase.jaxb.Parameter> params = (parameters == null ? null : parameters.getParameter());
    if ((params != null) && !params.isEmpty()) {
      for (final org.psikeds.knowledgebase.jaxb.Parameter p : params) {
        final RelationParameter param = this.trans.xml2ValueObject(p);
        final String parameterID = (param == null ? null : param.getParameterID());
        if (!StringUtils.isEmpty(parameterID)) {
          allParams.addParameter(param);
          save(KEY_PREFIX_RELATION_PARAM, parameterID, param);
          attachRelationParameter(param);
        }
      }
    }
    save(KEY_ALL_RELATION_PARAMS, allParams);
  }

  private void setRules(final org.psikeds.knowledgebase.jaxb.Rules rules) {
    if (rules != null) {
      setRules(this.trans.xml2ValueObject(rules));
    }
  }

  private void setRules(final Rules rules) {
    if (rules != null) {
      save(KEY_ALL_RULES, rules);
      final List<Rule> lst = rules.getRule();
      for (final Rule r : lst) {
        if (r != null) {
          save(KEY_PREFIX_RULE, r.getRuleID(), r);
          attachRule(r);
        }
      }
    }
  }

  private void setRelations(final org.psikeds.knowledgebase.jaxb.Relations relations) {
    final Relations allRelations = new Relations();
    final List<org.psikeds.knowledgebase.jaxb.Relation> rellst = (relations == null ? null : relations.getRelation());
    if ((rellst != null) && !rellst.isEmpty()) {
      for (final org.psikeds.knowledgebase.jaxb.Relation rel : rellst) {
        final String relationID = (rel == null ? null : rel.getId());
        if (!StringUtils.isEmpty(relationID)) {
          final String label = rel.getLabel();
          final String description = rel.getDescription();
          final String variantID = rel.getNexusRef();
          final String conditionalEventID = rel.getCondRef();
          final RelationOperator operator = this.trans.xml2ValueObject(rel.getRelType());
          final RelationParameter leftSide = createRelationParameter(variantID, this.trans.xml2ValueObject(rel.getLpType()), rel.getLpRef());
          final RelationParameter rightSide = createRelationParameter(variantID, this.trans.xml2ValueObject(rel.getRpType()), rel.getRpRef());
          final Relation r = new Relation(label, description, relationID, variantID, leftSide, rightSide, operator, conditionalEventID);
          save(KEY_PREFIX_RELATION, relationID, r);
          attachRelation(r);
        }
      }
    }
    save(KEY_ALL_RELATIONS, allRelations);
  }

  private RelationParameter createRelationParameter(final String variantID, final String parameterType, final String parameterValue) {
    RelationParameter param = null;
    if (!StringUtils.isEmpty(variantID)) {
      if (RelationParameter.PARAMETER_TYPE_FEATURE.equals(parameterType)) {
        // value is reference to a parameter which is a reference to a feature
        param = getRelationParameter(parameterValue);
      }
      else {
        // value is reference to a feature value, i.e. a constant
        final FeatureValue fv = getFeatureValue(parameterValue);
        final String featureValueID = (fv == null ? null : fv.getFeatureValueID());
        if (!StringUtils.isEmpty(featureValueID)) {
          // create a new parameter for this constant value
          param = new RelationParameter(variantID, featureValueID);
          final String parameterID = param.getParameterID();
          RelationParameters allParams = getRelationParameters();
          if (allParams == null) { // strange but possible
            allParams = new RelationParameters();
          }
          allParams.addParameter(param);
          save(KEY_ALL_RELATION_PARAMS, allParams);
          save(KEY_PREFIX_RELATION_PARAM, parameterID, param);
          attachRelationParameter(param);
        }
      }
    }
    return param;
  }

  // ----------------------------------------------------------------

  private void attachEvent(final Event e) {
    final String variantId = (e == null ? null : e.getVariantID());
    if (!StringUtils.isEmpty(variantId)) {
      Events evts = getAttachedEvents(variantId);
      if (evts == null) {
        evts = new Events();
      }
      evts.addEvent(e);
      save(KEY_PREFIX_ATTACHED_EVENTS, variantId, evts);
    }
  }

  private void attachRule(final Rule r) {
    final String variantId = (r == null ? null : r.getVariantID());
    if (!StringUtils.isEmpty(variantId)) {
      Rules rules = getAttachedRules(variantId);
      if (rules == null) {
        rules = new Rules();
      }
      rules.addRule(r);
      save(KEY_PREFIX_ATTACHED_RULES, variantId, rules);
    }
  }

  private void attachRelationParameter(final RelationParameter p) {
    final String variantId = (p == null ? null : p.getVariantID());
    if (!StringUtils.isEmpty(variantId)) {
      RelationParameters params = getAttachedRelationParameters(variantId);
      if (params == null) {
        params = new RelationParameters();
      }
      params.addParameter(p);
      save(KEY_PREFIX_ATTACHED_RELATION_PARAMS, variantId, params);
    }
  }

  private void attachRelation(final Relation r) {
    final String variantId = (r == null ? null : r.getVariantID());
    if (!StringUtils.isEmpty(variantId)) {
      Relations relations = getAttachedRelations(variantId);
      if (relations == null) {
        relations = new Relations();
      }
      relations.addRelation(r);
      save(KEY_PREFIX_ATTACHED_RELATIONS, variantId, relations);
    }
  }

  // ----------------------------------------------------------------

  private void save(final String key, final String subkey, final Object value) {
    if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(subkey)) {
      save(key + subkey, value);
    }
  }

  private void save(final String key, final Object value) {
    if ((value != null) && !StringUtils.isEmpty(key)) {
      this.knowledge.put(key, value);
    }
  }

  private Object load(final String key, final String subkey) {
    return (StringUtils.isEmpty(key) || StringUtils.isEmpty(subkey) ? null : load(key + subkey));
  }

  private Object load(final String key) {
    Object value = null;
    if (!StringUtils.isEmpty(key)) {
      value = this.knowledge.get(key);
    }
    return value;
  }
}
