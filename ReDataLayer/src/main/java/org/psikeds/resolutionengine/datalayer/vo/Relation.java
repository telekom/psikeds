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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import org.springframework.util.StringUtils;

/**
 * A Relation is always attached to a Variant and defined as some logical
 * Operation/Dependency between two Features or a Feature and a constant Value,
 * defined by the corresponding Relation-Partners.
 * 
 * Optionally a conditional can be specified (conditionalEventID), meaning that
 * this Relation is only relevant if the corresponding Event was triggered.
 * 
 * Note 1: Relation-ID must be globally unique.
 * 
 * Note 2: Variant-ID must point to an existing Object!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Relation")
public class Relation extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final RelationOperator DEFAULT_OPERATOR = RelationOperator.DEFAULT_OPERATOR;

  private String label;
  private String description;
  private String variantID;
  private String conditionalEventID;
  private RelationParameter leftSide;
  private RelationParameter rightSide;
  private RelationOperator operator;

  public Relation() {
    this(null, null, null, null, null, null);
  }

  public Relation(final String label, final String description, final String relationID, final String variantID, final RelationParameter leftSide, final RelationParameter rightSide) {
    this(label, description, relationID, variantID, leftSide, rightSide, DEFAULT_OPERATOR);
  }

  public Relation(final String label, final String description, final String relationID, final String variantID,
      final RelationParameter leftSide, final RelationParameter rightSide, final RelationOperator operator) {
    this(label, description, relationID, variantID, leftSide, rightSide, operator, null);
  }

  public Relation(final String label, final String description, final String relationID, final String variantID,
      final RelationParameter leftSide, final RelationParameter rightSide, final RelationOperator operator,
      final String conditionalEventID) {
    super(relationID);
    setLabel(label);
    setDescription(description);
    setVariantID(variantID);
    setLeftSide(leftSide);
    setRightSide(rightSide);
    setOperator(operator);
    setConditionalEventID(conditionalEventID);
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

  public String getRelationID() {
    return getId();
  }

  public void setRelationID(final String relationID) {
    setId(relationID);
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
    this.operator = (operator != null ? operator : DEFAULT_OPERATOR);
  }

  public RelationParameter getLeftSide() {
    return this.leftSide;
  }

  public void setLeftSide(final RelationParameter leftSide) {
    this.leftSide = leftSide;
  }

  public RelationParameter getRightSide() {
    return this.rightSide;
  }

  public void setRightSide(final RelationParameter rightSide) {
    this.rightSide = rightSide;
  }

  public String getConditionalEventID() {
    return this.conditionalEventID;
  }

  public void setConditionalEventID(final String conditionalEventID) {
    this.conditionalEventID = conditionalEventID;
  }

  @JsonIgnore
  public boolean isConditional() {
    return (!StringUtils.isEmpty(getConditionalEventID()));
  }
}
