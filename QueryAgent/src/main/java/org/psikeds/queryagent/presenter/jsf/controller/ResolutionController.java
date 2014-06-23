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
package org.psikeds.queryagent.presenter.jsf.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;
import org.psikeds.queryagent.presenter.jsf.model.KnowledgeRepresentation;
import org.psikeds.queryagent.presenter.jsf.util.Constants;
import org.psikeds.queryagent.presenter.jsf.util.SelectionHelper;

/**
 * JSF-Controller for handling all Interactions regarding Knowledge Resolution.
 * 
 * @author marco@juliano.de
 */
public class ResolutionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionController.class);

  private ResolutionService service;
  private KnowledgeRepresentation model;

  private String selected;

  public ResolutionController() {
    this(null, null);
  }

  public ResolutionController(final ResolutionService service, final KnowledgeRepresentation model) {
    this.service = service;
    this.model = model;
    this.selected = null;
  }

  public ResolutionService getService() {
    return this.service;
  }

  public void setService(final ResolutionService service) {
    this.service = service;
  }

  public KnowledgeRepresentation getModel() {
    return this.model;
  }

  public void setModel(final KnowledgeRepresentation model) {
    this.model = model;
  }

  // ------------------------------------------------------

  public String home() {
    return Constants.RESULT_HOME;
  }

  public String back() {
    return Constants.RESULT_BACK;
  }

  public String choices() {
    return Constants.RESULT_CHOICES;
  }

  public String apppkgs() {
    return Constants.RESULT_APPPKGS;
  }

  // ------------------------------------------------------

  public String resume() {
    if ((this.model == null) || (this.service == null)) {
      LOGGER.error("Model or Service missing! Check configuration of JSF- and Spring-Beans.");
      return Constants.RESULT_ERROR;
    }
    if (this.model.isNotInitialized()) {
      return init();
    }
    return Constants.RESULT_SUCCESS;
  }

  public String init() {
    String ret = Constants.RESULT_ERROR;
    ResolutionResponse resp = null;
    try {
      LOGGER.trace("--> init()");
      resp = this.service.init();
      this.model.setLastResponse(resp);
      this.selected = null;
      ret = Constants.RESULT_SUCCESS;
    }
    catch (final Exception ex) {
      ret = Constants.RESULT_ERROR;
      LOGGER.error("init() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- init(); ret = {}\nresp = {}", ret, resp);
    }
    return ret;
  }

  // ------------------------------------------------------

  public String getSelected() {
    return this.selected;
  }

  public void setSelected(final String selected) {
    this.selected = selected;
  }

  public String select() {
    String ret = Constants.RESULT_ERROR;
    ResolutionRequest req = null;
    ResolutionResponse resp = null;
    try {
      LOGGER.trace("--> select( {} )", this.selected);
      final String sessionId = this.model.getSessionID();
      final Decission decission = SelectionHelper.getDecissionFromString(this.selected);
      req = new ResolutionRequest(sessionId, decission);
      resp = getService().select(req);
      this.model.setLastResponse(resp);
      this.selected = null;
      ret = Constants.RESULT_SUCCESS;
    }
    catch (final Exception ex) {
      ret = Constants.RESULT_ERROR;
      LOGGER.error("select() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- select(); ret = {}\nreq = {}\nresp = {}", ret, req, resp);
    }
    return ret;
  }

  // Requires Servlet 3.0 / EL 2.2: #{Ctrl.resolve(di.selectionKey)}
  public String resolve(final String selected) {
    setSelected(selected);
    return select();
  }

  // Requires Servlet 3.0 / EL 2.2: #{Ctrl.resolve(di.selectionKey)}
  public String predict(final String selected) {
    setSelected(selected);
    return predict();
  }

  public String predict() {
    String ret = Constants.RESULT_ERROR;
    ResolutionRequest req = null;
    ResolutionResponse resp = null;
    try {
      LOGGER.trace("--> predict( {} )", this.selected);
      final String sessionId = this.model.getSessionID();
      final Decission decission = SelectionHelper.getDecissionFromString(this.selected);
      req = new ResolutionRequest(sessionId, decission);
      resp = getService().predict(req);
      this.model.setPrediction(resp);
      this.selected = null;
      ret = Constants.RESULT_PREDICT;
    }
    catch (final Exception ex) {
      ret = Constants.RESULT_ERROR;
      LOGGER.error("predict() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- predict(); ret = {}\nreq = {}\nresp = {}", ret, req, resp);
    }
    return ret;
  }
}
