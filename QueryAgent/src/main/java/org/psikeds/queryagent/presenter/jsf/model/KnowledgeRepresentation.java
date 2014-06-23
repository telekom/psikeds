/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.jsf.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.pojos.Choices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concepts;
import org.psikeds.queryagent.interfaces.presenter.pojos.Errors;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntities;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warnings;

/**
 * The Frontend Representation/Model of our current Knowledge;
 * usually cached within Session-Scope.
 * 
 * Additionally the Model holds a Reference to every Object within
 * the Knowledge allowing the GUI to perform information lookups.
 * 
 * @author marco@juliano.de
 */
public class KnowledgeRepresentation implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final KnowledgeCache DEFAULT_CACHE = new KnowledgeCache();

  KnowledgeCache cache;
  ResolutionResponse lastResponse;
  ResolutionResponse prediction;

  public KnowledgeRepresentation() {
    this(null, null, null);
  }

  public KnowledgeRepresentation(final KnowledgeCache cache, final ResolutionResponse lastResponse, final ResolutionResponse prediction) {
    setKnowledgeCache(cache);
    setLastResponse(lastResponse);
    setPrediction(prediction);
  }

  public KnowledgeCache getKnowledgeCache() {
    return this.cache;
  }

  public void setKnowledgeCache(final KnowledgeCache cache) {
    clearCache();
    this.cache = (cache == null ? DEFAULT_CACHE : cache);
  }

  public ResolutionResponse getPrediction() {
    return this.prediction;
  }

  public void setPrediction(final ResolutionResponse prediction) {
    this.prediction = prediction;
    updateCache(this.prediction);
  }

  public ResolutionResponse getLastResponse() {
    return this.lastResponse;
  }

  public void setLastResponse(final ResolutionResponse lastResponse) {
    this.lastResponse = lastResponse;
    updateCache(this.lastResponse);
  }

  // ----------------------------------------------------------------

  /**
   * @return Metadata
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.BaseResolutionContext#getMetadata()
   */
  public Metadata getMetadata() {
    return this.lastResponse.getMetadata();
  }

  /**
   * @return Knowledge
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.BaseResolutionContext#getKnowledge()
   */
  public Knowledge getKnowledge() {
    return (this.lastResponse == null ? null : this.lastResponse.getKnowledge());
  }

  /**
   * @return Warnings
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#getWarnings()
   */
  public Warnings getWarnings() {
    return this.lastResponse.getWarnings();
  }

  /**
   * @return Errors
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#getErrors()
   */
  public Errors getErrors() {
    return this.lastResponse.getErrors();
  }

  /**
   * @return Choices
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#getChoices()
   */
  public Choices getChoices() {
    return this.lastResponse.getChoices();
  }

  public Knowledge getPredictedKnowledge() {
    return (this.prediction == null ? null : this.prediction.getKnowledge());
  }

  // ----------------------------------------------------------------

  /**
   * @return true if resolved, false else
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#isResolved()
   */
  public boolean isResolved() {
    return (this.lastResponse != null) && this.lastResponse.isResolved();
  }

  /**
   * @return true if there is no response or there are any errors, false else
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#hasErrors()
   */
  public boolean hasErrors() {
    return (this.lastResponse == null) || this.lastResponse.hasErrors();
  }

  /**
   * @return true if there are any warnings, false else
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#hasWarnings()
   */
  public boolean hasWarnings() {
    return (this.lastResponse != null) && this.lastResponse.hasWarnings();
  }

  /**
   * @return SessionID
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#getSessionID()
   */
  public String getSessionID() {
    return (this.lastResponse == null ? null : this.lastResponse.getSessionID());
  }

  /**
   * @return true if there is no SessionID or no Knowledge, false else
   * @see org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse#isNotInitialized()
   */
  public boolean isNotInitialized() {
    return (StringUtils.isEmpty(getSessionID()) || (getKnowledge() == null));
  }

  public boolean hasPrediction() {
    return ((this.prediction != null) && (getPredictedKnowledge() != null));
  }

  // ----------------------------------------------------------------

  /**
   * @param id
   * @return Concept
   * @see org.psikeds.queryagent.presenter.jsf.model.KnowledgeCache#getConcept(java.lang.String)
   */
  public Concept getConcept(final String id) {
    return this.cache.getConcept(id);
  }

  /**
   * @param id
   * @return Feature
   * @see org.psikeds.queryagent.presenter.jsf.model.KnowledgeCache#getFeature(java.lang.String)
   */
  public Feature getFeature(final String id) {
    return this.cache.getFeature(id);
  }

  /**
   * @param id
   * @return FeatureValue
   * @see org.psikeds.queryagent.presenter.jsf.model.KnowledgeCache#getFeatureValue(java.lang.String)
   */
  public FeatureValue getFeatureValue(final String id) {
    return this.cache.getFeatureValue(id);
  }

  /**
   * @param id
   * @return Variant
   * @see org.psikeds.queryagent.presenter.jsf.model.KnowledgeCache#getVariant(java.lang.String)
   */
  public Variant getVariant(final String id) {
    return this.cache.getVariant(id);
  }

  /**
   * @param id
   * @return Purpose
   * @see org.psikeds.queryagent.presenter.jsf.model.KnowledgeCache#getPurpose(java.lang.String)
   */
  public Purpose getPurpose(final String id) {
    return this.cache.getPurpose(id);
  }

  // ----------------------------------------------------------------

  private void clearCache() {
    if (this.cache != null) {
      this.cache.clear();
    }
  }

  private void updateCache(final ResolutionResponse resp) {
    if (resp != null) {
      final Knowledge k = resp.getKnowledge();
      final KnowledgeEntities entities = (k == null ? null : k.getEntities());
      updateCache(entities);
    }
  }

  private void updateCache(final KnowledgeEntities entities) {
    // keep references to all objects in the cache for later lookups by the GUI
    if ((this.cache != null) && (entities != null) && !entities.isEmpty()) {
      for (final KnowledgeEntity ke : entities) {
        if (ke != null) {
          this.cache.put(ke.getFeatures());
          this.cache.put(ke.getPurpose());
          final Variant v = ke.getVariant();
          this.cache.put(v);
          this.cache.put(v.getFeatures());
          final Concepts cons = v.getConcepts();
          for (final Concept c : cons) {
            if (c != null) {
              this.cache.put(c);
              this.cache.put(c.getValues());
            }
          }
          updateCache(ke.getChildren());
        }
      }
    }
  }
}
