/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.resolutionengine.interfaces.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.interceptor.Fault;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.common.services.AbstractSOAPService;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolate;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist;
import org.psikeds.resolutionengine.interfaces.services.ChocolateService;

/**
 * SOAP Service "implementing" the ChocolateService. It does not really
 * implement this Interface but invokes a delegate implementation the Interface
 * {@link org.psikeds.resolutionengine.interfaces.services.ChocolateService}
 * 
 * @author marco@juliano.de
 */
@WebService(name = "ChocolateService", targetNamespace = "org.psikeds.resolutionengine", serviceName = "ChocolateService")
public class ChocolateSOAPService extends AbstractSOAPService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChocolateSOAPService.class);

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  private final ChocolateService delegate;

  @Autowired
  public ChocolateSOAPService(final ChocolateService delegate) {
    super();
    this.delegate = delegate;
    this.context = null;
  }

  @WebMethod(operationName = "list")
  @WebResult(name = "Chocolatelist")
  public Chocolatelist getChocolates(final String reqid) {
    try {
      final Chocolatelist lst = (Chocolatelist) handleRequest(reqid, getExecutable(this.delegate, "getChocolates"));
      if (lst == null || lst.size() <= 0) {
        throw new IllegalStateException("No Chocolates available!");
      }
      return lst;
    }
    catch (final Exception ex) {
      throw new Fault(ex);
    }
  }

  @WebMethod(operationName = "select")
  @WebResult(name = "Chocolate")
  public Chocolate selectChocolate(@WebParam(name = "refid") final String refid, final String reqid) {
    try {
      final Chocolate choco = (Chocolate) handleRequest(reqid, getExecutable(this.delegate, "selectChocolate"), refid);
      if (choco == null) {
        throw new IllegalArgumentException("There is no Chocolate with refid = " + refid);
      }
      return choco;
    }
    catch (final Exception ex) {
      throw new Fault(ex);
    }
  }

  @WebMethod(operationName = "add")
  @WebResult(name = "Chocolatelist")
  public Chocolatelist addChocolate(@WebParam(name = "chocolate") final Chocolate choco, final String reqid) {
    try {
      final Chocolatelist lst = (Chocolatelist) handleRequest(reqid, getExecutable(this.delegate, "addChocolate"), choco);
      if (lst == null || lst.size() <= 0) {
        throw new IllegalStateException("Could not add new Chocolate");
      }
      return lst;
    }
    catch (final Exception ex) {
      throw new Fault(ex);
    }
  }
}
