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
package org.psikeds.resolutionengine.datalayer.knowledgebase.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Chocolate;
import org.psikeds.resolutionengine.datalayer.vo.Chocolatelist;

public class KnowledgeBaseImpl implements KnowledgeBase {

  private static final String KEY_ALL_CHOCOLATES = "all.choco.list";
  private static final String KEY_PREFIX_CHOCOLATE = "choco.ref.";

  protected final Map<String, Object> chocolates;

  public KnowledgeBaseImpl() {
    this.chocolates = new ConcurrentHashMap<String, Object>();
  }

  public KnowledgeBaseImpl(final List<Chocolate> chocolst) {
    this();
    setChocolates(chocolst);
  }

  public KnowledgeBaseImpl(final Chocolatelist chocolst) {
    this(chocolst.getChocolates());
  }

  public void setChocolates(final List<Chocolate> chocolst) {
    this.chocolates.clear();
    storeChocolatelist(chocolst);
    for (final Chocolate choco : chocolst) {
      storeChocolate(choco);
    }
  }

  // -------------------------------------------------------------

  @Override
  @SuppressWarnings("unchecked")
  // we know that this is a list of chocolates
  public List<Chocolate> getChocolates() {
    return (List<Chocolate>) this.chocolates.get(KEY_ALL_CHOCOLATES);
  }

  @Override
  public Chocolate getChocolate(final String refid) {
    return (Chocolate) this.chocolates.get(KEY_PREFIX_CHOCOLATE + refid);
  }

  @Override
  public void addChocolate(final Chocolate c) {
    this.getChocolates().add(c);
    storeChocolate(c);
  }

  // -------------------------------------------------------------

  private void storeChocolatelist(final List<Chocolate> chocolst) {
    this.chocolates.put(KEY_ALL_CHOCOLATES, chocolst);
  }

  private void storeChocolate(final Chocolate choco) {
    this.chocolates.put(KEY_PREFIX_CHOCOLATE + choco.getRefid(), choco);
  }
}
