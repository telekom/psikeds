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

import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.POJO;

/**
 * Selected/Assigned Feature-Value of a KE.
 * 
 * @author marco@juliano.de
 */
public class SelectedFeatureValueDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public SelectedFeatureValueDisplayItem(final FeatureValue fv) {
    super(POJO.composeId(fv.getFeatureID(), fv.getFeatureValueID()), null, fv.getFeatureID() + " = " + fv.getValue(), TYPE_SELECTED_FEATURE_VALUE);
  }
}
