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
package org.psikeds.queryagent.presenter.jsf.view;

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
  public static final String TYPE_ENTITY = "Entity";                // a knowledge entity (selected variant for a purpose)
  public static final String TYPE_PURPOSE = "Purpose";              // a purpose
  public static final String TYPE_VARIANT = "Variant";              // a variant
  public static final String TYPE_FEATURE = "Feature";              // a feature
  public static final String TYPE_FEATURE_VALUE = "FeatureValue";   // a value of a feature
  public static final String TYPE_CONCEPT = "Concept";              // a concept
  public static final String TYPE_LABEL = "Label";                  // everything else, i.e. just some string

  public static final String DEFAULT_TYPE = TYPE_LABEL;
  public static final int DEFAULT_LEVEL = 0;

  private String key;
  private String value;
  private String longDescription;
  private String selectionKey;
  private String type;
  private int level;
  private List<DisplayItem> child;
  private DisplayItem parent;

  public DisplayItem() {
    this(null, null, null, DEFAULT_TYPE);
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
    return this.type;
  }

  public void setType(final String type) {
    if (TYPE_ENTITY.equalsIgnoreCase(type)) {
      this.type = TYPE_ENTITY;
    }
    else if (TYPE_PURPOSE.equalsIgnoreCase(type)) {
      this.type = TYPE_PURPOSE;
    }
    else if (TYPE_VARIANT.equalsIgnoreCase(type)) {
      this.type = TYPE_VARIANT;
    }
    else if (TYPE_FEATURE.equalsIgnoreCase(type)) {
      this.type = TYPE_FEATURE;
    }
    else if (TYPE_FEATURE_VALUE.equalsIgnoreCase(type)) {
      this.type = TYPE_FEATURE_VALUE;
    }
    else if (TYPE_CONCEPT.equalsIgnoreCase(type)) {
      this.type = TYPE_CONCEPT;
    }
    else {
      // everything else is just a plain text label
      this.type = TYPE_LABEL;
    }
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
    if (TYPE_LABEL.equals(this.type)) {
      return getValue(); // a label is just a string, nothing else
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
