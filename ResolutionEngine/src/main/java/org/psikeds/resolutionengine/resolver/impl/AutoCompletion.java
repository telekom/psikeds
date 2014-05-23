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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
import org.psikeds.resolutionengine.resolver.ResolutionException;
import org.psikeds.resolutionengine.resolver.Resolver;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;
import org.psikeds.resolutionengine.util.ChoicesHelper;
import org.psikeds.resolutionengine.util.KnowledgeEntityHelper;

/**
 * This Resolver completes automatically all Choices, i.e. it constructs
 * a new Knowledge-Entity for every VariantChoice containing just one Variant
 * and a new FeatureValue for every FeatureChoice containing only one Value
 * for a Feature.
 * 
 * Note: MUST run after both VariantDecissionEvaluator and FeatureDecissionEvaluator!
 * 
 * @author marco@juliano.de
 * 
 */
public class AutoCompletion implements InitializingBean, Resolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutoCompletion.class);

  public static final Transformer DEFAULT_TRANSFORMER = new Vo2PojoTransformer();
  public static final boolean DEFAULT_COMPLETE_ROOT_PURPOSES = false;

  private KnowledgeBase kb;
  private Transformer trans;

  /**
   * Shall Root-Purposes with just one fulfilling Variant be auto-completed?
   * Note: Doing so can have unexpected results for Knowledgebases with more
   * than one Root-Purpose!
   */
  private boolean autoCompleteRootPurposes;

  public AutoCompletion() {
    this(null);
  }

  public AutoCompletion(final KnowledgeBase kb) {
    this(kb, DEFAULT_TRANSFORMER);
  }

  public AutoCompletion(final KnowledgeBase kb, final Transformer trans) {
    this(kb, trans, DEFAULT_COMPLETE_ROOT_PURPOSES);
  }

  public AutoCompletion(final KnowledgeBase kb, final boolean complete) {
    this(kb, DEFAULT_TRANSFORMER, complete);
  }

  public AutoCompletion(final KnowledgeBase kb, final Transformer trans, final boolean complete) {
    setKnowledgeBase(kb);
    setTransformer(trans);
    setAutoCompleteRootPurposes(complete);
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
    this.trans = (trans != null ? trans : DEFAULT_TRANSFORMER);
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
    LOGGER.info("Config: Auto-Completion for Root-Purpose: {}", this.autoCompleteRootPurposes);
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.trans, "No Transformer!");
  }

  // ----------------------------------------------------------------

  /**
   * @param knowledge
   *          current Knowledge
   * @param decission
   *          Decission Interactive decission by Client if not null,
   *          otherwise an automatic Resolution
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
    try {
      LOGGER.debug("Autocompleting all Choices ...");
      ok = autocompleteKnowledge(knowledge, decission, metadata);
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished Autocompletion of all Choices. " + (ok ? "OK." : "ERROR!"));
    }
  }

  // ----------------------------------------------------------------

  private boolean autocompleteKnowledge(final Knowledge knowledge, final Decission decission, final Metadata metadata) throws ResolutionException {
    try {
      LOGGER.trace("--> autocompleteKnowledge()\nKnowledge = {}", knowledge);
      return autocompleteKnowledgeEntities(decission, null, knowledge.getEntities(), knowledge.getChoices(), metadata);
    }
    finally {
      LOGGER.trace("<-- autocompleteKnowledge()\nKnowledge = {}", knowledge);
    }
  }

  private boolean autocompleteKnowledgeEntities(
      final Decission decission,
      final KnowledgeEntity parentEntity,
      final KnowledgeEntities entities,
      final List<? extends Choice> choices,
      final Metadata metadata) throws ResolutionException {

    boolean ok = false;
    final boolean interactive = (decission != null);
    try {
      LOGGER.trace("--> autocompleteKnowledgeEntities(); interactive = {}\nChoices = {}", interactive, choices);
      // Step 1: Autocomplete current Choices
      final Iterator<? extends Choice> iter = (choices == null ? null : choices.iterator());
      while ((iter != null) && iter.hasNext()) {
        final Choice c = iter.next();
        // Is this a Choice for a Variant?
        if (c instanceof VariantChoice) {
          final VariantChoice vc = (VariantChoice) c;
          final List<Variant> vars = vc.getVariants();
          // Does this Choice contain one or none Variant(s)?
          if (vars.size() < 2) {
            LOGGER.trace("Found: {}", vc);
            final Purpose p = vc.getPurpose();
            final String pid = p.getPurposeID();
            if (p.isRoot() && !interactive && !this.autoCompleteRootPurposes) {
              // Auto-complete Root-Purposes only if Enabled or requested by Client-Decission
              LOGGER.debug("Skipping Auto-Completion for Root-Purpose: {}", pid);
              continue;
            }
            LOGGER.info("Auto-Completion for Purpose: {}", pid);
            // exactly one variant
            if (!vars.isEmpty()) {
              // Create a new KnowledgeEntity for the selected Variant
              Variant v = vars.get(0);
              final String vid = v.getVariantID();
              LOGGER.trace("Creating Choices and KE for Variant {}", vid);
              // ensure clean data, therefore lookup variant from knowledge base (again)
              final org.psikeds.resolutionengine.datalayer.vo.Variant variant = this.kb.getVariant(vid);
              v = this.trans.valueObject2Pojo(variant, this.kb.getFeatures(vid));
              final long qty = this.kb.getQuantity(pid, vid);
              // get new choices for this variant
              final VariantChoices newVariantChoices = ChoicesHelper.getNewVariantChoices(this.kb, this.trans, variant);
              final FeatureChoices newFeatureChoices = ChoicesHelper.getNewFeatureChoices(this.kb, this.trans, variant);
              final ConceptChoices newConceptChoices = ChoicesHelper.getNewConceptChoices(this.kb, this.trans, variant);
              // create new knowledge entity
              final KnowledgeEntity ke = new KnowledgeEntity(qty, p, v, newVariantChoices, newFeatureChoices, newConceptChoices);
              KnowledgeEntityHelper.addNewKnowledgeEntity(entities, ke);
              // cleanup
              vars.clear();
              // TODO: possible performance optimization: update relevant events and rules based on new variant/entity
            }
            // remove old VariantChoice
            completionMessage(metadata, vc);
            iter.remove();
          }
        }
        // Is this a Choice for a Value of a Feature?
        else if (c instanceof FeatureChoice) {
          final FeatureChoice fc = (FeatureChoice) c;
          final FeatureValues values = fc.getPossibleValues();
          if (values.size() < 2) {
            LOGGER.trace("Found: {}", fc);
            final String fid = fc.getFeatureID();
            LOGGER.info("Auto-Completion for Feature {}", fid);
            //exactly one feature value
            if (!values.isEmpty()) {
              // Update Parent-KnowledgeEntity with new Feature-Value
              final FeatureValue fv = values.get(0);
              parentEntity.addFeature(fv);
              LOGGER.trace("Adding new FeatureValue: {}", fv);
              // cleanup
              values.clear();
            }
            // remove old FeatureChoice
            completionMessage(metadata, fc);
            iter.remove();
          }
        }
        else if (c instanceof ConceptChoice) {
          // TODO implement!
          throw new IllegalArgumentException("ConceptChoices not implemented yet!");
        }
        else {
          throw new IllegalArgumentException("Unsupported kind of Choice: " + String.valueOf(c));
        }
      }
      // Step 2: Recursively check Children and auto-complete their Choices, too
      if ((entities != null) && !entities.isEmpty()) {
        for (final KnowledgeEntity ke : entities) {
          autocompleteKnowledgeEntities(decission, ke, ke.getChildren(), ke.getPossibleVariants(), metadata);
          autocompleteKnowledgeEntities(decission, ke, ke.getChildren(), ke.getPossibleFeatures(), metadata);
        }
      }
      // done
      ok = true;
      return ok;
    }
    catch (final Exception ex) {
      ok = false;
      final String errmsg = "Cannot auto-complete: " + ex.getMessage();
      LOGGER.warn(errmsg);
      throw new ResolutionException(errmsg, ex);
    }
    finally {
      LOGGER.trace("<-- autocompleteKnowledgeEntities() = " + (ok ? "OK." : "ERROR!"));
    }
  }

  // ----------------------------------------------------------------

  private void completionMessage(final Metadata metadata, final VariantChoice vc) {
    if (vc != null) {
      final String msg = String.format("Completed VariantChoice: %s", vc);
      LOGGER.debug(msg);
      if (metadata != null) {
        final String key = String.format("AutoCompletion_VC_%s_%s", vc.getParentVariantID(), vc.getPurpose().getPurposeID());
        metadata.addInfo(key, msg);
      }
    }
  }

  private void completionMessage(final Metadata metadata, final FeatureChoice fc) {
    if (fc != null) {
      final String msg = String.format("Completed FeatureChoice: %s", fc);
      LOGGER.debug(msg);
      if (metadata != null) {
        final String key = String.format("AutoCompletion_FC_%s_%s", fc.getParentVariantID(), fc.getFeatureID());
        metadata.addInfo(key, msg);
      }
    }
  }
}
