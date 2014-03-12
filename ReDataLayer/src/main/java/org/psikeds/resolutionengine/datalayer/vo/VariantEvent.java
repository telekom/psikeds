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
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Variant-Event, i.e. an Event triggered by the Selection of a certain
 * Variant for a Purpose.
 * 
 * Note: Context and triggering Variant-ID of this Event must point to a
 * Variant that is located within the Subtree under the Variant this Event
 * is attached to.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "VariantEvent")
public class VariantEvent extends Event implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String triggeringVariantId;

  public VariantEvent() {
    super();
    this.triggeringVariantId = null;
  }

  public VariantEvent(final String label, final String description, final String eventID, final String attachedVariantId, final List<String> context, final String triggeringVariantId) {
    super(label, description, eventID, attachedVariantId, context);
    this.triggeringVariantId = triggeringVariantId;
  }

  public VariantEvent(final String label, final String description, final String eventID, final String attachedVariantId, final List<String> context, final String triggeringVariantId,
      final boolean notEvent) {
    super(label, description, eventID, attachedVariantId, context, notEvent);
    this.triggeringVariantId = triggeringVariantId;
  }

  public String getTriggeringVariantId() {
    return this.triggeringVariantId;
  }

  public void setTriggeringVariantId(final String triggeringVariantId) {
    this.triggeringVariantId = triggeringVariantId;
  }
}
