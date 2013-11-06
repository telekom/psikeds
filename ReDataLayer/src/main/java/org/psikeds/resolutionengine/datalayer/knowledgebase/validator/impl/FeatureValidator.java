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
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * Basic Validator checking that all referenced Features exist.
 * 
 * @author marco@juliano.de
 * 
 */
public class FeatureValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Features ...");
      boolean valid = true;

      final Variants vars = kb.getVariants();
      final List<Variant> vlst = vars.getVariant();
      for (final Variant v : vlst) {
        final List<String> flst = v.getFeatureIds();
        for (final String fid : flst) {
          if (StringUtils.isEmpty(fid)) {
            valid = false;
            LOGGER.warn("Emptry FeatureID!");
          }
          final Feature f = kb.getFeature(fid);
          if ((f == null) || !fid.equals(f.getId())) {
            valid = false;
            LOGGER.warn("Unknown FeatureID: " + fid);
          }
        }
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase is not valid! See logfiles for details.");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Features ... OK.");
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Features ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... failed to validate KnowledgeBase regarding Features: " + ex.getMessage(), ex);
      throw new ValidationException("Failed to validate KnowledgeBase regarding Features.", ex);
    }
  }
}
