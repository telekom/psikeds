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
package org.psikeds.queryagent.interfaces.presenter.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.interceptor.Fault;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.common.services.AbstractSOAPService;
import org.psikeds.queryagent.interfaces.presenter.pojos.InitResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectResponse;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;

/**
 * SOAP Service "implementing" the ResolutionService. It does not really
 * implement this Interface but invokes a delegate implementation the Interface
 * {@link org.psikeds.queryagent.interfaces.presenter.services.ResolutionService}
 *
 * @author marco@juliano.de
 */
@WebService(name = "ResolutionService", targetNamespace = "org.psikeds.queryagent.soap", serviceName = "ResolutionService")
public class ResolutionSOAPService extends AbstractSOAPService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionSOAPService.class);

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  private final ResolutionService delegate;

  @Autowired
  public ResolutionSOAPService(final ResolutionService delegate) {
    super();
    this.delegate = delegate;
    this.context = null;
  }

  @WebMethod(operationName = "doInit")
  @WebResult(name = "InitResponse", targetNamespace = "org.psikeds.queryagent.soap")
  public InitResponse doInit(final String reqid) {
    try {
      final InitResponse resp = (InitResponse) handleRequest(reqid, getExecutable(this.delegate, "init"));
      if (resp == null || resp.getKnowledgeEntity() == null || resp.getSessionID() == null) {
        throw new IllegalStateException("Failed to initialize Session and to create Knowledge-Entity!");
      }
      return resp;
    }
    catch (final Exception ex) {
      throw new Fault(ex);
    }
  }

  @WebMethod(operationName = "doSelect")
  @WebResult(name = "SelectResponse", targetNamespace = "org.psikeds.queryagent.soap")
  public SelectResponse doSelect(@WebParam(name = "SelectRequest") final SelectRequest req, final String reqid) {
    try {
      final SelectResponse resp = (SelectResponse) handleRequest(reqid, getExecutable(this.delegate, "select"), req);
      if (resp == null || resp.getKnowledgeEntity() == null || resp.getSessionID() == null) {
        throw new IllegalArgumentException(String.valueOf(req));
      }
      return resp;
    }
    catch (final Exception ex) {
      throw new Fault(ex);
    }
  }
}
