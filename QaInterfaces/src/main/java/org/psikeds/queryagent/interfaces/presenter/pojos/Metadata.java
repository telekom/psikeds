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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Meta-Infos regarding the current Resolution and Knowledge-Base.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Metadata")
public class Metadata extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, Object> infomap;

  public Metadata() {
    this(null);
  }

  public Metadata(final Map<String, Object> infos) {
    super();
    addInfo(infos);
  }

  public Map<String, Object> getInfoMap() {
    if (this.infomap == null) {
      this.infomap = new ConcurrentHashMap<String, Object>();
    }
    return this.infomap;
  }

  public void setInfoMap(final Map<String, Object> infomap) {
    this.infomap = infomap;
  }

  public void addInfo(final Map<String, Object> infos) {
    if ((infos != null) && !infos.isEmpty()) {
      getInfoMap().putAll(infos);
    }
  }

  public void addInfo(final String key, final Object value) {
    if ((key != null) && (value != null)) {
      getInfoMap().put(key, value);
    }
  }

  public Object getInfo(final String key) {
    return (key == null ? null : getInfoMap().get(key));
  }

  public void clearInfoMap() {
    if (this.infomap != null) {
      this.infomap.clear();
      this.infomap = null;
    }
  }
}
