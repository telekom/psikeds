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
package org.psikeds.resolutionengine.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Choices;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantDecission;

/**
 * Integration-Tests for Resolution-Engine running online against Application Server.
 * 
 * Note: Any Test called *Test.java is a Unit-Test executed offline by Surefire.
 * Everything called *IT.java is an Integration-Test executed online by Failsafe.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionEngineIT {

  private static final String LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "./src/main/resources/log4j.xml");
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
  public void setUp() {
    LOGGER.info("Setting up Integration-Tests ...");
    try {
      this.baseUrl = System.getProperty("resolution.engine.base.url");
      LOGGER.debug("Base URL = {}", this.baseUrl);
      assertFalse("No Base URL!", StringUtils.isEmpty(this.baseUrl));
      this.configDir = System.getProperty("org.psikeds.config.dir");
      LOGGER.debug("Config Dir = {}", this.configDir);
      assertFalse("No Config Dir!", StringUtils.isEmpty(this.configDir));
      this.providers = new ArrayList<Object>();
      this.providers.add(new JacksonJsonProvider());
      this.providers.add(new JAXBElementProvider());
    }
    finally {
      LOGGER.info(" ... setup of Integration-Tests finished.");
    }
  }

  @Test
  public void testAgainstApplicationServer() throws Exception {
    boolean ok = false;
    try {
      LOGGER.info("Testing against Application Server ...");
      checkDeployedServices();
      final ResolutionResponse ires = checkInitService();
      final String sessionID = ires.getSessionID();
      final Choices choices = ires.getChoices();
      final Decission decission = makeDecission(choices);
      checkPredictionService(sessionID, decission);
      checkSelectService(sessionID, decission);
      ok = true;
    }
    catch (final AssertionError ae) {
      ok = false;
      LOGGER.error("Functional error while testing Resolution-Engine against Application-Server: " + ae.getMessage(), ae);
      throw ae;
    }
    catch (final Exception ex) {
      ok = false;
      final String msg = "Technical error while testing Resolution-Engine against Application-Server: " + ex.getMessage();
      LOGGER.error(msg, ex);
      fail(msg);
    }
    finally {
      LOGGER.info("... tests against Application Server finished " + (ok ? "succesfully." : "with Errors!!!"));
    }
  }

  // ---------------------------------------------------------------

  private void checkDeployedServices() throws IllegalStateException, IOException {
    LOGGER.info(" ... checking deployed REST-Services ...");
    final String wadlUrl = this.baseUrl + "/?_wadl";
    LOGGER.trace("URL = {}", wadlUrl);
    final WebClient wadlClient = WebClient.create(wadlUrl);
    final Response wadlResp = wadlClient.get();
    assertNotNull("No Response!", wadlResp);
    final int code = wadlResp.getStatus();
    LOGGER.info("HTTP {}", code);
    assertEquals(Response.Status.OK.getStatusCode(), code);
    final String wadl = IOUtils.toString((InputStream) wadlResp.getEntity());
    LOGGER.trace("WADL = {}", wadl);
    assertFalse("No WADL!", StringUtils.isEmpty(wadl));
  }

  private ResolutionResponse checkInitService() throws JsonParseException, IOException {
    ResolutionResponse ires = null;
    LOGGER.info("... checking Init-Service ...");
    try {
      final String initServiceUrl = this.baseUrl + "/resolution/init";
      LOGGER.debug("URL = {}", initServiceUrl);
      final WebClient initClient = WebClient.create(initServiceUrl);
      final Response initResp = initClient.accept(MediaType.APPLICATION_JSON).get();
      checkHttpResponse(initResp);
      ires = getContent(initResp, ResolutionResponse.class);
      assertNotNull("No Init-ResolutionResponse!", ires);
      assertNotNull("No SessionID in Init-ResolutionResponse!", ires.getSessionID());
      assertNotNull("No Knowledge in Init-ResolutionResponse!", ires.getKnowledge());
      assertFalse("Initial Knowledge is already fully resolved! Check Testdata!", ires.isResolved());
      final Choices choices = ires.getChoices();
      assertTrue("No Choices in Init-ResolutionResponse! Check Testdata!", (choices != null) && (choices.size() > 0));
      return ires;
    }
    finally {
      LOGGER.info("... finished check of Init-Service ...");
      LOGGER.trace("Resp = {}", ires);
    }
  }

  private Decission makeDecission(final List<Choice> choices) {
    Decission decission = null;
    try {
      final Choice c = choices.get(0);
      if (c instanceof VariantChoice) {
        final VariantChoice vc = (VariantChoice) c;
        final Purpose p = vc.getPurpose();
        final Variant v = vc.getVariants().get(0);
        decission = new VariantDecission(p, v);
      }
      else if (c instanceof FeatureChoice) {
        final FeatureChoice fc = (FeatureChoice) c;
        decission = new FeatureDecission(fc, 0);
      }
      return decission;
    }
    finally {
      LOGGER.debug("Decission = {}", decission);
    }
  }

  private ResolutionResponse checkPredictionService(final String sessionID, final Decission decission) throws JsonParseException, IOException {
    ResolutionResponse pres = null;
    LOGGER.info("... checking Prediction-Service ...");
    try {
      final String predictServiceUrl = this.baseUrl + "/resolution/predict";
      LOGGER.debug("URL = {}", predictServiceUrl);
      final WebClient predictClient = WebClient.create(predictServiceUrl, this.providers, true);

      final ResolutionRequest preq = new ResolutionRequest(sessionID, decission);
      LOGGER.trace("Req = {}", preq);
      final Response predictResp = predictClient.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(preq);
      checkHttpResponse(predictResp);
      pres = getContent(predictResp, ResolutionResponse.class);
      assertNotNull("No Prediction-ResolutionResponse!", pres);
      final String newSessionID = pres.getSessionID();
      assertNotNull("No SessionID in Prediction-ResolutionResponse!", newSessionID);
      assertNotEquals("Old and new SessionID are identical, but must be different!", sessionID, newSessionID);
      assertNotNull("No Knowledge in Select-ResolutionResponse!", pres.getKnowledge());
      return pres;
    }
    finally {
      LOGGER.info("... finished check of Select-Service ...");
      LOGGER.trace("Resp = {}", pres);
    }
  }

  private ResolutionResponse checkSelectService(final String sessionID, final Decission decission) throws JsonParseException, IOException {
    ResolutionResponse sres = null;
    LOGGER.info("... checking Select-Service ...");
    try {
      final String selectServiceUrl = this.baseUrl + "/resolution/select";
      LOGGER.debug("URL = {}", selectServiceUrl);
      final WebClient selectClient = WebClient.create(selectServiceUrl, this.providers, true);

      final ResolutionRequest sreq = new ResolutionRequest(sessionID, decission);
      LOGGER.trace("Req = {}", sreq);
      final Response selectResp = selectClient.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(sreq);
      checkHttpResponse(selectResp);
      sres = getContent(selectResp, ResolutionResponse.class);
      assertNotNull("No Select-ResolutionResponse!", sres);
      final String newSessionID = sres.getSessionID();
      assertNotNull("No SessionID in Select-ResolutionResponse!", newSessionID);
      assertEquals("Old and new SessionID are different, but must be identical!", sessionID, newSessionID);
      assertNotNull("No Knowledge in Select-ResolutionResponse!", sres.getKnowledge());
      return sres;
    }
    finally {
      LOGGER.info("... finished check of Select-Service ...");
      LOGGER.trace("Resp = {}", sres);
    }
  }

  private void checkHttpResponse(final Response resp) {
    assertNotNull("No Response!", resp);
    final int code = resp.getStatus();
    LOGGER.info("HTTP {}", code);
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
      LOGGER.trace("Content: {} = {}", valueType, content);
    }
  }
}
