/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.common.services.AbstractRESTService;
import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate;
import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist;
import org.psikeds.queryagent.interfaces.presenter.services.ChocolateService;

/**
 * REST Service "implementing" the ChocolateService. It does not really
 * implement this Interface but invokes a delegate implementation the Interface
 * {@link org.psikeds.queryagent.interfaces.presenter.services.ChocolateService}
 * 
 * @author marco@juliano.de
 * 
 */
@Path("/chocolate")
public class ChocolateRESTService extends AbstractRESTService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChocolateRESTService.class);

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private final ChocolateService delegate;

    @Autowired
    public ChocolateRESTService(final ChocolateService delegate) {
        super();
        this.delegate = delegate;
        this.context = null;
    }

    // -----------------------------------------------------

    @GET
    @Path("/list")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getChocolates(final String reqid) {
        try {
            final Chocolatelist lst = (Chocolatelist) handleRequest(reqid, getExecutable(this.delegate, "getChocolates"));
            if (lst != null && lst.size() > 0) {
                return buildResponse(Status.OK, lst);
            }
            return buildResponse(Status.SERVICE_UNAVAILABLE, "No Chocolates available!");
        }
        catch (final Exception ex) {
            return buildResponse(ex);
        }
    }

    @POST
    @Path("/select")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN })
    public Response selectChocolate(final String refid, final String reqid) {
        try {
            final Chocolate choco = (Chocolate) handleRequest(reqid, getExecutable(this.delegate, "selectChocolate"), refid);
            if (choco != null) {
                return buildResponse(Status.OK, choco);
            }
            return buildResponse(Status.BAD_REQUEST, "There is no Chocolate with refid = " + refid);
        }
        catch (final Exception ex) {
            return buildResponse(ex);
        }
    }

    @PUT
    @Path("/add")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response addChocolate(final Chocolate choco, final String reqid) {
        try {
            final Chocolatelist lst = (Chocolatelist) handleRequest(reqid, getExecutable(this.delegate, "addChocolate"), choco);
            if (lst != null && lst.size() > 0) {
                return buildResponse(Status.OK, lst);
            }
            return buildResponse(Status.SERVICE_UNAVAILABLE, "Could not add new Chocolate");
        }
        catch (final Exception ex) {
            return buildResponse(ex);
        }
    }
}
