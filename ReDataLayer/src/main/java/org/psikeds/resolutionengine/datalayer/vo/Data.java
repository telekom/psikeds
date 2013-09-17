/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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

/**
 * Wrapper for all Knowledge-Data.
 *
 * @author marco@juliano.de
 *
 */
public class Data extends ValueObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private Features features;
  private Purposes purposes;
  private Variants variants;
  private Alternatives alternatives;
  private Constituents constituents;
  private Events events;
  private Rules rules;

  public Data() {
    this(null, null, null, null, null, null, null);
  }

  public Data(final Features features, final Purposes purposes, final Variants variants, final Alternatives alternatives, final Constituents constituents, final Events events, final Rules rules) {
    super();
    this.features = features;
    this.purposes = purposes;
    this.variants = variants;
    this.alternatives = alternatives;
    this.constituents = constituents;
    this.events = events;
    this.rules = rules;
  }

  public Features getFeatures() {
    return this.features;
  }

  public void setFeatures(final Features value) {
    this.features = value;
  }

  public Purposes getPurposes() {
    return this.purposes;
  }

  public void setPurposes(final Purposes value) {
    this.purposes = value;
  }

  public Variants getVariants() {
    return this.variants;
  }

  public void setVariants(final Variants value) {
    this.variants = value;
  }

  public Alternatives getAlternatives() {
    return this.alternatives;
  }

  public void setAlternatives(final Alternatives value) {
    this.alternatives = value;
  }

  public Constituents getConstituents() {
    return this.constituents;
  }

  public void setConstituents(final Constituents value) {
    this.constituents = value;
  }

  public Events getEvents() {
    return this.events;
  }

  public void setEvents(final Events value) {
    this.events = value;
  }

  public Rules getRules() {
    return this.rules;
  }

  public void setRules(final Rules value) {
    this.rules = value;
  }
}
