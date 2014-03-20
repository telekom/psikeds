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
package org.psikeds.queryagent.presenter.jsf.view;

import org.psikeds.queryagent.presenter.jsf.model.KnowledgeRepresentation;

/**
 * Base Class of all Views. It holds a Model / Representation of our
 * current Knowledge and provides some general Properties that can be
 * used in XHTML-Templates.
 * 
 * @author marco@juliano.de
 */
public abstract class BaseView {

  protected KnowledgeRepresentation model;

  public BaseView(final KnowledgeRepresentation model) {
    this.model = model;
  }

  public KnowledgeRepresentation getModel() {
    return this.model;
  }

  public void setModel(final KnowledgeRepresentation model) {
    this.model = model;
  }

  public boolean isResolved() {
    return ((this.model != null) && this.model.isResolved());
  }

  public boolean hasErrors() {
    return ((this.model == null) || this.model.hasErrors());
  }

  public boolean hasWarnings() {
    return ((this.model != null) && this.model.hasWarnings());
  }

  public String getSessionID() {
    return (this.model == null ? null : this.model.getSessionID());
  }

  public boolean isNotInitialized() {
    return ((this.model == null) || this.model.isNotInitialized());
  }
}