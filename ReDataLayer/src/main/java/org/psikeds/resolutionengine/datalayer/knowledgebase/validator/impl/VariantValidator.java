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
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
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
          // 1. check basic properties
          if (StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("No VariantID: {}", v);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Variant: {}", v);
          }
          if (StringUtils.isEmpty(v.getLabel())) {
            valid = false;
            LOGGER.warn("Variant {} has no Label!", vid);
          }
          // 2. check features
          boolean hasFeatures = false;
          boolean hasValues = false;
          boolean hasConcepts = false;
          final List<String> fids = v.getFeatureIds();
          if ((fids != null) && !fids.isEmpty()) {
            hasFeatures = true;
            for (final String id : fids) {
              final Feature f = kb.getFeature(id);
              final String fid = (f == null ? null : f.getFeatureID());
              if (StringUtils.isEmpty(fid) || !fid.equals(id)) {
                valid = false;
                LOGGER.warn("Feature {} referenced by Variant {} does not exist!", id, vid);
              }
            }
          }
          // 3. check feature values
          final List<FeatureValue> values = v.getFeatureValues();
          if ((values != null) && !values.isEmpty()) {
            hasValues = true;
            if (!hasFeatures) {
              valid = false;
              LOGGER.warn("Variant {} defines no Features but has Feature-Values.", vid);
            }
            else {
              for (final FeatureValue fv : values) {
                final String featureId = (fv == null ? null : fv.getFeatureID());
                final Feature f = (StringUtils.isEmpty(featureId) ? null : kb.getFeature(featureId));
                if ((f == null) || !featureId.equals(f.getFeatureID())) {
                  valid = false;
                  LOGGER.warn("Feature {} referenced by Variant {} does not exist!", featureId, vid);
                }
                else {
                  final String featureValueID = fv.getFeatureValueID();
                  final FeatureValue val = (StringUtils.isEmpty(featureValueID) ? null : kb.getFeatureValue(featureValueID));
                  if ((val == null) || !featureId.equals(val.getFeatureID()) || !featureValueID.equals(val.getFeatureValueID())) {
                    valid = false;
                    LOGGER.warn("Feature-Value {} of Feature {} referenced by Variant {} does not exist!", featureValueID, featureId, vid);
                  }
                  if (!v.getFeatureIds().contains(featureId)) {
                    valid = false;
                    LOGGER.warn("Feature-ID {} of Feature-Value {} is not within Feature-ID-List of Variant {}", featureId, featureValueID, vid);
                  }
                }
              }
            }
          }
          // 4. check concepts
          final List<Concept> concepts = v.getConcepts();
          if ((concepts != null) && !concepts.isEmpty()) {
            hasConcepts = true;
            if (!hasFeatures) {
              valid = false;
              LOGGER.warn("Variant {} defines no Features but has Concepts.", vid);
            }
            else {
              for (final Concept c : concepts) {
                final String cid = (c == null ? null : c.getConceptID());
                if (StringUtils.isEmpty(cid)) {
                  valid = false;
                  LOGGER.warn("Variant {} contains illegal Concept: {}", vid, c);
                  continue;
                }
                final Concept lookup = kb.getConcept(cid);
                if ((lookup == null) || !cid.equals(lookup.getConceptID())) {
                  valid = false;
                  LOGGER.warn("Concept {} referenced by Variant {} does not exist!", cid, vid);
                }
                final List<String> featlst = c.getFeatureIds();
                if ((featlst == null) || featlst.isEmpty()) {
                  valid = false;
                  LOGGER.warn("Concept {} referenced by Variant {} does not have any Features!", cid, vid);
                  continue;
                }
                for (final String featid : featlst) {
                  if (StringUtils.isEmpty(featid) || !fids.contains(featid)) {
                    valid = false;
                    LOGGER.warn("Concept {} referenced by Variant {} contains illegal Feature-ID: {}", cid, vid, featid);
                  }
                }
              }
            }
          }
          // final consistency check
          if (hasFeatures && !hasValues && !hasConcepts) {
            valid = false;
            LOGGER.warn("Variant {} defines Features but has neither Concepts nor Feature-Values.", vid);
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
