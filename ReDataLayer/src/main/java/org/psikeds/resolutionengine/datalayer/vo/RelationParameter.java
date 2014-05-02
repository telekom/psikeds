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

import org.apache.commons.lang.StringUtils;

/**
 * A Relation-Partner is used within Relation of the Form
 * <Left-Side-Relation-Partner> <RelationOperator> <Right-Side-Relation-Partner>
 * 
 * Note: A Feature is a Declaration of possible Values that can be referenced
 * at several places within the Knowledge-Base. Only within a certain Context
 * the Feature points to a specific Feature-Value!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "RelationParameter")
public class RelationParameter extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String PARAMETER_TYPE_FEATURE = "feature";
  public static final String PARAMETER_TYPE_CONST_VALUE = "value";

  private String label;
  private String description;
  private String variantID;
  private List<String> context;
  private String parameterType;
  private String parameterValue;

  public RelationParameter() {
    this(null, null);
  }

  public RelationParameter(final String variantID, final String featureValueID) {
    this(null, null, null, variantID, null, PARAMETER_TYPE_CONST_VALUE, featureValueID);
    setId(variantID, featureValueID);
  }

  public RelationParameter(final String label, final String description, final String parameterID, final String variantID,
      final List<String> context, final String featureID) {
    this(label, description, parameterID, variantID, context, PARAMETER_TYPE_FEATURE, featureID);
  }

  public RelationParameter(final String label, final String description, final String parameterID, final String variantID,
      final List<String> context, final String parameterType, final String parameterValue) {
    super(parameterID);
    setLabel(label);
    setDescription(description);
    setVariantID(variantID);
    setContext(context);
    setParameterType(parameterType);
    setParameterValue(parameterValue);
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getParameterID() {
    return getId();
  }

  public void setParameterID(final String parameterID) {
    setId(parameterID);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String variantID) {
    this.variantID = variantID;
  }

  public String getParameterType() {
    return this.parameterType;
  }

  public void setParameterType(final String parameterType) {
    if (PARAMETER_TYPE_FEATURE.equalsIgnoreCase(parameterType)) {
      this.parameterType = PARAMETER_TYPE_FEATURE;
    }
    else {
      this.parameterType = PARAMETER_TYPE_CONST_VALUE;
    }
  }

  @JsonIgnore
  public boolean isConstant() {
    return PARAMETER_TYPE_CONST_VALUE.equals(getParameterType());
  }

  public String getParameterValue() {
    return this.parameterValue;
  }

  public void setParameterValue(final String parameterValue) {
    this.parameterValue = parameterValue;
  }

  public List<String> getContext() {
    if (this.context == null) {
      this.context = new ArrayList<String>();
    }
    return this.context;
  }

  public void setContext(final List<String> context) {
    this.context = context;
  }

  public boolean addContextPathID(final String id) {
    return (!StringUtils.isEmpty(id) && getContext().add(id));
  }
}
