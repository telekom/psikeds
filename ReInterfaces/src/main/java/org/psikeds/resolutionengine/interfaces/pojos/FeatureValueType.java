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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Enum of all Types of a Feature's Value exposed to the Client via the Interface.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "FeatureValueType")
public enum FeatureValueType {

  INTEGER("integer"),
  FLOAT("float"),
  BOOLEAN("boolean"),
  DATE("date"),
  STRING("string");

  private String valueType;

  private FeatureValueType(final String str) {
    this.valueType = str;
  }

  public String value() {
    return this.valueType;
  }

  @Override
  public String toString() {
    return value();
  }

  /**
   * Convert plaintext string to typesafe enum
   *
   * @param str
   * @return enum
   */
  public static FeatureValueType fromValue(final String str) {
    FeatureValueType fvt = null;
    try {
      if (INTEGER.value().equalsIgnoreCase(str)) {
        fvt = INTEGER;
      }
      else if (FLOAT.value().equalsIgnoreCase(str)) {
        fvt = FLOAT;
      }
      else if (BOOLEAN.value().equalsIgnoreCase(str)) {
        fvt = BOOLEAN;
      }
      else if (DATE.value().equalsIgnoreCase(str)) {
        fvt = DATE;
      }
    }
    catch (final Exception ex) {
      fvt = null;
    }
    return fvt == null ? STRING : fvt;
  }
}
