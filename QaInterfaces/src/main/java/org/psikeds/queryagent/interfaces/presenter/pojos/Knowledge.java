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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing the current Knowledge, i.e. everything
 * we know so far.
 * 
 * Initially our Knowledge does not contain any Entities but only Choices,
 * one for each Root-Purpose. For every Choice made, it is removed from the
 * List of Choices and one or several corresponding Knowledge-Entities are
 * created. In the End there is a Tree of Knowledge-Entities and no Choices
 * left, then our Resolution-Process is finished.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Knowledge")
public class Knowledge extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private KnowledgeEntities entities;
  private VariantChoices choices;

  public Knowledge() {
    this(null);
  }

  public Knowledge(final VariantChoices choices) {
    this(null, choices);
  }

  public Knowledge(final KnowledgeEntities entities, final VariantChoices choices) {
    super();
    this.entities = entities;
    this.choices = choices;
  }

  // ----------------------------------------------------------------

  public KnowledgeEntities getEntities() {
    if (this.entities == null) {
      this.entities = new KnowledgeEntities();
    }
    return this.entities;
  }

  public void setEntities(final KnowledgeEntities entities) {
    clearEntities();
    this.entities = entities;
  }

  public void addKnowledgeEntity(final KnowledgeEntity ke) {
    if (ke != null) {
      getEntities().add(ke);
    }
  }

  public void addAllEntities(final KnowledgeEntities entities) {
    if ((entities != null) && !entities.isEmpty()) {
      getEntities().addAll(entities);
    }
  }

  public void clearEntities() {
    if (this.entities != null) {
      this.entities.clear();
      this.entities = null;
    }
  }

  // ----------------------------------------------------------------

  public VariantChoices getChoices() {
    if (this.choices == null) {
      this.choices = new VariantChoices();
    }
    return this.choices;
  }

  public void setChoices(final VariantChoices choices) {
    clearChoices();
    this.choices = choices;
  }

  public void addChoice(final VariantChoice choice) {
    if (choice != null) {
      getChoices().add(choice);
    }
  }

  public void addAllChoices(final VariantChoices choices) {
    if ((choices != null) && !choices.isEmpty()) {
      getChoices().addAll(choices);
    }
  }

  public void clearChoices() {
    if (this.choices != null) {
      this.choices.clear();
      this.choices = null;
    }
  }
}
