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
package org.psikeds.resolutionengine.datalayer.vo;

import java.io.Serializable;

import org.psikeds.common.util.ObjectDumper;

/**
 * Base of all Value-Objects in this Package.
 *
 * @author marco@juliano.de
 *
 */
public class ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  protected ValueObject() {
    // prevent direct instantiation
  }

  /**
   * @return
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    sb.append('\n');
    sb.append(new ObjectDumper().dump(this));
    return sb.toString();
  }
}
