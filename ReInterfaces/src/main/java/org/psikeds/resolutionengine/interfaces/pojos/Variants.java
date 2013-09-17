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
package org.psikeds.resolutionengine.interfaces.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a List of all Variants.
 *
 * Note: Reading from and writing to JSON works out of the box.
 *       However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "Variants")
public class Variants extends POJO implements Serializable {

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
    return getVariant().add(value);
  }

  public void setVariant(final List<Variant> lst) {
    this.variant = lst;
  }
}
