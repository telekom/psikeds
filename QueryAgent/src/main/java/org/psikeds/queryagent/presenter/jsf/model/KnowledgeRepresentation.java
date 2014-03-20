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
package org.psikeds.queryagent.presenter.jsf.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.pojos.Choices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Errors;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warnings;

/**
 * The Frontend Representation/Model of our current Knowledge;
 * usually cached within Session-Scope.
 * 
 * @author marco@juliano.de
 */
public class KnowledgeRepresentation implements Serializable {

  private static final long serialVersionUID = 1L;

  ResolutionResponse lastResponse;

  public KnowledgeRepresentation() {
    this(null);
  }

  public KnowledgeRepresentation(final ResolutionResponse lastResponse) {
    this.lastResponse = lastResponse;
  }

  public ResolutionResponse getLastResponse() {
    return this.lastResponse;
  }

  public void setLastResponse(final ResolutionResponse lastResponse) {
    this.lastResponse = lastResponse;
  }

  // ----------------------------------------------------------------

  public Metadata getMetadata() {
    return getLastResponse().getMetadata();
  }

  public Knowledge getKnowledge() {
    return getLastResponse().getKnowledge();
  }

  public Warnings getWarnings() {
    return getLastResponse().getWarnings();
  }

  public Errors getErrors() {
    return getLastResponse().getErrors();
  }

  public Choices getChoices() {
    return getLastResponse().getChoices();
  }

  // ----------------------------------------------------------------

  public boolean isResolved() {
    return (this.lastResponse != null) && this.lastResponse.isResolved();
  }

  public boolean hasErrors() {
    return (this.lastResponse == null) || this.lastResponse.hasErrors();
  }

  public boolean hasWarnings() {
    return (this.lastResponse != null) && this.lastResponse.hasWarnings();
  }

  public String getSessionID() {
    return (this.lastResponse == null ? null : this.lastResponse.getSessionID());
  }

  public boolean isNotInitialized() {
    return (StringUtils.isEmpty(getSessionID()) || (getKnowledge() == null));
  }
}
