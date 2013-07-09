/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [x] GNU Affero General Public License
 * [ ] GNU General Public License
 * [ ] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.resolutionengine.datalayer.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Chocolatelist {

  private List<Chocolate> chocolates;

  public Chocolatelist() {
    this.chocolates = new ArrayList<Chocolate>();
  }

  public Chocolatelist(final List<Chocolate> chocolates) {
    this.chocolates = chocolates;
  }

  public List<Chocolate> getChocolates() {
    return this.chocolates;
  }

  public void setChocolates(final List<Chocolate> chocolates) {
    this.chocolates = chocolates;
  }

  public int size() {
    return this.chocolates == null ? 0 : this.chocolates.size();
  }

  public boolean isEmpty() {
    return size() <= 0;
  }

  public boolean contains(final Chocolate choco) {
    return this.chocolates.contains(choco);
  }

  public boolean add(final Chocolate choco) {
    return this.chocolates.add(choco);
  }

  public boolean addAll(final Collection<? extends Chocolate> col) {
    return this.chocolates.addAll(col);
  }

  public void clear() {
    this.chocolates.clear();
  }
}
