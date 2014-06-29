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
package org.psikeds.resolutionengine.interfaces.pojos;

import java.io.Serializable;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A single Error containing a Message and a Code.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ErrorMessage")
public class ErrorMessage extends ResolutionMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  public ErrorMessage() {
    super();
  }

  public ErrorMessage(final int code, final String message) {
    super(code, message);
  }

  public ErrorMessage(final String message) {
    this(Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
  }

  public ErrorMessage(final Status status) {
    this(status.getStatusCode(), status.getReasonPhrase());
  }

  public ErrorMessage(final Throwable t) {
    this(t.getMessage());
  }

  public ErrorMessage(final String message, final Throwable t) {
    this(message + " " + t.getMessage());
  }
}
