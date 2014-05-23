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
package org.psikeds.resolutionengine.services;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.util.JSONHelper;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.util.FeatureValueHelper;
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
import org.psikeds.resolutionengine.datalayer.vo.KnowledgeData;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameters;
import org.psikeds.resolutionengine.datalayer.vo.Relations;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * Mock-Implementation of KnowledgeBase for Testing.
 * 
 * Reads a pre-created KnowledgeBase from a JSON-File or otherwise simply
 * returns Dummy-Value.
 * 
 * When run as a standalone Java-Application, Test-Data will be created.
 * 
 * @author marco@juliano.de
 * 
 */
public class KnowledgeBaseMock implements KnowledgeBase {

  private static final String LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "./src/main/resources/log4j.xml");
  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseMock.class);

  private static final String TEST_DATA_DIR = System.getProperty("org.psikeds.test.data.dir", "./src/test/resources/");
  private static final File KNOWLEDGEDATA = new File(TEST_DATA_DIR, "KnowledgeData.json");

  private KnowledgeData knowledge;

  public KnowledgeBaseMock() {
    this(null);
  }

  public KnowledgeBaseMock(final KnowledgeData knowledge) {
    this.knowledge = knowledge;
  }

  // ----------------------------------------------------------------
  // Methods of Interface KnowledgeBase
  // ----------------------------------------------------------------

  /**
   * @return Features
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures()
   */
  @Override
  public Features getFeatures() {
    return getKnowledgeData().getFeatures();
  }

  /**
   * @return FeatureValues
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValues()
   */
  @Override
  public FeatureValues getFeatureValues() {
    return getKnowledgeData().getFeatureValues();
  }

  /**
   * @return Concepts
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConcepts()
   */
  @Override
  public Concepts getConcepts() {
    return getKnowledgeData().getConcepts();
  }

  /**
   * @return Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurposes()
   */
  @Override
  public Purposes getPurposes() {
    return getKnowledgeData().getPurposes();
  }

  /**
   * @return Variants
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariants()
   */
  @Override
  public Variants getVariants() {
    return getKnowledgeData().getVariants();
  }

  /**
   * @return Alternatives
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAlternatives()
   */
  @Override
  public Alternatives getAlternatives() {
    return getKnowledgeData().getAlternatives();
  }

  /**
   * @return Constituents
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstituents()
   */
  @Override
  public Constituents getConstituents() {
    return getKnowledgeData().getConstituents();
  }

  /**
   * @return Events
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvents()
   */
  @Override
  public Events getEvents() {
    return getKnowledgeData().getEvents();
  }

  /**
   * @return Rules
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRules()
   */
  @Override
  public Rules getRules() {
    return getKnowledgeData().getRules();
  }

  /**
   * @return RelationParameters
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelationParameters()
   */
  @Override
  public RelationParameters getRelationParameters() {
    return getKnowledgeData().getRelationParameters();
  }

  /**
   * @return Relations
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelations()
   */
  @Override
  public Relations getRelations() {
    return getKnowledgeData().getRelations();
  }

  /**
   * @param featureId
   * @return Feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature getFeature(final String featureId) {
    if (!StringUtils.isEmpty(featureId)) {
      for (final Feature f : getFeatures().getFeature()) {
        if ((f != null) && featureId.equals(f.getFeatureID())) {
          return f;
        }
      }
    }
    final Feature f = new Feature(featureId);
    if (!StringUtils.isEmpty(featureId)) {
      final String lower = featureId.toLowerCase();
      final String uppper = featureId.toUpperCase();
      f.addValue(lower + "L11", lower);
      f.addValue(uppper + "u22", uppper);
    }
    LOGGER.debug("Feature {} not found, returning Dummy!", featureId);
    return f;
  }

  /**
   * @param featureValueID
   * @return FeatureValue
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValue(java.lang.String)
   */
  @Override
  public FeatureValue getFeatureValue(final String featureValueID) {
    if (!StringUtils.isEmpty(featureValueID)) {
      for (final FeatureValue fv : getFeatureValues().getValue()) {
        if ((fv != null) && featureValueID.equals(fv.getFeatureValueID())) {
          return fv;
        }
      }
    }
    LOGGER.debug("FeatureValue {} not found, returning Dummy!", featureValueID);
    return new FeatureValue(featureValueID, featureValueID, Feature.VALUE_TYPE_STRING, featureValueID);
  }

  /**
   * @param conceptID
   * @return Concept
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConcept(java.lang.String)
   */
  @Override
  public Concept getConcept(final String conceptID) {
    if (!StringUtils.isEmpty(conceptID)) {
      for (final Concept c : getConcepts().getConcept()) {
        if ((c != null) && conceptID.equals(c.getConceptID())) {
          return c;
        }
      }
    }
    LOGGER.debug("Concept {} not found, returning Dummy!", conceptID);
    return new Concept(conceptID);
  }

  /**
   * @param purposeId
   * @return Purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurpose(java.lang.String)
   */
  @Override
  public Purpose getPurpose(final String purposeId) {
    if (!StringUtils.isEmpty(purposeId)) {
      for (final Purpose p : getPurposes().getPurpose()) {
        if ((p != null) && purposeId.equals(p.getPurposeID())) {
          return p;
        }
      }
    }
    LOGGER.debug("Purpose {} not found, returning Dummy!", purposeId);
    return new Purpose(purposeId);
  }

  /**
   * @param variantId
   * @return Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariant(java.lang.String)
   */
  @Override
  public Variant getVariant(final String variantId) {
    if (!StringUtils.isEmpty(variantId)) {
      for (final Variant v : getVariants().getVariant()) {
        if ((v != null) && variantId.equals(v.getVariantID())) {
          return v;
        }
      }
    }
    LOGGER.debug("Variant {} not found, returning Dummy!", variantId);
    return new Variant(variantId);
  }

  /**
   * @param eventId
   * @return Event
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvent(java.lang.String)
   */
  @Override
  public Event getEvent(final String eventId) {
    if (!StringUtils.isEmpty(eventId)) {
      for (final Event e : getEvents().getEvent()) {
        if ((e != null) && eventId.equals(e.getEventID())) {
          return e;
        }
      }
    }
    LOGGER.debug("Event {} not found, returning Dummy!", eventId);
    return new Event(eventId, null);
  }

  /**
   * @param ruleId
   * @return Rule
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRule(java.lang.String)
   */
  @Override
  public Rule getRule(final String ruleId) {
    if (!StringUtils.isEmpty(ruleId)) {
      for (final Rule r : getRules().getRule()) {
        if ((r != null) && ruleId.equals(r.getRuleID())) {
          return r;
        }
      }
    }
    LOGGER.debug("Rule {} not found, returning Dummy!", ruleId);
    return new Rule(ruleId, null);
  }

  /**
   * @param parameterID
   * @return RelationParameter
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelationParameter(java.lang.String)
   */
  @Override
  public RelationParameter getRelationParameter(final String parameterID) {
    if (!StringUtils.isEmpty(parameterID)) {
      for (final RelationParameter p : getRelationParameters().getParameter()) {
        if ((p != null) && parameterID.equals(p.getParameterID())) {
          return p;
        }
      }
    }
    LOGGER.debug("RelationParameter {} not found, returning Dummy!", parameterID);
    return new RelationParameter(parameterID, null, parameterID, null, null, null);
  }

  /**
   * @return Relation
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelation(java.lang.String)
   */
  @Override
  public Relation getRelation(final String relationId) {
    if (!StringUtils.isEmpty(relationId)) {
      for (final Relation r : getRelations().getRelation()) {
        if ((r != null) && relationId.equals(r.getRelationID())) {
          return r;
        }
      }
    }
    LOGGER.debug("Relation {} not found, returning Dummy!", relationId);
    return new Relation(relationId, null, relationId, null, null, null, null);
  }

  /**
   * @param purposeId
   * @return Fulfills
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfills(java.lang.String)
   */
  @Override
  public Fulfills getFulfills(final String purposeId) {
    if (!StringUtils.isEmpty(purposeId)) {
      for (final Fulfills f : getAlternatives().getFulfills()) {
        if ((f != null) && purposeId.equals(f.getPurposeID())) {
          return f;
        }
      }
    }
    LOGGER.debug("No Fulfills for Purpose {} found, returning Dummy!", purposeId);
    return new Fulfills(purposeId, null);
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
   * @return Constitutes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutes(java.lang.String)
   */
  @Override
  public Constitutes getConstitutes(final String variantId) {
    if (!StringUtils.isEmpty(variantId)) {
      for (final Constitutes c : getConstituents().getConstitutes()) {
        if ((c != null) && variantId.equals(c.getVariantID())) {
          return c;
        }
      }
    }
    LOGGER.debug("No Constitutes for Variant {} found, returning Dummy!", variantId);
    return new Constitutes(variantId, (String) null);
  }

  /**
   * @param variantId
   * @return Events
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedEvents(java.lang.String)
   */
  @Override
  public Events getAttachedEvents(final String variantId) {
    return getEvents();
  }

  /**
   * @param variantId
   * @return Rules
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRules(java.lang.String)
   */
  @Override
  public Rules getAttachedRules(final String variantId) {
    return getRules();
  }

  /**
   * @param variantId
   * @return RelationParameters
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRelationParameters(java.lang.String)
   */
  @Override
  public RelationParameters getAttachedRelationParameters(final String variantId) {
    return getRelationParameters();
  }

  /**
   * @param variantId
   * @return Relations
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAttachedRelations(java.lang.String)
   */
  @Override
  public Relations getAttachedRelations(final String variantId) {
    return getRelations();
  }

  /**
   * @param purposeId
   * @param variantId
   * @return true if fulfilled
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isFulfilledBy(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean isFulfilledBy(final String purposeId, final String variantId) {
    if (!StringUtils.isEmpty(purposeId) && !StringUtils.isEmpty(variantId)) {
      final Variants vars = getFulfillingVariants(purposeId);
      final List<Variant> lst = (vars == null ? null : vars.getVariant());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Variant idx : lst) {
          if ((idx != null) && variantId.equals(idx.getVariantID())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param variantId
   * @param purposeId
   * @return true if constituted
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isConstitutedBy(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean isConstitutedBy(final String variantId, final String purposeId) {
    if (!StringUtils.isEmpty(purposeId) && !StringUtils.isEmpty(variantId)) {
      final Purposes purps = getConstitutingPurposes(variantId);
      final List<Purpose> lst = purps.getPurpose();
      for (final Purpose idx : lst) {
        if ((idx != null) && purposeId.equals(idx.getPurposeID())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRootPurposes()
   */
  @Override
  public Purposes getRootPurposes() {
    final Purposes purps = new Purposes();
    for (final Purpose p : getPurposes().getPurpose()) {
      if ((p != null) && p.isRoot()) {
        purps.addPurpose(p);
      }
    }
    if (purps.getPurpose().isEmpty()) {
      final Purpose p = getPurpose("root");
      p.setRoot(true);
      purps.addPurpose(p);
    }
    return purps;
  }

  /**
   * @param purposeId
   * @return Variants
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfillingVariants(java.lang.String)
   */
  @Override
  public Variants getFulfillingVariants(final String purposeId) {
    final Variants vars = new Variants();
    final Fulfills ff = (StringUtils.isEmpty(purposeId) ? null : getFulfills(purposeId));
    final List<String> vids = (ff == null ? null : ff.getVariantID());
    if ((vids != null) && !vids.isEmpty()) {
      for (final String variantId : vids) {
        final Variant v = getVariant(variantId);
        vars.addVariant(v);
      }
    }
    return vars;
  }

  /**
   * @param variantId
   * @return Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutingPurposes(java.lang.String)
   */
  @Override
  public Purposes getConstitutingPurposes(final String variantId) {
    final Purposes conpurps = new Purposes();
    if (!StringUtils.isEmpty(variantId)) {
      final Constitutes consts = (StringUtils.isEmpty(variantId) ? null : getConstitutes(variantId));
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
   * @return Features
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures(java.lang.String)
   */
  @Override
  public Features getFeatures(final String variantId) {
    final Features feats = new Features();
    final Variant v = (StringUtils.isEmpty(variantId) ? null : getVariant(variantId));
    final List<String> fids = (v == null ? null : v.getFeatureIds());
    if ((fids != null) && !fids.isEmpty()) {
      for (final String featureId : fids) {
        final Feature f = getFeature(featureId);
        if (f != null) {
          feats.addFeature(f);
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
   * @param featureId
   * @return all values for this feature on this variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatureValues(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public FeatureValues getFeatureValues(final String variantId, final String featureId) {
    final FeatureValues values = new FeatureValues();
    final Variant v = (StringUtils.isEmpty(variantId) ? null : getVariant(variantId));
    if (v != null) {
      for (final FeatureValue fv : v.getFeatureValues()) {
        if ((fv != null) && fv.getFeatureID().equals(featureId)) {
          values.addValue(fv);
        }
      }
    }
    return values;
  }

  /**
   * @param variantId
   * @param conceptID
   * @return true if variant has this concept
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#hasConcept(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean hasConcept(final String variantId, final String conceptID) {
    if (!StringUtils.isEmpty(variantId) && !StringUtils.isEmpty(conceptID)) {
      final Variant var = getVariant(variantId);
      final List<Concept> lst = (var == null ? null : var.getConcepts());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Concept idx : lst) {
          if ((idx != null) && conceptID.equals(idx.getConceptID())) {
            return true;
          }
        }
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
    if (!StringUtils.isEmpty(variantId) && !StringUtils.isEmpty(featureId)) {
      final Features feats = getFeatures(variantId);
      final List<Feature> lst = (feats == null ? null : feats.getFeature());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Feature idx : lst) {
          if ((idx != null) && featureId.equals(idx.getFeatureID())) {
            return true;
          }
        }
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
   * @return boolean
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isValid()
   */
  @Override
  public boolean isValid() {
    return (getKnowledgeData() != null) && (getMetaData() != null);
  }

  /**
   * @return MetaData
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getMetaData()
   */
  @Override
  public MetaData getMetaData() {
    return getKnowledgeData().getMetadata();
  }

  // ----------------------------------------------------------------
  // Internal Helpers
  // ----------------------------------------------------------------

  private synchronized KnowledgeData getKnowledgeData() {
    if (this.knowledge == null) {
      try {
        // Load Knowledgebase from JSON
        this.knowledge = JSONHelper.readObjectFromJsonFile(KNOWLEDGEDATA, KnowledgeData.class);
      }
      catch (final Exception ex) {
        LOGGER.error("Could not load KnowledgeData " + KNOWLEDGEDATA.getPath(), ex);
        this.knowledge = null;
      }
      if (this.knowledge == null) {
        // Fallback: empty Knowledgebase, i.e. return always dummies
        this.knowledge = new KnowledgeData();
      }
    }
    return this.knowledge;
  }

  // ----------------------------------------------------------------
  // Methods for creating Test-Data
  // ----------------------------------------------------------------

  private static String getCurrentUser() {
    String username;
    try {
      username = System.getProperty("user.name");
    }
    catch (final Exception ex) {
      username = null;
    }
    return StringUtils.isEmpty(username) ? "marco@juliano.de" : username;
  }

  private static String getHostName() {
    String hostname;
    try {
      final InetAddress machine = InetAddress.getLocalHost();
      hostname = machine.getHostName();
    }
    catch (final Exception ex) {
      hostname = null;
    }
    return StringUtils.isEmpty(hostname) ? "www.psikeds.org" : hostname;
  }

  private static String getLanguage() {
    String lang;
    try {
      lang = Locale.getDefault().toString();
    }
    catch (final Exception ex) {
      lang = null;
    }
    return StringUtils.isEmpty(lang) ? "en_EN" : lang;
  }

  private static String getKnowledgeBaseIdentifier() {
    final StringBuilder sb = new StringBuilder("PSIKEDS");
    sb.append(System.currentTimeMillis());
    return sb.toString();
  }

  private static MetaData createMetaData() {
    final String kbid = getKnowledgeBaseIdentifier();
    final Calendar created = Calendar.getInstance();
    final String now = DateFormat.getDateTimeInstance().format(created.getTime());
    final String user = getCurrentUser();
    final String host = getHostName();
    final String language = getLanguage();
    final String release = "V1.0";
    final List<String> creator = new ArrayList<String>();
    creator.add(user);
    final List<String> description = new ArrayList<String>();
    description.add("Mock Knowledgebase " + release + " for Testing Purposes");
    description.add("Created by " + user + " on " + host + " at " + now);
    final Map<String, Object> additionalInfo = new ConcurrentHashMap<String, Object>();
    additionalInfo.put("KB_CREATOR", user);
    additionalInfo.put("KB_SERVER", host);
    return new MetaData(kbid, kbid, null, release, null, null, language, created, created, created, creator, description, additionalInfo);
  }

  private static KnowledgeData createKnowledgeData() {
    // create features and feature-values
    final Features features = new Features();
    final FeatureValues values = new FeatureValues();
    final List<FeatureValue> f1ir1 = FeatureValueHelper.calculateIntegerRange("F1", "IR1", 1, 5, 1);
    values.addValue(f1ir1);
    final Feature f1 = new Feature("F1", "F1", "F1", Feature.VALUE_TYPE_INTEGER, null, f1ir1);
    features.addFeature(f1);
    final List<FeatureValue> f2fr2 = FeatureValueHelper.calculateFloatRange("F2", "FR2", 2.0f, 6.0f, 2.0f);
    values.addValue(f2fr2);
    final Feature f2 = new Feature("F2", "F2", "F2", Feature.VALUE_TYPE_FLOAT, null, f2fr2);
    features.addFeature(f2);
    final Feature f3 = new Feature("F3");
    final FeatureValue f3s1 = new FeatureValue("F3", "F3S1", Feature.VALUE_TYPE_STRING, "lala");
    values.addValue(f3s1);
    f3.addValue(f3s1);
    final FeatureValue f3s2 = new FeatureValue("F3", "F3S2", Feature.VALUE_TYPE_STRING, "lele");
    values.addValue(f3s2);
    f3.addValue(f3s2);
    final FeatureValue f3s3 = new FeatureValue("F3", "F3S3", Feature.VALUE_TYPE_STRING, "lolo");
    values.addValue(f3s3);
    f3.addValue(f3s3);
    final FeatureValue f3s4 = new FeatureValue("F3", "F3S4", Feature.VALUE_TYPE_STRING, "lulu");
    values.addValue(f3s4);
    f3.addValue(f3s4);
    features.addFeature(f3);
    final List<FeatureValue> f4ir4 = FeatureValueHelper.calculateIntegerRange("F4", "IR4", 3, 15, 3);
    values.addValue(f4ir4);
    final Feature f4 = new Feature("F4", "F4", "F4", Feature.VALUE_TYPE_INTEGER, null, f4ir4);
    features.addFeature(f4);
    final List<FeatureValue> f5fr5 = FeatureValueHelper.calculateFloatRange("F5", "FR5", 5.0000f, 8.0000f, 0.6543f);
    values.addValue(f5fr5);
    final Feature f5 = new Feature("F5", "F5", "F5", Feature.VALUE_TYPE_FLOAT, null, f5fr5);
    features.addFeature(f5);

    // create purposes and purpose-lists
    final Purpose p1 = new Purpose("P1", null, "P1", true);
    final Purpose p2 = new Purpose("P2", null, "P2", true);
    final Purpose p111 = new Purpose("P111");
    final Purpose p112 = new Purpose("P112");
    final Purpose p113 = new Purpose("P113");
    final Purpose p221 = new Purpose("P221");
    final Purpose p222 = new Purpose("P222");
    final Purpose p223 = new Purpose("P223");
    final List<Component> v11ps = new ArrayList<Component>();
    v11ps.add(new Component(p111.getPurposeID(), 1));
    v11ps.add(new Component(p112.getPurposeID(), 2));
    v11ps.add(new Component(p113.getPurposeID(), 3));
    final List<Component> v22ps = new ArrayList<Component>();
    v22ps.add(new Component(p221.getPurposeID(), 1));
    v22ps.add(new Component(p222.getPurposeID(), 2));
    v22ps.add(new Component(p223.getPurposeID(), 3));
    final List<Purpose> allpurps = new ArrayList<Purpose>();
    allpurps.add(p1);
    allpurps.add(p2);
    allpurps.add(p111);
    allpurps.add(p112);
    allpurps.add(p113);
    allpurps.add(p221);
    allpurps.add(p222);
    allpurps.add(p223);
    final Purposes purposes = new Purposes(allpurps);

    // create variants and variant-lists
    final Variant v11 = new Variant("V11");
    v11.addFeatureId(f1.getFeatureID());
    v11.addFeatureValue(f1ir1);
    v11.addFeatureId(f4.getFeatureID());
    v11.addFeatureValue(f4ir4);
    final Variant v12 = new Variant("V12");
    v12.addFeatureId(f2.getFeatureID());
    v12.addFeatureValue(f2fr2);
    v12.addFeatureId(f5.getFeatureID());
    v12.addFeatureValue(f5fr5);
    final Variant v13 = new Variant("V13");
    v13.addFeatureId(f3.getFeatureID());
    v13.addFeatureValue(f3s1);
    v13.addFeatureValue(f3s2);
    v13.addFeatureValue(f3s3);
    v13.addFeatureValue(f3s4);
    final Variant v21 = new Variant("V21");
    v21.addFeatureId(f1.getFeatureID());
    v21.addFeatureValue(f1ir1);
    v21.addFeatureId(f4.getFeatureID());
    v21.addFeatureValue(f4ir4);
    final Variant v22 = new Variant("V22");
    v22.addFeatureId(f2.getFeatureID());
    v22.addFeatureValue(f2fr2);
    v22.addFeatureId(f5.getFeatureID());
    v22.addFeatureValue(f5fr5);
    final Variant v23 = new Variant("V23");
    v23.addFeatureId(f3.getFeatureID());
    v23.addFeatureValue(f3s1);
    v23.addFeatureValue(f3s2);
    v23.addFeatureValue(f3s3);
    v23.addFeatureValue(f3s4);
    final Variant v1121 = new Variant("V1121");
    v1121.addFeatureId(f1.getFeatureID());
    v1121.addFeatureValue(f1ir1);
    v1121.addFeatureId(f4.getFeatureID());
    v1121.addFeatureValue(f4ir4);
    final Variant v1122 = new Variant("V1122");
    v1122.addFeatureId(f2.getFeatureID());
    v1122.addFeatureValue(f2fr2);
    v1122.addFeatureId(f5.getFeatureID());
    v1122.addFeatureValue(f5fr5);
    final Variant v1123 = new Variant("V1123");
    v1123.addFeatureId(f3.getFeatureID());
    v1123.addFeatureValue(f3s1);
    v1123.addFeatureValue(f3s2);
    v1123.addFeatureValue(f3s3);
    v1123.addFeatureValue(f3s4);
    final Variant v2231 = new Variant("V2231");
    v2231.addFeatureId(f1.getFeatureID());
    v2231.addFeatureValue(f1ir1);
    v2231.addFeatureId(f4.getFeatureID());
    v2231.addFeatureValue(f4ir4);
    final Variant v2232 = new Variant("V2232");
    v2232.addFeatureId(f2.getFeatureID());
    v2232.addFeatureValue(f2fr2);
    v2232.addFeatureId(f5.getFeatureID());
    v2232.addFeatureValue(f5fr5);
    final Variant v2233 = new Variant("V2233");
    v2233.addFeatureId(f3.getFeatureID());
    v2233.addFeatureValue(f3s1);
    v2233.addFeatureValue(f3s2);
    v2233.addFeatureValue(f3s3);
    v2233.addFeatureValue(f3s4);
    final List<String> p1vs = new ArrayList<String>();
    p1vs.add(v11.getVariantID());
    p1vs.add(v12.getVariantID());
    p1vs.add(v13.getVariantID());
    final List<String> p2vs = new ArrayList<String>();
    p2vs.add(v21.getVariantID());
    p2vs.add(v22.getVariantID());
    p2vs.add(v23.getVariantID());
    final List<String> p112vs = new ArrayList<String>();
    p112vs.add(v1121.getVariantID());
    p112vs.add(v1122.getVariantID());
    p112vs.add(v1123.getVariantID());
    final List<String> p223vs = new ArrayList<String>();
    p223vs.add(v2231.getVariantID());
    p223vs.add(v2232.getVariantID());
    p223vs.add(v2233.getVariantID());
    final List<Variant> allvars = new ArrayList<Variant>();
    allvars.add(v11);
    allvars.add(v12);
    allvars.add(v13);
    allvars.add(v21);
    allvars.add(v22);
    allvars.add(v23);
    allvars.add(v1121);
    allvars.add(v1122);
    allvars.add(v1123);
    allvars.add(v2231);
    allvars.add(v2232);
    allvars.add(v2233);
    final Variants variants = new Variants(allvars);

    // create alternatives, i.e. links from purposes to variants
    final Alternatives alternatives = new Alternatives();
    alternatives.addFulfills(new Fulfills(p1.getPurposeID(), p1vs));
    alternatives.addFulfills(new Fulfills(p2.getPurposeID(), p2vs));
    alternatives.addFulfills(new Fulfills(p112.getPurposeID(), p112vs));
    alternatives.addFulfills(new Fulfills(p223.getPurposeID(), p223vs));

    // create constituents, i.e. links from variants to components/purposes
    final Constituents constituents = new Constituents();
    constituents.addConstitutes(new Constitutes(v11.getVariantID(), v11ps));
    constituents.addConstitutes(new Constitutes(v22.getVariantID(), v22ps));

    // keep it simple no need for events, rules or relations in a mock
    final Concepts concepts = new Concepts();
    final Events events = new Events();
    final Rules rules = new Rules();
    final RelationParameters parameters = new RelationParameters();
    final Relations relations = new Relations();

    // compile all data
    final MetaData metadata = createMetaData();
    return new KnowledgeData(metadata, features, values, concepts, purposes, variants, alternatives, constituents, events, rules, parameters, relations);
  }

  private static void generateTestData(final boolean force) throws JsonProcessingException, IOException {
    LOGGER.info("Start generating Test-Data ... ");
    final KnowledgeData data = createKnowledgeData();
    LOGGER.debug("KnowledgeData = {}", data);
    if (force || !KNOWLEDGEDATA.exists()) {
      JSONHelper.writeObjectToJsonFile(KNOWLEDGEDATA, data);
    }
    LOGGER.info("... finished generating Test-Data.");
  }

  private static void setUpLogging() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  public static void main(final String[] args) {
    try {
      setUpLogging();
      final boolean force = (args.length > 0) && "force".equalsIgnoreCase(args[0]);
      generateTestData(force);
    }
    catch (final Exception ex) {
      LOGGER.error("Could not generate Test-Data!", ex);
    }
  }
}
