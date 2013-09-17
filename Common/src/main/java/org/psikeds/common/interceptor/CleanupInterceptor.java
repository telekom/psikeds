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
package org.psikeds.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.psikeds.common.util.LoggingHelper;

/**
 * Interceptor for cleaning up the Logging-MDC.
 * Required because otherwise we could have Memory-Leaks.
 *
 * @author marco@juliano.de
 *
 */
public class CleanupInterceptor<T extends Message> extends AbstractPhaseInterceptor<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CleanupInterceptor.class);

  /**
   * Default constructor for usage as Outbound-Interceptor after
   * Response is logged and sent to Caller.
   * 
   */
  public CleanupInterceptor() {
    this(Phase.PRE_STREAM);
    addAfter(LoggingOutInterceptor.class.getName());
  }

  public CleanupInterceptor(final String phase) {
    super(phase);
  }

  /**
   * @param message
   * @throws Fault
   * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
   */
  @Override
  public void handleMessage(final T message) throws Fault {
    cleanup();
  }

  /**
   * @param message
   * @see org.apache.cxf.phase.AbstractPhaseInterceptor#handleFault(org.apache.cxf.message.Message)
   */
  @Override
  public void handleFault(final T message) {
    cleanup();
  }

  private void cleanup() {
    try {
      LoggingHelper.clear();
    }
    finally {
      LOGGER.trace("Cleaning up!");
    }
  }
}
