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
package org.psikeds.resolutionengine.resolver;

/**
 * Exception thrown by Resolver(s) if logical Resolution of Knowledge-Entities
 * fails, e.g. if there is a illegal Decission or an inconsistent State.
 *
 * @author marco@juliano.de
 */
public class ResolutionException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public ResolutionException(final String message) {
    super(message);
  }

  public ResolutionException(final Throwable cause) {
    super(cause);
  }

  public ResolutionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
