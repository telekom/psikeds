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
package org.psikeds.resolutionengine.interfaces.rest;

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
import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;

/**
 * REST Service "implementing" the ResolutionService. It does not really
 * implement this Interface but invokes a delegate implementation the Interface
 * {@link org.psikeds.resolutionengine.interfaces.services.ResolutionService}
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
    this.delegate = delegate;
    this.context = null;
  }

  // -----------------------------------------------------

  @GET
  @Path("/init")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response init(final String reqid) {
    try {
      final InitResponse resp = (InitResponse) handleRequest(reqid, getExecutable(this.delegate, "init"));
      if (resp != null && resp.getKnowledgeEntity() != null && resp.getSessionID() != null) {
        return buildResponse(Status.OK, resp);
      }
      return buildResponse(Status.SERVICE_UNAVAILABLE, "Failed to initialize Session and to create Knowledge-Entity!");
    }
    catch (final Exception ex) {
      return buildResponse(ex);
    }
  }

  @POST
  @Path("/select")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response select(final SelectRequest req, final String reqid) {
    try {
      final SelectResponse resp = (SelectResponse) handleRequest(reqid, getExecutable(this.delegate, "select"), req);
      if (resp != null && resp.getKnowledgeEntity() != null && resp.getSessionID() != null) {
        return buildResponse(Status.OK, resp);
      }
      return buildResponse(Status.BAD_REQUEST, String.valueOf(req));
    }
    catch (final Exception ex) {
      return buildResponse(ex);
    }
  }
}
