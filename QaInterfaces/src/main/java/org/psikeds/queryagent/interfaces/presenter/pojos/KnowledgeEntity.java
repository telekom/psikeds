/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a Knowledge-Entity, "one piece" of Knowledge.
 * A KE is a selected Variant for a certain Purpose. It can have siblings, i.e.
 * other KEs constituting this KE. There might also be Choices, i.e. Purposes
 * for which a constituting Variant must yet be selected.
 * 
 * Note: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "KnowledgeEntity")
public class KnowledgeEntity extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Purpose purpose;
  private Variant variant;
  private List<KnowledgeEntity> siblings;
  private List<Choice> choices;

  public KnowledgeEntity() {
    this(null, null);
  }

  /**
   * Leaf-KnowledgeEntity, only Purpose and Variant, no Siblings or Choices.
   * 
   * @param purpose
   * @param variant
   */
  public KnowledgeEntity(final Purpose purpose, final Variant variant) {
    this(purpose, variant, null, null);
  }

  /**
   * New KnowledgeEntity, not resolved, only Choices no Siblings yet
   * 
   * @param purpose
   * @param variant
   * @param choices
   */
  public KnowledgeEntity(final Purpose purpose, final Variant variant, final List<Choice> choices) {
    this(purpose, variant, null, choices);
  }

  public KnowledgeEntity(final Purpose purpose, final Variant variant, final List<KnowledgeEntity> siblings, final List<Choice> choices) {
    super(purpose, variant);
    this.purpose = purpose;
    this.variant = variant;
    this.siblings = siblings;
    this.choices = choices;
  }

  public Purpose getPurpose() {
    return this.purpose;
  }

  public void setPurpose(final Purpose purpose) {
    this.purpose = purpose;
  }

  public Variant getVariant() {
    return this.variant;
  }

  public void setVariant(final Variant variant) {
    this.variant = variant;
  }

  public List<KnowledgeEntity> getSiblings() {
    if (this.siblings == null) {
      this.siblings = new ArrayList<KnowledgeEntity>();
    }
    return this.siblings;
  }

  public void setSiblings(final List<KnowledgeEntity> siblings) {
    this.siblings = siblings;
  }

  public void addSibling(final KnowledgeEntity ke) {
    if (ke != null) {
      getSiblings().add(ke);
    }
  }

  public List<Choice> getChoices() {
    if (this.choices == null) {
      this.choices = new ArrayList<Choice>();
    }
    return this.choices;
  }

  public void setChoices(final List<Choice> choices) {
    this.choices = choices;
  }

  public void addChoice(final Choice chc) {
    if (chc != null) {
      getChoices().add(chc);
    }
  }
}
