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
package org.psikeds.queryagent.presenter.jsf.controller;

/**
 * Controller used for adding a new Item via the JSF-Tag "inputText".
 * 
 * @author marco@juliano.de
 */
public interface AddItemController {

  String getKey();

  void setKey(String key);

  String getValue();

  void setValue(String value);

  String doAdd();
}
