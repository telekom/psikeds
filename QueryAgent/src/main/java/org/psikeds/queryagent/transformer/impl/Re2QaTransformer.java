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

import org.psikeds.queryagent.interfaces.presenter.pojos.Choice;
import org.psikeds.queryagent.interfaces.presenter.pojos.Choices;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concept;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.ConceptDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Concepts;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Decissions;
import org.psikeds.queryagent.interfaces.presenter.pojos.ErrorMessage;
import org.psikeds.queryagent.interfaces.presenter.pojos.Errors;
import org.psikeds.queryagent.interfaces.presenter.pojos.Feature;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValue;
import org.psikeds.queryagent.interfaces.presenter.pojos.FeatureValues;
import org.psikeds.queryagent.interfaces.presenter.pojos.Features;
import org.psikeds.queryagent.interfaces.presenter.pojos.Knowledge;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntities;
import org.psikeds.queryagent.interfaces.presenter.pojos.KnowledgeEntity;
import org.psikeds.queryagent.interfaces.presenter.pojos.Metadata;
import org.psikeds.queryagent.interfaces.presenter.pojos.Purpose;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionMessage;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variant;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoice;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantChoices;
import org.psikeds.queryagent.interfaces.presenter.pojos.VariantDecission;
import org.psikeds.queryagent.interfaces.presenter.pojos.Variants;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warning;
import org.psikeds.queryagent.interfaces.presenter.pojos.Warnings;
import org.psikeds.queryagent.transformer.Transformer;

/**
 * Helper for transforming POJOs from the Interface of the Resolution-Engine (RE)
 * into POJOs of the Interface of the Query-Agent (QA) ... and vice versa.
 * 
 * It is used by {@link org.psikeds.queryagent.presenter.services.ResolutionBusinessService} in
 * order to transform Requests for and Responses from
 * {@link org.psikeds.queryagent.requester.client.ResolutionEngineClient}
 * 
 * Note: Currently the Interfaces of RE and QA are very similar, making this
 * Transformer "quite simple". However this will change for future Frontends
 * having a more sophisticated Data-Model.
 * 
 * @author marco@juliano.de
 */
public class Re2QaTransformer implements Transformer {

  @Override
  public Choice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Choice re) {
    Choice qa = null;
    if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.VariantChoice) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.VariantChoice) re);
    }
    else if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice) re);
    }
    else if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice) re);
    }
    else {
      throw new IllegalArgumentException("Unexpected kind of Choice: " + String.valueOf(re));
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Choice qa2re(final Choice qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Choice re = null;
    if (qa instanceof VariantChoice) {
      re = qa2re((VariantChoice) qa);
    }
    else if (qa instanceof FeatureChoice) {
      re = qa2re((FeatureChoice) qa);
    }
    else if (qa instanceof ConceptChoice) {
      re = qa2re((ConceptChoice) qa);
    }
    else {
      throw new IllegalArgumentException("Unexpected kind of Choice: " + String.valueOf(qa));
    }
    return re;
  }

  @Override
  public Choices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Choices re) {
    Choices qa = null;
    if (re != null) {
      qa = new Choices();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Choice c : re) {
        qa.add(re2qa(c));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Choices qa2re(final Choices qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Choices re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Choices();
      for (final Choice c : qa) {
        re.add(qa2re(c));
      }
    }
    return re;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Concept qa2re(final Concept qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Concept(qa.getConceptID(), qa2re(qa.getValues())));
  }

  @Override
  public Concept re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Concept re) {
    return (re == null ? null : new Concept(re.getConceptID(), re2qa(re.getValues())));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice qa2re(final ConceptChoice qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice(qa.getParentVariantID(), qa2re(qa.getConcepts())));
  }

  @Override
  public ConceptChoice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice re) {
    return (re == null ? null : new ConceptChoice(re.getParentVariantID(), re2qa(re.getConcepts())));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices qa2re(final ConceptChoices qa) {
    org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices();
      for (final ConceptChoice c : qa) {
        re.add(qa2re(c));
      }
    }
    return re;
  }

  @Override
  public ConceptChoices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices re) {
    ConceptChoices qa = null;
    if (re != null) {
      qa = new ConceptChoices();
      for (final org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice c : re) {
        qa.add(re2qa(c));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission qa2re(final ConceptDecission qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission(qa.getVariantID(), qa.getConceptID()));
  }

  @Override
  public ConceptDecission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission re) {
    return (re == null ? null : new ConceptDecission(re.getVariantID(), re.getConceptID()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Concepts qa2re(final Concepts qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Concepts re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Concepts();
      for (final Concept c : qa) {
        re.add(qa2re(c));
      }
    }
    return re;
  }

  @Override
  public Concepts re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Concepts re) {
    Concepts qa = null;
    if (re != null) {
      qa = new Concepts();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Concept c : re) {
        qa.add(re2qa(c));
      }
    }
    return qa;
  }

  @Override
  public Decission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Decission re) {
    Decission qa = null;
    if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.VariantDecission) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.VariantDecission) re);
    }
    else if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission) re);
    }
    else if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.ConceptDecission) re);
    }
    else {
      throw new IllegalArgumentException("Unexpected kind of Decission: " + String.valueOf(re));
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Decission qa2re(final Decission qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Decission re = null;
    if (qa instanceof VariantDecission) {
      re = qa2re((VariantDecission) qa);
    }
    else if (qa instanceof FeatureDecission) {
      re = qa2re((FeatureDecission) qa);
    }
    else if (qa instanceof ConceptDecission) {
      re = qa2re((ConceptDecission) qa);
    }
    else {
      throw new IllegalArgumentException("Unexpected kind of Decission: " + String.valueOf(qa));
    }
    return re;
  }

  @Override
  public Decissions re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Decissions re) {
    Decissions qa = null;
    if (re != null) {
      qa = new Decissions();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Decission d : re) {
        qa.add(re2qa(d));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Decissions qa2re(final Decissions qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Decissions re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Decissions();
      for (final Decission d : qa) {
        re.add(qa2re(d));
      }
    }
    return re;
  }

  @Override
  public ErrorMessage re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage re) {
    return (re == null ? null : new ErrorMessage(re.getCode(), re.getMessage()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage qa2re(final ErrorMessage qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage(qa.getCode(), qa.getMessage()));
  }

  @Override
  public Errors re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Errors re) {
    Errors qa = null;
    if (re != null) {
      qa = new Errors();
      for (final org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage msg : re) {
        qa.add(re2qa(msg));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Errors qa2re(final Errors qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Errors re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Errors();
      for (final ErrorMessage msg : qa) {
        re.add(qa2re(msg));
      }
    }
    return re;
  }

  @Override
  public Feature re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Feature re) {
    return (re == null ? null : new Feature(re.getLabel(), re.getDescription(), re.getFeatureID(), re.getValueType(), re.getUnit()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Feature qa2re(final Feature qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Feature(qa.getLabel(), qa.getDescription(), qa.getFeatureID(), qa.getValueType(), qa.getUnit()));
  }

  @Override
  public FeatureChoice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice re) {
    return (re == null ? null : new FeatureChoice(re.getParentVariantID(), re.getFeatureID(), re2qa(re.getPossibleValues())));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice qa2re(final FeatureChoice qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice(qa.getParentVariantID(), qa.getFeatureID(), qa2re(qa.getPossibleValues())));
  }

  @Override
  public FeatureChoices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices re) {
    FeatureChoices qa = null;
    if (re != null) {
      qa = new FeatureChoices();
      for (final org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice fc : re) {
        qa.add(re2qa(fc));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices qa2re(final FeatureChoices qa) {
    org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices();
      for (final FeatureChoice fc : qa) {
        re.add(qa2re(fc));
      }
    }
    return re;
  }

  @Override
  public FeatureDecission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission re) {
    return (re == null ? null : new FeatureDecission(re.getVariantID(), re.getFeatureID(), re.getFeatureValueID()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission qa2re(final FeatureDecission qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission(qa.getVariantID(), qa.getFeatureID(), qa.getFeatureValueID()));
  }

  @Override
  public Features re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Features re) {
    Features qa = null;
    if (re != null) {
      qa = new Features();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Feature f : re) {
        qa.add(re2qa(f));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Features qa2re(final Features qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Features re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Features();
      for (final Feature f : qa) {
        re.add(qa2re(f));
      }
    }
    return re;
  }

  @Override
  public FeatureValue re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue re) {
    return (re == null ? null : new FeatureValue(re.getFeatureID(), re.getFeatureValueID(), re.getValue()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValue qa2re(final FeatureValue qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.FeatureValue(qa.getFeatureID(), qa.getFeatureValueID(), qa.getValue()));
  }

  @Override
  public FeatureValues re2qa(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValues re) {
    FeatureValues qa = null;
    if (re != null) {
      qa = new FeatureValues();
      for (final org.psikeds.resolutionengine.interfaces.pojos.FeatureValue fv : re) {
        qa.add(re2qa(fv));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValues qa2re(final FeatureValues qa) {
    org.psikeds.resolutionengine.interfaces.pojos.FeatureValues re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.FeatureValues();
      for (final FeatureValue fv : qa) {
        re.add(qa2re(fv));
      }
    }
    return re;
  }

  @Override
  public Knowledge re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Knowledge re) {
    return (re == null ? null : new Knowledge(re2qa(re.getEntities()), re2qa(re.getChoices())));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Knowledge qa2re(final Knowledge qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Knowledge(qa2re(qa.getEntities()), qa2re(qa.getChoices())));
  }

  @Override
  public KnowledgeEntities re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities re) {
    KnowledgeEntities qa = null;
    if (re != null) {
      qa = new KnowledgeEntities();
      for (final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity ke : re) {
        qa.add(re2qa(ke));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities qa2re(final KnowledgeEntities qa) {
    org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities();
      for (final KnowledgeEntity ke : qa) {
        re.add(qa2re(ke));
      }
    }
    return re;
  }

  @Override
  public KnowledgeEntity re2qa(final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re) {
    KnowledgeEntity qa = null;
    if (re != null) {
      final long quantity = re.getQuantity();
      final Purpose purpose = re2qa(re.getPurpose());
      final Variant variant = re2qa(re.getVariant());
      final FeatureValues features = re2qa(re.getFeatures());
      final KnowledgeEntities children = re2qa(re.getChildren());
      final VariantChoices possibleVariants = re2qa(re.getPossibleVariants());
      final FeatureChoices possibleFeatures = re2qa(re.getPossibleFeatures());
      final ConceptChoices possibleConcepts = re2qa(re.getPossibleConcepts());
      qa = new KnowledgeEntity(quantity, purpose, variant, features, children, possibleVariants, possibleFeatures, possibleConcepts);
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity qa2re(final KnowledgeEntity qa) {
    org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity re = null;
    if (qa != null) {
      final long quantity = qa.getQuantity();
      final org.psikeds.resolutionengine.interfaces.pojos.Purpose purpose = qa2re(qa.getPurpose());
      final org.psikeds.resolutionengine.interfaces.pojos.Variant variant = qa2re(qa.getVariant());
      final org.psikeds.resolutionengine.interfaces.pojos.FeatureValues features = qa2re(qa.getFeatures());
      final org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities children = qa2re(qa.getChildren());
      final org.psikeds.resolutionengine.interfaces.pojos.VariantChoices possibleVariants = qa2re(qa.getPossibleVariants());
      final org.psikeds.resolutionengine.interfaces.pojos.FeatureChoices possibleFeatures = qa2re(qa.getPossibleFeatures());
      final org.psikeds.resolutionengine.interfaces.pojos.ConceptChoices possibleConcepts = qa2re(qa.getPossibleConcepts());
      re = new org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity(quantity, purpose, variant, features, children, possibleVariants, possibleFeatures, possibleConcepts);
    }
    return re;
  }

  @Override
  public Metadata re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Metadata re) {
    return (re == null ? null : new Metadata(re.getInfoMap()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Metadata qa2re(final Metadata qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Metadata(qa.getInfoMap()));
  }

  @Override
  public Purpose re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Purpose re) {
    return re == null ? null : new Purpose(re.getLabel(), re.getDescription(), re.getPurposeID(), re.isRoot());
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose qa2re(final Purpose qa) {
    return qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(qa.getLabel(), qa.getDescription(), qa.getPurposeID(), qa.isRoot());
  }

  @Override
  public ResolutionMessage re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage re) {
    ResolutionMessage qa = null;
    if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.Warning) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.Warning) re);
    }
    else if (re instanceof org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage) {
      qa = re2qa((org.psikeds.resolutionengine.interfaces.pojos.ErrorMessage) re);
    }
    else {
      throw new IllegalArgumentException("Unexpected kind of ResolutionMessage: " + String.valueOf(re));
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage qa2re(final ResolutionMessage qa) {
    org.psikeds.resolutionengine.interfaces.pojos.ResolutionMessage re = null;
    if (qa instanceof Warning) {
      re = qa2re((Warning) qa);
    }
    else if (qa instanceof ErrorMessage) {
      re = qa2re((ErrorMessage) qa);
    }
    else {
      throw new IllegalArgumentException("Unexpected kind of ResolutionMessage: " + String.valueOf(qa));
    }
    return re;
  }

  @Override
  public ResolutionRequest re2qa(final org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest re) {
    ResolutionRequest qa = null;
    if (re != null) {
      final String sessionID = re.getSessionID();
      final Metadata metadata = re2qa(re.getMetadata());
      final Knowledge knowledge = re2qa(re.getKnowledge());
      final Decissions decissions = re2qa(re.getDecissions());
      qa = new ResolutionRequest(sessionID, metadata, knowledge, decissions);
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
      final org.psikeds.resolutionengine.interfaces.pojos.Decissions decissions = qa2re(qa.getDecissions());
      re = new org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest(sessionID, metadata, knowledge, decissions);
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
      final Errors errors = re2qa(re.getErrors());
      final Warnings warnings = re2qa(re.getWarnings());
      qa = new ResolutionResponse(sessionID, metadata, knowledge, errors, warnings);
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
      final org.psikeds.resolutionengine.interfaces.pojos.Errors errors = qa2re(qa.getErrors());
      final org.psikeds.resolutionengine.interfaces.pojos.Warnings warnings = qa2re(qa.getWarnings());
      re = new org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse(sessionID, metadata, knowledge, errors, warnings);
    }
    return re;
  }

  @Override
  public Variant re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variant re) {
    return (re == null ? null : new Variant(re.getLabel(), re.getDescription(), re.getVariantID(), re2qa(re.getFeatures())));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant qa2re(final Variant qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Variant(qa.getLabel(), qa.getDescription(), qa.getVariantID(), qa2re(qa.getFeatures())));
  }

  @Override
  public VariantChoice re2qa(final org.psikeds.resolutionengine.interfaces.pojos.VariantChoice re) {
    VariantChoice qa = null;
    if (re != null) {
      final long qty = re.getQuantity();
      final String parentVariantID = re.getParentVariantID();
      final Purpose purpose = re2qa(re.getPurpose());
      final Variants variants = re2qa(re.getVariants());
      qa = new VariantChoice(parentVariantID, purpose, variants, qty);
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice qa2re(final VariantChoice qa) {
    org.psikeds.resolutionengine.interfaces.pojos.VariantChoice re = null;
    if (qa != null) {
      final long qty = qa.getQuantity();
      final String parentVariantID = qa.getParentVariantID();
      final org.psikeds.resolutionengine.interfaces.pojos.Purpose purpose = qa2re(qa.getPurpose());
      final org.psikeds.resolutionengine.interfaces.pojos.Variants variants = qa2re(qa.getVariants());
      re = new org.psikeds.resolutionengine.interfaces.pojos.VariantChoice(parentVariantID, purpose, variants, qty);
    }
    return re;
  }

  @Override
  public VariantChoices re2qa(final org.psikeds.resolutionengine.interfaces.pojos.VariantChoices re) {
    VariantChoices qa = null;
    if (re != null) {
      qa = new VariantChoices();
      for (final org.psikeds.resolutionengine.interfaces.pojos.VariantChoice vc : re) {
        qa.add(re2qa(vc));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoices qa2re(final VariantChoices qa) {
    org.psikeds.resolutionengine.interfaces.pojos.VariantChoices re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.VariantChoices();
      for (final VariantChoice vc : qa) {
        re.add(qa2re(vc));
      }
    }
    return re;
  }

  @Override
  public VariantDecission re2qa(final org.psikeds.resolutionengine.interfaces.pojos.VariantDecission re) {
    return (re == null ? null : new VariantDecission(re.getPurposeID(), re.getVariantID()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantDecission qa2re(final VariantDecission qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.VariantDecission(qa.getPurposeID(), qa.getVariantID()));
  }

  @Override
  public Variants re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Variants re) {
    Variants qa = null;
    if (re != null) {
      qa = new Variants();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Variant v : re) {
        qa.add(re2qa(v));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variants qa2re(final Variants qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Variants re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Variants();
      for (final Variant v : qa) {
        re.add(qa2re(v));
      }
    }
    return re;
  }

  @Override
  public Warning re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Warning re) {
    return (re == null ? null : new Warning(re.getMessage()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Warning qa2re(final Warning qa) {
    return (qa == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Warning(qa.getMessage()));
  }

  @Override
  public Warnings re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Warnings re) {
    Warnings qa = null;
    if (re != null) {
      qa = new Warnings();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Warning w : re) {
        qa.add(re2qa(w));
      }
    }
    return qa;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Warnings qa2re(final Warnings qa) {
    org.psikeds.resolutionengine.interfaces.pojos.Warnings re = null;
    if (qa != null) {
      re = new org.psikeds.resolutionengine.interfaces.pojos.Warnings();
      for (final Warning w : qa) {
        re.add(qa2re(w));
      }
    }
    return re;
  }
}
