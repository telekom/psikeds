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
package org.psikeds.resolutionengine.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.resolutionengine.cache.ResolutionCache;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Variants;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
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
public class ResolutionBusinessService implements InitializingBean, ResolutionService {

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

  // ----------------------------------------------------------------

  /**
   * Check that ResolutionBusinessService was configured/wired correctly.
   *
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.trans, "No Transformer!");
    Validate.notNull(this.gen, "No Session-ID-Generator!");
    Validate.notNull(this.cache, "No Resolution-Cache!");
  }

  // ----------------------------------------------------------------

  /**
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#init()
   */
  @Override
  public ResolutionResponse init() {
    ResolutionResponse resp = null;
    try {
      final Metadata metadata = getMetadata();
      final String sessionID = createSessionId(metadata);
      final Knowledge knowledge = getInitialKnowledge(metadata);
      this.cache.saveSessionData(sessionID, knowledge);
      resp = new ResolutionResponse(sessionID, metadata, knowledge);
      return resp;
    }
    finally {
      LOGGER.debug("init() = {}", resp);
    }
  }

  /**
   * @param req ResolutionRequest
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    ResolutionResponse resp = null;
    try {
      // get data from request
      String sessionID = req.getSessionID();
      Knowledge oldKnowledge = req.getKnowledge();
      // XXX Is it really clever to accept Metadata from a Client's Request?
      final Metadata metadata = req.getMetadata() != null ? req.getMetadata() : getMetadata();
      final Decission decission = req.getMadeDecission();

      if (!StringUtils.isEmpty(sessionID) && oldKnowledge == null) {
        // get cached knowledge for session-id
        oldKnowledge = (Knowledge) this.cache.getSessionData(sessionID);
        LOGGER.debug("Using cached Knowledge for SessionID {}", sessionID);
      }
      else if (oldKnowledge != null && StringUtils.isEmpty(sessionID)) {
        // create new session-id for knowledge
        sessionID = createSessionId(metadata);
        LOGGER.debug("Created new SessionID {} for supplied Knowledge", sessionID);
      }
      else {
        throw new IllegalArgumentException("Illegal Resolution-Request: Exactly one of Knowledge or Session-ID required.");
      }

      // resolve new knowledge based on decission and metadata
      final Knowledge newKnowledge = resolve(oldKnowledge, decission, metadata);
      resp = new ResolutionResponse(sessionID, metadata, newKnowledge);

      if (resp.isResolved()) {
        // resolution finished ... cleanup
        this.cache.removeSessionData(sessionID);
      }
      else {
        // cache current state of resolution for next request
        this.cache.saveSessionData(sessionID, newKnowledge);
      }
      return resp;
    }
    finally {
      LOGGER.debug("select(\n{}\n) = {}", req, resp);
    }
  }

  // ----------------------------------------------------------------

  private Knowledge getInitialKnowledge(final Metadata metadata) {
    // TODO: tbd: what metadata? any metadata-specific purposes or variants?
    final Purposes purps = this.kb.getRootPurposes();
    final List<Choice> choices = new ArrayList<Choice>();
    final List<Purpose> plst = purps.getPurpose();
    for (final Purpose p : plst) {
      final Variants vars = this.kb.getFulfillingVariants(p);
      final Choice c = this.trans.valueObject2Pojo(p, vars);
      choices.add(c);
    }
    // Initially there are no Entities, only Choice containing the Root-Purposes
    final Knowledge knowledge = new Knowledge(choices);
    LOGGER.trace("Generated initial Knowledge: {}", knowledge);
    return knowledge;
  }

  private Knowledge resolve(final Knowledge oldKnowledge, final Decission decission, final Metadata metadata) {
    Knowledge newKnowledge = null;
    if (decission == null) {
      // no decission, no change
      newKnowledge = oldKnowledge;
    }
    else {

      // TODO: perform some real resolution here!

      if (newKnowledge == null) {
        // something went wrong, restart
        newKnowledge = getInitialKnowledge(metadata);
      }
    }
    LOGGER.trace("Resolved oldKnowledge = {}\nwith decission = {} to newKnowledge = {}", oldKnowledge, decission, newKnowledge);
    return newKnowledge;
  }

  private String createSessionId(final Metadata metadata) {
    String sessionID = null;
    try {
      // TODO: tbd: what metadata? any metadata-specific session settings?
      sessionID = this.gen.getNextId();
      return sessionID;
    }
    finally {
      LOGGER.trace("Generated a new SessionID: {}", sessionID);
    }
  }

  private Metadata getMetadata() {
    return this.trans.valueObject2Pojo(this.kb.getMetadata());
  }
}
