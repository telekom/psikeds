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
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
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

  private static final String SESS_KEY_KNOWLEDGE = "Knowledge";
  private static final String SESS_KEY_EVENTS = "Events";
  private static final String SESS_KEY_RULES = "Rules";

  private KnowledgeBase kb;
  private List<Resolver> resolverChain;
  private ResolutionCache cache;
  private Transformer trans;
  private IdGenerator gen;

  public ResolutionBusinessService() {
    this(null, null, null, null, null);
  }

  public ResolutionBusinessService(final KnowledgeBase kb, final List<Resolver> resolverChain, final ResolutionCache cache, final Transformer trans, final IdGenerator gen) {
    this.kb = kb;
    this.resolverChain = resolverChain;
    this.cache = cache;
    this.trans = trans;
    this.gen = gen;
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

  public List<Resolver> getResolvers() {
    if (this.resolverChain == null) {
      this.resolverChain = new ArrayList<Resolver>();
    }
    return this.resolverChain;
  }

  public void setResolvers(final List<Resolver> resolverChain) {
    this.resolverChain = resolverChain;
  }

  public void addResolver(final Resolver res) {
    getResolvers().add(res);
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
    Validate.isTrue(getResolvers().size() > 0, "No Resolver-Chain!");
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
      checkValidity();
      // create intial data for new resolution session
      final Metadata metadata = getMetadata();
      final String sessionID = createSessionId(metadata);
      final Knowledge knowledge = getInitialKnowledge(metadata);
      // save intial knowledge and metadata in cache for next request
      this.cache.saveObject(sessionID, SESS_KEY_KNOWLEDGE, knowledge);
      resp = new ResolutionResponse(sessionID, metadata, knowledge);
      return resp;
    }
    finally {
      LOGGER.debug("init() = {}", resp);
    }
  }

  /**
   * @param req
   *          ResolutionRequest
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    ResolutionResponse resp = null;
    try {
      checkValidity();
      // get data from request
      String sessionID = req.getSessionID();
      Knowledge oldKnowledge = req.getKnowledge();
      // TODO: tbd: Should we really accept Metadata from a Client's Request?
      final Metadata metadata = req.getMetadata() != null ? req.getMetadata() : getMetadata();
      final Decission decission = req.getMadeDecission();

      if (!StringUtils.isEmpty(sessionID) && (oldKnowledge == null)) {
        // get cached knowledge for session-id
        oldKnowledge = (Knowledge) this.cache.getObject(sessionID, SESS_KEY_KNOWLEDGE);
        LOGGER.debug("Using cached Knowledge for SessionID {}", sessionID);
      }
      else if ((oldKnowledge != null) && StringUtils.isEmpty(sessionID)) {
        // create new session-id for supplied knowledge
        sessionID = createSessionId(metadata);
        LOGGER.debug("Created new SessionID {} for supplied Knowledge", sessionID);
      }
      else {
        throw new ResolutionException("Illegal Resolution-Request: Exactly one of Knowledge or Session-ID required.");
      }

      // resolve new knowledge based on decission and metadata
      final Knowledge newKnowledge = resolve(oldKnowledge, decission, metadata);
      resp = new ResolutionResponse(sessionID, metadata, newKnowledge);

      if (resp.isResolved()) {
        // resolution finished ... cleanup session
        this.cache.removeSession(sessionID);
      }
      else {
        // cache current state of resolution for next request
        this.cache.saveObject(sessionID, SESS_KEY_KNOWLEDGE, newKnowledge);
      }
      return resp;
    }
    finally {
      LOGGER.debug("select(\n{}\n) = {}", req, resp);
    }
  }

  // ----------------------------------------------------------------

  private Knowledge getInitialKnowledge(final Metadata metadata) {
    // initial knowledge consists of root-purposes and their
    // fulfilling variants as first choices for the client
    final Purposes purps = this.kb.getRootPurposes();
    final List<Choice> choices = new ArrayList<Choice>();
    final List<Purpose> plst = purps.getPurpose();
    for (final Purpose p : plst) {
      final Variants vars = this.kb.getFulfillingVariants(p);
      final Choice c = this.trans.valueObject2Pojo(p, vars);
      choices.add(c);
    }
    // Initially there are no Entities, only Choice containing the Root-Purposes.
    Knowledge knowledge = new Knowledge(choices);
    // However we must execute the Resolver-Chain, because in some rare Cases
    // there might be some automatic Resolutions right in the Beginning, e.g.
    // some Root-Purpose with exactly one variant.
    knowledge = resolve(knowledge, null, metadata);
    LOGGER.trace("Generated initial Knowledge: {}", knowledge);
    return knowledge;
  }

  private Knowledge resolve(final Knowledge oldKnowledge, final Decission decission, final Metadata metadata) {
    Knowledge knowledge = oldKnowledge;
    try {
      knowledge.setStable(true);
      for (final Resolver res : getResolvers()) {
        knowledge = res.resolve(knowledge, decission, metadata);
      }
      if (!knowledge.isStable()) {
        // Some Resolver signaled that the Knowledge is not stable yet, i.e.
        // that it must be re-resolved again. This happens e.g. after a Rule
        // was applied or Choices with only one Variant were auto-completed.
        // Note: We do not supply any Decission because this was triggered
        // automatically and not by a Client-Interaction.
        knowledge = resolve(knowledge, null, metadata);
      }
      return knowledge;
    }
    finally {
      LOGGER.trace("Resolved Knowledge =\n{}\nbased on Decission = {}", knowledge, decission);
    }
  }

  private String createSessionId(final Metadata metadata) {
    String sessionID = null;
    try {
      sessionID = this.gen.getNextId();
      metadata.saveInfo(
          Metadata.SESSION_ID,
          sessionID
          );
      return sessionID;
    }
    finally {
      LOGGER.trace("Generated a new SessionID: {}", sessionID);
    }
  }

  private Metadata getMetadata() {
    return this.trans.valueObject2Pojo(this.kb.getMetadata());
  }

  private void checkValidity() {
    if (!this.kb.isValid()) {
      LOGGER.warn("KnowledgeBase is not valid. Results may be incorrect!");
      // QQQ throw exception???
    }
  }
}
