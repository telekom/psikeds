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
package org.psikeds.queryagent.requester.client.impl;

import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.requester.client.WebClientFactory;
import org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;

/**
 * Implementation of ResolutionEngineClient using the REST Interface.
 * 
 * @author marco@juliano.de
 */
public class ResolutionEngineClientRestImpl extends AbstractBaseClient implements ResolutionEngineClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionEngineClientRestImpl.class);

  public static final String DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL = "http://localhost:8080/resolutionengine/services/rest/";

  public static final String DEFAULT_INIT_SERVICE_SUB_CTX = "resolution/init";
  public static final String DEFAULT_CURRENT_SERVICE_SUB_CTX = "resolution/current";
  public static final String DEFAULT_SELECT_SERVICE_SUB_CTX = "resolution/select";
  public static final String DEFAULT_PREDICT_SERVICE_SUB_CTX = "resolution/predict";

  public static final String DEFAULT_INIT_SERVICE_METHOD = "GET";
  public static final String DEFAULT_CURRENT_SERVICE_METHOD = "GET";
  public static final String DEFAULT_SELECT_SERVICE_METHOD = "POST";
  public static final String DEFAULT_PREDICT_SERVICE_METHOD = "POST";

  private String initServiceUrl;
  private String initServiceMethod;
  private String currentServiceUrl;
  private String currentServiceMethod;
  private String selectServiceUrl;
  private String selectServiceMethod;
  private String predictServiceUrl;
  private String predictServiceMethod;

  public ResolutionEngineClientRestImpl() {
    this(null);
  }

  public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory) {
    this(clientFactory, DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL);
  }

  public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory, final String reBaseUrl) {
    this(clientFactory,
        reBaseUrl + DEFAULT_INIT_SERVICE_SUB_CTX, DEFAULT_INIT_SERVICE_METHOD,
        reBaseUrl + DEFAULT_CURRENT_SERVICE_SUB_CTX, DEFAULT_CURRENT_SERVICE_METHOD,
        reBaseUrl + DEFAULT_SELECT_SERVICE_SUB_CTX, DEFAULT_SELECT_SERVICE_METHOD,
        reBaseUrl + DEFAULT_PREDICT_SERVICE_SUB_CTX, DEFAULT_PREDICT_SERVICE_METHOD);
  }

  public ResolutionEngineClientRestImpl(
      final String initServiceUrl, final String initServiceMethod,
      final String currentServiceUrl, final String currentServiceMethod,
      final String selectServiceUrl, final String selectServiceMethod,
      final String predictServiceUrl, final String predictServiceMethod) {
    this(null, initServiceUrl, initServiceMethod, currentServiceUrl, selectServiceUrl, currentServiceMethod, selectServiceMethod, predictServiceUrl, predictServiceMethod);
  }

  public ResolutionEngineClientRestImpl(
      final WebClientFactory clientFactory,
      final String initServiceUrl, final String initServiceMethod,
      final String currentServiceUrl, final String currentServiceMethod,
      final String selectServiceUrl, final String selectServiceMethod,
      final String predictServiceUrl, final String predictServiceMethod) {
    super(clientFactory);
    this.initServiceUrl = initServiceUrl;
    this.initServiceMethod = initServiceMethod;
    this.currentServiceUrl = currentServiceUrl;
    this.currentServiceMethod = currentServiceMethod;
    this.selectServiceUrl = selectServiceUrl;
    this.selectServiceMethod = selectServiceMethod;
    this.predictServiceUrl = predictServiceUrl;
    this.predictServiceMethod = predictServiceMethod;
  }

  // ----------------------------------------------------------------

  public String getInitServiceUrl() {
    return this.initServiceUrl;
  }

  public void setInitServiceUrl(final String initServiceUrl) {
    this.initServiceUrl = initServiceUrl;
  }

  public String getInitServiceMethod() {
    return this.initServiceMethod;
  }

  public void setInitServiceMethod(final String initServiceMethod) {
    this.initServiceMethod = initServiceMethod;
  }

  public String getCurrentServiceUrl() {
    return this.currentServiceUrl;
  }

  public void setCurrentServiceUrl(final String currentServiceUrl) {
    this.currentServiceUrl = currentServiceUrl;
  }

  public String getCurrentServiceMethod() {
    return this.currentServiceMethod;
  }

  public void setCurrentServiceMethod(final String currentServiceMethod) {
    this.currentServiceMethod = currentServiceMethod;
  }

  public String getSelectServiceUrl() {
    return this.selectServiceUrl;
  }

  public void setSelectServiceUrl(final String selectServiceUrl) {
    this.selectServiceUrl = selectServiceUrl;
  }

  public String getSelectServiceMethod() {
    return this.selectServiceMethod;
  }

  public void setSelectServiceMethod(final String selectServiceMethod) {
    this.selectServiceMethod = selectServiceMethod;
  }

  public String getPredictServiceUrl() {
    return this.predictServiceUrl;
  }

  public void setPredictServiceUrl(final String predictServiceUrl) {
    this.predictServiceUrl = predictServiceUrl;
  }

  public String getPredictServiceMethod() {
    return this.predictServiceMethod;
  }

  public void setPredictServiceMethod(final String predictServiceMethod) {
    this.predictServiceMethod = predictServiceMethod;
  }

  // ----------------------------------------------------------------

  /**
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeInitService()
   */
  @Override
  public ResolutionResponse invokeInitService() {
    ResolutionResponse rr = null;
    try {
      LOGGER.trace("--> invokeInitService()");
      final Response resp = invokeService(this.initServiceUrl, this.initServiceMethod);
      if (isError(resp)) {
        rr = createErrorResponse(resp);
      }
      else {
        rr = getContent(resp, ResolutionResponse.class);
      }
    }
    catch (final Exception ex) {
      rr = createErrorResponse(ex);
    }
    LOGGER.trace("<-- invokeInitService(); Response = {}", rr);
    return rr;
  }

  /**
   * @param sessionID
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeSelectService(java.lang.String)
   */
  @Override
  public ResolutionResponse invokeCurrentService(final String sessionID) {
    ResolutionResponse rr = null;
    try {
      LOGGER.trace("--> invokeCurrentService( {} )", sessionID);
      final Response resp = invokeService(this.currentServiceUrl, this.currentServiceMethod, sessionID, String.class);
      if (isError(resp)) {
        rr = createErrorResponse(resp);
      }
      else {
        rr = getContent(resp, ResolutionResponse.class);
      }
    }
    catch (final Exception ex) {
      rr = createErrorResponse(ex);
    }
    LOGGER.trace("<-- invokeCurrentService( {} ); Response = {}", sessionID, rr);
    return rr;
  }

  /**
   * @param req
   *          ResolutionRequest
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeSelectService(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse invokeSelectService(final ResolutionRequest req) {
    ResolutionResponse rr = null;
    try {
      LOGGER.trace("--> invokeSelectService( {} )", req);
      final Response resp = invokeService(this.selectServiceUrl, this.selectServiceMethod, req, ResolutionRequest.class);
      if (isError(resp)) {
        rr = createErrorResponse(resp);
      }
      else {
        rr = getContent(resp, ResolutionResponse.class);
      }
    }
    catch (final Exception ex) {
      rr = createErrorResponse(ex);
    }
    LOGGER.trace("<-- invokeSelectService( ); Response = {}", rr);
    return rr;
  }

  /**
   * @param req
   *          ResolutionRequest
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokePredictionService(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse invokePredictionService(final ResolutionRequest req) {
    ResolutionResponse rr = null;
    try {
      LOGGER.trace("--> invokePredictionService( {} )", req);
      final Response resp = invokeService(this.predictServiceUrl, this.predictServiceMethod, req, ResolutionRequest.class);
      if (isError(resp)) {
        rr = createErrorResponse(resp);
      }
      else {
        rr = getContent(resp, ResolutionResponse.class);
      }
    }
    catch (final Exception ex) {
      rr = createErrorResponse(ex);
    }
    LOGGER.trace("<-- invokePredictionService( ); Response = {}", rr);
    return rr;
  }

  // ----------------------------------------------------------------

  /**
   * @return Logger
   * @see org.psikeds.queryagent.requester.client.impl.AbstractBaseClient#getLogger()
   */
  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  // ----------------------------------------------------------------

  private ResolutionResponse createErrorResponse(final Response resp) {
    final ResolutionResponse rr = new ResolutionResponse();
    final ErrorMessage em = new ErrorMessage(getStatusCode(resp), getReasonPhrase(resp));
    rr.addError(em);
    return rr;
  }

  private ResolutionResponse createErrorResponse(final Exception ex) {
    final ResolutionResponse rr = new ResolutionResponse();
    final int code;
    if (ex instanceof IllegalArgumentException) {
      code = Status.BAD_REQUEST.getStatusCode();
    }
    else if (ex instanceof IOException) {
      code = Status.SERVICE_UNAVAILABLE.getStatusCode();
    }
    else {
      code = Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
    final String message = ex.getMessage();
    final ErrorMessage em = new ErrorMessage(code, message);
    rr.addError(em);
    return rr;
  }
}
