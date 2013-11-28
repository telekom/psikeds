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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.presenter.jsf.model.KnowledgeRepresentation;

/**
 * A View interpreting all selected Variants as Application Packages that must
 * be installed on a W7 Cloud Instance.
 * The View is backed by a Model/Bean usually cached within Session-Scope.
 * 
 * @author marco@juliano.de
 */
public class W7AppPkgView extends BaseView {

  private static final Logger LOGGER = LoggerFactory.getLogger(W7AppPkgView.class);

  private String mapping;
  private boolean skipEmpty;

  public W7AppPkgView() {
    this(null, null, true);
  }

  public W7AppPkgView(final KnowledgeRepresentation model, final String mapping, final boolean skipEmpty) {
    super(model);
    this.mapping = mapping;
    this.skipEmpty = skipEmpty;
  }

  public String getMapping() {
    return this.mapping;
  }

  public void setMapping(final String mapping) {
    this.mapping = mapping;
  }

  public boolean isSkipEmpty() {
    return this.skipEmpty;
  }

  public void setSkipEmpty(final boolean skipEmpty) {
    this.skipEmpty = skipEmpty;
  }

  // ------------------------------------------------------

  public boolean isWithoutData() {
    return (isNotInitialized() || this.model.getKnowledge().getEntities().isEmpty());
  }

  public boolean isSoftwareRequired() {
    return getNumOfApps() > 0;

  }

  public int getNumOfApps() {
    // not the most efficient way, but sufficient for our purpose
    return (isWithoutData() ? 0 : getApplicationPackages().size());
  }

  /**
   * @return List<DisplayItem> of all application package names
   */
  public List<DisplayItem> getApplicationPackages() {
    // remove duplicates by first creating a map and than converting into list
    final Map<String, DisplayItem> map = new HashMap<String, DisplayItem>();
    List<DisplayItem> lst = null;
    try {
      LOGGER.trace("--> getApplicationPackages()");
      final Knowledge k = this.model.getKnowledge();
      for (final KnowledgeEntity ke : k.getEntities()) {
        add2map(map, ke);
      }
      lst = new ArrayList<DisplayItem>(map.values());
    }
    catch (final Exception ex) {
      LOGGER.error("getApplicationPackages() failed!", ex);
    }
    finally {
      LOGGER.trace("<-- getApplicationPackages(); lst = {}", lst);
    }
    return lst;
  }

  // ------------------------------------------------------

  private void add2map(final Map<String, DisplayItem> map, final KnowledgeEntity ke) {
    if (ke != null) {
      LOGGER.trace("Adding KE to Map: {}", ke);
      final Variant v = ke.getVariant();
      if (v != null) {
        add2map(map, v.getLabel(), v.getDescription());
      }
      for (final KnowledgeEntity sib : ke.getSiblings()) {
        add2map(map, sib);
      }
    }
  }

  private void add2map(final Map<String, DisplayItem> map, final String label, final String description) {
    if (StringUtils.isEmpty(label)) {
      LOGGER.trace("Skipping empty Label, this must never happen.");
      return;
    }
    String apppkg = description;
    if (StringUtils.isEmpty(apppkg)) {
      if (this.skipEmpty) {
        LOGGER.trace("Skipping Variant {} with empty Description.", label);
        return;
      }
      else {
        apppkg = label;
        LOGGER.trace("Falling back from Description to Label for Variant {}", label);
      }
    }
    if (!StringUtils.isEmpty(this.mapping)) {
      apppkg = String.format(this.mapping, apppkg);
    }
    LOGGER.trace("Adding App-Pkg-Name to Map: {}", apppkg);
    final DisplayItem di = new DisplayItem(apppkg, apppkg, null, DisplayItem.TYPE_LABEL);
    map.put(apppkg, di);
  }
}
