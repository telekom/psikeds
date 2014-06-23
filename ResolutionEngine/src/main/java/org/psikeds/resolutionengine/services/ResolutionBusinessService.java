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
import org.psikeds.common.util.JSONHelper;
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
      LOGGER.debug("--> init()");
      // init is an empty request without knowledge or decission, creating a new session
      return handleRequest(null, true);
    }
    finally {
      LOGGER.debug("<-- init()");
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
      LOGGER.debug("--> current()");
      // current is a request with just a sessionid and without any knowledge or decissions
      final ResolutionRequest req = (StringUtils.isEmpty(sessionID) ? null : new ResolutionRequest(sessionID));
      return handleRequest(req, false);
    }
    finally {
      LOGGER.debug("<-- current()");
    }
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#select(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    try {
      LOGGER.debug("--> select()");
      // resolve request and keep current session
      return handleRequest(req, false);
    }
    finally {
      LOGGER.debug("<-- select()");
    }
  }

  /**
   * @param req
   * @return ResolutionResponse
   * @see org.psikeds.resolutionengine.interfaces.services.ResolutionService#predict(org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse predict(final ResolutionRequest req) {
    try {
      LOGGER.debug("--> predict()");
      // predict result by resolving request within a new session
      return handleRequest(req, true);
    }
    finally {
      LOGGER.debug("<-- predict()");
    }
  }

  // ----------------------------------------------------------------

  private ResolutionResponse handleRequest(final ResolutionRequest req, final boolean enforceSeparateSession) {
    ResolutionResponse resp = null;
    Metadata metadata = null;
    String oldSessionID = null;
    String newSessionID = null;
    boolean initialKnowledge = false;
    boolean freshSession = false;
    try {
      LOGGER.trace("--> handleRequest(); enforceSeparateSession = {}\nResolutionRequest = {}", enforceSeparateSession, req);
      // --- Step 0: check preconditions
      if (this.checkValidityAtRuntime) {
        checkValidity();
      }
      // --- Step 1: get metadata; either from request or kb
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
      // --- Step 2: get or create session
      oldSessionID = (req == null ? null : req.getSessionID());
      if (StringUtils.isEmpty(oldSessionID)) {
        // no session, create new one
        oldSessionID = this.gen.getNextId();
        newSessionID = oldSessionID;
        freshSession = true;
        LOGGER.info("No Session, created new one: {}", newSessionID);
      }
      else if (enforceSeparateSession) {
        // use data from old session but create a new one (for prediction)
        freshSession = true;
        newSessionID = this.gen.getNextId();
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Created a separate Session:\n{}  -->  {}", oldSessionID, newSessionID);
        }
        else {
          LOGGER.info("Created a separate Session: {}", newSessionID);
        }
      }
      else {
        // just resume current session
        newSessionID = oldSessionID;
        freshSession = false;
        LOGGER.debug("Resuming existing Session: {}", newSessionID);
      }
      if (freshSession) {
        this.cache.removeSession(newSessionID); // just to be sure!
      }
      // --- Step 3: get knowledge; either from request or cache or kb
      Knowledge knowledge = (req == null ? null : req.getKnowledge());
      if (knowledge == null) {
        knowledge = (Knowledge) this.cache.getObject(oldSessionID, SESS_KEY_KNOWLEDGE);
        if (knowledge == null) {
          initialKnowledge = true;
          this.cache.removeSession(oldSessionID); // new knowledge, clean state
          knowledge = createInitialKnowledge(metadata);
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Created new initial Knowledge for SessionID {}:\n{}", oldSessionID, knowledge);
          }
          else {
            LOGGER.info("Created new initial Knowledge for SessionID {}.", oldSessionID);
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
          LOGGER.info("Using Knowledge supplied by Client.");
        }
      }
      this.cache.saveObject(oldSessionID, SESS_KEY_KNOWLEDGE, knowledge);
      // --- Step 4: get or create raeh
      RulesAndEventsHandler raeh = (RulesAndEventsHandler) this.cache.getObject(oldSessionID, SESS_KEY_RULES_AND_EVENTS);
      if (raeh == null) {
        raeh = RulesAndEventsHandler.init(getKnowledgeBase(), knowledge);
        this.cache.saveObject(oldSessionID, SESS_KEY_RULES_AND_EVENTS, raeh);
        LOGGER.trace("Created new RAEH based on Session {}:\n{}", oldSessionID, raeh);
      }
      else if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Got existing RAEH from Cache for Session {}:\n{}", oldSessionID, raeh);
      }
      // --- Step 5: clone data if necessary
      if (!oldSessionID.equals(newSessionID)) {
        LOGGER.debug("Cloning Knowledge and RAEH for Usage in separate Session.");
        knowledge = JSONHelper.copy(knowledge, Knowledge.class);
        raeh = raeh.copy();
      }
      // --- Step 6: update cache for new session
      this.cache.saveObject(newSessionID, SESS_KEY_KNOWLEDGE, knowledge);
      this.cache.saveObject(newSessionID, SESS_KEY_RULES_AND_EVENTS, raeh);
      // --- Step 7: resolve Knowledge based on Decission(s)
      final Decissions decissions = (req == null ? null : req.getDecissions());
      if (!initialKnowledge || this.resolveInitialKnowledge) {
        // Sometimes there might be some automatic Resolutions also for
        // Initial-Knowledge right in the Beginning, e.g. some Root-Purpose
        // with exactly one Variant which can be auto-resolved.
        // However this automatic Resolution might not be desired, e.g.
        // if Root-Purposes are just Alternatives and not all mandatory.
        // Therefore this Functionality is configurable!
        knowledge = resolveDecissions(knowledge, decissions, metadata, raeh);
      }
      // --- Step 8: create Response-Object
      resp = new ResolutionResponse(newSessionID, metadata, knowledge);
      if (resp.isResolved()) {
        // done, cleanup session
        this.cache.removeSession(newSessionID);
        LOGGER.info("Resolution {} finished.", newSessionID);
      }
      else {
        // cache current state of resolution for next request
        this.cache.saveObject(newSessionID, SESS_KEY_KNOWLEDGE, knowledge);
        this.cache.saveObject(newSessionID, SESS_KEY_RULES_AND_EVENTS, raeh);
        LOGGER.debug("Resolved next Step for Session {}", newSessionID);
      }
    }
    catch (final Exception ex) {
      LOGGER.warn("Could not handle Request: " + ex.getMessage(), ex);
      final Errors errors = new Errors();
      errors.add(new ErrorMessage(ex));
      resp = new ResolutionResponse(newSessionID, metadata, errors);
    }
    finally {
      LOGGER.trace("<-- handleRequest(); enforceSeparateSession = {}; freshSession = {}; initialKnowledge = {}\noldSessionID = {}; newSessionID = {}\nResolutionResponse = {}", enforceSeparateSession,
          freshSession, initialKnowledge, oldSessionID, newSessionID, resp);
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

  private Knowledge createInitialKnowledge(final Metadata metadata) {
    try {
      LOGGER.trace("--> createInitialKnowledge()");
      // initial knowledge consists of root-purposes and their
      // fulfilling variants as first choices for the client
      final Purposes purps = this.kb.getRootPurposes();
      final VariantChoices choices = new VariantChoices();
      final List<Purpose> plst = purps.getPurpose();
      for (final Purpose p : plst) {
        final String purposeId = (p == null ? null : p.getPurposeID());
        final Fulfills ff = (StringUtils.isEmpty(purposeId) ? null : this.kb.getFulfills(purposeId));
        if (ff != null) {
          LOGGER.trace("Adding choices for Root-Purpose {}", purposeId);
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
    finally {
      LOGGER.trace("<-- createInitialKnowledge()");
    }
  }

  private Knowledge resolveDecissions(Knowledge knowledge, final List<Decission> decissions, final Metadata metadata, final RulesAndEventsHandler raeh) {
    try {
      LOGGER.trace("--> resolveDecissions()");
      if (knowledge == null) {
        throw new ResolutionException("Cannot resolve Decissions, Knowledge is NULL!!!");
      }
      if (raeh == null) {
        throw new ResolutionException("Cannot resolve Decissions, RulesAndEventsHandler is NULL!!!");
      }
      if ((decissions == null) || decissions.isEmpty()) {
        knowledge = autoResolve(knowledge, metadata, raeh);
      }
      else {
        LOGGER.debug("Total of {} Decission(s)", decissions.size());
        for (final Decission decission : decissions) {
          knowledge = resolve(knowledge, decission, metadata, raeh);
        }
      }
      return knowledge;
    }
    finally {
      LOGGER.trace("<-- resolveDecissions()");
    }
  }

  private Knowledge autoResolve(final Knowledge knowledge, final Metadata metadata, final RulesAndEventsHandler raeh) {
    LOGGER.debug("Auto-Resolution!");
    return resolve(knowledge, null, metadata, raeh);
  }

  private Knowledge resolve(Knowledge knowledge, final Decission decission, final Metadata metadata, final RulesAndEventsHandler raeh) {
    boolean ok = false;
    try {
      LOGGER.trace("--> resolve(); Decission = {}", decission);
      // in the beginning knowledge is clean/stable
      knowledge.setStable(true);
      // Invoke every Resolver in Chain
      for (final Resolver res : getResolvers()) {
        knowledge = res.resolve(knowledge, decission, raeh, metadata);
      }
      if (!knowledge.isStable()) {
        // Some Resolver signaled that the Knowledge is not stable yet, i.e.
        // that it must be re-resolved again. This happens e.g. after a Rule
        // was applied or Choices with only one Variant were auto-completed.
        // Note: We do not supply any Decission because this was triggered
        // automatically and not by a Client-Interaction.
        knowledge = autoResolve(knowledge, metadata, raeh);
      }
      // done
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.trace("<-- resolve(); " + (ok ? "OK." : "ERROR!") + "\nKnowledge = {}", knowledge);
    }
  }
}
