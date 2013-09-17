/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.resolutionengine.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * Cache holding all information regarding a current Resolution/Session
 *
 * @author marco@juliano.de
 *
 */
public class ResolutionCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionCache.class);

  private Map<String, Object> cache;

  public ResolutionCache() {
    this(new ConcurrentHashMap<String, Object>());
  }

  public ResolutionCache(final Map<String, Object> map) {
    this.cache = map;
    LOGGER.debug("Data is cached in " + this.cache);
  }

  public void saveSessionData(final String sessionID, final Object data) {
    if (this.cache != null && !StringUtils.isEmpty(sessionID)) {
      LOGGER.trace("saveSessionData, IN: {} = {}", sessionID, data);
      this.cache.put(sessionID, data);
    }
  }

  public Object getSessionData(final String sessionID) {
    final Object data = this.cache == null || StringUtils.isEmpty(sessionID) ? null : this.cache.get(sessionID);
    LOGGER.trace("getSessionData, OUT: {} = {}", sessionID, data);
    return data;
  }

  public Object removeSessionData(final String sessionID) {
    final Object data = this.cache == null || StringUtils.isEmpty(sessionID) ? null : this.cache.remove(sessionID);
    LOGGER.trace("removeSessionData, DEL: {} = {}", sessionID, data);
    return data;
  }

  public void clear() {
    if (this.cache != null) {
      this.cache.clear();
      LOGGER.debug("All data removed from Cache " + this.cache);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    this.clear();
    this.cache = null;
  }
}
