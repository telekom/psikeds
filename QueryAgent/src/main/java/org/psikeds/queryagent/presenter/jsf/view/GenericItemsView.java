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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Generic implementation of the View-Interface delegating all calls
 * to an injected Bean.
 * 
 * @author marco@juliano.de
 */
public class GenericItemsView implements View {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(GenericItemsView.class);

  private Item delegateBean;

  public GenericItemsView() {
    this(null);
  }

  public GenericItemsView(final Item bean) {
    setDelegateBean(bean);
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.view.View#getDelegateBean()
   */
  @Override
  public Item getDelegateBean() {
    LOGGER.trace("Getting delegate {} from view {}", this.delegateBean, this);
    return this.delegateBean;
  }

  /**
   * @param delegateBean
   * @see org.psikeds.queryagent.presenter.jsf.view.View#setDelegateBean(org.psikeds.queryagent.presenter.jsf.model.Item)
   */
  @Override
  public void setDelegateBean(final Item bean) {
    this.delegateBean = bean;
    LOGGER.trace("Setting delegate {} for view {}", bean, this);
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getKey()
   */
  @Override
  public String getKey() {
    return getDelegateBean().getKey();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getValue()
   */
  @Override
  public String getValue() {
    return getDelegateBean().getValue();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#isHavingSiblings()
   */
  @Override
  public boolean isHavingSiblings() {
    return getDelegateBean().isHavingSiblings();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getSiblings()
   */
  @Override
  public List<? extends Item> getSiblings() {
    return getDelegateBean().getSiblings();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getHierarchyLevel()
   */
  @Override
  public int getHierarchyLevel() {
    return getDelegateBean().getHierarchyLevel();
  }
}
