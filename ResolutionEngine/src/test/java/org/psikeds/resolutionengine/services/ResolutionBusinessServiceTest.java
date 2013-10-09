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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.resolutionengine.cache.ResolutionCache;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.resolver.impl.AutoCompletion;
import org.psikeds.resolutionengine.resolver.impl.DecissionEvaluator;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;

/**
 * Unit-Tests for {@link org.psikeds.resolutionengine.services.ResolutionBusinessService}.
 *
 * Note: Anything called *Test.java is a Unit-Test executed offline by Surefire.
 *       Everythinf called *IT.java is an Integration-Test executed online by Failsafe.
 *
 * @author marco@juliano.de
 *
 */
public class ResolutionBusinessServiceTest {

  private static final String LOG4J = "./src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionBusinessServiceTest.class);

  private KnowledgeBase kb;
  private Transformer trans;
  private List<Resolver> resolvers;
  private ResolutionCache cache;
  private ResolutionService srvc;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    this.kb = new KnowledgeBaseMock();
    this.trans = new Vo2PojoTransformer();
    this.resolvers = new ArrayList<Resolver>();
    this.resolvers.add(new DecissionEvaluator());
    this.resolvers.add(new AutoCompletion(this.kb, this.trans));
    this.cache = new ResolutionCache();
    this.srvc = new ResolutionBusinessService(
        this.kb,
        this.resolvers,
        this.cache,
        this.trans,
        new SessionIdGenerator("MOCK")
        );
  }

  @After
  public void tearDown() throws Exception {
    this.cache.clear();
    this.resolvers.clear();
  }

  /**
   * Test method for {@link org.psikeds.resolutionengine.services.ResolutionBusinessService}.
   */
  @Test
  public void testResolutionService() throws Exception {
    assertNotNull("ResolutionCache is null!", this.cache);
    assertNotNull("ResolutionService is null!", this.srvc);

    final ResolutionResponse ires = this.srvc.init();
    LOGGER.debug("Received:\n{}", ires);
    assertNotNull("No Init-ResolutionResponse!", ires);

    assertFalse("Initial Knowledge is already fully resolved! Check Testdata!", ires.isResolved());

    final String sessionID1 = ires.getSessionID();
    assertNotNull("No initial SessionID!", sessionID1);

    final Knowledge k1 = ires.getKnowledge();
    assertNotNull("No initial Knowledge!", k1);

    final List<Choice> choices = ires.getPossibleChoices();
    assertNotNull("No choices!", choices);
    assertFalse("No choices!", choices.isEmpty());

    for (final Choice c : choices) {
      assertNotNull("Choice is null!", c);
      final Purpose p = c.getPurpose();
      assertNotNull("No Purpose in Choice!", p);
      assertTrue("Initial choice contains a Purpose which is not a Root-Purpose: " + p, p.isRoot());
      final List<Variant> vars = c.getVariants();
      assertNotNull("No Variants in Choice!", vars);
      assertFalse("No Variants in Choice!", vars.isEmpty());
    }

    final Choice c = choices.get(0);
    final Purpose p = c.getPurpose();
    final Variant v = c.getVariants().get(0);
    final Decission decission = new Decission(p, v);
    final ResolutionRequest sreq = new ResolutionRequest(sessionID1, decission);
    LOGGER.debug("Sending:\n{}", sreq);

    final ResolutionResponse sres = this.srvc.select(sreq);
    LOGGER.debug("Received:\n{}", sres);
    assertNotNull("No Select-ResolutionResponse!", sres);

    final String sessionID2 = sres.getSessionID();
    assertNotNull("No SessionID in SelectResponse!", sessionID2);
    assertEquals("SessionIDs are not matching!", sessionID1, sessionID2);

    final Knowledge k2 = sres.getKnowledge();
    assertNotNull("No new Knowledge in Select-ResolutionResponse!", k2);

    assertFalse("Knowledge is fully resolved but still contains Choices!", sres.isResolved() && sres.getPossibleChoices() != null && sres.getPossibleChoices().size() > 0);
    assertFalse("Knowledge is not resolved yet but does not contain any Choices!", !sres.isResolved() && (sres.getPossibleChoices() == null || sres.getPossibleChoices().isEmpty()));
  }
}
