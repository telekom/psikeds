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
package org.psikeds.resolutionengine.resolver.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.resolver.RelevantEvents;
import org.psikeds.resolutionengine.resolver.RelevantRules;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;

/**
 * This Resolver will evaluate the relevant Events und apply all Rules
 * that were triggered. Decission and Metadata will be ignored.
 * 
 * @author marco@juliano.de
 */
public class RulesEvaluator implements Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(RulesEvaluator.class);

  /**
   * @param knowledge
   *          current old Knowledge
   * @param decission
   *          Decission (ignored!)
   * @param events
   *          RelevantEvents
   * @param rules
   *          RelevantRules
   * @param metadata
   *          Metadata (optional, can be null)
   * @return Knowledge resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final RelevantEvents events, final RelevantRules rules, final Metadata metadata) throws ResolutionException {
    boolean ok = false;
    try {
      LOGGER.debug("Evaluating Events and Rules ...");
      if ((knowledge == null) || (events == null) || (rules == null)) {
        final String errmsg = "Cannot evaluate: Required object missing!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
      // TODO
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished evaluating Events and Rules. " + (ok ? "OK." : "ERROR!"));
    }
  }
}
