/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [x] GNU General Public License
 * [ ] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.jsf.controller.impl;

import org.psikeds.queryagent.interfaces.presenter.services.ChocolateService;
import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Base for all Controllers.
 * 
 * @author marco@juliano.de
 */
class BaseChocolateController {

  private Item allItemsBean;
  private Item selectedItemBean;
  private ChocolateService service;

  BaseChocolateController(final ChocolateService srvc, final Item all, final Item selected) {
    this.service = srvc;
    this.allItemsBean = all;
    this.selectedItemBean = selected;
  }

  public void setService(final ChocolateService srvc) {
    this.service = srvc;
  }

  public ChocolateService getService() {
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
