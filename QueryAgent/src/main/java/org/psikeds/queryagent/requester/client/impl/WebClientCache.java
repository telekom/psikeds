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
package org.psikeds.queryagent.requester.client.impl;

import org.apache.cxf.jaxrs.client.WebClient;

import org.psikeds.common.cache.LimitedHashMap;

/**
 * The WebClientCache is holding all cached WebClients, i.e. Connections
 * to the Resolution-Engine.
 * 
 * @author marco@juliano.de
 * 
 */
public class WebClientCache extends LimitedHashMap<String, WebClient> {

  private static final long serialVersionUID = 1L;

  public static final int DEFAULT_MAX_CONNECTIONS_PER_SERVER = LimitedHashMap.DEFAULT_MAX_MAP_SIZE;

  public WebClientCache() {
    this(DEFAULT_MAX_CONNECTIONS_PER_SERVER);
  }

  public WebClientCache(final int maxConnectionsPerServer) {
    super(maxConnectionsPerServer);
  }

  public WebClientCache(final int maxConnectionsPerServer, final int initialCapacity, final float loadFactor, final boolean accessOrder) {
    super(maxConnectionsPerServer, initialCapacity, loadFactor, accessOrder);
  }

  public int getMaxConnectionsPerServer() {
    return getMaxSize();
  }

  public void setMaxConnectionsPerServer(final int maxConnectionsPerServer) {
    setMaxSize(maxConnectionsPerServer);
  }
}
