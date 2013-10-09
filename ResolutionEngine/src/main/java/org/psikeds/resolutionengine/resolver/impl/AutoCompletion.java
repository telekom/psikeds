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

  public AutoCompletion() {
    this(null, null);
  }

  public AutoCompletion(final KnowledgeBase kb, final Transformer trans) {
    this.kb = kb;
    this.trans = trans;
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
   * @param knowledge            current old Knowledge
   * @param decission            Decission (ignored!)
   * @param metadata             Metadata (ignored!)
   * @return Knowledge           resulting new Knowledge
   * @throws ResolutionException if any error occurs
   */
  @Override
  public Knowledge resolve(final Knowledge knowledge, final Decission decission, final Metadata metadata) throws ResolutionException {
    try {
      LOGGER.trace("--> resolve(); Knowledge = {} \n Decission = {}", knowledge, decission);
      if (knowledge == null) {
        throw new ResolutionException("Cannot auto-complete: Knowledge is null!");
      }
      autocomplete(knowledge.getEntities(), knowledge.getChoices(), metadata);
      return knowledge;
    }
    finally {
      LOGGER.trace("<-- resolve(); new Knowledge = {}", knowledge);
    }
  }

  private void autocomplete(final List<KnowledgeEntity> entities, final List<Choice> choices, final Metadata metadata) throws ResolutionException {
    try {
      // Step 1: Autocomplete current Choices
      final Iterator<Choice> iter = choices.iterator();
      while (iter.hasNext()) {
        final Choice c = iter.next();
        final List<Variant> vars = c.getVariants();
        // Does this Choice contain just one Variant?
        if (vars.size() == 1) {
          LOGGER.debug("Found Choice with just one Variant: " + c);
          // Create a new KnowledgeEntity containing Purpose, Variant and new Choices
          final Purpose p = c.getPurpose();
          final Variant v = vars.get(0);
          final List<Choice> newchoices = getNewChoices(v);
          final KnowledgeEntity ke = new KnowledgeEntity(p, v, newchoices);
          LOGGER.debug("Auto-completed Choice to new Entity: " + ke);
          // Add new KnowledgeEntity to List
          entities.add(ke);
          // Remove old Choice from List
          iter.remove();
        }
      }
      // Step 2: Recursively check Siblings and autocomplete their Choices
      for (final KnowledgeEntity ke : entities) {
        autocomplete(ke.getSiblings(), ke.getChoices(), metadata);
      }
    }
    catch (final Exception ex) {
      throw new ResolutionException("Cannot auto-complete: " + ex.getMessage(), ex);
    }
  }

  private List<Choice> getNewChoices(final Variant v) {
    final List<Choice> choices = new ArrayList<Choice>();
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
      choices.add(c);
    }
    // return list of all new choices
    return choices;
  }
}
