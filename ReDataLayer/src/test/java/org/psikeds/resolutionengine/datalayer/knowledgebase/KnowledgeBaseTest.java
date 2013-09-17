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
package org.psikeds.resolutionengine.datalayer.knowledgebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

public class KnowledgeBaseTest {

  private static final String LOG4J = "../ResolutionEngine/src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseTest.class);

  private static final String XML = "../KnowledgeBase/src/main/resources/kb.xml";
  private static final String ENCODING = "UTF-8";

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
    try {
      LOGGER.info("Starting test of KnowledgeBase ...");
      final XMLParser parser = new XMLParser();
      parser.setEncoding(ENCODING);
      parser.setXmlFilename(XML);
      parser.setNumOfSkippedElements(0);
      final KnowledgeBaseFactory factory = new XmlKnowledgeBaseFactory(parser);
      final KnowledgeBase kb = factory.create();
      assertTrue("Failed to load KB from File " + XML, kb != null);

      final Purposes purps = kb.getPurposes();
      LOGGER.info("Purposes = {}", purps);
      assertTrue("No Purposes!", purps != null);
      final List<Purpose> plst = purps.getPurpose();
      int expected = 5;
      assertEquals("KB has " + plst.size() + " Purposes, not expected " + expected, expected, plst.size());

      final Variants vars = kb.getVariants();
      LOGGER.info("Variants = {}", vars);
      assertTrue("No Variants!", vars != null);
      final List<Variant> vlst = vars.getVariant();
      expected = 22;
      assertEquals("KB has " + vlst.size() + " Variants, not expected " + expected, expected, vlst.size());

      final Alternatives alts = kb.getAlternatives();
      LOGGER.info("Alternatives = {}", alts);
      assertTrue("No Alternatives!", alts != null);

      final Constituents cons = kb.getConstituents();
      LOGGER.info("Constituents = {}", cons);
      assertTrue("No Constituents!", cons != null);

      final Events evts = kb.getEvents();
      LOGGER.info("Events = {}", evts);
      assertTrue("No Events!", evts != null);

      final Rules rules = kb.getRules();
      LOGGER.info("Rules = {}", rules);
      assertTrue("No Rules!", rules != null);

      // TODO: additional tests
    }
    catch (final AssertionError ae) {
      LOGGER.error("Functional Error!", ae);
      throw ae;
    }
    catch (final Throwable t) {
      LOGGER.error("Technical Error!", t);
      fail(t.getMessage());
    }
    finally {
      LOGGER.info("... test of KnowledgeBase finished.");
    }
  }
}
