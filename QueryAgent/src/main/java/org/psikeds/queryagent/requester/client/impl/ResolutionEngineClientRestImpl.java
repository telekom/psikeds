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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.requester.client.WebClientFactory;
import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;

/**
 * Implementation of ResolutionEngineClient using REST.
 * 
 * @author marco@juliano.de
 */
public class ResolutionEngineClientRestImpl extends AbstractBaseClient implements ResolutionEngineClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionEngineClientRestImpl.class);

  public static final String DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL = "http://localhost:8080/resolutionengine/services/rest/";
  public static final String DEFAULT_INIT_SERVICE_SUB_CTX = "resolution/init";
  public static final String DEFAULT_SELECT_SERVICE_SUB_CTX = "resolution/select";
  public static final String DEFAULT_INIT_SERVICE_REST_URL = DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL + DEFAULT_INIT_SERVICE_SUB_CTX;
  public static final String DEFAULT_SELECT_SERVICE_REST_URL = DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL + DEFAULT_SELECT_SERVICE_SUB_CTX;

  public static final String DEFAULT_INIT_SERVICE_METHOD = "GET";
  public static final String DEFAULT_SELECT_SERVICE_METHOD = "POST";

  private String initServiceUrl;
  private String initServiceMethod;
  private String selectServiceUrl;
  private String selectServiceMethod;

  public ResolutionEngineClientRestImpl() {
    this(null);
  }

  public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory) {
    this(clientFactory,
        DEFAULT_INIT_SERVICE_REST_URL, DEFAULT_INIT_SERVICE_METHOD,
        DEFAULT_SELECT_SERVICE_REST_URL, DEFAULT_SELECT_SERVICE_METHOD);
  }

  public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory, final String reBaseUrl) {
    this(clientFactory,
        reBaseUrl + DEFAULT_INIT_SERVICE_SUB_CTX, DEFAULT_INIT_SERVICE_METHOD,
        reBaseUrl + DEFAULT_SELECT_SERVICE_SUB_CTX, DEFAULT_SELECT_SERVICE_METHOD);
  }

  public ResolutionEngineClientRestImpl(final String initServiceUrl, final String initServiceMethod,
      final String selectServiceUrl, final String selectServiceMethod) {
    this(null, initServiceUrl, initServiceMethod, selectServiceUrl, selectServiceMethod);
  }

  public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory,
      final String initServiceUrl, final String initServiceMethod,
      final String selectServiceUrl, final String selectServiceMethod) {
    super(clientFactory);
    this.initServiceUrl = initServiceUrl;
    this.initServiceMethod = initServiceMethod;
    this.selectServiceUrl = selectServiceUrl;
    this.selectServiceMethod = selectServiceMethod;
  }

  // ------------------------------------------------------

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

  //------------------------------------------------------

  /**
   * @return InitResponse
   * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeInitService()
   */
  @Override
  public InitResponse invokeInitService() {
    return invokeService(this.initServiceUrl, this.initServiceMethod, InitResponse.class);
  }

  /**
   * @param req SelectRequest
   * @return SelectResponse
   * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeSelectService(org.psikeds.resolutionengine.interfaces.pojos.SelectRequest)
   */
  @Override
  public SelectResponse invokeSelectService(final SelectRequest req) {
    return invokeService(this.selectServiceUrl, this.selectServiceMethod, req, SelectRequest.class, SelectResponse.class);
  }

  /**
   * @return Logger
   * @see org.psikeds.queryagent.requester.client.impl.AbstractBaseClient#getLogger()
   */
  @Override
  protected Logger getLogger() {
    return LOGGER;
  }
}
