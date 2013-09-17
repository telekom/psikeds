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
package org.psikeds.queryagent.presenter.jsf.controller;

import java.util.List;

import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Controller used for selecting an Item via the JSF-Tag "selectOneRadio".
 * 
 * @author marco@juliano.de
 */
public interface SelectionController {

  /**
   * Get List of all Items currently available for Selection.
   * 
   * @return
   */
  List<? extends Item> getListOfItems();

  /**
   * Get unique ID of selected Item.
   * 
   * @return String Key / Unique ID
   */
  String getSelectedItem();

  /**
   * Set unique ID of selected Item.
   * 
   * @param key Key / Unique ID
   */
  void setSelectedItem(String key);

  /**
   * Perform the actual Selection including Calls to Backend, etc.
   * 
   * @return String JSF-Navigation-Outcome
   */
  String doSelect();
}
