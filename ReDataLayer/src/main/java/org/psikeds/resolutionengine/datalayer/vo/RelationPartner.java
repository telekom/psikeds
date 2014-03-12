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
@XmlRootElement(name = "RelationPartner")
public class RelationPartner extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<String> context;
  private String featureID;

  public RelationPartner() {
    this(null, null);
  }

  public RelationPartner(final List<String> context, final String featureID) {
    super();
    this.context = context;
    this.featureID = featureID;
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

  public void addContextPathID(final String id) {
    if (id != null) {
      getContext().add(id);
    }
  }

  public String getFeatureID() {
    return this.featureID;
  }

  public void setFeatureID(final String featureID) {
    this.featureID = featureID;
  }
}
