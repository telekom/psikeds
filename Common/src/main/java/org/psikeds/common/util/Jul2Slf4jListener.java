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
package org.psikeds.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Some tools/libraries insist on using Java Util Logging (JUL), but
 * we prefer SLF4J. Therefore this Listener installs our own implementation
 * of a {@link java.util.logging.Handler} that forwards all log records to
 * SLF4J.
 * 
 * @author marco@juliano.de
 * 
 */
public class Jul2Slf4jListener implements ServletContextListener {

    private List<Handler> oldHandlers;
    private Handler newHandler;

    /**
     * @param sce
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(final ServletContextEvent sce) {
        // Remove old JUL-Handlers
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        this.oldHandlers = new ArrayList<Handler>();
        for (final Handler handler : rootLogger.getHandlers()) {
            this.oldHandlers.add(handler);
            rootLogger.removeHandler(handler);
        }
        // Register our own Handler for all log records.
        this.newHandler = new Jul2Slf4jHandler();
        this.newHandler.setLevel(Level.ALL);
        rootLogger.addHandler(this.newHandler);
        rootLogger.setLevel(Level.ALL);
    }

    /**
     * @param sce
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(final ServletContextEvent sce) {
        // Remove our own SLF4J-Handler
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.removeHandler(this.newHandler);
        // Restore the old JUL-Handlers
        for (final Handler handler : this.oldHandlers) {
            rootLogger.addHandler(handler);
        }
    }
}
