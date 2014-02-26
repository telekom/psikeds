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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureDescription;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
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

  private static final String LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "../ResolutionEngine/src/main/resources/log4j.xml");
  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionServiceTest.class);

  private static final String TEST_DATA_DIR = System.getProperty("org.psikeds.test.data.dir", "./src/test/resources/");

  private static final File INIT_KNOWLEDGE = new File(TEST_DATA_DIR, "InitialKnowledge.json");
  private static final File VARIANT_DECISSION = new File(TEST_DATA_DIR, "VariantDecission.json");
  private static final File SELECT_VARIANT_KNOWLEDGE = new File(TEST_DATA_DIR, "SelectedVariantKnowledge.json");
  private static final File FEATURE_DECISSION = new File(TEST_DATA_DIR, "FeatureDecission.json");
  private static final File SELECT_FEATURE_KNOWLEDGE = new File(TEST_DATA_DIR, "SelectedFeatureKnowledge.json");

  private static final File INIT_RESPONSE = new File(TEST_DATA_DIR, "InitResponse.json");
  private static final File CURRENT_RESPONSE = new File(TEST_DATA_DIR, "CurrentResponse.json");
  private static final File SELECT_VARIANT_REQUEST = new File(TEST_DATA_DIR, "SelectVariantRequest.json");
  private static final File SELECT_VARIANT_RESPONSE = new File(TEST_DATA_DIR, "SelectVariantResponse.json");
  private static final File SELECT_FEATURE_REQUEST = new File(TEST_DATA_DIR, "SelectFeatureRequest.json");
  private static final File SELECT_FEATURE_RESPONSE = new File(TEST_DATA_DIR, "SelectFeatureResponse.json");

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private ResolutionService srvc = null;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    final Knowledge initialKnowledge = readObjectFromJsonFile(INIT_KNOWLEDGE, Knowledge.class);
    final Knowledge selectVariantKnowledge = readObjectFromJsonFile(SELECT_VARIANT_KNOWLEDGE, Knowledge.class);
    final Knowledge selectFeatureKnowledge = readObjectFromJsonFile(SELECT_FEATURE_KNOWLEDGE, Knowledge.class);
    final Metadata metadata = new Metadata();
    metadata.saveInfo(Metadata.KB_TIMESTAMP, String.valueOf(Calendar.getInstance()));
    this.srvc = new ResolutionServiceMock(initialKnowledge, selectVariantKnowledge, selectFeatureKnowledge, metadata);
  }

  /**
   * Test method for {@link org.psikeds.resolutionengine.interfaces.services.ResolutionService}.
   */
  @Test
  public void testResolutionService() throws Exception {
    try {
      LOGGER.info("Testing ResolutionService ...");
      assertNotNull("ResolutionService is null!", this.srvc);

      LOGGER.info("... getting initial Knowledge ...");
      final ResolutionResponse ires = this.srvc.init();
      writeObjectToJsonFile(INIT_RESPONSE, ires);

      assertNotNull("No initial Resolution-Response!", ires);
      final String sessionID1 = ires.getSessionID();
      assertFalse("No initial SessionID!", StringUtils.isEmpty(sessionID1));
      final Knowledge ike = ires.getKnowledge();
      assertNotNull("No initial Knowledge!", ike);
      final List<Choice> choices1 = ike.getChoices();
      assertNotNull("No initial Choices!", choices1);
      assertFalse("No initial Choices!", choices1.isEmpty());
      assertFalse("Initial Knowledge is already resolved!?!?", ires.isResolved());
      final Metadata metadata = ires.getMetadata();
      assertNotNull("No initial Metadata!", metadata);

      LOGGER.info("... getting current Knowledge ...");
      final ResolutionResponse cres1 = this.srvc.current(sessionID1);
      writeObjectToJsonFile(CURRENT_RESPONSE, cres1);

      assertNotNull("No Response for current Knowledge!", cres1);
      final String sessionID2 = cres1.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID2));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID2);
      final Knowledge c1ke = cres1.getKnowledge();
      assertNotNull("No current Knowledge!", c1ke);

      LOGGER.info("... making a Variant-Decission and asking for Resolution ...");
      final VariantDecission vd = readObjectFromJsonFile(VARIANT_DECISSION, VariantDecission.class);
      final ResolutionRequest svreq = new ResolutionRequest(sessionID1, metadata, vd);
      writeObjectToJsonFile(SELECT_VARIANT_REQUEST, svreq);
      final ResolutionResponse svres = this.srvc.select(svreq);
      writeObjectToJsonFile(SELECT_VARIANT_RESPONSE, svres);

      assertNotNull("No Resolution-Response!", svres);
      final String sessionID3 = svres.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID3));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID3);
      final Knowledge svke = svres.getKnowledge();
      assertNotNull("No Knowledge in Resolution-Response!", svke);

      LOGGER.info("... making a Feature-Decission and asking for Resolution ...");
      final FeatureDecission fd = readObjectFromJsonFile(FEATURE_DECISSION, FeatureDecission.class);
      final ResolutionRequest sfreq = new ResolutionRequest(sessionID1, metadata, fd);
      writeObjectToJsonFile(SELECT_FEATURE_REQUEST, sfreq);
      final ResolutionResponse sfres = this.srvc.select(sfreq);
      writeObjectToJsonFile(SELECT_FEATURE_RESPONSE, sfres);

      assertNotNull("No Resolution-Response!", sfres);
      final String sessionID4 = sfres.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID4));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID4);
      final Knowledge sfke = sfres.getKnowledge();
      assertNotNull("No Knowledge in Resolution-Response!", sfke);

      LOGGER.info("... checking current Knowledge again ...");
      final ResolutionResponse cres2 = this.srvc.current(sessionID1);
      writeObjectToJsonFile(CURRENT_RESPONSE, cres2);

      assertNotNull("No Response for current Knowledge!", cres2);
      final String sessionID5 = cres2.getSessionID();
      assertFalse("No SessionID in Resolution-Response!", StringUtils.isEmpty(sessionID5));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID5);
      final Knowledge c2ke = cres2.getKnowledge();
      assertNotNull("No current Knowledge!", c2ke);
      final List<Choice> choices2 = c2ke.getChoices();
      assertNotNull("No current Choices!", choices2);
      assertFalse("No current Choices!", choices2.isEmpty());
      assertFalse("Current Knowledge is already resolved!?!?", cres2.isResolved());

      LOGGER.info("... done. Handling of POJOs for Resolution worked as expected.");
    }
    catch (final Exception ex) {
      LOGGER.error("Test failed!", ex);
      throw ex;
    }
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
      LOGGER.trace("Writing Object to File {}\n{}", f, obj);
      MAPPER.writeValue(f, obj);
      assertTrue("Could not write Object(s) to File!", ((f != null) && f.exists()));
    }
  }

  private static void generateTestData(final boolean force) throws JsonProcessingException, IOException {
    LOGGER.info("Start generating Test-Data ... ");

    final FeatureDescription f1 = new FeatureDescription("F1", null, "F1", "integer");
    final FeatureDescription f2 = new FeatureDescription("F2", null, "F2", "float");
    final FeatureDescription f3 = new FeatureDescription("F3", null, "F3", "string");

    final Purpose p1 = new Purpose("P1", null, "P1", true);
    final Purpose p2 = new Purpose("P2", null, "P2", true);
    final Purpose p3 = new Purpose("P3", null, "P3", true);

    final Variant v11 = new Variant("V11", null, "V11");
    v11.addFeature(f1);
    final Variant v21 = new Variant("V21", null, "V21");
    final Variant v22 = new Variant("V22", null, "V22");
    v22.addFeature(f2);
    final Variant v23 = new Variant("V23", null, "V23");
    final Variant v31 = new Variant("V31", null, "V31");
    final Variant v32 = new Variant("V32", null, "V32");
    final Variant v33 = new Variant("V33", null, "V33");
    v33.addFeature(f3);

    final KnowledgeEntity ke1 = new KnowledgeEntity(p1, v11);
    final FeatureValue fv1 = new FeatureValue(f1, "42");
    ke1.addFeature(fv1);

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
      writeObjectToJsonFile(INIT_KNOWLEDGE, initialKnowledge);
    }

    final VariantDecission vd = new VariantDecission(p2, v22);
    LOGGER.info("... created Variant-Decission:\n{}", vd);
    if (force || !VARIANT_DECISSION.exists()) {
      writeObjectToJsonFile(VARIANT_DECISSION, vd);
    }

    final Knowledge selectVariantKnowledge = new Knowledge();
    selectVariantKnowledge.addKnowledgeEntity(ke1);
    final KnowledgeEntity ke2 = new KnowledgeEntity(p2, v22);
    selectVariantKnowledge.addKnowledgeEntity(ke2);
    selectVariantKnowledge.addChoice(vc2);

    final List<String> f2vals = new ArrayList<String>();
    f2vals.add("1.0");
    f2vals.add("2.0");
    f2vals.add("3.0");
    final FeatureChoice fc = new FeatureChoice(v22, f2, f2vals);
    ke1.addChoice(fc);

    LOGGER.info("... created Selected-Variant-Knowledge:\n{}", selectVariantKnowledge);
    if (force || !SELECT_VARIANT_KNOWLEDGE.exists()) {
      writeObjectToJsonFile(SELECT_VARIANT_KNOWLEDGE, selectVariantKnowledge);
    }

    final FeatureDecission fd = new FeatureDecission(fc, 1);
    LOGGER.info("... created Feature-Decission:\n{}", fd);
    if (force || !FEATURE_DECISSION.exists()) {
      writeObjectToJsonFile(FEATURE_DECISSION, fd);
    }

    final Knowledge selectFeatureKnowledge = new Knowledge();
    selectFeatureKnowledge.addKnowledgeEntity(ke1);
    final FeatureValue fv2 = new FeatureValue(f2, fd);
    ke2.clearChoices();
    ke2.addFeature(fv2);
    selectFeatureKnowledge.addKnowledgeEntity(ke2);
    selectFeatureKnowledge.addChoice(vc2);

    LOGGER.info("... created Selected-Feature-Knowledge:\n{}", selectFeatureKnowledge);
    if (force || !SELECT_FEATURE_KNOWLEDGE.exists()) {
      writeObjectToJsonFile(SELECT_FEATURE_KNOWLEDGE, selectFeatureKnowledge);
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
