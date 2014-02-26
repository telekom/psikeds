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
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Response-Object representing the current Context of a Resolution
 * sent by the Server back to the Client.
 * 
 * A Response contains either the current Knowledge and or Errors,
 * but not both!
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

  private Errors errors;
  private Choices choices;  // summary of all choices spread over the knowledge tree, will be calculated!

  public ResolutionResponse() {
    this(null, null, null, null);
  }

  public ResolutionResponse(final String sessionID, final Knowledge knowledge) {
    this(sessionID, null, knowledge);
  }

  public ResolutionResponse(final String sessionID, final Errors errors) {
    this(sessionID, null, errors);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge) {
    this(sessionID, metadata, knowledge, null);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Errors errors) {
    this(sessionID, metadata, null, errors);
  }

  // there are either errors or knowledge ... use one of the constructors above
  private ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge, final Errors errors) {
    super(sessionID, metadata, knowledge);
    setErrors(errors);
    calculateChoices();
  }

  // ----------------------------------------------------------------

  public Errors getErrors() {
    if (this.errors == null) {
      this.errors = new Errors();
    }
    return this.errors;
  }

  public void setErrors(final Errors errors) {
    clearErrors();
    this.errors = errors;
  }

  public void addError(final ErrorMessage error) {
    if (error != null) {
      getErrors().add(error);
    }
  }

  public void addAllErrors(final Errors errors) {
    if ((errors != null) && !errors.isEmpty()) {
      getErrors().addAll(errors);
    }
  }

  public void clearErrors() {
    if (this.errors != null) {
      this.errors.clear();
      this.errors = null;
    }
  }

  // ----------------------------------------------------------------

  public Choices getChoices() {
    if (this.choices == null) {
      this.choices = new Choices();
    }
    return this.choices;
  }

  public void setChoices(final Choices choices) {
    clearChoices();
    this.choices = choices;
  }

  public void addChoice(final Choice choice) {
    if (choice != null) {
      getChoices().add(choice);
    }
  }

  public void addAllChoices(final Choices choices) {
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

  // ----------------------------------------------------------------

  public void calculateChoices() {
    clearChoices();
    if (this.knowledge != null) {
      addAllChoices(this.knowledge.getChoices());
      addChoices(this.knowledge.getEntities());
    }
  }

  private void addChoices(final List<KnowledgeEntity> entities) {
    if ((entities != null) && !entities.isEmpty()) {
      for (final KnowledgeEntity ke : entities) {
        addChoices(ke);
      }
    }
  }

  private void addChoices(final KnowledgeEntity ke) {
    if (ke != null) {
      addAllChoices(ke.getChoices());
      addChoices(ke.getChildren());
    }
  }

  // ----------------------------------------------------------------

  @JsonIgnore
  public boolean isResolved() {
    return ((this.choices == null) || this.choices.isEmpty());
  }

  @JsonIgnore
  public boolean hasError() {
    return ((this.errors != null) && !this.errors.isEmpty());
  }
}
