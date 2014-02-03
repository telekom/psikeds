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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request-Object representing the current Context of a Resolution
 * sent by the Client to the Server.
 * 
 * A Request usually contains either a SessionID or the current Knowledge
 * and a List of 0 to n Decissions.
 * 
 * If there is a SessionID, the Knowledged cached on Server-side is used.
 * The Knowledge passed by the Client can/will be used if Resolution-State
 * is not cached on the Server (any more) and mustr be (re)created.
 * 
 * If there is neither a SessionID nor Knowledge specified a new Resolution
 * is started and the initial Knowledge is created from the Knowledge-Base.
 * 
 * Optionally additional Metadata can be added. Metadate could contain
 * Information regarding Language-Settings or special Features desired
 * by the Client.
 * 
 * Note: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ResolutionRequest")
public class ResolutionRequest extends BaseResolutionContext implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Decission> decissions;

  public ResolutionRequest() {
    this((String) null);
  }

  public ResolutionRequest(final String sessionID) {
    this(sessionID, null, null);
  }

  public ResolutionRequest(final Decission decission) {
    this(null, null, null, decission);
  }

  public ResolutionRequest(final List<Decission> decissions) {
    this(null, null, null, decissions);
  }

  public ResolutionRequest(final String sessionID, final Decission decission) {
    this(sessionID, null, null, decission);
  }

  public ResolutionRequest(final String sessionID, final List<Decission> decissions) {
    this(sessionID, null, null, decissions);
  }

  public ResolutionRequest(final Knowledge knowledge, final Decission decission) {
    this(null, null, knowledge, decission);
  }

  public ResolutionRequest(final Knowledge knowledge, final List<Decission> decissions) {
    this(null, null, knowledge, decissions);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Knowledge knowledge) {
    this(sessionID, metadata, knowledge, (List<Decission>) null);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Knowledge knowledge, final Decission decission) {
    this(sessionID, metadata, knowledge);
    addMadeDecission(decission);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Knowledge knowledge, final List<Decission> decissions) {
    super(sessionID, metadata, knowledge);
    setMadeDecissions(decissions);
  }

  public List<Decission> getMadeDecissions() {
    if (this.decissions == null) {
      this.decissions = new ArrayList<Decission>();
    }
    return this.decissions;
  }

  public void setMadeDecissions(final List<Decission> decissions) {
    this.decissions = decissions;
  }

  public void addMadeDecission(final Decission decission) {
    if (decission != null) {
      getMadeDecissions().add(decission);
    }
  }
}
