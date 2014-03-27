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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of Variants.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Variants")
public class Variants extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Variant> variant;

  public Variants() {
    this(null);
  }

  public Variants(final List<Variant> variant) {
    super();
    setVariant(variant);
  }

  public List<Variant> getVariant() {
    if (this.variant == null) {
      this.variant = new ArrayList<Variant>();
    }
    return this.variant;
  }

  public boolean addVariant(final Variant value) {
    return ((value != null) && getVariant().add(value));
  }

  public void setVariant(final List<Variant> lst) {
    this.variant = lst;
  }
}
