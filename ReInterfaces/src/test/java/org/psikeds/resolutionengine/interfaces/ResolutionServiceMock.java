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
import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;

/**
 * Mock-Implementation of ResolutionService for testing purposes.
 *
 * @author marco@juliano.de
 *
 */
public class ResolutionServiceMock implements ResolutionService {

  private InitResponse initResp;
  private SelectResponse selResp;
  private final KnowledgeEntity ke;
  private IdGenerator gen;

  public ResolutionServiceMock(final KnowledgeEntity ke) {
    this(null, null, ke, null);
  }

  public ResolutionServiceMock(final InitResponse initResp, final SelectResponse selResp) {
    this(initResp, selResp, null, null);
  }

  public ResolutionServiceMock(final InitResponse initResp, final SelectResponse selResp, final KnowledgeEntity ke) {
    this(initResp, selResp, ke, null);
  }

  public ResolutionServiceMock(final InitResponse initResp, final SelectResponse selResp, final KnowledgeEntity ke, final IdGenerator gen) {
    this.initResp = initResp;
    this.selResp = selResp;
    this.ke = ke;
    try {
      this.gen = gen == null ? new SessionIdGenerator("TEST") : gen;
    }
    catch (final Exception ex) {
      this.gen = null;
    }
  }

  /**
   * @return InitResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#init()
   */
  @Override
  public InitResponse init() {
    if (this.initResp == null) {
      this.initResp = createInitResponse();
    }
    if (this.initResp.getSessionID() == null) {
      this.initResp.setSessionID(createSessionID());
    }
    if (this.initResp.getKnowledgeEntity() == null) {
      this.initResp.setKnowledgeEntity(this.ke);
    }
    return this.initResp;
  }

  /**
   * @param req
   * @return SelectResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.SelectRequest)
   */
  @Override
  public SelectResponse select(final SelectRequest req) {
    if (this.selResp == null) {
      this.selResp = createSelectResponse();
    }
    if (this.selResp.getSessionID() == null) {
      this.selResp.setSessionID(req.getSessionID());
    }
    if (this.selResp.getSessionID() == null) {
      this.selResp.setSessionID(createSessionID());
    }
    if (this.selResp.getKnowledgeEntity() == null) {
      this.selResp.setKnowledgeEntity(this.ke);
    }
    if (this.selResp.getKnowledgeEntity() == null) {
      this.selResp.setKnowledgeEntity(req.getKnowledgeEntity());
    }
    return this.selResp;
  }

  private SelectResponse createSelectResponse() {
    final SelectResponse resp = new SelectResponse();
    resp.setKnowledgeEntity(this.ke);
    return resp;
  }

  private InitResponse createInitResponse() {
    final InitResponse resp = new InitResponse();
    resp.setKnowledgeEntity(this.ke);
    return resp;
  }

  private String createSessionID() {
    return this.gen == null ? null : this.gen.getNextId();
  }
}
