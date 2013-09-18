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
package org.psikeds.resolutionengine.datalayer.knowledgebase.validator;

/**
 * Exception thrown by Validator(s) if Knowledge Base is not valid.
 *
 * @author marco@juliano.de
 *
 */
public class ValidationException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public ValidationException(final String message) {
    super(message);
  }

  public ValidationException(final Throwable cause) {
    super(cause);
  }

  public ValidationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
