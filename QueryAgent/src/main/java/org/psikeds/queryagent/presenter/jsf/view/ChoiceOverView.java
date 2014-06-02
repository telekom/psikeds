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
import org.psikeds.queryagent.interfaces.presenter.pojos.Choices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoice;
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

  @Override
  public boolean isWithoutData() {
    return (isNotInitialized() || isResolved() || this.model.getChoices().isEmpty());
  }

  public List<DisplayItem> getPossibleChoices() {
    final List<DisplayItem> lst = new ArrayList<DisplayItem>();
    try {
      LOGGER.trace("--> getPossibleChoices()");
      if (!isWithoutData()) {
        final Choices choices = this.model.getChoices();
        if ((choices != null) && !choices.isEmpty()) {
          for (final Choice c : choices) {
            if (c instanceof VariantChoice) {
              final VariantChoice vc = (VariantChoice) c;
              final Purpose p = vc.getPurpose();
              final DisplayItem dp = new DisplayItem(p.getPurposeID(), p.getLabel(), p.getDescription(), DisplayItem.TYPE_PURPOSE);
              lst.add(dp);
              LOGGER.trace("Added P: {}", dp);
              for (final Variant v : vc.getVariants()) {
                final DisplayItem dv = new DisplayItem(v.getVariantID(), v.getLabel(), v.getDescription(), DisplayItem.TYPE_VARIANT);
                dv.setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_VARIANT, p, v));
                dp.addChild(dv);
                lst.add(dv);
                LOGGER.trace("Added V: {}", dv);
              }
            }
            else if (c instanceof FeatureChoice) {
              final FeatureChoice fc = (FeatureChoice) c;
              final String vid = fc.getParentVariantID();
              final String fid = fc.getFeatureID();
              // TODO: get variant and feature from the model and create nicer display items
              final DisplayItem df = new DisplayItem(fid, fid, null, DisplayItem.TYPE_FEATURE);
              lst.add(df);
              LOGGER.trace("Added F: {}", df);
              for (final FeatureValue fv : fc.getPossibleValues()) {
                final String fvid = fv.getFeatureValueID();
                final String value = fv.getValue();
                final DisplayItem dfv = new DisplayItem(fvid, value, null, DisplayItem.TYPE_FEATURE_VALUE);
                dfv.setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_FEATURE_VALUE, vid, fid, fvid));
                df.addChild(dfv);
                lst.add(dfv);
                LOGGER.trace("Added FV: {}", dfv);
              }
            }
            else if (c instanceof ConceptChoice) {
              final ConceptChoice cc = (ConceptChoice) c;
              final String vid = cc.getParentVariantID();
              for (final Concept con : cc.getConcepts()) {
                final String cid = con.getConceptID();
                final DisplayItem dc = new DisplayItem(cid, con.getLabel(), con.getDescription(), DisplayItem.TYPE_CONCEPT);
                dc.setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_CONCEPT, vid, cid));
                lst.add(dc);
                LOGGER.trace("Added C: {}", dc);
              }
            }
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
