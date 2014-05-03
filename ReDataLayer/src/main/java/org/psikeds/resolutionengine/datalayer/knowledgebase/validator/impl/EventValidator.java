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
import org.psikeds.resolutionengine.datalayer.vo.Concept;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
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
        long count = 0;
        for (final Event e : lst) {
          count++;
          // --- Step 1: lookup and double check ---
          final String eid = (e == null ? null : e.getEventID());
          if (StringUtils.isEmpty(eid)) {
            valid = false;
            LOGGER.warn("Illegal Event: {}", e);
            continue;
          }
          final Event lookup = kb.getEvent(eid);
          if ((lookup == null) || !eid.equals(lookup.getEventID())) {
            valid = false;
            LOGGER.warn("Event not found: {}", e);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Event: {}", e);
          }
          // --- Step 2: check basics ---
          if (StringUtils.isEmpty(e.getLabel()) || !e.getLabel().equals(lookup.getLabel())) {
            valid = false;
            LOGGER.warn("Event {} has no Label.", eid);
          }
          final String vid = e.getVariantID();
          final Variant v = (StringUtils.isEmpty(vid) ? null : kb.getVariant(vid));
          if ((v == null) || !vid.equals(v.getVariantID())) {
            valid = false;
            LOGGER.warn("Variant {} referenced by Event {} does not exists!", vid, eid);
            continue;
          }
          final List<String> ctx = e.getContext();
          if ((ctx == null) || ctx.isEmpty()) {
            valid = false;
            LOGGER.warn("Event {} has no Context-Path!", eid);
            continue;
          }
          final String trigger = e.getTriggerID();
          if (StringUtils.isEmpty(trigger)) {
            valid = false;
            LOGGER.warn("Event {} has no Trigger!", eid);
            continue;
          }
          // --- Step 3: check trigger ---
          final String type = e.getTriggerType();
          if (Event.TRIGGER_TYPE_VARIANT.equals(type)) {
            if (!checkVariantTriggerAndContextPath(kb, eid, vid, ctx, trigger)) {
              valid = false;
            }
          }
          else if (Event.TRIGGER_TYPE_FEATURE_VALUE.equals(type)) {
            if (!checkFeatureValueTriggerAndContextPath(kb, eid, vid, ctx, trigger)) {
              valid = false;
            }
          }
          else if (Event.TRIGGER_TYPE_CONCEPT.equals(type)) {
            if (!checkConceptTriggerAndContextPath(kb, eid, vid, ctx, trigger)) {
              valid = false;
            }
          }
          else {
            valid = false;
            LOGGER.warn("Event {} has illegal Trigger-Type: {}", eid, type);
          }
        }
        LOGGER.debug("Checked {} Events.", count);
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

  private boolean checkVariantTriggerAndContextPath(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctx, final String triggeringVariantId) {
    final Variant trigVar = kb.getVariant(triggeringVariantId);
    if ((trigVar == null) || !triggeringVariantId.equals(trigVar.getVariantID())) {
      LOGGER.warn("Triggering Variant {} referenced by Event {} does not exists!", triggeringVariantId, eventId);
      return false;
    }
    final String lastPathElement = checkContextPath(kb, eventId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      return false;
    }
    if (lastPathElement.equals(triggeringVariantId) && lastPathElement.equals(rootVariantId)) {
      LOGGER.debug("Event {} is a Variant-Event without Context, i.e. Ctx = Variant = Trigger = {}", eventId, rootVariantId);
      // Note: We need this special case, otherwise we could not define Events on selecting a Variant for a Root-Purpose!
      return true;
    }
    final Purpose p = kb.getPurpose(lastPathElement);
    if ((p == null) || !lastPathElement.equals(p.getPurposeID())) {
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

  private boolean checkFeatureValueTriggerAndContextPath(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctx, final String triggeringFeatureValueId) {
    final FeatureValue trigVal = kb.getFeatureValue(triggeringFeatureValueId);
    if ((trigVal == null) || !triggeringFeatureValueId.equals(trigVal.getFeatureValueID())) {
      LOGGER.warn("Triggering Feature-Value {} referenced by Event {} does not exists!", triggeringFeatureValueId, eventId);
      return false;
    }
    final String lastPathElement = checkContextPath(kb, eventId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      return false;
    }
    final Variant v = kb.getVariant(lastPathElement);
    if ((v == null) || !lastPathElement.equals(v.getVariantID())) {
      LOGGER.warn("Last Element {} of Context of Feature-Value-Event {} is not a Variant!", lastPathElement, eventId);
      return false;
    }
    final String featureId = trigVal.getFeatureID();
    if (!kb.hasFeature(lastPathElement, featureId)) {
      LOGGER.warn("Triggering Feature-Value {} is not a valid for Variant {} in the Context of Event {}", triggeringFeatureValueId, lastPathElement, eventId);
      return false;
    }
    return true;
  }

  private boolean checkConceptTriggerAndContextPath(final KnowledgeBase kb, final String eventId, final String rootVariantId, final List<String> ctx, final String triggeringConceptId) {
    final Concept trigCon = kb.getConcept(triggeringConceptId);
    if ((trigCon == null) || !triggeringConceptId.equals(trigCon.getConceptID())) {
      LOGGER.warn("Triggering Concept {} referenced by Event {} does not exists!", triggeringConceptId, eventId);
      return false;
    }
    final String lastPathElement = checkContextPath(kb, eventId, rootVariantId, ctx);
    if (StringUtils.isEmpty(lastPathElement)) {
      return false;
    }
    final Variant v = kb.getVariant(lastPathElement);
    if ((v == null) || !lastPathElement.equals(v.getVariantID())) {
      LOGGER.warn("Last Element {} of Context of Concept-Event {} is not a Variant!", lastPathElement, eventId);
      return false;
    }
    final String conceptID = trigCon.getConceptID();
    if (!kb.hasConcept(lastPathElement, conceptID)) {
      LOGGER.warn("Triggering Concept {} is not a valid for Variant {} in the Context of Event {}", triggeringConceptId, lastPathElement, eventId);
      return false;
    }
    return true;
  }

  // ----------------------------------------------------------------

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
