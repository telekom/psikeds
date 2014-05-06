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

import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * A general Choice, either VariantChoice, FeatureChoice or ConceptChoice
 * 
 * @author marco@juliano.de
 * 
 */
@XmlSeeAlso({ ConceptChoice.class, FeatureChoice.class, VariantChoice.class })
@JsonSubTypes({ @JsonSubTypes.Type(value = ConceptChoice.class, name = "ConceptChoice"),
    @JsonSubTypes.Type(value = FeatureChoice.class, name = "FeatureChoice"),
    @JsonSubTypes.Type(value = VariantChoice.class, name = "VariantChoice"), })
public abstract class Choice extends POJO {

  private static final long serialVersionUID = 1L;

  protected String parentVariantID;

  public Choice() {
    this(null);
  }

  public Choice(final String parentVariantID) {
    super();
    this.parentVariantID = parentVariantID;
  }

  public String getParentVariantID() {
    return this.parentVariantID;
  }

  public void setParentVariantID(final String parentVariantID) {
    this.parentVariantID = parentVariantID;
  }
}
