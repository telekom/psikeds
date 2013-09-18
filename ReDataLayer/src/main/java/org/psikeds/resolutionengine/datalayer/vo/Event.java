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
 * An Event is defined by its Context Path (Variant->Purpose->Variant->...)
 * and the ID of the Variant it is attached to.
 *
 * Note 1: ID must be globally unique.
 *
 * Note 2: VariantId must reference an existing Object!
 *
 * Note 3: Context Path of an Event must point to an Entity that is located
 *         within the Subtree under the Variant this Event is attached to.
 *
 * @author marco@juliano.de
 *
 */
public class Event extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String id;
  private ContextPath ctx;
  private String variantId;

  public Event() {
    this(null, null, null, null, null);
  }

  public Event(final String label, final String description, final String id, final ContextPath ctx, final String variantId) {
    super();
    this.label = label;
    this.description = description;
    this.id = id;
    this.ctx = ctx;
    this.variantId = variantId;
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

  public ContextPath getContextPath() {
    return this.ctx;
  }

  public void setContextPath(final ContextPath ctx) {
    this.ctx = ctx;
  }

  public String getVariantId() {
    return this.variantId;
  }

  public void setVariantId(final String variantId) {
    this.variantId = variantId;
  }
}
