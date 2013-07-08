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
package org.psikeds.common.services;

import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.Logger;

import org.apache.cxf.continuations.Continuation;
import org.apache.cxf.continuations.ContinuationProvider;

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
public abstract class AbstractBaseService {

  public static final boolean DEFAULT_ASYNC_SUPPORT = true;
  public static final long DEFAULT_SUSPENSION_TIMEOUT = 20000L;
  public static final Executor DEFAULT_SYNCHRONOUS_STRATEGY = new SynchronousExecutor();
  public static final ExecutableFactory DEFAULT_EXECUTABLE_FACTORY = new GenericExecutableFactory();

  /**
   * Time in milliseconds that an incomming request/continuation is suspended.
   * Request/thread will be resumed either after this period of time or when
   * callback signals that processing is finished.
   */
  protected long suspensionTimeout;

  /**
   * Map containing all currently waiting Requests, i.e. all suspended
   * Continuations
   */
  protected Map<String, Continuation> waitingRequests;

  /**
   * Strategy used for executing requests. Default for synchronous processing
   * is direct invocation (@see org.psikeds.common.exec.SynchronousExecutor)
   * and for asynchronous processing a pool of threads (@see
   * org.psikeds.common.exec.Threadpool). However you can plug in any strategy
   * most suitable for your environment as long as it matches the
   * asyncSupported flag.
   */
  protected Executor executionStrategy;

  /**
   * Factory for creating the required Executable.
   */
  protected ExecutableFactory executableFactory;

  /**
   * Shall requests be processed synchronous/blocking (false) or
   * asynchronous/non-blocking (true).
   * If Flag is set to true but execution platform does not support
   * Continuations, an automatic fallback to synchronous processing will be
   * performed.
   */
  protected boolean asyncSupported;

  /**
   * The default Service is asynchronous and using a Threadpool for generic
   * Executables.
   */
  public AbstractBaseService() {
    this(DEFAULT_ASYNC_SUPPORT);
  }

  public AbstractBaseService(final boolean asyncSupported) {
    // By default, every Service gets a new/separate Threadpool!
    this(asyncSupported, asyncSupported ? new Threadpool() : DEFAULT_SYNCHRONOUS_STRATEGY, DEFAULT_EXECUTABLE_FACTORY);
  }

  public AbstractBaseService(final boolean asyncSupported, final Executor executionStrategy, final ExecutableFactory executableFactory) {
    this(asyncSupported, executionStrategy, executableFactory, new HashMap<String, Continuation>(), DEFAULT_SUSPENSION_TIMEOUT);
  }

  public AbstractBaseService(final boolean asyncSupported, final Executor executionStrategy, final ExecutableFactory executableFactory, final Map<String, Continuation> waitingRequests,
      final long suspensionTimeout) {
    this.asyncSupported = asyncSupported;
    this.executionStrategy = executionStrategy;
    this.executableFactory = executableFactory;
    this.waitingRequests = waitingRequests;
    this.suspensionTimeout = suspensionTimeout;
  }

  /**
   * @param suspensionTimeout the suspensionTimeout to set
   */
  public void setSuspensionTimeout(final long suspensionTimeout) {
    this.suspensionTimeout = suspensionTimeout;
  }

  /**
   * @param waitingRequests the waitingRequests to set
   */
  public void setWaitingRequests(final Map<String, Continuation> waitingRequests) {
    this.waitingRequests = waitingRequests;
  }

  /**
   * @param executionStrategy the executionStrategy to set
   */
  public void setExecutionStrategy(final Executor executionStrategy) {
    this.executionStrategy = executionStrategy;
  }

  /**
   * @param executableFactory the executableFactory to set
   */
  public void setExecutableFactory(final ExecutableFactory executableFactory) {
    this.executableFactory = executableFactory;
  }

  /**
   * @param asyncSupported the asyncSupported to set
   */
  public void setAsyncSupported(final boolean asyncSupported) {
    this.asyncSupported = asyncSupported;
  }

  // ------------------------------------------------------------------------
  // --- Request Handling
  // ------------------------------------------------------------------------

  protected Object handleRequest(final String reqId, final Executable reqExec) throws InterruptedIOException {
    return handleRequest(reqId, reqExec, null);
  }

  protected Object handleRequest(final String reqId, final Executable reqExec, final Object reqData) throws InterruptedIOException {
    getLogger().trace("--> handleRequest({}, {}, {})", reqId, reqExec, reqData);
    Object respData = null;
    try {
      final Continuation cont = getContinuation(reqId);
      if (cont == null) {
        if (this.asyncSupported) {
          getLogger().warn("Continuations and asynchronous invocation of requests are configured (asyncSupported=true), however not supported by plattform!!!");
          getLogger().warn("Falling back to synchronous execution strategy: {}" + DEFAULT_SYNCHRONOUS_STRATEGY.getClass().getName());
          this.asyncSupported = false;
          this.executionStrategy = DEFAULT_SYNCHRONOUS_STRATEGY;
        }
        // new request, synchronous invocation, blocking and waiting
        // for the result
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
            throw new InterruptedIOException(sb.toString());
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
    getLogger().trace("--> invokeSynchronous({}, {}, {})", reqId, reqExec, reqData);
    Object respData = null;
    try {
      final Callback cb = new CallbackImpl(reqId, reqData);
      reqExec.setCallback(cb);
      this.executionStrategy.execute(reqExec);
      if (!cb.isFinished()) {
        final StringBuilder sb = new StringBuilder("This must never happen! Synchronous execution of ");
        sb.append(reqId);
        sb.append(" ended without being finished!? Check executable ");
        sb.append(reqExec);
        sb.append(" and strategy ");
        sb.append(this.executionStrategy);
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
    getLogger().trace("--> invokeAsync({}, {}, {}, {})", reqId, cont, reqExec, reqData);
    try {
      final Callback acb = new AsyncCallbackImpl(reqId, cont, reqData);
      reqExec.setCallback(acb);
      saveContinuation(reqId, cont);
      // First suspend continuation then start execution within seperate
      // thread. Otherwise worker thread could call resume() on
      // continuation before suspend() was originally invoked.
      suspendRequest(cont);
      this.executionStrategy.execute(reqExec);
    }
    finally {
      getLogger().trace("<-- invokeAsync({}, {}, {}, {})", reqId, cont, reqExec, reqData);
    }
  }

  private boolean suspendRequest(final Continuation cont) {
    getLogger().trace("--> suspendRequest({})", cont);
    try {
      return cont.suspend(this.suspensionTimeout);
    }
    finally {
      getLogger().trace("<-- suspendRequest({})", cont);
    }
  }

  private void saveContinuation(final String reqId, final Continuation cont) {
    getLogger().trace("--> saveContinuation({}, {})", reqId, cont);
    try {
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
    getLogger().trace("--> removeContinuation({})", reqId);
    Continuation cont = null;
    try {
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
    getLogger().trace("--> getContinuation({})", reqId);
    Continuation cont = null;
    try {
      if (!this.asyncSupported) {
        getLogger().debug("Async invocation of requests is disabled per configuration.");
        return cont;
      }
      cont = removeContinuation(reqId);
      if (cont != null) {
        getLogger().debug("Found existing Continuation: {}", cont);
        return cont;
      }
      getLogger().debug("Creating new Continuation for {}", reqId);
      final ContinuationProvider prov = getContinuationProvider();
      // provider might be null if continuations are not supported by the
      // execution platform
      cont = prov == null ? null : prov.getContinuation();
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
