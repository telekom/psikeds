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
 * Feature holding String-Values.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "StringFeature")
public class StringFeature extends Feature<String> implements Serializable {

  private static final long serialVersionUID = 1L;

  public StringFeature() {
    this(null);
  }

  public StringFeature(final String featureID) {
    this(featureID, featureID, featureID);
  }

  public StringFeature(final String label, final String description, final String featureID) {
    this(label, description, featureID, null);
  }

  public StringFeature(final String label, final String description, final String featureID, final List<String> values) {
    super(label, description, featureID, values);
  }

  @JsonIgnore
  @Override
  public String getValueType() {
    return VALUE_TYPE_STRING;
  }

  @JsonIgnore
  @Override
  public List<String> getValuesAsStrings() {
    return new ArrayList<String>(getValues());
  }
}
