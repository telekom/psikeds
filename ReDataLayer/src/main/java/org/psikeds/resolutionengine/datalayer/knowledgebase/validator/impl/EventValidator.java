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
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureEvent;
import org.psikeds.resolutionengine.datalayer.vo.FloatFeature;
import org.psikeds.resolutionengine.datalayer.vo.IntegerFeature;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.StringFeature;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.VariantEvent;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * Validator checking that Events are valid and consistent.
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
          final String eid = (e == null ? null : e.getEventID());
          if (StringUtils.isEmpty(eid)) {
            valid = false;
            LOGGER.warn("Event has no ID: {}", e);
            continue;
          }
          final String vid = (e == null ? null : e.getVariantID());
          if (StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("Event is not attached to a Variant: {}", e);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Event: {}", e);
          }
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !vid.equals(v.getVariantID())) {
            valid = false;
            LOGGER.warn("Variant {} referenced by Event {} does not exists!", vid, eid);
          }
          final List<String> ctx = e.getContext();
          if ((ctx == null) || ctx.isEmpty()) {
            valid = false;
            LOGGER.warn("Event has no Context: {}", e);
            continue;
          }
          if (e instanceof VariantEvent) {
            // --- Step 2a: check variant trigger ---
            final VariantEvent ve = (VariantEvent) e;
            final String triggerID = ve.getTriggeringVariantId();
            final Variant trigger = (StringUtils.isEmpty(triggerID) ? null : kb.getVariant(triggerID));
            if ((trigger == null) || !triggerID.equals(trigger.getVariantID())) {
              valid = false;
              LOGGER.warn("Triggering Variant {} referenced by Event {} does not exists!", triggerID, eid);
            }
            else {
              // --- Step 3a: check context path and variant ---
              valid = valid && checkContextPathAndVariant(kb, eid, vid, ctx, triggerID);
            }
          }
          else if (e instanceof FeatureEvent) {
            // --- Step 2b: check feature trigger ---
            final FeatureEvent fe = (FeatureEvent) e;
            final String featureID = fe.getTriggeringFeatureID();
            final Feature<?> feature = (StringUtils.isEmpty(featureID) ? null : kb.getFeature(featureID));
            if ((feature == null) || !featureID.equals(feature.getFeatureID())) {
              valid = false;
              LOGGER.warn("Triggering Feature {} referenced by Event {} does not exists!", featureID, eid);
            }
            else {
              final String value = fe.getTriggeringFeatureValue();
              if (StringUtils.isEmpty(value)) {
                if (fe.isNotEvent()) {
                  LOGGER.info("Event {} is triggered by NOT an EMPTY value, i.e. whenever any Value is selected. Intention or mistake?!?", eid);
                }
                else {
                  valid = false;
                  LOGGER.warn("Triggering Value of Event {} is empty!", eid);
                }
              }
              else {
                if (feature instanceof StringFeature) {
                  final StringFeature strf = (StringFeature) feature;
                  final List<String> values = strf.getValues();
                  if ((values == null) || !values.contains(value)) {
                    valid = false;
                    LOGGER.warn("Triggering Value {} of Event {} is not a possible String-Value of triggering Feature {}", value, eid, featureID);
                  }
                }
                else if (feature instanceof IntegerFeature) {
                  try {
                    final IntegerFeature intf = (IntegerFeature) feature;
                    final Integer ival = new Integer(Integer.parseInt(value));
                    final List<Integer> values = intf.getValues();
                    if ((values == null) || !values.contains(ival)) {
                      valid = false;
                      LOGGER.warn("Triggering Value {} of Event {} is not a possible Integer-Value of triggering Feature {}", value, eid, featureID);
                    }
                  }
                  catch (final Exception ex) {
                    valid = false;
                    LOGGER.warn("Cannot check triggering Value {} of Feature-Event {} - {}", value, eid, ex.getMessage());
                  }
                }
                else if (feature instanceof FloatFeature) {
                  try {
                    final FloatFeature fltf = (FloatFeature) feature;
                    final Float fval = new Float(Float.parseFloat(value));
                    final List<Float> values = fltf.getValues();
                    if ((values == null) || !values.contains(fval)) {
                      valid = false;
                      LOGGER.warn("Triggering Value {} of Event {} is not a possible Float-Value of triggering Feature {}", value, eid, featureID);
                    }
                  }
                  catch (final Exception ex) {
                    valid = false;
                    LOGGER.warn("Cannot check triggering Value {} of Feature-Event {} - {}", value, eid, ex.getMessage());
                  }
                }
                // --- Step 3b: check context path ---
                valid = valid && checkContextPathAndFeature(kb, eid, vid, ctx, featureID);
              }
            }
          }
          else {
            valid = false;
            LOGGER.warn("Unexpected type of Event, neither Variant-Event nor Feature-Value-Event: {}", e);
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

  // ----------------------------------------------------------------

  private boolean checkContextPathAndFeature(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctx, final String triggeringFeatureId) {
    final String lastPathElement = checkContextPath(kb, eventId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      return false;
    }
    final Variant v = kb.getVariant(lastPathElement);
    if (v == null) {
      LOGGER.warn("Last Element {} of Context of Feature-Event {} is not a Variant!", lastPathElement, eventId);
      return false;
    }
    final List<String> feats = v.getFeatureIds();
    if ((feats == null) || !feats.contains(triggeringFeatureId)) {
      LOGGER.warn("Triggering Feature {} is not a valid Feature for Variant {} in the Context of Event {}", triggeringFeatureId, lastPathElement, eventId);
      return false;
    }
    return true;
  }

  private boolean checkContextPathAndVariant(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctx, final String triggeringVariantId) {
    final String lastPathElement = checkContextPath(kb, eventId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      return false;
    }
    final Purpose p = kb.getPurpose(lastPathElement);
    if (p == null) {
      LOGGER.warn("Last Element {} of Context of Variant-Event {} is not a Purpose!", lastPathElement, eventId);
      return false;
    }
    final Variants vars = kb.getFulfillingVariants(p.getPurposeID());
    final List<Variant> lst = (vars == null ? null : vars.getVariant());
    if ((lst == null) || lst.isEmpty()) {
      LOGGER.warn("Last Element {} of Context of Variant-Event {} does not have any fulfilling Variants!", lastPathElement, eventId);
      return false;
    }
    for (final Variant v : lst) {
      if ((v != null) && triggeringVariantId.equals(v.getVariantID())) {
        return true;
      }
    }
    LOGGER.warn("Triggering Variant {} is not a fulfilling Variant for Purpose {} in the Context of Event {}", triggeringVariantId, lastPathElement, eventId);
    return false;
  }

  private String checkContextPath(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctx) {
    // --- Step A: check that first element of context matches to root/parent-variant ---
    String lastPathElement = null;
    String firstPathElement = null;
    try {
      firstPathElement = ctx.get(0);
    }
    catch (final Exception ex) {
      firstPathElement = null;
    }
    if (StringUtils.isEmpty(rootVariantId) || StringUtils.isEmpty(firstPathElement) || !rootVariantId.equals(firstPathElement)) {
      LOGGER.warn("Context of Event {} does not start with Root-Variant-ID: {}", eventId, rootVariantId);
      return null;
    }
    // --- Step B: walk along the path and check consistency ---
    boolean variant = true;
    Object previous = null;
    for (final String id : ctx) {
      lastPathElement = id;
      if (StringUtils.isEmpty(id)) {
        LOGGER.warn("Context of Event {} contains an empty ID!", eventId);
        return null;
      }
      if (variant) {
        final Variant v = kb.getVariant(id);
        if ((v == null) || !id.equals(v.getVariantID())) {
          LOGGER.warn("Context of Event {} contains unknown Variant-ID: {}", eventId, id);
          return null;
        }
        if (previous != null) {
          final Purpose p = (Purpose) previous;
          final String pid = (p == null ? null : p.getPurposeID());
          if (!kb.isFulfilledBy(pid, id)) {
            LOGGER.warn("Context of Event {} contains illegal Edge from {} to {}", eventId, pid, id);
            return null;
          }
        }
        previous = v;
      }
      else {
        final Purpose p = kb.getPurpose(id);
        if ((p == null) || !id.equals(p.getPurposeID())) {
          LOGGER.warn("Context of Event {} contains unknown Purpose-ID: {}", eventId, id);
          return null;
        }
        if (previous != null) {
          final Variant v = (Variant) previous;
          final String vid = (v == null ? null : v.getVariantID());
          if (!kb.isConstitutedBy(vid, id)) {
            LOGGER.warn("Context of Event {} contains illegal Edge from {} to {}", eventId, vid, id);
            return null;
          }
        }
        previous = p;
      }
      variant = !variant;
    }
    // --- Done! ---
    return lastPathElement;
  }
}
