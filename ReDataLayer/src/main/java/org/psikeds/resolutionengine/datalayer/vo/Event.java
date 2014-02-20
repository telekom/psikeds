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
 * An Event is defined by the ID of the Variant it is attached to,
 * its Context (= Path: Variant->Purpose->Variant->...) and the
 * triggering Entity, i.e. a Variant or Feature.
 * 
 * Note 1: Event-ID must be globally unique.
 * 
 * Note 2: Variant-ID must reference an existing Object!
 * 
 * Note 3: Context and Trigger of an Event must point to an Entity that
 * is located within the Subtree under the Variant this Event is
 * attached to.
 * 
 * @author marco@juliano.de
 * 
 */
public class Event extends ValueObject implements Serializable {

  public static final boolean DEFAULT_FEATURE_EVENT = false;
  public static final boolean DEFAULT_NOT_EVENT = false;

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String variantId;
  private Context context;
  private String trigger;
  private boolean featureEvent;
  private boolean notEvent;

  public Event() {
    this(null, null, null, null, null, null);
  }

  public Event(final String label, final String description, final String eventID, final String variantId, final Context context, final String trigger) {
    this(label, description, eventID, variantId, context, trigger, DEFAULT_FEATURE_EVENT, DEFAULT_NOT_EVENT);
  }

  public Event(final String label, final String description, final String eventID, final String variantId, final Context context, final String trigger,
      final boolean featureEvent, final boolean notEvent) {
    super(eventID);
    this.label = label;
    this.description = description;
    this.variantId = variantId;
    this.context = context;
    this.trigger = trigger;
    this.featureEvent = featureEvent;
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

  public String getVariantId() {
    return this.variantId;
  }

  public void setVariantId(final String variantId) {
    this.variantId = variantId;
  }

  public Context getContext() {
    return this.context;
  }

  public void setContext(final Context context) {
    this.context = context;
  }

  public String getTrigger() {
    return this.trigger;
  }

  public void setTrigger(final String trigger) {
    this.trigger = trigger;
  }

  public boolean isFeatureEvent() {
    return this.featureEvent;
  }

  public void setFeatureEvent(final boolean featureEvent) {
    this.featureEvent = featureEvent;
  }

  public boolean isNotEvent() {
    return this.notEvent;
  }

  public void setNotEvent(final boolean notEvent) {
    this.notEvent = notEvent;
  }
}
