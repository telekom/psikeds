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

import org.apache.commons.lang.StringUtils;

/**
 * A Variant is constituted by one or several Component(s), i.e. Purposes.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Constitutes")
public class Constitutes extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String variantID;
  private List<Component> components;

  public Constitutes() {
    this(null, (List<Component>) null);
  }

  public Constitutes(final String variantID, final String purposeID) {
    super();
    setVariantID(variantID);
  }

  public Constitutes(final String variantID, final List<Component> components) {
    super();
    setVariantID(variantID);
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String value) {
    this.variantID = value;
  }

  public List<Component> getComponents() {
    if (this.components == null) {
      this.components = new ArrayList<Component>();
    }
    return this.components;
  }

  public boolean addComponent(final String purposeID) {
    return (!StringUtils.isEmpty(purposeID) && addComponent(new Component(purposeID)));
  }

  public boolean addComponent(final Component comp) {
    return ((comp != null) && getComponents().add(comp));
  }

  public void setComponents(final List<Component> components) {
    this.components = components;
  }
}
