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

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
import org.psikeds.resolutionengine.interfaces.pojos.VariantDecission;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;

/**
 * Based on the made Variant-Decission this Resolver will reduce the corresponding
 * Variant-Choice to just one single Variant so that afterwards new Knowledge-Entities
 * will be created by the AutoCompletion-Resolver.
 * If no Decission is supplied, nothing happens. Metadata is optional.
 * 
 * Note: Must run before AutoCompletion-Resolver!
 * 
 * @author marco@juliano.de
 */
public class VariantDecissionEvaluator implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(VariantDecissionEvaluator.class);

  public static final boolean DEFAULT_ROOT_PURPOSE_OPTIONAL = true;

  /**
   * This Flag controls what happens when there are more than one Root-Purpose
   * in a KnowledgeBase:
   * If Root-Purposes are optional (Flag = true) than the Client can choose exactly
   * one of them and the rest is removed from the Knowledge.
   * If Root-Purposes are all mandatory (Flag = false) than a Decission for every
   * single one of thrm must be supplied, resulting in separate Knowledge-Graphs
   * for each Root-Purpose.
   */
  private boolean rootPurposeOptional;

  public VariantDecissionEvaluator() {
    this(DEFAULT_ROOT_PURPOSE_OPTIONAL);
  }

  public VariantDecissionEvaluator(final boolean rootPurposeOptional) {
    this.rootPurposeOptional = rootPurposeOptional;
  }

  public boolean isRootPurposeOptional() {
    return this.rootPurposeOptional;
  }

  public void setRootPurposeOptional(final boolean rootPurposeOptional) {
    this.rootPurposeOptional = rootPurposeOptional;
  }

  // ----------------------------------------------------------------

  /**
   * Check that Decission-Evaluator was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    LOGGER.info("Config: Root-Purposes are optional: {}", this.rootPurposeOptional);
    // just info, nothing to validate
  }

  // ----------------------------------------------------------------

  /**
   * @param knowledge
   *          current Knowledge
   * @param decission
   *          some Decission, ignored if not a VariantDecission
   * @param raeh
   *          all Rules and Events currently relevant (ignored!)
   * @param metadata
   *          Metadata (optional, can be null)
   * @return Knowledge
   *         resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final RulesAndEventsHandler raeh, final Metadata metadata) throws ResolutionException {
    boolean ok = false;
    boolean found = false;
    try {
      LOGGER.debug("Evaluating made Decission ...");
      if (decission instanceof VariantDecission) {
        final VariantDecission vd = (VariantDecission) decission;
        found = updateKnowledge(knowledge, vd, metadata);
      }
      else {
        LOGGER.debug("Skipping Evaluation, no Variant-Decission.");
      }
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished evaluating made Decission: " + (found ? "" : "NOT") + " found, " + (ok ? "OK." : "ERROR!"));
    }
  }

  //----------------------------------------------------------------------

  private boolean updateKnowledge(final Knowledge knowledge, final VariantDecission vd, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateKnowledge(); Variant-Decission = {}\nKnowledge = {}", vd, knowledge);
      found = updateVariantChoices(knowledge.getChoices(), vd, metadata);
      for (final KnowledgeEntity ke : knowledge.getEntities()) {
        found = found | updateKnowledgeEntity(ke, vd, metadata);
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateKnowledge(); Found = {}; Knowledge = {}", found, knowledge);
    }
  }

  private boolean updateKnowledgeEntity(final KnowledgeEntity ke, final VariantDecission vd, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    try {
      LOGGER.trace("--> updateKnowledgeEntity(); KnowledgeEntity = {}", ke);
      found = updateVariantChoices(ke.getPossibleVariants(), vd, metadata);
      for (final KnowledgeEntity child : ke.getChildren()) {
        found = found | updateKnowledgeEntity(child, vd, metadata);
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateKnowledgeEntity(); Found = {}; KnowledgeEntity = {}", found, ke);
    }
  }

  private boolean updateVariantChoices(final VariantChoices choices, final VariantDecission vd, final Metadata metadata) throws ResolutionException {
    boolean found = false;
    boolean concernsRootPurpose = false;
    try {
      LOGGER.trace("--> updateVariantChoices(); Variant-Decission = {}\nChoices = {}", vd, choices);
      for (final VariantChoice vc : choices) {
        final Variant v = (vc == null ? null : vc.matches(vd));
        if (v != null) {
          // Found Variant matching our Decission!
          vc.setVariant(v);
          final Purpose p = vc.getPurpose();
          if ((p != null) && p.isRoot()) {
            concernsRootPurpose = true;
          }
          found = true;
          decissionMessage(metadata, vd, vc);
        }
      }
      if (concernsRootPurpose && this.rootPurposeOptional) {
        // Client's decission concerned a Root-Purpose, which are optional,
        // i.e. we must remove all other Root-Purposes, not matching our Decission
        // from the List of remaining Choices.
        for (final Choice c : choices) {
          if (c instanceof VariantChoice) {
            final VariantChoice vc = (VariantChoice) c;
            final Purpose p = vc.getPurpose();
            if ((p != null) && p.isRoot()) {
              final Variant v = vc.matches(vd);
              if (v == null) {
                // Found another Choice for a Root-Purpose, but it's not matching
                // our decission. ==> Remove all Variants, Auto-Complete-Resolver
                // will then clean it up.
                vc.getVariants().clear();
                LOGGER.info("Removed all Variants for Root-Purpose {}", p);
              }
            }
          }
        }
      }
      return found;
    }
    finally {
      LOGGER.trace("<-- updateVariantChoices(); Found = {}; ConcernsRootPurpose = {}\nChoices = {}", found, concernsRootPurpose, choices);
    }
  }

  private void decissionMessage(final Metadata metadata, final VariantDecission vd, final VariantChoice vc) {
    final String msg = String.format("Found Variant-Choice matching Decission.\nDecission = %s\nChoice = %s", vd, vc);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(msg);
    }
    else {
      LOGGER.info("Applying Variant-Decission {}", vd);
    }
    if (metadata != null) {
      final String key = String.format("Decission_VD_%s_%s", vd.getPurposeID(), vd.getVariantID());
      metadata.addInfo(key, msg);
    }
  }
}
