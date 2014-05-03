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
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValues;
import org.psikeds.resolutionengine.datalayer.vo.Features;

/**
 * Validator checking that all referenced Features and FeatureValues
 * are existing and consistent.
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
      LOGGER.debug("Validating KnowledgeBase regarding Features and FeatureValues ...");
      boolean valid = true;
      long featureCount = 0;
      long valueCount = 0;
      // Note: Features and FeatureValues are optional, i.e. these Nodes can be null!
      final Features allFeatures = (kb == null ? null : kb.getFeatures());
      final List<Feature> featureList = (allFeatures == null ? null : allFeatures.getFeature());
      final boolean hasFeatures = ((featureList != null) && !featureList.isEmpty());
      final FeatureValues allValues = (kb == null ? null : kb.getFeatureValues());
      final List<FeatureValue> valueList = (allValues == null ? null : allValues.getValue());
      final boolean hasValues = ((valueList != null) && !valueList.isEmpty());
      if (hasFeatures && !hasValues) {
        valid = false;
        LOGGER.warn("KnowledgeBase contains Features but no Feature-Values!");
      }
      else if (!hasFeatures && hasValues) {
        valid = false;
        LOGGER.warn("KnowledgeBase contains Feature-Values but no Features!");
      }
      else if (hasFeatures && hasValues) {
        // 1.: check all feature values and their references to features
        for (final FeatureValue fv : valueList) {
          valueCount++;
          final String featureId = (fv == null ? null : fv.getFeatureID());
          final String featureValueId = (fv == null ? null : fv.getFeatureValueID());
          if (StringUtils.isEmpty(featureId) || StringUtils.isEmpty(featureValueId)) {
            valid = false;
            LOGGER.warn("Corrupt Feature-Value, IDs missing! Feature-ID = {} ; Feature-Value-ID = {}", featureId, featureValueId);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking FeatureValue: {}", fv);
          }
          // double check: get feature by id
          final Feature f = kb.getFeature(featureId);
          if ((f == null) || !featureId.equals(f.getFeatureID())) {
            valid = false;
            LOGGER.warn("Feature {} referenced by Feature-Value {} does not exist!", featureId, featureValueId);
          }
          else {
            final String fType = f.getType();
            final String vType = fv.getType();
            if (StringUtils.isEmpty(fType) || !fType.equals(vType)) {
              valid = false;
              LOGGER.warn("Feature {} and Feature-Value {} are not of the same Type: {} vs. {}", featureId, featureValueId, fType, vType);
            }
            if (!f.getValues().contains(fv)) {
              valid = false;
              LOGGER.warn("Feature-Value {} has a Reference to Feature {} but not vice versa. Check Implementation!!!\n{}\n{}", featureValueId, featureId, fv, f);
            }
            if (!featureList.contains(f)) {
              valid = false;
              LOGGER.warn("Feature {} referenced by Feature-Value {} is not within List of all Features. Check Implementation!!!\n{}\n{}", featureId, featureValueId, f, fv);
            }
          }
          // double check: get feature-value by id and compare
          final FeatureValue lookup = kb.getFeatureValue(featureValueId);
          if ((lookup == null) || !featureId.equals(lookup.getFeatureID()) || !featureValueId.equals(lookup.getFeatureValueID())) {
            valid = false;
            LOGGER.warn("Feature-Value not found: {}", fv);
            continue;
          }
          // check basic properties
          if (StringUtils.isEmpty(fv.getType()) || !fv.getType().equals(lookup.getType())) {
            valid = false;
            LOGGER.warn("Feature-Value {} has no Type!", featureValueId);
          }
          if (StringUtils.isEmpty(fv.getValue()) || !fv.getValue().equals(lookup.getValue())) {
            valid = false;
            LOGGER.warn("Feature-Value {} has no Value!", featureValueId);
          }
        }
        // 2.: check all features and their references to feature-values
        for (final Feature f : featureList) {
          featureCount++;
          final String featureId = (f == null ? null : f.getFeatureID());
          if (StringUtils.isEmpty(featureId)) {
            valid = false;
            LOGGER.warn("Corrupt Feature, no ID!\n{}", f);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Feature: {}", f);
          }
          // double check: get feature by id and compare
          final Feature flookup = kb.getFeature(featureId);
          if ((flookup == null) || !featureId.equals(flookup.getFeatureID())) {
            valid = false;
            LOGGER.warn("Feature not found: {}", f);
            continue;
          }
          // check basic properties
          if (StringUtils.isEmpty(f.getLabel()) || !f.getLabel().equals(flookup.getLabel())) {
            valid = false;
            LOGGER.warn("Feature {} has no Label!", featureId);
          }
          final String fType = f.getType();
          if (StringUtils.isEmpty(fType) || !fType.equals(flookup.getType())) {
            valid = false;
            LOGGER.warn("Feature {} has no Type!", featureId);
          }
          final List<FeatureValue> values = f.getValues();
          if ((values == null) || values.isEmpty()) {
            valid = false;
            LOGGER.warn("Feature {} has no Values!", featureId);
            continue;
          }
          // check feature values of this features
          for (final FeatureValue fv : values) {
            // lookup references
            final String featureValueId = (fv == null ? null : fv.getFeatureValueID());
            if (StringUtils.isEmpty(featureValueId)) {
              valid = false;
              LOGGER.warn("Feature {} contains corrupt Feature-Value: {}", featureId, fv);
              continue;
            }
            final FeatureValue vlookup = kb.getFeatureValue(featureValueId);
            if ((vlookup == null) || !featureId.equals(vlookup.getFeatureID()) || !featureValueId.equals(vlookup.getFeatureValueID())) {
              valid = false;
              LOGGER.warn("Feature {} references a non-existing Feature-Value: {}", featureId, fv);
              continue;
            }
            // check type of feature and value
            final String vType = fv.getType();
            if (StringUtils.isEmpty(vType) || !vType.equals(fType)) {
              valid = false;
              LOGGER.warn("Feature {} and Feature-Value {} are not of the same Type: {} vs. {}", featureId, featureValueId, fType, vType);
            }
            // check cross-reference to global list
            if (!valueList.contains(fv)) {
              valid = false;
              LOGGER.warn("Feature-Value {} referenced by Feature {} is not within List of all Values. Check Implementation!!!\n{}\n{}", featureValueId, featureId, fv, f);
            }
          }
        }
        LOGGER.debug("Checked {} Features and {} FeatureValues.", featureCount, valueCount);
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase contains invalid Features or FeatureValues!");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Features and FeatureValues ... OK.");
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Features and FeatureValues ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... could not validate KnowledgeBase regarding Features and FeatureValues ... ERROR!", ex);
      throw new ValidationException("Could not validate Features or FeatureValues: " + ex.getMessage(), ex);
    }
  }
}
