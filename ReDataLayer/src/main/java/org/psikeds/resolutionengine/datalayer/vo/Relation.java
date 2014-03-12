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

/**
 * A Relation is always attached to a Variant and defined as some logical
 * Operation/Dependency between two Features, defined by the corresponding
 * Relation-Partners.
 * 
 * Example: Left greaterThan Right
 * Semantic: The Value of the "Left" Feature must always be greater than
 * the Value of the "Right" Feature.
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
  private String variantId;
  private RelationPartner leftSide;
  private RelationPartner rightSide;
  private RelationOperator operator;

  public Relation() {
    this(null, null, null, null, null, null, null);
  }

  public Relation(final String label, final String description, final String relationID, final String variantId, final RelationPartner leftSide, final RelationPartner rightSide) {
    this(label, description, relationID, variantId, leftSide, rightSide, DEFAULT_OPERATOR);
  }

  public Relation(final String label, final String description, final String relationID, final String variantId,
      final RelationPartner leftSide, final RelationPartner rightSide, final RelationOperator operator) {
    super(relationID);
    setLabel(label);
    setDescription(description);
    setVariantID(variantId);
    setLeftSide(leftSide);
    setRightSide(rightSide);
    setOperator(operator);
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
    return this.variantId;
  }

  public void setVariantID(final String variantId) {
    this.variantId = variantId;
  }

  public RelationOperator getOperator() {
    return this.operator;
  }

  public void setOperator(final RelationOperator operator) {
    this.operator = (operator != null ? operator : DEFAULT_OPERATOR);
  }

  public RelationPartner getLeftSide() {
    return this.leftSide;
  }

  public void setLeftSide(final RelationPartner leftSide) {
    this.leftSide = leftSide;
  }

  public RelationPartner getRightSide() {
    return this.rightSide;
  }

  public void setRightSide(final RelationPartner rightSide) {
    this.rightSide = rightSide;
  }
}
