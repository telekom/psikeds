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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import org.springframework.util.StringUtils;

import org.psikeds.common.util.ObjectDumper;

/**
 * Base of all Value-Objects in this Package.
 * 
 * JSON-TypeInfo-Settings will be inherited by all derived Classes.
 * 
 * By default the ID of a ValueObject is for internal Usage only. If you want to
 * make it visible within JSON- or XML-Data, you have to expose it via public
 * setters and getters. For Examples see Rule, Event, Purpose, etc.
 * 
 * @author marco@juliano.de
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public abstract class ValueObject implements Serializable, Comparable<Object> {

  private static final long serialVersionUID = 1L;
  private static final char COMPOSE_ID_SEPARATOR = '/';

  // unique id of this Value Object
  private String id;

  protected ValueObject() {
    this.id = null;
  }

  protected ValueObject(final String id) {
    setId(id);
  }

  protected ValueObject(final String... ids) {
    setId(ids);
  }

  protected ValueObject(final ValueObject... vos) {
    setId(vos);
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
  protected void setId(final ValueObject... vos) {
    this.id = composeId(vos);
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
   * @return the value 0 if argument Object is a ValueObject of same Class and its ID is equal to
   *         the ID of
   *         this Object;
   *         a value greater than 0 if the specified Object is null, not a ValueObject of the same
   *         Class or
   *         its ID is lexicographically less than the ID of this Object;
   *         a value less 0 else
   * @see java.lang.Object#equals(Object obj)
   * @see java.lang.String#equals(String str)
   */
  @Override
  public int compareTo(final Object obj) {
    // check that obj is not null and a value object of the same class
    if (!(obj instanceof ValueObject) || !obj.getClass().equals(this.getClass())) {
      return 1;
    }
    final ValueObject vo = (ValueObject) obj;
    // value objects without IDs can never be compared
    if (StringUtils.isEmpty(vo.getId())) {
      return 1;
    }
    if (StringUtils.isEmpty(this.getId())) {
      return -1;
    }
    // compare IDs
    return this.getId().compareTo(vo.getId());
  }

  /**
   * @return true if Object is not null, a ValueObject and has equal ID; false else
   * @see java.lang.Object#equals(Object obj)
   */
  @Override
  public boolean equals(final Object obj) {
    return (this.compareTo(obj) == 0);
  }

  // ------------------------------------------------------

  protected static String composeId(final ValueObject... vos) {
    final StringBuilder sb = new StringBuilder();
    for (final ValueObject v : vos) {
      final String vid = (v == null ? null : v.getId());
      if (!StringUtils.isEmpty(vid)) {
        if (sb.length() > 0) {
          sb.append(COMPOSE_ID_SEPARATOR);
        }
        sb.append(vid);
      }
    }
    return sb.toString();
  }

  protected static String composeId(final String... ids) {
    final StringBuilder sb = new StringBuilder();
    for (final String vid : ids) {
      if (!StringUtils.isEmpty(vid)) {
        if (sb.length() > 0) {
          sb.append(COMPOSE_ID_SEPARATOR);
        }
        sb.append(vid);
      }
    }
    return sb.toString();
  }
}
