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
package org.psikeds.queryagent.requester.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.requester.client.WebClientFactory;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolate;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist;

/**
 * Implementation of ResolutionEngineClient using REST.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionEngineClientRestImpl extends AbstractBaseClient implements ResolutionEngineClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionEngineClientRestImpl.class);

    public static final String DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL = "http://localhost:8080/resolutionengine/services/rest/";
    public static final String DEFAULT_LIST_SERVICE_REST_URL = DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL + "chocolate/list";
    public static final String DEFAULT_SELECT_SERVICE_REST_URL = DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL + "chocolate/select";
    public static final String DEFAULT_ADD_SERVICE_REST_URL = DEFAULT_RESOLUTION_ENGINE_BASE_REST_URL + "chocolate/add";

    public static final String DEFAULT_LIST_SERVICE_METHOD = "GET";
    public static final String DEFAULT_SELECT_SERVICE_METHOD = "POST";
    public static final String DEFAULT_ADD_SERVICE_METHOD = "PUT";

    private String listServiceUrl;
    private String listServiceMethod;
    private String selectServiceUrl;
    private String selectServiceMethod;
    private String addServiceUrl;
    private String addServiceMethod;

    public ResolutionEngineClientRestImpl() {
        this(null);
    }

    /**
     * @param clientFactory
     */
    @Autowired
    public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory) {
        this(clientFactory, DEFAULT_LIST_SERVICE_REST_URL, DEFAULT_LIST_SERVICE_METHOD, DEFAULT_SELECT_SERVICE_REST_URL, DEFAULT_SELECT_SERVICE_METHOD, DEFAULT_ADD_SERVICE_REST_URL,
                DEFAULT_ADD_SERVICE_METHOD);
    }

    /**
     * @param listServiceUrl
     * @param listServiceMethod
     * @param selectServiceUrl
     * @param selectServiceMethod
     * @param addServiceUrl
     * @param addServiceMethod
     */
    public ResolutionEngineClientRestImpl(final String listServiceUrl, final String listServiceMethod, final String selectServiceUrl, final String selectServiceMethod, final String addServiceUrl,
            final String addServiceMethod) {
        this(null, listServiceUrl, listServiceMethod, selectServiceUrl, selectServiceMethod, addServiceUrl, addServiceMethod);
    }

    /**
     * @param clientFactory
     * @param listServiceUrl
     * @param listServiceMethod
     * @param selectServiceUrl
     * @param selectServiceMethod
     * @param addServiceUrl
     * @param addServiceMethod
     */
    public ResolutionEngineClientRestImpl(final WebClientFactory clientFactory, final String listServiceUrl, final String listServiceMethod, final String selectServiceUrl,
            final String selectServiceMethod, final String addServiceUrl,
            final String addServiceMethod) {
        super(clientFactory);
        this.listServiceUrl = listServiceUrl;
        this.listServiceMethod = listServiceMethod;
        this.selectServiceUrl = selectServiceUrl;
        this.selectServiceMethod = selectServiceMethod;
        this.addServiceUrl = addServiceUrl;
        this.addServiceMethod = addServiceMethod;
    }

    // ------------------------------------------------------

    /**
     * @param listServiceUrl the listServiceUrl to set
     */
    public void setListServiceUrl(final String listServiceUrl) {
        this.listServiceUrl = listServiceUrl;
    }

    /**
     * @param listServiceMethod the listServiceMethod to set
     */
    public void setListServiceMethod(final String listServiceMethod) {
        this.listServiceMethod = listServiceMethod;
    }

    /**
     * @param selectServiceUrl the selectServiceUrl to set
     */
    public void setSelectServiceUrl(final String selectServiceUrl) {
        this.selectServiceUrl = selectServiceUrl;
    }

    /**
     * @param selectServiceMethod the selectServiceMethod to set
     */
    public void setSelectServiceMethod(final String selectServiceMethod) {
        this.selectServiceMethod = selectServiceMethod;
    }

    /**
     * @param addServiceUrl the addServiceUrl to set
     */
    public void setAddServiceUrl(final String addServiceUrl) {
        this.addServiceUrl = addServiceUrl;
    }

    /**
     * @param addServiceMethod the addServiceMethod to set
     */
    public void setAddServiceMethod(final String addServiceMethod) {
        this.addServiceMethod = addServiceMethod;
    }

    // ------------------------------------------------------

    /**
     * @return Chocolatelist
     * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeListService()
     */
    @Override
    public Chocolatelist invokeListService() {
        return invokeService(this.listServiceUrl, this.listServiceMethod, Chocolatelist.class);
    }

    /**
     * @param refid
     * @return Chocolate
     * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeSelectService(java.lang.String)
     */
    @Override
    public Chocolate invokeSelectService(final String refid) {
        return invokeService(this.selectServiceUrl, this.selectServiceMethod, refid, String.class, Chocolate.class);
    }

    /**
     * @param c
     * @return Chocolatelist
     * @see org.psikeds.queryagent.requester.client.ResolutionEngineClient#invokeAddService(org.psikeds.resolutionengine.interfaces.pojos.Chocolate)
     */
    @Override
    public Chocolatelist invokeAddService(final Chocolate c) {
        return invokeService(this.addServiceUrl, this.addServiceMethod, c, Chocolate.class, Chocolatelist.class);
    }

    /**
     * @return Logger
     * @see org.psikeds.queryagent.requester.client.impl.AbstractBaseClient#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    // ------------------------------------------------------

    // TODO: extract to separate test-class

    @SuppressWarnings("rawtypes")
    public static void main(final String[] args) {
        try {
            BasicConfigurator.configure();
            final String log4jConfigFile = "C:/workspace/psikeds/QueryAgent/src/main/resources/log4j.xml";
            DOMConfigurator.configure(log4jConfigFile);

            final List<Object> providers = new ArrayList<Object>();
            providers.add(new JacksonJsonProvider());
            providers.add(new JAXBElementProvider());

            final WebClientFactory wcf = new WebClientFactoryImpl(providers);
            final ResolutionEngineClientRestImpl rerc = new ResolutionEngineClientRestImpl(wcf);

            rerc.invokeListService();

            final String refid = "cc987";
            final Chocolate choco = new Chocolate(refid, "Rum-Trauben-Nuss");
            rerc.invokeAddService(choco);

            rerc.invokeSelectService(refid);
        }
        catch (final Exception ex) {
            LOGGER.error("Could not invoke REST-Service!", ex);
        }
    }
}
