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
package org.psikeds.queryagent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Integration-Tests for Query-Agent running online against Application Server.
 * 
 * Note: Any Test called *Test.java is a Unit-Test executed offline by Surefire.
 * Everything called *IT.java is an Integration-Test executed online by Failsafe.
 * 
 * @author marco@juliano.de
 * 
 */
public class QueryAgentIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueryAgentIT.class);

  private static String LOG4J;

  private String baseUrl;
  private String configDir;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "./src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    this.baseUrl = System.getProperty("query.agent.base.url");
    LOGGER.info("Base URL = {}", this.baseUrl);
    assertFalse("No Base URL!", StringUtils.isEmpty(this.baseUrl));
    this.configDir = System.getProperty("org.psikeds.config.dir");
    LOGGER.info("Config Dir = {}", this.configDir);
    assertFalse("No Config Dir!", StringUtils.isEmpty(this.configDir));
  }

  @Test
  public void testAgainstApplicationServer() throws Exception {
    // just check that everything was deployed and that
    // services and a wadl are available
    final String wadlUrl = this.baseUrl + "/?_wadl";
    LOGGER.info("Checking deployed REST-Services: " + wadlUrl);
    final WebClient wadlClient = WebClient.create(wadlUrl);
    final Response wadlResp = wadlClient.get();
    assertTrue("No Response!", wadlResp != null);
    final int code = wadlResp.getStatus();
    final int len = wadlResp.getLength();
    LOGGER.info("HTTP {}, {} bytes.", code, len);
    assertEquals(Response.Status.OK.getStatusCode(), code);
    final String wadl = IOUtils.toString((InputStream) wadlResp.getEntity());
    LOGGER.debug("WADL = " + wadl);
    assertFalse("No WADL!", StringUtils.isEmpty(wadl));
  }
}
