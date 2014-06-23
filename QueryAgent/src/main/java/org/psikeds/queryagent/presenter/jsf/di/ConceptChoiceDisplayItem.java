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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Choice of possible Concepts.
 * 
 * @author marco@juliano.de
 */
public class ConceptChoiceDisplayItem extends DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  protected Map<String, Object> choices;

  public ConceptChoiceDisplayItem(final String key) {
    super(key, key, null, TYPE_CONCEPT_CHOICE);
    this.choices = null;
  }

  public Map<String, Object> getChoices() {
    if (this.choices == null) {
      this.choices = new LinkedHashMap<String, Object>();
    }
    return this.choices;
  }

  public void setChoices(final Map<String, Object> choices) {
    this.choices = choices;
  }

  @Override
  public void addChild(final DisplayItem item) {
    if (item != null) {
      super.addChild(item);
      if (TYPE_CONCEPT.equals(item.getType())) {
        String label = item.getValue();
        if (item instanceof ConceptDisplayItem) {
          final ConceptDisplayItem cdi = (ConceptDisplayItem) item;
          label = label + " " + cdi.getAdditionalInfo();
        }
        getChoices().put(label, item.getSelectionKey());
      }
    }
  }
}
