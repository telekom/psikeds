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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.Logger;

import org.apache.commons.lang.Validate;
import org.apache.cxf.continuations.Continuation;
import org.apache.cxf.continuations.ContinuationProvider;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.common.exec.Callback;
import org.psikeds.common.exec.Executable;
import org.psikeds.common.exec.ExecutableFactory;
import org.psikeds.common.exec.SynchronousExecutor;
import org.psikeds.common.exec.Threadpool;
import org.psikeds.common.exec.impl.AsyncCallbackImpl;
import org.psikeds.common.exec.impl.CallbackImpl;
import org.psikeds.common.exec.impl.GenericExecutableFactory;
import org.psikeds.common.util.LoggingHelper;

/**
 * This is the Base of all REST- and SOAP-Services of psiKeds. It supports both
 * synchronous and asynchronous processing of requests.
 * 
 * @author marco@juliano.de
 */
public abstract class AbstractBaseService implements InitializingBean {

  public static final boolean DEFAULT_ASYNC_SUPPORT = true;
  public static final long DEFAULT_SUSPENSION_TIMEOUT = 20000L;
  public static final Executor DEFAULT_SYNCHRONOUS_STRATEGY = new SynchronousExecutor();
  public static final ExecutableFactory DEFAULT_EXECUTABLE_FACTORY = new GenericExecutableFactory();

  /**
   * Time in milliseconds that an incomming request/continuation is
   * suspended.
   * Request/Thread will be resumed either after this period of Time
   * or when Callback signals that Processing is finished.
   */
  protected long suspensionTimeout;

  /**
   * Map containing all currently waiting Requests,
   * i.e. all suspended Continuations
   */
  protected Map<String, Continuation> waitingRequests;

  /**
   * Strategy used for execution of Requests.
   * Default for synchronous Processing is direct invocation
   * (@see org.psikeds.common.exec.SynchronousExecutor)
   * and for asynchronous Processing a Pool of Threads
   * (@see org.psikeds.common.exec.Threadpool).
   * However you can plug in any Strategy most suitable for your
   * Environment as long as it matches the Flag asyncSupported.
   */
  protected Executor executionStrategy;

  /**
   * Factory for creating the required Executable.
   * (@see org.psikeds.common.exec.Executable)
   */
  protected ExecutableFactory executableFactory;

  /**
   * Shall Requests be processed synchronous/blocking (false) or
   * asynchronous/non-blocking (true)?
   * 
   * If Flag is set to true but Execution-Platform does not support
   * Continuations, an automatic fallback to synchronous processing
   * will be performed.
   */
  protected boolean asyncSupported;

  // ------------------------------------------------------------------------
  // --- Constructors, Getters and Setters
  // ------------------------------------------------------------------------

  public AbstractBaseService() {
    this(DEFAULT_ASYNC_SUPPORT);
  }

  public AbstractBaseService(final boolean asyncSupported) {
    this(asyncSupported, DEFAULT_SUSPENSION_TIMEOUT);
  }

  public AbstractBaseService(final long suspensionTimeout) {
    this((suspensionTimeout > 0), suspensionTimeout);
  }

  public AbstractBaseService(final boolean asyncSupported, final long suspensionTimeout) {
    // By default, every Service gets a new/separate Threadpool
    this(asyncSupported, suspensionTimeout, asyncSupported ? new Threadpool() : DEFAULT_SYNCHRONOUS_STRATEGY, DEFAULT_EXECUTABLE_FACTORY);
  }

  public AbstractBaseService(final boolean asyncSupported, final long suspensionTimeout, final Executor executionStrategy, final ExecutableFactory executableFactory) {
    // By default, every Service has a new/separate HashMap for its waiting/suspended Requests
    this(asyncSupported, suspensionTimeout, executionStrategy, executableFactory, new HashMap<String, Continuation>());
  }

  public AbstractBaseService(final boolean asyncSupported, final long suspensionTimeout, final Executor executionStrategy, final ExecutableFactory executableFactory,
      final Map<String, Continuation> waitingRequests) {
    this.asyncSupported = asyncSupported;
    this.executionStrategy = executionStrategy;
    this.executableFactory = executableFactory;
    this.waitingRequests = waitingRequests;
    this.suspensionTimeout = suspensionTimeout;
  }

  public long getSuspensionTimeout() {
    return this.suspensionTimeout;
  }

  public void setSuspensionTimeout(final long suspensionTimeout) {
    this.suspensionTimeout = suspensionTimeout;
  }

  public Map<String, Continuation> getWaitingRequests() {
    return this.waitingRequests;
  }

  public void setWaitingRequests(final Map<String, Continuation> waitingRequests) {
    this.waitingRequests = waitingRequests;
  }

  public Executor getExecutionStrategy() {
    return this.executionStrategy;
  }

  public void setExecutionStrategy(final Executor executionStrategy) {
    this.executionStrategy = executionStrategy;
  }

  public ExecutableFactory getExecutableFactory() {
    return this.executableFactory;
  }

  public void setExecutableFactory(final ExecutableFactory executableFactory) {
    this.executableFactory = executableFactory;
  }

  public boolean isAsyncSupported() {
    return this.asyncSupported;
  }

  public void setAsyncSupported(final boolean asyncSupported) {
    this.asyncSupported = asyncSupported;
  }

  // ------------------------------------------------------------------------
  // --- Implementation of Interface InitializingBean
  // ------------------------------------------------------------------------

  /**
   * Check that AbstractBaseService (or derived Class) was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(this.executionStrategy, "No Execution-Strategy!");
    Validate.notNull(this.executableFactory, "No Executable-Factory!");
    Validate.notNull(this.waitingRequests, "No Map for waiting/suspsended Requests!");
    Validate.isTrue((!this.asyncSupported || (this.suspensionTimeout > 0)), "Asynchronuous Invocation must specify a Suspension-Timeout greater than Zero!");
  }

  // ------------------------------------------------------------------------
  // --- Request Handling
  // ------------------------------------------------------------------------

  protected Object handleRequest(final String reqId, final Executable reqExec) throws InterruptedIOException {
    return handleRequest(reqId, reqExec, null);
  }

  protected Object handleRequest(final String reqId, final Executable reqExec, final Object reqData) throws InterruptedIOException {
    Object respData = null;
    try {
      getLogger().trace("--> handleRequest({}, {}, {})", reqId, reqExec, reqData);
      final Continuation cont = getContinuation(reqId);
      if (cont == null) {
        if (this.asyncSupported) {
          getLogger()
              .warn(
                  "Continuations and asynchronous invocation of requests are configured (asyncSupported = {}), but not supported by Execution-Platform!\nFalling back to synchronous Execution-Strategy: {}",
                  this.asyncSupported, DEFAULT_SYNCHRONOUS_STRATEGY.getClass().getName());
          this.asyncSupported = false;
          this.executionStrategy = DEFAULT_SYNCHRONOUS_STRATEGY;
        }
        // new request, synchronous invocation, blocking and waiting for the result
        respData = invokeSynchronous(reqId, reqExec, reqData);
        return respData;
      }
      synchronized (cont) {
        if (cont.isNew()) {
          // new request, start asynchronous invocation, non blocking
          invokeAsync(reqId, cont, reqExec, reqData);
        }
        else {
          final Callback cb = (Callback) cont.getObject();
          if (cont.isResumed() && cb.isFinished()) {
            // asynchronous execution finished, callback received
            respData = cb.getPayload();
            removeContinuation(reqId);
          }
          else {
            // Callback not received yet, request timed out. Check
            // timeout settings if this happens very often.
            removeContinuation(reqId);
            final StringBuilder sb = new StringBuilder("Timeout reached after ");
            sb.append(this.suspensionTimeout);
            sb.append("ms for ");
            sb.append(reqId);
            sb.append(" [");
            sb.append(reqExec);
            sb.append(']');
            final String msg = sb.toString();
            getLogger().info(msg);
            throw new InterruptedIOException(msg);
          }
        }
        return respData;
      }
    }
    finally {
      getLogger().trace("<-- handleRequest({}, {}, {}); respData = {}", reqId, reqExec, reqData, respData);
    }
  }

  protected Executable getExecutable(final Object delegate, final String serviceName) {
    LoggingHelper.setServiceName(serviceName);
    return this.executableFactory.getExecutable(delegate, serviceName);
  }

  // ------------------------------------------------------------------------
  // --- Internal Helpers
  // ------------------------------------------------------------------------

  private Object invokeSynchronous(final String reqId, final Executable reqExec, final Object reqData) {
    Object respData = null;
    try {
      getLogger().trace("--> invokeSynchronous({}, {}, {})", reqId, reqExec, reqData);
      final Callback cb = new CallbackImpl(reqId, reqData);
      reqExec.setCallback(cb);
      this.executionStrategy.execute(reqExec);
      if (!cb.isFinished()) {
        final StringBuilder sb = new StringBuilder("This must never happen! Synchronous Execution of ");
        sb.append(reqId);
        sb.append(" ended without being finished!? Check implementation of Executable ");
        sb.append(reqExec);
        sb.append(" and Strategy ");
        sb.append(this.executionStrategy);
        final String msg = sb.toString();
        getLogger().error(msg);
        throw new IllegalThreadStateException(sb.toString());
      }
      respData = cb.getPayload();
      return respData;
    }
    finally {
      getLogger().trace("<-- invokeSynchronous({}, {}, {}); respData = {}", reqId, reqExec, reqData, respData);
    }
  }

  private void invokeAsync(final String reqId, final Continuation cont, final Executable reqExec, final Object reqData) {
    try {
      getLogger().trace("--> invokeAsync({}, {}, {}, {})", reqId, cont, reqExec, reqData);
      final Callback acb = new AsyncCallbackImpl(reqId, cont, reqData);
      reqExec.setCallback(acb);
      saveContinuation(reqId, cont);
      // First suspend Continuation then start Execution within seperate
      // Thread. Otherwise worker thread could call resume() on Continuation
      // before suspend() was originally invoked!
      suspendRequest(cont);
      this.executionStrategy.execute(reqExec);
    }
    finally {
      getLogger().trace("<-- invokeAsync({}, {}, {}, {})", reqId, cont, reqExec, reqData);
    }
  }

  private boolean suspendRequest(final Continuation cont) {
    try {
      getLogger().trace("--> suspendRequest({})", cont);
      return cont.suspend(this.suspensionTimeout);
    }
    finally {
      getLogger().trace("<-- suspendRequest({})", cont);
    }
  }

  private void saveContinuation(final String reqId, final Continuation cont) {
    try {
      getLogger().trace("--> saveContinuation({}, {})", reqId, cont);
      synchronized (this.waitingRequests) {
        this.waitingRequests.put(reqId, cont);
        this.waitingRequests.notifyAll();
      }
    }
    finally {
      getLogger().trace("<-- saveContinuation({}, {})", reqId, cont);
    }
  }

  private Continuation removeContinuation(final String reqId) {
    Continuation cont = null;
    try {
      getLogger().trace("--> removeContinuation({})", reqId);
      synchronized (this.waitingRequests) {
        cont = this.waitingRequests.remove(reqId);
        this.waitingRequests.notifyAll();
      }
      return cont;
    }
    finally {
      getLogger().trace("<-- removeContinuation({}); cont = {}", reqId, cont);
    }
  }

  private Continuation getContinuation(final String reqId) {
    Continuation cont = null;
    try {
      getLogger().trace("--> getContinuation({})", reqId);
      if (!this.asyncSupported) {
        getLogger().debug("Asynchronuous Invocation of Requests is disabled per Configuration.");
        return cont;
      }
      cont = removeContinuation(reqId);
      if (cont != null) {
        getLogger().debug("Found existing Continuation: {}", cont);
        return cont;
      }
      getLogger().debug("Creating new Continuation for Req-Id: {}", reqId);
      final ContinuationProvider prov = getContinuationProvider();
      // Provider might be null if Continuations are not supported
      // by the Execution-Platform
      cont = (prov == null ? null : prov.getContinuation());
      return cont;
    }
    finally {
      getLogger().trace("<-- getContinuation({}); cont = {}", reqId, cont);
    }
  }

  // ------------------------------------------------------------------------
  // --- To be implemented by derived Classes
  // ------------------------------------------------------------------------

  /**
   * SOAP- and REST-Services differ in just one detail: How to obtain the
   * MessageContext and how to get the ContinuationProvider from it. Therefore
   * this must be implemented in derived classes.
   * 
   * @return ContinuationProvider
   */
  protected abstract ContinuationProvider getContinuationProvider();

  protected abstract Logger getLogger();
}
