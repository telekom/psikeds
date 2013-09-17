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
package org.psikeds.resolutionengine.transformer.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for transforming Value Objects from the Datalayer into POJOs of the
 * Interface (and vice versa).
 *
 * @author marco@juliano.de
 */
public class Vo2PojoTransformer implements Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vo2PojoTransformer.class);

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Alternatives)
   */
  @Override
  public Alternatives pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Alternatives pojo) {
    Alternatives vo = null;
    if (pojo != null) {
      vo = new Alternatives();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Fulfills> lst = pojo.getFulfills();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Fulfills f : lst) {
        vo.addFulfills(pojo2ValueObject(f));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Alternatives)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Alternatives valueObject2Pojo(final Alternatives vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Alternatives pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Alternatives();
      final List<Fulfills> lst = vo.getFulfills();
      for (final Fulfills f : lst) {
        pojo.addFulfills(valueObject2Pojo(f));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Constituents)
   */
  @Override
  public Constituents pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Constituents pojo) {
    Constituents vo = null;
    if (pojo != null) {
      vo = new Constituents();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Constitutes> lst = pojo.getConstitutes();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Constitutes c : lst) {
        vo.addConstitutes(pojo2ValueObject(c));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Constituents)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Constituents valueObject2Pojo(final Constituents vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Constituents pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Constituents();
      final List<Constitutes> lst = vo.getConstitutes();
      for (final Constitutes c : lst) {
        pojo.addConstitutes(valueObject2Pojo(c));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Constitutes)
   */
  @Override
  public Constitutes pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Constitutes pojo) {
    return pojo == null ? null : new Constitutes(pojo.getDescription(), pojo.getVariantID(), pojo.getPurposeID());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Constitutes)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Constitutes valueObject2Pojo(final Constitutes vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Constitutes(vo.getDescription(), vo.getVariantID(), vo.getPurposeID());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Event)
   */
  @Override
  public Event pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Event pojo) {
    return pojo == null ? null : new Event(pojo.getLabel(), pojo.getDescription(), pojo.getId());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Event)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Event valueObject2Pojo(final Event vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Event(vo.getLabel(), vo.getDescription(), vo.getId());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Events)
   */
  @Override
  public Events pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Events pojo) {
    Events vo = null;
    if (pojo != null) {
      vo = new Events();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Event> lst = pojo.getEvent();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Event e : lst) {
        vo.addEvent(pojo2ValueObject(e));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Events)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Events valueObject2Pojo(final Events vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Events pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Events();
      final List<Event> lst = vo.getEvent();
      for (final Event e : lst) {
        pojo.addEvent(valueObject2Pojo(e));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Feature)
   */
  @Override
  public Feature pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Feature pojo) {
    return pojo == null ? null : new Feature(pojo.getLabel(), pojo.getDescription(), pojo.getId());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Feature)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Feature valueObject2Pojo(final Feature vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Feature(vo.getLabel(), vo.getDescription(), vo.getId());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Features)
   */
  @Override
  public Features pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Features pojo) {
    Features vo = null;
    if (pojo != null) {
      vo = new Features();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Feature> lst = pojo.getFeature();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Feature f : lst) {
        vo.addFeature(pojo2ValueObject(f));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Features)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Features valueObject2Pojo(final Features vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Features pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Features();
      final List<Feature> lst = vo.getFeature();
      for (final Feature f : lst) {
        pojo.addFeature(valueObject2Pojo(f));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Fulfills)
   */
  @Override
  public Fulfills pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Fulfills pojo) {
    return pojo == null ? null : new Fulfills(pojo.getDescription(), pojo.getPurposeID(), pojo.getVariantID());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Fulfills)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Fulfills valueObject2Pojo(final Fulfills vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Fulfills(vo.getDescription(), vo.getPurposeID(), vo.getVariantID());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Purpose)
   */
  @Override
  public Purpose pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Purpose pojo) {
    return pojo == null ? null : new Purpose(pojo.getLabel(), pojo.getDescription(), pojo.getId());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Purpose)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(final Purpose vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(vo.getLabel(), vo.getDescription(), vo.getId());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Purposes)
   */
  @Override
  public Purposes pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Purposes pojo) {
    Purposes vo = null;
    if (pojo != null) {
      vo = new Purposes();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> lst = pojo.getPurpose();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Purpose p : lst) {
        vo.addPurpose(pojo2ValueObject(p));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Purposes)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purposes valueObject2Pojo(final Purposes vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Purposes pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Purposes();
      final List<Purpose> lst = vo.getPurpose();
      for (final Purpose p : lst) {
        pojo.addPurpose(valueObject2Pojo(p));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Rule)
   */
  @Override
  public Rule pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Rule pojo) {
    return pojo == null ? null : new Rule(pojo.getLabel(), pojo.getDescription(), pojo.getId());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Rule)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Rule valueObject2Pojo(final Rule vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Rule(vo.getLabel(), vo.getDescription(), vo.getId());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Rules)
   */
  @Override
  public Rules pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Rules pojo) {
    Rules vo = null;
    if (pojo != null) {
      vo = new Rules();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Rule> lst = pojo.getRule();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Rule r : lst) {
        vo.addRule(pojo2ValueObject(r));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Rules)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Rules valueObject2Pojo(final Rules vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Rules pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Rules();
      final List<Rule> lst = vo.getRule();
      for (final Rule r : lst) {
        pojo.addRule(valueObject2Pojo(r));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Variant)
   */
  @Override
  public Variant pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Variant pojo) {
    return pojo == null ? null : new Variant(pojo.getLabel(), pojo.getDescription(), pojo.getId());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variant)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final Variant vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Variant(vo.getLabel(), vo.getDescription(), vo.getId());
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Variants)
   */
  @Override
  public Variants pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Variants pojo) {
    Variants vo = null;
    if (pojo != null) {
      vo = new Variants();
      final List<org.psikeds.resolutionengine.interfaces.pojos.Variant> lst = pojo.getVariant();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Variant v : lst) {
        vo.addVariant(pojo2ValueObject(v));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variants)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variants valueObject2Pojo(final Variants vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Variants pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Variants();
      final List<Variant> lst = vo.getVariant();
      for (final Variant v : lst) {
        pojo.addVariant(valueObject2Pojo(v));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }
}
