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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

/**
 * Meta-Infos regarding the Knowledge-Base.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "MetaData")
public class MetaData extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  protected String name;
  protected String teaser;
  protected String release;
  protected String copyright;
  protected String license;
  protected String language;
  protected Calendar created;
  protected Calendar lastmodified;
  protected Calendar loaded;
  protected List<String> creator;
  protected List<String> description;
  protected Map<String, Object> additionalInfo;

  public MetaData(final String id, final String name, final String teaser, final String release, final String copyright, final String license, final String language,
      final Calendar created, final Calendar lastmodified, final List<String> creator, final List<String> description) {
    this(id, name, teaser, release, copyright, license, language, created, lastmodified, null, creator, description, null);
  }

  public MetaData(final String id, final String name, final String teaser, final String release, final String copyright, final String license, final String language,
      final Calendar created, final Calendar lastmodified, final Calendar loaded,
      final List<String> creator, final List<String> description, final Map<String, Object> additionalInfo) {
    super(id);
    setName(name);
    setTeaser(teaser);
    setRelease(release);
    setCopyright(copyright);
    setLicense(license);
    setLanguage(language);
    setCreated(created);
    setLastmodified(lastmodified);
    setLoaded(loaded);
    setCreator(creator);
    setDescription(description);
    setAdditionalInfo(additionalInfo);
  }

  // ----------------------------------------------------------------

  public String getLanguage() {
    return this.language;
  }

  public void setLanguage(final String language) {
    this.language = language;
  }

  @Override
  public String getId() {
    return (StringUtils.isEmpty(this.id) ? this.name : this.id);
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getTeaser() {
    return this.teaser;
  }

  public void setTeaser(final String teaser) {
    this.teaser = teaser;
  }

  public String getRelease() {
    return this.release;
  }

  public void setRelease(final String release) {
    this.release = release;
  }

  public String getCopyright() {
    return this.copyright;
  }

  public void setCopyright(final String copyright) {
    this.copyright = copyright;
  }

  public String getLicense() {
    return this.license;
  }

  public void setLicense(final String license) {
    this.license = license;
  }

  // ----------------------------------------------------------------

  public Calendar getCreated() {
    return this.created;
  }

  public void setCreated(final Calendar value) {
    this.created = value;
  }

  public Calendar getLastmodified() {
    return (this.lastmodified == null ? this.created : this.lastmodified);
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

  // ----------------------------------------------------------------

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
    return (!StringUtils.isEmpty(value) && getDescription().add(value));
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

  public void addAdditionalInfo(final String key, final Serializable value) {
    if (!StringUtils.isEmpty(key) && (value != null)) {
      getAdditionalInfo().put(key, value);
    }
  }
}
