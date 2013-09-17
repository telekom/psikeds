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

import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;

/**
 * Generates a (most probably) unique Request-ID like this:
 *
 * <hostname>-<current time in ms>-<random ascii characters>
 * 
 * @author marco@juliano.de
 */
public class RequestIdGenerator extends RandomStringGenerator implements IdGenerator {

  private boolean includeHostname;
  private boolean includeTimestamp;
  private String hostName;

  /**
   * @throws NoSuchAlgorithmException
   */
  public RequestIdGenerator() throws NoSuchAlgorithmException {
    this(true, true);
  }

  /**
   * @param ts
   * @param hn
   * @throws NoSuchAlgorithmException
   */
  public RequestIdGenerator(final boolean ts, final boolean hn) throws NoSuchAlgorithmException {
    super();
    this.includeTimestamp = ts;
    this.includeHostname = hn;
    initHostName();
  }

  private void initHostName() {
    try {
      final InetAddress localMachine = InetAddress.getLocalHost();
      this.hostName = localMachine.getHostName();
    }
    catch (final Exception ex) {
      this.hostName = null;
      this.includeHostname = false;
    }
  }

  /**
   * @param includeHostname the includeHostname to set
   */
  public void setIncludeHostname(final boolean includeHostname) {
    this.includeHostname = includeHostname;
  }

  /**
   * @param includeTimestamp the includeTimestamp to set
   */
  public void setIncludeTimestamp(final boolean includeTimestamp) {
    this.includeTimestamp = includeTimestamp;
  }

  /**
   * @return String next request id
   * @see org.psikeds.common.idgen.IdGenerator#getNextId()
   */
  @Override
  public String getNextId() {
    final StringBuilder sb = new StringBuilder();
    if (this.includeHostname && !StringUtils.isEmpty(this.hostName)) {
      sb.append(this.hostName);
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
