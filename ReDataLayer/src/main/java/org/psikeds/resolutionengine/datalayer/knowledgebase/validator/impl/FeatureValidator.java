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
 * Basic Validator checking that all referenced Features are existing and reasonable.
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
      final Variants vars = (kb == null ? null : kb.getVariants());
      final List<Variant> vlst = (vars == null ? null : vars.getVariant());
      if ((vlst != null) && !vlst.isEmpty()) {
        for (final Variant v : vlst) {
          final List<String> flst = (v == null ? null : v.getFeatureIds());
          if ((flst != null) && !flst.isEmpty()) {
            for (final String fid : flst) {
              if (StringUtils.isEmpty(fid)) {
                valid = false;
                LOGGER.warn("Emptry FeatureID!");
                continue;
              }
              final Feature<?> f = kb.getFeature(fid);
              if ((f == null) || !fid.equals(f.getId())) {
                valid = false;
                LOGGER.warn("Unknown FeatureID: {}", fid);
                continue;
              }
              if (StringUtils.isEmpty(f.getLabel())) {
                valid = false;
                LOGGER.warn("Feature {} has no Label!", fid);
              }
              if (f.getValues().isEmpty()) {
                valid = false;
                LOGGER.warn("Feature {} has no Values!", fid);
              }
              final Class<?> ftype = f.getValueType();
              if (!String.class.equals(ftype) && !Integer.class.equals(ftype) && !Float.class.equals(ftype)) {
                valid = false;
                LOGGER.warn("Feature {} has unsupported Value Type: {}", fid, ftype);
              }
            }
          }
        }
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase contains invalid Features!");
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
      LOGGER.error("... could not validate KnowledgeBase regarding Features ... ERROR!", ex);
      throw new ValidationException("Could not validate Features: " + ex.getMessage(), ex);
    }
  }
}
