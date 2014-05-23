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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Warning for the Client, i.e. this is no severe Error but nevertheless
 * some important information.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Warning")
public class Warning extends ResolutionMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  public Warning() {
    super();
  }

  public Warning(final String message) {
    super(OK_CODE, message);
  }
}
