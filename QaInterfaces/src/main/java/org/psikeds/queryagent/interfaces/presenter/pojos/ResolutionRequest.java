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
 * sent by the Client to the Server.
 * 
 * A Request must contain a SessionID and a Decission. Optionally
 * additional Metadata and the current Knowledge can be added. The
 * later is required if the Session was already expired and the
 * Resolution-State must be recreated in the Backend (Resolution-Engine).
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

  private Decission decission;

  public ResolutionRequest() {
    this(null, null, null, null);
  }

  public ResolutionRequest(final String sessionID, final Decission decission) {
    this(sessionID, null, null, decission);
  }

  public ResolutionRequest(final Knowledge knowledge, final Decission decission) {
    this(null, null, knowledge, decission);
  }

  public ResolutionRequest(final String sessionID, final Metadata metadata, final Knowledge knowledge, final Decission decission) {
    super(sessionID, metadata, knowledge);
    this.decission = decission;
  }

  public Decission getMadeDecission() {
    return this.decission;
  }

  public void setMadeDecission(final Decission decission) {
    this.decission = decission;
  }
}
