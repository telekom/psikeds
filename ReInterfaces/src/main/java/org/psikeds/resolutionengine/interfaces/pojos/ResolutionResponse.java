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
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Response-Object representing the current Context of a Resolution
 * sent by the Server back to the Client.
 * 
 * A Response contains either the current Knowledge or one or more Errors,
 * but never both! In both cases some Warnings could be present.
 * 
 * Note: The Choices in this Response-Object is a summary of all still open
 * Choices for both Variants and Features spread over the Knowledge-Tree.
 * This is for convenience of the Client only.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "ResolutionResponse")
public class ResolutionResponse extends BaseResolutionContext implements Serializable {

  private static final long serialVersionUID = 1L;

  private Warnings warnings;
  private Errors errors;
  private Choices choices;

  public ResolutionResponse() {
    this(null, null, null, null, null);
  }

  public ResolutionResponse(final String sessionID, final Knowledge knowledge) {
    this(sessionID, null, knowledge);
  }

  public ResolutionResponse(final String sessionID, final Errors errors) {
    this(sessionID, null, errors);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge) {
    this(sessionID, metadata, knowledge, null, null);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Errors errors) {
    this(sessionID, metadata, null, errors, null);
  }

  public ResolutionResponse(final String sessionID, final Metadata metadata, final Knowledge knowledge, final Errors errors, final Warnings warnings) {
    super(sessionID, metadata, knowledge);
    setErrors(errors);
    setWarnings(warnings);
    calculateChoices();
  }

  // ----------------------------------------------------------------

  public Warnings getWarnings() {
    if (this.warnings == null) {
      this.warnings = new Warnings();
    }
    return this.warnings;
  }

  public void setWarnings(final Warnings warnings) {
    clearWarnings();
    this.warnings = warnings;
  }

  public void addWarning(final Warning warning) {
    if (warning != null) {
      getWarnings().add(warning);
    }
  }

  public void addAllWarnings(final Collection<? extends Warning> c) {
    if ((c != null) && !c.isEmpty()) {
      getWarnings().addAll(c);
    }
  }

  public void clearWarnings() {
    if (this.warnings != null) {
      this.warnings.clear();
      this.warnings = null;
    }
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

  public void addAllErrors(final Collection<? extends ErrorMessage> c) {
    if ((c != null) && !c.isEmpty()) {
      getErrors().addAll(c);
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

  public void setChoices(final Choices c) {
    clearChoices();
    addAllChoices(c);
  }

  public void addChoice(final Choice choice) {
    if (choice != null) {
      getChoices().add(choice);
    }
  }

  public void addAllChoices(final Collection<? extends Choice> c) {
    if ((c != null) && !c.isEmpty()) {
      getChoices().addAll(c);
    }
  }

  public void clearChoices() {
    if (this.choices != null) {
      this.choices.clear();
      this.choices = null;
    }
  }

  // ----------------------------------------------------------------

  @JsonIgnore
  public void calculateChoices() {
    clearChoices();
    if (this.knowledge != null) {
      addAllChoices(this.knowledge.getChoices());
      addChoices(this.knowledge.getEntities());
    }
  }

  private void addChoices(final Collection<? extends KnowledgeEntity> entities) {
    if ((entities != null) && !entities.isEmpty()) {
      for (final KnowledgeEntity ke : entities) {
        addChoices(ke);
      }
    }
  }

  private void addChoices(final KnowledgeEntity ke) {
    if (ke != null) {
      addAllChoices(ke.getPossibleVariants());
      addAllChoices(ke.getPossibleFeatures());
//      addAllChoices(ke.getPossibleConcepts()); // TODO: enable concepts here
      addChoices(ke.getChildren());
    }
  }

  // ----------------------------------------------------------------

  @JsonIgnore
  public boolean isResolved() {
    return ((this.choices == null) || this.choices.isEmpty());
  }

  @JsonIgnore
  public boolean hasErrors() {
    return ((this.errors != null) && !this.errors.isEmpty());
  }

  @JsonIgnore
  public boolean hasWarnings() {
    return ((this.warnings != null) && !this.warnings.isEmpty());
  }
}
