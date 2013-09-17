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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * An Event is a Path: Variant -> Purpose -> Variant -> Purpose -> ...
 * and its Conclusion ... Details to be defined yet!
 *
 * Note 1: ID must be globally unique.
 *
 * Note 2: Reading from and writing to JSON works out of the box.
 *         However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "Event")
public class Event extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String label;
  private String description;
  private String id;

  public Event() {
    this(null, null, null);
  }

  public Event(final String label, final String description, final String id) {
    super();
    this.label = label;
    this.description = description;
    this.id = id;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String value) {
    this.label = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String value) {
    this.description = value;
  }

  public String getId() {
    return this.id;
  }

  public void setId(final String value) {
    this.id = value;
  }
}
