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

import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValues;
import org.psikeds.queryagent.presenter.jsf.util.SelectionHelper;

/**
 * Selectable Concept of a Concept Choice.
 * 
 * @author marco@juliano.de
 */
public class ConceptDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String additionalInfo;

  public ConceptDisplayItem(final Concept con) {
    super(con.getConceptID(), con.getLabel(), con.getDescription(), TYPE_CONCEPT);
    final FeatureValues fvs = con.getValues();
    final StringBuilder sb = new StringBuilder();
    for (final FeatureValue fv : fvs) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(fv.getFeatureID());
      sb.append(" = ");
      sb.append(fv.getValue());
    }
    this.additionalInfo = "( " + sb.toString() + " )";
  }

  public ConceptDisplayItem(final ConceptChoice cc, final Concept con) {
    this(con);
    setSelectionKey(cc, con);
  }

  public void setSelectionKey(final ConceptChoice cc, final Concept con) {
    setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_CONCEPT, cc.getParentVariantID(), con.getConceptID()));
  }

  public String getAdditionalInfo() {
    return this.additionalInfo;
  }

  public void setAdditionalInfo(final String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }
}
