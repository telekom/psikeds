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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.resolutionengine.cache.ResolutionCache;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;
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

  private ResolutionService srvc;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    final KnowledgeBase kb = new KnowledgeBaseMock();
    final Transformer trans = new Vo2PojoTransformer();
    final IdGenerator gen = new SessionIdGenerator();
    final ResolutionCache cache = new ResolutionCache();
    this.srvc = new ResolutionBusinessService(kb, trans, gen, cache);
  }

  /**
   * Test method for {@link org.psikeds.resolutionengine.services.ResolutionBusinessService}.
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

    final String choice = "P2";
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
}
