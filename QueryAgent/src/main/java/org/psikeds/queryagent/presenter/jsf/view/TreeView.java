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

import org.psikeds.queryagent.interfaces.presenter.pojos.Choice;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
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

  // ------------------------------------------------------

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
        addChoices(lst, k.getChoices());
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

  // ------------------------------------------------------

  private void addChoices(final List<DisplayItem> lst, final List<Choice> choices) {
    addChoices(lst, choices, null);
  }

  private void addChoices(final List<DisplayItem> lst, final List<Choice> choices, final DisplayItem parent) {
    if ((lst != null) && (choices != null)) {
      for (final Choice c : choices) {
        final Purpose p = c.getPurpose();
        final DisplayItem dp = new DisplayItem(p.getId(), p.getLabel(), p.getDescription(), DisplayItem.TYPE_PURPOSE);
        if (parent != null) {
          parent.addChild(dp);
        }
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

  // ------------------------------------------------------

  private void addEntities(final List<DisplayItem> lst, final List<KnowledgeEntity> entities) {
    addEntities(lst, entities, null);
  }

  private void addEntities(final List<DisplayItem> lst, final List<KnowledgeEntity> entities, final DisplayItem parent) {
    if ((lst != null) && (entities != null)) {
      for (final KnowledgeEntity ke : entities) {
        final DisplayItem dke = createDisplayItem(ke);
        if (dke != null) {
          if (parent != null) {
            parent.addChild(dke);
          }
          lst.add(dke);
          LOGGER.trace("Added KE: {}", dke);
          addEntities(lst, ke.getSiblings(), dke);
          addChoices(lst, ke.getChoices(), dke);
        }
      }
    }
  }

  private DisplayItem createDisplayItem(final KnowledgeEntity ke) {
    DisplayItem di = null;
    if (ke != null) {
      final Purpose p = ke.getPurpose();
      final Variant v = ke.getVariant();
      final String key = SelectionHelper.createSelectionString(p, v);
      final String value = p.getLabel();
      final String desc = (StringUtils.isEmpty(this.mapping) ? v.getLabel() : String.format(this.mapping, v.getLabel()));
      di = new DisplayItem(key, value, desc, DisplayItem.TYPE_ENTITY);
    }
    return di;
  }
}
