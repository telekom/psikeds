/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
import org.psikeds.resolutionengine.datalayer.vo.Data;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Knowledgebase;
import org.psikeds.resolutionengine.datalayer.vo.Meta;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * This implementation of a KnowledgeBase acts as a KBParserCallback
 * and receives Data from an XML-Source.
 *
 * @author marco@juliano.de
 *
 */
public class XmlKnowledgeBase implements KnowledgeBase, KBParserCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlKnowledgeBase.class);

  private static final String KEY_META = "kb.meta";
  private static final String KEY_DATA = "kb.data";

  private static final String KEY_ALL_FEATURES = "kb.all.features";
  private static final String KEY_ALL_PURPOSES = "kb.all.purposes";
  private static final String KEY_ALL_VARIANTS = "kb.all.variants";
  private static final String KEY_ALL_ALTERNATIVES = "kb.all.alternatives";
  private static final String KEY_ALL_CONSTITUENTS = "kb.all.constituents";
  private static final String KEY_ALL_EVENTS = "kb.all.events";
  private static final String KEY_ALL_RULES = "kb.all.rules";

  private static final String KEY_PREFIX_FEATURE = "kb.feature.";
  private static final String KEY_PREFIX_PURPOSE = "kb.purpose.";
  private static final String KEY_PREFIX_VARIANT = "kb.variant.";
  private static final String KEY_PREFIX_EVENT = "kb.all.event.";
  private static final String KEY_PREFIX_RULE = "kb.all.rule.";

  private Map<String, Object> knowledge;
  private Transformer trans;
  private boolean valid;

  public XmlKnowledgeBase() {
    this(new Xml2VoTransformer());
  }

  public XmlKnowledgeBase(final Transformer trans) {
    this(trans, new ConcurrentHashMap<String, Object>());
  }

  public XmlKnowledgeBase(final Transformer trans, final Map<String, Object> knowledge) {
    this.trans = trans;
    this.knowledge = knowledge;
    this.valid = false; // this kb is not valid unless explicitly validated!
  }

  public Transformer getTransformer() {
    return this.trans;
  }

  public void setTransformer(final Transformer trans) {
    this.trans = trans;
  }

  public Map<String, Object> getKnowledge() {
    return this.knowledge;
  }

  public void setKnowledge(final Map<String, Object> knowledge) {
    this.knowledge = knowledge;
  }

  public void setValid(final boolean valid) {
    this.valid = valid;
  }

  // -------------------------------------------------------------
  // Methods required for the Interface KnowledgeBase
  // -------------------------------------------------------------

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
   * @param featureId
   * @return Feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature getFeature(final String featureId) {
    return (Feature) load(KEY_PREFIX_FEATURE + featureId);
  }

  /**
   * @param purposeId
   * @return Purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurpose(java.lang.String)
   */
  @Override
  public Purpose getPurpose(final String purposeId) {
    return (Purpose) load(KEY_PREFIX_PURPOSE + purposeId);
  }

  /**
   * @param variantId
   * @return Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariant(java.lang.String)
   */
  @Override
  public Variant getVariant(final String variantId) {
    return (Variant) load(KEY_PREFIX_VARIANT + variantId);
  }

  /**
   * @param eventId
   * @return Event
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvent(java.lang.String)
   */
  @Override
  public Event getEvent(final String eventId) {
    return (Event) load(KEY_PREFIX_EVENT + eventId);
  }

  /**
   * @param ruleId
   * @return Rule
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRule(java.lang.String)
   */
  @Override
  public Rule getRule(final String ruleId) {
    return (Rule) load(KEY_PREFIX_RULE + ruleId);
  }

  /**
   * @return all Purposes flagged with "root" attribute
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRootPurposes()
   */
  @Override
  public Purposes getRootPurposes() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param purposeId
   * @return
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfillingVariants(java.lang.String)
   */
  @Override
  public Variants getFulfillingVariants(final String purposeId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param purpose
   * @return
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfillingVariants(org.psikeds.resolutionengine.datalayer.vo.Purpose)
   */
  @Override
  public Variants getFulfillingVariants(final Purpose purpose) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param variantId
   * @return
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures(java.lang.String)
   */
  @Override
  public Features getFeatures(final String variantId) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param variant
   * @return
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures(org.psikeds.resolutionengine.datalayer.vo.Variant)
   */
  @Override
  public Features getFeatures(final Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return boolean valid
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
        setKnowledgebase(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Knowledgebase) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Meta) {
        setMeta(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Meta) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Data) {
        setData(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Data) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Features) {
        setFeatures(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Features) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Purposes) {
        setPurposes(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Purposes) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Variants) {
        setVariants(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Variants) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Constituents) {
        setConstituents(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Constituents) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Alternatives) {
        setAlternatives(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Alternatives) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Events) {
        setEvents(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Events) element));
      }
      else if (element instanceof org.psikeds.knowledgebase.jaxb.Rules) {
        setRules(this.trans.xml2ValueObject((org.psikeds.knowledgebase.jaxb.Rules) element));
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

  private void setKnowledgebase(final Knowledgebase kb) {
    if (kb != null) {
      setMeta(kb.getMeta());
      setData(kb.getData());
    }
  }

  private void setMeta(final Meta meta) {
    if (meta != null) {
      save(KEY_META, meta);
    }
  }

  private void setData(final Data data) {
    if (data != null) {
      save(KEY_DATA, data);
      setFeatures(data.getFeatures());
      setPurposes(data.getPurposes());
      setVariants(data.getVariants());
      setAlternatives(data.getAlternatives());
      setConstituents(data.getConstituents());
      setEvents(data.getEvents());
      setRules(data.getRules());
    }
  }

  private void setFeatures(final Features feats) {
    if (feats != null) {
      save(KEY_ALL_FEATURES, feats);
      final List<Feature> lst = feats.getFeature();
      for (final Feature f : lst) {
        save(KEY_PREFIX_FEATURE + f.getId(), f);
      }
    }
  }

  private void setPurposes(final Purposes purps) {
    if (purps != null) {
      save(KEY_ALL_PURPOSES, purps);
      final List<Purpose> lst = purps.getPurpose();
      for (final Purpose p : lst) {
        save(KEY_PREFIX_PURPOSE + p.getId(), p);
      }
    }
  }

  private void setVariants(final Variants vars) {
    if (vars != null) {
      save(KEY_ALL_VARIANTS, vars);
      final List<Variant> lst = vars.getVariant();
      for (final Variant v : lst) {
        save(KEY_PREFIX_VARIANT + v.getId(), v);
      }
    }
  }

  private void setAlternatives(final Alternatives alts) {
    if (alts != null) {
      save(KEY_ALL_ALTERNATIVES, alts);
    }
  }

  private void setConstituents(final Constituents cons) {
    if (cons != null) {
      save(KEY_ALL_CONSTITUENTS, cons);
    }
  }

  private void setEvents(final Events evnts) {
    if (evnts != null) {
      save(KEY_ALL_EVENTS, evnts);
      final List<Event> lst = evnts.getEvent();
      for (final Event e : lst) {
        save(KEY_PREFIX_EVENT + e.getId(), e);
      }
    }
  }

  private void setRules(final Rules rules) {
    if (rules != null) {
      save(KEY_ALL_RULES, rules);
      final List<Rule> lst = rules.getRule();
      for (final Rule r : lst) {
        save(KEY_PREFIX_RULE + r.getRuleID(), r);
      }
    }
  }

  // -------------------------------------------------------------

  private void save(final String key, final Object value) {
    if (value != null && !StringUtils.isEmpty(key)) {
      LOGGER.trace("save: key = {}\nvalue = {}", key, value);
      this.knowledge.put(key, value);
    }
  }

  private Object load(final String key) {
    Object value = null;
    if (!StringUtils.isEmpty(key)) {
      value = this.knowledge.get(key);
    }
    LOGGER.trace("load: key = {}\nvalue = {}", key, value);
    return value;
  }
}
