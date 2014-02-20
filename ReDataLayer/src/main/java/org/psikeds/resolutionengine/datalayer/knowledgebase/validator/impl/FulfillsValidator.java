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
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Basic Validator checking that all References in Alternatives/Fulfills are valid.
 * 
 * @author marco@juliano.de
 * 
 */
public class FulfillsValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FulfillsValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Alternatives / Fulfills ...");
      boolean valid = true;

      final Alternatives alts = kb.getAlternatives();
      final List<Fulfills> lst = alts.getFulfills();
      for (final Fulfills f : lst) {
        final String pid = f.getPurposeID();
        if (StringUtils.isEmpty(pid)) {
          valid = false;
          LOGGER.warn("Empty PurposeID!");
        }
        else {
          final Purpose p = kb.getPurpose(pid);
          if ((p == null) || !pid.equals(p.getId())) {
            valid = false;
            LOGGER.warn("Unknown PurposeID: {}", pid);
          }
          else if (StringUtils.isEmpty(p.getLabel())) {
            valid = false;
            LOGGER.warn("Purpose {} has no Label!", pid);
          }
        }
        final List<String> varids = f.getVariantID();
        if ((varids == null) || varids.isEmpty()) {
          valid = false;
          LOGGER.warn("No fulfilling Variants for PurposeID: {}", pid);
        }
        else {
          for (final String vid : varids) {
            if (StringUtils.isEmpty(vid)) {
              valid = false;
              LOGGER.warn("Empty VariantID for PurposeID: {}", pid);
            }
            else {
              final Variant v = kb.getVariant(vid);
              if ((v == null) || !vid.equals(v.getId())) {
                valid = false;
                LOGGER.warn("Unknown VariantID: {}", vid);
              }
              else if (StringUtils.isEmpty(v.getLabel())) {
                valid = false;
                LOGGER.warn("Variant {} has no Label!", vid);
              }
            }
          }
        }
        final long qty = f.getQuantity();
        if (qty < Fulfills.MINIMUM_QUANTITY) {
          valid = false;
          LOGGER.warn("Illegal Quantity: " + qty);
        }
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase is not valid! See logfiles for details.");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Alternatives / Fulfills ... OK.");
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Alternatives / Fulfills ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... failed to validate KnowledgeBase regarding Alternatives / Fulfills: " + ex.getMessage(), ex);
      throw new ValidationException("Failed to validate KnowledgeBase regarding Alternatives / Fulfills.", ex);
    }
  }
}
