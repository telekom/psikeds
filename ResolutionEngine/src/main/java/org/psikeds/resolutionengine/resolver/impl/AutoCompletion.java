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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
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
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * This Resolver completes automatically all Choices, i.e. it constructs
 * a new Knowledge-Entity for every Choice containing just one Variant for
 * a Purpose.
 * 
 * @author marco@juliano.de
 */
public class AutoCompletion implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutoCompletion.class);

  private KnowledgeBase kb;
  private Transformer trans;
  private boolean autoCompleteRootPurposes;

  public AutoCompletion() {
    this(null, null);
  }

  public AutoCompletion(final KnowledgeBase kb, final Transformer trans) {
    this.kb = kb;
    this.trans = trans;
    this.autoCompleteRootPurposes = false;
  }

  public boolean isAutoCompleteRootPurposes() {
    return this.autoCompleteRootPurposes;
  }

  public void setAutoCompleteRootPurposes(final boolean autoCompleteRootPurposes) {
    this.autoCompleteRootPurposes = autoCompleteRootPurposes;
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

  // ----------------------------------------------------------------

  /**
   * Check that AutoCompletion-Resolver was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.trans, "No Transformer!");
  }

  // ----------------------------------------------------------------

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
    try {
      LOGGER.debug("Autocompleting all Choices ...");
      autocompleteKnowledge(knowledge, decission, metadata);
      ok = true;
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished Autocompletion of all Choices. " + (ok ? "OK." : "ERROR!"));
    }
  }

  private void autocompleteKnowledge(final Knowledge knowledge, final Decission decission, final Metadata metadata) throws ResolutionException {
    try {
      LOGGER.trace("--> autocompleteKnowledge: Knowledge = {}", knowledge);
      if (knowledge == null) {
        final String errmsg = "Cannot auto-complete: Knowledge is null!";
        LOGGER.warn(errmsg);
        throw new ResolutionException(errmsg);
      }
      autocompleteEntities(decission, knowledge.getEntities(), knowledge.getChoices(), metadata);
    }
    finally {
      LOGGER.trace("<-- autocompleteKnowledge: Knowledge = {}", knowledge);
    }
  }

  private void autocompleteEntities(final Decission decission, final List<KnowledgeEntity> entities, final List<Choice> choices, final Metadata metadata) throws ResolutionException {
    boolean ok = false;
    final boolean interactive = (decission != null);
    try {
      LOGGER.trace("--> autocompleteEntities: interactive = {}\nEntities = {}\nChoices = {}", interactive, entities, choices);
      // Step 1: Autocomplete current Choices
      final Iterator<Choice> iter = choices.iterator();
      while (iter.hasNext()) {
        final Choice c = iter.next();
        final List<Variant> vars = c.getVariants();
        // Does this Choice contain one or none Variant(s)?
        if (vars.size() < 2) {
          final Purpose p = c.getPurpose();
          if (p.isRoot() && !interactive && !this.autoCompleteRootPurposes) {
            // Auto-complete Root-Purposes only if Enabled or requested by Client-Decission
            LOGGER.debug("Skipping Auto-Completion for Root-Purpose: {}", p);
            continue;
          }
          LOGGER.debug("Auto-completing Choice: {}", c);
          if (!vars.isEmpty()) { // exactly one
            // Create a new KnowledgeEntity containing Purpose, Variant and new Choices
            final Variant v = vars.get(0);
            final List<Choice> newchoices = getNewChoices(v);
            final KnowledgeEntity ke = new KnowledgeEntity(p, v, newchoices);
            addNewEntity(entities, ke);
            // cleanup
            vars.clear();
          }
          // remove old Choice, also cleans up buggy choices without any variant
          completionMessage(metadata, c);
          iter.remove();
        }
      }
      // Step 2: Recursively check Siblings and autocomplete their Choices
      for (final KnowledgeEntity ke : entities) {
        autocompleteEntities(decission, ke.getSiblings(), ke.getChoices(), metadata);
      }
      // done
      ok = true;
    }
    catch (final Exception ex) {
      ok = false;
      final String errmsg = "Cannot auto-complete: " + ex.getMessage();
      LOGGER.warn(errmsg);
      throw new ResolutionException(errmsg, ex);
    }
    finally {
      LOGGER.trace("<-- autocompleteEntities: " + (ok ? "OK." : "ERROR!") + "\ninteractive = {}\nEntities = {}\nChoices = {}", interactive, entities, choices);
    }
  }

  private void addNewEntity(final List<KnowledgeEntity> entities, final KnowledgeEntity ke) {
    for (final KnowledgeEntity e : entities) {
      final String pid1 = e.getPurpose().getId();
      final String pid2 = ke.getPurpose().getId();
      final String vid1 = e.getVariant().getId();
      final String vid2 = ke.getVariant().getId();
      if (pid1.equals(pid2) && vid1.equals(vid2)) {
        LOGGER.trace("Entity-List already contains KnowledgeEntity: {}", ke);
        return;
      }
    }
    LOGGER.trace("Adding new KnowledgeEntity: {}", ke);
    entities.add(ke);
  }

  private List<Choice> getNewChoices(final Variant v) {
    final List<Choice> choices = new ArrayList<Choice>();
    try {
      LOGGER.trace("--> getNewChoices: Variant = {}", v);
      // ensure clean data, therefore lookup parent-variant from knowledge base (again)
      final String variantId = v.getId();
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent = this.kb.getVariant(variantId);
      // get all purposes constituting parent-variant
      final org.psikeds.resolutionengine.datalayer.vo.Purposes newpurps = this.kb.getConstitutingPurposes(parent);
      // get for every purpose ...
      for (final org.psikeds.resolutionengine.datalayer.vo.Purpose p : newpurps.getPurpose()) {
        // ... all fulfilling variants ...
        final org.psikeds.resolutionengine.datalayer.vo.Variants newvars = this.kb.getFulfillingVariants(p);
        // ... and transform parent-variant, purpose and new variant
        //     into a Choice-POJO for the Client
        final Choice c = this.trans.valueObject2Pojo(parent, p, newvars);
        LOGGER.debug("Adding new Choice: {}", c);
        choices.add(c);
      }
      // return list of all new choices
      return choices;
    }
    finally {
      LOGGER.trace("<-- getNewChoices: Variant = {}\nChoices = {}", v, choices);
    }
  }

  private void completionMessage(final Metadata metadata, final Choice c) {
    final String msg = String.format("Completed: %s", c);
    LOGGER.debug(msg);
    if (metadata != null) {
      final String key = String.format("AutoCompletion%d", System.currentTimeMillis());
      metadata.saveInfo(key, msg);
    }
  }
}
