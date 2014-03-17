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
 * Feature holding Integer-Values.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "IntegerFeature")
public class IntegerFeature extends Feature<Integer> implements Serializable {

  private static final long serialVersionUID = 1L;

  public IntegerFeature() {
    this(null);
  }

  public IntegerFeature(final String featureID) {
    this(featureID, featureID, featureID);
  }

  public IntegerFeature(final String label, final String description, final String featureID) {
    this(label, description, featureID, null);
  }

  public IntegerFeature(final String label, final String description, final String featureID, final List<Integer> values) {
    super(label, description, featureID, values);
  }

  @JsonIgnore
  @Override
  public String getValueType() {
    return VALUE_TYPE_INTEGER;
  }

  @JsonIgnore
  @Override
  public List<String> getValuesAsStrings() {
    final List<String> lst = new ArrayList<String>();
    for (final Integer i : getValues()) {
      lst.add(String.valueOf(i));
    }
    return lst;
  }
}
