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
 * Validator checking that all Variants are consistent and reasonable.
 * 
 * @author marco@juliano.de
 * 
 */
public class VariantValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(VariantValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Variants ...");
      boolean valid = true;
      final Variants vars = (kb == null ? null : kb.getVariants());
      final List<Variant> lst = (vars == null ? null : vars.getVariant());
      if ((lst == null) || lst.isEmpty()) {
        valid = false;
        LOGGER.warn("KnowledgeBase does not contain any Variants!");
      }
      else {
        for (final Variant v : lst) {
          final String vid = (v == null ? null : v.getVariantID());
          if (StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("No VariantID: {}", v);
            continue;
          }
          if (StringUtils.isEmpty(v.getLabel())) {
            valid = false;
            LOGGER.warn("Variant {} has no Label!", vid);
          }
          String firstFID = null;
          final List<String> fids = v.getFeatureIds();
          if ((fids != null) && !fids.isEmpty()) {
            firstFID = fids.get(0);
            for (final String id : fids) {
              final Feature<?> f = kb.getFeature(id);
              final String fid = (f == null ? null : f.getFeatureID());
              if (StringUtils.isEmpty(fid)) {
                valid = false;
                LOGGER.warn("Feature {} referenced by Variant {} does not exist!", id, vid);
              }
            }
          }
          final String defaultValue = v.getDefaultFeatureValue();
          if (!StringUtils.isEmpty(defaultValue)) {
            if (StringUtils.isEmpty(firstFID)) {
              valid = false;
              LOGGER.warn("Variant {} has no Features but defines a Default-Value: {}", vid, defaultValue);
            }
            else {
              final Feature<?> f = kb.getFeature(firstFID);
              final List<String> values = (f == null ? null : f.getValuesAsStrings());
              if ((values == null) || values.isEmpty() || !values.contains(defaultValue)) {
                valid = false;
                LOGGER.warn("Variant {} defines for Feature {} a Default-Value that is not allowed: {}", vid, firstFID, defaultValue);
              }
            }
          }
        }
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase contains invalid Variants!");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Variants ... OK.");
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Variants ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... could not validate KnowledgeBase regarding Variants ... ERROR!", ex);
      throw new ValidationException("Could not validate Variants: " + ex.getMessage(), ex);
    }
  }
}
