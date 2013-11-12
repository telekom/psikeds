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

import org.springframework.util.StringUtils;

import org.psikeds.common.util.ObjectDumper;

/**
 * Base of all POJOs in this Package.
 * 
 * @author marco@juliano.de
 * 
 */
public class POJO implements Serializable, Comparable<Object> {

  private static final long serialVersionUID = 1L;
  private static final char COMPOSE_ID_SEPARATOR = '/';

  // unique id of this pojo
  protected String id;

  protected POJO() {
    this.id = null;
  }

  protected POJO(final POJO... pojos) {
    this.id = composeId(pojos);
  }

  protected POJO(final String... ids) {
    this.id = composeId(ids);
  }

  public String getId() {
    return this.id;
  }

  public void setId(final String value) {
    this.id = value;
  }

  // ------------------------------------------------------

  /**
   * @return String Representation of this Object
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    sb.append('\n');
    sb.append(new ObjectDumper().dump(this));
    return sb.toString();
  }

  /**
   * @return the value 0 if argument Object is a POJO of same Class and its ID is equal to the ID of
   *         this Object;
   *         a value greater than 0 if the specified Object is null, not a POJO of the same Class or
   *         its ID is lexicographically less than the ID of this Object;
   *         a value less 0 else
   * @see java.lang.Object#equals(Object obj)
   * @see java.lang.String#equals(String str)
   */
  @Override
  public int compareTo(final Object obj) {
    // check that obj is not null and a pojo of the same class
    if (!(obj instanceof POJO) || !obj.getClass().equals(this.getClass())) {
      return 1;
    }
    final POJO pojo = (POJO) obj;
    // pojos without IDs can never be compared
    if (StringUtils.isEmpty(pojo.getId())) {
      return 1;
    }
    if (StringUtils.isEmpty(this.getId())) {
      return -1;
    }
    // compare IDs
    return this.getId().compareTo(pojo.getId());
  }

  /**
   * @return true if obj not null, pojo and equal; false else
   * @see java.lang.Object#equals(Object obj)
   */
  @Override
  public boolean equals(final Object obj) {
    return (this.compareTo(obj) == 0);
  }

  // ------------------------------------------------------

  private static String composeId(final POJO... pojos) {
    final StringBuilder sb = new StringBuilder();
    for (final POJO p : pojos) {
      final String pid = (p == null ? null : p.getId());
      if (!StringUtils.isEmpty(pid)) {
        if (sb.length() > 0) {
          sb.append(COMPOSE_ID_SEPARATOR);
        }
        sb.append(pid);
      }
    }
    return sb.toString();
  }

  private static String composeId(final String... ids) {
    final StringBuilder sb = new StringBuilder();
    for (final String pid : ids) {
      if (!StringUtils.isEmpty(pid)) {
        if (sb.length() > 0) {
          sb.append(COMPOSE_ID_SEPARATOR);
        }
        sb.append(pid);
      }
    }
    return sb.toString();
  }
}
