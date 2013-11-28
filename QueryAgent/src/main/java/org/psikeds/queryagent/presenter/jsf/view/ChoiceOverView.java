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
package org.psikeds.queryagent.presenter.jsf.view;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.interfaces.presenter.pojos.Choice;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.presenter.jsf.model.KnowledgeRepresentation;
import org.psikeds.queryagent.presenter.jsf.util.SelectionHelper;

/**
 * An (Over-)View of all possible Choices.
 * 
 * @author marco@juliano.de
 */
public class ChoiceOverView extends BaseView {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChoiceOverView.class);

  public ChoiceOverView() {
    this(null);
  }

  public ChoiceOverView(final KnowledgeRepresentation model) {
    super(model);
  }

  public boolean isWithoutData() {
    return (isNotInitialized() || isResolved() || this.model.getPossibleChoices().isEmpty());
  }

  public List<DisplayItem> getPossibleChoices() {
    final List<DisplayItem> lst = new ArrayList<DisplayItem>();
    try {
      LOGGER.trace("--> getPossibleChoices()");
      if (!isWithoutData()) {
        final List<Choice> choices = this.model.getPossibleChoices();
        for (final Choice c : choices) {
          final Purpose p = c.getPurpose();
          final DisplayItem dp = new DisplayItem(p.getId(), p.getLabel(), p.getDescription(), DisplayItem.TYPE_PURPOSE);
          lst.add(dp);
          LOGGER.trace("Added P: {}", dp);
          for (final Variant v : c.getVariants()) {
            final DisplayItem dv = new DisplayItem(v.getId(), v.getLabel(), v.getDescription(), DisplayItem.TYPE_VARIANT);
            dv.setSelectionKey(SelectionHelper.createSelectionString(p, v));
            dp.addChild(dv);
            lst.add(dv);
            LOGGER.trace("Added V: {}", dv);
          }
        }
      }
    }
    catch (final Exception ex) {
      LOGGER.error("getPossibleChoices() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- getPossibleChoices(); lst = {}", lst);
    }
    return lst;
  }
}
