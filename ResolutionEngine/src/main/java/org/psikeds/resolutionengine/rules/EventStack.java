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
package org.psikeds.resolutionengine.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

import org.psikeds.resolutionengine.cache.LimitedHashMap;
import org.psikeds.resolutionengine.datalayer.vo.Event;

/**
 * A stack of Events; Depending on the current State of Resolution and
 * the corresponding Knowledge, all Events are either on the Possible-
 * or the Obsolete- or the Triggered-Stack.
 * 
 * @author marco@juliano.de
 * 
 */
public class EventStack extends LimitedHashMap<String, Event> implements Serializable {

  private static final long serialVersionUID = 1L;

  public EventStack() {
    super();
  }

  public EventStack(final int maxSize) {
    super(maxSize);
  }

  public List<Event> getEvents() {
    return new ArrayList<Event>(this.values());
  }

  public void setEvents(final Collection<? extends Event> events) {
    clear();
    addEvents(events);
  }

  public void addEvents(final Collection<? extends Event> events) {
    if (events != null) {
      for (final Event e : events) {
        addEvent(e);
      }
    }
  }

  public Event addEvent(final Event e) {
    final String eid = (e == null ? null : e.getEventID());
    return (StringUtils.isEmpty(eid) ? null : this.put(eid, e));
  }

  public Event removeEvent(final Event e) {
    final String eid = (e == null ? null : e.getEventID());
    return removeEvent(eid);
  }

  public Event removeEvent(final String eid) {
    return (StringUtils.isEmpty(eid) ? null : this.remove(eid));
  }

  // ------------------------------------------------------

  public boolean containsEvent(final Event e) {
    return containsEvent(e == null ? null : e.getEventID());
  }

  public boolean containsEvent(final String eid) {
    return (StringUtils.isEmpty(eid) ? false : this.containsKey(eid));
  }

  public Event move2stack(final Event e, final EventStack destination) {
    final Event orig = removeEvent(e);
    return ((orig == null) || (destination == null) ? null : destination.addEvent(e));
  }

  public Event move2stack(final String eid, final EventStack destination) {
    final Event e = removeEvent(eid);
    return ((e == null) || (destination == null) ? null : destination.addEvent(e));
  }

  // ------------------------------------------------------

  public StringBuilder dumpEvents(final StringBuilder sb, final boolean verbose) {
    sb.append("#Events = ");
    sb.append(size());
    for (final Event e : getEvents()) {
      if (verbose) {
        sb.append('\n');
        sb.append(e);
      }
      else {
        sb.append(", ");
        sb.append(e.getEventID());
      }
    }
    sb.append('\n');
    return sb;
  }
}
