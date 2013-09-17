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

/**
 * Root-Object including Knowledge-Data and Meta-Information.
 *
 * @author marco@juliano.de
 *
 */
public class Knowledgebase extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private Meta meta;
  private Data data;

  public Knowledgebase() {
    this(null, null);
  }

  public Knowledgebase(final Meta m, final Data d) {
    super();
    this.meta = m;
    this.data = d;
  }

  public Meta getMeta() {
    return this.meta;
  }

  public void setMeta(final Meta value) {
    this.meta = value;
  }

  public Data getData() {
    return this.data;
  }

  public void setData(final Data value) {
    this.data = value;
  }
}
