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
package org.psikeds.common.idgen.impl;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;

/**
 * Generates a (most probably) unique Session-ID like this:
 * 
 * <prefix>-<current thread id>-<current time in ms>-<random ascii characters>
 * 
 * @author marco@juliano.de
 */
public class SessionIdGenerator extends RandomStringGenerator implements IdGenerator {

  public static final String DEFAULT_PREFIX = "psikeds";
  public static final boolean DEFAULT_INCLUDE_PREFIX = true;
  public static final boolean DEFAULT_INCLUDE_THREAD_ID = true;
  public static final boolean DEFAULT_INCLUDE_TIMESTAMP = true;

  private String prefix;
  private boolean includePrefix;
  private boolean includeThreadId;
  private boolean includeTimestamp;

  public SessionIdGenerator() throws NoSuchAlgorithmException {
    this(DEFAULT_PREFIX);
  }

  public SessionIdGenerator(final String prefix) throws NoSuchAlgorithmException {
    this(prefix, DEFAULT_INCLUDE_PREFIX, DEFAULT_INCLUDE_THREAD_ID, DEFAULT_INCLUDE_TIMESTAMP);
  }

  public SessionIdGenerator(final String prefix, final boolean includePrefix, final boolean includeThreadId, final boolean includeTimestamp) throws NoSuchAlgorithmException {
    super();
    this.prefix = prefix;
    this.includePrefix = includePrefix;
    this.includeThreadId = includeThreadId;
    this.includeTimestamp = includeTimestamp;
  }

  // ------------------------------------------------------

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  public boolean isIncludePrefix() {
    return this.includePrefix;
  }

  public void setIncludePrefix(final boolean includePrefix) {
    this.includePrefix = includePrefix;
  }

  public boolean isIncludeThreadId() {
    return this.includeThreadId;
  }

  public void setIncludeThreadId(final boolean includeThreadId) {
    this.includeThreadId = includeThreadId;
  }

  public boolean isIncludeTimestamp() {
    return this.includeTimestamp;
  }

  public void setIncludeTimestamp(final boolean includeTimestamp) {
    this.includeTimestamp = includeTimestamp;
  }

  // ------------------------------------------------------

  /**
   * @return String next request id
   * @see org.psikeds.common.idgen.IdGenerator#getNextId()
   */
  @Override
  public String getNextId() {
    final StringBuilder sb = new StringBuilder();
    if (this.includePrefix && !StringUtils.isEmpty(this.prefix)) {
      sb.append(this.prefix);
      sb.append('-');
    }
    if (this.includeThreadId) {
      sb.append(Thread.currentThread().getId());
      sb.append('-');
    }
    if (this.includeTimestamp) {
      sb.append(System.currentTimeMillis());
      sb.append('-');
    }
    sb.append(super.getNextId());
    return sb.toString();
  }
}
