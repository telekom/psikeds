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
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.knowledgebase.xml.impl.XMLParser;
import org.psikeds.resolutionengine.datalayer.knowledgebase.impl.XmlKnowledgeBaseFactory;
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
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

public class KnowledgeBaseTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseTest.class);
  private static final String LOG4J = "../ResolutionEngine/src/main/resources/log4j.xml";
  private static final String XML = "../KnowledgeBase/src/main/resources/kb.xml";

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  /**
   * Test method for KnowledgeBase and KnowledgeBaseFactory.
   */
  @Test
  public void testKnowledgeBase() throws Exception {
    boolean ok = true;
    LOGGER.info("Starting test of KnowledgeBase ...");
    try {
      LOGGER.info(" ... parsing XML " + XML + " ...");
      final XMLParser parser = new XMLParser(XML);
      final KnowledgeBaseFactory factory = new XmlKnowledgeBaseFactory(parser);
      final KnowledgeBase kb = factory.create();
      assertNotNull("Failed to load KB from File " + XML, kb);

      LOGGER.info("... checking Features ...");
      Features feats = kb.getFeatures();
      LOGGER.trace("All Features = {}", feats);
      assertNotNull("No Features!", feats);
      final List<Feature> flst = feats.getFeature();
      assertNotNull("No List of Features!", flst);
      int result = flst.size();
      int expected = 4;
      assertEquals("KB has " + result + " Features total, not expected " + expected, expected, result);

      LOGGER.info("... checking Alternatives ...");
      final Alternatives alts = kb.getAlternatives();
      LOGGER.trace("All Alternatives = {}", alts);
      assertNotNull("No Alternatives!", alts);
      final List<Fulfills> fflst = alts.getFulfills();
      assertNotNull("No List of Fulfills!", fflst);
      result = fflst.size();
      expected = 5;
      assertEquals("KB has " + result + " Alternatives / Fulfills total, not expected " + expected, expected, result);

      LOGGER.info("... checking Constituents ...");
      final Constituents cons = kb.getConstituents();
      LOGGER.trace("All Constituents = {}", cons);
      assertNotNull("No Constituents!", cons);
      final List<Constitutes> clst = cons.getConstitutes();
      assertNotNull("No List of Constitutes!", clst);
      result = clst.size();
      expected = 3;
      assertEquals("KB has " + result + " Constituents / Constitutes total, not expected " + expected, expected, result);

      LOGGER.info("... checking Events ...");
      Events evts = kb.getEvents();
      LOGGER.trace("All Events = {}", evts);
      assertNotNull("No Events!", evts);
      final List<Event> elst = evts.getEvent();
      assertNotNull("No List of Events!", elst);
      result = elst.size();
      expected = 3;
      assertEquals("KB has " + result + " Events total, not expected " + expected, expected, result);

      LOGGER.info("... checking Rules ...");
      Rules rules = kb.getRules();
      LOGGER.trace("All Rules = {}", rules);
      assertNotNull("No Rules!", rules);
      final List<Rule> rlst = rules.getRule();
      assertNotNull("No List of Rules!", rlst);
      result = rlst.size();
      expected = 1;
      assertEquals("KB has " + result + " Rules total, not expected " + expected, expected, result);

      LOGGER.info("... checking Root-Purposes ...");
      Purposes purps = kb.getRootPurposes();
      LOGGER.trace("Root-Purposes = {}", purps);
      assertNotNull("No Root-Purposes!", purps);
      List<Purpose> plst = purps.getPurpose();
      assertNotNull("No List of Root-Purposes!", plst);
      result = plst.size();
      expected = 3;
      assertEquals("KB has " + result + " Root-Purposes, not expected " + expected, expected, result);

      LOGGER.info("... checking all Purposes ...");
      purps = kb.getPurposes();
      LOGGER.trace("All Purposes = {}", purps);
      assertNotNull("No Purposes!", purps);
      plst = purps.getPurpose();
      assertNotNull("No List of all Purposes!", plst);
      result = plst.size();
      expected = 5;
      assertEquals("KB has " + result + " overall Purposes, not expected " + expected, expected, result);

      LOGGER.info("... checking fulfilling Variants ...");
      for (final Purpose p : plst) {
        final Variants ffvs = kb.getFulfillingVariants(p);
        final String purposeId = p.getId();
        LOGGER.trace("Fulfilling Variants of {} are\n{}", purposeId, ffvs);
        assertNotNull("No fulfilling Variants!", ffvs);
        final List<Variant> ffvlst = ffvs.getVariant();
        assertNotNull("No List of fulfilling Variants!", ffvlst);
        assertFalse("Purpose " + purposeId + " is not fulfilled by any Variant!", ffvlst.isEmpty());
      }

      LOGGER.info("... checking all Variants ...");
      final Variants vars = kb.getVariants();
      LOGGER.trace("All Variants = {}", vars);
      assertNotNull("No Variants!", vars);
      final List<Variant> vlst = vars.getVariant();
      assertNotNull("No List of all Variants!", vlst);
      result = vlst.size();
      expected = 22;
      assertEquals("KB has " + result + " Variants total, not expected " + expected, expected, result);

      LOGGER.info("... Features, Rules and Events of each Variant ...");
      for (final Variant v : vlst) {
        feats = kb.getFeatures(v.getId());
        LOGGER.trace("Features of Variant {} are\n{}", v.getId(), feats);
        evts = kb.getAttachedEvents(v.getId());
        LOGGER.trace("Events attached to Variant {} are\n{}", v.getId(), evts);
        rules = kb.getAttachedRules(v.getId());
        LOGGER.trace("Rules attached to Variant {} are\n{}", v.getId(), rules);
        purps = kb.getConstitutingPurposes(v);
        LOGGER.trace("Purposes constituting Variant {} are\n{}", v.getId(), purps);
      }

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
      LOGGER.info("... test of KnowledgeBase finished " + (ok ? "without problems." : "with Errors!"));
    }
  }
}
