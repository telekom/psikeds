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

import static org.junit.Assert.assertTrue;

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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Data;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValueType;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.Knowledgebase;
import org.psikeds.resolutionengine.datalayer.vo.Meta;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * Mock-Implementation of KnowledgeBase for testing purposes.
 * 
 * @author marco@juliano.de
 * 
 */
public class KnowledgeBaseMock implements KnowledgeBase {

  private static final String LOG4J = "../ResolutionEngine/src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseMock.class);

  private static final String TEST_DATA_DIR = "./src/test/resources/";
  private static final File KNOWLEDGEBASE = new File(TEST_DATA_DIR, "KnowledgeBase.json");

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private Knowledgebase kb;

  public KnowledgeBaseMock() {
    this(null);
  }

  public KnowledgeBaseMock(final Knowledgebase kb) {
    this.kb = kb;
  }

  // ----------------------------------------------------------------

  /**
   * @return Features
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures()
   */
  @Override
  public Features getFeatures() {
    return getData().getFeatures();
  }

  /**
   * @return Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurposes()
   */
  @Override
  public Purposes getPurposes() {
    return getData().getPurposes();
  }

  /**
   * @return Variants
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariants()
   */
  @Override
  public Variants getVariants() {
    return getData().getVariants();
  }

  /**
   * @return Alternatives
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getAlternatives()
   */
  @Override
  public Alternatives getAlternatives() {
    return getData().getAlternatives();
  }

  /**
   * @return Constituents
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstituents()
   */
  @Override
  public Constituents getConstituents() {
    return getData().getConstituents();
  }

  /**
   * @return Events
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvents()
   */
  @Override
  public Events getEvents() {
    return getData().getEvents();
  }

  /**
   * @return Rules
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRules()
   */
  @Override
  public Rules getRules() {
    return getData().getRules();
  }

  /**
   * @param featureId
   * @return Feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature getFeature(final String featureId) {
    for (final Feature f : getFeatures().getFeature()) {
      if ((f != null) && featureId.equals(f.getId())) {
        return f;
      }
    }
    return new Feature(featureId, featureId, featureId, featureId, null);
  }

  /**
   * @param purposeId
   * @return Purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurpose(java.lang.String)
   */
  @Override
  public Purpose getPurpose(final String purposeId) {
    for (final Purpose p : getPurposes().getPurpose()) {
      if ((p != null) && purposeId.equals(p.getId())) {
        return p;
      }
    }
    return new Purpose(purposeId, purposeId, purposeId);
  }

  /**
   * @param variantId
   * @return Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariant(java.lang.String)
   */
  @Override
  public Variant getVariant(final String variantId) {
    for (final Variant v : getVariants().getVariant()) {
      if ((v != null) && variantId.equals(v.getId())) {
        return v;
      }
    }
    return new Variant(variantId, variantId, variantId);
  }

  /**
   * @param eventId
   * @return Event
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvent(java.lang.String)
   */
  @Override
  public Event getEvent(final String eventId) {
    for (final Event e : getEvents().getEvent()) {
      if ((e != null) && eventId.equals(e.getId())) {
        return e;
      }
    }
    return new Event(eventId, eventId, eventId, null, null);
  }

  /**
   * @param ruleId
   * @return Rule
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRule(java.lang.String)
   */
  @Override
  public Rule getRule(final String ruleId) {
    for (final Rule r : getRules().getRule()) {
      if ((r != null) && ruleId.equals(r.getId())) {
        return r;
      }
    }
    return new Rule(ruleId, ruleId, ruleId, null, null, null, null);
  }

  /**
   * @param purposeId
   * @return Fulfills
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfills(java.lang.String)
   */
  @Override
  public Fulfills getFulfills(final String purposeId) {
    for (final Fulfills f : getAlternatives().getFulfills()) {
      if ((f != null) && purposeId.equals(f.getPurposeID())) {
        return f;
      }
    }
    return new Fulfills(purposeId, purposeId, null);
  }

  /**
   * @param variantId
   * @return Constitutes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutes(java.lang.String)
   */
  @Override
  public Constitutes getConstitutes(final String variantId) {
    for (final Constitutes c : getConstituents().getConstitutes()) {
      if ((c != null) && variantId.equals(c.getVariantID())) {
        return c;
      }
    }
    return new Constitutes(variantId, variantId, (String) null);
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
    final Fulfills f = getFulfills(purposeId);
    for (final String variantId : f.getVariantID()) {
      final Variant v = getVariant(variantId);
      vars.addVariant(v);
    }
    return vars;
  }

  /**
   * @param purpose
   * @return Variants
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFulfillingVariants(org.psikeds.resolutionengine.datalayer.vo.Purpose)
   */
  @Override
  public Variants getFulfillingVariants(final Purpose purpose) {
    return getFulfillingVariants(purpose.getId());
  }

  /**
   * @param variantId
   * @return Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutingPurposes(java.lang.String)
   */
  @Override
  public Purposes getConstitutingPurposes(final String variantId) {
    final Purposes purps = new Purposes();
    final Constitutes c = getConstitutes(variantId);
    for (final String purposeId : c.getPurposeID()) {
      final Purpose p = getPurpose(purposeId);
      purps.addPurpose(p);
    }
    return purps;
  }

  /**
   * @param variant
   * @return Purposes
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getConstitutingPurposes(org.psikeds.resolutionengine.datalayer.vo.Variant)
   */
  @Override
  public Purposes getConstitutingPurposes(final Variant variant) {
    return getConstitutingPurposes(variant.getId());
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
    for (final String featureId : v.getFeatureIds()) {
      final Feature f = getFeature(featureId);
      feats.addFeature(f);
    }
    return feats;
  }

  /**
   * @param variant
   * @return Features
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeatures(org.psikeds.resolutionengine.datalayer.vo.Variant)
   */
  @Override
  public Features getFeatures(final Variant variant) {
    return getFeatures(variant.getId());
  }

  /**
   * @return boolean
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#isValid()
   */
  @Override
  public boolean isValid() {
    return (getData() != null) && (getMetadata() != null);
  }

  /**
   * @return Meta
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getMetadata()
   */
  @Override
  public Meta getMetadata() {
    return getKnowledgebase().getMeta();
  }

  // ----------------------------------------------------------------

  private Data getData() {
    return getKnowledgebase().getData();
  }

  private synchronized Knowledgebase getKnowledgebase() {
    if (this.kb == null) {
      try {
        // Load Knowledgebase from JSON
        this.kb = readObjectFromJsonFile(KNOWLEDGEBASE, Knowledgebase.class);
      }
      catch (final Exception ex) {
        LOGGER.error("Could not load Knowledgebase " + KNOWLEDGEBASE.getPath(), ex);
        this.kb = null;
      }
      if (this.kb == null) {
        // Fallback: empty Knowledgebase
        this.kb = new Knowledgebase();
      }
    }
    return this.kb;
  }

  // ----------------------------------------------------------------

  private static <T> T readObjectFromJsonFile(final File f, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if ((type != null) && (f != null) && f.isFile() && f.exists() && f.canRead()) {
      obj = MAPPER.readValue(f, type);
      LOGGER.debug("Read Object from File {}\n{}", f, obj);
    }
    return obj;
  }

  private static void writeObjectToJsonFile(final File f, final Object obj) throws JsonProcessingException, IOException {
    if ((f != null) && (obj != null)) {
      LOGGER.info("Writing Object to File {}\n{}", f, obj);
      MAPPER.writeValue(f, obj);
      assertTrue("Could not write Object(s) to File " + f.getPath(), f.exists());
    }
  }

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

  private static Meta createMeta() {
    final Calendar created = Calendar.getInstance();
    final Calendar lastmodified = created;
    final String now = DateFormat.getDateTimeInstance().format(created.getTime());
    final String user = getCurrentUser();
    final String host = getHostName();
    final String lang = getLanguage();
    final List<String> creator = new ArrayList<String>();
    creator.add(user);
    final List<String> description = new ArrayList<String>();
    description.add("Mock Knowledgebase for Testing Purposes");
    description.add("Created by " + user + " on " + host + " at " + now);
    final Map<String, Object> optionalInfo = new ConcurrentHashMap<String, Object>();
    optionalInfo.put("language", lang);
    optionalInfo.put("creator", user);
    return new Meta(created, lastmodified, creator, description, optionalInfo);
  }

  // ----------------------------------------------------------------

  private static Data createData() {

    final Feature f1 = new Feature("F1", "F1", "F1", "1", FeatureValueType.INTEGER);
    final Feature f2 = new Feature("F2", "F2", "F2", "2.0", FeatureValueType.FLOAT);
    final Feature f3 = new Feature("F3", "F3", "F3", "3,0", FeatureValueType.STRING);
    final Feature f4 = new Feature("F4", "F4", "F4", "4", FeatureValueType.INTEGER);
    final List<String> intFeats = new ArrayList<String>();
    intFeats.add(f1.getId());
    intFeats.add(f4.getId());
    final List<String> floatFeats = new ArrayList<String>();
    floatFeats.add(f2.getId());
    final List<String> textFeats = new ArrayList<String>();
    textFeats.add(f3.getId());
    final List<Feature> allFeats = new ArrayList<Feature>();
    allFeats.add(f1);
    allFeats.add(f2);
    allFeats.add(f3);
    allFeats.add(f4);
    final Features features = new Features(allFeats);

    final Purpose p1 = new Purpose("P1", "P1", "P1", true);
    final Purpose p2 = new Purpose("P2", "P2", "P2", true);
    final Purpose p111 = new Purpose("P111", "P111", "P111", false);
    final Purpose p112 = new Purpose("P112", "P112", "P112", false);
    final Purpose p113 = new Purpose("P113", "P113", "P113", false);
    final Purpose p221 = new Purpose("P221", "P221", "P221", false);
    final Purpose p222 = new Purpose("P222", "P222", "P222", false);
    final Purpose p223 = new Purpose("P223", "P223", "P223", false);
    final List<String> v11ps = new ArrayList<String>();
    v11ps.add(p111.getId());
    v11ps.add(p112.getId());
    v11ps.add(p113.getId());
    final List<String> v22ps = new ArrayList<String>();
    v22ps.add(p221.getId());
    v22ps.add(p222.getId());
    v22ps.add(p223.getId());
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
    p1vs.add(v11.getId());
    p1vs.add(v12.getId());
    p1vs.add(v13.getId());
    final List<String> p2vs = new ArrayList<String>();
    p2vs.add(v21.getId());
    p2vs.add(v22.getId());
    p2vs.add(v23.getId());
    final List<String> p112vs = new ArrayList<String>();
    p112vs.add(v1121.getId());
    p112vs.add(v1122.getId());
    p112vs.add(v1123.getId());
    final List<String> p223vs = new ArrayList<String>();
    p223vs.add(v2231.getId());
    p223vs.add(v2232.getId());
    p223vs.add(v2233.getId());
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

    final Alternatives alternatives = new Alternatives();
    alternatives.addFulfills(new Fulfills("p1vs", p1.getId(), p1vs));
    alternatives.addFulfills(new Fulfills("p2vs", p2.getId(), p2vs));
    alternatives.addFulfills(new Fulfills("p112vs", p112.getId(), p112vs));
    alternatives.addFulfills(new Fulfills("p223vs", p223.getId(), p223vs));

    final Constituents constituents = new Constituents();
    constituents.addConstitutes(new Constitutes("v11ps", v11.getId(), v11ps));
    constituents.addConstitutes(new Constitutes("v22ps", v22.getId(), v22ps));

    final Events events = new Events();
    final Rules rules = new Rules();

    return new Data(features, purposes, variants, alternatives, constituents, events, rules);
  }

  // ----------------------------------------------------------------

  private static void generateTestData(final boolean force) throws JsonProcessingException, IOException {
    LOGGER.info("Start generating Test-Data ... ");
    final Meta m = createMeta();
    final Data d = createData();
    final Knowledgebase kb = new Knowledgebase(m, d);
    if (force || !KNOWLEDGEBASE.exists()) {
      writeObjectToJsonFile(KNOWLEDGEBASE, kb);
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
