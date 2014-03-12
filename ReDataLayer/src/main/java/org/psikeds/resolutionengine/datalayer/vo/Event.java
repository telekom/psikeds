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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * An Event is defined by the ID of the Variant it is attached to,
 * its Context (= Path: Variant->Purpose->Variant->...) and the
 * triggering Entity, i.e. a Variant or a Feature-Value.
 * 
 * Note 1: Event-ID must be globally unique.
 * 
 * Note 2: Variant-ID must reference an existing Object!
 * 
 * @author marco@juliano.de
 * 
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = VariantEvent.class, name = "VariantEvent"), @JsonSubTypes.Type(value = FeatureEvent.class, name = "FeatureEvent"), })
public abstract class Event extends ValueObject {

  private static final long serialVersionUID = 1L;

  protected static final boolean DEFAULT_NOT_EVENT = false;

  protected String label;
  protected String description;
  protected String variantID;
  protected List<String> context;
  protected boolean notEvent;

  protected Event() {
    this(null, null, null, null, null);
  }

  protected Event(final String label, final String description, final String eventID, final String variantID, final List<String> context) {
    this(label, description, eventID, variantID, context, DEFAULT_NOT_EVENT);
  }

  protected Event(final String label, final String description, final String eventID, final String variantID, final List<String> context, final boolean notEvent) {
    super(eventID);
    this.label = label;
    this.description = description;
    this.variantID = variantID;
    this.context = context;
    this.notEvent = notEvent;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getEventID() {
    return getId();
  }

  public void setEventID(final String eventID) {
    setId(eventID);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public List<String> getContext() {
    if (this.context == null) {
      this.context = new ArrayList<String>();
    }
    return this.context;
  }

  public void setContext(final List<String> context) {
    this.context = context;
  }

  public void addContextPathID(final String id) {
    if (id != null) {
      getContext().add(id);
    }
  }

  public boolean isNotEvent() {
    return this.notEvent;
  }

  public void setNotEvent(final boolean notEvent) {
    this.notEvent = notEvent;
  }
}
