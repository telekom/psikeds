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
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import org.apache.commons.lang.StringUtils;

/**
 * A Rule is always attached to a Variant and defined by one or several Premise(s) (premiseEventID)
 * and a Conclusion (conclusionEventID).
 * 
 * Note 1: Rule-ID must be globally unique.
 * 
 * Note 2: Variant-ID, Premise-Event-IDs and Conclusion-Event-ID must point to
 * existing Objects!
 * 
 * IMPORTANT!!!
 * If there is exactly one Premise and this single Premise is identical to the
 * Nexus/Variant-ID, then this Premise is self-fulfilling and must not be interpreted
 * as an Event!!!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Rule")
public class Rule extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String variantID;
  private List<String> premiseEventID;
  private String conclusionEventID;

  public Rule() {
    this(null, null);
  }

  public Rule(final String ruleID, final String variantID) {
    this(ruleID, null, ruleID, variantID, (List<String>) null, null);
  }

  public Rule(final String label, final String description, final String ruleID, final String variantID, final String premiseEventID, final String conclusionEventID) {
    this(label, description, ruleID, variantID, (List<String>) null, conclusionEventID);
    addPremiseEventID(premiseEventID);
  }

  public Rule(final String label, final String description, final String ruleID, final String variantID, final List<String> premiseEventID, final String conclusionEventID) {
    super(ruleID);
    setLabel(label);
    setDescription(description);
    setVariantID(variantID);
    setPremiseEventID(premiseEventID);
    setConclusionEventID(conclusionEventID);
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

  public String getRuleID() {
    return getId();
  }

  public void setRuleID(final String ruleID) {
    setId(ruleID);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public String getConclusionEventID() {
    return this.conclusionEventID;
  }

  public void setConclusionEventID(final String conclusionEventID) {
    this.conclusionEventID = conclusionEventID;
  }

  public List<String> getPremiseEventID() {
    if (this.premiseEventID == null) {
      this.premiseEventID = new ArrayList<String>();
    }
    return this.premiseEventID;
  }

  public boolean addPremiseEventID(final String premiseEventID) {
    return (!StringUtils.isEmpty(premiseEventID) && getPremiseEventID().add(premiseEventID));
  }

  public boolean addPremiseEventID(final Collection<? extends String> col) {
    return ((col != null) && !col.isEmpty() && getPremiseEventID().addAll(col));
  }

  public void setPremiseEventID(final List<String> premiseEventID) {
    this.premiseEventID = premiseEventID;
  }

  @JsonIgnore
  public boolean isSelfFulfilling() {
    if ((this.premiseEventID != null) && (this.premiseEventID.size() == 1)) {
      final String premise = this.premiseEventID.get(0);
      if (!StringUtils.isEmpty(premise) && premise.equals(this.variantID)) {
        return true;
      }
    }
    return false;
  }
}
