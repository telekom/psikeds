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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;

/**
 * Based on the made Decission this Resolver will reduce the corresponding
 * Choice to just one Variant so that the AutoCompletion-Resolver afterwards
 * constructs new Knowledge-Entities for it.
 * If no Decission is supplied, nothing happens. Metadata will be ignored.
 *
 * @author marco@juliano.de
 */
public class DecissionEvaluator implements Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(DecissionEvaluator.class);

  /**
   * @param knowledge            current old Knowledge
   * @param decission            Decission (can be null)
   * @param metadata             Metadata (ignored)
   * @return Knowledge           resulting new Knowledge
   * @throws ResolutionException if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final Metadata metadata) throws ResolutionException {
    try {
      LOGGER.trace("--> resolve(); Knowledge = {} \n Decission = {}", knowledge, decission);
      if (knowledge == null) {
        throw new ResolutionException("Cannot evaluate Decission: Knowledge is null!");
      }
      if (decission != null) {
        if (StringUtils.isEmpty(decission.getPurposeID()) || StringUtils.isEmpty(decission.getVariantID())) {
          throw new ResolutionException("Cannot evaluate, illegal Decission: " + decission);
        }
        final List<Choice> choices = knowledge.getChoices();
        boolean found = updateChoices(choices, decission);
        for (final KnowledgeEntity ke : knowledge.getEntities()) {
          found = found | updateChoices(ke, decission);
        }
      }
      else {
        LOGGER.debug("Skipping Evaluation, Decission is null.");
      }
      return knowledge;
    }
    finally {
      LOGGER.trace("<-- resolve(); new Knowledge = {}", knowledge);
    }
  }

  private boolean updateChoices(final KnowledgeEntity ke, final Decission decission) throws ResolutionException {
    boolean found = updateChoices(ke.getChoices(), decission);
    for (final KnowledgeEntity sibling : ke.getSiblings()) {
      found = found | updateChoices(sibling, decission);
    }
    return found;
  }

  private boolean updateChoices(final List<Choice> choices, final Decission decission) throws ResolutionException {
    boolean found = false;
    for (final Choice c : choices) {
      final Variant v = c.matches(decission);
      if (v != null) {
        LOGGER.debug("Found Choice matching Decission.\nd = {}\nc = {}", decission, c);
        c.setVariant(v);
        found = true;
      }
    }
    return found;
  }
}
