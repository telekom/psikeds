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
package org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Data;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.Knowledgebase;
import org.psikeds.resolutionengine.datalayer.vo.Meta;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;

/**
 * Helper for transforming a JAXB XML Object from the Knowledgebase into a
 * Value Object for the Datalayer.
 * The Knowledgebase is read-only, therefore only transformations XML to VO
 * are supported/implemented.
 *
 * @author marco@juliano.de
 */
public class Xml2VoTransformer implements Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Xml2VoTransformer.class);

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Alternatives)
   */
  @Override
  public Alternatives xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Alternatives xml) {
    Alternatives vo = null;
    if (xml != null) {
      vo = new Alternatives();
      final List<org.psikeds.knowledgebase.jaxb.Fulfills> lst = xml.getFulfills();
      for (final org.psikeds.knowledgebase.jaxb.Fulfills f : lst) {
        vo.addFulfills(xml2ValueObject(f));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Constituents)
   */
  @Override
  public Constituents xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Constituents xml) {
    Constituents vo = null;
    if (xml != null) {
      vo = new Constituents();
      final List<org.psikeds.knowledgebase.jaxb.Constitutes> lst = xml.getConstitutes();
      for (final org.psikeds.knowledgebase.jaxb.Constitutes c : lst) {
        vo.addConstitutes(xml2ValueObject(c));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Constitutes)
   */
  @Override
  public Constitutes xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Constitutes xml) {
    return xml == null ? null : new Constitutes(xml.getDescription(), xml.getVariantID(), xml.getPurposeID());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Data)
   */
  @Override
  public Data xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Data xml) {
    Data vo = null;
    if (xml != null) {
      final Features features = xml2ValueObject(xml.getFeatures());
      final Purposes purposes = xml2ValueObject(xml.getPurposes());
      final Variants variants = xml2ValueObject(xml.getVariants());
      final Alternatives alternatives = xml2ValueObject(xml.getAlternatives());
      final Constituents constituents = xml2ValueObject(xml.getConstituents());
      final Events events = xml2ValueObject(xml.getEvents());
      final Rules rules = xml2ValueObject(xml.getRules());
      vo = new Data(features, purposes, variants, alternatives, constituents, events, rules);
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Event)
   */
  @Override
  public Event xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Event xml) {
    return xml == null ? null : new Event(xml.getLabel(), xml.getDescription(), xml.getId());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Events)
   */
  @Override
  public Events xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Events xml) {
    Events vo = null;
    if (xml != null) {
      vo = new Events();
      final List<org.psikeds.knowledgebase.jaxb.Event> lst = xml.getEvent();
      for (final org.psikeds.knowledgebase.jaxb.Event e : lst) {
        vo.addEvent(xml2ValueObject(e));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Feature)
   */
  @Override
  public Feature xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Feature xml) {
    return xml == null ? null : new Feature(xml.getLabel(), xml.getDescription(), xml.getId());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Features)
   */
  @Override
  public Features xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Features xml) {
    Features vo = null;
    if (xml != null) {
      vo = new Features();
      final List<org.psikeds.knowledgebase.jaxb.Feature> lst = xml.getFeature();
      for (final org.psikeds.knowledgebase.jaxb.Feature f : lst) {
        vo.addFeature(xml2ValueObject(f));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Fulfills)
   */
  @Override
  public Fulfills xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Fulfills xml) {
    return xml == null ? null : new Fulfills(xml.getDescription(), xml.getPurposeID(), xml.getVariantID());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Knowledgebase)
   */
  @Override
  public Knowledgebase xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Knowledgebase xml) {
    Knowledgebase vo = null;
    if (xml != null) {
      final Meta meta = xml2ValueObject(xml.getMeta());
      final Data data = xml2ValueObject(xml.getData());
      vo = new Knowledgebase(meta, data);
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Meta)
   */
  @Override
  public Meta xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Meta xml) {
    Meta vo = null;
    if (xml != null) {
      vo = new Meta(xml.getCreated(), xml.getLastmodified(), xml.getCreator(), xml.getDescription());
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Purpose)
   */
  @Override
  public Purpose xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Purpose xml) {
    return xml == null ? null : new Purpose(xml.getLabel(), xml.getDescription(), xml.getId());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Purposes)
   */
  @Override
  public Purposes xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Purposes xml) {
    Purposes vo = null;
    if (xml != null) {
      vo = new Purposes();
      final List<org.psikeds.knowledgebase.jaxb.Purpose> lst = xml.getPurpose();
      for (final org.psikeds.knowledgebase.jaxb.Purpose p : lst) {
        vo.addPurpose(xml2ValueObject(p));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Rule)
   */
  @Override
  public Rule xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Rule xml) {
    return xml == null ? null : new Rule(xml.getLabel(), xml.getDescription(), xml.getId());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Rules)
   */
  @Override
  public Rules xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Rules xml) {
    Rules vo = null;
    if (xml != null) {
      vo = new Rules();
      final List<org.psikeds.knowledgebase.jaxb.Rule> lst = xml.getRule();
      for (final org.psikeds.knowledgebase.jaxb.Rule r : lst) {
        vo.addRule(xml2ValueObject(r));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Variant)
   */
  @Override
  public Variant xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Variant xml) {
    return xml == null ? null : new Variant(xml.getLabel(), xml.getDescription(), xml.getId());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Variants)
   */
  @Override
  public Variants xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Variants xml) {
    Variants vo = null;
    if (xml != null) {
      vo = new Variants();
      final List<org.psikeds.knowledgebase.jaxb.Variant> lst = xml.getVariant();
      for (final org.psikeds.knowledgebase.jaxb.Variant v : lst) {
        vo.addVariant(xml2ValueObject(v));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }
}
