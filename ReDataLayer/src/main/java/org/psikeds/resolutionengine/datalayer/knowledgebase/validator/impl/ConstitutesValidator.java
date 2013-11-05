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
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Basic Validator checking that all Constituents / Constitutes are valid.
 * 
 * @author marco@juliano.de
 * 
 */
public class ConstitutesValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConstitutesValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Constituents / Constitutes ...");
      boolean valid = true;

      final Constituents cons = kb.getConstituents();
      final List<Constitutes> lst = cons.getConstitutes();
      for (final Constitutes c : lst) {
        final String vid = c.getVariantID();
        if (StringUtils.isEmpty(vid)) {
          valid = false;
          LOGGER.warn("Empty VariantID!");
        }
        else {
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !vid.equals(v.getId())) {
            valid = false;
            LOGGER.warn("Unknown VariantID: " + vid);
          }
        }
        final List<String> purpids = c.getPurposeID();
        if ((purpids == null) || purpids.isEmpty()) {
          valid = false;
          LOGGER.warn("No constituting Purposes of VariantID: " + vid);
        }
        else {
          for (final String pid : purpids) {
            if (StringUtils.isEmpty(pid)) {
              valid = false;
              LOGGER.warn("Empty PurposeID for VariantID: " + vid);
            }
            else {
              final Purpose p = kb.getPurpose(pid);
              if ((p == null) || !pid.equals(p.getId())) {
                valid = false;
                LOGGER.warn("Unknown PurposeID: " + pid);
              }
            }
          }
        }
      }
      if (!valid) {
        LOGGER.warn("... finished validating KnowledgeBase regarding Constituents / Constitutes ... NOT VALID!");
        throw new ValidationException("KnowledgeBase is not valid! See logfiles for details.");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Constituents / Constitutes ... OK.");
      }
    }
    catch (final Exception ex) {
      LOGGER.error("... failed to validate KnowledgeBase regarding Constituents / Constitutes: " + ex.getMessage(), ex);
      throw new ValidationException("Failed to validate KnowledgeBase regarding Constituents / Constitutes.", ex);
    }
  }
}
