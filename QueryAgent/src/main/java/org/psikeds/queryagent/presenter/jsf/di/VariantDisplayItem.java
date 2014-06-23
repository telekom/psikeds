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

import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.presenter.jsf.util.SelectionHelper;

/**
 * Selectable Variant of a Variant Choice.
 * 
 * @author marco@juliano.de
 */
public class VariantDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public VariantDisplayItem(final Variant v) {
    super(v.getVariantID(), v.getLabel(), v.getDescription(), TYPE_VARIANT);
  }

  public VariantDisplayItem(final Purpose p, final Variant v) {
    this(v);
    setSelectionKey(p, v);
  }

  public void setSelectionKey(final Purpose p, final Variant v) {
    setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_VARIANT, p, v));
  }
}
