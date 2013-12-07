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
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
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
  private static final String SESS_KEY_RULES_AND_EVENTS = "RulesAndEvents";

  private KnowledgeBase kb;
  private List<Resolver> resolverChain;
  private ResolutionCache cache;
  private Transformer trans;
  private IdGenerator gen;
  private boolean resolveInitialKnowledge;
  private boolean checkValidityOnStartup;
  private boolean checkValidityAtRuntime;

  public ResolutionBusinessService() {
    this(null, null, null, null, null);
  }

  public ResolutionBusinessService(final KnowledgeBase kb, final List<Resolver> resolverChain, final ResolutionCache cache, final Transformer trans, final IdGenerator gen) {
    this.kb = kb;
    this.resolverChain = resolverChain;
    this.cache = cache;
    this.trans = trans;
    this.gen = gen;
    this.resolveInitialKnowledge = false;
    this.checkValidityOnStartup = true;
    this.checkValidityAtRuntime = true;
  }

  public boolean isCheckValidityOnStartup() {
    return this.checkValidityOnStartup;
  }

  public void setCheckValidityOnStartup(final boolean checkValidityOnStartup) {
    this.checkValidityOnStartup = checkValidityOnStartup;
  }

  public boolean isCheckValidityAtRuntime() {
    return this.checkValidityAtRuntime;
  }

  public void setCheckValidityAtRuntime(final boolean checkValidityAtRuntime) {
    this.checkValidityAtRuntime = checkValidityAtRuntime;
  }

  public boolean isResolveInitialKnowledge() {
    return this.resolveInitialKnowledge;
  }

  public void setResolveInitialKnowledge(final boolean resolveInitialKnowledge) {
    this.resolveInitialKnowledge = resolveInitialKnowledge;
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
    if (this.checkValidityOnStartup) {
      checkValidity();
    }
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
      LOGGER.trace("--> init()");
      if (this.checkValidityAtRuntime) {
        checkValidity();
      }
      // create intial data for new resolution session
      final Metadata metadata = getMetadata();
      final String sessionID = createSessionId(metadata);
      final Knowledge knowledge = getInitialKnowledge(metadata, sessionID);
      // save initial knowledge in cache for next request
      this.cache.saveObject(sessionID, SESS_KEY_KNOWLEDGE, knowledge);
      resp = new ResolutionResponse(sessionID, metadata, knowledge);
      return resp;
    }
    finally {
      LOGGER.trace("<-- init(); resp = {}", resp);
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
      LOGGER.trace("--> select(); req = {}", req);
      if (this.checkValidityAtRuntime) {
        checkValidity();
      }
      // get data from request
      String sessionID = req.getSessionID();
      Knowledge oldKnowledge = req.getKnowledge();
      // XXX: Should we really accept Metadata from a Client's Request?
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
      final Knowledge newKnowledge = resolve(oldKnowledge, decission, metadata, sessionID);
      resp = new ResolutionResponse(sessionID, metadata, newKnowledge);
      if (resp.isResolved()) {
        LOGGER.info("Resolution {} finished.", sessionID);
        // cleanup session
        this.cache.removeSession(sessionID);
      }
      else {
        // cache current state of resolution for next request
        this.cache.saveObject(sessionID, SESS_KEY_KNOWLEDGE, newKnowledge);
        LOGGER.info("Resolved next Step for {}\nK = {}", sessionID, newKnowledge);
      }
      return resp;
    }
    finally {
      LOGGER.trace("<-- select(); resp = {}", resp);
    }
  }

  // ----------------------------------------------------------------

  private Knowledge getInitialKnowledge(final Metadata metadata, final String sessionID) {
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
    if (this.resolveInitialKnowledge) {
      // We must execute the Resolver-Chain, because sometimes there
      // might be some automatic Resolutions right in the Beginning,
      // e.g. some Root-Purpose with exactly one variant.
      // However this automatic Resolution might not be desired, e.g.
      // if Root-Purposes are Alternatives not mandatory.
      // Therefore this Functionality is configurable.
      knowledge = resolve(knowledge, metadata, sessionID);
    }
    LOGGER.info("Generated initial Knowledge. S = {}\nK = {}", sessionID, knowledge);
    return knowledge;
  }

  private Knowledge resolve(final Knowledge oldKnowledge, final Metadata metadata, final String sessionID) {
    LOGGER.trace("Auto-Resolution!");
    return resolve(oldKnowledge, null, metadata, sessionID);
  }

  private Knowledge resolve(final Knowledge oldKnowledge, final Decission decission, final Metadata metadata, final String sessionID) {
    Knowledge knowledge = oldKnowledge;
    boolean ok = false;
    try {
      LOGGER.trace("--> resolve(); S = {}\nD = {}", sessionID, decission);
      knowledge.setStable(true);

      // Get all Rules and Events relevant for this Session
      final RulesAndEventsHandler raeh = getRulesAndEvents(knowledge, metadata, sessionID);

      // Invoke every Resolver in Chain
      for (final Resolver res : getResolvers()) {
        knowledge = res.resolve(knowledge, decission, raeh, metadata);
      }

      // Cache Rules and Events for later reuse
      saveRulesAndEvents(sessionID, raeh);

      if (!knowledge.isStable()) {
        // Some Resolver signaled that the Knowledge is not stable yet, i.e.
        // that it must be re-resolved again. This happens e.g. after a Rule
        // was applied or Choices with only one Variant were auto-completed.
        // Note: We do not supply any Decission because this was triggered
        // automatically and not by a Client-Interaction.
        knowledge = resolve(knowledge, metadata, sessionID);
      }

      // done
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.trace("<-- resolve(); " + (ok ? "OK." : "ERROR!") + "\nS = {}\nKnowledge = {}", sessionID, knowledge);
    }
  }

  // ----------------------------------------------------------------

  private RulesAndEventsHandler getRulesAndEvents(final Knowledge knowledge, final Metadata metadata, final String sessionID) {
    RulesAndEventsHandler raeh = (RulesAndEventsHandler) this.cache.getObject(sessionID, SESS_KEY_RULES_AND_EVENTS);
    if (raeh == null) {
      raeh = createInitialRulesAndEvents(knowledge, metadata, sessionID);
      saveRulesAndEvents(sessionID, raeh);
      LOGGER.trace("Created new REAH: {}", raeh);
    }
    else {
      LOGGER.trace("Got existing REAH from Cache: {}", raeh);
    }
    return raeh;
  }

  private RulesAndEventsHandler createInitialRulesAndEvents(final Knowledge knowledge, final Metadata metadata, final String sessionID) {
    // TODO: performance optimization: do not start with all rules and events but with "visible" ones
    return RulesAndEventsHandler.init(getKnowledgeBase());
  }

  private void saveRulesAndEvents(final String sessionID, final RulesAndEventsHandler raeh) {
    this.cache.saveObject(sessionID, SESS_KEY_RULES_AND_EVENTS, raeh);
  }

  // ----------------------------------------------------------------

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

  private void checkValidity() throws ResolutionException {
    if (!this.kb.isValid()) {
      final String errmsg = "KnowledgeBase is not valid. Results may be incorrect!";
      LOGGER.warn(errmsg);
      throw new ResolutionException(errmsg);
    }
  }
}
