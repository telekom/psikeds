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
 * Note: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ErrorMessage")
public class ErrorMessage extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private int code;
  private String message;

  public ErrorMessage() {
    this(0, null);
  }

  public ErrorMessage(final Status status) {
    this(status.getStatusCode(), status.getReasonPhrase());
  }

  public ErrorMessage(final int code, final String message) {
    super();
    this.code = code;
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public int getCode() {
    return this.code;
  }

  public void setCode(final int code) {
    this.code = code;
  }
}
