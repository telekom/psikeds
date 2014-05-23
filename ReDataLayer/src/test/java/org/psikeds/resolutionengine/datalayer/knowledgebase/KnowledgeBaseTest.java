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
package org.psikeds.resolutionengine.datalayer.knowledgebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.util.JSONHelper;
import org.psikeds.knowledgebase.xml.impl.XMLParser;
import org.psikeds.resolutionengine.datalayer.knowledgebase.impl.XmlKnowledgeBaseFactory;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.ConceptValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.ConstitutesValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.EventValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.FeatureValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.FulfillsValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.RelationValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.RuleValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl.VariantValidator;
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
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

public class KnowledgeBaseTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseTest.class);

  private static String LOG4J;
  private static String XML;
  private static File TEST_DATA_DIR;
  private static File JSON;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "../ResolutionEngine/src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
    XML = System.getProperty("org.psikeds.test.kb.xml", "../KnowledgeBase/src/main/resources/default.knowledgebase.xml");
    TEST_DATA_DIR = new File(System.getProperty("org.psikeds.test.data.dir", "./src/test/resources/"));
    if (!TEST_DATA_DIR.exists()) {
      TEST_DATA_DIR.mkdir();
    }
    JSON = new File(TEST_DATA_DIR, "KnowledgeData.json");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    JSON.deleteOnExit();
  }

  /**
   * Test method for KnowledgeBase and KnowledgeBaseFactory.
   */
  @Test
  public void testKnowledgeBase() throws Exception {
    boolean ok = false;
    LOGGER.info("Starting test of KnowledgeBase ...");
    try {
      LOGGER.info(" ... parsing XML " + XML + " ...");
      final XMLParser parser = new XMLParser(XML);
      final List<Validator> validators = new ArrayList<Validator>();
      validators.add(new FeatureValidator());
      validators.add(new ConceptValidator());
      validators.add(new FulfillsValidator());
      validators.add(new ConstitutesValidator());
      validators.add(new VariantValidator());
      validators.add(new EventValidator());
      validators.add(new RuleValidator());
      validators.add(new RelationValidator());
      final KnowledgeBaseFactory factory = new XmlKnowledgeBaseFactory(parser, validators);
      final KnowledgeBase kb = factory.create();
      assertNotNull("Failed to load KB from File " + XML, kb);
      assertTrue("KB is not valid!", kb.isValid());

      LOGGER.info("... checking Features ...");
      final Features allFeatures = kb.getFeatures();
      LOGGER.trace("All Features = {}", allFeatures);
      assertNotNull("No Features!", allFeatures);
      final List<Feature> flst = allFeatures.getFeature();
      assertNotNull("No List of Features!", flst);
      int result = flst.size();
      int expected = 10;
      assertEquals("KB has " + result + " Features total, not expected " + expected, expected, result);

      LOGGER.info("... checking FeatureValues ...");
      final FeatureValues allFeatureValues = kb.getFeatureValues();
      LOGGER.trace("All FeatureValues = {}", allFeatureValues);
      assertNotNull("No FeatureValues!", allFeatureValues);
      final List<FeatureValue> fvlst = allFeatureValues.getValue();
      assertNotNull("No List of FeatureValues!", fvlst);
      result = fvlst.size();
      expected = 397;
      assertEquals("KB has " + result + " FeatureValues total, not expected " + expected, expected, result);

      LOGGER.info("... checking Concepts ...");
      final Concepts allConcepts = kb.getConcepts();
      LOGGER.trace("All Concepts = {}", allConcepts);
      assertNotNull("No Concepts!", allConcepts);
      final List<Concept> conlst = allConcepts.getConcept();
      assertNotNull("No List of Concepts!", conlst);
      result = conlst.size();
      expected = 12;
      assertEquals("KB has " + result + " Concepts total, not expected " + expected, expected, result);

      LOGGER.info("... checking Alternatives ...");
      final Alternatives allAlternatives = kb.getAlternatives();
      LOGGER.trace("All Alternatives = {}", allAlternatives);
      assertNotNull("No Alternatives!", allAlternatives);
      final List<Fulfills> fflst = allAlternatives.getFulfills();
      assertNotNull("No List of Fulfills!", fflst);
      result = fflst.size();
      expected = 6;
      assertEquals("KB has " + result + " Alternatives / Fulfills total, not expected " + expected, expected, result);

      LOGGER.info("... checking Constituents ...");
      final Constituents allConstituents = kb.getConstituents();
      LOGGER.trace("All Constituents = {}", allConstituents);
      assertNotNull("No Constituents!", allConstituents);
      final List<Constitutes> clst = allConstituents.getConstitutes();
      assertNotNull("No List of Constitutes!", clst);
      result = clst.size();
      expected = 3;
      assertEquals("KB has " + result + " Constituents / Constitutes total, not expected " + expected, expected, result);

      LOGGER.info("... checking Events ...");
      final Events allEvents = kb.getEvents();
      LOGGER.trace("All Events = {}", allEvents);
      assertNotNull("No Events!", allEvents);
      final List<Event> elst = allEvents.getEvent();
      assertNotNull("No List of Events!", elst);
      result = elst.size();
      expected = 31;
      assertEquals("KB has " + result + " Events total, not expected " + expected, expected, result);

      LOGGER.info("... checking Rules ...");
      final Rules allRules = kb.getRules();
      LOGGER.trace("All Rules = {}", allRules);
      assertNotNull("No Rules!", allRules);
      final List<Rule> rlst = allRules.getRule();
      assertNotNull("No List of Rules!", rlst);
      result = rlst.size();
      expected = 25;
      assertEquals("KB has " + result + " Rules total, not expected " + expected, expected, result);

      LOGGER.info("... checking RelationParameters ...");
      final RelationParameters allRelationParameters = kb.getRelationParameters();
      LOGGER.trace("All RelationParameters = {}", allRelationParameters);
      assertNotNull("No RelationParameters!", allRelationParameters);
      final List<RelationParameter> params = allRelationParameters.getParameter();
      assertNotNull("No List of RelationParameters!", params);
      result = params.size();
      expected = 16;
      assertEquals("KB has " + result + " RelationParameters total, not expected " + expected, expected, result);

      LOGGER.info("... checking Relations ...");
      final Relations allRelations = kb.getRelations();
      LOGGER.trace("All Relations = {}", allRelations);
      assertNotNull("No Relations!", allRelations);
      final List<Relation> rels = allRelations.getRelation();
      assertNotNull("No List of Relations!", rels);
      result = rels.size();
      expected = 11;
      assertEquals("KB has " + result + " Relations total, not expected " + expected, expected, result);

      LOGGER.info("... checking Root-Purposes ...");
      final Purposes rootPurposes = kb.getRootPurposes();
      LOGGER.trace("Root-Purposes = {}", rootPurposes);
      assertNotNull("No Root-Purposes!", rootPurposes);
      List<Purpose> plst = rootPurposes.getPurpose();
      assertNotNull("No List of Root-Purposes!", plst);
      result = plst.size();
      expected = 1;
      assertEquals("KB has " + result + " Root-Purposes, not expected " + expected, expected, result);

      LOGGER.info("... checking all Purposes ...");
      final Purposes allPurposes = kb.getPurposes();
      LOGGER.trace("All Purposes = {}", allPurposes);
      assertNotNull("No Purposes!", allPurposes);
      plst = allPurposes.getPurpose();
      assertNotNull("No List of all Purposes!", plst);
      result = plst.size();
      expected = 6;
      assertEquals("KB has " + result + " overall Purposes, not expected " + expected, expected, result);

      LOGGER.info("... checking fulfilling Variants ...");
      for (final Purpose p : plst) {
        final String purposeId = p.getPurposeID();
        final Variants ffvs = kb.getFulfillingVariants(purposeId);
        LOGGER.trace("Fulfilling Variants of {} are\n{}", purposeId, ffvs);
        assertNotNull("No fulfilling Variants!", ffvs);
        final List<Variant> ffvlst = ffvs.getVariant();
        assertNotNull("No List of fulfilling Variants!", ffvlst);
        assertFalse("Purpose " + purposeId + " is not fulfilled by any Variant!", ffvlst.isEmpty());
      }

      LOGGER.info("... checking all Variants ...");
      final Variants allVariants = kb.getVariants();
      LOGGER.trace("All Variants = {}", allVariants);
      assertNotNull("No Variants!", allVariants);
      final List<Variant> vlst = allVariants.getVariant();
      assertNotNull("No List of all Variants!", vlst);
      result = vlst.size();
      expected = 11;
      assertEquals("KB has " + result + " Variants total, not expected " + expected, expected, result);

      LOGGER.info("... Features, Rules and Events of each Variant ...");
      for (final Variant v : vlst) {
        final Features features = kb.getFeatures(v.getVariantID());
        LOGGER.trace("Features of Variant {} are\n{}", v.getVariantID(), features);
        final Events events = kb.getAttachedEvents(v.getVariantID());
        LOGGER.trace("Events attached to Variant {} are\n{}", v.getVariantID(), events);
        final Rules rules = kb.getAttachedRules(v.getVariantID());
        LOGGER.trace("Rules attached to Variant {} are\n{}", v.getVariantID(), rules);
        final Relations relations = kb.getAttachedRelations(v.getVariantID());
        LOGGER.trace("Relations attached to Variant {} are\n{}", v.getVariantID(), relations);
        final Purposes purposes = kb.getConstitutingPurposes(v.getVariantID());
        LOGGER.trace("Purposes constituting Variant {} are\n{}", v.getVariantID(), purposes);
      }

      LOGGER.info("... testing Serialization and writing all Knowledge-Base-Data to JSON-File ...");
      final MetaData metadata = kb.getMetaData();
      KnowledgeData kd = new KnowledgeData(metadata, allFeatures, allFeatureValues, allConcepts, allPurposes, allVariants, allAlternatives, allConstituents, allEvents, allRules,
          allRelationParameters, allRelations);
      JSONHelper.writeObjectToJsonFile(JSON, kd);

      LOGGER.info("... testing Deserialization and reading all Knowledge-Base-Data back from JSON-File ...");
      kd = JSONHelper.readObjectFromJsonFile(JSON, KnowledgeData.class);
      assertNotNull("Cannot read Knowledge-Base-Data back from JSON-File!", kd);

      LOGGER.trace("Knowledge-Base-Data =\n{}", kd);
      ok = true;
    }
    catch (final AssertionError ae) {
      ok = false;
      LOGGER.error("Functional Error: " + ae.getMessage(), ae);
      throw ae;
    }
    catch (final Throwable t) {
      ok = false;
      LOGGER.error("Technical Error: " + t.getMessage(), t);
      fail(t.getMessage());
    }
    finally {
      LOGGER.info("... test of KnowledgeBase finished " + (ok ? "without problems." : "with ERRORS!!!"));
    }
  }
}
