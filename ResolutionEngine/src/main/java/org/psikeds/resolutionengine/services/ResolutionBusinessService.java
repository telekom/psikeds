/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.resolutionengine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.resolutionengine.cache.ResolutionCache;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Alternatives;
import org.psikeds.resolutionengine.interfaces.pojos.Constituents;
import org.psikeds.resolutionengine.interfaces.pojos.Events;
import org.psikeds.resolutionengine.interfaces.pojos.Features;
import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Purposes;
import org.psikeds.resolutionengine.interfaces.pojos.Rules;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;
import org.psikeds.resolutionengine.interfaces.pojos.Variants;
import org.psikeds.resolutionengine.interfaces.services.ResolutionService;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * This Business Service is the actual implementation of
 * {@link org.psikeds.resolutionengine.interfaces.services.ResolutionService}
 *
 * It is wired via Spring to the REST-Service and used as a delegate.
 * 
 * @author marco@juliano.de
 */
public class ResolutionBusinessService implements ResolutionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolutionBusinessService.class);

  private KnowledgeBase kb;
  private Transformer trans;
  private IdGenerator gen;
  private ResolutionCache cache;

  public ResolutionBusinessService() {
    this(null, null, null, null);
  }

  public ResolutionBusinessService(final KnowledgeBase kb, final Transformer trans, final IdGenerator gen, final ResolutionCache cache) {
    this.kb = kb;
    this.trans = trans;
    this.gen = gen;
    this.cache = cache;
  }

  public KnowledgeBase getKnowledgeBase() {
    return this.kb;
  }

  public void setKnowledgeBase(final KnowledgeBase kb) {
    this.kb = kb;
  }

  public Transformer getTransformer() {
    return this.trans;
  }

  public void setTransformer(final Transformer trans) {
    this.trans = trans;
  }

  public IdGenerator getSessionIdGenerator() {
    return this.gen;
  }

  public void setSessionIdGenerator(final IdGenerator gen) {
    this.gen = gen;
  }

  public ResolutionCache getResolutionCache() {
    return this.cache;
  }

  public void setResolutionCache(final ResolutionCache cache) {
    this.cache = cache;
  }

  //-----------------------------------------------------

  /**
   * @return InitResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#init()
   */
  @Override
  public InitResponse init() {
    InitResponse resp = null;
    try {
      final String sessionID = createSessionId();
      final KnowledgeEntity ke = getInitialKnowledgeEntity();
      this.cache.saveSessionData(sessionID, ke);
      resp = new InitResponse(sessionID, ke);
      return resp;
    }
    finally {
      LOGGER.debug("init() = {}", resp);
    }
  }

  /**
   * @param req SelectRequest
   * @return SelectResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.SelectRequest)
   */
  @Override
  public SelectResponse select(final SelectRequest req) {
    SelectResponse resp = null;
    try {
      String sessionID = req.getSessionID();
      KnowledgeEntity origKE = req.getKnowledgeEntity();
      final String choice = req.getChoice();
      if (!StringUtils.isEmpty(sessionID) && origKE == null) {
        origKE = (KnowledgeEntity) this.cache.getSessionData(sessionID);
        LOGGER.debug("Using cached KnowledgeEntity for SessionID {}", sessionID);
      }
      else if (origKE != null && StringUtils.isEmpty(sessionID)) {
        // create a new session id for this knowledge entity
        sessionID = createSessionId();
        LOGGER.debug("Created new SessionID {} for supplied KnowledgeEntity.", sessionID);
      }
      final KnowledgeEntity newKE = resolve(origKE, choice);
      if (newKE.isFullyResolved()) {
        // resolution finished ... cleanup
        this.cache.removeSessionData(sessionID);
      }
      else {
        // cache current state of resolution for next request
        this.cache.saveSessionData(sessionID, newKE);
      }
      resp = new SelectResponse(sessionID, newKE);
      return resp;
    }
    finally {
      LOGGER.debug("select(\n{}\n) = {}", req, resp);
    }
  }

  //-----------------------------------------------------

  private KnowledgeEntity getInitialKnowledgeEntity() {
    final Features features = this.trans.valueObject2Pojo(this.kb.getFeatures());
    final Purposes purposes = this.trans.valueObject2Pojo(this.kb.getPurposes());
    final Variants variants = this.trans.valueObject2Pojo(this.kb.getVariants());
    final Alternatives alternatives = this.trans.valueObject2Pojo(this.kb.getAlternatives());
    final Constituents constituents = this.trans.valueObject2Pojo(this.kb.getConstituents());
    final Events events = this.trans.valueObject2Pojo(this.kb.getEvents());
    final Rules rules = this.trans.valueObject2Pojo(this.kb.getRules());
    final boolean fullyResolved = false;
    // TODO: create a reasonable initial knowledge entity instead of copying all data from the knowledge base!
    final KnowledgeEntity newKE = new KnowledgeEntity(features, purposes, variants, alternatives, constituents, events, rules, fullyResolved);
    LOGGER.trace("Generated initial KnowledgeEntity: {}", newKE);
    return newKE;
  }

  private KnowledgeEntity resolve(final KnowledgeEntity origKE, final String choice) {
    KnowledgeEntity newKE = null;
    if (StringUtils.isEmpty(choice)) {
      // no choice, no change
      newKE = origKE;
    }

    // TODO: perform some real resolution here!

    if (newKE == null) {
      // something went wrong, restart
      newKE = getInitialKnowledgeEntity();
    }
    LOGGER.trace("Resolved origKE = {}\nwith choice = {} to newKE = {}", origKE, choice, newKE);
    return newKE;
  }

  private String createSessionId() {
    final String sessionID = this.gen == null ? null : this.gen.getNextId();
    LOGGER.trace("Generated a new SessionID: {}", sessionID);
    return sessionID;
  }
}
