/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.transformer;

import org.psikeds.queryagent.interfaces.presenter.pojos.Choice;
import org.psikeds.queryagent.interfaces.presenter.pojos.Choices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decissions;
import org.psikeds.queryagent.interfaces.presenter.pojos.ErrorMessage;
import org.psikeds.queryagent.interfaces.presenter.pojos.Errors;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDescription;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValues;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntities;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionMessage;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warning;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warnings;

/**
 * Helper for transforming POJOs from the Interface of the Resolution-Engine (RE)
 * into POJOs of the Interface of the Query-Agent (QA) ... and vice versa.
 * 
 * It is used by {@link org.psikeds.queryagent.presenter.services.ResolutionBusinessService} in
 * order to transform Requests for and Responses from
 * {@link org.psikeds.queryagent.requester.client.ResolutionEngineClient}
 * 
 * Note: Currently the Interfaces of RE and QA are very similar, making this
 * Transformer "quite simple". However this will change for future Frontends
 * having a more sophisticated Data-Model.
 * 
 * @author marco@juliano.de
 */
public interface Transformer {

  Choice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Choice re);

  org.psikeds.resolutionengine.interfaces.pojos.Choice qa2re(final Choice qa);

  Choices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Choices re);

  org.psikeds.resolutionengine.interfaces.pojos.Choices qa2re(final Choices qa);

  Decission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Decission re);

  org.psikeds.resolutionengine.interfaces.pojos.Decission qa2re(final Decission qa);

  Decissions re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Decissions re);

  org.psikeds.resolutionengine.interfaces.pojos.Decissions qa2re(final Decissions qa);

  ErrorMessage re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage re);

  org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage qa2re(final ErrorMessage qa);

  Errors re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Errors re);

  org.psikeds.resolutionengine.interfaces.pojos.Errors qa2re(final Errors qa);

  Feature re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Feature re);

  org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(final Feature qa);

  FeatureChoice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice qa2re(final FeatureChoice qa);

  FeatureChoices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices qa2re(final FeatureChoices qa);

  FeatureDecission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission qa2re(final FeatureDecission qa);

  FeatureDescription re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureDescription re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureDescription qa2re(final FeatureDescription qa);

  Features re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Features re);

  org.psikeds.resolutionengine.interfaces.pojos.Features qa2re(final Features qa);

  FeatureValue re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValue qa2re(final FeatureValue qa);

  FeatureValues re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValues re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValues qa2re(final FeatureValues qa);

  Knowledge re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Knowledge re);

  org.psikeds.resolutionengine.interfaces.pojos.Knowledge qa2re(final Knowledge qa);

  KnowledgeEntities re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities re);

  org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities qa2re(final KnowledgeEntities qa);

  KnowledgeEntity re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re);

  org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(final KnowledgeEntity qa);

  Metadata re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Metadata re);

  org.psikeds.resolutionengine.interfaces.pojos.Metadata qa2re(final Metadata qa);

  Purpose re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purpose re);

  org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(final Purpose qa);

  ResolutionMessage re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage qa2re(final ResolutionMessage qa);

  ResolutionRequest re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest qa2re(final ResolutionRequest qa);

  ResolutionResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse qa2re(final ResolutionResponse qa);

  Variant re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variant re);

  org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(final Variant qa);

  VariantChoice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.VariantChoice re);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoice qa2re(final VariantChoice qa);

  VariantChoices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.VariantChoices re);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoices qa2re(final VariantChoices qa);

  VariantDecission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.VariantDecission re);

  org.psikeds.resolutionengine.interfaces.pojos.VariantDecission qa2re(final VariantDecission qa);

  Warning re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Warning re);

  org.psikeds.resolutionengine.interfaces.pojos.Warning qa2re(final Warning qa);

  Warnings re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Warnings re);

  org.psikeds.resolutionengine.interfaces.pojos.Warnings qa2re(final Warnings qa);
}
