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
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
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
  private static final String KEY_ALL_PURPOSES = "kb.all.purposes";
  private static final String KEY_ALL_VARIANTS = "kb.all.variants";
  private static final String KEY_ALL_ALTERNATIVES = "kb.all.alternatives";
  private static final String KEY_ALL_CONSTITUENTS = "kb.all.constituents";
  private static final String KEY_ALL_EVENTS = "kb.all.events";
  private static final String KEY_ALL_RULES = "kb.all.rules";
  private static final String KEY_ALL_RELATIONS = "kb.all.relations";

  private static final String KEY_PREFIX_FEATURE = "kb.feature.";
  private static final String KEY_PREFIX_PURPOSE = "kb.purpose.";
  private static final String KEY_PREFIX_VARIANT = "kb.variant.";
  private static final String KEY_PREFIX_EVENT = "kb.event.";
  private static final String KEY_PREFIX_RULE = "kb.rule.";
  private static final String KEY_PREFIX_RELATION = "kb.relation.";

  private static final String KEY_PREFIX_FULFILLS = "kb.fulfills.";
  private static final String KEY_PREFIX_CONSTITUTES = "kb.constitutes.";

  private static final String KEY_PREFIX_ATTACHED_EVENTS = "kb.attached.events.";
  private static final String KEY_PREFIX_ATTACHED_RULES = "kb.attached.rules.";
  private static final String KEY_PREFIX_ATTACHED_RELATIONS = "kb.attached.relations.";

  private static final String KEY_ROOT_PURPOSES = "kb.root.purposes";

  private Map<String, Object> knowledge;
  private Transformer trans;
  private boolean valid;

  public XmlKnowledgeBase() {
    this(null);
  }

  public XmlKnowledgeBase(final Transformer trans) {
    this(trans, null);
  }

  public XmlKnowledgeBase(final Transformer trans, final Map<String, Object> knowledge) {
    this(trans, knowledge, false);
  }

  public XmlKnowledgeBase(final Transformer trans, final Map<String, Object> knowledge, final boolean valid) {
    setTransformer(trans);
    setKnowledge(knowledge);
    setValid(valid);
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

  // -------------------------------------------------------------
  // Methods required for the Interface KnowledgeBase
  // -------------------------------------------------------------

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
   * @return all Relations
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelations()
   */
  @Override
  public Relations getRelations() {
    return (Relations) load(KEY_ALL_RELATIONS);
  }

  //-------------------------------------------------------------

  /**
   * @param featureId
   * @return Feature<?>
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature<?> getFeature(final String featureId) {
    return (Feature<?>) load(KEY_PREFIX_FEATURE, featureId);
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
   * @param relationId
   * @return Relation
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelation(java.lang.String)
   */
  @Override
  public Relation getRelation(final String relationId) {
    return (Relation) load(KEY_PREFIX_RELATION, relationId);
  }

  //-------------------------------------------------------------

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
   * @param purposeId
   * @param variantId
   * @return long quantity
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getQuantity(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public long getQuantity(final String purposeId, final String variantId) {
    if (!StringUtils.isEmpty(purposeId) && !StringUtils.isEmpty(variantId)) {
      final Fulfills ff = getFulfills(purposeId);
      if (ff != null) {
        for (final String vid : ff.getVariantID()) {
          if (variantId.equals(vid)) {
            return ff.getQuantity();
          }
        }
      }
    }
    return Fulfills.DEFAULT_QUANTITY;
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
          final Feature<?> f = getFeature(featureId);
          if (f != null) {
            feats.addFeature(f);
          }
        }
      }
    }
    return feats;
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
   * @return Relations attached to Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRelations(java.lang.String)
   */
  @Override
  public Relations getAttachedRelations(final String variantId) {
    return (Relations) load(KEY_PREFIX_ATTACHED_RELATIONS, variantId);
  }

  // -------------------------------------------------------------

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
        final List<String> pids = consts.getPurposeID();
        for (final String purposeId : pids) {
          final Purpose p = getPurpose(purposeId);
          if (p != null) {
            conpurps.addPurpose(p);
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
    final List<Feature<?>> lst = feats.getFeature();
    for (final Feature<?> idx : lst) {
      if ((idx != null) && featureId.equals(idx.getFeatureID())) {
        return true;
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

  // -------------------------------------------------------------
  // Method required for the Interface KBParserCallback
  // -------------------------------------------------------------

  /**
   * @param element
   * @see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object)
   */
  @Override
  public void handleElement(final Object element) {
    try {
      if (element instanceof org.psikeds.knowledgebase.jaxb.Knowledgebase) {
        setKnowledgebase((org.psikeds.knowledgebase.jaxb.Knowledgebase) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Meta) {
        setMetaData((org.psikeds.knowledgebase.jaxb.Meta) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Data) {
        setData((org.psikeds.knowledgebase.jaxb.Data) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Features) {
        setFeatures((org.psikeds.knowledgebase.jaxb.Features) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Purposes) {
        setPurposes((org.psikeds.knowledgebase.jaxb.Purposes) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Variants) {
        setVariants((org.psikeds.knowledgebase.jaxb.Variants) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Constituents) {
        setConstituents((org.psikeds.knowledgebase.jaxb.Constituents) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Alternatives) {
        setAlternatives((org.psikeds.knowledgebase.jaxb.Alternatives) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Events) {
        setEvents((org.psikeds.knowledgebase.jaxb.Events) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Rules) {
        setRules((org.psikeds.knowledgebase.jaxb.Rules) element);
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Relations) {
        setRelations((org.psikeds.knowledgebase.jaxb.Relations) element);
      }
      else {
        LOGGER.warn("Skipping unexpected XML-Element:\n{}", element);
      }
    }
    catch (final Exception ex) {
      LOGGER.error("Exception while handling XML-Element:\n{}", element);
      LOGGER.error("handleElement() failed!", ex);
    }
  }

  // -------------------------------------------------------------
  // Internal Helpers
  // -------------------------------------------------------------

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
      setFeatures(data.getFeatures());
      setPurposes(data.getPurposes());
      setVariants(data.getVariants());
      setAlternatives(data.getAlternatives());
      setConstituents(data.getConstituents());
      setEvents(data.getEvents());
      setRules(data.getRules());
      setRelations(data.getRelations());
    }
  }

  private void setFeatures(final org.psikeds.knowledgebase.jaxb.Features feats) {
    if (feats != null) {
      setFeatures(this.trans.xml2ValueObject(feats));
    }
  }

  private void setFeatures(final Features feats) {
    if (feats != null) {
      save(KEY_ALL_FEATURES, feats);
      final List<Feature<?>> lst = feats.getFeature();
      for (final Feature<?> f : lst) {
        if (f != null) {
          save(KEY_PREFIX_FEATURE, f.getFeatureID(), f);
        }
      }
    }
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
    if (vars != null) {
      setVariants(this.trans.xml2ValueObject(vars));
    }
  }

  private void setVariants(final Variants vars) {
    if (vars != null) {
      save(KEY_ALL_VARIANTS, vars);
      final List<Variant> lst = vars.getVariant();
      for (final Variant v : lst) {
        if (v != null) {
          save(KEY_PREFIX_VARIANT, v.getVariantID(), v);
        }
      }
    }
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
        setFulfills(full);
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
        setConstitutes(c);
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
    if (relations != null) {
      setRelations(this.trans.xml2ValueObject(relations));
    }
  }

  private void setRelations(final Relations relations) {
    if (relations != null) {
      save(KEY_ALL_RELATIONS, relations);
      final List<Relation> lst = relations.getRelation();
      for (final Relation r : lst) {
        if (r != null) {
          save(KEY_PREFIX_RELATION, r.getRelationID(), r);
          attachRelation(r);
        }
      }
    }
  }

  // -------------------------------------------------------------

  private void setConstitutes(final Constitutes cons) {
    final String variantId = (cons == null ? null : cons.getVariantID());
    save(KEY_PREFIX_CONSTITUTES, variantId, cons);
  }

  private void setFulfills(final Fulfills full) {
    final String purposeId = (full == null ? null : full.getPurposeID());
    save(KEY_PREFIX_FULFILLS, purposeId, full);
  }

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

  // -------------------------------------------------------------

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
