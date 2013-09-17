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
 * A Purpose is fulfilled by one or several Variant(s).
 *
 * Note 1: PurposeID and VariantID(s) must reference existing Objects!
 *
 * Note 2: Reading from and writing to JSON works out of the box.
 *         However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "Fulfills")
public class Fulfills extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String description;
  private String purposeID;
  private List<String> variantID;

  public Fulfills() {
    this(null, null, (List<String>) null);
  }

  public Fulfills(final String description, final String purposeID, final String variantID) {
    super();
    setDescription(description);
    setPurposeID(purposeID);
    addVariantID(variantID);
  }

  public Fulfills(final String description, final String purposeID, final List<String> variantID) {
    super();
    setDescription(description);
    setPurposeID(purposeID);
    setVariantID(variantID);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String value) {
    this.description = value;
  }

  public String getPurposeID() {
    return this.purposeID;
  }

  public void setPurposeID(final String value) {
    this.purposeID = value;
  }

  public List<String> getVariantID() {
    if (this.variantID == null) {
      this.variantID = new ArrayList<String>();
    }
    return this.variantID;
  }

  public boolean addVariantID(final String value) {
    return getVariantID().add(value);
  }

  public void setVariantID(final List<String> lst) {
    this.variantID = lst;
  }
}
