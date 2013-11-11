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
 * A Rule is always attached to a Variant and defined as an Event triggering
 * the Rule (triggerEventID), an Event required as a Premise (premiseEventID)
 * and an Event that is fired as a conclusion (conclusionEventID).
 * 
 * Note 1: Rule-ID must be globally unique.
 * 
 * Note 2: Variant-Id, Trigger-Event-ID, Premise-Event-ID and Conclusion-Event-ID
 * must point to existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
public class Rule extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String variantID;
  private String triggerEventID;
  private String premiseEventID;
  private String conclusionEventID;

  public Rule() {
    this(null, null, null, null, null, null, null);
  }

  public Rule(final String label, final String description, final String ruleID, final String variantID, final String triggerEventID, final String premiseEventID, final String conclusionEventID) {
    super(ruleID);
    this.label = label;
    this.description = description;
    this.variantID = variantID;
    this.triggerEventID = triggerEventID;
    this.premiseEventID = premiseEventID;
    this.conclusionEventID = conclusionEventID;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String lbl) {
    this.label = lbl;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String desc) {
    this.description = desc;
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public String getTriggerEventID() {
    return this.triggerEventID;
  }

  public void setTriggerEventID(final String triggerEventID) {
    this.triggerEventID = triggerEventID;
  }

  public String getPremiseEventID() {
    return this.premiseEventID;
  }

  public void setPremiseEventID(final String premiseEventID) {
    this.premiseEventID = premiseEventID;
  }

  public String getConclusionEventID() {
    return this.conclusionEventID;
  }

  public void setConclusionEventID(final String conclusionEventID) {
    this.conclusionEventID = conclusionEventID;
  }
}
