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

import org.psikeds.queryagent.interfaces.presenter.pojos.Alternatives;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constituents;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constitutes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Event;
import org.psikeds.queryagent.interfaces.presenter.pojos.Events;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.Fulfills;
import org.psikeds.queryagent.interfaces.presenter.pojos.InitResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purposes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Rule;
import org.psikeds.queryagent.interfaces.presenter.pojos.Rules;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;

/**
 * Helper for transforming POJOs from the Interface of the
 * Resolution-Engine (RE) into POJOs of the Interface of the
 * Query-Agent (QA) ... and vice versa.
 *
 * @author marco@juliano.de
 */
public interface Transformer {

  Alternatives re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Alternatives re);

  org.psikeds.resolutionengine.interfaces.pojos.Alternatives qa2re(final Alternatives qa);

  Constituents re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Constituents re);

  org.psikeds.resolutionengine.interfaces.pojos.Constituents qa2re(final Constituents qa);

  Constitutes re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Constitutes re);

  org.psikeds.resolutionengine.interfaces.pojos.Constitutes qa2re(final Constitutes qa);

  Event re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Event re);

  org.psikeds.resolutionengine.interfaces.pojos.Event qa2re(final Event qa);

  Events re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Events re);

  org.psikeds.resolutionengine.interfaces.pojos.Events qa2re(final Events qa);

  Feature re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Feature re);

  org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(final Feature qa);

  Features re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Features re);

  org.psikeds.resolutionengine.interfaces.pojos.Features qa2re(final Features qa);

  Fulfills re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Fulfills re);

  org.psikeds.resolutionengine.interfaces.pojos.Fulfills qa2re(final Fulfills qa);

  Purpose re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purpose re);

  org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(final Purpose qa);

  Purposes re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purposes re);

  org.psikeds.resolutionengine.interfaces.pojos.Purposes qa2re(final Purposes qa);

  Rule re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Rule re);

  org.psikeds.resolutionengine.interfaces.pojos.Rule qa2re(final Rule qa);

  Rules re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Rules re);

  org.psikeds.resolutionengine.interfaces.pojos.Rules qa2re(final Rules qa);

  Variant re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variant re);

  org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(final Variant qa);

  Variants re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variants re);

  org.psikeds.resolutionengine.interfaces.pojos.Variants qa2re(final Variants qa);

  KnowledgeEntity re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re);

  org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(final KnowledgeEntity qa);

  InitResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.InitResponse re);

  org.psikeds.resolutionengine.interfaces.pojos.InitResponse qa2re(final InitResponse qa);

  SelectRequest re2qa(final org.psikeds.resolutionengine.interfaces.pojos.SelectRequest re);

  org.psikeds.resolutionengine.interfaces.pojos.SelectRequest qa2re(final SelectRequest qa);

  SelectResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.SelectResponse re);

  org.psikeds.resolutionengine.interfaces.pojos.SelectResponse qa2re(final SelectResponse qa);
}
