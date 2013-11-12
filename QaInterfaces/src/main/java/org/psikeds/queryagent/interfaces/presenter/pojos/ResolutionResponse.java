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
 * Response-Object representing the current Context of a Resolution
 * sent by the Server back to the Client.
 * 
 * Note: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ResolutionResponse")
public class ResolutionResponse extends BaseResolutionContext implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Choice> choices;
  private boolean resolved;

  public ResolutionResponse() {
    this(null, null);
  }

  public ResolutionResponse(final String sessionID, final Knowledge knowledge) {
    this(sessionID, null, knowledge);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge) {
    this(sessionID, metadata, knowledge, false);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge, final boolean resolved) {
    this(sessionID, metadata, knowledge, resolved, null);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge, final boolean resolved, final List<Choice> choices) {
    super(sessionID, metadata, knowledge);
    setResolved(resolved);
    setPossibleChoices(choices);
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
    if ((lst != null) && !lst.isEmpty()) {
      getPossibleChoices().addAll(lst);
    }
  }

  public void clearPossibleChoices() {
    if (this.choices != null) {
      this.choices.clear();
    }
  }
}
