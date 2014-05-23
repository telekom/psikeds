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
package org.psikeds.queryagent.requester.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.util.JSONHelper;
import org.psikeds.queryagent.requester.client.impl.ResolutionEngineClientRestImpl;
import org.psikeds.queryagent.requester.client.impl.WebClientFactoryImpl;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantDecission;

/**
 * Simple Client for invoking Services of Resolution-Engine
 * (i.e. perform manually what the Query-Agent would do)
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionEngineClientTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionEngineClientTool.class);

  private static String LOG4J;
  private static File TEST_DATA_DIR;

  private static boolean doInvokeInit = true;
  private static boolean doInvokeCurrent = true;
  private static boolean doInvokeSelect = true;
  private static boolean doUseSessionId = true;
  private static boolean doOverWrite = false;

  private static File fInitResponse;
  private static File fCurrentResponse;
  private static File fSelectRequest;
  private static File fSelectResponse;
  private static File fDecission;
  private static String reBaseUrl;

  public static void main(final String[] args) {
    try {
      configureLogging();
      parseParameters(args);
      invokeServices();
    }
    catch (final Exception ex) {
      LOGGER.error("Could not invoke Services of Resolution-Engine!", ex);
    }
  }

  private static void configureLogging() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "./src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
  }

  private static void parseParameters(final String[] args) {
    // defaults
    TEST_DATA_DIR = new File(System.getProperty("org.psikeds.test.data.dir", "./src/test/resources/"));
    if (!TEST_DATA_DIR.exists()) {
      TEST_DATA_DIR.mkdir();
    }
    fInitResponse = new File(TEST_DATA_DIR, "InitResponse.json");
    fCurrentResponse = new File(TEST_DATA_DIR, "CurrentResponse.json");
    fSelectRequest = new File(TEST_DATA_DIR, "SelectRequest.json");
    fSelectResponse = new File(TEST_DATA_DIR, "SelectResponse.json");
    fDecission = new File(TEST_DATA_DIR, "Decission.json");
    reBaseUrl = ResolutionEngineClientRestImpl.DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL;
    // parse parameters
    for (final String param : args) {
      final String lower = param.toLowerCase();
      if ("-f".equals(lower) || "-overwrite".equals(lower)) {
        doOverWrite = true;
      }
      else if ("-noinit".equals(lower)) {
        doInvokeInit = false;
      }
      else if ("-noselect".equals(lower)) {
        doInvokeSelect = false;
      }
      else if ("-nosession".equals(lower)) {
        doUseSessionId = false;
      }
      else if (lower.startsWith("-initresp=")) {
        fInitResponse = new File(TEST_DATA_DIR, param.substring(10));
      }
      else if (lower.startsWith("-currentresp=")) {
        fCurrentResponse = new File(TEST_DATA_DIR, param.substring(13));
      }
      else if (lower.startsWith("-selectreq=")) {
        fSelectRequest = new File(TEST_DATA_DIR, param.substring(11));
      }
      else if (lower.startsWith("-selectresp=")) {
        fSelectResponse = new File(TEST_DATA_DIR, param.substring(12));
      }
      else if (lower.startsWith("-decission=")) {
        fDecission = new File(TEST_DATA_DIR, param.substring(11));
      }
      else if (lower.startsWith("-url=")) {
        reBaseUrl = param.substring(5);
      }
      else {
        throw new IllegalArgumentException("Unknown Parameter " + param);
      }
    }
  }

  private static void invokeServices() throws JsonProcessingException, IOException {
    final ResolutionEngineClient client = createResolutionEngineClient();
    final ResolutionResponse iresp = invokeInitService(client);
    final ResolutionResponse sresp = invokeSelectService(client, iresp);
    invokeCurrentService(client, sresp);
  }

  // ----------------------------------------------------------------

  @SuppressWarnings("rawtypes")
  private static ResolutionEngineClient createResolutionEngineClient() {
    LOGGER.info("Creating WebClient for {}", reBaseUrl);
    final List<Object> providers = new ArrayList<Object>();
    providers.add(new JacksonJsonProvider());
    providers.add(new JAXBElementProvider());
    final WebClientFactory fact = new WebClientFactoryImpl(providers);
    return new ResolutionEngineClientRestImpl(fact, reBaseUrl);
  }

  private static ResolutionResponse invokeInitService(final ResolutionEngineClient client) throws JsonProcessingException, IOException {
    ResolutionResponse iresp = null;
    if (doInvokeInit) {
      LOGGER.info("Invoking Init-Service ...");
      iresp = client.invokeInitService();
      LOGGER.info(" ... done. Response = {}", iresp);
      if (doOverWrite || !fInitResponse.exists()) {
        JSONHelper.writeObjectToJsonFile(fInitResponse, iresp);
      }
    }
    else {
      iresp = JSONHelper.readObjectFromJsonFile(fInitResponse, ResolutionResponse.class);
    }
    return (iresp == null ? new ResolutionResponse() : iresp);
  }

  private static ResolutionResponse invokeSelectService(final ResolutionEngineClient client, final ResolutionResponse iresp) throws JsonProcessingException, IOException {
    ResolutionResponse sresp = null;
    if (doInvokeSelect) {
      LOGGER.info("Invoking Select-Service ...");
      final ResolutionRequest sreq = getSelectRequest(iresp);
      LOGGER.info(" ... Request = {}", sreq);
      sresp = client.invokeSelectService(sreq);
      LOGGER.info(" ... done. Response = {}", sresp);
      if (doOverWrite || !fSelectResponse.exists()) {
        JSONHelper.writeObjectToJsonFile(fSelectResponse, sresp);
      }
    }
    else {
      sresp = JSONHelper.readObjectFromJsonFile(fSelectResponse, ResolutionResponse.class);
    }
    return (sresp == null ? new ResolutionResponse() : sresp);
  }

  private static ResolutionResponse invokeCurrentService(final ResolutionEngineClient client, final ResolutionResponse sresp) throws JsonProcessingException, IOException {
    ResolutionResponse cresp = null;
    if (doInvokeCurrent) {
      LOGGER.info("Invoking Current-Service ...");
      final String sessionID = (sresp == null ? null : sresp.getSessionID());
      cresp = client.invokeCurrentService(sessionID);
      LOGGER.info(" ... done. Response = {}", cresp);
      if (doOverWrite || !fCurrentResponse.exists()) {
        JSONHelper.writeObjectToJsonFile(fCurrentResponse, cresp);
      }
    }
    else {
      cresp = JSONHelper.readObjectFromJsonFile(fCurrentResponse, ResolutionResponse.class);
    }
    return (cresp == null ? new ResolutionResponse() : cresp);
  }

  // ----------------------------------------------------------------

  private static ResolutionRequest getSelectRequest(final ResolutionResponse iresp) throws JsonProcessingException, IOException {
    ResolutionRequest sreq = JSONHelper.readObjectFromJsonFile(fSelectRequest, ResolutionRequest.class);
    final String sessionID = getSessionID(iresp);
    if (sreq == null) {
      LOGGER.debug("File {} not found, creating default Select-Request.", fSelectRequest);
      final Metadata metadata = getMetadata(iresp);
      final Decission decission = getDecission(iresp);
      sreq = new ResolutionRequest(sessionID, metadata, null, decission);
      JSONHelper.writeObjectToJsonFile(fSelectRequest, sreq);
    }
    if (doUseSessionId) {
      sreq.setSessionID(sessionID);
    }
    return sreq;
  }

  private static String getSessionID(final ResolutionResponse iresp) {
    final String sessionID = (iresp == null ? null : iresp.getSessionID());
    return (StringUtils.isEmpty(sessionID) ? String.valueOf(System.currentTimeMillis()) : sessionID);
  }

  private static Metadata getMetadata(final ResolutionResponse iresp) {
    return (iresp == null ? null : iresp.getMetadata());
  }

  private static Decission getDecission(final ResolutionResponse iresp) throws JsonProcessingException, IOException {
    VariantDecission vd = null;
    try {
      vd = JSONHelper.readObjectFromJsonFile(fDecission, VariantDecission.class);
    }
    catch (final Exception ex) {
      vd = null;
    }
    if (vd == null) {
      Purpose p = null;
      Variant v = null;
      LOGGER.debug("File {} not found, creating VariantDecission from first Choice.", fDecission);
      for (final Choice c : iresp.getChoices()) {
        if (c instanceof VariantChoice) {
          final VariantChoice vc = (VariantChoice) c;
          p = vc.getPurpose();
          v = vc.getVariants().get(0);
          vd = new VariantDecission(p, v);
          break;
        }
      }
      if (vd == null) {
        LOGGER.debug("No Variant-Choice in IResp, fallback to Dummy-Values!");// probably no variant-choice in iresp, fallback
        p = new Purpose("P1", "P1", "P1", true);
        v = new Variant("V1", "V1", "V1");
        vd = new VariantDecission(p, v);
      }
      try {
        JSONHelper.writeObjectToJsonFile(fDecission, vd);
      }
      catch (final Exception ex) {
        // ignore
      }
    }
    return vd;
  }
}
