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

import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;

/**
 * Generates a (most probably) unique Request-ID like this:
 * 
 * <hostname>-<random ascii characters>-<current time in ms>
 * 
 * @author marco@juliano.de
 */
public class RequestIdGenerator extends RandomStringGenerator implements IdGenerator {

  public static final boolean DEFAULT_INCLUDE_HOSTNAME = false;
  public static final boolean DEFAULT_RESOLVE_HOSTNAME = false;
  public static final boolean DEFAULT_INCLUDE_TIMESTAMP = true;

  private boolean includeHostname;
  private boolean resolveHostname;
  private boolean includeTimestamp;
  private String hostName;

  public RequestIdGenerator() throws NoSuchAlgorithmException {
    this(DEFAULT_INCLUDE_TIMESTAMP, DEFAULT_INCLUDE_HOSTNAME, DEFAULT_RESOLVE_HOSTNAME);
  }

  public RequestIdGenerator(final boolean its, final boolean ihn, final boolean rhn) throws NoSuchAlgorithmException {
    super();
    this.includeTimestamp = its;
    this.includeHostname = ihn;
    this.resolveHostname = rhn;
    initHostName();
  }

  public void initHostName() {
    try {
      if (this.includeHostname && StringUtils.isEmpty(this.hostName)) {
        final InetAddress localMachine = InetAddress.getLocalHost();
        // InetAddress.getHostName() performs a Nameservice-Lookup.
        // This can be a very expensive Operation, worst case blocking
        // for several Minutes until some Timeout occurs!
        this.hostName = (this.resolveHostname ? localMachine.getHostName() : localMachine.getHostAddress());
      }
    }
    catch (final Exception ex) {
      this.hostName = null;
      this.includeHostname = false;
      this.resolveHostname = false;
    }
  }

  // ------------------------------------------------------

  public String getHostName() {
    return this.hostName;
  }

  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }

  public boolean isResolveHostname() {
    return this.resolveHostname;
  }

  public void setResolveHostname(final boolean resolveHostname) {
    this.resolveHostname = resolveHostname;
  }

  public boolean isIncludeHostname() {
    return this.includeHostname;
  }

  public void setIncludeHostname(final boolean includeHostname) {
    this.includeHostname = includeHostname;
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
    if (this.includeHostname && !StringUtils.isEmpty(this.hostName)) {
      sb.append(this.hostName);
      sb.append('-');
    }
    sb.append(super.getNextId());
    if (this.includeTimestamp) {
      sb.append('-');
      sb.append(System.currentTimeMillis());
    }
    return sb.toString();
  }
}
