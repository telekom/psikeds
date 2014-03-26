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
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.KnowledgeData;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.Relations;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.StringFeature;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.VariantEvent;
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
   * @return Relations
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRelations()
   */
  @Override
  public Relations getRelations() {
    return getKnowledgeData().getRelations();
  }

  /**
   * @param featureId
   * @return Feature<?>
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature<?> getFeature(final String featureId) {
    if (!StringUtils.isEmpty(featureId)) {
      for (final Feature<?> f : getFeatures().getFeature()) {
        if ((f != null) && featureId.equals(f.getFeatureID())) {
          return f;
        }
      }
    }
    final StringFeature f = new StringFeature(featureId);
    if (!StringUtils.isEmpty(featureId)) {
      f.addValue(featureId.toLowerCase());
      f.addValue(featureId.toUpperCase());
    }
    LOGGER.debug("Feature {} not found, returning Dummy!", featureId);
    return f;
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
    return new VariantEvent(eventId, eventId, eventId, null, null, null);
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
    return new Rule(ruleId, ruleId, ruleId, null, null, null);
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
    return new Relation(relationId, relationId, relationId, null, null, null, null);
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
    return new Fulfills(purposeId, purposeId, null);
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
    if (!StringUtils.isEmpty(variantId)) {
      for (final Constitutes c : getConstituents().getConstitutes()) {
        if ((c != null) && variantId.equals(c.getVariantID())) {
          return c;
        }
      }
    }
    LOGGER.debug("No Constitutes for Variant {} found, returning Dummy!", variantId);
    return new Constitutes(variantId, variantId, (List<String>) null);
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
   * @param variantId
   * @param featureId
   * @return true if having feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#hasFeature(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public boolean hasFeature(final String variantId, final String featureId) {
    if (!StringUtils.isEmpty(variantId) && !StringUtils.isEmpty(featureId)) {
      final Features feats = getFeatures(variantId);
      final List<Feature<?>> lst = feats.getFeature();
      for (final Feature<?> idx : lst) {
        if ((idx != null) && featureId.equals(idx.getFeatureID())) {
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
    final Purposes purps = new Purposes();
    final Constitutes c = (StringUtils.isEmpty(variantId) ? null : getConstitutes(variantId));
    final List<String> pids = (c == null ? null : c.getPurposeID());
    if ((pids != null) && !pids.isEmpty()) {
      for (final String purposeId : c.getPurposeID()) {
        final Purpose p = getPurpose(purposeId);
        purps.addPurpose(p);
      }
    }
    return purps;
  }

  /**
   * @param variantId
   * @return Features
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures(java.lang.String)
   */
  @Override
  public Features getFeatures(final String variantId) {
    final Features feats = new Features();
    final Variant v = getVariant(variantId);
    final List<String> fids = (v == null ? null : v.getFeatureIds());
    if ((fids != null) && !fids.isEmpty()) {
      for (final String featureId : fids) {
        final Feature<?> f = getFeature(featureId);
        if (f != null) {
          feats.addFeature(f);
        }
      }
    }
    return feats;
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

  private static MetaData createMetaData() {
    final Calendar created = Calendar.getInstance();
    final Calendar lastmodified = created;
    final Calendar loaded = created;
    final String now = DateFormat.getDateTimeInstance().format(created.getTime());
    final String user = getCurrentUser();
    final String host = getHostName();
    final String language = getLanguage();
    final String version = "V1.0";
    final List<String> creator = new ArrayList<String>();
    creator.add(user);
    final List<String> description = new ArrayList<String>();
    description.add("Mock Knowledgebase " + version + " for Testing Purposes");
    description.add("Created by " + user + " on " + host + " at " + now);
    final Map<String, Object> additionalInfo = new ConcurrentHashMap<String, Object>();
    additionalInfo.put("KB_CREATOR", user);
    additionalInfo.put("KB_SERVER", host);
    return new MetaData(created, lastmodified, loaded, language, version, creator, description, additionalInfo);
  }

  private static KnowledgeData createKnowledgeData() {
    // create features and feature-lists
    final Feature<?> f1 = Feature.getFeature("F1", "F1", "F1", "integer", "1", "5");
    final Feature<?> f2 = Feature.getFeature("F2", "F2", "F2", "float", "2.000", "3.500", "0.200");
    final List<String> values = new ArrayList<String>();
    values.add("F3");
    values.add("lala");
    values.add("lele");
    values.add("lulu");
    final Feature<?> f3 = Feature.getFeature("F3", "F3", "F3", "string", values);
    final Feature<?> f4 = Feature.getFeature("F4", "F4", "F4", "integer", "4", "15", "3");
    final Feature<?> f5 = Feature.getFeature("F5", "F5", "F5", "float", "5.0000", "8.0000", "0.6543");
    final List<String> intFeats = new ArrayList<String>();
    intFeats.add(f1.getFeatureID());
    intFeats.add(f4.getFeatureID());
    final List<String> floatFeats = new ArrayList<String>();
    floatFeats.add(f2.getFeatureID());
    floatFeats.add(f5.getFeatureID());
    final List<String> textFeats = new ArrayList<String>();
    textFeats.add(f3.getFeatureID());
    final List<Feature<?>> allFeats = new ArrayList<Feature<?>>();
    allFeats.add(f1);
    allFeats.add(f2);
    allFeats.add(f3);
    allFeats.add(f4);
    allFeats.add(f5);
    final Features features = new Features(allFeats);
    // create purposes and purpose-lists
    final Purpose p1 = new Purpose("P1", "P1", "P1", true);
    final Purpose p2 = new Purpose("P2", "P2", "P2", true);
    final Purpose p111 = new Purpose("P111", "P111", "P111", false);
    final Purpose p112 = new Purpose("P112", "P112", "P112", false);
    final Purpose p113 = new Purpose("P113", "P113", "P113", false);
    final Purpose p221 = new Purpose("P221", "P221", "P221", false);
    final Purpose p222 = new Purpose("P222", "P222", "P222", false);
    final Purpose p223 = new Purpose("P223", "P223", "P223", false);
    final List<String> v11ps = new ArrayList<String>();
    v11ps.add(p111.getPurposeID());
    v11ps.add(p112.getPurposeID());
    v11ps.add(p113.getPurposeID());
    final List<String> v22ps = new ArrayList<String>();
    v22ps.add(p221.getPurposeID());
    v22ps.add(p222.getPurposeID());
    v22ps.add(p223.getPurposeID());
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
    final Variant v11 = new Variant("V11", "V11", "V11", intFeats);
    final Variant v12 = new Variant("V12", "V12", "V12", floatFeats);
    final Variant v13 = new Variant("V13", "V13", "V13", textFeats);
    final Variant v21 = new Variant("V21", "V21", "V21", intFeats);
    final Variant v22 = new Variant("V22", "V22", "V22", floatFeats);
    final Variant v23 = new Variant("V23", "V23", "V23", textFeats);
    final Variant v1121 = new Variant("V1121", "V1121", "V1121", intFeats);
    final Variant v1122 = new Variant("V1122", "V1122", "V1122", floatFeats);
    final Variant v1123 = new Variant("V1123", "V1123", "V1123", textFeats);
    final Variant v2231 = new Variant("V2231", "V2231", "V2231", intFeats);
    final Variant v2232 = new Variant("V2232", "V2232", "V2232", floatFeats);
    final Variant v2233 = new Variant("V2233", "V2233", "V2233", textFeats);
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
    alternatives.addFulfills(new Fulfills("p1vs", p1.getPurposeID(), p1vs));
    alternatives.addFulfills(new Fulfills("p2vs", p2.getPurposeID(), p2vs));
    alternatives.addFulfills(new Fulfills("p112vs", p112.getPurposeID(), p112vs));
    alternatives.addFulfills(new Fulfills("p223vs", p223.getPurposeID(), p223vs));
    // create constituents, i.e. links from variants to purposes
    final Constituents constituents = new Constituents();
    constituents.addConstitutes(new Constitutes("v11ps", v11.getVariantID(), v11ps));
    constituents.addConstitutes(new Constitutes("v22ps", v22.getVariantID(), v22ps));
    // keep it simple no need for events, rules or relations in a mock
    final Events events = new Events();
    final Rules rules = new Rules();
    final Relations relations = new Relations();
    final MetaData metadata = createMetaData();
    return new KnowledgeData(metadata, features, purposes, variants, alternatives, constituents, events, rules, relations);
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
