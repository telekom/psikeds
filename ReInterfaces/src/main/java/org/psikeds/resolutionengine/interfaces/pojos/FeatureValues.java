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
import java.util.Collection;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * A List of FeatureValues ... unfortunately we have to create a Sub-Class of
 * ArrayList<FeatureValue> because a simple List will loose all of its
 * Type-Information due to Java-Type-Erasure resulting in ugly errors during
 * JSON-Deserialization!
 * 
 * @author marco@juliano.de
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class FeatureValues extends ArrayList<FeatureValue> implements Serializable {

  private static final long serialVersionUID = 1L;

  public FeatureValues() {
    super();
  }

  public FeatureValues(final Collection<? extends FeatureValue> c) {
    super(c);
  }

  public FeatureValues(final int initialCapacity) {
    super(initialCapacity);
  }
}
