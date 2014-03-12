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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Enum of all supported Operators for Relations on Features.
 ** 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "RelationOperator")
public enum RelationOperator {

  EQUAL("equal"),
  NOT_EQUAL("notEqual"),
  LESS_THAN("lessThan"),
  LESS_OR_EQUAL("lessOrEqual"),
  GREATER_THAN("greaterThan"),
  GREATER_OR_EQUAL("greaterOrEqual");

  public static final RelationOperator DEFAULT_OPERATOR = EQUAL;

  private String operator;

  private RelationOperator(final String str) {
    this.operator = str;
  }

  public String value() {
    return this.operator;
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
  public static RelationOperator fromValue(final String str) {
    RelationOperator op = null;
    try {
      if (NOT_EQUAL.value().equalsIgnoreCase(str)) {
        op = NOT_EQUAL;
      }
      else if (LESS_THAN.value().equalsIgnoreCase(str)) {
        op = LESS_THAN;
      }
      else if (LESS_OR_EQUAL.value().equalsIgnoreCase(str)) {
        op = LESS_OR_EQUAL;
      }
      else if (GREATER_THAN.value().equalsIgnoreCase(str)) {
        op = GREATER_THAN;
      }
      else if (GREATER_OR_EQUAL.value().equalsIgnoreCase(str)) {
        op = GREATER_OR_EQUAL;
      }
    }
    catch (final Exception ex) {
      op = null;
    }
    return op == null ? DEFAULT_OPERATOR : op; // default is equal-operator
  }
}
