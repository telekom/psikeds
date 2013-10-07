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
package org.psikeds.resolutionengine.cache;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * Cache holding all information regarding the current Resolution/Session
 *
 * Note: The actual Cache is a Map<String, Object> holding all data.
 *
 *       By default a LimitedHashMap is used, but you can plug in other
 *       implementation or settings you like.
 *
 * @author marco@juliano.de
 *
 */
public class ResolutionCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionCache.class);

  private Map<String, Object> cache;

  public ResolutionCache() {
    this(new LimitedHashMap<String, Object>());
  }

  public ResolutionCache(final Map<String, Object> map) {
    this.cache = map;
  }

  public void saveSessionData(final String sessionID, final Object data) {
    saveSessionData(sessionID, null, data);
  }

  public void saveSessionData(final String sessionID, final String key, final Object data) {
    if (this.cache != null) {
      final String cachekey = constructCacheKey(sessionID, key);
      if (!StringUtils.isEmpty(cachekey)) {
        LOGGER.trace("IN: {} = {}", cachekey, data);
        if (!(data instanceof Serializable)) {
          LOGGER.warn("Not a serializable Object: {}", cachekey);
        }
        this.cache.put(cachekey, data);
      }
    }
  }

  public Object getSessionData(final String sessionID) {
    return getSessionData(sessionID, null);
  }

  public Object getSessionData(final String sessionID, final String key) {
    Object data = null;
    if (this.cache != null) {
      final String cachekey = constructCacheKey(sessionID, key);
      if (!StringUtils.isEmpty(cachekey)) {
        data = this.cache.get(cachekey);
        LOGGER.trace("OUT: {} = {}", cachekey, data);
      }
    }
    return data;
  }

  public Object removeSessionData(final String sessionID) {
    return removeSessionData(sessionID, null);
  }

  public Object removeSessionData(final String sessionID, final String key) {
    Object data = null;
    if (this.cache != null) {
      final String cachekey = constructCacheKey(sessionID, key);
      if (!StringUtils.isEmpty(cachekey)) {
        data = this.cache.remove(cachekey);
        LOGGER.trace("DEL: {} = {}", cachekey, data);
      }
    }
    return data;
  }

  public void clear() {
    if (this.cache != null) {
      this.cache.clear();
      LOGGER.trace("Cache cleared.");
    }
  }

  public boolean isEmpty() {
    return this.cache == null || this.cache.isEmpty();
  }

  public int size() {
    return this.cache == null ? 0 : this.cache.size();
  }

  @Override
  protected void finalize() throws Throwable {
    this.clear();
    this.cache = null;
  }

  private String constructCacheKey(final String primekey, final String subkey) {
    if (StringUtils.isEmpty(subkey)) {
      return primekey;
    }
    if (StringUtils.isEmpty(primekey)) {
      return subkey;
    }
    final StringBuilder sb = new StringBuilder(primekey);
    sb.append('.');
    sb.append(subkey);
    return sb.toString();
  }
}
