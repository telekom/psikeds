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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.queryagent.requester.client.impl.ResolutionEngineClientRestImpl;
import org.psikeds.queryagent.requester.client.impl.WebClientFactoryImpl;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;

/**
 * Simple Client for invoking Services of Resolution-Engine
 * (i.e. perform manually what the Query-Agent would do)
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionEngineClientTool {

  private static final String LOG4J = "./src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionEngineClientTool.class);

  private static final String TEST_DATA_DIR = "./src/test/resources/";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static boolean doInvokeInit = true;
  private static boolean doInvokeSelect = true;
  private static boolean doUseSessionId = true;
  private static boolean doOverWrite = false;
  private static File fInitResponse = new File(TEST_DATA_DIR, "InitResponse.json");
  private static File fSelectRequest = new File(TEST_DATA_DIR, "SelectRequest.json");
  private static File fSelectResponse = new File(TEST_DATA_DIR, "SelectResponse.json");
  private static File fDecission = new File(TEST_DATA_DIR, "Decission.json");
  private static String reBaseUrl = ResolutionEngineClientRestImpl.DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL;

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
    DOMConfigurator.configure(LOG4J);
  }

  private static void parseParameters(final String[] args) {
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
    invokeSelectService(client, iresp);
  }

  // ------------------------------------------------------

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
        writeObjectToJsonFile(fInitResponse, iresp);
      }
    }
    else {
      iresp = readObjectFromJsonFile(fInitResponse, ResolutionResponse.class);
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
        writeObjectToJsonFile(fSelectResponse, sresp);
      }
    }
    else {
      sresp = readObjectFromJsonFile(fSelectResponse, ResolutionResponse.class);
    }
    return (sresp == null ? new ResolutionResponse() : sresp);
  }

  // ------------------------------------------------------

  private static ResolutionRequest getSelectRequest(final ResolutionResponse iresp) throws JsonProcessingException, IOException {
    ResolutionRequest sreq = readObjectFromJsonFile(fSelectRequest, ResolutionRequest.class);
    final String sessionID = getSessionID(iresp);
    if (sreq == null) {
      LOGGER.debug("File {} not found, creating default Select-Request.", fSelectRequest);
      final Metadata metadata = getMetadata(iresp);
      final Decission decission = getDecission(iresp);
      sreq = new ResolutionRequest(sessionID, metadata, null, decission);
      writeObjectToJsonFile(fSelectRequest, sreq);
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
    Decission decission = readObjectFromJsonFile(fDecission, Decission.class);
    if (decission == null) {
      LOGGER.debug("File {} not found, creating Decission from first Choice.", fDecission);
      Purpose p;
      Variant v;
      try {
        final Choice c = iresp.getPossibleChoices().get(0);
        p = c.getPurpose();
        v = c.getVariants().get(0);
      }
      catch (final Exception ex) {
        // probably no choice in iresp, fallback
        p = new Purpose("P1", "P1", "P1", true);
        v = new Variant("V1", "V1", "V1");
      }
      decission = new Decission(p, v);
      writeObjectToJsonFile(fDecission, decission);
    }
    return decission;
  }

  // ------------------------------------------------------

  private static <T> T readObjectFromJsonFile(final File f, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if ((type != null) && (f != null) && f.isFile() && f.exists() && f.canRead()) {
      obj = MAPPER.readValue(f, type);
      LOGGER.debug("Read Object from File {}\n{}", f, obj);
    }
    return obj;
  }

  private static void writeObjectToJsonFile(final File f, final Object obj) throws JsonProcessingException, IOException {
    if ((f != null) && (obj != null)) {
      LOGGER.info("Writing Object to File {}\n{}", f, obj);
      MAPPER.writeValue(f, obj);
      if (!f.exists()) {
        throw new IOException("Could not write Object(s) to File " + f.getPath());
      }
    }
  }
}
