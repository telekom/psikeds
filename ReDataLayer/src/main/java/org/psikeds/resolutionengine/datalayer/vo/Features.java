/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.resolutionengine.datalayer.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * List of all features or attributes.
 *
 * @author marco@juliano.de
 *
 */
public class Features extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Feature> feature;

  public Features() {
    this(null);
  }

  public Features(final List<Feature> lst) {
    super();
    setFeature(lst);
  }

  public List<Feature> getFeature() {
    if (this.feature == null) {
      this.feature = new ArrayList<Feature>();
    }
    return this.feature;
  }

  public boolean addFeature(final Feature value) {
    return getFeature().add(value);
  }

  public void setFeature(final List<Feature> lst) {
    this.feature = lst;
  }
}
