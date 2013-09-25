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
package org.psikeds.resolutionengine.interfaces.pojos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base-Object representing the current Context of a Resolution.
 * This is the essential data structure exchanged by Client and Server,
 * i.e. the Client will send a derived ResolutionRequest-Object and the
 * Server will answer with a derived ResolutionResponse-Object.
 *
 * @author marco@juliano.de
 *
 */
public class BaseResolutionContext extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String sessionID;
  protected Map<String, Object> metadata;
  protected Knowledge knowledge;

  protected BaseResolutionContext() {
    this(null, null, null);
  }

  protected BaseResolutionContext(final String sessionID, final Knowledge knowledge) {
    this(sessionID, null, knowledge);
  }

  protected BaseResolutionContext(final String sessionID, final Map<String, Object> metadata, final Knowledge knowledge) {
    super();
    this.sessionID = sessionID;
    this.metadata = metadata;
    this.knowledge = knowledge;
  }

  public String getSessionID() {
    return this.sessionID;
  }

  public void setSessionID(final String sessionID) {
    this.sessionID = sessionID;
  }

  public Map<String, Object> getMetadata() {
    if (this.metadata == null) {
      this.metadata = new HashMap<String, Object>();
    }
    return this.metadata;
  }

  public void setMetadata(final Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public Knowledge getKnowledge() {
    return this.knowledge;
  }

  public void setKnowledge(final Knowledge knowledge) {
    this.knowledge = knowledge;
  }
}
