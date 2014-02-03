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

import java.util.ArrayList;
import java.util.List;

import org.psikeds.queryagent.interfaces.presenter.pojos.Choice;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValueType;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.transformer.Transformer;

/**
 * Helper for transforming POJOs from the Interface of the
 * Resolution-Engine (RE) into POJOs of the Interface of the
 * Query-Agent (QA) ... and vice versa.
 * 
 * @author marco@juliano.de
 */
public class Re2QaTransformer implements Transformer {

  @Override
  public Choice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Choice re) {
    Choice qa = null;
    if (re != null) {
      final Variant parentVariant = re2qa(re.getParentVariant());
      final Purpose purpose = re2qa(re.getPurpose());
      final List<Variant> variants = new ArrayList<Variant>();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Variant v : re.getVariants()) {
        variants.add(re2qa(v));
      }
      qa = new Choice(parentVariant, purpose, variants);
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Choice qa2re(final Choice qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Choice re = null;
    if (qa != null) {
      final org.psikeds.resolutionengine.interfaces.pojos.Variant parentVariant = qa2re(qa.getParentVariant());
      final org.psikeds.resolutionengine.interfaces.pojos.Purpose purpose = qa2re(qa.getPurpose());
      final List<org.psikeds.resolutionengine.interfaces.pojos.Variant> variants = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Variant>();
      for (final Variant v : qa.getVariants()) {
        variants.add(qa2re(v));
      }
      re = new org.psikeds.resolutionengine.interfaces.pojos.Choice(parentVariant, purpose, variants);
    }
    return re;
  }

  @Override
  public Decission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Decission re) {
    Decission qa = null;
    if (re != null) {
      qa = new Decission(re.getPurposeID(), re.getVariantID());
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Decission qa2re(final Decission qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Decission re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Decission(qa.getPurposeID(), qa.getVariantID());
    }
    return re;
  }

  @Override
  public Feature re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Feature re) {
    Feature qa = null;
    if (re != null) {
      qa = new Feature(re.getLabel(), re.getDescription(), re.getId(), re.getMinValue(), re.getMaxValue(), re2qa(re.getValueType()));
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(final Feature qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Feature re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Feature(qa.getLabel(), qa.getDescription(), qa.getId(), qa.getMinValue(), qa.getMaxValue(), qa2re(qa.getValueType()));
    }
    return re;
  }

  @Override
  public FeatureValueType re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType re) {
    return (re == null ? null : FeatureValueType.fromValue(re.value()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType qa2re(final FeatureValueType qa) {
    return (qa == null ? null : org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType.fromValue(qa.value()));
  }

  @Override
  public Knowledge re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Knowledge re) {
    Knowledge qa = null;
    if (re != null) {
      final List<KnowledgeEntity> entities = new ArrayList<KnowledgeEntity>();
      for (final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity ke : re.getEntities()) {
        entities.add(re2qa(ke));
      }
      final List<Choice> choices = new ArrayList<Choice>();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Choice c : re.getChoices()) {
        choices.add(re2qa(c));
      }
      qa = new Knowledge(entities, choices);
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Knowledge qa2re(final Knowledge qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Knowledge re = null;
    if (qa != null) {
      final List<org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity> entities = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity>();
      for (final KnowledgeEntity ke : qa.getEntities()) {
        entities.add(qa2re(ke));
      }
      final List<org.psikeds.resolutionengine.interfaces.pojos.Choice> choices = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Choice>();
      for (final Choice c : qa.getChoices()) {
        choices.add(qa2re(c));
      }
      re = new org.psikeds.resolutionengine.interfaces.pojos.Knowledge(entities, choices);
    }
    return re;
  }

  @Override
  public KnowledgeEntity re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re) {
    KnowledgeEntity qa = null;
    if (re != null) {
      final Purpose purpose = re2qa(re.getPurpose());
      final Variant variant = re2qa(re.getVariant());
      final List<KnowledgeEntity> siblings = new ArrayList<KnowledgeEntity>();
      for (final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity ke : re.getSiblings()) {
        siblings.add(re2qa(ke));
      }
      final List<Choice> choices = new ArrayList<Choice>();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Choice c : re.getChoices()) {
        choices.add(re2qa(c));
      }
      qa = new KnowledgeEntity(purpose, variant, siblings, choices);
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(final KnowledgeEntity qa) {
    org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re = null;
    if (qa != null) {
      final org.psikeds.resolutionengine.interfaces.pojos.Purpose purpose = qa2re(qa.getPurpose());
      final org.psikeds.resolutionengine.interfaces.pojos.Variant variant = qa2re(qa.getVariant());
      final List<org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity> siblings = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity>();
      for (final KnowledgeEntity ke : qa.getSiblings()) {
        siblings.add(qa2re(ke));
      }
      final List<org.psikeds.resolutionengine.interfaces.pojos.Choice> choices = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Choice>();
      for (final Choice c : qa.getChoices()) {
        choices.add(qa2re(c));
      }
      re = new org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity(purpose, variant, siblings, choices);
    }
    return re;
  }

  @Override
  public Metadata re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Metadata re) {
    return (re == null ? null : new Metadata(re.getInfomap()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Metadata qa2re(final Metadata qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Metadata(qa.getInfomap()));
  }

  @Override
  public Purpose re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purpose re) {
    return re == null ? null : new Purpose(re.getLabel(), re.getDescription(), re.getId(), re.isRoot());
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(final Purpose qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(qa.getLabel(), qa.getDescription(), qa.getId(), qa.isRoot());
  }

  @Override
  public ResolutionRequest re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest re) {
    ResolutionRequest qa = null;
    if (re != null) {
      final String sessionID = re.getSessionID();
      final Metadata metadata = re2qa(re.getMetadata());
      final Knowledge knowledge = re2qa(re.getKnowledge());
      qa = new ResolutionRequest(sessionID, metadata, knowledge);
      for (final org.psikeds.resolutionengine.interfaces.pojos.Decission decission : re.getMadeDecissions()) {
        if (decission != null) {
          qa.addMadeDecission(re2qa(decission));
        }
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest qa2re(final ResolutionRequest qa) {
    org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest re = null;
    if (qa != null) {
      final String sessionID = qa.getSessionID();
      final org.psikeds.resolutionengine.interfaces.pojos.Metadata metadata = qa2re(qa.getMetadata());
      final org.psikeds.resolutionengine.interfaces.pojos.Knowledge knowledge = qa2re(qa.getKnowledge());
      re = new org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest(sessionID, metadata, knowledge);
      for (final Decission decission : qa.getMadeDecissions()) {
        if (decission != null) {
          re.addMadeDecission(qa2re(decission));
        }
      }
    }
    return re;
  }

  @Override
  public ResolutionResponse re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse re) {
    ResolutionResponse qa = null;
    if (re != null) {
      final String sessionID = re.getSessionID();
      final Metadata metadata = re2qa(re.getMetadata());
      final Knowledge knowledge = re2qa(re.getKnowledge());
      final boolean resolved = re.isResolved();
      qa = new ResolutionResponse(sessionID, metadata, knowledge, resolved);
      for (final org.psikeds.resolutionengine.interfaces.pojos.Choice c : re.getPossibleChoices()) {
        qa.addPossibleChoice(re2qa(c));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse qa2re(final ResolutionResponse qa) {
    org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse re = null;
    if (qa != null) {
      final String sessionID = qa.getSessionID();
      final org.psikeds.resolutionengine.interfaces.pojos.Metadata metadata = qa2re(qa.getMetadata());
      final org.psikeds.resolutionengine.interfaces.pojos.Knowledge knowledge = qa2re(qa.getKnowledge());
      // choices and resolved will be automatically calculated by this constructor
      re = new org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse(sessionID, metadata, knowledge);
    }
    return re;
  }

  @Override
  public Variant re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variant re) {
    Variant qa = null;
    if (re != null) {
      qa = new Variant(re.getLabel(), re.getDescription(), re.getId());
      for (final org.psikeds.resolutionengine.interfaces.pojos.Feature f : re.getFeatures()) {
        qa.addFeature(re2qa(f));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(final Variant qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Variant re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Variant(qa.getLabel(), qa.getDescription(), qa.getId());
      for (final Feature f : qa.getFeatures()) {
        re.addFeature(qa2re(f));
      }
    }
    return re;
  }
}
