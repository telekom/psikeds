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
package org.psikeds.common.exec;

import java.util.concurrent.Executor;

/**
 * Very simple, synchronous Strategy: Directly invoke run() within the
 * current Thread.
 * 
 * @author marco@juliano.de
 */
public class SynchronousExecutor implements Executor {

  /**
   * @param command
   * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
   */
  @Override
  public void execute(final Runnable command) {
    command.run();
  }
}
