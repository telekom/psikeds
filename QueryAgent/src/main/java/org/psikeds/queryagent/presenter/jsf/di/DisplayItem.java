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
package org.psikeds.queryagent.presenter.jsf.di;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * An Item/Object that shall be used within a View in order to be displayed
 * on an HTML-Page. Besides being a serializable POJO it has some additional
 * features making the usage within the XHTML-Template more convenient.
 * 
 * @author marco@juliano.de
 */
public class DisplayItem implements Serializable {

  private static final long serialVersionUID = 1L;

  // some types that can be used in the xhtml-template to control layout and design
  public static final String TYPE_ERROR = "Error";
  public static final String TYPE_WARNING = "Warning";
  public static final String TYPE_ENTITY = "Entity";
  public static final String TYPE_PURPOSE = "Purpose";
  public static final String TYPE_VARIANT = "Variant";
  public static final String TYPE_FEATURE = "Feature";
  public static final String TYPE_FEATURE_VALUE = "FeatureValue";
  public static final String TYPE_SELECTED_FEATURE_VALUE = "SelectedFeatureValue";
  public static final String TYPE_CONCEPT_CHOICE = "ConceptChoice";
  public static final String TYPE_CONCEPT = "Concept";
  public static final String TYPE_LABEL = "Label";

  public static final int DEFAULT_LEVEL = 0;

  protected String key;
  protected String value;
  protected String longDescription;
  protected String selectionKey;
  protected String type;
  protected int level;
  protected List<DisplayItem> child;
  protected DisplayItem parent;

  public DisplayItem() {
    this(null, null);
  }

  public DisplayItem(final String id, final String txt) {
    this(id, id, txt, DisplayItem.TYPE_LABEL);
  }

  public DisplayItem(final String key, final String value, final String longDescription, final String type) {
    this(key, value, longDescription, type, null, DEFAULT_LEVEL);
  }

  public DisplayItem(final String key, final String value, final String longDescription, final String type, final String selectionKey, final int level) {
    this.child = null;
    this.parent = null;
    this.key = key;
    this.value = value;
    this.longDescription = longDescription;
    this.selectionKey = selectionKey;
    setLevel(level);
    setType(type);
  }

  // ------------------------------------------------------

  public String getKey() {
    // if there is no key we will fall back to the selection key
    return (StringUtils.isEmpty(this.key) ? this.selectionKey : this.key);
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public String getValue() {
    // if there is no value we will fall back to the long description
    return (StringUtils.isEmpty(this.value) ? this.longDescription : this.value);
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public String getLongDescription() {
    return this.longDescription;
  }

  public void setLongDescription(final String longDescription) {
    this.longDescription = longDescription;
  }

  public String getSelectionKey() {
    return this.selectionKey;
  }

  public void setSelectionKey(final String selectionKey) {
    this.selectionKey = selectionKey;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(final int level) {
    this.level = (level < DEFAULT_LEVEL ? DEFAULT_LEVEL : level);
  }

  public String getType() {
    return (StringUtils.isEmpty(this.type) ? TYPE_LABEL : this.type);
  }

  public void setType(final String type) {
    this.type = type;
  }

  public boolean isSelectable() {
    // a display item must have a selection key and must not be a plain text label in order to be selectable
    return (!StringUtils.isEmpty(this.selectionKey) && !TYPE_LABEL.equals(this.type));
  }

  // ------------------------------------------------------

  public DisplayItem getParent() {
    return this.parent;
  }

  public void setParent(final DisplayItem parent) {
    this.parent = parent;
    this.level = (parent == null ? DEFAULT_LEVEL : parent.getLevel() + 1); // inherit level from parent
  }

  public List<DisplayItem> getChild() {
    if (this.child == null) {
      this.child = new ArrayList<DisplayItem>();
    }
    return this.child;
  }

  public void setChild(final List<DisplayItem> lst) {
    this.child = lst;
  }

  public void addChild(final DisplayItem item) {
    if (item != null) {
      item.setParent(this);
      getChild().add(item);
    }
  }

  // ------------------------------------------------------

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (TYPE_LABEL.equalsIgnoreCase(this.type) || TYPE_WARNING.equalsIgnoreCase(this.type) || TYPE_ERROR.equalsIgnoreCase(this.type)) {
      return getValue(); // just a string, nothing else
    }
    else {
      // display all fields for debugging and logging
      final StringBuilder sb = new StringBuilder(super.toString());
      final String comma = ", ";
      sb.append(" [ ");
      sb.append(this.key);
      sb.append(comma);
      sb.append(this.value);
      sb.append(comma);
      sb.append(this.longDescription);
      sb.append(comma);
      sb.append(this.selectionKey);
      sb.append(comma);
      sb.append(this.type);
      sb.append(comma);
      sb.append(this.level);
      sb.append(" ]");
      return sb.toString();
    }
  }
}
