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

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A possible Choice: Which Variants can be choosen for a certain Purpose?
 * Optionally a Quantity might be specified, meaning that not one but
 * n Variants fulfill the Purpose, e.g. 4 wheels for driving.
 * 
 * Note 1: PurposeIDs and VariantIDs must reference existing Objects!
 * 
 * Note 2: If the Purpose is a Root-Purpose, there is no Parent-Variant,
 * i.e. Parent-Variant-ID is null.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "VariantChoice")
public class VariantChoice extends Choice implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final long MINIMUM_QUANTITY = 1L;
  public static final long DEFAULT_QUANTITY = MINIMUM_QUANTITY;

  private Purpose purpose;
  private Variants variants;
  private long quantity;

  public VariantChoice() {
    this(null);
  }

  public VariantChoice(final Purpose purpose) {
    this(null, purpose);
  }

  public VariantChoice(final String parentVariantID, final Purpose purpose) {
    this(parentVariantID, purpose, DEFAULT_QUANTITY);
  }

  public VariantChoice(final Purpose purpose, final long qty) {
    this(null, purpose, qty);
  }

  public VariantChoice(final String parentVariantID, final Purpose purpose, final long qty) {
    this(parentVariantID, purpose, null, qty);
  }

  public VariantChoice(final String parentVariantID, final Purpose purpose, final Variants variants, final long qty) {
    super(parentVariantID);
    setPurpose(purpose);
    setVariants(variants);
    setQuantity(qty);
  }

  public Purpose getPurpose() {
    return this.purpose;
  }

  public void setPurpose(final Purpose purpose) {
    this.purpose = purpose;
  }

  public Variants getVariants() {
    if (this.variants == null) {
      this.variants = new Variants();
    }
    return this.variants;
  }

  public void setVariants(final Variants variants) {
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
   * Check whether some made Decission matches to this Choice, i.e.
   * whether the Client selected one of the Variants for the Purpose.
   * 
   * @param decission
   * @return true if matching, false else
   */
  @JsonIgnore
  @Override
  public boolean matches(final Decission decission) {
    Variant v;
    try {
      final VariantDecission vd = (VariantDecission) decission;
      v = matches(vd);
    }
    catch (final Exception ex) {
      // Probably not a VariantDecission
      v = null;
    }
    return (v != null);
  }

  /**
   * Check whether a made VariantDecission matches to this Choice, i.e.
   * whether the Client selected one of the Variants for the Purpose.
   * 
   * @param vd
   * @return Variant if matching, null else
   */
  @JsonIgnore
  public Variant matches(final VariantDecission vd) {
    try {
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
      // one of the Objects was NULL
    }
    return null;
  }
}
