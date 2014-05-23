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
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * A List of ConceptChoices ... unfortunately we have to create a Sub-Class of
 * ArrayList<ConceptChoice> because a simple List will loose its Type-Information
 * due to Java-Type-Erasure resulting in errors during JSON-Deserialization!
 * 
 * Note: For convenience you can set the Parent-Variant of all Feature-Choices
 * 
 * @author marco@juliano.de
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@XmlRootElement(name = "ConceptChoices")
public class ConceptChoices extends ArrayList<ConceptChoice> implements Serializable {

  private static final long serialVersionUID = 1L;

  public ConceptChoices() {
    super();
  }

  public ConceptChoices(final Collection<? extends ConceptChoice> c) {
    super(c);
  }

  public ConceptChoices(final int initialCapacity) {
    super(initialCapacity);
  }

  @JsonIgnore
  public void setParentVariantID(final String parentVariantID) {
    if (!this.isEmpty()) {
      final Iterator<ConceptChoice> iter = this.iterator();
      while ((iter != null) && iter.hasNext()) {
        final ConceptChoice cc = iter.next();
        cc.setParentVariantID(parentVariantID);
      }
    }
  }
}
