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
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Quite simple Threadpool with a fixed size and capacity.
 * Note: Queue is not fair, i.e. order of requests is not guaranteed!
 * 
 * @author marco@juliano.de
 */
public class Threadpool extends ThreadPoolExecutor implements Executor {

  public static final int DEFAULT_MIN_POOL_SIZE = 20;
  public static final int DEFAULT_MAX_POOL_SIZE = DEFAULT_MIN_POOL_SIZE;
  public static final long DEFAULT_THREAD_TIMEOUT = 0L;
  public static final int DEFAULT_QUEUE_CAPACITY = 50;

  public Threadpool() {
    this(DEFAULT_MIN_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_THREAD_TIMEOUT, DEFAULT_QUEUE_CAPACITY);
  }

  public Threadpool(final int minPoolSize, final int maxPoolSize, final long timeout, final int queueCapacity) {
    super(minPoolSize, maxPoolSize, timeout, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueCapacity));
  }
}
