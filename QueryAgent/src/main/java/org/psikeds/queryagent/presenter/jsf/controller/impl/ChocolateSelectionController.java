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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;
import org.psikeds.queryagent.presenter.jsf.controller.SelectionController;
import org.psikeds.queryagent.presenter.jsf.model.Item;
import org.psikeds.queryagent.presenter.jsf.util.Constants;

/**
 * Implementation of SelectionController for selecting one kind of Chocolate.
 * 
 * @author marco@juliano.de
 */
public class ChocolateSelectionController extends BaseChocolateController implements SelectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChocolateSelectionController.class);

  private String selectedKey;

  public ChocolateSelectionController() {
    this(null, null, null);
  }

  public ChocolateSelectionController(final ResolutionService cs, final Item all, final Item selected) {
    super(cs, all, selected);
    initSelectedItem();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.controller.SelectionController#getListOfItems()
   */
  @Override
  public List<? extends Item> getListOfItems() {
    //    final ChocolatelistItem allItems = (ChocolatelistItem) getAllItemsBean();
    //    return allItems.getSiblings();
    return null;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.controller.SelectionController#getSelectedItem()
   */
  @Override
  public String getSelectedItem() {
    return this.selectedKey;
  }

  /**
   * @param key
   * @see org.psikeds.queryagent.presenter.jsf.controller.SelectionController#setSelectedItem(java.lang.String)
   */
  @Override
  public void setSelectedItem(final String key) {
    this.selectedKey = key;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.controller.SelectionController#doSelect()
   */
  @Override
  public String doSelect() {
    String ret = Constants.RESULT_ERROR;
    try {
      LOGGER.trace("--> doSelect()");
      if (StringUtils.isEmpty(this.selectedKey)) {
        throw new IllegalArgumentException("Nothing selected. Selected-Key is empty!");
      }
      //      LOGGER.debug("Selecting chocolate with RefId = {}", this.selectedKey);
      //      final Chocolate choco = getService().selectChocolate(this.selectedKey);
      //      final ChocolateItem selectedItem = (ChocolateItem) getSelectedItemBean();
      //      selectedItem.setChocolate(choco);
      //      LOGGER.trace("Updated selectedItem = {}", selectedItem);
      ret = Constants.RESULT_SUCCESS;
    }
    catch (final Exception ex) {
      ret = Constants.RESULT_ERROR;
      LOGGER.error("Could not select Chocolate [{}] - {}", this.selectedKey, ex);
    }
    finally {
      LOGGER.trace("<-- doSelect(); ret = {}", ret);
    }
    return ret;
  }

  @Override
  public void setSelectedItemBean(final Item selectedItemBean) {
    super.setSelectedItemBean(selectedItemBean);
    initSelectedItem();
  }

  // pre-select last choice, or initially first item in list
  private void initSelectedItem() {
    try {
      LOGGER.trace("--> initSelectedItem()");
      //      final ChocolateItem selectedItem = (ChocolateItem) getSelectedItemBean();
      //      final Chocolate choco = selectedItem == null ? null : selectedItem.getChocolate();
      //      final String key = choco == null ? null : choco.getRefid();
      //      LOGGER.trace("Got {} from {}", key, selectedItem);
      final String key = null;
      //      if (StringUtils.isEmpty(key)) {
      //        final ChocolatelistItem allBean = (ChocolatelistItem) getAllItemsBean();
      //        final List<Item> lst = allBean == null ? null : allBean.getSiblings();
      //        final int size = lst == null ? 0 : lst.size();
      //        if (size > 0) {
      //          key = lst.get(0).getKey();
      //          LOGGER.trace("Got {} from {}", key, allBean);
      //        }
      //      }
      this.selectedKey = key;
    }
    finally {
      LOGGER.trace("<-- initSelectedItem(); selectedKey = {}", this.selectedKey);
    }
  }
}
