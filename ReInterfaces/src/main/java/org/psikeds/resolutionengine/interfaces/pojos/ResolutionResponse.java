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
package org.psikeds.resolutionengine.interfaces.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response-Object representing the current Context of a Resolution
 * sent by the Server back to the Client.
 *
 * Note: Reading from and writing to JSON works out of the box.
 *       However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "ResolutionResponse")
public class ResolutionResponse extends BaseResolutionContext implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final long DEFAULT_MAX_TREE_DEPTHS = 15L;

  private List<Choice> choices;
  private boolean resolved;
  private long maxTreeDepth;

  public ResolutionResponse() {
    this(null, null);
  }

  public ResolutionResponse(final String sessionID, final Knowledge knowledge) {
    this(sessionID, null, knowledge);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge) {
    super(sessionID, metadata, knowledge);
    this.choices = null;
    this.resolved = false;
    this.maxTreeDepth = DEFAULT_MAX_TREE_DEPTHS;
    calculateChoices();
  }

  // -----------------------------------------------------------

  public boolean isResolved() {
    return this.resolved;
  }

  public void setResolved(final boolean f) {
    this.resolved = f;
  }

  public List<Choice> getPossibleChoices() {
    if (this.choices == null) {
      this.choices = new ArrayList<Choice>();
    }
    return this.choices;
  }

  public void setPossibleChoices(final List<Choice> lst) {
    this.choices = lst;
  }

  public void addPossibleChoice(final Choice choice) {
    if (choice != null) {
      getPossibleChoices().add(choice);
    }
  }

  public void addAllPossibleChoices(final List<Choice> lst) {
    if (lst != null && !lst.isEmpty()) {
      getPossibleChoices().addAll(lst);
    }
  }

  public void clearPossibleChoices() {
    if (this.choices != null) {
      this.choices.clear();
    }
  }

  public long getMaxTreeDepth() {
    return this.maxTreeDepth;
  }

  public void setMaxTreeDepth(final long depth) {
    this.maxTreeDepth = depth < 0 ? DEFAULT_MAX_TREE_DEPTHS : depth;
  }

  // -----------------------------------------------------------

  public boolean calculateChoices() {
    clearPossibleChoices();
    if (this.knowledge != null) {
      addAllPossibleChoices(this.knowledge.getChoices());
      addChoices(this.knowledge.getEntities(), this.getMaxTreeDepth());
      this.resolved = getPossibleChoices().isEmpty();
    }
    else {
      this.resolved = true;
    }
    return this.resolved;
  }

  private void addChoices(final List<KnowledgeEntity> entities, final long depth) {
    if (entities != null && !entities.isEmpty() && depth > 0) {
      for (final KnowledgeEntity ke : entities) {
        addChoices(ke, depth);
      }
    }
  }

  private void addChoices(final KnowledgeEntity ke, final long depth) {
    if (ke != null) {
      addAllPossibleChoices(ke.getChoices());
      addChoices(ke.getSiblings(), depth - 1);
    }
  }
}
