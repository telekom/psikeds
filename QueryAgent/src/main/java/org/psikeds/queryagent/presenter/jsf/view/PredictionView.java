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

import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntities;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.POJO;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoices;
import org.psikeds.queryagent.presenter.jsf.di.ConceptDisplayItem;
import org.psikeds.queryagent.presenter.jsf.di.DisplayItem;
import org.psikeds.queryagent.presenter.jsf.di.FeatureDisplayItem;
import org.psikeds.queryagent.presenter.jsf.di.FeatureValueDisplayItem;
import org.psikeds.queryagent.presenter.jsf.di.KnowledgeEntityDisplayItem;
import org.psikeds.queryagent.presenter.jsf.di.PurposeDisplayItem;
import org.psikeds.queryagent.presenter.jsf.di.VariantDisplayItem;
import org.psikeds.queryagent.presenter.jsf.model.KnowledgeRepresentation;

/**
 * A View rendering the current Prediction as a Tree of DisplayItems.
 * Similar to the Knowledge-View but based on the Prediction not on
 * the current Knowledge.
 * 
 * @author marco@juliano.de
 * 
 */
public class PredictionView extends BaseView {

  private static final Logger LOGGER = LoggerFactory.getLogger(PredictionView.class);

  public PredictionView() {
    this(null);
  }

  public PredictionView(final KnowledgeRepresentation model) {
    super(model);
  }

  // ----------------------------------------------------------------

  @Override
  public boolean isResolved() {
    return ((this.model != null) && (this.model.getPrediction() != null) && this.model.getPrediction().isResolved());
  }

  @Override
  public String getSessionID() {
    return (isNotInitialized() ? null : this.model.getPrediction().getSessionID());
  }

  @Override
  public boolean isNotInitialized() {
    return ((this.model == null) || !this.model.hasPrediction());
  }

  @Override
  public boolean isWithoutData() {
    // whenever it is initialized we also have something to display
    return isNotInitialized();
  }

  public List<DisplayItem> getKnowledge() {
    final List<DisplayItem> knowledge = new ArrayList<DisplayItem>();
    try {
      LOGGER.trace("--> getKnowledge()");
      if (!isWithoutData()) {
        final Knowledge k = this.model.getPredictedKnowledge();
        addEntities(knowledge, k.getEntities());
        addVariantChoices(knowledge, k.getChoices());
      }
    }
    catch (final Exception ex) {
      LOGGER.error("getKnowledge() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- getKnowledge(); knowledge = {}", knowledge);
    }
    return knowledge;
  }

  // ----------------------------------------------------------------

  private void addVariantChoices(final List<DisplayItem> knowledge, final VariantChoices choices) {
    addVariantChoices(knowledge, choices, null);
  }

  private void addVariantChoices(final List<DisplayItem> knowledge, final VariantChoices choices, final DisplayItem parent) {
    for (final VariantChoice vc : choices) {
      final Purpose p = vc.getPurpose();
      final DisplayItem dp = new PurposeDisplayItem(p);
      if (parent != null) {
        parent.addChild(dp);
      }
      knowledge.add(dp);
      for (final Variant v : vc.getVariants()) {
        final VariantDisplayItem dv = new VariantDisplayItem(v);
        dp.addChild(dv);
        knowledge.add(dv);
      }
    }
  }

  // ----------------------------------------------------------------

  private void addFeatureChoices(final List<DisplayItem> knowledge, final FeatureChoices choices, final DisplayItem parent) {
    for (final FeatureChoice fc : choices) {
      final String fid = fc.getFeatureID();
      final Feature f = this.model.getFeature(fid);
      final DisplayItem df = new FeatureDisplayItem(f);
      if (parent != null) {
        parent.addChild(df);
      }
      knowledge.add(df);
      for (final FeatureValue fv : fc.getPossibleValues()) {
        final FeatureValueDisplayItem dfv = new FeatureValueDisplayItem(fv);
        df.addChild(dfv);
        dfv.setLevel(dfv.getLevel() - 1); // hack for displaying value on same level as parent-feature
        knowledge.add(dfv);
      }
    }
  }

  // ----------------------------------------------------------------

  private void addConceptChoices(final List<DisplayItem> knowledge, final ConceptChoices choices, final DisplayItem parent) {
    for (final ConceptChoice cc : choices) {
      for (final Concept con : cc.getConcepts()) {
        if (con != null) {
          final ConceptDisplayItem dc = new ConceptDisplayItem(con);
          if (parent != null) {
            parent.addChild(dc);
          }
          dc.setLevel(dc.getLevel() - 1); // hack for displaying concepts on the correct level
          knowledge.add(dc);
        }
      }
    }
  }

  // ----------------------------------------------------------------

  private void addEntities(final List<DisplayItem> knowledge, final KnowledgeEntities entities) {
    addEntities(knowledge, entities, null);
  }

  private void addEntities(final List<DisplayItem> knowledge, final KnowledgeEntities entities, final DisplayItem parent) {
    for (final KnowledgeEntity ke : entities) {
      final DisplayItem dke = new KnowledgeEntityDisplayItem(ke);
      if (parent != null) {
        parent.addChild(dke);
      }
      knowledge.add(dke);
      for (final FeatureValue fv : ke.getFeatures()) {
        final String id = POJO.composeId(fv.getFeatureID(), fv.getFeatureValueID());
        final String txt = fv.getFeatureID() + " = " + fv.getValue();
        final DisplayItem label = new DisplayItem(id, txt);
        dke.addChild(label);
        knowledge.add(label);
      }
      addEntities(knowledge, ke.getChildren(), dke);
      addVariantChoices(knowledge, ke.getPossibleVariants(), dke);
      addConceptChoices(knowledge, ke.getPossibleConcepts(), dke);
      addFeatureChoices(knowledge, ke.getPossibleFeatures(), dke);
    }
  }
}
