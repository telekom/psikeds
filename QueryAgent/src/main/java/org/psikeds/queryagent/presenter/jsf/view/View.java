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
package org.psikeds.queryagent.presenter.jsf.view;

import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * A View-Object to be displayed in an XHTML-Page. The View is backed by a
 * Delegate Bean, i.e. an Item usually cached within Session-Scope.
 * 
 * @author marco@juliano.de
 */
public interface View extends Item {

  Item getDelegateBean();

  void setDelegateBean(Item delegateBean);
}
