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
import org.psikeds.resolutionengine.datalayer.vo.Concept;
import org.psikeds.resolutionengine.datalayer.vo.Concepts;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;

/**
 * Validator checking that all Concepts are valid/consistent.
 * 
 * @author marco@juliano.de
 * 
 */
public class ConceptValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConceptValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Concepts ...");
      boolean valid = true;
      long count = 0;
      // Note: Concepts are optional, i.e. this Node can be null!
      final Concepts concepts = (kb == null ? null : kb.getConcepts());
      final List<Concept> lst = (concepts == null ? null : concepts.getConcept());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Concept c : lst) {
          count++;
          final String cid = (c == null ? null : c.getConceptID());
          if (StringUtils.isEmpty(cid)) {
            valid = false;
            LOGGER.warn("Illegal Concept: {}", c);
            continue;
          }
          final Concept clookup = kb.getConcept(cid);
          if ((clookup == null) || !cid.equals(clookup.getConceptID())) {
            valid = false;
            LOGGER.warn("Concept not found: {}", c);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Concept: {}", c);
          }
          if (StringUtils.isEmpty(c.getLabel()) || !c.getLabel().equals(clookup.getLabel())) {
            valid = false;
            LOGGER.warn("Concept {} has no Label.", cid);
          }
          final List<String> features = c.getFeatureIds();
          final int numFeatures = (features == null ? 0 : features.size());
          if (numFeatures <= 0) {
            valid = false;
            LOGGER.warn("Concept {} has no Feature-IDs!", cid);
          }
          final List<FeatureValue> values = c.getValues();
          final int numValues = (values == null ? 0 : values.size());
          if (numValues <= 0) {
            valid = false;
            LOGGER.warn("Concept {} has no Feature-Values!", cid);
          }
          else {
            final List<String> found = new ArrayList<String>();
            for (final FeatureValue fv : values) {
              final String featureValueId = (fv == null ? null : fv.getFeatureValueID());
              final String featureId = (fv == null ? null : fv.getFeatureID());
              if (StringUtils.isEmpty(featureValueId) || StringUtils.isEmpty(featureId)) {
                valid = false;
                LOGGER.warn("Concept {} contains illegal Feature-Value: {}", cid, fv);
                continue;
              }
              if (found.contains(featureId)) {
                valid = false;
                LOGGER.warn("Concept {} contains duplicate Values for Feature {}", cid, featureId);
              }
              else {
                found.add(featureId);
                if ((numFeatures > 0) && !features.contains(featureId)) {
                  valid = false;
                  LOGGER.warn("Concept {} contains Feature-Value {} for unknown Feature {}", cid, featureValueId, featureId);
                }
              }
            }
            final int numFound = found.size();
            if ((numValues != numFeatures) || (numValues != numFound) || (numFeatures != numFound)) {
              valid = false;
              LOGGER.warn("Inconsistency in Concept {}. Number of Features not identical wih Number of Values! values = {} ; features = {] ; found = {}", cid, numValues, numFeatures, numFound);
            }
          }
        }
        LOGGER.debug("Checked {} Concepts.", count);
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase contains invalid Concepts! See logfiles for details.");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Concepts ... OK.");
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Concepts ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... failed to validate KnowledgeBase regarding Concepts: " + ex.getMessage(), ex);
      throw new ValidationException("Failed to validate KnowledgeBase regarding Concepts.", ex);
    }
  }
}
