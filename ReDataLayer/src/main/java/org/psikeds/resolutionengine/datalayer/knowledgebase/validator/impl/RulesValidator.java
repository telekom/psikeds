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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;
import org.psikeds.resolutionengine.datalayer.vo.ContextPath;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * Basic Validator checking that the Context-Paths of Rules and Events are valid.
 * 
 * @author marco@juliano.de
 * 
 */
public class RulesValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Rules and Events ...");
      validateEvents(kb);
      validateRules(kb);
      LOGGER.debug("... finished validating KnowledgeBase regarding Rules and Events ... OK.");
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Rules and Events ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... failed to validate KnowledgeBase regarding Rules and Events: " + ex.getMessage(), ex);
      throw new ValidationException("Failed to validate KnowledgeBase regarding Rules and Events.", ex);
    }
  }

  private void validateEvents(final KnowledgeBase kb) throws ValidationException {
    boolean valid = true;
    try {
      final Events events = kb.getEvents();
      final List<Event> lst = events.getEvent();
      for (final Event e : lst) {
        final String eid = e.getId();
        final String vid = e.getVariantId();
        if (StringUtils.isEmpty(eid) || StringUtils.isEmpty(vid)) {
          valid = false;
          LOGGER.warn("Event contains illegal IDs: {}", e);
        }
        final Variant v = kb.getVariant(vid);
        if ((v == null) || !v.getId().equals(vid)) {
          valid = false;
          LOGGER.warn("Variant referenced by Event does not exists: {}", e);
        }
        final ContextPath cp = e.getContextPath();
        LOGGER.trace("Checking ContextPath of Event {}", e);
        if (!checkContextPath(kb, cp)) {
          valid = false;
          LOGGER.warn("ContextPath of Event is not valid: {}", e);
        }
      }
    }
    catch (final Exception ex) {
      valid = false;
    }
    if (!valid) {
      throw new ValidationException("KnowledgeBase is not valid! See logfiles for details.");
    }
  }

  private boolean checkContextPath(final KnowledgeBase kb, final ContextPath cp) {
    final List<String> path = (cp == null ? new ArrayList<String>() : cp.getPathIDs());
    LOGGER.trace("Path = {}", path);
    if (path.isEmpty()) {
      LOGGER.warn("ContextPath is empty!");
      return false;
    }
    boolean variant = true;
    Object previous = null;
    for (final String id : path) {
      if (StringUtils.isEmpty(id)) {
        LOGGER.warn("ContextPath contains empty ID!");
        return false;
      }
      if (variant) {
        final Variant v = kb.getVariant(id);
        if ((v == null) || !v.getId().equals(id)) {
          LOGGER.warn("ContextPath contains unknown Variant-ID: {}", id);
          return false;
        }
        if (previous != null) {
          boolean found = false;
          final Purpose p = (Purpose) previous;
          final String pid = (p == null ? null : p.getId());
          final Variants vars = kb.getFulfillingVariants(pid);
          final List<Variant> lst = vars.getVariant();
          for (final Variant idx : lst) {
            if ((idx != null) && id.equals(idx.getId())) {
              found = true;
              break;
            }
          }
          if (!found) {
            LOGGER.warn("ContextPath contains illegal Edge from {} to {}", pid, id);
            return false;
          }
        }
        previous = v;
      }
      else {
        final Purpose p = kb.getPurpose(id);
        if ((p == null) || !p.getId().equals(id)) {
          LOGGER.warn("ContextPath contains unknown Purpose-ID: {}", id);
          return false;
        }
        if (previous != null) {
          boolean found = false;
          final Variant v = (Variant) previous;
          final String vid = (v == null ? null : v.getId());
          final Purposes purps = kb.getConstitutingPurposes(vid);
          final List<Purpose> lst = purps.getPurpose();
          for (final Purpose idx : lst) {
            if ((idx != null) && id.equals(idx.getId())) {
              found = true;
              break;
            }
          }
          if (!found) {
            LOGGER.warn("ContextPath contains illegal Edge from {} to {}", vid, id);
            return false;
          }
        }
        previous = p;
      }
      variant = !variant;
    }
    return true;
  }

  private void validateRules(final KnowledgeBase kb) throws ValidationException {
    boolean valid = true;
    try {
      final Rules rules = kb.getRules();
      final List<Rule> lst = rules.getRule();
      for (final Rule r : lst) {
        final String rid = r.getRuleID();
        final String vid = r.getVariantID();
        if (StringUtils.isEmpty(rid) || StringUtils.isEmpty(vid)) {
          valid = false;
          LOGGER.warn("Rule contains illegal IDs: {}", r);
        }
        final Variant v = kb.getVariant(vid);
        if ((v == null) || !v.getId().equals(vid)) {
          valid = false;
          LOGGER.warn("Variant referenced by Rule does not exists: {}", r);
        }
        final String peid = r.getPremiseEventID();
        final Event pe = kb.getEvent(peid);
        if ((pe == null) || !pe.getId().equals(peid)) {
          valid = false;
          LOGGER.warn("Referenced Premise-Event does not exists: {}", r);
        }
        final String teid = r.getTriggerEventID();
        final Event te = kb.getEvent(teid);
        if ((te == null) || !te.getId().equals(teid)) {
          valid = false;
          LOGGER.warn("Referenced Trigger-Event does not exists: {}", r);
        }
        final String ceid = r.getConclusionEventID();
        final Event ce = kb.getEvent(ceid);
        if ((ce == null) || !ce.getId().equals(ceid)) {
          valid = false;
          LOGGER.warn("Referenced Conclusion-Event does not exists: {}", r);
        }
      }
    }
    catch (final Exception ex) {
      valid = false;
    }
    if (!valid) {
      throw new ValidationException("KnowledgeBase is not valid! See logfiles for details.");
    }
  }

}
