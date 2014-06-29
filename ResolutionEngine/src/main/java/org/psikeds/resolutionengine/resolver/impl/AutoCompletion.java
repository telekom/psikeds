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
import org.psikeds.resolutionengine.interfaces.pojos.Concept;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice;
import org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices;
import org.psikeds.resolutionengine.interfaces.pojos.Concepts;
import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices;
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
import org.psikeds.resolutionengine.resolver.SessionState;
import org.psikeds.resolutionengine.transformer.Transformer;
import org.psikeds.resolutionengine.transformer.impl.Vo2PojoTransformer;
import org.psikeds.resolutionengine.util.ChoicesHelper;
import org.psikeds.resolutionengine.util.ConceptHelper;
import org.psikeds.resolutionengine.util.FeatureValueHelper;
import org.psikeds.resolutionengine.util.KnowledgeEntityHelper;

/**
 * This Resolver completes automatically all Choices, i.e. it constructs
 * a new Knowledge-Entity for every VariantChoice containing just one Variant,
 * a new FeatureValue for every FeatureChoice containing only one Value for a
 * Feature and applies all Feature-Values to a Knowledge-Entity if a ConceptChoice
 * contains only one Concept.
 * 
 * Note: MUST run after both VariantDecissionEvaluator, FeatureDecissionEvaluator
 * and ConceptDecissionEvaluator!
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
   * @param state
   *          current State of Resolution-Session
   * @param decission
   *          made Decission (can be null)
   * @return Knowledge
   *         resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  @Override
  public Knowledge resolve(final SessionState state, final Decission decission) throws ResolutionException {
    boolean ok = false;
    try {
      LOGGER.debug("Autocompleting all Choices ...");
      final Knowledge knowledge = state.getKnowledge();
      ok = autocompleteKnowledge(knowledge, decission, state);
      return knowledge;
    }
    finally {
      LOGGER.debug("... finished Autocompletion of all Choices. " + (ok ? "OK." : "ERROR!"));
    }
  }

  // ----------------------------------------------------------------

  private boolean autocompleteKnowledge(final Knowledge knowledge, final Decission decission, final SessionState state) throws ResolutionException {
    try {
      LOGGER.trace("--> autocompleteKnowledge()\nKnowledge = {}", knowledge);
      return autocompleteKnowledgeEntities(decission, null, knowledge.getEntities(), knowledge.getChoices(), state);
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
      final SessionState state) throws ResolutionException {

    boolean ok = false;
    final boolean interactive = (decission != null);
    try {
      LOGGER.trace("--> autocompleteKnowledgeEntities(); interactive = {}\nChoices = {}", interactive, choices);
      // Step 1: Autocomplete current/remaining Choices
      final Iterator<? extends Choice> iter = (choices == null ? null : choices.iterator());
      while ((iter != null) && iter.hasNext()) {
        boolean removed = false;
        final Choice c = iter.next();
        // Is this a Choice for a Variant?
        if (c instanceof VariantChoice) {
          final VariantChoice vc = (VariantChoice) c;
          final List<Variant> vars = vc.getVariants();
          // Does this Choice contain one or none Variant(s)?
          if (vars.size() < 2) {
            LOGGER.trace("Found VC: {}", vc);
            final Purpose p = vc.getPurpose();
            final String pid = p.getPurposeID();
            final String parent = vc.getParentVariantID();
            if (p.isRoot() && !interactive && !this.autoCompleteRootPurposes) {
              // Auto-complete Root-Purposes only if Enabled or requested by Client-Decission
              LOGGER.debug("Skipping Auto-Completion for Root-Purpose: {}", pid);
              continue;
            }
            // exactly one variant
            if (!vars.isEmpty()) {
              // Create a new KnowledgeEntity for the selected Variant
              Variant v = vars.get(0);
              final String vid = v.getVariantID();
              LOGGER.trace("Creating Choices and KE for Variant {}", vid);
              // ensure clean data, therefore lookup variant from knowledge base (again)
              final org.psikeds.resolutionengine.datalayer.vo.Variant variant = this.kb.getVariant(vid);
              v = this.trans.valueObject2Pojo(variant, this.kb.getFeatures(vid), this.kb.getAttachedConcepts(vid));
              final long qty = this.kb.getQuantity(pid, vid);
              // get new choices for this variant
              final VariantChoices newVariantChoices = ChoicesHelper.getNewVariantChoices(this.kb, this.trans, variant);
              final FeatureChoices newFeatureChoices = ChoicesHelper.getNewFeatureChoices(this.kb, this.trans, variant);
              final ConceptChoices newConceptChoices = ChoicesHelper.getNewConceptChoices(this.kb, this.trans, variant);
              // create new knowledge entity
              final KnowledgeEntity ke = new KnowledgeEntity(qty, p, v, newVariantChoices, newFeatureChoices, newConceptChoices);
              KnowledgeEntityHelper.addNewKnowledgeEntity(entities, ke);
              completionMessage(pid, parent, state, vc);
              vars.clear();
            }
            removed = true;
          }
        }
        // Is this a Choice for a Value of a Feature?
        else if (c instanceof FeatureChoice) {
          final FeatureChoice fc = (FeatureChoice) c;
          final String fid = fc.getFeatureID();
          final String parent = fc.getParentVariantID();
          final FeatureValues values = fc.getPossibleValues();
          if (values.size() < 2) {
            LOGGER.trace("Found FC: {}", fc);
            if (values.isEmpty()) {
              unfulfillableMessage(fid, parent, state, fc);
              continue;
            }
            else { //exactly one feature value
              FeatureValueHelper.applyFeatureValue(parentEntity, values.get(0));
              completionMessage(fid, parent, state, fc);
              values.clear();
              removed = true;
            }
          }
        }
        // Is this a Choice for a Concept?
        else if (c instanceof ConceptChoice) {
          final ConceptChoice cc = (ConceptChoice) c;
          final String parent = cc.getParentVariantID();
          final Concepts cons = cc.getConcepts();
          if (cons.size() < 2) {
            LOGGER.trace("Found CC: {}", cc);
            if (cons.isEmpty()) {
              unfulfillableMessage(parent, state, cc);
              continue;
            }
            else {
              // exactly one concept
              final Concept con = cons.get(0);
              ConceptHelper.applyConcept(parentEntity, con);
              final String cid = con.getConceptID();
              completionMessage(cid, parent, state, cc);
              cons.clear();
              removed = true;
            }
          }
        }
        else {
          throw new IllegalArgumentException("Unsupported kind of Choice: " + String.valueOf(c));
        }
        if (removed) {
          LOGGER.trace("Removing old/obsolete Choice: {}", c);
          iter.remove();
        }
      }
      // Step 2: Recursively check Children and auto-complete their Choices, too
      if ((entities != null) && !entities.isEmpty()) {
        for (final KnowledgeEntity ke : entities) {
          autocompleteKnowledgeEntities(decission, ke, ke.getChildren(), ke.getPossibleVariants(), state);
          autocompleteKnowledgeEntities(decission, ke, ke.getChildren(), ke.getPossibleFeatures(), state);
          autocompleteKnowledgeEntities(decission, ke, ke.getChildren(), ke.getPossibleConcepts(), state);
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

  private void completionMessage(final String pid, final String parent, final SessionState state, final VariantChoice vc) {
    String msg = null;
    if (LOGGER.isDebugEnabled() && (vc != null)) {
      msg = String.format("Auto-completed VariantChoice for Purpose %s of Parent-Variant %s: %s", pid, parent, vc);
      LOGGER.debug(msg);
    }
    else {
      msg = String.format("Auto-completed VariantChoice for Purpose %s of Parent-Variant %s", pid, parent);
      LOGGER.info(msg);
    }
    final Metadata metadata = (state == null ? null : state.getMetadata());
    if (metadata != null) {
      final String key = String.format("AutoCompletion_VC_%s_%s", parent, pid);
      metadata.addInfo(key, msg);
    }
  }

//  private void unfulfillableMessage(final String pid, final String parent, final SessionState state, final VariantChoice vc) {
//    String msg = null;
//    if (LOGGER.isDebugEnabled() && (vc != null)) {
//      msg = String.format("Purpose %s of Parent-Variant %s is unfulfillable: %s", pid, parent, vc);
//      LOGGER.debug(msg);
//    }
//    else {
//      msg = String.format("Purpose %s of Parent-Variant %s is unfulfillable!", pid, parent);
//      LOGGER.info(msg);
//    }
//    final Metadata metadata = (state == null ? null : state.getMetadata());
//    if (metadata != null) {
//      final String key = String.format("Unfulfillable_VC_%s_%s", parent, pid);
//      metadata.addInfo(key, msg);
//      state.addWarning(msg);
//    }
//  }

  // ----------------------------------------------------------------
  private void completionMessage(final String fid, final String parent, final SessionState state, final FeatureChoice fc) {
    String msg = null;
    if (LOGGER.isDebugEnabled() && (fc != null)) {
      msg = String.format("Auto-completed FeatureChoice for Feature %s of Variant %s: %s", fid, parent, fc);
      LOGGER.debug(msg);
    }
    else {
      msg = String.format("Auto-completed FeatureChoice for Feature %s of Variant %s", fid, parent);
      LOGGER.info(msg);
    }
    final Metadata metadata = (state == null ? null : state.getMetadata());
    if (metadata != null) {
      final String key = String.format("AutoCompletion_FC_%s_%s", parent, fid);
      metadata.addInfo(key, msg);
    }
  }

  private void unfulfillableMessage(final String fid, final String parent, final SessionState state, final FeatureChoice fc) {
    String msg = null;
    if (LOGGER.isDebugEnabled() && (fc != null)) {
      msg = String.format("Feature %s of Variant %s is unfulfillable: %s", fid, parent, fc);
      LOGGER.debug(msg);
    }
    else {
      msg = String.format("Feature %s of Variant %s is unfulfillable!", fid, parent);
      LOGGER.info(msg);
    }
    final Metadata metadata = (state == null ? null : state.getMetadata());
    if (metadata != null) {
      final String key = String.format("Unfulfillable_FC_%s_%s", parent, fid);
      metadata.addInfo(key, msg);
      state.addWarning(msg);
    }
  }

  // ----------------------------------------------------------------

  private void completionMessage(final String cid, final String parent, final SessionState state, final ConceptChoice cc) {
    String msg = null;
    if (LOGGER.isDebugEnabled() && (cc != null)) {
      msg = String.format("Auto-completed Concept %s for Variant %s: %s", cid, parent, cc);
      LOGGER.debug(msg);
    }
    else {
      msg = String.format("Auto-completed Concept %s for Variant %s", cid, parent);
      LOGGER.info(msg);
    }
    final Metadata metadata = (state == null ? null : state.getMetadata());
    if (metadata != null) {
      final String key = String.format("AutoCompletion_CC_%s_%s", parent, cid);
      metadata.addInfo(key, msg);
    }
  }

  private void unfulfillableMessage(final String parent, final SessionState state, final ConceptChoice cc) {
    String msg = null;
    if (LOGGER.isDebugEnabled() && (cc != null)) {
      msg = String.format("Concepts of Variant {} cannot be fulfilled: %s", parent, cc);
      LOGGER.debug(msg);
    }
    else {
      msg = String.format("Concepts of Variant {} cannot be fulfilled!", parent);
      LOGGER.info(msg);
    }
    final Metadata metadata = (state == null ? null : state.getMetadata());
    if (metadata != null) {
      final String key = String.format("Unfulfillable_CC_%s", parent);
      metadata.addInfo(key, msg);
      state.addWarning(msg);
    }
  }
}
