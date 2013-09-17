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
package org.psikeds.queryagent.interfaces.presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.queryagent.interfaces.presenter.pojos.Alternatives;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constituents;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constitutes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Events;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.Fulfills;
import org.psikeds.queryagent.interfaces.presenter.pojos.InitResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purposes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Rules;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;

/**
 * Tests for {@link org.psikeds.queryagent.interfaces.presenter.services.ResolutionService}.
 *
 * @author marco@juliano.de
 *
 */
public class ResolutionServiceTest {

  private static final String LOG4J = "../QueryAgent/src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionServiceTest.class);

  private static final String TEST_DATA_DIR = "./src/test/resources/";
  private static final File KNOWLEDGE = new File(TEST_DATA_DIR, "KnowledgeEntity.json");
  private static final File INIT_RESPONSE = new File(TEST_DATA_DIR, "InitResponse.json");
  private static final File SELECT_RESPONSE = new File(TEST_DATA_DIR, "SelectResponse.json");
  private static final File CHOICE = new File(TEST_DATA_DIR, "Choice.json");

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ResolutionService srvc;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    final InitResponse initResp = readObjectFromJsonFile(INIT_RESPONSE, InitResponse.class);
    final SelectResponse selResp = readObjectFromJsonFile(SELECT_RESPONSE, SelectResponse.class);
    final KnowledgeEntity ke = readObjectFromJsonFile(KNOWLEDGE, KnowledgeEntity.class);
    this.srvc = new ResolutionServiceMock(initResp, selResp, ke);
  }

  /**
   * Test method for {@link org.psikeds.queryagent.interfaces.presenter.services.ResolutionService}.
   */
  @Test
  public void testResolutionService() throws Exception {
    assertTrue("ResolutionService is null!", this.srvc != null);

    final InitResponse ires = this.srvc.init();
    LOGGER.debug("Received:\n{}", ires);
    assertTrue("No InitResponse!", ires != null);

    final String sessionID1 = ires.getSessionID();
    assertTrue("No initial SessionID!", sessionID1 != null);
    final KnowledgeEntity ke1 = ires.getKnowledgeEntity();
    assertTrue("No initial KnowledgeEntity!", ke1 != null);

    final String choice = readObjectFromJsonFile(CHOICE, String.class);
    final SelectRequest sreq = new SelectRequest(sessionID1, ke1, choice);
    LOGGER.debug("Sending:\n{}", sreq);

    final SelectResponse sres = this.srvc.select(sreq);
    LOGGER.debug("Received:\n{}", sres);
    assertTrue("No SelectResponse!", sres != null);

    final String sessionID2 = sres.getSessionID();
    assertTrue("No SessionID in SelectResponse!", sessionID2 != null);
    final KnowledgeEntity ke2 = sres.getKnowledgeEntity();
    assertTrue("No KnowledgeEntity in SelectResponse!", ke2 != null);
    assertEquals("SessionIDs are not matching!", sessionID1, sessionID2);

    // TODO: additional tests
  }

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

    final Features features = null;
    final Purposes purposes = new Purposes();
    purposes.addPurpose(new Purpose("P1", "", "P1"));
    purposes.addPurpose(new Purpose("P2", "", "P2"));
    purposes.addPurpose(new Purpose("P3", "", "P3"));
    final Variants variants = new Variants();
    variants.addVariant(new Variant("V1", "", "V1"));
    variants.addVariant(new Variant("V2", "", "V2"));
    variants.addVariant(new Variant("V3", "", "V3"));
    final Alternatives alternatives = new Alternatives();
    alternatives.addFulfills(new Fulfills("", "P1", "V1"));
    alternatives.addFulfills(new Fulfills("", "P2", "V2"));
    alternatives.addFulfills(new Fulfills("", "P3", "V3"));
    final Constituents constituents = new Constituents();
    constituents.addConstitutes(new Constitutes("", "V1", "P1"));
    constituents.addConstitutes(new Constitutes("", "V2", "P2"));
    constituents.addConstitutes(new Constitutes("", "V3", "P3"));
    final Events events = null;
    final Rules rules = null;
    final boolean fullyResolved = false;
    final KnowledgeEntity ke = new KnowledgeEntity(features, purposes, variants, alternatives, constituents, events, rules, fullyResolved);
    if (force || !KNOWLEDGE.exists()) {
      writeObjectToJsonFile(KNOWLEDGE, ke);
    }

    // Generate a new sessionID with each request
    final String sessionID = null;

    final InitResponse ires = new InitResponse(sessionID, ke);
    if (force || !INIT_RESPONSE.exists()) {
      writeObjectToJsonFile(INIT_RESPONSE, ires);
    }

    final SelectResponse sres = new SelectResponse(sessionID, ke);
    if (force || !SELECT_RESPONSE.exists()) {
      writeObjectToJsonFile(SELECT_RESPONSE, sres);
    }

    final String choice = "P2";
    if (force || !CHOICE.exists()) {
      writeObjectToJsonFile(CHOICE, choice);
    }

    LOGGER.info("... finished generating Test-Data. ");
  }

  public static void main(final String[] args) {
    try {
      setUpBeforeClass();
      final boolean force = args.length > 0 && "force".equalsIgnoreCase(args[0]);
      generateTestData(force);
    }
    catch (final Exception ex) {
      LOGGER.error("Could not generate Test-Data!", ex);
    }
  }
}
