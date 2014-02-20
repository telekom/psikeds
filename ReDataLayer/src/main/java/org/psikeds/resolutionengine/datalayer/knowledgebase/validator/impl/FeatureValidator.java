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
import org.psikeds.resolutionengine.datalayer.vo.Features;

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
      // Note: Features are optional, i.e. this Node can be null!
      final Features feats = kb.getFeatures();
      final List<Feature<?>> flst = (feats == null ? null : feats.getFeature());
      if ((flst != null) && !flst.isEmpty()) {
        for (final Feature<?> f1 : flst) {
          final String fid = (f1 == null ? null : f1.getId());
          if (StringUtils.isEmpty(fid)) {
            valid = false;
            LOGGER.warn("No FeatureID: {}", f1);
            continue;
          }
          // double check: get feature by id and compare!
          final Feature<?> f2 = kb.getFeature(fid);
          if ((f2 == null) || !f1.equals(f2)) {
            valid = false;
            LOGGER.warn("Feature not found: {}", f1);
            continue;
          }
          // check basic properties
          if (StringUtils.isEmpty(f2.getLabel())) {
            valid = false;
            LOGGER.warn("Feature {} has no Label!", fid);
          }
          if (f2.getValues().isEmpty()) {
            valid = false;
            LOGGER.warn("Feature {} has no Values!", fid);
          }
          final Class<?> ftype = f2.getValueType();
          if (!String.class.equals(ftype) && !Integer.class.equals(ftype) && !Float.class.equals(ftype)) {
            valid = false;
            LOGGER.warn("Feature {} has unsupported Value Type: {}", fid, ftype);
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
