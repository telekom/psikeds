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
 * A Feature-Event, i.e. an Event triggered by a certain Value of a Feature.
 * 
 * Note: Context and Feature-ID of this Event must point to a Feature that is
 * located within the Subtree under the Variant this Event is attached to.
 * The triggering Value must also be an allowed Value of this Feature.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureEvent")
public class FeatureEvent extends Event implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String triggeringFeatureID;
  protected String triggeringFeatureValue;

  public FeatureEvent() {
    super();
    this.triggeringFeatureID = null;
    this.triggeringFeatureValue = null;
  }

  public FeatureEvent(final String label, final String description, final String eventID, final String attachedVariantId, final List<String> context, final String triggeringFeatureID,
      final String triggeringFeatureValue) {
    super(label, description, eventID, attachedVariantId, context);
    this.triggeringFeatureID = triggeringFeatureID;
    this.triggeringFeatureValue = triggeringFeatureValue;
  }

  public FeatureEvent(final String label, final String description, final String eventID, final String attachedVariantId, final List<String> context, final String triggeringFeatureID,
      final String triggeringFeatureValue, final boolean notEvent) {
    super(label, description, eventID, attachedVariantId, context, notEvent);
    this.triggeringFeatureID = triggeringFeatureID;
    this.triggeringFeatureValue = triggeringFeatureValue;
  }

  public String getTriggeringFeatureID() {
    return this.triggeringFeatureID;
  }

  public void setTriggeringFeatureID(final String triggeringFeatureID) {
    this.triggeringFeatureID = triggeringFeatureID;
  }

  public String getTriggeringFeatureValue() {
    return this.triggeringFeatureValue;
  }

  public void setTriggeringFeatureValue(final String triggeringFeatureValue) {
    this.triggeringFeatureValue = triggeringFeatureValue;
  }
}
