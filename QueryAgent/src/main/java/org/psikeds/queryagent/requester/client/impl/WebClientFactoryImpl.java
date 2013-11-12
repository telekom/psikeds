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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.requester.client.WebClientFactory;

/**
 * Factory creating and caching CXF-WebClients for a given URL.
 * 
 * @author marco@juliano.de
 */
public class WebClientFactoryImpl implements WebClientFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebClientFactoryImpl.class);

  public static final boolean DEFAULT_CACHE_CLIENTS = true;
  public static final String DEFAULT_ACCEPT_HEADER = MediaType.APPLICATION_JSON;
  public static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON;
  public static final String DEFAULT_USER_AGENT = ResolutionEngineClient.class.getName();

  private final Map<String, WebClient> clients = new ConcurrentHashMap<String, WebClient>();
  private String acceptHeader;
  private String contentTypeHeader;
  private String userAgent;
  private List<Object> providers;
  private boolean cacheClients;

  public WebClientFactoryImpl() {
    this(null);
  }

  public WebClientFactoryImpl(final List<Object> providers) {
    this(DEFAULT_ACCEPT_HEADER, DEFAULT_CONTENT_TYPE, providers);
  }

  public WebClientFactoryImpl(final String acceptHeader, final String contentTypeHeader) {
    this(acceptHeader, contentTypeHeader, null);
  }

  public WebClientFactoryImpl(final String acceptHeader, final String contentTypeHeader, final List<Object> providers) {
    this(acceptHeader, contentTypeHeader, null, DEFAULT_USER_AGENT, DEFAULT_CACHE_CLIENTS);
  }

  public WebClientFactoryImpl(final String acceptHeader, final String contentTypeHeader, final List<Object> providers, final String userAgent, final boolean cacheClients) {
    this.acceptHeader = acceptHeader;
    this.contentTypeHeader = contentTypeHeader;
    this.providers = providers;
    this.userAgent = userAgent;
    this.cacheClients = cacheClients;
  }

  // ------------------------------------------------------

  public String getAcceptHeader() {
    return this.acceptHeader;
  }

  public void setAcceptHeader(final String acceptHeader) {
    this.acceptHeader = acceptHeader;
  }

  public String getContentTypeHeader() {
    return this.contentTypeHeader;
  }

  public void setContentTypeHeader(final String contentTypeHeader) {
    this.contentTypeHeader = contentTypeHeader;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public void setUserAgent(final String userAgent) {
    this.userAgent = userAgent;
  }

  public List<Object> getProviders() {
    return this.providers;
  }

  public void setProviders(final List<Object> providers) {
    this.providers = providers;
  }

  public boolean isCacheClients() {
    return this.cacheClients;
  }

  public void setCacheClients(final boolean cacheClients) {
    this.cacheClients = cacheClients;
  }

  /**
   * Create a new CXF-WebClient for the Target-URL using the Factory-Default-Settings
   * 
   * @param url
   *          Target-URL
   * @return WebClient
   */
  @Override
  public WebClient getClient(final String url) {
    return getClient(url, null, null, null);
  }

  /**
   * Create a new CXF-WebClient for the Target-URL
   * 
   * @param url
   *          Target-URL
   * @param accept
   *          HTTP-Accept-Header
   * @param content
   *          HTTP-Content-Type-Header
   * @param agent
   *          USer-Agent-String
   * @return WebClient
   */
  @Override
  public WebClient getClient(final String url, String accept, String content, String agent) {
    WebClient wc = this.cacheClients ? this.clients.get(url) : null;
    if (wc != null) {
      LOGGER.debug("Reusing cached WebClient: {}", wc);
      return wc;
    }

    final int size = this.providers == null ? 0 : this.providers.size();
    if (size > 0) {
      LOGGER.trace("Creating new WebClient for {} using {} providers.", url, size);
      // if cache client, we also must be thread safe!
      wc = WebClient.create(url, this.providers, this.cacheClients);
    }
    else {
      LOGGER.trace("Creating new WebClient for {} without any providers.", url);
      // if cache client, we also must be thread safe!
      wc = WebClient.create(url, this.cacheClients);
    }

    // set accept header if specified
    if (StringUtils.isEmpty(accept)) {
      accept = this.acceptHeader;
    }
    if (!StringUtils.isEmpty(accept)) {
      LOGGER.trace("{} = {}", HttpHeaders.ACCEPT, accept);
      wc.accept(accept);
    }
    // set content type if specified
    if (StringUtils.isEmpty(content)) {
      content = this.contentTypeHeader;
    }
    if (!StringUtils.isEmpty(content)) {
      LOGGER.trace("{} = {}", HttpHeaders.CONTENT_TYPE, content);
      wc.type(content);
    }
    // set user agent string if specified
    if (StringUtils.isEmpty(agent)) {
      agent = this.userAgent;
    }
    if (!StringUtils.isEmpty(agent)) {
      LOGGER.trace("{} = {}", HttpHeaders.USER_AGENT, agent);
      wc.replaceHeader(HttpHeaders.USER_AGENT, agent);
    }

    if (this.cacheClients) {
      LOGGER.trace("Caching WebClient for later reuse.");
      this.clients.put(url, wc);
    }
    LOGGER.debug("Created new WebClient: {}", wc);
    return wc;
  }
}
