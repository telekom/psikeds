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
package org.psikeds.resolutionengine.interfaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import org.codehaus.jackson.JsonProcessingException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.util.JSONHelper;
import org.psikeds.resolutionengine.interfaces.pojos.Feature;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
import org.psikeds.resolutionengine.interfaces.pojos.VariantDecission;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;

/**
 * Tests for {@link org.psikeds.resolutionengine.interfaces.services.ResolutionService} and
 * corresponding POJOs. Focus of this Test is on the Interface Objects, Interaction with
 * ResolutionService and (Un-)Marshalling to and from JSON.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionServiceTest.class);

  private static String LOG4J;
  private static File TEST_DATA_DIR;

  private static File INIT_KNOWLEDGE;
  private static File VARIANT_DECISSION;
  private static File SELECT_VARIANT_KNOWLEDGE;
  private static File FEATURE_DECISSION;
  private static File SELECT_FEATURE_KNOWLEDGE;

  private static File INIT_RESPONSE;
  private static File CURRENT_RESPONSE;
  private static File SELECT_VARIANT_REQUEST;
  private static File SELECT_VARIANT_RESPONSE;
  private static File SELECT_FEATURE_REQUEST;
  private static File SELECT_FEATURE_RESPONSE;

  private ResolutionService srvc;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "../ResolutionEngine/src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
    TEST_DATA_DIR = new File(System.getProperty("org.psikeds.test.data.dir", "./src/test/resources/"));
    if (!TEST_DATA_DIR.exists()) {
      TEST_DATA_DIR.mkdir();
    }
    INIT_KNOWLEDGE = new File(TEST_DATA_DIR, "InitialKnowledge.json");
    VARIANT_DECISSION = new File(TEST_DATA_DIR, "VariantDecission.json");
    SELECT_VARIANT_KNOWLEDGE = new File(TEST_DATA_DIR, "SelectedVariantKnowledge.json");
    FEATURE_DECISSION = new File(TEST_DATA_DIR, "FeatureDecission.json");
    SELECT_FEATURE_KNOWLEDGE = new File(TEST_DATA_DIR, "SelectedFeatureKnowledge.json");
    INIT_RESPONSE = new File(TEST_DATA_DIR, "InitResponse.json");
    CURRENT_RESPONSE = new File(TEST_DATA_DIR, "CurrentResponse.json");
    SELECT_VARIANT_REQUEST = new File(TEST_DATA_DIR, "SelectVariantRequest.json");
    SELECT_VARIANT_RESPONSE = new File(TEST_DATA_DIR, "SelectVariantResponse.json");
    SELECT_FEATURE_REQUEST = new File(TEST_DATA_DIR, "SelectFeatureRequest.json");
    SELECT_FEATURE_RESPONSE = new File(TEST_DATA_DIR, "SelectFeatureResponse.json");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    // keep static knowledge but delete requests and responses after each run
    INIT_RESPONSE.deleteOnExit();
    CURRENT_RESPONSE.deleteOnExit();
    SELECT_VARIANT_REQUEST.deleteOnExit();
    SELECT_VARIANT_RESPONSE.deleteOnExit();
    SELECT_FEATURE_REQUEST.deleteOnExit();
    SELECT_FEATURE_RESPONSE.deleteOnExit();
  }

  @Before
  public void setUp() throws Exception {
    final Knowledge initialKnowledge = JSONHelper.readObjectFromJsonFile(INIT_KNOWLEDGE, Knowledge.class);
    final Knowledge selectVariantKnowledge = JSONHelper.readObjectFromJsonFile(SELECT_VARIANT_KNOWLEDGE, Knowledge.class);
    final Knowledge selectFeatureKnowledge = JSONHelper.readObjectFromJsonFile(SELECT_FEATURE_KNOWLEDGE, Knowledge.class);
    final Calendar now = Calendar.getInstance();
    final String started = DateFormat.getDateTimeInstance().format(now.getTime());
    final Metadata metadata = new Metadata();
    metadata.addInfo(Metadata.KB_CREATED, started);
    metadata.addInfo(Metadata.KB_LOADED, started);
    this.srvc = new ResolutionServiceMock(initialKnowledge, selectVariantKnowledge, selectFeatureKnowledge, metadata);
  }

  /**
   * Test method for {@link org.psikeds.resolutionengine.interfaces.services.ResolutionService}.
   * 
   * Note: All Request and Responses will be written and read again in order
   * to test Serialization and Deserialization.
   * 
   */
  @Test
  public void testResolutionService() throws Exception {
    try {
      LOGGER.info("Testing ResolutionService ...");
      assertNotNull("ResolutionService is null!", this.srvc);

      LOGGER.info("... getting initial Knowledge ...");
      ResolutionResponse ires = this.srvc.init();
      JSONHelper.writeObjectToJsonFile(INIT_RESPONSE, ires);
      ires = JSONHelper.readObjectFromJsonFile(INIT_RESPONSE, ResolutionResponse.class);

      assertNotNull("No initial Resolution-Response!", ires);
      assertFalse("Resolution-Response contains Errors!", ires.hasErrors());
      final String sessionID1 = ires.getSessionID();
      assertFalse("No initial SessionID!", StringUtils.isEmpty(sessionID1));
      final Knowledge ike = ires.getKnowledge();
      assertNotNull("No initial Knowledge!", ike);
      final VariantChoices choices1 = ike.getChoices();
      assertNotNull("No initial Variant-Choices!", choices1);
      int expected = 2;
      int size = choices1.size();
      assertEquals("Expected " + expected + " initial Variant-Choices but got " + size, expected, size);
      assertFalse("Initial Knowledge is already resolved!?!?", ires.isResolved());
      final Metadata metadata = ires.getMetadata();
      assertNotNull("No initial Metadata!", metadata);

      LOGGER.info("... getting current Knowledge ...");
      ResolutionResponse cres1 = this.srvc.current(sessionID1);
      JSONHelper.writeObjectToJsonFile(CURRENT_RESPONSE, cres1);
      cres1 = JSONHelper.readObjectFromJsonFile(CURRENT_RESPONSE, ResolutionResponse.class);

      assertNotNull("No Response for current Knowledge!", cres1);
      assertFalse("Resolution-Response contains Errors!", cres1.hasErrors());
      final String sessionID2 = cres1.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID2));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID2);
      final Knowledge c1ke = cres1.getKnowledge();
      assertNotNull("No current Knowledge!", c1ke);

      LOGGER.info("... making a Variant-Decission and asking for Resolution ...");
      final VariantDecission vd = JSONHelper.readObjectFromJsonFile(VARIANT_DECISSION, VariantDecission.class);
      ResolutionRequest svreq = new ResolutionRequest(sessionID1, metadata, vd);
      JSONHelper.writeObjectToJsonFile(SELECT_VARIANT_REQUEST, svreq);
      svreq = JSONHelper.readObjectFromJsonFile(SELECT_VARIANT_REQUEST, ResolutionRequest.class);
      ResolutionResponse svres = this.srvc.select(svreq);
      JSONHelper.writeObjectToJsonFile(SELECT_VARIANT_RESPONSE, svres);
      svres = JSONHelper.readObjectFromJsonFile(SELECT_VARIANT_RESPONSE, ResolutionResponse.class);

      assertNotNull("No Resolution-Response!", svres);
      assertFalse("Resolution-Response contains Errors!", svres.hasErrors());
      final String sessionID3 = svres.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID3));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID3);
      final Knowledge svke = svres.getKnowledge();
      assertNotNull("No Knowledge in Resolution-Response!", svke);
      final VariantChoices choices2 = svke.getChoices();
      assertNotNull("No Variant-Choices after Decission!", choices2);
      expected = 1;
      size = choices2.size();
      assertEquals("Expected " + expected + " Variant-Choices after Decission but got " + size, expected, size);
      assertFalse("Knowledge is already resolved!?!?", svres.isResolved());

      LOGGER.info("... making a Feature-Decission and asking for Resolution ...");
      final FeatureDecission fd = JSONHelper.readObjectFromJsonFile(FEATURE_DECISSION, FeatureDecission.class);
      ResolutionRequest sfreq = new ResolutionRequest(sessionID1, metadata, fd);
      JSONHelper.writeObjectToJsonFile(SELECT_FEATURE_REQUEST, sfreq);
      sfreq = JSONHelper.readObjectFromJsonFile(SELECT_FEATURE_REQUEST, ResolutionRequest.class);
      ResolutionResponse sfres = this.srvc.select(sfreq);
      JSONHelper.writeObjectToJsonFile(SELECT_FEATURE_RESPONSE, sfres);
      sfres = JSONHelper.readObjectFromJsonFile(SELECT_FEATURE_RESPONSE, ResolutionResponse.class);

      assertNotNull("No Resolution-Response!", sfres);
      assertFalse("Resolution-Response contains Errors!", sfres.hasErrors());
      final String sessionID4 = sfres.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID4));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID4);
      final Knowledge sfke = sfres.getKnowledge();
      assertNotNull("No Knowledge in Resolution-Response!", sfke);

      LOGGER.info("... checking current Knowledge again ...");
      ResolutionResponse cres2 = this.srvc.current(sessionID1);
      JSONHelper.writeObjectToJsonFile(CURRENT_RESPONSE, cres2);
      cres2 = JSONHelper.readObjectFromJsonFile(CURRENT_RESPONSE, ResolutionResponse.class);

      assertNotNull("No Response for current Knowledge!", cres2);
      assertFalse("Resolution-Response contains Errors!", cres2.hasErrors());
      final String sessionID5 = cres2.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID5));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID5);
      final Knowledge c2ke = cres2.getKnowledge();
      assertNotNull("No current Knowledge!", c2ke);
      final VariantChoices choices3 = c2ke.getChoices();
      assertNotNull("No current Choices!", choices3);
      assertFalse("No current Choices!", choices3.isEmpty());
      assertFalse("Current Knowledge is already resolved!?!?", cres2.isResolved());

      // QQQ additional tests for concepts and features

      LOGGER.info("... done. Handling of POJOs for Resolution worked as expected.");
    }
    catch (final Exception ex) {
      LOGGER.error("Test failed!", ex);
      throw ex;
    }
  }

  private static void generateTestData(final boolean force) throws JsonProcessingException, IOException {
    LOGGER.info("Start generating Test-Data ... ");

    final Feature f1 = new Feature("F1", Feature.VALUE_TYPE_INTEGER);
    final Feature f2 = new Feature("F2", Feature.VALUE_TYPE_FLOAT);
    final Feature f3 = new Feature("F3", Feature.VALUE_TYPE_STRING);

    final FeatureValue fv11 = new FeatureValue(f1.getFeatureID(), "FV11", "42");
    final FeatureValue fv21 = new FeatureValue(f2.getFeatureID(), "FV21", "1.0");
    final FeatureValue fv22 = new FeatureValue(f2.getFeatureID(), "FV22", "2.0");
    final FeatureValue fv23 = new FeatureValue(f2.getFeatureID(), "FV23", "3.0");
//    final FeatureValue fv31 = new FeatureValue(f3.getFeatureID(), "FV31", "foo");
//    final FeatureValue fv32 = new FeatureValue(f3.getFeatureID(), "FV32", "bar");

    final Purpose p1 = new Purpose("P1", true);
    final Purpose p2 = new Purpose("P2", true);
    final Purpose p3 = new Purpose("P3", true);

    final Variant v11 = new Variant("V11");
    v11.addFeature(f1);
    final Variant v21 = new Variant("V21");
    final Variant v22 = new Variant("V22");
    v22.addFeature(f2);
    final Variant v23 = new Variant("V23");
    final Variant v31 = new Variant("V31");
    final Variant v32 = new Variant("V32");
    final Variant v33 = new Variant("V33");
    v33.addFeature(f3);

    final KnowledgeEntity ke1 = new KnowledgeEntity(p1, v11);
    ke1.addFeature(fv11);

    final Knowledge initialKnowledge = new Knowledge();
    initialKnowledge.addKnowledgeEntity(ke1);

    final VariantChoice vc1 = new VariantChoice(p2);
    vc1.addVariant(v21);
    vc1.addVariant(v22);
    vc1.addVariant(v23);
    initialKnowledge.addChoice(vc1);

    final VariantChoice vc2 = new VariantChoice(p3);
    vc2.addVariant(v31);
    vc2.addVariant(v32);
    vc2.addVariant(v33);
    initialKnowledge.addChoice(vc2);

    LOGGER.info("... created initial Knowledge:\n{}", initialKnowledge);
    if (force || !INIT_KNOWLEDGE.exists()) {
      JSONHelper.writeObjectToJsonFile(INIT_KNOWLEDGE, initialKnowledge);
    }

    final VariantDecission vd = new VariantDecission(p2, v22);
    LOGGER.info("... created Variant-Decission:\n{}", vd);
    if (force || !VARIANT_DECISSION.exists()) {
      JSONHelper.writeObjectToJsonFile(VARIANT_DECISSION, vd);
    }

    final FeatureChoice fc2 = new FeatureChoice(v22, f2);
    fc2.addPossibleValue(fv21);
    fc2.addPossibleValue(fv22);
    fc2.addPossibleValue(fv23);

    final KnowledgeEntity ke2 = new KnowledgeEntity(p2, v22);
    ke2.addPossibleFeature(fc2);

    final Knowledge selectVariantKnowledge = new Knowledge();
    selectVariantKnowledge.addKnowledgeEntity(ke1);
    selectVariantKnowledge.addKnowledgeEntity(ke2);
    selectVariantKnowledge.addChoice(vc2);

    LOGGER.info("... created Selected-Variant-Knowledge:\n{}", selectVariantKnowledge);
    if (force || !SELECT_VARIANT_KNOWLEDGE.exists()) {
      JSONHelper.writeObjectToJsonFile(SELECT_VARIANT_KNOWLEDGE, selectVariantKnowledge);
    }

    final FeatureDecission fd = new FeatureDecission(fc2, 1);
    LOGGER.info("... created Feature-Decission:\n{}", fd);
    if (force || !FEATURE_DECISSION.exists()) {
      JSONHelper.writeObjectToJsonFile(FEATURE_DECISSION, fd);
    }

    final Knowledge selectFeatureKnowledge = new Knowledge();
    selectFeatureKnowledge.addKnowledgeEntity(ke1);
    final FeatureValue fv = fc2.matches(fd);
    ke2.clearPossibleFeatures();
    ke2.addFeature(fv);
    selectFeatureKnowledge.addKnowledgeEntity(ke2);
    selectFeatureKnowledge.addChoice(vc2);

    LOGGER.info("... created Selected-Feature-Knowledge:\n{}", selectFeatureKnowledge);
    if (force || !SELECT_FEATURE_KNOWLEDGE.exists()) {
      JSONHelper.writeObjectToJsonFile(SELECT_FEATURE_KNOWLEDGE, selectFeatureKnowledge);
    }

    // QQQ additional data for concepts and relations

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
