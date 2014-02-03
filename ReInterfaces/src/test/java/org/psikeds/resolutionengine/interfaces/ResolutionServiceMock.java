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
package org.psikeds.resolutionengine.interfaces;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;

/**
 * Mock-Implementation of ResolutionService for testing purposes.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionServiceMock implements ResolutionService {

  private Knowledge initKnowledge;
  private Knowledge selectKnowledge;
  private IdGenerator gen;

  public ResolutionServiceMock(final Knowledge knowledge) {
    this(knowledge, knowledge);
  }

  public ResolutionServiceMock(final Knowledge initKnowledge, final Knowledge selectKnowledge) {
    this(initKnowledge, selectKnowledge, null);
  }

  public ResolutionServiceMock(final Knowledge initKnowledge, final Knowledge selectKnowledge, final IdGenerator gen) {
    setInitKnowledge(initKnowledge);
    setSelectKnowledge(selectKnowledge);
    setIdGenerator(gen);
  }

  // ------------------------------------------------------

  public Knowledge getInitKnowledge() {
    return this.initKnowledge;
  }

  public void setInitKnowledge(final Knowledge initKnowledge) {
    this.initKnowledge = initKnowledge;
  }

  public Knowledge getSelectKnowledge() {
    return this.selectKnowledge;
  }

  public void setSelectKnowledge(final Knowledge selectKnowledge) {
    this.selectKnowledge = selectKnowledge;
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
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#init()
   */
  @Override
  public ResolutionResponse init() {
    return new ResolutionResponse(createSessionID(), getInitKnowledge());
  }

  /**
   * @param sessionID
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#current(java.lang.String)
   */
  @Override
  public ResolutionResponse current(final String sessionID) {
    final ResolutionResponse resp = init();
    resp.setSessionID(sessionID);
    return resp;
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    String sessionId = (req == null ? null : req.getSessionID());
    if (StringUtils.isEmpty(sessionId)) {
      sessionId = createSessionID();
    }
    Knowledge know = (req == null ? null : req.getKnowledge());
    if (know == null) {
      know = getSelectKnowledge();
    }
    return new ResolutionResponse(sessionId, know);
  }

  // ------------------------------------------------------

  private String createSessionID() {
    return this.gen == null ? null : this.gen.getNextId();
  }
}
