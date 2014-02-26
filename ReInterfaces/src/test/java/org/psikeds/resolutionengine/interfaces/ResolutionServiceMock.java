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
package org.psikeds.resolutionengine.interfaces;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.VariantDecission;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;

/**
 * A simple Mock-Implementation of ResolutionService for Testing.
 * 
 * @author marco@juliano.de
 * 
 */
public class ResolutionServiceMock implements ResolutionService {

  private Knowledge lastReturnedKnowledge;
  private Knowledge initialKnowledge;
  private Knowledge selectVariantKnowledge;
  private Knowledge selectFeatureKnowledge;
  private Metadata metadata;
  private IdGenerator gen;

  public ResolutionServiceMock(final Knowledge knowledge) {
    this(knowledge, knowledge, knowledge, null);
  }

  public ResolutionServiceMock(final Knowledge initialKnowledge, final Knowledge selectVariantKnowledge, final Knowledge selectFeatureKnowledge, final Metadata metadata) {
    this(initialKnowledge, selectVariantKnowledge, selectFeatureKnowledge, metadata, null);
  }

  public ResolutionServiceMock(final Knowledge initialKnowledge, final Knowledge selectVariantKnowledge, final Knowledge selectFeatureKnowledge, final Metadata metadata, final IdGenerator gen) {
    this.initialKnowledge = initialKnowledge;
    this.lastReturnedKnowledge = initialKnowledge;
    this.selectVariantKnowledge = selectVariantKnowledge;
    this.selectFeatureKnowledge = selectFeatureKnowledge;
    this.metadata = metadata;
    setIdGenerator(gen);
  }

  // ------------------------------------------------------

  public Knowledge getInitialKnowledge() {
    return this.initialKnowledge;
  }

  public void setInitialKnowledge(final Knowledge initialKnowledge) {
    this.initialKnowledge = initialKnowledge;
  }

  public Knowledge getSelectVariantKnowledge() {
    return this.selectVariantKnowledge;
  }

  public void setSelectVariantKnowledge(final Knowledge selectVariantKnowledge) {
    this.selectVariantKnowledge = selectVariantKnowledge;
  }

  public Knowledge getSelectFeatureKnowledge() {
    return this.selectFeatureKnowledge;
  }

  public void setSelectFeatureKnowledge(final Knowledge selectFeatureKnowledge) {
    this.selectFeatureKnowledge = selectFeatureKnowledge;
  }

  public Metadata getMetadata() {
    return this.metadata;
  }

  public void setMetadata(final Metadata metadata) {
    this.metadata = metadata;
  }

  public IdGenerator getIdGenerator() {
    return this.gen;
  }

  public void setIdGenerator(final IdGenerator gen) {
    try {
      this.gen = gen == null ? new SessionIdGenerator("TEST") : gen;
    }
    catch (final Exception ex) {
      this.gen = null;
    }
  }

  // ------------------------------------------------------

  /**
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#init()
   */
  @Override
  public ResolutionResponse init() {
    this.lastReturnedKnowledge = this.initialKnowledge;
    final String sessionID = (this.gen == null ? null : this.gen.getNextId());
    return new ResolutionResponse(sessionID, this.metadata, this.initialKnowledge);
  }

  /**
   * @param sessionID
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#current(java.lang.String)
   */
  @Override
  public ResolutionResponse current(final String sessionID) {
    if ((this.lastReturnedKnowledge == null) || StringUtils.isEmpty(sessionID)) {
      return init();
    }
    else {
      return new ResolutionResponse(sessionID, this.metadata, this.lastReturnedKnowledge);
    }
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    final String sessionID = (req == null ? null : req.getSessionID());
    if (StringUtils.isEmpty(sessionID)) {
      return init();
    }
    final List<Decission> lst = req.getDecissions();
    if ((lst == null) || lst.isEmpty()) {
      return current(sessionID);
    }
    // we only support single decissions in this mock
    final Decission decission = lst.get(0);
    // very simple: depending on the type of decission always return one of the knowledges
    if (decission instanceof VariantDecission) {
      this.lastReturnedKnowledge = this.selectVariantKnowledge;
      return new ResolutionResponse(sessionID, this.metadata, this.selectVariantKnowledge);
    }
    else if (decission instanceof FeatureDecission) {
      this.lastReturnedKnowledge = this.selectFeatureKnowledge;
      return new ResolutionResponse(sessionID, this.metadata, this.selectFeatureKnowledge);
    }
    else { // decission is null
      return current(sessionID);
    }
  }
}
