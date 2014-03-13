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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * A general Message for the Client, either ErrorMessage or Warning
 * 
 * @author marco@juliano.de
 * 
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = ErrorMessage.class, name = "ErrorMessage"), @JsonSubTypes.Type(value = Warning.class, name = "Warning"), })
public abstract class ResolutionMessage extends POJO {

  private static final long serialVersionUID = 1L;

  public static final int OK_CODE = 0;

  protected int code;
  protected String message;

  protected ResolutionMessage() {
    this(OK_CODE, null);
  }

  protected ResolutionMessage(final int code, final String message) {
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

  @JsonIgnore
  public boolean isError() {
    return (this.code != OK_CODE);
  }
}
