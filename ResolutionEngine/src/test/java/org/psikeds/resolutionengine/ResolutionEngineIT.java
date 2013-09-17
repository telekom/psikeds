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
package org.psikeds.resolutionengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;

/**
 * Integration-Tests for Resolution-Engine running online against Application Server.
 *
 * Note: Anything called *Test.java is a Unit-Test executed offline by Surefire.
 *       Everythinf called *IT.java is an Integration-Test executed online by Failsafe.
 *
 * @author marco@juliano.de
 *
 */
public class ResolutionEngineIT {

  private static final String LOG4J = "./src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionEngineIT.class);

  private String baseUrl;
  private String configDir;
  private List<Object> providers;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  @SuppressWarnings("rawtypes")
  public void setUp() throws Exception {
    this.baseUrl = System.getProperty("resolution.engine.base.url");
    LOGGER.info("Base URL = {}", this.baseUrl);
    assertFalse("No Base URL!", StringUtils.isEmpty(this.baseUrl));
    this.configDir = System.getProperty("org.psikeds.config.dir");
    LOGGER.info("Config Dir = {}", this.configDir);
    assertFalse("No Config Dir!", StringUtils.isEmpty(this.configDir));
    this.providers = new ArrayList<Object>();
    this.providers.add(new JacksonJsonProvider());
    this.providers.add(new JAXBElementProvider());
  }

  @Test
  public void testAgainstApplicationServer() throws Exception {
    checkDeployedServices();
    final String sessionID = checkInitService();
    checkSelectService(sessionID);
    // TODO: additional tests
  }

  // ---------------------------------------------------------------

  private void checkDeployedServices() throws IllegalStateException, IOException {
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

  private String checkInitService() throws JsonParseException, IOException {
    final String initServiceUrl = this.baseUrl + "/resolution/init";
    LOGGER.info("Checking Init-Service: " + initServiceUrl);
    final WebClient initClient = WebClient.create(initServiceUrl);
    final Response initResp = initClient.accept(MediaType.APPLICATION_JSON).get();
    checkHttpResponse(initResp);
    final InitResponse ires = getContent(initResp, InitResponse.class);
    assertTrue("No InitResponse!", ires != null);
    LOGGER.debug(String.valueOf(ires));
    final String sessionID = ires.getSessionID();
    assertTrue("No SessionID!", sessionID != null);
    assertTrue("No KnowledgeEntity!", ires.getKnowledgeEntity() != null);
    return sessionID;
  }

  private void checkSelectService(final String sessionID) throws JsonParseException, IOException {
    final String selectServiceUrl = this.baseUrl + "/resolution/select";
    LOGGER.info("Checking Select-Service: " + selectServiceUrl);
    final WebClient selectClient = WebClient.create(selectServiceUrl, this.providers, true);
    final SelectRequest sreq = new SelectRequest(sessionID, null, "P1");
    LOGGER.debug(String.valueOf(sreq));
    final Response selectResp = selectClient.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(sreq);
    checkHttpResponse(selectResp);
    final SelectResponse sres = getContent(selectResp, SelectResponse.class);
    assertTrue("No SelectResponse!", sres != null);
    LOGGER.debug(String.valueOf(sres));
    final String newSessionID = sres.getSessionID();
    assertTrue("No SessionID!", newSessionID != null);
    assertEquals("Old and new SessionID are note identical!", sessionID, newSessionID);
    assertTrue("No KnowledgeEntity!", sres.getKnowledgeEntity() != null);
  }

  private void checkHttpResponse(final Response resp) {
    assertTrue("No Response!", resp != null);
    final int code = resp.getStatus();
    final int len = resp.getLength();
    LOGGER.info("HTTP {}, {} bytes.", code, len);
    assertEquals(Response.Status.OK.getStatusCode(), code);
  }

  private <T> T getContent(final Response resp, final Class<T> valueType) throws JsonParseException, IOException {
    InputStream stream = null;
    T content = null;
    try {
      stream = (InputStream) resp.getEntity();
      final MappingJsonFactory factory = new MappingJsonFactory();
      final JsonParser parser = factory.createJsonParser(stream);
      content = parser.readValueAs(valueType);
      return content;
    }
    finally {
      if (stream != null) {
        try {
          stream.close();
        }
        catch (final IOException ioex) {
          // ignore
        }
        finally {
          stream = null;
        }
      }
      LOGGER.trace("getContent: {} = {}", valueType, content);
    }
  }
}
