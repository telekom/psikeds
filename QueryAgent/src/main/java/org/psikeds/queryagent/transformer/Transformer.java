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
import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concepts;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decissions;
import org.psikeds.queryagent.interfaces.presenter.pojos.ErrorMessage;
import org.psikeds.queryagent.interfaces.presenter.pojos.Errors;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDecission;
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
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;
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

  Choice re2qa(org.psikeds.resolutionengine.interfaces.pojos.Choice re);

  org.psikeds.resolutionengine.interfaces.pojos.Choice qa2re(Choice qa);

  Choices re2qa(org.psikeds.resolutionengine.interfaces.pojos.Choices re);

  org.psikeds.resolutionengine.interfaces.pojos.Choices qa2re(Choices qa);

  org.psikeds.resolutionengine.interfaces.pojos.Concept qa2re(Concept qa);

  Concept re2qa(org.psikeds.resolutionengine.interfaces.pojos.Concept re);

  org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice qa2re(ConceptChoice qa);

  ConceptChoice re2qa(org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice re);

  org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices qa2re(ConceptChoices qa);

  ConceptChoices re2qa(org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices re);

  org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission qa2re(ConceptDecission qa);

  ConceptDecission re2qa(org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission re);

  org.psikeds.resolutionengine.interfaces.pojos.Concepts qa2re(Concepts qa);

  Concepts re2qa(org.psikeds.resolutionengine.interfaces.pojos.Concepts re);

  Decission re2qa(org.psikeds.resolutionengine.interfaces.pojos.Decission re);

  org.psikeds.resolutionengine.interfaces.pojos.Decission qa2re(Decission qa);

  Decissions re2qa(org.psikeds.resolutionengine.interfaces.pojos.Decissions re);

  org.psikeds.resolutionengine.interfaces.pojos.Decissions qa2re(Decissions qa);

  ErrorMessage re2qa(org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage re);

  org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage qa2re(ErrorMessage qa);

  Errors re2qa(org.psikeds.resolutionengine.interfaces.pojos.Errors re);

  org.psikeds.resolutionengine.interfaces.pojos.Errors qa2re(Errors qa);

  Feature re2qa(org.psikeds.resolutionengine.interfaces.pojos.Feature re);

  org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(Feature qa);

  FeatureChoice re2qa(org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice qa2re(FeatureChoice qa);

  FeatureChoices re2qa(org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices qa2re(FeatureChoices qa);

  FeatureDecission re2qa(org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission qa2re(FeatureDecission qa);

  Features re2qa(org.psikeds.resolutionengine.interfaces.pojos.Features re);

  org.psikeds.resolutionengine.interfaces.pojos.Features qa2re(Features qa);

  FeatureValue re2qa(org.psikeds.resolutionengine.interfaces.pojos.FeatureValue re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValue qa2re(FeatureValue qa);

  FeatureValues re2qa(org.psikeds.resolutionengine.interfaces.pojos.FeatureValues re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValues qa2re(FeatureValues qa);

  Knowledge re2qa(org.psikeds.resolutionengine.interfaces.pojos.Knowledge re);

  org.psikeds.resolutionengine.interfaces.pojos.Knowledge qa2re(Knowledge qa);

  KnowledgeEntities re2qa(org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities re);

  org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities qa2re(KnowledgeEntities qa);

  KnowledgeEntity re2qa(org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re);

  org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(KnowledgeEntity qa);

  Metadata re2qa(org.psikeds.resolutionengine.interfaces.pojos.Metadata re);

  org.psikeds.resolutionengine.interfaces.pojos.Metadata qa2re(Metadata qa);

  Purpose re2qa(org.psikeds.resolutionengine.interfaces.pojos.Purpose re);

  org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(Purpose qa);

  ResolutionMessage re2qa(org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage qa2re(ResolutionMessage qa);

  ResolutionRequest re2qa(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest qa2re(ResolutionRequest qa);

  ResolutionResponse re2qa(org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse qa2re(ResolutionResponse qa);

  Variant re2qa(org.psikeds.resolutionengine.interfaces.pojos.Variant re);

  org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(Variant qa);

  VariantChoice re2qa(org.psikeds.resolutionengine.interfaces.pojos.VariantChoice re);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoice qa2re(VariantChoice qa);

  VariantChoices re2qa(org.psikeds.resolutionengine.interfaces.pojos.VariantChoices re);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoices qa2re(VariantChoices qa);

  VariantDecission re2qa(org.psikeds.resolutionengine.interfaces.pojos.VariantDecission re);

  org.psikeds.resolutionengine.interfaces.pojos.VariantDecission qa2re(VariantDecission qa);

  Variants re2qa(org.psikeds.resolutionengine.interfaces.pojos.Variants re);

  org.psikeds.resolutionengine.interfaces.pojos.Variants qa2re(Variants qa);

  Warning re2qa(org.psikeds.resolutionengine.interfaces.pojos.Warning re);

  org.psikeds.resolutionengine.interfaces.pojos.Warning qa2re(Warning qa);

  Warnings re2qa(org.psikeds.resolutionengine.interfaces.pojos.Warnings re);

  org.psikeds.resolutionengine.interfaces.pojos.Warnings qa2re(Warnings qa);
}
