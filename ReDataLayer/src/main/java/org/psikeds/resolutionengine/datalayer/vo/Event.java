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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

/**
 * An Event is defined by the ID of the Variant it is attached to,
 * its Context (= Path: Variant->Purpose->Variant->...) and the
 * triggering Entity, i.e. a Variant, a Feature-Value or a Concept.
 * 
 * Note 1: Event-ID must be globally unique.
 * 
 * Note 2: Variant-ID must reference an existing Object!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Event")
public class Event extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String TRIGGER_TYPE_VARIANT = "variant";
  public static final String TRIGGER_TYPE_FEATURE_VALUE = "feature";
  public static final String TRIGGER_TYPE_CONCEPT = "concept";

  public static final boolean DEFAULT_NOT_EVENT = false;

  private String label;
  private String description;
  private String variantID;
  private List<String> context;
  private String triggerID;
  private String triggerType;
  private boolean notEvent;

  public Event() {
    this(null, null);
  }

  public Event(final String eventID, final String variantID) {
    this(eventID, null, eventID, variantID, null, null, null);
  }

  public Event(final String label, final String description, final String eventID, final String variantID,
      final List<String> context, final String triggerID, final String triggerType) {
    this(label, description, eventID, variantID, context, triggerID, triggerType, DEFAULT_NOT_EVENT);
  }

  public Event(final String label, final String description, final String eventID, final String variantID,
      final List<String> context, final String triggerID, final String triggerType,
      final boolean notEvent) {
    super(eventID);
    setLabel(label);
    setDescription(description);
    setVariantID(variantID);
    setContext(context);
    setTriggerID(triggerID);
    setTriggerType(triggerType);
    setNotEvent(notEvent);
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

  public boolean addContextPathID(final String id) {
    return (!StringUtils.isEmpty(id) && getContext().add(id));
  }

  public String getTriggerID() {
    return this.triggerID;
  }

  public void setTriggerID(final String triggerID) {
    this.triggerID = triggerID;
  }

  public String getTriggerType() {
    return this.triggerType;
  }

  public void setTriggerType(final String type) {
    if (TRIGGER_TYPE_FEATURE_VALUE.equalsIgnoreCase(type)) {
      this.triggerType = TRIGGER_TYPE_FEATURE_VALUE;
    }
    else if (TRIGGER_TYPE_CONCEPT.equalsIgnoreCase(type)) {
      this.triggerType = TRIGGER_TYPE_CONCEPT;
    }
    else {
      this.triggerType = TRIGGER_TYPE_VARIANT;
    }
  }

  public boolean isNotEvent() {
    return this.notEvent;
  }

  public void setNotEvent(final boolean notEvent) {
    this.notEvent = notEvent;
  }
}
