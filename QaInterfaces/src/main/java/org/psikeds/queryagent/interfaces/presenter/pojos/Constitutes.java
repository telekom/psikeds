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
 * A Variant is constituted by one or several Purposes(s).
 *
 * Note 1: VariantID and PurposeID(s) must reference existing Objects!
 *
 * Note 2: Reading from and writing to JSON works out of the box.
 *         However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "Constitutes")
public class Constitutes extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String description;
  private String variantID;
  private List<String> purposeID;

  public Constitutes() {
    this(null, null, (List<String>) null);
  }

  public Constitutes(final String description, final String variantID, final String purposeID) {
    super();
    setDescription(description);
    setVariantID(variantID);
    addPurposeID(purposeID);
  }

  public Constitutes(final String description, final String variantID, final List<String> purposeID) {
    super();
    setDescription(description);
    setVariantID(variantID);
    setPurposeID(purposeID);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String value) {
    this.description = value;
  }

  public String getVariantID() {
    return this.variantID;
  }

  public void setVariantID(final String value) {
    this.variantID = value;
  }

  public List<String> getPurposeID() {
    if (this.purposeID == null) {
      this.purposeID = new ArrayList<String>();
    }
    return this.purposeID;
  }

  public boolean addPurposeID(final String value) {
    return getPurposeID().add(value);
  }

  public void setPurposeID(final List<String> lst) {
    this.purposeID = lst;
  }
}
