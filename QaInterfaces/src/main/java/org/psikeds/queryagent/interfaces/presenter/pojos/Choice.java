/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Choice is a certain Purpose of a (Parent-)Variant for which one of several
 * Variants can be choosen.
 * 
 * Note 1: PurposeIDs and VariantIDs must reference existing Objects!
 * 
 * Note 2: If the Purpose is a Root-Purpose, there is no Parent-Variant,
 * i.e. Parent-Variant is null.
 * 
 * Note 3: Reading from and writing to JSON works out of the box.
 * However for XML the XmlRootElement annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Choice")
public class Choice extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Variant parentVariant;
  private Purpose purpose;
  private List<Variant> variants;

  public Choice() {
    this(null);
  }

  public Choice(final Purpose purpose) {
    this(purpose, null);
  }

  public Choice(final Purpose purpose, final List<Variant> variants) {
    this(null, purpose, variants);
  }

  public Choice(final Variant parentVariant, final Purpose purpose, final List<Variant> variants) {
    super(parentVariant, purpose);
    this.parentVariant = parentVariant;
    this.purpose = purpose;
    this.variants = variants;
  }

  public Variant getParentVariant() {
    return this.parentVariant;
  }

  public void setParentVariant(final Variant parentVariant) {
    this.parentVariant = parentVariant;
  }

  public Purpose getPurpose() {
    return this.purpose;
  }

  public void setPurpose(final Purpose purpose) {
    this.purpose = purpose;
  }

  public List<Variant> getVariants() {
    if (this.variants == null) {
      this.variants = new ArrayList<Variant>();
    }
    return this.variants;
  }

  public void setVariants(final List<Variant> variants) {
    this.variants = variants;
  }

  public void addVariant(final Variant variant) {
    if (variant != null) {
      getVariants().add(variant);
    }
  }

  public void setVariant(final Variant variant) {
    getVariants().clear();
    addVariant(variant);
  }
}
