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
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValueType;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;

/**
 * Helper for transforming POJOs from the Interface of the
 * Resolution-Engine (RE) into POJOs of the Interface of the
 * Query-Agent (QA) ... and vice versa.
 * 
 * @author marco@juliano.de
 */
public interface Transformer {

  Choice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Choice re);

  org.psikeds.resolutionengine.interfaces.pojos.Choice qa2re(final Choice qa);

  Decission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Decission re);

  org.psikeds.resolutionengine.interfaces.pojos.Decission qa2re(final Decission qa);

  Feature re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Feature re);

  org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(final Feature qa);

  FeatureValueType re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType re);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType qa2re(final FeatureValueType qa);

  Knowledge re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Knowledge re);

  org.psikeds.resolutionengine.interfaces.pojos.Knowledge qa2re(final Knowledge qa);

  KnowledgeEntity re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re);

  org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(final KnowledgeEntity qa);

  Metadata re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Metadata re);

  org.psikeds.resolutionengine.interfaces.pojos.Metadata qa2re(final Metadata qa);

  Purpose re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purpose re);

  org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(final Purpose qa);

  ResolutionRequest re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest qa2re(final ResolutionRequest qa);

  ResolutionResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse re);

  org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse qa2re(final ResolutionResponse qa);

  Variant re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variant re);

  org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(final Variant qa);
}
