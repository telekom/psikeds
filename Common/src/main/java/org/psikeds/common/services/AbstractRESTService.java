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

import java.io.InterruptedIOException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.continuations.ContinuationProvider;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.springframework.beans.factory.InitializingBean;

/**
 * Base for all REST-Services.
 * 
 * @author marco@juliano.de
 */
public abstract class AbstractRESTService extends AbstractBaseService implements InitializingBean {

  @Context
  protected MessageContext context;

  public AbstractRESTService() {
    super();
    this.context = null;
  }

  public AbstractRESTService(final boolean asyncSupported) {
    super(asyncSupported);
    this.context = null;
  }

  public AbstractRESTService(final long suspensionTimeout) {
    super(suspensionTimeout);
    this.context = null;
  }

  public AbstractRESTService(final boolean asyncSupported, final long suspensionTimeout) {
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
      final String key = ContinuationProvider.class.getName();
      getLogger().debug("Getting {} from {}", key, this.context);
      prov = (this.context == null ? null : (ContinuationProvider) this.context.get(key));
      return prov;
    }
    finally {
      getLogger().trace("<-- getContinuationProvider(); prov = {}", prov);
    }
  }

  // ------------------------------------------------------------------------
  // --- Helpers for building restful responses.
  // ------------------------------------------------------------------------

  protected Response buildResponse(final IllegalArgumentException iaex) {
    return buildResponse(Status.BAD_REQUEST, iaex.getMessage());
  }

  protected Response buildResponse(final SecurityException sex) {
    return buildResponse(Status.FORBIDDEN, sex.getMessage());
  }

  protected Response buildResponse(final InterruptedIOException ioex) {
    return buildResponse(Status.REQUEST_TIMEOUT, ioex.getMessage());
  }

  protected Response buildResponse(final InterruptedException iex) {
    return buildResponse(Status.REQUEST_TIMEOUT, iex.getMessage());
  }

  protected Response buildResponse(final Exception ex) {
    return buildResponse(Status.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  protected Response buildResponse(final Status stat) {
    return buildResponse(stat, null);
  }

  protected Response buildResponse(final Status stat, final Object obj) {
    if (obj == null) {
      return Response.status(stat).build();
    }
    else {
      if (obj instanceof Throwable) {
        return Response.status(stat).entity(((Throwable) obj).getMessage()).build();
      }
      else {
        return Response.status(stat).entity(obj).build();
      }
    }
  }
}
