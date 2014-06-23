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

import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;

/**
 * Feature of a Feature-Choice.
 * 
 * @author marco@juliano.de
 */
public class FeatureDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public FeatureDisplayItem(final Feature f) {
    super(f.getFeatureID(), f.getLabel(), f.getDescription(), TYPE_FEATURE);
  }
}
