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
package org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;
import org.psikeds.resolutionengine.datalayer.vo.Context;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Basic Validator checking that Events are valid.
 * 
 * @author marco@juliano.de
 * 
 */
public class EventValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Events ...");
      validateEvents(kb);
      LOGGER.debug("... finished validating KnowledgeBase regarding Events ... OK.");
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Events ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... could not validate KnowledgeBase regarding Events!", ex);
      throw new ValidationException("Could not validate KnowledgeBase regarding Events!", ex);
    }
  }

  private void validateEvents(final KnowledgeBase kb) throws ValidationException {
    boolean valid = true;
    try {
      // Note: Events are optional, i.e. this Node can be null!
      final Events events = (kb == null ? null : kb.getEvents());
      final List<Event> lst = (events == null ? null : events.getEvent());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Event e : lst) {
          // --- Step 1: check basics ---
          final String eid = (e == null ? null : e.getId());
          final String vid = (e == null ? null : e.getVariantId());
          if (StringUtils.isEmpty(eid) || StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("Event contains no or unknown IDs: {}", e);
            continue;
          }
          else if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Event: {}", e);
          }
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !v.getId().equals(vid)) {
            valid = false;
            LOGGER.warn("Variant {} referenced by Event {} does not exists!", vid, eid);
          }
          final boolean fevt = e.isFeatureEvent();
          final boolean nevt = e.isNotEvent();
          if (fevt && nevt) {
            valid = false;
            LOGGER.warn("Event must not be both Feature-Event and Not-Event: {}", eid);
          }
          // --- Step 2: check trigger ---
          final String trigger = e.getTrigger();
          boolean hasTrigger = !StringUtils.isEmpty(trigger);
          Feature<?> triggeringFeature = null;
          Variant triggeringVariant = null;
          if (hasTrigger) {
            if (fevt) {
              triggeringFeature = kb.getFeature(trigger);
              if ((triggeringFeature == null) || !trigger.equals(triggeringFeature.getId())) {
                valid = false;
                hasTrigger = false;
                LOGGER.warn("Trigger {} of Event {} is not an existing Feature!", trigger, eid);
              }
            }
            else {
              triggeringVariant = kb.getVariant(trigger);
              if ((triggeringVariant == null) || !trigger.equals(triggeringVariant.getId())) {
                valid = false;
                hasTrigger = false;
                LOGGER.warn("Trigger {} of Event {} is not an existing Variant!", trigger, eid);
              }
            }
          }
          else {
            valid = false;
            LOGGER.warn("Event has no Trigger: {}", eid);
          }
          // --- Step 3: check context path ---
          final Context ctx = e.getContext();
          final List<String> path = (ctx == null ? null : ctx.getPathIDs());
          final boolean hasContext = ((path != null) && !path.isEmpty());
          if (hasContext) {
            if (hasTrigger) {
              // validate context path and check that trigger is logically matching
              if (!checkContextPath(kb, eid, vid, path, triggeringFeature, triggeringVariant)) {
                valid = false;
                LOGGER.warn("Context is not valid: {}", e);
              }
            }
            else {
              // event has invalid trigger, but let's validate the context path nevertheless
              if (!checkContextPath(kb, eid, vid, path)) {
                valid = false;
                LOGGER.warn("Context is not valid: {}", e);
              }
            }
          }
          else {
            valid = false;
            LOGGER.warn("Event has no Context: {}", eid);
          }
        }
      }
    }
    catch (final Exception ex) {
      valid = false;
      throw new ValidationException("Could not validate Events", ex);
    }
    if (!valid) {
      throw new ValidationException("Invalid Events in KnowledgeBase!");
    }
  }

  private boolean checkContextPath(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctxPath) {
    return checkContextPath(kb, eventId, rootVariantId, ctxPath, null, null);
  }

  private boolean checkContextPath(final KnowledgeBase kb, final String eventId, final String rootVariantId,
      final List<String> ctxPath,
      final Feature<?> triggeringFeature, final Variant triggeringVariant) {
    // --- Step A: check basics ---
    final String firstPathElement = ctxPath.get(0);
    if (StringUtils.isEmpty(rootVariantId) || !rootVariantId.equals(firstPathElement)) {
      LOGGER.warn("Context of Event {} does not start with Root-Variant-Id: {}", eventId, rootVariantId);
      return false;
    }
    // --- Step B: walk along the path and check consistancy ---
    boolean variant = true;
    Object previous = null;
    for (final String id : ctxPath) {
      if (StringUtils.isEmpty(id)) {
        LOGGER.warn("Context of Event {} contains empty ID!", eventId);
        return false;
      }
      if (variant) {
        final Variant v = kb.getVariant(id);
        if ((v == null) || !id.equals(v.getId())) {
          LOGGER.warn("Context of Event {} contains unknown Variant-ID: {}", eventId, id);
          return false;
        }
        if (previous != null) {
          final Purpose p = (Purpose) previous;
          final String pid = (p == null ? null : p.getId());
          if (!kb.isFulfilledBy(pid, id)) {
            LOGGER.warn("Context of Event {} contains illegal Edge from {} to {}", eventId, pid, id);
            return false;
          }
        }
        previous = v;
      }
      else {
        final Purpose p = kb.getPurpose(id);
        if ((p == null) || !id.equals(p.getId())) {
          LOGGER.warn("Context of Event {} contains unknown Purpose-ID: {}", eventId, id);
          return false;
        }
        if (previous != null) {
          final Variant v = (Variant) previous;
          final String vid = (v == null ? null : v.getId());
          if (!kb.isConstitutedBy(vid, id)) {
            LOGGER.warn("Context of Event {} contains illegal Edge from {} to {}", eventId, vid, id);
            return false;
          }
        }
        previous = p;
      }
      variant = !variant;
    }
    // --- Step C: check that trigger is matching to last element of context path ---
    if (variant) {
      // next element would be a variant, i.e. last element was a purpose!
      if ((triggeringVariant != null) && (previous != null)) {
        final Purpose p = (Purpose) previous;
        final String pid = p.getId();
        final String vid = triggeringVariant.getId();
        if (!kb.isFulfilledBy(pid, vid)) {
          LOGGER.warn("Triggering Variant {} of Event {} is not matching to last Element of Context-Path {}", vid, eventId, pid);
          return false;
        }
      }
    }
    else {
      // next element would be a purpose, i.e. last element was a variant!
      if ((triggeringFeature != null) && (previous != null)) {
        final Variant v = (Variant) previous;
        final String vid = v.getId();
        final String fid = triggeringFeature.getId();
        if (!kb.hasFeature(vid, fid)) {
          LOGGER.warn("Triggering Feature {} of Event {} is not matching to last Element of Context-Path {}", fid, eventId, vid);
          return false;
        }
      }
    }
    // --- Done! ---
    return true;
  }
}
