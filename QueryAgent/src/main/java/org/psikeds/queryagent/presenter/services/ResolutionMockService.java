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
package org.psikeds.queryagent.presenter.services;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.queryagent.interfaces.presenter.pojos.Alternatives;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constituents;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constitutes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Events;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.Fulfills;
import org.psikeds.queryagent.interfaces.presenter.pojos.InitResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purposes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Rules;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;

/**
 * Mock-Business-Service for Testing purpose.
 *
 * You can plug this Class into the REST/SOAP-Service if you want to test the
 * QueryAgent only, without actually invoking the ResolutionEngine.
 *
 * See: QueryAgent/src/main/webapp/WEB-INF/spring-config/business-services-context.xml
 *
 * @author marco@juliano.de
 */
public class ResolutionMockService implements ResolutionService {

  private InitResponse initResp;
  private SelectResponse selResp;
  private KnowledgeEntity ke;
  private IdGenerator gen;

  public ResolutionMockService() {
    final Purposes p = new Purposes();
    p.addPurpose(new Purpose("P1", "", "P1"));
    p.addPurpose(new Purpose("P2", "", "P2"));
    p.addPurpose(new Purpose("P3", "", "P3"));

    final Variants v = new Variants();
    v.addVariant(new Variant("V1", "", "V1"));
    v.addVariant(new Variant("V2", "", "V2"));
    v.addVariant(new Variant("V3", "", "V3"));

    final Alternatives a = new Alternatives();
    a.addFulfills(new Fulfills("", "P1", "V1"));
    a.addFulfills(new Fulfills("", "P2", "V2"));
    a.addFulfills(new Fulfills("", "P3", "V3"));

    final Constituents c = new Constituents();
    c.addConstitutes(new Constitutes("", "V1", "P1"));
    c.addConstitutes(new Constitutes("", "V2", "P2"));
    c.addConstitutes(new Constitutes("", "V3", "P3"));

    final Events e = null;
    final Features f = null;
    final Rules r = null;

    this.ke = new KnowledgeEntity(f, p, v, a, c, e, r, false);
    this.initResp = createInitResponse();
    this.selResp = createSelectResponse();

    try {
      this.gen = new SessionIdGenerator("MOCK");
    }
    catch (final Exception ex) {
      this.gen = null;
    }
  }

  public ResolutionMockService(final InitResponse initResp, final SelectResponse selResp, final KnowledgeEntity ke, final IdGenerator gen) {
    this.initResp = initResp;
    this.selResp = selResp;
    this.ke = ke;
    this.gen = gen;
  }

  public InitResponse getInitResp() {
    return this.initResp;
  }

  public void setInitResp(final InitResponse initResp) {
    this.initResp = initResp;
  }

  public SelectResponse getSelResp() {
    return this.selResp;
  }

  public void setSelResp(final SelectResponse selResp) {
    this.selResp = selResp;
  }

  public KnowledgeEntity getKe() {
    return this.ke;
  }

  public void setKe(final KnowledgeEntity ke) {
    this.ke = ke;
  }

  public IdGenerator getGen() {
    return this.gen;
  }

  public void setGen(final IdGenerator gen) {
    this.gen = gen;
  }

  // ---------------------------------------------------

  /**
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#init()
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
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#select(org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest)
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

  // ---------------------------------------------------

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
