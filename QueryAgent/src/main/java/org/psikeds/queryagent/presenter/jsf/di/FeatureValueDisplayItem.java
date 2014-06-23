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

import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.POJO;
import org.psikeds.queryagent.presenter.jsf.util.SelectionHelper;

/**
 * Selectable FeatureValue of a Feature Choice.
 * 
 * @author marco@juliano.de
 */
public class FeatureValueDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public FeatureValueDisplayItem(final FeatureValue fv) {
    super(fv.getFeatureValueID(), fv.getValue(), POJO.composeId(fv.getFeatureID(), fv.getFeatureValueID()) + " = " + fv.getValue(), TYPE_FEATURE_VALUE);
  }

  public FeatureValueDisplayItem(final FeatureChoice fc, final FeatureValue fv) {
    this(fv);
    setSelectionKey(fc, fv);
  }

  public void setSelectionKey(final FeatureChoice fc, final FeatureValue fv) {
    setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_FEATURE_VALUE, fc.getParentVariantID(), fc.getFeatureID(), fv.getFeatureValueID()));
  }
}
