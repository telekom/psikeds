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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Feature holding Float-Values.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "FloatFeature")
public class FloatFeature extends Feature<Float> implements Serializable {

  private static final long serialVersionUID = 1L;

  public FloatFeature() {
    this(null);
  }

  public FloatFeature(final String featureID) {
    this(featureID, featureID, featureID);
  }

  public FloatFeature(final String label, final String description, final String featureID) {
    this(label, description, featureID, null);
  }

  public FloatFeature(final String label, final String description, final String featureID, final List<Float> values) {
    super(label, description, featureID, values);
  }

  @JsonIgnore
  @Override
  public String getValueType() {
    return VALUE_TYPE_FLOAT;
  }

  @JsonIgnore
  @Override
  public List<String> getValuesAsStrings() {
    final List<String> lst = new ArrayList<String>();
    for (final Float f : getValues()) {
      lst.add(String.valueOf(f));
    }
    return lst;
  }
}
