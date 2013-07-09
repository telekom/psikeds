/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import org.springframework.beans.factory.annotation.Autowired;

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

  public static final String USER_AGENT_HEADER_NAME = "user-agent";
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

  /**
   * @param acceptHeader
   * @param contentTypeHeader
   * @param providers
   * @param cacheClients
   */
  @Autowired
  public WebClientFactoryImpl(final List<Object> providers) {
    this(DEFAULT_ACCEPT_HEADER, DEFAULT_CONTENT_TYPE, DEFAULT_USER_AGENT, providers, DEFAULT_CACHE_CLIENTS);
  }

  /**
   * @param acceptHeader
   * @param contentTypeHeader
   */
  public WebClientFactoryImpl(final String acceptHeader, final String contentTypeHeader) {
    this(acceptHeader, contentTypeHeader, DEFAULT_USER_AGENT, null, DEFAULT_CACHE_CLIENTS);
  }

  /**
   * @param acceptHeader
   * @param contentTypeHeader
   * @param userAgent
   * @param providers
   * @param cacheClients
   */
  public WebClientFactoryImpl(final String acceptHeader, final String contentTypeHeader, final String userAgent, final List<Object> providers, final boolean cacheClients) {
    this.acceptHeader = acceptHeader;
    this.contentTypeHeader = contentTypeHeader;
    this.userAgent = userAgent;
    this.providers = providers;
    this.cacheClients = cacheClients;
  }

  /**
   * @param providers the providers to set
   */
  public void setProviders(final List<Object> providers) {
    this.providers = providers;
  }

  /**
   * @param acceptHeader the acceptHeader to set
   */
  public void setAcceptHeader(final String acceptHeader) {
    this.acceptHeader = acceptHeader;
  }

  /**
   * @param contentTypeHeader the contentTypeHeader to set
   */
  public void setContentTypeHeader(final String contentTypeHeader) {
    this.contentTypeHeader = contentTypeHeader;
  }

  /**
   * @param cacheClients the cacheClients to set
   */
  public void setCacheClients(final boolean cacheClients) {
    this.cacheClients = cacheClients;
  }

  /**
   * @param userAgent the userAgent to set
   */
  public void setUserAgent(final String userAgent) {
    this.userAgent = userAgent;
  }

  /**
   * @param url
   * @return
   * @see org.psikeds.queryagent.requester.client.WebClientFactory#getClient(java.lang.String)
   */
  @Override
  public WebClient getClient(final String url) {
    WebClient wc = this.clients.get(url);
    if (wc != null) {
      LOGGER.debug("Reusing cached WebClient: {}", String.valueOf(wc));
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
    if (!StringUtils.isEmpty(this.acceptHeader)) {
      wc.accept(this.acceptHeader);
    }
    if (!StringUtils.isEmpty(this.contentTypeHeader)) {
      wc.type(this.contentTypeHeader);
    }
    if (!StringUtils.isEmpty(this.userAgent)) {
      wc.replaceHeader(USER_AGENT_HEADER_NAME, this.userAgent);
    }
    if (this.cacheClients) {
      LOGGER.trace("Caching WebClient for later reuse.");
      this.clients.put(url, wc);
    }
    LOGGER.debug("Created new WebClient: {}", String.valueOf(wc));
    return wc;
  }
}
