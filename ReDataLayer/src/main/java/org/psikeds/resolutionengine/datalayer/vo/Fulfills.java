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
 * A Purpose is fulfilled by one or several Variant(s).
 * 
 * Note: PurposeID and VariantID(s) must reference existing Objects!
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Fulfills")
public class Fulfills extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private String purposeID;
  private List<String> variantID;

  public Fulfills() {
    this(null, null);
  }

  public Fulfills(final String purposeID, final List<String> variantID) {
    super();
    setPurposeID(purposeID);
    setVariantID(variantID);
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

  public boolean addVariantID(final String id) {
    return (!StringUtils.isEmpty(id) && getVariantID().add(id));
  }

  public void setVariantID(final List<String> lst) {
    this.variantID = lst;
  }
}
