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
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Meta-Infos regarding the Knowledge-Base.
 * 
 * @author marco@juliano.de
 * 
 */
public class Meta extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private Calendar created;
  private Calendar lastmodified;
  private Calendar loaded;
  private String language;
  private String version;
  private List<String> creator;
  private List<String> description;
  private Map<String, Object> additionalInfo;

  public Meta() {
    this(null, null, null, null, null, null);
  }

  public Meta(final Calendar created, final Calendar lastmodified,
      final String language, final String version,
      final List<String> creator, final List<String> description) {
    this(created, lastmodified, null, language, version, creator, description, null);
  }

  public Meta(final Calendar created, final Calendar lastmodified, final Calendar loaded,
      final String language, final String version,
      final List<String> creator, final List<String> description,
      final Map<String, Object> additionalInfo) {
    super();
    setCreated(created);
    setLastmodified(lastmodified);
    setLoaded(loaded);
    setLanguage(language);
    setVersion(version);
    setCreator(creator);
    setDescription(description);
    setAdditionalInfo(additionalInfo);
  }

  public Calendar getCreated() {
    return this.created;
  }

  public void setCreated(final Calendar value) {
    this.created = value;
  }

  public Calendar getLastmodified() {
    return this.lastmodified;
  }

  public void setLastmodified(final Calendar value) {
    this.lastmodified = value;
  }

  public Calendar getLoaded() {
    return this.loaded;
  }

  public void setLoaded(final Calendar loaded) {
    if (loaded != null) {
      this.loaded = loaded;
    }
    else {
      // now!
      this.loaded = Calendar.getInstance();
    }
  }

  public String getLanguage() {
    return this.language;
  }

  public void setLanguage(final String language) {
    this.language = language;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public List<String> getCreator() {
    if (this.creator == null) {
      this.creator = new ArrayList<String>();
    }
    return this.creator;
  }

  public boolean addCreator(final String value) {
    return getCreator().add(value);
  }

  public void setCreator(final List<String> lst) {
    this.creator = lst;
  }

  public List<String> getDescription() {
    if (this.description == null) {
      this.description = new ArrayList<String>();
    }
    return this.description;
  }

  public boolean addDescription(final String value) {
    return getDescription().add(value);
  }

  public void setDescription(final List<String> lst) {
    this.description = lst;
  }

  public Map<String, Object> getAdditionalInfo() {
    if (this.additionalInfo == null) {
      this.additionalInfo = new ConcurrentHashMap<String, Object>();
    }
    return this.additionalInfo;
  }

  public void setAdditionalInfo(final Map<String, Object> additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public void addAdditionalInfo(final String key, final Object value) {
    if ((key != null) && (value != null)) {
      getAdditionalInfo().put(key, value);
    }
  }
}
