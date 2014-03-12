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
package org.psikeds.resolutionengine.datalayer.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper for all Knowledge-Base-Data. Used for testing (De-)Serialization
 * of Value-Objects.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "KnowledgeData")
public class KnowledgeData extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private MetaData metadata;
  private Features features;
  private Purposes purposes;
  private Variants variants;
  private Alternatives alternatives;
  private Constituents constituents;
  private Events events;
  private Rules rules;
  private Relations relations;

  public KnowledgeData() {
    this(null, null, null, null, null, null, null, null, null);
  }

  public KnowledgeData(final MetaData metadata, final Features features, final Purposes purposes, final Variants variants, final Alternatives alternatives, final Constituents constituents,
      final Events events, final Rules rules, final Relations relations) {
    super();
    this.metadata = metadata;
    this.features = features;
    this.purposes = purposes;
    this.variants = variants;
    this.alternatives = alternatives;
    this.constituents = constituents;
    this.events = events;
    this.rules = rules;
    this.relations = relations;
  }

  public MetaData getMetadata() {
    return this.metadata;
  }

  public void setMetadata(final MetaData metadata) {
    this.metadata = metadata;
  }

  public Features getFeatures() {
    return this.features;
  }

  public void setFeatures(final Features features) {
    this.features = features;
  }

  public Purposes getPurposes() {
    return this.purposes;
  }

  public void setPurposes(final Purposes purposes) {
    this.purposes = purposes;
  }

  public Variants getVariants() {
    return this.variants;
  }

  public void setVariants(final Variants variants) {
    this.variants = variants;
  }

  public Alternatives getAlternatives() {
    return this.alternatives;
  }

  public void setAlternatives(final Alternatives alternatives) {
    this.alternatives = alternatives;
  }

  public Constituents getConstituents() {
    return this.constituents;
  }

  public void setConstituents(final Constituents constituents) {
    this.constituents = constituents;
  }

  public Events getEvents() {
    return this.events;
  }

  public void setEvents(final Events events) {
    this.events = events;
  }

  public Rules getRules() {
    return this.rules;
  }

  public void setRules(final Rules rules) {
    this.rules = rules;
  }

  public Relations getRelations() {
    return this.relations;
  }

  public void setRelations(final Relations relations) {
    this.relations = relations;
  }
}
