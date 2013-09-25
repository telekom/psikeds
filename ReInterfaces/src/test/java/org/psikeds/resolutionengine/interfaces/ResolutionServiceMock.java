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

  private final Knowledge initKnow;
  private final Knowledge selectKnow;
  private IdGenerator gen;

  public ResolutionServiceMock(final Knowledge know) {
    this(know, know);
  }

  public ResolutionServiceMock(final Knowledge initKnow, final Knowledge selectKnow) {
    this(initKnow, selectKnow, null);
  }

  public ResolutionServiceMock(final Knowledge initKnow, final Knowledge selectKnow, final IdGenerator gen) {
    this.initKnow = initKnow;
    this.selectKnow = selectKnow;
    try {
      this.gen = gen == null ? new SessionIdGenerator("TEST") : gen;
    }
    catch (final Exception ex) {
      this.gen = null;
    }
  }

  /**
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#init()
   */
  @Override
  public ResolutionResponse init() {
    final ResolutionResponse resp = createResolutionResponse(null, this.initKnow);
    resp.calculateChoices();
    return resp;
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    final String sessionId = req == null ? null : req.getSessionID();
    final Knowledge know = req != null && req.getKnowledge() != null ? req.getKnowledge() : this.selectKnow;
    final ResolutionResponse resp = createResolutionResponse(sessionId, know);
    resp.calculateChoices();
    return resp;
  }

  private ResolutionResponse createResolutionResponse(final String sessionId, final Knowledge know) {
    final ResolutionResponse resp = new ResolutionResponse();
    if (sessionId != null) {
      resp.setSessionID(sessionId);
    }
    else {
      resp.setSessionID(createSessionID());
    }
    if (know != null) {
      resp.setKnowledge(know);
    }
    return resp;
  }

  private String createSessionID() {
    return this.gen == null ? null : this.gen.getNextId();
  }
}
