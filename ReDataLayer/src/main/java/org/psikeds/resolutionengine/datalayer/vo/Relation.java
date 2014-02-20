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
 * A Relation is always attached to a Variant and defined as some logical
 * Operation/Dependency between two Features, defined by the corresponding
 * Feature-Events.
 * 
 * Example: Feature-Event1 greaterThan Feature-Event2
 * Semantic: The Value of Feature1 in the Context defined by Feature-Event1
 * must always be greater than the Value of Feature2 in the Context
 * defined by Feature-Event2.
 * 
 * Note 1: A Feature is a Definition of possible Values that can be referenced
 * at several places within the Knowledge-Base. Only the Feature-Event
 * points to a certain application of this Feature and its Value within
 * this Context.
 * 
 * Note 2: Relation-ID must be globally unique.
 * 
 * Note 3: Variant-Id, Left-Partner-Event-ID Right-Partner-Event-ID
 * must point to existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
public class Relation extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String variantID;
  private RelationOperator operator;
  private String leftPartnerEventID;
  private String rightPartnerEventID;

  public Relation() {
    this(null, null, null, null, null, null, null);
  }

  public Relation(final String label, final String description, final String relationID, final String variantID,
      final RelationOperator operator, final String leftPartnerEventID, final String rightPartnerEventID) {
    super(relationID);
    setLabel(label);
    setDescription(description);
    setVariantID(variantID);
    setOperator(operator);
    setLeftPartnerEventID(leftPartnerEventID);
    setRightPartnerEventID(rightPartnerEventID);
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

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public RelationOperator getOperator() {
    return this.operator;
  }

  public void setOperator(final RelationOperator operator) {
    this.operator = (operator != null ? operator : RelationOperator.EQUAL);
  }

  public String getLeftPartnerEventID() {
    return this.leftPartnerEventID;
  }

  public void setLeftPartnerEventID(final String leftPartnerEventID) {
    this.leftPartnerEventID = leftPartnerEventID;
  }

  public String getRightPartnerEventID() {
    return this.rightPartnerEventID;
  }

  public void setRightPartnerEventID(final String rightPartnerEventID) {
    this.rightPartnerEventID = rightPartnerEventID;
  }
}
