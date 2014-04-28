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
package org.psikeds.resolutionengine.datalayer.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of FeatureValues.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FeatureValues")
public class FeatureValues extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<FeatureValue> value;

  public FeatureValues() {
    this(null);
  }

  public FeatureValues(final List<FeatureValue> lst) {
    super();
    setValue(lst);
  }

  public List<FeatureValue> getValue() {
    if (this.value == null) {
      this.value = new ArrayList<FeatureValue>();
    }
    return this.value;
  }

  public boolean addValue(final FeatureValue value) {
    return ((value != null) && getValue().add(value));
  }

  public void setValue(final List<FeatureValue> lst) {
    this.value = lst;
  }
}
