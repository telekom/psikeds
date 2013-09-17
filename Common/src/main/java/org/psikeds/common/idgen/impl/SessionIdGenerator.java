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

  private static final String DEFAULT_PREFIX = "psikeds";
  private String prefix;

  /**
   * @throws NoSuchAlgorithmException
   */
  public SessionIdGenerator() throws NoSuchAlgorithmException {
    this(DEFAULT_PREFIX);
  }

  /**
   * @param pref
   * @throws NoSuchAlgorithmException
   */
  public SessionIdGenerator(final String pref) throws NoSuchAlgorithmException {
    super();
    this.prefix = pref;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  /**
   * @return String next session id
   * @see org.psikeds.common.idgen.IdGenerator#getNextId()
   */
  @Override
  public String getNextId() {
    final StringBuilder sb = new StringBuilder();
    if (!StringUtils.isEmpty(this.prefix)) {
      sb.append(this.prefix);
      sb.append('-');
    }
    sb.append(Thread.currentThread().getId());
    sb.append('-');
    sb.append(System.currentTimeMillis());
    sb.append('-');
    sb.append(super.getNextId());
    return sb.toString();
  }
}
