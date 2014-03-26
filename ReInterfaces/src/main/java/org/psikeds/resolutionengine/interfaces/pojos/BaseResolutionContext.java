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

import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * Base-Object representing the current Context of a Resolution.
 * This is the essential data structure exchanged by Client and Server,
 * i.e. the Client will send a derived ResolutionRequest-Object and the
 * Server will answer with a derived ResolutionResponse-Object.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlSeeAlso({ ResolutionRequest.class, ResolutionResponse.class })
@JsonSubTypes({ @JsonSubTypes.Type(value = ResolutionRequest.class, name = "ResolutionRequest"), @JsonSubTypes.Type(value = ResolutionResponse.class, name = "ResolutionResponse"), })
public abstract class BaseResolutionContext extends POJO {

  private static final long serialVersionUID = 1L;

  protected Metadata metadata;
  protected Knowledge knowledge;

  protected BaseResolutionContext() {
    this(null, null, null);
  }

  protected BaseResolutionContext(final String sessionID, final Knowledge knowledge) {
    this(sessionID, null, knowledge);
  }

  protected BaseResolutionContext(final String sessionID, final Metadata metadata, final Knowledge knowledge) {
    super(sessionID);
    this.metadata = metadata;
    this.knowledge = knowledge;
  }

  public Metadata getMetadata() {
    return this.metadata;
  }

  public void setMetadata(final Metadata metadata) {
    this.metadata = metadata;
  }

  public Knowledge getKnowledge() {
    return this.knowledge;
  }

  public void setKnowledge(final Knowledge knowledge) {
    this.knowledge = knowledge;
  }

  public String getSessionID() {
    return getId();
  }

  public void setSessionID(final String sessionID) {
    setId(sessionID);
  }
}
