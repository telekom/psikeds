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
package org.psikeds.queryagent.transformer.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.queryagent.interfaces.presenter.pojos.Alternatives;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constituents;
import org.psikeds.queryagent.interfaces.presenter.pojos.Constitutes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Event;
import org.psikeds.queryagent.interfaces.presenter.pojos.Events;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.Fulfills;
import org.psikeds.queryagent.interfaces.presenter.pojos.InitResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purposes;
import org.psikeds.queryagent.interfaces.presenter.pojos.Rule;
import org.psikeds.queryagent.interfaces.presenter.pojos.Rules;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.SelectResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;
import org.psikeds.queryagent.transformer.Transformer;

/**
 * Helper for transforming POJOs from the Interface of the
 * Resolution-Engine (RE) into POJOs of the Interface of the
 * Query-Agent (QA) ... and vice versa.
 *
 * @author marco@juliano.de
 */
public class Re2QaTransformer implements Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Re2QaTransformer.class);

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Alternatives)
   */
  @Override
  public Alternatives re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Alternatives re) {
    Alternatives qa = null;
    if (re != null) {
      qa = new Alternatives();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Fulfills> lst = re.getFulfills();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Fulfills f : lst) {
        qa.addFulfills(re2qa(f));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Alternatives)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Alternatives qa2re(final Alternatives qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Alternatives re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Alternatives();
      final List<Fulfills> lst = qa.getFulfills();
      for (final Fulfills f : lst) {
        re.addFulfills(qa2re(f));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Constituents)
   */
  @Override
  public Constituents re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Constituents re) {
    Constituents qa = null;
    if (re != null) {
      qa = new Constituents();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Constitutes> lst = re.getConstitutes();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Constitutes c : lst) {
        qa.addConstitutes(re2qa(c));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Constituents)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Constituents qa2re(final Constituents qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Constituents re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Constituents();
      final List<Constitutes> lst = qa.getConstitutes();
      for (final Constitutes c : lst) {
        re.addConstitutes(qa2re(c));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Constitutes)
   */
  @Override
  public Constitutes re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Constitutes re) {
    return re == null ? null : new Constitutes(re.getDescription(), re.getVariantID(), re.getPurposeID());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Constitutes)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Constitutes qa2re(final Constitutes qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Constitutes(qa.getDescription(), qa.getVariantID(), qa.getPurposeID());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Event)
   */
  @Override
  public Event re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Event re) {
    return re == null ? null : new Event(re.getLabel(), re.getDescription(), re.getId());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Event)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Event qa2re(final Event qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Event(qa.getLabel(), qa.getDescription(), qa.getId());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Events)
   */
  @Override
  public Events re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Events re) {
    Events qa = null;
    if (re != null) {
      qa = new Events();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Event> lst = re.getEvent();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Event e : lst) {
        qa.addEvent(re2qa(e));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Events)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Events qa2re(final Events qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Events re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Events();
      final List<Event> lst = qa.getEvent();
      for (final Event e : lst) {
        re.addEvent(qa2re(e));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Feature)
   */
  @Override
  public Feature re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Feature re) {
    return re == null ? null : new Feature(re.getLabel(), re.getDescription(), re.getId());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Feature)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(final Feature qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Feature(qa.getLabel(), qa.getDescription(), qa.getId());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Features)
   */
  @Override
  public Features re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Features re) {
    Features qa = null;
    if (re != null) {
      qa = new Features();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Feature> lst = re.getFeature();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Feature f : lst) {
        qa.addFeature(re2qa(f));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Features)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Features qa2re(final Features qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Features re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Features();
      final List<Feature> lst = qa.getFeature();
      for (final Feature f : lst) {
        re.addFeature(qa2re(f));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Fulfills)
   */
  @Override
  public Fulfills re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Fulfills re) {
    return re == null ? null : new Fulfills(re.getDescription(), re.getPurposeID(), re.getVariantID());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Fulfills)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Fulfills qa2re(final Fulfills qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Fulfills(qa.getDescription(), qa.getPurposeID(), qa.getVariantID());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Purpose)
   */
  @Override
  public Purpose re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purpose re) {
    return re == null ? null : new Purpose(re.getLabel(), re.getDescription(), re.getId());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Purpose)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(final Purpose qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(qa.getLabel(), qa.getDescription(), qa.getId());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Purposes)
   */
  @Override
  public Purposes re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purposes re) {
    Purposes qa = null;
    if (re != null) {
      qa = new Purposes();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> lst = re.getPurpose();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Purpose p : lst) {
        qa.addPurpose(re2qa(p));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Purposes)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purposes qa2re(final Purposes qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Purposes re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Purposes();
      final List<Purpose> lst = qa.getPurpose();
      for (final Purpose p : lst) {
        re.addPurpose(qa2re(p));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Rule)
   */
  @Override
  public Rule re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Rule re) {
    return re == null ? null : new Rule(re.getLabel(), re.getDescription(), re.getId());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Rule)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Rule qa2re(final Rule qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Rule(qa.getLabel(), qa.getDescription(), qa.getId());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Rules)
   */
  @Override
  public Rules re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Rules re) {
    Rules qa = null;
    if (re != null) {
      qa = new Rules();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Rule> lst = re.getRule();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Rule r : lst) {
        qa.addRule(re2qa(r));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Rules)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Rules qa2re(final Rules qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Rules re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Rules();
      final List<Rule> lst = qa.getRule();
      for (final Rule r : lst) {
        re.addRule(qa2re(r));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Variant)
   */
  @Override
  public Variant re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variant re) {
    return re == null ? null : new Variant(re.getLabel(), re.getDescription(), re.getId());
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Variant)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(final Variant qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Variant(qa.getLabel(), qa.getDescription(), qa.getId());
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.Variants)
   */
  @Override
  public Variants re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variants re) {
    Variants qa = null;
    if (re != null) {
      qa = new Variants();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Variant> lst = re.getVariant();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Variant v : lst) {
        qa.addVariant(re2qa(v));
      }
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.Variants)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variants qa2re(final Variants qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Variants re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Variants();
      final List<Variant> lst = qa.getVariant();
      for (final Variant v : lst) {
        re.addVariant(qa2re(v));
      }
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity)
   */
  @Override
  public KnowledgeEntity re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re) {
    KnowledgeEntity qa = null;
    if (re != null) {
      final Features features = re2qa(re.getFeatures());
      final Purposes purposes = re2qa(re.getPurposes());
      final Variants variants = re2qa(re.getVariants());
      final Alternatives alternatives = re2qa(re.getAlternatives());
      final Constituents constituents = re2qa(re.getConstituents());
      final Events events = re2qa(re.getEvents());
      final Rules rules = re2qa(re.getRules());
      final boolean fullyResolved = re.isFullyResolved();
      qa = new KnowledgeEntity(features, purposes, variants, alternatives, constituents, events, rules, fullyResolved);
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(final KnowledgeEntity qa) {
    org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re = null;
    if (qa != null) {
      final org.psikeds.resolutionengine.interfaces.pojos.Features features = qa2re(qa.getFeatures());
      final org.psikeds.resolutionengine.interfaces.pojos.Purposes purposes = qa2re(qa.getPurposes());
      final org.psikeds.resolutionengine.interfaces.pojos.Variants variants = qa2re(qa.getVariants());
      final org.psikeds.resolutionengine.interfaces.pojos.Alternatives alternatives = qa2re(qa.getAlternatives());
      final org.psikeds.resolutionengine.interfaces.pojos.Constituents constituents = qa2re(qa.getConstituents());
      final org.psikeds.resolutionengine.interfaces.pojos.Events events = qa2re(qa.getEvents());
      final org.psikeds.resolutionengine.interfaces.pojos.Rules rules = qa2re(qa.getRules());
      final boolean fullyResolved = qa.isFullyResolved();
      re = new org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity(features, purposes, variants, alternatives, constituents, events, rules, fullyResolved);
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.InitResponse)
   */
  @Override
  public InitResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.InitResponse re) {
    InitResponse qa = null;
    if (re != null) {
      final String sessionID = re.getSessionID();
      final KnowledgeEntity knowledgeEntity = re2qa(re.getKnowledgeEntity());
      qa = new InitResponse(sessionID, knowledgeEntity);
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.InitResponse)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.InitResponse qa2re(final InitResponse qa) {
    org.psikeds.resolutionengine.interfaces.pojos.InitResponse re = null;
    if (qa != null) {
      final String sessionID = qa.getSessionID();
      final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity knowledgeEntity = qa2re(qa.getKnowledgeEntity());
      re = new org.psikeds.resolutionengine.interfaces.pojos.InitResponse(sessionID, knowledgeEntity);
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.SelectResponse)
   */
  @Override
  public SelectResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.SelectResponse re) {
    SelectResponse qa = null;
    if (re != null) {
      final String sessionID = re.getSessionID();
      final KnowledgeEntity knowledgeEntity = re2qa(re.getKnowledgeEntity());
      qa = new SelectResponse(sessionID, knowledgeEntity);
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.SelectResponse)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.SelectResponse qa2re(final SelectResponse qa) {
    org.psikeds.resolutionengine.interfaces.pojos.SelectResponse re = null;
    if (qa != null) {
      final String sessionID = qa.getSessionID();
      final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity knowledgeEntity = qa2re(qa.getKnowledgeEntity());
      re = new org.psikeds.resolutionengine.interfaces.pojos.SelectResponse(sessionID, knowledgeEntity);
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }

  /**
   * @param re
   * @return qa
   * @see org.psikeds.queryagent.transformer.Transformer#re2qa(org.psikeds.resolutionengine.interfaces.pojos.SelectRequest)
   */
  @Override
  public SelectRequest re2qa(final org.psikeds.resolutionengine.interfaces.pojos.SelectRequest re) {
    SelectRequest qa = null;
    if (re != null) {
      final String sessionID = re.getSessionID();
      final String choice = re.getChoice();
      final KnowledgeEntity knowledgeEntity = re2qa(re.getKnowledgeEntity());
      qa = new SelectRequest(sessionID, knowledgeEntity, choice);
      LOGGER.trace("re2qa: re = {}\n--> qa = {}", re, qa);
    }
    return qa;
  }

  /**
   * @param qa
   * @return re
   * @see org.psikeds.queryagent.transformer.Transformer#qa2re(org.psikeds.queryagent.interfaces.presenter.pojos.SelectRequest)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.SelectRequest qa2re(final SelectRequest qa) {
    org.psikeds.resolutionengine.interfaces.pojos.SelectRequest re = null;
    if (qa != null) {
      final String sessionID = qa.getSessionID();
      final String choice = qa.getChoice();
      final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity knowledgeEntity = qa2re(qa.getKnowledgeEntity());
      re = new org.psikeds.resolutionengine.interfaces.pojos.SelectRequest(sessionID, knowledgeEntity, choice);
      LOGGER.trace("qa2re: qa = {}\n--> re = {}", qa, re);
    }
    return re;
  }
}
