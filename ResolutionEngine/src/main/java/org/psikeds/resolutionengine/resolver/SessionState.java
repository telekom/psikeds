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
package org.psikeds.resolutionengine.resolver;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage;
import org.psikeds.resolutionengine.interfaces.pojos.Errors;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Warning;
import org.psikeds.resolutionengine.interfaces.pojos.Warnings;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;

/**
 * The current State of a Resolution-Session, i.e. this is a wrapper around
 * all Objects representing the current State.
 * 
 * @author marco@juliano.de
 * 
 */
public class SessionState {

  private String sessionID;
  private Metadata metadata;
  private Knowledge knowledge;
  private Warnings warnings;
  private Errors errors;
  private RulesAndEventsHandler raeh;

  public SessionState(final String sessionID, final Metadata metadata, final Knowledge knowledge, final RulesAndEventsHandler raeh) {
    this.sessionID = sessionID;
    this.metadata = metadata;
    this.knowledge = knowledge;
    this.warnings = null;
    this.errors = null;
    this.raeh = raeh;
  }

  // ----------------------------------------------------------------

  public String getSessionID() {
    return this.sessionID;
  }

  public void setSessionID(final String sessionID) {
    this.sessionID = sessionID;
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

  public RulesAndEventsHandler getRaeh() {
    return this.raeh;
  }

  public void setRaeh(final RulesAndEventsHandler raeh) {
    this.raeh = raeh;
  }

  // ----------------------------------------------------------------

  public Warnings getWarnings() {
    if (this.warnings == null) {
      this.warnings = new Warnings();
    }
    return this.warnings;
  }

  public void setWarnings(final Warnings warnings) {
    clearWarnings();
    this.warnings = warnings;
  }

  public void addWarning(final Warning warning) {
    if (warning != null) {
      getWarnings().add(warning);
    }
  }

  public void addWarning(final String message) {
    if (!StringUtils.isEmpty(message)) {
      addWarning(new Warning(message));
    }
  }

  public void clearWarnings() {
    if (this.warnings != null) {
      this.warnings.clear();
      this.warnings = null;
    }
  }

  // ----------------------------------------------------------------

  public Errors getErrors() {
    if (this.errors == null) {
      this.errors = new Errors();
    }
    return this.errors;
  }

  public void setErrors(final Errors errors) {
    clearErrors();
    this.errors = errors;
  }

  public void addError(final ErrorMessage error) {
    if (error != null) {
      getErrors().add(error);
    }
  }

  public void addError(final Throwable t) {
    if (t != null) {
      getErrors().add(new ErrorMessage(t));
    }
  }

  public void addError(final String message) {
    if (!StringUtils.isEmpty(message)) {
      getErrors().add(new ErrorMessage(message));
    }
  }

  public void clearErrors() {
    if (this.errors != null) {
      this.errors.clear();
      this.errors = null;
    }
  }

  // ----------------------------------------------------------------

  public ResolutionResponse createResolutionResponse() {
    return new ResolutionResponse(this.sessionID, this.metadata, this.knowledge, this.errors, this.warnings);
  }
}
