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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * A List of KnowledgeEntities ... unfortunately we have to create a Sub-Class
 * of ArrayList<KnowledgeEntity> because a simple List will loose all its
 * Type-Information due to Java-Type-Erasure resulting in errors during JSON-
 * Deserialization!
 * 
 * @author marco@juliano.de
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@XmlRootElement(name = "KnowledgeEntities")
public class KnowledgeEntities extends ArrayList<KnowledgeEntity> implements Serializable {

  private static final long serialVersionUID = 1L;

  public KnowledgeEntities() {
    super();
  }

  public KnowledgeEntities(final Collection<? extends KnowledgeEntity> c) {
    super(c);
  }

  public KnowledgeEntities(final int initialCapacity) {
    super(initialCapacity);
  }
}
