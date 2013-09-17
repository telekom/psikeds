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
import java.util.ArrayList;
import java.util.List;

/**
 * List of all purposes.
 *
 * @author marco@juliano.de
 *
 */
public class Purposes extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Purpose> purpose;

  public Purposes() {
    this(null);
  }

  public Purposes(final List<Purpose> purpose) {
    super();
    setPurpose(purpose);
  }

  public List<Purpose> getPurpose() {
    if (this.purpose == null) {
      this.purpose = new ArrayList<Purpose>();
    }
    return this.purpose;
  }

  public boolean addPurpose(final Purpose value) {
    return getPurpose().add(value);
  }

  public void setPurpose(final List<Purpose> lst) {
    this.purpose = lst;
  }
}
