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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Component defines how many instances of a Purpose constitute a certain Variant.
 * 
 * Note: VariantID and PurposeID(s) must reference existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Component")
public class Component extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final long MINIMUM_QUANTITY = 1L;
  public static final long DEFAULT_QUANTITY = MINIMUM_QUANTITY;

  private String purposeID;
  private long quantity;

  public Component() {
    this(null);
  }

  public Component(final String purposeID) {
    this(purposeID, DEFAULT_QUANTITY);
  }

  public Component(final String purposeID, final long quantity) {
    super();
    setQuantity(quantity);
    setPurposeID(purposeID);
  }

  public String getPurposeID() {
    return this.purposeID;
  }

  public void setPurposeID(final String purposeID) {
    this.purposeID = purposeID;
  }

  public long getQuantity() {
    return this.quantity;
  }

  public void setQuantity(final long qty) {
    this.quantity = qty < MINIMUM_QUANTITY ? MINIMUM_QUANTITY : qty;
  }
}
