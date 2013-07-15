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
package org.psikeds.queryagent.presenter.jsf.model.impl;

import java.util.List;

import org.psikeds.queryagent.interfaces.presenter.pojos.Ingredient;
import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Item used for displaying/selecting a single kind of chocolate.
 * 
 * @author marco@juliano.de
 */
public class IngredientItem implements Item {

  private Ingredient ingred;

  public IngredientItem() {
    this(null);
  }

  public IngredientItem(final Ingredient ingr) {
    setIngredient(ingr);
  }

  public Ingredient getIngredient() {
    return this.ingred;
  }

  public void setIngredient(final Ingredient ingr) {
    this.ingred = ingr;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#isHavingSiblings()
   */
  @Override
  public boolean isHavingSiblings() {
    return false;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getSiblings()
   */
  @Override
  public List<Item> getSiblings() {
    return null;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getKey()
   */
  @Override
  public String getKey() {
    return this.ingred == null ? "" : this.ingred.getRefid();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getValue()
   */
  @Override
  public String getValue() {
    return this.ingred == null ? "" : this.ingred.getDescription();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getHierarchyLevel()
   */
  @Override
  public int getHierarchyLevel() {
    return 2;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    sb.append('\n');
    sb.append(String.valueOf(this.ingred));
    return sb.toString();
  }
}
