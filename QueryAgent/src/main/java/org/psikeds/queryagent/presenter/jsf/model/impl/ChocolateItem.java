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

import java.util.ArrayList;
import java.util.List;

import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate;
import org.psikeds.queryagent.interfaces.presenter.pojos.Ingredient;
import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Item used for displaying/selecting a single kind of chocolate.
 * 
 * @author marco@juliano.de
 */
public class ChocolateItem implements Item {

  private static final long serialVersionUID = 1L;

  private final List<IngredientItem> ingredlist = new ArrayList<IngredientItem>();

  private Chocolate choco;

  public ChocolateItem() {
    this(null);
  }

  public ChocolateItem(final Chocolate choco) {
    setChocolate(choco);
  }

  public Chocolate getChocolate() {
    return this.choco;
  }

  public void setChocolate(final Chocolate choco) {
    clear();
    this.choco = choco;
    if (this.choco != null) {
      for (final Ingredient ingr : this.choco.getIngredients()) {
        final IngredientItem item = new IngredientItem(ingr);
        this.ingredlist.add(item);
      }
    }
  }

  public void clear() {
    this.ingredlist.clear();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#isHavingSiblings()
   */
  @Override
  public boolean isHavingSiblings() {
    return this.ingredlist.size() > 0;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getSiblings()
   */
  @Override
  public List<IngredientItem> getSiblings() {
    return this.ingredlist;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getKey()
   */
  @Override
  public String getKey() {
    return this.choco == null ? "" : this.choco.getRefid();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getValue()
   */
  @Override
  public String getValue() {
    return this.choco == null ? "" : this.choco.getDescription();
  }

  /**
   * @return
   * @see org.psikeds.queryagent.presenter.jsf.model.Item#getHierarchyLevel()
   */
  @Override
  public int getHierarchyLevel() {
    return 1;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    sb.append('\n');
    sb.append(String.valueOf(this.choco));
    return sb.toString();
  }
}
