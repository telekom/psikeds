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
package org.psikeds.common.config;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.context.ContextLoader;

/**
 * Custom ContextLoader for the Springframework, allowing to override the
 * configuration at runtime with files found in the configuration directory.
 * Therefore it install a proxy for the javax.servlet.ServletContext and
 * overrides the location of the Spring-Context-Files.
 * 
 * @author marco@juliano.de
 * 
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextLoaderListener.class);

    private ContextLoader contextLoader;

    public ContextLoaderListener() {
        super();
        this.contextLoader = null;
    }

    /**
     * Initialization of web application is starting. Install context-loader and
     * -proxy.
     */
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        if (this.contextLoader == null) {
            this.contextLoader = this;
        }
        final ServletContext ctx = ServletContextProxy.newInstance(event.getServletContext());
        this.contextLoader.initWebApplicationContext(ctx);
    }

    /**
     * Servlet is shutting down. Close context and destroy Spring-Beans.
     */
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        if (this.contextLoader != null) {
            this.contextLoader.closeWebApplicationContext(event.getServletContext());
        }
        final ServletContext sc = event.getServletContext();
        final Enumeration<?> attrNames = sc.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            final String attrName = (String) attrNames.nextElement();
            if (attrName.startsWith("org.springframework.")) {
                final Object attrValue = sc.getAttribute(attrName);
                if (attrValue instanceof DisposableBean) {
                    try {
                        ((DisposableBean) attrValue).destroy();
                    }
                    catch (final Exception ex) {
                        LOGGER.error(
                                "Couldn't invoke destroy method of attribute with name '"
                                        + attrName + "'", ex);
                    }
                }
            }
        }
    }
}
