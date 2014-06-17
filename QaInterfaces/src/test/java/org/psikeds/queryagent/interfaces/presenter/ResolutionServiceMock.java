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
package org.psikeds.queryagent.interfaces.presenter;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.ErrorMessage;
import org.psikeds.queryagent.interfaces.presenter.pojos.Errors;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warning;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;

/**
 * A simple Mock-Implementation of
 * {@link org.psikeds.queryagent.interfaces.presenter.services.ResolutionService}
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionServiceMock implements ResolutionService {

  private Knowledge lastReturnedKnowledge;
  private Knowledge initialKnowledge;
  private Knowledge selectVariantKnowledge;
  private Knowledge selectFeatureKnowledge;
  private Metadata metadata;
  private IdGenerator gen;

  public ResolutionServiceMock(final Knowledge knowledge) {
    this(knowledge, knowledge, knowledge, null);
  }

  public ResolutionServiceMock(final Knowledge initialKnowledge, final Knowledge selectVariantKnowledge, final Knowledge selectFeatureKnowledge, final Metadata metadata) {
    this(initialKnowledge, selectVariantKnowledge, selectFeatureKnowledge, metadata, null);
  }

  public ResolutionServiceMock(final Knowledge initialKnowledge, final Knowledge selectVariantKnowledge, final Knowledge selectFeatureKnowledge, final Metadata metadata, final IdGenerator gen) {
    this.initialKnowledge = initialKnowledge;
    this.lastReturnedKnowledge = initialKnowledge;
    this.selectVariantKnowledge = selectVariantKnowledge;
    this.selectFeatureKnowledge = selectFeatureKnowledge;
    this.metadata = metadata;
    setIdGenerator(gen);
  }

  // ------------------------------------------------------

  public Knowledge getInitialKnowledge() {
    return this.initialKnowledge;
  }

  public void setInitialKnowledge(final Knowledge initialKnowledge) {
    this.initialKnowledge = initialKnowledge;
  }

  public Knowledge getSelectVariantKnowledge() {
    return this.selectVariantKnowledge;
  }

  public void setSelectVariantKnowledge(final Knowledge selectVariantKnowledge) {
    this.selectVariantKnowledge = selectVariantKnowledge;
  }

  public Knowledge getSelectFeatureKnowledge() {
    return this.selectFeatureKnowledge;
  }

  public void setSelectFeatureKnowledge(final Knowledge selectFeatureKnowledge) {
    this.selectFeatureKnowledge = selectFeatureKnowledge;
  }

  public Metadata getMetadata() {
    return this.metadata;
  }

  public void setMetadata(final Metadata metadata) {
    this.metadata = metadata;
  }

  public IdGenerator getIdGenerator() {
    return this.gen;
  }

  public void setIdGenerator(final IdGenerator gen) {
    try {
      this.gen = gen == null ? new SessionIdGenerator("TEST") : gen;
    }
    catch (final Exception ex) {
      this.gen = null;
    }
  }

  // ------------------------------------------------------

  /**
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#init()
   */
  @Override
  public ResolutionResponse init() {
    this.lastReturnedKnowledge = this.initialKnowledge;
    final String sessionID = (this.gen == null ? null : this.gen.getNextId());
    return new ResolutionResponse(sessionID, this.metadata, this.initialKnowledge);
  }

  /**
   * @param sessionID
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#current(java.lang.String)
   */
  @Override
  public ResolutionResponse current(final String sessionID) {
    ResolutionResponse resp;
    if ((this.lastReturnedKnowledge == null) || StringUtils.isEmpty(sessionID)) {
      resp = init();
      resp.addWarning(new Warning("Cannot get current State. Fallback to initial Knowledge!"));
    }
    else {
      resp = new ResolutionResponse(sessionID, this.metadata, this.lastReturnedKnowledge);
    }
    return resp;
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    ResolutionResponse resp;
    final String sessionID = (req == null ? null : req.getSessionID());
    if (StringUtils.isEmpty(sessionID)) {
      resp = init();
      resp.addWarning(new Warning("No Session-ID! Fallback to initial Knowledge!"));
    }
    else {
      final List<Decission> lst = req.getDecissions();
      if ((lst == null) || lst.isEmpty()) {
        resp = current(sessionID);
        resp.addWarning(new Warning("No Decission! Returning current State of Resolution."));
      }
      else {
        // we only support single decissions in this mock
        final Decission decission = lst.get(0);
        // very simple: depending on the type of decission always return one of the knowledges
        if (decission instanceof VariantDecission) {
          this.lastReturnedKnowledge = this.selectVariantKnowledge;
          resp = new ResolutionResponse(sessionID, this.metadata, this.selectVariantKnowledge);
        }
        else if (decission instanceof FeatureDecission) {
          this.lastReturnedKnowledge = this.selectFeatureKnowledge;
          resp = new ResolutionResponse(sessionID, this.metadata, this.selectFeatureKnowledge);
        }
        else if (decission instanceof ConceptDecission) {
          resp = current(sessionID);
          resp.addWarning(new Warning("Concept-Decission is not supported by Mock! Returning current State of Resolution."));
        }
        else {
          // decission is probably null
          final Errors error = new Errors();
          error.add(new ErrorMessage(Status.BAD_REQUEST.getStatusCode(), "Request does not contain a valid Decission!"));
          resp = new ResolutionResponse(sessionID, this.metadata, error);
        }
      }
    }
    return resp;
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#predict(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse predict(final ResolutionRequest req) {
    return select(req);
  }
}
