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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.requester.client.WebClientFactory;

/**
 * Factory creating and caching CXF-WebClients for a given URL.
 * 
 * @author marco@juliano.de
 */
public class WebClientFactoryImpl implements InitializingBean, WebClientFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebClientFactoryImpl.class);

  public static final String DEFAULT_ACCEPT_HEADER = MediaType.APPLICATION_JSON;
  public static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON;
  public static final String DEFAULT_USER_AGENT = ResolutionEngineClient.class.getName();

  private String acceptHeader;
  private String contentTypeHeader;
  private String userAgent;
  private boolean cacheClients;
  private List<Object> providers;
  private Map<String, WebClient> cache;

  public WebClientFactoryImpl() {
    this(null);
  }

  public WebClientFactoryImpl(final List<Object> providers) {
    this(null, providers);
  }

  public WebClientFactoryImpl(final Map<String, WebClient> cache, final List<Object> providers) {
    this(DEFAULT_ACCEPT_HEADER, DEFAULT_CONTENT_TYPE, DEFAULT_USER_AGENT, (cache != null), cache, providers);
  }

  public WebClientFactoryImpl(final String acceptHeader, final String contentTypeHeader, final String userAgent, final boolean cacheClients, final Map<String, WebClient> cache,
      final List<Object> providers) {
    this.acceptHeader = acceptHeader;
    this.contentTypeHeader = contentTypeHeader;
    this.userAgent = userAgent;
    this.cache = cache;
    this.providers = providers;
    this.cacheClients = cacheClients;
  }

  // ----------------------------------------------------------------

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

  public Map<String, WebClient> getCache() {
    return this.cache;
  }

  public void setCache(final Map<String, WebClient> cache) {
    this.cache = cache;
  }

  // ----------------------------------------------------------------

  @Override
  public void afterPropertiesSet() throws Exception {
    if (LOGGER.isInfoEnabled()) {
      final StringBuilder sb = new StringBuilder("Config: Default-Accept-Header: {}\n");
      sb.append("Default-Content-Type-Header: {}\n");
      sb.append("Default-User-Agent: {}\n");
      sb.append("Caching Clients: {}");
      if (this.cache == null) {
        sb.append("\nNo WebClient-Cache supplied.");
      }
      if ((this.providers == null) || this.providers.isEmpty()) {
        sb.append("\nNo JAX-RS-Providers defined.");
      }
      LOGGER.info(sb.toString(), this.acceptHeader, this.contentTypeHeader, this.userAgent, this.cacheClients);
    }
    Validate.isTrue(!((this.cache == null) && this.cacheClients), "WebClients shall be cached but no Cache-Implementation supplied!!");
  }

  // ----------------------------------------------------------------

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
    if (StringUtils.isEmpty(url)) {
      throw new IllegalArgumentException("Cannot create WebClient without URL!");
    }
    // get web client from cache
    WebClient wc = (this.cacheClients ? this.cache.get(url) : null);
    if (wc != null) {
      LOGGER.debug("Reusing cached WebClient for URL {}", url);
    }
    else {
      // if caching clients, we also must be thread safe!
      final boolean threadsafe = this.cacheClients;
      final int size = this.providers == null ? 0 : this.providers.size();
      if (size > 0) {
        LOGGER.debug("Creating new WebClient for URL {} using {} JAX-RS-Providers.", url, size);
        wc = WebClient.create(url, this.providers, threadsafe);
      }
      else {
        LOGGER.debug("Creating new WebClient for URL {} without any JAX-RS-Provider.", url);
        wc = WebClient.create(url, threadsafe);
      }
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
      LOGGER.trace("Caching WebClient for URL {} for later reuse: {}", url, wc);
      this.cache.put(url, wc);
    }
    return wc;
  }
}
