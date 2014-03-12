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
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Validator checking that all Rules are valid and consistent.
 * 
 * @author marco@juliano.de
 * 
 */
public class RuleValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(RuleValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Rules ...");
      validateRules(kb);
      LOGGER.debug("... finished validating KnowledgeBase regarding Rules ... OK.");
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Rules ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... could not validate KnowledgeBase regarding Rules!", ex);
      throw new ValidationException("Could not validate KnowledgeBase regarding Rules!", ex);
    }
  }

  private void validateRules(final KnowledgeBase kb) throws ValidationException {
    boolean valid = true;
    try {
      // Note: Rules are optional, i.e. this Node can be null!
      final Rules rules = (kb == null ? null : kb.getRules());
      final List<Rule> lst = (rules == null ? null : rules.getRule());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Rule r : lst) {
          // --- Step 1: check basics ---
          final String rid = (r == null ? null : r.getRuleID());
          if (StringUtils.isEmpty(rid)) {
            valid = false;
            LOGGER.warn("Rule has no ID: {}", r);
            continue;
          }
          final String vid = (r == null ? null : r.getVariantID());
          if (StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("Rule is not attached to a Variant: {}", r);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Rule: {}", r);
          }
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !vid.equals(v.getVariantID())) {
            valid = false;
            LOGGER.warn("Variant {} referenced by Rule {} does not exists.", vid, rid);
          }
          // --- Step 2: check premise ---
          final String peid = (r == null ? null : r.getPremiseEventID());
          final Event pe = (StringUtils.isEmpty(peid) ? null : kb.getEvent(peid));
          if ((pe == null) || !peid.equals(pe.getEventID())) {
            valid = false;
            LOGGER.warn("Premise-Event {} referenced by Rule {} does not exists.", peid, rid);
          }
          else {
            final String pevid = pe.getVariantID();
            if (StringUtils.isEmpty(pevid) || !vid.equals(pevid)) {
              valid = false;
              LOGGER.warn("Premise-Event {} and Rule {} are not attached to the same Variant: {} vs. {}", peid, rid, pevid, vid);
            }
          }
          // --- Step 3: check conclusion ---
          final String ceid = (r == null ? null : r.getConclusionEventID());
          final Event ce = (StringUtils.isEmpty(ceid) ? null : kb.getEvent(ceid));
          if ((ce == null) || !ceid.equals(ce.getEventID())) {
            valid = false;
            LOGGER.warn("Conclusion-Event {} referenced by Rule {} does not exists.", ceid, rid);
          }
          else {
            final String cevid = ce.getVariantID();
            if (StringUtils.isEmpty(cevid) || !vid.equals(cevid)) {
              valid = false;
              LOGGER.warn("Conclusion-Event {} and Rule {} are not attached to the same Variant: {} vs. {}", ceid, rid, cevid, vid);
            }
          }
        }
      }
    }
    catch (final Exception ex) {
      valid = false;
      throw new ValidationException("Could not validate Rules", ex);
    }
    if (!valid) {
      throw new ValidationException("Invalid Rules in KnowledgeBase!");
    }
  }
}
