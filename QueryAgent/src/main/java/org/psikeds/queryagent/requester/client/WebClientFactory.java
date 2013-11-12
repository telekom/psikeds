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

import org.apache.cxf.jaxrs.client.WebClient;

/**
 * Factory creating CXF-WebClients for a given URL.
 * 
 * @author marco@juliano.de
 */
public interface WebClientFactory {

  /**
   * Create a new CXF-WebClient for the Target-URL using the Factory-Default-Settings
   * 
   * @param url
   *          Target-URL
   * @return WebClient
   */
  WebClient getClient(String url);

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
  WebClient getClient(String url, String accept, String content, String agent);
}
