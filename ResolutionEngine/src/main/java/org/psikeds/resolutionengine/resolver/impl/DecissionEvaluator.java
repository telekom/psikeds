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
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.resolver.RelevantEvents;
import org.psikeds.resolutionengine.resolver.RelevantRules;
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

  private boolean rootPurposeOptional;

  public DecissionEvaluator() {
    this(true);
  }

  public DecissionEvaluator(final boolean rootPurposeOptional) {
    this.rootPurposeOptional = rootPurposeOptional;
  }

  public boolean isRootPurposeOptional() {
    return this.rootPurposeOptional;
  }

  public void setRootPurposeOptional(final boolean rootPurposeOptional) {
    this.rootPurposeOptional = rootPurposeOptional;
  }

  //----------------------------------------------------------------

  /**
   * @param knowledge
   *          current old Knowledge
   * @param decission
   *          Decission Interactive decission by Client if not null, otherwise an automatic
   *          Resolution
   * @param events
   *          RelevantEvents (ignored!)
   * @param rules
   *          RelevantRules (ignored!)
   * @param metadata
   *          Metadata (optional, can be null)
   * @return Knowledge resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final RelevantEvents events, final RelevantRules rules, final Metadata metadata) throws ResolutionException {
    boolean ok = false;
    boolean found = false;
    try {
      LOGGER.debug("Evaluating made Decission ...");
      if (decission != null) {
        if (StringUtils.isEmpty(decission.getPurposeID()) || StringUtils.isEmpty(decission.getVariantID())) {
          final String errmsg = String.format("Cannot evaluate, illegal Decission: %s", decission);
          LOGGER.warn(errmsg);
          throw new ResolutionException(errmsg);
        }
        found = updateKnowledge(knowledge, decission, metadata);
      }
      else {
        LOGGER.debug("Skipping Evaluation, Decission is null.");
      }
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished evaluating made Decission. " + (found ? "" : "NOT") + " found, " + (ok ? "OK." : "ERROR!"));
    }
  }

  //----------------------------------------------------------------

  private boolean updateKnowledge(final Knowledge knowledge, final Decission decission, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateKnowledge: Knowledge = {}\nDecission = {}", knowledge, decission);
      if (knowledge == null) {
        final String errmsg = "Cannot evaluate Decission: Knowledge is null!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
      final List<Choice> choices = knowledge.getChoices();
      found = updateChoices(choices, decission, metadata);
      for (final KnowledgeEntity ke : knowledge.getEntities()) {
        found = found | updateEntity(ke, decission, metadata);
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateKnowledge: Found = {}\nKnowledge = {}\nDecission = {}", found, knowledge, decission);
    }
  }

  private boolean updateEntity(final KnowledgeEntity ke, final Decission decission, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateEntity: KnowledgeEntity = {}", ke);
      found = updateChoices(ke.getChoices(), decission, metadata);
      for (final KnowledgeEntity sibling : ke.getSiblings()) {
        found = found | updateEntity(sibling, decission, metadata);
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateEntity: Found = {}\nKnowledgeEntity = {}", found, ke);
    }
  }

  private boolean updateChoices(final List<Choice> choices, final Decission decission, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    boolean concernsRootPurpose = false;
    try {
      LOGGER.trace("--> updateChoices: Choices = {}", choices);
      for (final Choice c : choices) {
        final Variant v = c.matches(decission);
        if (v != null) {
          c.setVariant(v);
          final Purpose p = c.getPurpose();
          if ((p != null) && p.isRoot()) {
            concernsRootPurpose = true;
          }
          found = true;
          decissionMessage(metadata, decission, c);
        }
      }
      if (concernsRootPurpose && this.rootPurposeOptional) {
        // Client's decission concerned a Root-Purpose, which are optional,
        // i.e. we must remove all other Root-Purposes from List of Choices.
        for (final Choice c : choices) {
          final Purpose p = c.getPurpose();
          if ((p != null) && p.isRoot()) {
            final Variant v = c.matches(decission);
            if (v == null) {
              // Found another Choice for a Root-Purpose, but it's not matching
              // our decission. ==> Remove all Variants, Auto-Complete-Resolver
              // will then clean it up.
              c.getVariants().clear();
              LOGGER.debug("Removed all Variants for Purpose {}", p);
            }
          }
        }
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateChoices: Found = {}\nConcernsRootPurpose = {}\nChoices = {}", found, concernsRootPurpose, choices);
    }
  }

  private void decissionMessage(final Metadata metadata, final Decission decission, final Choice c) {
    final String msg = String.format("Found Choice matching Decission.\nDecission = %s\nChoice = %s", decission, c);
    LOGGER.debug(msg);
    if (metadata != null) {
      final String key = String.format("Decission%d", System.currentTimeMillis());
      metadata.saveInfo(key, msg);
    }
  }
}
