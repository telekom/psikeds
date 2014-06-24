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

import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantDecission;

/**
 * A made Decission.
 * 
 * @author marco@juliano.de
 */
public class DecissionDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public DecissionDisplayItem(final Decission decission) {
    super(null, null, null, TYPE_DECISSION);
    final StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    if (decission instanceof VariantDecission) {
      final VariantDecission vd = (VariantDecission) decission;
      sb.append("P:");
      sb.append(vd.getPurposeID());
      sb.append(" -> V:");
      sb.append(vd.getVariantID());
    }
    else if (decission instanceof FeatureDecission) {
      final FeatureDecission fd = (FeatureDecission) decission;
      sb.append("F:");
      sb.append(fd.getFeatureID());
      sb.append(" -> FV:");
      sb.append(fd.getFeatureValueID());
    }
    else if (decission instanceof ConceptDecission) {
      final ConceptDecission cd = (ConceptDecission) decission;
      sb.append("V:");
      sb.append(cd.getVariantID());
      sb.append(" -> C:");
      sb.append(cd.getConceptID());
    }
    sb.append(" ]");
    setLongDescription(sb.toString());
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getLongDescription();
  }
}
