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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.resolutionengine.cache.ResolutionCache;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Choices;
import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission;
import org.psikeds.resolutionengine.interfaces.pojos.Concepts;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantDecission;
import org.psikeds.resolutionengine.interfaces.pojos.Variants;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.resolver.impl.AutoCompletion;
import org.psikeds.resolutionengine.resolver.impl.ConceptDecissionEvaluator;
import org.psikeds.resolutionengine.resolver.impl.EventEvaluator;
import org.psikeds.resolutionengine.resolver.impl.FeatureDecissionEvaluator;
import org.psikeds.resolutionengine.resolver.impl.RulesEvaluator;
import org.psikeds.resolutionengine.resolver.impl.VariantDecissionEvaluator;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;

/**
 * Unit-Tests for {@link org.psikeds.resolutionengine.services.ResolutionBusinessService} and
 * Resolvers ({@link org.psikeds.resolutionengine.resolver.Resolver}).
 * 
 * Note: Any Test called *Test.java is a Unit-Test executed offline by Surefire.
 * Everything called *IT.java is an Integration-Test executed online by Failsafe.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionBusinessServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionBusinessServiceTest.class);
  private static String LOG4J;

  private KnowledgeBase kb;
  private Transformer trans;
  private List<Resolver> resolvers;
  private ResolutionCache cache;
  private ResolutionService srvc;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "./src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    this.kb = new KnowledgeBaseMock();
    this.trans = new Vo2PojoTransformer();
    this.resolvers = new ArrayList<Resolver>();
    this.resolvers.add(new VariantDecissionEvaluator());
    this.resolvers.add(new FeatureDecissionEvaluator());
    this.resolvers.add(new ConceptDecissionEvaluator());
    this.resolvers.add(new AutoCompletion(this.kb, this.trans));
    this.resolvers.add(new EventEvaluator(this.kb, this.trans));
    this.resolvers.add(new RulesEvaluator(this.kb, this.trans));
    this.cache = new ResolutionCache();
    this.srvc = new ResolutionBusinessService(
        this.kb,
        this.resolvers,
        this.cache,
        this.trans,
        new SessionIdGenerator("MOCK"));
  }

  @After
  public void tearDown() throws Exception {
    this.cache.clear();
    this.resolvers.clear();
  }

  /**
   * Test method for {@link org.psikeds.resolutionengine.services.ResolutionBusinessService}.
   * 
   * This Test will request the initial Knowledge, make a Decission based on the first Choice
   * and afterwards ask for the current/last Resolution-State.
   * 
   */
  @Test
  public void testResolutionService() throws Exception {
    try {
      LOGGER.info("Testing Resolution-Business-Service ...");
      assertNotNull("KnowledgeBase is null!", this.kb);
      assertNotNull("ResolutionCache is null!", this.cache);
      assertNotNull("ResolutionService is null!", this.srvc);

      LOGGER.info("Getting initial Knowledge ...");
      final ResolutionResponse ires = this.srvc.init();
      assertNotNull("No Init-Response!", ires);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("... received:\n{}", ires);
      }
      else {
        LOGGER.info("... Init-Response received.");
      }

      assertFalse("Resolution failed! Init-Response contains Error-Messages!", ires.hasErrors());
      assertFalse("Initial Knowledge is already fully resolved! Check Testdata!", ires.isResolved());
      final String sessionID1 = ires.getSessionID();
      assertFalse("No initial SessionID!", StringUtils.isEmpty(sessionID1));
      final Knowledge k1 = ires.getKnowledge();
      assertNotNull("No initial Knowledge!", k1);
      final Choices choices = ires.getChoices();
      assertNotNull("No choices!", choices);
      assertFalse("No choices!", choices.isEmpty());

      Decission decission = null;
      for (final Choice c : choices) {
        assertNotNull("Choice is null!", c);
        if (c instanceof VariantChoice) {
          final VariantChoice vc = (VariantChoice) c;
          final Purpose p = vc.getPurpose();
          assertNotNull("No Purpose in Choice!", p);
          assertTrue("Initial choice contains a Purpose which is not a Root-Purpose: " + p, p.isRoot());
          final Variants vars = vc.getVariants();
          assertNotNull("No Variants in Choice!", vars);
          assertFalse("No Variants in Choice!", vars.isEmpty());
          final Variant v = vars.get(0);
          assertNotNull("Variant of first Choice is null!", v);
          decission = new VariantDecission(p, v);
        }
        else if (c instanceof FeatureChoice) {
          final FeatureChoice fc = (FeatureChoice) c;
          final String fid = fc.getFeatureID();
          assertFalse("No Feature in Choice!", StringUtils.isEmpty(fid));
          final String vid = fc.getParentVariantID();
          assertFalse("No Parent-Variant in Choice!", StringUtils.isEmpty(vid));
          final FeatureValues fvs = fc.getPossibleValues();
          assertNotNull("No possible Values in Choice!", fvs);
          assertFalse("No possible Values in Choice!", fvs.isEmpty());
          final FeatureValue val = fvs.get(0);
          assertNotNull("First Value of Choice is null!", val);
          final String fvid = val.getFeatureValueID();
          assertFalse("ID of first Feature-Value in Choice is empty!", StringUtils.isEmpty(fvid));
          assertEquals("Feature-ID of FeatureChoice and FeatureValue are not the same", fid, val.getFeatureID());
          decission = new FeatureDecission(vid, fid, fvid);
        }
        else if (c instanceof ConceptChoice) {
          final ConceptChoice cc = (ConceptChoice) c;
          final String vid = cc.getParentVariantID();
          assertFalse("No Parent-Variant in Choice!", StringUtils.isEmpty(vid));
          final Concepts cons = cc.getConcepts();
          assertNotNull("No Concepts in Choice!", cons);
          assertFalse("No Concepts in Choice!", cons.isEmpty());
          final Concept con = cons.get(0);
          assertNotNull("First Concept of Choice is null!", con);
          final String cid = con.getConceptID();
          assertFalse("ID of first Concept in Choice is empty!", StringUtils.isEmpty(cid));
          decission = new ConceptDecission(vid, cid);
        }
        else {
          decission = null;
          fail("Unexpected Object: " + String.valueOf(c));
        }
      }
      assertNotNull("No Decission possible!", decission);

      final ResolutionRequest sreq = new ResolutionRequest(sessionID1, decission);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Sending Select-Request:\n{}", sreq);
      }
      else {
        LOGGER.info("Sending Select-Request ...");
      }
      final ResolutionResponse sres = this.srvc.select(sreq);
      assertNotNull("No Select-Response!", sres);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("... received:\n{}", sres);
      }
      else {
        LOGGER.info("... Select-Response received.");
      }

      assertFalse("Resolution failed! Select-Response contains Error-Messages!", sres.hasErrors());
      final String sessionID2 = sres.getSessionID();
      assertFalse("No SessionID in Select-Response!", StringUtils.isEmpty(sessionID2));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID2);
      final Knowledge k2 = sres.getKnowledge();
      assertNotNull("No new Knowledge in Select-Response!", k2);

      assertFalse("Knowledge is fully resolved but still contains Choices!", sres.isResolved() && (sres.getChoices() != null) && (sres.getChoices().size() > 0));
      assertFalse("Knowledge is not resolved yet but does not contain any Choices!", !sres.isResolved() && ((sres.getChoices() == null) || sres.getChoices().isEmpty()));

      LOGGER.info("Requesting current Resolution-State for existing SessionID ...");
      final ResolutionResponse cres = this.srvc.current(sessionID1);
      assertNotNull("No Current-Response!", cres);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("... received:\n{}", cres);
      }
      else {
        LOGGER.info("... Current-Response received.");
      }

      final String sessionID3 = cres.getSessionID();
      assertFalse("No SessionID in Current-Response!", StringUtils.isEmpty(sessionID3));
      assertEquals("SessionIDs are not matching!", sessionID1, sessionID3);
      final Knowledge k3 = cres.getKnowledge();
      assertNotNull("No Knowledge in Current-Response!", k3);

      // TODO: additional tests here

      LOGGER.info("... done. Resolution-Business-Service worked as expected.");
    }
    catch (final Exception ex) {
      LOGGER.error("Test failed!", ex);
      throw ex;
    }
  }
}
