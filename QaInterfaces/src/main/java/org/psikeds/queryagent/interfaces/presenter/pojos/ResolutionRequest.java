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
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request-Object representing the current Context of a Resolution
 * sent by the Client/Browser to the Server/Query-Agent.
 * 
 * A Request usually contains either a SessionID or the current Knowledge
 * and a List of 0 to n Decissions.
 * 
 * If there is a SessionID, the Knowledge cached on Server-side is used.
 * The Knowledge passed by the Client can/will be used if Resolution-State
 * is not cached on the Server (any more) and must be (re)created.
 * 
 * If there is neither a SessionID nor Knowledge specified a new Resolution
 * is started and the initial Knowledge is created from the Knowledge-Base.
 * 
 * Optionally additional Metadata can be added. Metadate could contain
 * Information regarding Language-Settings or special Features desired
 * by the Client.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ResolutionRequest")
public class ResolutionRequest extends BaseResolutionContext implements Serializable {

  private static final long serialVersionUID = 1L;

  private Decissions decissions;

  public ResolutionRequest() {
    this((String) null);
  }

  public ResolutionRequest(final String sessionID) {
    this(sessionID, null, null, (Decissions) null);
  }

  public ResolutionRequest(final Decission decission) {
    this(null, null, null, decission);
  }

  public ResolutionRequest(final Decissions decissions) {
    this(null, null, null, decissions);
  }

  public ResolutionRequest(final String sessionID, final Decission decission) {
    this(sessionID, null, null, decission);
  }

  public ResolutionRequest(final String sessionID, final Decissions decissions) {
    this(sessionID, null, null, decissions);
  }

  public ResolutionRequest(final Knowledge knowledge, final Decission decission) {
    this(null, null, knowledge, decission);
  }

  public ResolutionRequest(final Knowledge knowledge, final Decissions decissions) {
    this(null, null, knowledge, decissions);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Decission decission) {
    this(sessionID, metadata, null, decission);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Decissions decissions) {
    this(sessionID, metadata, null, decissions);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Knowledge knowledge, final Decission decission) {
    this(sessionID, metadata, knowledge, (Decissions) null);
    addDecission(decission);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Knowledge knowledge, final Decissions decissions) {
    super(sessionID, metadata, knowledge);
    setDecissions(decissions);
  }

  public Decissions getDecissions() {
    if (this.decissions == null) {
      this.decissions = new Decissions();
    }
    return this.decissions;
  }

  public void setDecissions(final Decissions decissions) {
    clearDecissions();
    this.decissions = decissions;
  }

  public void addDecission(final Decission decission) {
    if (decission != null) {
      getDecissions().add(decission);
    }
  }

  public void addAllDecissions(final Decissions decissions) {
    if ((decissions != null) && !decissions.isEmpty()) {
      getDecissions().addAll(decissions);
    }
  }

  public void clearDecissions() {
    if (this.decissions != null) {
      this.decissions.clear();
      this.decissions = null;
    }
  }
}
