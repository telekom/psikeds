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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Metadata-Infos regarding the current Resolution and Knowledge-Base.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Metadata")
public class Metadata extends POJO implements Serializable {

  public static final String SESSION_ID = "SESSION_ID";
  public static final String KB_TIMESTAMP = "KB_TIMESTAMP";

  private static final long serialVersionUID = 1L;

  private Map<String, Object> infomap;

  public Metadata() {
    this(null);
  }

  public Metadata(final Map<String, Object> infomap) {
    super();
    this.infomap = infomap;
  }

  public Map<String, Object> getInfomap() {
    if (this.infomap == null) {
      this.infomap = new ConcurrentHashMap<String, Object>();
    }
    return this.infomap;
  }

  public void setInfomap(final Map<String, Object> infomap) {
    this.infomap = infomap;
  }

  public Object loadInfo(final String key) {
    return getInfomap().get(key);
  }

  public void saveInfo(final String key, final Object value) {
    getInfomap().put(key, value);
  }
}
