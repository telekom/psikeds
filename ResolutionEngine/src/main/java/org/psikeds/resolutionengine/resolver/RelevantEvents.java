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
package org.psikeds.resolutionengine.resolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.vo.ContextPath;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;

/**
 * All Events relevant for current State of Resolution
 * 
 * @author marco@juliano.de
 * 
 */
public class RelevantEvents extends ConcurrentHashMap<String, List<Event>> implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String KEY_FORMAT_PATH = "ContextPath:%s";
  private static final String KEY_FORMAT_VARIANT = "Variant:%s";
  private static final String KEY_FORMAT_EVENT = "Event:%s";

  public RelevantEvents() {
    this(null);
  }

  public RelevantEvents(final Knowledge knowledge) {
    init(knowledge);
  }

  // ------------------------------------------------------

  public List<Event> getEventsByContextPath(final ContextPath cp) {
    return (cp == null ? null : getEventsByContextPath(cp.pathAsString()));
  }

  public List<Event> getEventsByContextPath(final String path) {
    return (StringUtils.isEmpty(path) ? null : get(String.format(KEY_FORMAT_PATH, path)));
  }

  public List<Event> getEventsByVariant(final Variant v) {
    return (v == null ? null : getEventsByVariant(v.getId()));
  }

  public List<Event> getEventsByVariant(final String variantId) {
    return (StringUtils.isEmpty(variantId) ? null : get(String.format(KEY_FORMAT_VARIANT, variantId)));
  }

  public Event getEventById(final String eventId) {
    final List<Event> lst = (StringUtils.isEmpty(eventId) ? null : get(String.format(KEY_FORMAT_EVENT, eventId)));
    return (((lst == null) || lst.isEmpty()) ? null : lst.get(0));
  }

  // ------------------------------------------------------

  public void addEvent(final Event e) {
    addEventById(e);
    addEventByVariant(e);
    addEventByContextPath(e);
  }

  private void addEventByVariant(final Event e) {
    final String variantId = (e == null ? null : e.getVariantId());
    if (!StringUtils.isEmpty(variantId)) {
      List<Event> lst = getEventsByVariant(variantId);
      if (lst == null) {
        lst = new ArrayList<Event>();
      }
      lst.add(e);
      put(String.format(KEY_FORMAT_VARIANT, variantId), lst);
    }
  }

  private void addEventByContextPath(final Event e) {
    final ContextPath cp = (e == null ? null : e.getContextPath());
    final String path = (cp == null ? null : cp.pathAsString());
    if (!StringUtils.isEmpty(path)) {
      List<Event> lst = getEventsByContextPath(path);
      if (lst == null) {
        lst = new ArrayList<Event>();
      }
      lst.add(e);
      put(String.format(KEY_FORMAT_PATH, path), lst);
    }
  }

  private void addEventById(final Event e) {
    final String eventId = (e == null ? null : e.getId());
    if (!StringUtils.isEmpty(eventId)) {
      List<Event> lst = get(String.format(KEY_FORMAT_EVENT, eventId));
      // there might be several events per variant or even per context path
      // but never two events with the same id!
      if (lst == null) {
        lst = new ArrayList<Event>();
        lst.add(e);
        put(String.format(KEY_FORMAT_EVENT, eventId), lst);
      }
    }
  }

  // ------------------------------------------------------

  private void init(final Knowledge knowledge) {
    clear();
    if (knowledge != null) {
      // TODO
    }
  }
}
