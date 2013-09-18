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
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

public interface KnowledgeBase {

  // -------------------------------
  // global Access to all Objects
  // -------------------------------

  Features getFeatures();

  Purposes getPurposes();

  Variants getVariants();

  Alternatives getAlternatives();

  Constituents getConstituents();

  Events getEvents();

  Rules getRules();

  // -------------------------------
  // get Objects by ID
  // -------------------------------

  Feature getFeature(String featureId);

  Purpose getPurpose(String purposeId);

  Variant getVariant(String variantId);

  Event getEvent(String eventId);

  Rule getRule(String ruleId);

  // -------------------------------
  // specialized Methods
  // -------------------------------

  Purposes getRootPurposes();

  Variants getFulfillingVariants(String purposeId);

  Variants getFulfillingVariants(Purpose purpose);

  Features getFeatures(String variantId);

  Features getFeatures(Variant variant);

  boolean isValid();
}
