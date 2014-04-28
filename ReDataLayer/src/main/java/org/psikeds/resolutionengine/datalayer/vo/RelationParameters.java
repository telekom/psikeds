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
 * List of RelationParameters.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "RelationParameters")
public class RelationParameters extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<RelationParameter> parameter;

  public RelationParameters() {
    this(null);
  }

  public RelationParameters(final List<RelationParameter> parameter) {
    super();
    setParameter(parameter);
  }

  public List<RelationParameter> getParameter() {
    if (this.parameter == null) {
      this.parameter = new ArrayList<RelationParameter>();
    }
    return this.parameter;
  }

  public boolean addParameter(final RelationParameter value) {
    return ((value != null) && getParameter().add(value));
  }

  public void setParameter(final List<RelationParameter> parameter) {
    this.parameter = parameter;
  }
}
