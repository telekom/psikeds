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
package org.psikeds.queryagent.interfaces.presenter.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.common.services.AbstractRESTService;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;

/**
 * REST Service "implementing" the ResolutionService. It does not really
 * implement this Interface but invokes a delegate implementation the Interface
 * {@link org.psikeds.queryagent.interfaces.presenter.services.ResolutionService}
 * 
 * @author marco@juliano.de
 */
@Path("/resolution")
public class ResolutionRESTService extends AbstractRESTService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionRESTService.class);

  @Override
  protected Logger getLogger() {
    return LOGGER;
  }

  private final ResolutionService delegate;

  public ResolutionRESTService(final ResolutionService delegate) {
    super();
    this.delegate = delegate;
  }

  public ResolutionRESTService(final ResolutionService delegate, final boolean asyncSupported) {
    super(asyncSupported);
    this.delegate = delegate;
  }

  public ResolutionRESTService(final ResolutionService delegate, final long suspensionTimeout) {
    super(suspensionTimeout);
    this.delegate = delegate;
  }

  public ResolutionRESTService(final ResolutionService delegate, final boolean asyncSupported, final long suspensionTimeout) {
    super(asyncSupported, suspensionTimeout);
    this.delegate = delegate;
  }

  // -----------------------------------------------------

  @GET
  @Path("/init")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response init(final String reqid) {
    try {
      final ResolutionResponse resp = (ResolutionResponse) handleRequest(reqid, getExecutable(this.delegate, "init"));
      if ((resp != null) && (resp.getKnowledge() != null) && (resp.getSessionID() != null)) {
        return buildResponse(Status.OK, resp);
      }
      return buildResponse(Status.SERVICE_UNAVAILABLE, "Failed to create Session and to initialize Resolution!");
    }
    catch (final Exception ex) {
      return buildResponse(ex);
    }
  }

  @GET
  @Path("/current")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response current(final String sessionID, final String reqid) {
    try {
      if (sessionID == null) {
        return buildResponse(Status.BAD_REQUEST, "No Session-ID!");
      }
      final ResolutionResponse resp = (ResolutionResponse) handleRequest(reqid, getExecutable(this.delegate, "current"), sessionID);
      if ((resp != null) && (resp.getKnowledge() != null) && (resp.getSessionID() != null)) {
        return buildResponse(Status.OK, resp);
      }
      return buildResponse(Status.BAD_REQUEST, String.valueOf(sessionID));
    }
    catch (final Exception ex) {
      return buildResponse(ex);
    }
  }

  @POST
  @Path("/select")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response select(final ResolutionRequest req, final String reqid) {
    try {
      if ((req == null) || ((req.getSessionID() == null) && (req.getKnowledge() == null))) {
        return buildResponse(Status.BAD_REQUEST, String.valueOf(req));
      }
      final ResolutionResponse resp = (ResolutionResponse) handleRequest(reqid, getExecutable(this.delegate, "select"), req);
      if ((resp != null) && (resp.getKnowledge() != null) && (resp.getSessionID() != null)) {
        return buildResponse(Status.OK, resp);
      }
      return buildResponse(Status.BAD_REQUEST, String.valueOf(req));
    }
    catch (final Exception ex) {
      return buildResponse(ex);
    }
  }
}
