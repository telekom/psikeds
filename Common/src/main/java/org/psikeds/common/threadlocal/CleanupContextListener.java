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
package org.psikeds.common.threadlocal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.common.util.LoggingHelper;

/**
 * ServletContextListener performing final Cleanup when Application is undeployed.
 *
 * @author marco@juliano.de
 *
 */
public class CleanupContextListener implements ServletContextListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleanupContextListener.class);

  private boolean initialized;

  public CleanupContextListener() {
    this.initialized = false;
  }

  /**
   * Initialization of web application is starting.
   *
   * @param sce
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   *
   */
  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    this.initialized = true;
    LOGGER.debug("Initialized!");
  }

  /**
   * Web application is shutting down ... perform the final cleanup!
   *
   * @param sce
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   *
   */
  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    try {
      if (this.initialized) {
        // disable MDC, otherwise we create new Objects ourselves
        LoggingHelper.enabled = false;
        LoggingHelper.clear();
        // enable TLH and remove Entries from Map
        ThreadLocalHelper.enabled = true;
        ThreadLocalHelper.cleanThreadLocalMaps();
      }
    }
    finally {
      this.initialized = false;
      LOGGER.debug("Cleanup finished ... Listener destroyed!");
    }
  }
}
