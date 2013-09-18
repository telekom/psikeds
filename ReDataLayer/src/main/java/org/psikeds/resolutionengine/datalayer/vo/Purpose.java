/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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

import java.io.Serializable;

/**
 * A single Purpose. Purposes marked with the root-flag are used for the
 * initial context of a resolution.
 *
 * Note: ID must be globally unique.
 *
 * @author marco@juliano.de
 *
 */
public class Purpose extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String id;
  private boolean root;

  public Purpose() {
    this(null, null, null);
  }

  public Purpose(final String label, final String description, final String id) {
    this(label, description, id, false);
  }

  public Purpose(final String label, final String description, final String id, final boolean root) {
    super();
    this.label = label;
    this.description = description;
    this.id = id;
    this.root = root;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String value) {
    this.label = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String value) {
    this.description = value;
  }

  public String getId() {
    return this.id;
  }

  public void setId(final String value) {
    this.id = value;
  }

  public boolean isRoot() {
    return this.root;
  }

  public void setRoot(final boolean root) {
    this.root = root;
  }
}
