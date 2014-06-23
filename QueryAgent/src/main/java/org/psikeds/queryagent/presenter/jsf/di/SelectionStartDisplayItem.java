/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.jsf.di;

import java.io.Serializable;

/**
 * DI signaling that a new selection list begins.
 * 
 * @author marco@juliano.de
 */
public class SelectionStartDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public SelectionStartDisplayItem() {
    this(null);
  }

  public SelectionStartDisplayItem(final String key) {
    super(key, null, null, TYPE_SELECTION_START);
  }
}
