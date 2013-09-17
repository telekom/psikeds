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
package org.psikeds.resolutionengine.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
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

  private Data kbdata = null;

  // ---------------------------------------------------

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
   * @param id
   * @return Feature
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getFeature(java.lang.String)
   */
  @Override
  public Feature getFeature(final String id) {
    return new Feature(id, "", id);
  }

  /**
   * @param id
   * @return Purpose
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getPurpose(java.lang.String)
   */
  @Override
  public Purpose getPurpose(final String id) {
    return new Purpose(id, "", id);
  }

  /**
   * @param id
   * @return Variant
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getVariant(java.lang.String)
   */
  @Override
  public Variant getVariant(final String id) {
    return new Variant(id, "", id);
  }

  /**
   * @param id
   * @return Event
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getEvent(java.lang.String)
   */
  @Override
  public Event getEvent(final String id) {
    return new Event(id, "", id);
  }

  /**
   * @param id
   * @return Rule
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase#getRule(java.lang.String)
   */
  @Override
  public Rule getRule(final String id) {
    return new Rule(id, "", id);
  }

  private synchronized Data getData() {
    if (this.kbdata == null) {
      try {
        this.kbdata = readObjectFromJsonFile(KNOWLEDGEBASE, Data.class);
      }
      catch (final Exception ex) {
        LOGGER.error("Could not load KnowledgeBase " + KNOWLEDGEBASE.getPath(), ex);
        this.kbdata = null;
      }
      if (this.kbdata == null) {
        this.kbdata = new Data();
      }
    }
    return this.kbdata;
  }

  // ---------------------------------------------------

  private static <T> T readObjectFromJsonFile(final File f, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if (type != null && f != null && f.isFile() && f.exists() && f.canRead()) {
      obj = MAPPER.readValue(f, type);
      LOGGER.debug("Read Object from File {}\n{}", f, obj);
    }
    return obj;
  }

  private static void writeObjectToJsonFile(final File f, final Object obj) throws JsonProcessingException, IOException {
    if (f != null && obj != null) {
      LOGGER.info("Writing Object to File {}\n{}", f, obj);
      MAPPER.writeValue(f, obj);
      assertTrue("Could not write Object(s) to File " + f.getPath(), f.exists());
    }
  }

  private static void generateTestData(final boolean force) throws JsonProcessingException, IOException {
    LOGGER.info("Start generating Test-Data ... ");

    final Purposes p = new Purposes();
    p.addPurpose(new Purpose("P1", "", "P1"));
    p.addPurpose(new Purpose("P2", "", "P2"));
    p.addPurpose(new Purpose("P3", "", "P3"));

    final Variants v = new Variants();
    v.addVariant(new Variant("V1", "", "V1"));
    v.addVariant(new Variant("V2", "", "V2"));
    v.addVariant(new Variant("V3", "", "V3"));

    final Alternatives a = new Alternatives();
    a.addFulfills(new Fulfills("", "P1", "V1"));
    a.addFulfills(new Fulfills("", "P2", "V2"));
    a.addFulfills(new Fulfills("", "P3", "V3"));

    final Constituents c = new Constituents();
    c.addConstitutes(new Constitutes("", "V1", "P1"));
    c.addConstitutes(new Constitutes("", "V2", "P2"));
    c.addConstitutes(new Constitutes("", "V3", "P3"));

    final Events e = null;
    final Features f = null;
    final Rules r = null;

    final Data d = new Data(f, p, v, a, c, e, r);
    if (force || !KNOWLEDGEBASE.exists()) {
      writeObjectToJsonFile(KNOWLEDGEBASE, d);
    }

    LOGGER.info("... finished generating Test-Data. ");
  }

  private static void setUpLogging() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  public static void main(final String[] args) {
    try {
      setUpLogging();
      final boolean force = args.length > 0 && "force".equalsIgnoreCase(args[0]);
      generateTestData(force);
    }
    catch (final Exception ex) {
      LOGGER.error("Could not generate Test-Data!", ex);
    }
  }
}
