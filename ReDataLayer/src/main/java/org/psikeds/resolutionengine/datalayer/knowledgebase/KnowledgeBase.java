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
package org.psikeds.resolutionengine.datalayer.knowledgebase;

import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Concept;
import org.psikeds.resolutionengine.datalayer.vo.Concepts;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValues;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameters;
import org.psikeds.resolutionengine.datalayer.vo.Relations;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

public interface KnowledgeBase {

  // -------------------------------
  // global Access to all Objects
  // -------------------------------

  MetaData getMetaData();

  Features getFeatures();

  FeatureValues getFeatureValues();

  Concepts getConcepts();

  Purposes getPurposes();

  Variants getVariants();

  Alternatives getAlternatives();

  Constituents getConstituents();

  Events getEvents();

  Rules getRules();

  RelationParameters getRelationParameters();

  Relations getRelations();

  // -------------------------------
  // get Objects by ID
  // -------------------------------

  Feature getFeature(String featureId);

  FeatureValue getFeatureValue(String featureValueID);

  Concept getConcept(String conceptID);

  Purpose getPurpose(String purposeId);

  Variant getVariant(String variantId);

  Event getEvent(String eventId);

  Rule getRule(String ruleId);

  RelationParameter getRelationParameter(String parameterID);

  Relation getRelation(String relationId);

  // -------------------------------
  // get Objects by referencing Object
  // -------------------------------

  Fulfills getFulfills(String purposeId);

  Constitutes getConstitutes(String variantId);

  long getQuantity(String variantId, String purposeId);

  Features getFeatures(String variantId);

  FeatureValues getFeatureValuesOfConcept(String conceptID);

  FeatureValues getFeatureValuesWithinRange(String featureId, String rangeID);

  Events getAttachedEvents(String variantId);

  Rules getAttachedRules(String variantId);

  RelationParameters getAttachedRelationParameters(String variantId);

  Relations getAttachedRelations(String variantId);

  // -------------------------------
  // specialized Methods
  // -------------------------------

  Purposes getRootPurposes();

  Variants getFulfillingVariants(String purposeId);

  boolean isFulfilledBy(String purposeId, String variantId);

  Purposes getConstitutingPurposes(String variantId);

  boolean isConstitutedBy(String variantId, String purposeId);

  boolean hasConcept(String variantId, String conceptID);

  boolean hasFeature(String variantId, String featureId);

  boolean hasFeatureValue(String featureID, String featureValueID);

  boolean isIncludedIn(String featureValueID, String conceptID);

  boolean isValid();
}
