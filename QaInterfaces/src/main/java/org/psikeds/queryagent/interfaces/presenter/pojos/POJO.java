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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import org.springframework.util.StringUtils;

import org.psikeds.common.util.JSONHelper;

/**
 * Base of all POJOs in this Package.
 * 
 * JSON-TypeInfo-Settings will be inherited by all derived Classes.
 * 
 * By default the ID of a POJO is for internal Usage only. If you want to make
 * it visible within JSON- or XML-Data, you have to expose it via public setters
 * and getters, e.g. see Variant-ID, Purpose-ID or Feature-ID.
 * 
 * @author marco@juliano.de
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public abstract class POJO implements Serializable, Comparable<Object> {

  private static final long serialVersionUID = 1L;

  public static final char COMPOSE_ID_SEPARATOR = '/';

  // unique id of this pojo
  private String id;

  protected POJO() {
    this.id = null;
  }

  protected POJO(final String id) {
    setId(id);
  }

  protected POJO(final String... ids) {
    setId(ids);
  }

  protected POJO(final POJO... pojos) {
    setId(pojos);
  }

  @JsonIgnore
  protected String getId() {
    return this.id;
  }

  @JsonIgnore
  protected void setId(final String id) {
    this.id = id;
  }

  @JsonIgnore
  protected void setId(final String... ids) {
    this.id = composeId(ids);
  }

  @JsonIgnore
  protected void setId(final POJO... pojos) {
    this.id = composeId(pojos);
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
    sb.append(JSONHelper.dump(this));
    return sb.toString();
  }

  /**
   * @param obj
   *          a POJO
   * @return the value 0 if argument Object is a POJO of same Class and its ID is equal to
   *         the ID of this Object;
   *         a value greater than 0 if the specified Object is a POJO but has no ID
   *         or its ID is lexicographically less than the ID of this POJO;
   *         a value less 0 else
   * @see java.lang.Object#equals(Object obj)
   * @see java.lang.String#equals(String str)
   * @throws IllegalArgumentException
   *           if argument Object is null or not a POJO
   */
  @Override
  public int compareTo(final Object obj) {
    // check that obj is not null and a pojo
    if (!(obj instanceof POJO)) {
      throw new IllegalArgumentException("Not a POJO: " + String.valueOf(obj));
    }
    final POJO pojo = (POJO) obj;
    // check that this is the same type of pojo
    if (!pojo.getClass().equals(this.getClass())) {
      return pojo.getClass().getName().compareTo(this.getClass().getName());
    }
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
   * @return true if Object is not null, a POJO and has equal ID; false else
   * @see java.lang.Object#equals(Object obj)
   */
  @Override
  public boolean equals(final Object obj) {
    boolean ret;
    try {
      ret = (this.compareTo(obj) == 0);
    }
    catch (final Exception ex) {
      ret = false;
    }
    return ret;
  }

  // ------------------------------------------------------

  public static String composeId(final POJO... pojos) {
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

  public static String composeId(final String... ids) {
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
