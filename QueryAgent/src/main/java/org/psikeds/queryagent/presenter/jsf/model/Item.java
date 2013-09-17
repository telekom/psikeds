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
package org.psikeds.queryagent.presenter.jsf.model;

import java.io.Serializable;
import java.util.List;

/**
 * An Item/Object that shall be used within a View in order to be displayed in
 * an XHTML-Page.
 *
 * Each Item must have a unique key (for selection) and a value that will be
 * displayed. An Item can optionally have siblings, i.e. child-objects that
 * shall also be displayed.
 *
 * @author marco@juliano.de
 */
public interface Item extends Serializable {

  /**
   * Unique Key of this Item
   * 
   * @return
   */
  String getKey();

  /**
   * Value/Description/Text of this Item to be displayed in GUI
   * 
   * @return
   */
  String getValue();

  /**
   * Does this Item have any Siblings, i.e. related child-objects?
   * 
   * @return
   */
  boolean isHavingSiblings();

  /**
   * List of all Siblings, i.e. related child-objects
   * 
   * @return
   */
  List<? extends Item> getSiblings();

  /**
   * What is the Level of this Item within the full Item-Hierarchy?
   * 0 = Root, 1 = First Level, 2 = Second Level, ...
   * 
   * @return
   */
  int getHierarchyLevel();
}
