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
import static org.junit.Assert.assertNotNull;
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

import org.psikeds.queryagent.interfaces.presenter.pojos.Choice;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
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
  private static final File INIT_KNOWLEDGE = new File(TEST_DATA_DIR, "InitKnowledge.json");
  private static final File SELECT_KNOWLEDGE = new File(TEST_DATA_DIR, "SelectKnowledge.json");
  private static final File DECISSION = new File(TEST_DATA_DIR, "Decission.json");

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ResolutionService srvc;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    final Knowledge initKnow = readObjectFromJsonFile(INIT_KNOWLEDGE, Knowledge.class);
    final Knowledge selectKnow = readObjectFromJsonFile(SELECT_KNOWLEDGE, Knowledge.class);
    this.srvc = new ResolutionServiceMock(initKnow, selectKnow);
  }

  /**
   * Test method for {@link org.psikeds.queryagent.interfaces.presenter.services.ResolutionService}.
   */
  @Test
  public void testResolutionService() throws Exception {
    LOGGER.info("Testing ResolutionService ...");
    assertNotNull("ResolutionService is null!", this.srvc);

    LOGGER.info("... getting initial Knowledge ...");
    final ResolutionResponse ires = this.srvc.init();
    LOGGER.trace("Received:\n{}", ires);

    assertNotNull("No initial Resolution-Response!", ires);
    final String sessionID1 = ires.getSessionID();
    assertTrue("No initial SessionID!", (sessionID1 != null) && (sessionID1.length() > 0));
    final Knowledge ike = ires.getKnowledge();
    assertNotNull("No initial Knowledge!", ike);

    LOGGER.info("... making a Decission and asking for Resolution ...");
    final Decission decission = readObjectFromJsonFile(DECISSION, Decission.class);
    final ResolutionRequest sreq = new ResolutionRequest(sessionID1, decission);
    LOGGER.trace("Sending:\n{}", sreq);
    final ResolutionResponse sres = this.srvc.select(sreq);
    LOGGER.trace("Received:\n{}", sres);

    assertNotNull("No Resolution-Response!", sres);
    final String sessionID2 = sres.getSessionID();
    assertTrue("No SessionID in Resolution-Response!", (sessionID2 != null) && (sessionID2.length() > 0));
    final Knowledge ske = sres.getKnowledge();
    assertNotNull("No Knowledge in Resolution-Response!", ske);
    assertEquals("SessionIDs are not matching!", sessionID1, sessionID2);

    LOGGER.info("... done. Resolution worked as expected.");
  }

  private static <T> T readObjectFromJsonFile(final File f, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if ((type != null) && (f != null) && f.isFile() && f.exists() && f.canRead()) {
      obj = MAPPER.readValue(f, type);
      LOGGER.trace("Read Object from File {}\n{}", f, obj);
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

  private static void generateTestData(final boolean force) throws JsonProcessingException, IOException {
    LOGGER.info("Start generating Test-Data ... ");

    final Knowledge initKnow = new Knowledge();

    final Purpose p1 = new Purpose("P1", null, "P1", true);
    final Purpose p2 = new Purpose("P2", null, "P2", true);
    final Purpose p3 = new Purpose("P3", null, "P3", true);

    final Variant v11 = new Variant("V11", null, "V11");
    final Variant v21 = new Variant("V21", null, "V21");
    final Variant v22 = new Variant("V22", null, "V22");
    final Variant v23 = new Variant("V23", null, "V23");
    final Variant v31 = new Variant("V31", null, "V31");
    final Variant v32 = new Variant("V32", null, "V32");
    final Variant v33 = new Variant("V33", null, "V33");

    final KnowledgeEntity ke1 = new KnowledgeEntity(p1, v11);
    initKnow.addKnowledgeEntity(ke1);

    final Choice c2 = new Choice(p2);
    c2.addVariant(v21);
    c2.addVariant(v22);
    c2.addVariant(v23);
    initKnow.addChoice(c2);

    final Choice c3 = new Choice(p3);
    c2.addVariant(v31);
    c2.addVariant(v32);
    c2.addVariant(v33);
    initKnow.addChoice(c3);

    if (force || !INIT_KNOWLEDGE.exists()) {
      writeObjectToJsonFile(INIT_KNOWLEDGE, initKnow);
    }

    final Decission decission = new Decission(p2, v22);
    if (force || !DECISSION.exists()) {
      writeObjectToJsonFile(DECISSION, decission);
    }

    final Knowledge selectKnow = new Knowledge();

    selectKnow.addKnowledgeEntity(ke1);
    final KnowledgeEntity ke2 = new KnowledgeEntity(p2, v22);
    selectKnow.addKnowledgeEntity(ke2);

    selectKnow.addChoice(c3);

    if (force || !SELECT_KNOWLEDGE.exists()) {
      writeObjectToJsonFile(SELECT_KNOWLEDGE, selectKnow);
    }

    LOGGER.info("... finished generating Test-Data. ");
  }

  public static void main(final String[] args) {
    try {
      setUpBeforeClass();
      final boolean force = (args.length > 0) && "force".equalsIgnoreCase(args[0]);
      generateTestData(force);
    }
    catch (final Exception ex) {
      LOGGER.error("Could not generate Test-Data!", ex);
    }
  }
}
