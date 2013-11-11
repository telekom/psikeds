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
 * Interface object representing the current Knowledge, i.e. everything
 * we know so far.
 * 
 * Initially our Knowledge does not contain any Entities but only Choices,
 * one for each Root-Purpose. For every Choice made, it is removed from the
 * List of Choices and one or several corresponding Knowledge-Entities are
 * created. In the End there is a Tree of Knowledge-Entities and no Choices
 * left, then our Resolution-Process is finished.
 * 
 * The Knowledge-Datastructure is the main Object-Model that must/can be
 * displayed in a GUI, e.g. rendering Knowledge-Entities as a Tree and Choices
 * as Radio-Buttons or Checkboxes in HTML.
 * 
 * Note: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Knowledge")
public class Knowledge extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<KnowledgeEntity> entities;
  private List<Choice> choices;

  public Knowledge() {
    this(null);
  }

  public Knowledge(final List<Choice> choices) {
    this(null, choices);
  }

  public Knowledge(final List<KnowledgeEntity> entities, final List<Choice> choices) {
    super();
    this.entities = entities;
    this.choices = choices;
  }

  public List<KnowledgeEntity> getEntities() {
    if (this.entities == null) {
      this.entities = new ArrayList<KnowledgeEntity>();
    }
    return this.entities;
  }

  public void setEntities(final List<KnowledgeEntity> entities) {
    this.entities = entities;
  }

  public void addKnowledgeEntity(final KnowledgeEntity ke) {
    if (ke != null) {
      getEntities().add(ke);
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
