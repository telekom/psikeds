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
package org.psikeds.queryagent.presenter.jsf.controller.impl;

import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;
import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Base for all Controllers.
 * 
 * @author marco@juliano.de
 */
public class BaseChocolateController {

  Item allItemsBean;
  Item selectedItemBean;
  ResolutionService service;

  public BaseChocolateController(final ResolutionService srvc, final Item all, final Item selected) {
    this.service = srvc;
    this.allItemsBean = all;
    this.selectedItemBean = selected;
  }

  public void setService(final ResolutionService srvc) {
    this.service = srvc;
  }

  public ResolutionService getService() {
    return this.service;
  }

  public Item getAllItemsBean() {
    return this.allItemsBean;
  }

  public void setAllItemsBean(final Item allItemsBean) {
    this.allItemsBean = allItemsBean;
  }

  public Item getSelectedItemBean() {
    return this.selectedItemBean;
  }

  public void setSelectedItemBean(final Item selectedItemBean) {
    this.selectedItemBean = selectedItemBean;
  }
}
