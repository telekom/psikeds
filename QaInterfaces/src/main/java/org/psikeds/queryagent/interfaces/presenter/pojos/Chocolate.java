/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing one kind of chocolate. Note: Reading from and
 * writing to JSON works out of the box. However for XML the XmlRootElement
 * annotation is required.
 * 
 * @author marco@juliano.de
 */
@XmlRootElement(name = "Chocolate")
public class Chocolate {

  private String refid;
  private String description;
  private List<Ingredient> ingredients;

  public Chocolate() {
    this(null, null);
  }

  public Chocolate(final String refid, final String description) {
    this(refid, description, new ArrayList<Ingredient>());
  }

  public Chocolate(final String refid, final String description, final List<Ingredient> ingredients) {
    this.refid = refid;
    this.description = description;
    this.ingredients = ingredients;
  }

  public String getRefid() {
    return this.refid;
  }

  public void setRefid(final String refid) {
    this.refid = refid;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<Ingredient> getIngredients() {
    return this.ingredients;
  }

  public void setIngredients(final List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  // -----------------------------------------------------

  public int size() {
    return this.ingredients == null ? 0 : this.ingredients.size();
  }

  public void clear() {
    this.ingredients.clear();
  }

  public boolean add(final Ingredient ingr) {
    return this.ingredients.add(ingr);
  }

  public boolean addAll(final Collection<? extends Ingredient> col) {
    return this.ingredients.addAll(col);
  }

  // -----------------------------------------------------

  /**
   * @return
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("   ");
    sb.append(super.toString());
    sb.append(" [ refid = ");
    sb.append(String.valueOf(this.refid));
    sb.append(" | description = ");
    sb.append(String.valueOf(this.description));
    sb.append(" ]");
    for (int i = 0; i < size(); i++) {
      sb.append('\n');
      sb.append(String.valueOf(this.ingredients.get(i)));
    }
    return sb.toString();
  }
}
