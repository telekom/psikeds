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
package org.psikeds.queryagent.presenter.jsf.model;

import org.psikeds.common.cache.LimitedHashMap;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concepts;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValues;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.POJO;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;

/**
 * The KnowledgeCache is holding all cached Knowledge-POJOs, i.e.
 * Variants, Purposes, Features, Concepts, etc.
 * 
 * @author marco@juliano.de
 * 
 */
public class KnowledgeCache extends LimitedHashMap<String, POJO> {

  private static final long serialVersionUID = 1L;

  public static final int DEFAULT_MAX_POJOS_PER_SESSION = LimitedHashMap.DEFAULT_MAX_MAP_SIZE;

  public KnowledgeCache() {
    this(DEFAULT_MAX_POJOS_PER_SESSION);
  }

  public KnowledgeCache(final int maxPojosPerSession) {
    super(maxPojosPerSession);
  }

  public KnowledgeCache(final int maxPojosPerSession, final int initialCapacity, final float loadFactor, final boolean accessOrder) {
    super(maxPojosPerSession, initialCapacity, loadFactor, accessOrder);
  }

  public int getMaxPojosPerSession() {
    return getMaxSize();
  }

  public void setMaxPojosPerSession(final int maxPojosPerSession) {
    setMaxSize(maxPojosPerSession);
  }

  // ----------------------------------------------------------------

  public POJO put(final Concept c) {
    return (c == null ? null : put(c.getConceptID(), c));
  }

  public void put(final Concepts lst) {
    if ((lst != null) && !lst.isEmpty()) {
      for (final Concept c : lst) {
        put(c);
      }
    }
  }

  public POJO put(final Feature f) {
    return (f == null ? null : put(f.getFeatureID(), f));
  }

  public void put(final Features lst) {
    if ((lst != null) && !lst.isEmpty()) {
      for (final Feature f : lst) {
        put(f);
      }
    }
  }

  public POJO put(final FeatureValue fv) {
    return (fv == null ? null : put(fv.getFeatureValueID(), fv));
  }

  public void put(final FeatureValues lst) {
    if ((lst != null) && !lst.isEmpty()) {
      for (final FeatureValue fv : lst) {
        put(fv);
      }
    }
  }

  public POJO put(final Variant v) {
    return (v == null ? null : put(v.getVariantID(), v));
  }

  public void put(final Variants lst) {
    if ((lst != null) && !lst.isEmpty()) {
      for (final Variant v : lst) {
        put(v);
      }
    }
  }

  public POJO put(final Purpose p) {
    return (p == null ? null : put(p.getPurposeID(), p));
  }

  // ----------------------------------------------------------------

  public Concept getConcept(final String id) {
    return (Concept) get(id);
  }

  public Feature getFeature(final String id) {
    return (Feature) get(id);
  }

  public FeatureValue getFeatureValue(final String id) {
    return (FeatureValue) get(id);
  }

  public Variant getVariant(final String id) {
    return (Variant) get(id);
  }

  public Purpose getPurpose(final String id) {
    return (Purpose) get(id);
  }
}
