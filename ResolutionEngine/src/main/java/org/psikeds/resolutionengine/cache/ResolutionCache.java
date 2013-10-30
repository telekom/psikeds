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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * The ResolutionCache is holding all information regarding the current
 * Resolutions / Sessions.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionCache extends LimitedHashMap<String, CacheEntry> {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionCache.class);

  public static final int DEFAULT_MAX_SESSIONS_PER_SERVER = LimitedHashMap.DEFAULT_MAX_MAP_SIZE;
  public static final int DEFAULT_MAX_OBJECTS_PER_SESSION = CacheEntry.DEFAULT_MAX_OBJECTS_PER_SESSION;

  private int maxObjectsPerSession;

  public ResolutionCache() {
    this(DEFAULT_MAX_SESSIONS_PER_SERVER, DEFAULT_MAX_OBJECTS_PER_SESSION);
  }

  public ResolutionCache(final int maxSessionsPerServer, final int maxObjectsPerSession) {
    super(maxSessionsPerServer);
    this.maxObjectsPerSession = maxObjectsPerSession;
  }

  // ----------------------------------------------------------------

  public int getMaxObjectsPerSession() {
    return this.maxObjectsPerSession;
  }

  public void setMaxObjectsPerSession(final int maxObjectsPerSession) {
    this.maxObjectsPerSession = maxObjectsPerSession;
  }

  public int getMaxSessionsPerServer() {
    return getMaxSize();
  }

  public void setMaxSessionsPerServer(final int maxSessionsPerServer) {
    setMaxSize(maxSessionsPerServer);
  }

  // ----------------------------------------------------------------

  public CacheEntry getSession(final String sessionID, final boolean create) {
    CacheEntry sessionData = null;
    try {
      if (!StringUtils.isEmpty(sessionID)) {
        sessionData = get(sessionID);
        if ((sessionData == null) && create) {
          sessionData = new CacheEntry(this.maxObjectsPerSession);
          saveSession(sessionID, sessionData);
        }
      }
      return sessionData;
    }
    finally {
      LOGGER.trace("getSession: {} = {}", sessionID, sessionData);
    }
  }

  public void saveSession(final String sessionID, final CacheEntry sessionData) {
    try {
      if ((sessionData != null) && !StringUtils.isEmpty(sessionID)) {
        sessionData.setMaxObjectsPerSession(this.maxObjectsPerSession);
        put(sessionID, sessionData);
      }
    }
    finally {
      LOGGER.trace("saveSession: {} = {}", sessionID, sessionData);
    }
  }

  public CacheEntry removeSession(final String sessionID) {
    CacheEntry sessionData = null;
    try {
      if (!StringUtils.isEmpty(sessionID)) {
        sessionData = remove(sessionID);
      }
      return sessionData;
    }
    finally {
      LOGGER.trace("removeSession: {} = {}", sessionID, sessionData);
    }
  }

  // ----------------------------------------------------------------

  public Serializable getObject(final String sessionID, final String key) {
    Serializable obj = null;
    try {
      final CacheEntry sessionData = getSession(sessionID, false);
      if ((sessionData != null) && !StringUtils.isEmpty(key)) {
        obj = sessionData.get(key);
      }
      return obj;
    }
    finally {
      LOGGER.trace("getObject: {} , {} = {}", sessionID, key, obj);
    }
  }

  public void saveObject(final String sessionID, final String key, final Serializable obj) {
    try {
      if ((obj != null) && !StringUtils.isEmpty(key)) {
        final CacheEntry sessionData = getSession(sessionID, true);
        if (sessionData != null) {
          sessionData.put(key, obj);
          saveSession(sessionID, sessionData);
        }
      }
    }
    finally {
      LOGGER.trace("saveObject: {} , {}, {}", sessionID, key, obj);
    }
  }

  public Serializable removeObject(final String sessionID, final String key) {
    Serializable obj = null;
    try {
      final CacheEntry sessionData = getSession(sessionID, false);
      if ((sessionData != null) && !StringUtils.isEmpty(key)) {
        obj = sessionData.remove(key);
        saveSession(sessionID, sessionData);
      }
      return obj;
    }
    finally {
      LOGGER.trace("removeObject: {} , {} = {}", sessionID, key, obj);
    }
  }
}
