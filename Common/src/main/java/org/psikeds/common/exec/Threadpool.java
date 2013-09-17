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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Simple Threadpool using a bounded Queue for incomming Tasks.
 * By default all unreferenced Threads will die immediately
 * in order to ensure proper Finalization / Shutdown.
 *
 * Please note that the Order of Execution is not guaranteed,
 * i.e. Tasks could overtake each other.
 *
 * @author marco@juliano.de
 */
public class Threadpool extends ThreadPoolExecutor implements ExecutorService, Executor {

  public static final int DEFAULT_CORE_POOL_SIZE = 0;
  public static final int DEFAULT_MAX_POOL_SIZE = 20;
  public static final long DEFAULT_KEEP_ALIVE_SECONDS = 0L;
  public static final int DEFAULT_QUEUE_CAPACITY = 100;

  public Threadpool() {
    this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ALIVE_SECONDS, DEFAULT_QUEUE_CAPACITY);
  }

  public Threadpool(final int corePoolSize, final int maximumPoolSize, final long keepAliveSeconds, final int queueCapacity) {
    super(corePoolSize, maximumPoolSize, keepAliveSeconds, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueCapacity));
  }

  public Threadpool(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    if (keepAliveTime > 0) {
      allowCoreThreadTimeOut(true);
    }
  }
}
