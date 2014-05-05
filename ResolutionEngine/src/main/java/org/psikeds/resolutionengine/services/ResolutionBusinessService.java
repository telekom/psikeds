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
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Decissions;
import org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage;
import org.psikeds.resolutionengine.interfaces.pojos.Errors;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
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

  public static final boolean DEFAULT_RESOLVE_INITIAL_KNOWLEDGE = false;
  public static final boolean DEFAULT_CHECK_VALIDITY_ON_STARTUP = true;
  public static final boolean DEFAULT_CHECK_VALIDITY_AT_RUNTIME = true;

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
    this(kb, resolverChain, cache, trans, gen, DEFAULT_RESOLVE_INITIAL_KNOWLEDGE, DEFAULT_CHECK_VALIDITY_ON_STARTUP, DEFAULT_CHECK_VALIDITY_AT_RUNTIME);
  }

  public ResolutionBusinessService(final KnowledgeBase kb, final List<Resolver> resolverChain, final ResolutionCache cache, final Transformer trans, final IdGenerator gen,
      final boolean resolve, final boolean startup, final boolean runtime) {
    this.kb = kb;
    this.resolverChain = resolverChain;
    this.cache = cache;
    this.trans = trans;
    this.gen = gen;
    this.resolveInitialKnowledge = resolve;
    this.checkValidityOnStartup = startup;
    this.checkValidityAtRuntime = runtime;
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
    if (LOGGER.isInfoEnabled()) {
      final StringBuilder sb = new StringBuilder("Config: Resolve initial Knowledge: {}\n");
      sb.append("Check validity of Knowledge-Base on Startup: {}\n");
      sb.append("Check validity of Knowledge-Base at Runtime: {}");
      LOGGER.info(sb.toString(), this.resolveInitialKnowledge, this.checkValidityOnStartup, this.checkValidityAtRuntime);
    }
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.trans, "No Transformer!");
    Validate.notNull(this.gen, "No Session-ID-Generator!");
    Validate.notNull(this.cache, "No Resolution-Cache!");
    Validate.isTrue(getResolvers().size() > 0, "No Resolver-Chain!");
    if (this.checkValidityOnStartup) {
      LOGGER.info("Checking validity of Knowledge-Base.");
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
    try {
      LOGGER.trace("--> init()");
      return current(null);  // init is just current without session or decission
    }
    finally {
      LOGGER.trace("<-- init()");
    }
  }

  /**
   * @param sessionID
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#current(java.lang.String)
   */
  @Override
  public ResolutionResponse current(final String sessionID) {
    try {
      LOGGER.trace("--> current(); sessionID = {}", sessionID);
      final ResolutionRequest req = (StringUtils.isEmpty(sessionID) ? null : new ResolutionRequest(sessionID));
      return select(req);  // current is just select without any decissions
    }
    finally {
      LOGGER.trace("<-- current()");
    }
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    ResolutionResponse resp = null;
    Metadata metadata = null;
    String sessionID = null;
    boolean initialKnowledge = false;
    boolean freshSession = false;
    try {
      LOGGER.trace("--> select(); req = {}", req);
      if (this.checkValidityAtRuntime) {
        checkValidity();
      }

      // Step 1: get data; either from request or cache or kb
      metadata = (req == null ? null : req.getMetadata());
      if (metadata == null) {
        metadata = this.trans.valueObject2Pojo(this.kb.getMetaData());
        LOGGER.debug("Created new default Metadata.");
      }
      else {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Using Metadata supplied by Client:\n{}", metadata);
        }
        else {
          LOGGER.debug("Using Metadata supplied by Client.");
        }
      }
      sessionID = (req == null ? null : req.getSessionID());
      if (StringUtils.isEmpty(sessionID)) {
        freshSession = true;
        sessionID = this.gen.getNextId();
        this.cache.removeSession(sessionID); // just to be sure!
        LOGGER.info("Created new Session: {}", sessionID);
      }
      else {
        LOGGER.debug("Resuming old Session: {}", sessionID);
      }
      Knowledge knowledge = (req == null ? null : req.getKnowledge());
      if (knowledge == null) {
        knowledge = (Knowledge) this.cache.getObject(sessionID, SESS_KEY_KNOWLEDGE);
        if (knowledge == null) {
          initialKnowledge = true;
          this.cache.removeSession(sessionID); // new knowledge, clean state
          knowledge = createInitialKnowledge(metadata, sessionID);
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Created new initial Knowledge for SessionID {}:\n{}", sessionID, knowledge);
          }
          else {
            LOGGER.info("Created new initial Knowledge for SessionID {}.", sessionID);
          }
        }
        else {
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Using existing Knowledge from Cache:\n{}", knowledge);
          }
          else {
            LOGGER.debug("Using existing Knowledge from Cache.");
          }
        }
      }
      else {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Using Knowledge supplied by Client:\n{}", knowledge);
        }
        else {
          LOGGER.debug("Using Knowledge supplied by Client.");
        }
      }
      // whatever way we got our Knowledge, update Cache
      this.cache.saveObject(sessionID, SESS_KEY_KNOWLEDGE, knowledge);

      // Step 2: resolve Knowledge based on Decissions
      final Decissions decissions = (req == null ? null : req.getDecissions());
      if (!initialKnowledge || this.resolveInitialKnowledge) {
        // Sometimes there might be some automatic Resolutions also for
        // Initial-Knowledge right in the Beginning, e.g. some Root-Purpose
        // with exactly one Variant which can be auto-resolved.
        // However this automatic Resolution might not be desired, e.g.
        // if Root-Purposes are just Alternatives and not all mandatory.
        // Therefore this Functionality is configurable!
        knowledge = resolveDecissions(knowledge, decissions, metadata, sessionID);
      }

      // Step 3: create Response-Object
      resp = new ResolutionResponse(sessionID, metadata, knowledge);
      if (resp.isResolved()) {
        // done, cleanup session
        this.cache.removeSession(sessionID);
        LOGGER.info("Resolution {} finished.", sessionID);
      }
      else {
        // cache current state of resolution for next request
        this.cache.saveObject(sessionID, SESS_KEY_KNOWLEDGE, knowledge);
        LOGGER.debug("Resolved next Step for SessionID {}", sessionID);
      }
    }
    catch (final Exception ex) {
      LOGGER.warn("Could not resolve Knowledge!", ex);
      final Errors errors = new Errors();
      errors.add(new ErrorMessage(ex));
      resp = new ResolutionResponse(sessionID, metadata, errors);
    }
    finally {
      LOGGER.trace("<-- select(); freshSession = {}; initialKnowledge = {}; resp = {}", freshSession, initialKnowledge, resp);
    }
    return resp;
  }

  // ----------------------------------------------------------------

  private void checkValidity() throws ResolutionException {
    if (!this.kb.isValid()) {
      final String errmsg = "KnowledgeBase is not valid. Results may be incorrect!";
      LOGGER.warn(errmsg);
      throw new ResolutionException(errmsg);
    }
  }

  private Knowledge createInitialKnowledge(final Metadata metadata, final String sessionID) {
    // initial knowledge consists of root-purposes and their
    // fulfilling variants as first choices for the client
    final Purposes purps = this.kb.getRootPurposes();
    final VariantChoices choices = new VariantChoices();
    final List<Purpose> plst = purps.getPurpose();
    for (final Purpose p : plst) {
      final String purposeId = (p == null ? null : p.getPurposeID());
      final Fulfills ff = (StringUtils.isEmpty(purposeId) ? null : this.kb.getFulfills(purposeId));
      if (ff != null) {
        final VariantChoice vc = new VariantChoice(this.trans.valueObject2Pojo(p)); // root purposes have default quantity
        for (final String vid : ff.getVariantID()) {
          final Variant v = this.kb.getVariant(vid);
          if (v != null) {
            vc.addVariant(this.trans.valueObject2Pojo(v));
          }
        }
        choices.add(vc);
      }
    }
    // Initially there are no Entities, only Variant-Choice for the Root-Purposes.
    return new Knowledge(choices);
  }

  // ----------------------------------------------------------------

  private Knowledge resolveDecissions(Knowledge knowledge, final List<Decission> decissions, final Metadata metadata, final String sessionID) {
    if (knowledge == null) {
      throw new ResolutionException("Cannot resolve Decissions, Knowledge is NULL!!!");
    }
    if ((decissions == null) || decissions.isEmpty()) {
      knowledge = autoResolve(knowledge, metadata, sessionID);
    }
    else {
      for (final Decission decission : decissions) {
        knowledge = resolve(knowledge, decission, metadata, sessionID);
      }
    }
    return knowledge;
  }

  private Knowledge autoResolve(final Knowledge knowledge, final Metadata metadata, final String sessionID) {
    LOGGER.debug("Auto-Resolution!");
    return resolve(knowledge, null, metadata, sessionID);
  }

  private Knowledge resolve(Knowledge knowledge, final Decission decission, final Metadata metadata, final String sessionID) {
    boolean ok = false;
    try {
      LOGGER.trace("--> resolve(); S = {}; D = {}", sessionID, decission);
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
        knowledge = autoResolve(knowledge, metadata, sessionID);
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
      raeh = RulesAndEventsHandler.init(getKnowledgeBase(), knowledge);
      saveRulesAndEvents(sessionID, raeh);
      LOGGER.trace("Created new RAEH for {}:\n{}", sessionID, raeh);
    }
    else if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got existing RAEH from Cache for {}:\n{}", sessionID, raeh);
    }
    return raeh;
  }

  private void saveRulesAndEvents(final String sessionID, final RulesAndEventsHandler raeh) {
    this.cache.saveObject(sessionID, SESS_KEY_RULES_AND_EVENTS, raeh);
  }
}
