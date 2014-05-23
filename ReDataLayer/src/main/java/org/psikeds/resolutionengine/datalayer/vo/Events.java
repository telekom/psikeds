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
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * List of Events.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Events")
public class Events extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<Event> event;

  public Events() {
    this(null);
  }

  public Events(final List<Event> event) {
    super();
    setEvent(event);
  }

  public List<Event> getEvent() {
    if (this.event == null) {
      this.event = new ArrayList<Event>();
    }
    return this.event;
  }

  public boolean addEvent(final Event value) {
    return ((value != null) && getEvent().add(value));
  }

  public void setEvent(final List<Event> lst) {
    this.event = lst;
  }
}
