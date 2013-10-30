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

/**
 * A Cache-Entry is a Map of serializable Objects all related
 * to the corresponding Session.
 * 
 * @author marco@juliano.de
 * 
 */
public class CacheEntry extends LimitedHashMap<String, Serializable> {

  private static final long serialVersionUID = 1L;

  public static final int DEFAULT_MAX_OBJECTS_PER_SESSION = 8;

  public CacheEntry() {
    this(DEFAULT_MAX_OBJECTS_PER_SESSION);
  }

  public CacheEntry(final int maxObjectsPerSession) {
    super(maxObjectsPerSession);
  }

  public int getMaxObjectsPerSession() {
    return getMaxSize();
  }

  public void setMaxObjectsPerSession(final int maxObjectsPerSession) {
    setMaxSize(maxObjectsPerSession);
  }
}
