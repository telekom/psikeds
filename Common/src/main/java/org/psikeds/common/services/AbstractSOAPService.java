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
package org.psikeds.common.services;

import javax.ws.rs.core.Context;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.continuations.ContinuationProvider;

import org.springframework.beans.factory.InitializingBean;

/**
 * Base for all SOAP-Services.
 * 
 * @author marco@juliano.de
 */
public abstract class AbstractSOAPService extends AbstractBaseService implements InitializingBean {

  @Context
  protected WebServiceContext context;

  public AbstractSOAPService() {
    super();
    this.context = null;
  }

  public AbstractSOAPService(final boolean asyncSupported) {
    super(asyncSupported);
    this.context = null;
  }

  public AbstractSOAPService(final long suspensionTimeout) {
    super(suspensionTimeout);
    this.context = null;
  }

  public AbstractSOAPService(final boolean asyncSupported, final long suspensionTimeout) {
    super(asyncSupported, suspensionTimeout);
    this.context = null;
  }

  /**
   * @see org.psikeds.common.services.AbstractBaseService#getContinuationProvider()
   */
  @Override
  protected ContinuationProvider getContinuationProvider() {
    ContinuationProvider prov = null;
    try {
      getLogger().trace("--> getContinuationProvider()");
      final MessageContext ctx = this.context == null ? null : this.context.getMessageContext();
      final String key = ContinuationProvider.class.getName();
      getLogger().debug("Getting {} from {}", key, ctx);
      prov = (ctx == null ? null : (ContinuationProvider) ctx.get(key));
      return prov;
    }
    finally {
      getLogger().trace("<-- getContinuationProvider(); prov = {}", prov);
    }
  }
}
