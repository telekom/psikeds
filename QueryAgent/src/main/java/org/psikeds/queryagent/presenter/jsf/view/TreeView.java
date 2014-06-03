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

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValues;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.POJO;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoices;
import org.psikeds.queryagent.presenter.jsf.model.KnowledgeRepresentation;
import org.psikeds.queryagent.presenter.jsf.util.SelectionHelper;

/**
 * A View rendering the current Knowledge as a Tree of DisplayItems.
 * The View is backed by a Model/Bean usually cached within Session-Scope.
 * 
 * @author marco@juliano.de
 */
public class TreeView extends BaseView {

  private static final Logger LOGGER = LoggerFactory.getLogger(TreeView.class);

  private String mapping;

  public TreeView() {
    this(null, null);
  }

  public TreeView(final KnowledgeRepresentation model, final String mapping) {
    super(model);
    this.mapping = mapping;
  }

  public String getMapping() {
    return this.mapping;
  }

  public void setMapping(final String mapping) {
    this.mapping = mapping;
  }

  // ----------------------------------------------------------------

  @Override
  public boolean isWithoutData() {
    // whenever it is initialized we also have something to display
    return isNotInitialized();
  }

  public List<DisplayItem> getKnowledge() {
    final List<DisplayItem> lst = new ArrayList<DisplayItem>();
    try {
      LOGGER.trace("--> getKnowledge()");
      if (!isWithoutData()) {
        final Knowledge k = this.model.getKnowledge();
        addEntities(lst, k.getEntities());
        addVariantChoices(lst, k.getChoices());
      }
    }
    catch (final Exception ex) {
      LOGGER.error("getKnowledge() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- getKnowledge(); lst = {}", lst);
    }
    return lst;
  }

  // ----------------------------------------------------------------

  private void addVariantChoices(final List<DisplayItem> lst, final VariantChoices choices) {
    addVariantChoices(lst, choices, null);
  }

  private void addVariantChoices(final List<DisplayItem> lst, final VariantChoices choices, final DisplayItem parent) {
    if ((lst != null) && (choices != null)) {
      for (final VariantChoice vc : choices) {
        final Purpose p = vc.getPurpose();
        final DisplayItem dp = new DisplayItem(p.getPurposeID(), p.getLabel(), p.getDescription(), DisplayItem.TYPE_PURPOSE);
        if (parent != null) {
          parent.addChild(dp);
        }
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
    }
  }

  // ----------------------------------------------------------------

  private void addFeatureChoices(final List<DisplayItem> lst, final FeatureChoices choices, final DisplayItem parent) {
    if ((lst != null) && (choices != null)) {
      for (final FeatureChoice fc : choices) {
        final String vid = fc.getParentVariantID();
        final Variant v = this.model.getVariant(vid);
        final String vlbl = (v == null ? vid : v.getLabel());
        final String fid = fc.getFeatureID();
        final Feature f = (fid == null ? null : this.model.getFeature(fid));
        final String flbl = (f == null ? fid : f.getLabel());
        final String fdesc = (f == null ? fid : f.getDescription());
        final DisplayItem df = new DisplayItem(fid, flbl, fdesc, DisplayItem.TYPE_FEATURE);
        if (parent != null) {
          parent.addChild(df);
        }
        lst.add(df);
        LOGGER.trace("Added F: {}", df);
        for (final FeatureValue fv : fc.getPossibleValues()) {
          final String fvid = fv.getFeatureValueID();
          final String value = fv.getValue();
          final DisplayItem dfv = new DisplayItem(fvid, value, vlbl, DisplayItem.TYPE_FEATURE_VALUE);
          dfv.setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_FEATURE_VALUE, vid, fid, fvid));
          df.addChild(dfv);
          dfv.setLevel(dfv.getLevel() - 1); // hack for displaying value on same level as parent-feature
          lst.add(dfv);
          LOGGER.trace("Added FV: {}", dfv);
        }
      }
    }
  }

  // ----------------------------------------------------------------

  private void addConceptChoices(final List<DisplayItem> lst, final ConceptChoices choices, final DisplayItem parent) {
    if ((lst != null) && (choices != null)) {
      for (final ConceptChoice cc : choices) {
        final String vid = cc.getParentVariantID();
        for (final Concept con : cc.getConcepts()) {
          if (con != null) {
            final String cid = con.getConceptID();
            final String val = con.getLabel();
            String desc = con.getDescription();
            final FeatureValues fvs = con.getValues();
            if ((fvs != null) && !fvs.isEmpty()) {
              final StringBuilder sb = new StringBuilder();
              for (final FeatureValue fv : fvs) {
                if (sb.length() > 0) {
                  sb.append(", ");
                }
                sb.append(fv.getFeatureID());
                sb.append(" = ");
                sb.append(fv.getValue());
              }
              desc = "( " + sb.toString() + " )";
            }
            final DisplayItem dc = new DisplayItem(cid, val, desc, DisplayItem.TYPE_CONCEPT);
            dc.setSelectionKey(SelectionHelper.createSelectionString(SelectionHelper.SELECTION_TYPE_CONCEPT, vid, cid));
            if (parent != null) {
              parent.addChild(dc);
            }
            lst.add(dc);
            LOGGER.trace("Added C: {}", dc);
          }
        }
      }
    }
  }

  // ----------------------------------------------------------------

  private void addEntities(final List<DisplayItem> lst, final List<KnowledgeEntity> entities) {
    addEntities(lst, entities, null);
  }

  private void addEntities(final List<DisplayItem> lst, final List<KnowledgeEntity> entities, final DisplayItem parent) {
    if ((lst != null) && (entities != null)) {
      for (final KnowledgeEntity ke : entities) {
        if (ke != null) {
          final DisplayItem dke = createDI(ke);
          if (parent != null) {
            parent.addChild(dke);
          }
          lst.add(dke);
          LOGGER.trace("Added KE: {}", dke);
          for (final FeatureValue fv : ke.getFeatures()) {
            if (fv != null) {
              final DisplayItem label = createDI(fv);
              dke.addChild(label);
              lst.add(label);
              LOGGER.trace("Added LBL: {}", label);
            }
          }
          addEntities(lst, ke.getChildren(), dke);
          addVariantChoices(lst, ke.getPossibleVariants(), dke);
          addFeatureChoices(lst, ke.getPossibleFeatures(), dke);
          addConceptChoices(lst, ke.getPossibleConcepts(), dke);
        }
      }
    }
  }

  // ----------------------------------------------------------------

  private DisplayItem createDI(final KnowledgeEntity ke) {
    final Purpose p = ke.getPurpose();
    final Variant v = ke.getVariant();
    final String str = p.getLabel();
    final String desc = (StringUtils.isEmpty(this.mapping) ? v.getLabel() : String.format(this.mapping, v.getLabel()));
    return new DisplayItem(str, str, desc, DisplayItem.TYPE_ENTITY);
  }

  private DisplayItem createDI(final FeatureValue fv) {
    final String id = POJO.composeId(fv.getFeatureID(), fv.getFeatureValueID());
    final String txt = fv.getFeatureID() + " = " + fv.getValue();
    return createDI(id, txt);
  }

  private DisplayItem createDI(final String id, final String txt) {
    return new DisplayItem(id, id, txt, DisplayItem.TYPE_LABEL);
  }
}
