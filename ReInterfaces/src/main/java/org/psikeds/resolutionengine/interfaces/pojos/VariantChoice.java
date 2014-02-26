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
 * A possible Choice: Which Variants can be choosen for a certain Purpose?
 * Optionally a Quantity might be specified, meaning that not one but
 * n Variants fulfill the Purpose, e.g. 4 wheels for driving.
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
@XmlRootElement(name = "VariantChoice")
public class VariantChoice extends POJO implements Choice, Serializable {

  private static final long serialVersionUID = 1L;

  public static final long MINIMUM_QUANTITY = 1L;
  public static final long DEFAULT_QUANTITY = MINIMUM_QUANTITY;

  private Variant parentVariant;
  private Purpose purpose;
  private List<Variant> variants;
  private long quantity;

  public VariantChoice() {
    this(null);
  }

  public VariantChoice(final Purpose purpose) {
    this(purpose, null);
  }

  public VariantChoice(final Purpose purpose, final List<Variant> variants) {
    this(null, purpose, variants, DEFAULT_QUANTITY);
  }

  public VariantChoice(final Variant parentVariant, final Purpose purpose, final List<Variant> variants, final long qty) {
    super(parentVariant, purpose);
    setParentVariant(parentVariant);
    setPurpose(purpose);
    setVariants(variants);
    setQuantity(qty);
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

  public long getQuantity() {
    return this.quantity;
  }

  public void setQuantity(final long qty) {
    this.quantity = qty < MINIMUM_QUANTITY ? MINIMUM_QUANTITY : qty;
  }

  /**
   * Check whether a made Decission matches to this Choice, i.e.
   * whether the Client selected one of the Variants for the Purpose.
   * 
   * @param decission
   * @return variant if matching, null else
   */
  @Override
  public Variant matches(final Decission decission) {
    try {
      final VariantDecission vd = (VariantDecission) decission;
      final String pid = this.purpose.getId();
      if (pid.equals(vd.getPurposeID())) {
        for (final Variant v : getVariants()) {
          final String vid = v.getId();
          if (vid.equals(vd.getVariantID())) {
            return v;
          }
        }
      }
    }
    catch (final Exception ex) {
      // Either not a VariantDecission or one of the Objects was NULL
    }
    return null;
  }
}
