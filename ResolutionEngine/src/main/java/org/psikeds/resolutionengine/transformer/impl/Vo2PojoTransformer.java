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
package org.psikeds.resolutionengine.transformer.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValueType;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.ValueObject;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.datalayer.vo.Variants;
import org.psikeds.resolutionengine.interfaces.pojos.Choice;
import org.psikeds.resolutionengine.interfaces.pojos.POJO;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for transforming Value Objects from the Datalayer into POJOs of the
 * Interface (and vice versa).
 *
 * @author marco@juliano.de
 */
public class Vo2PojoTransformer implements Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vo2PojoTransformer.class);

  // ----------------------------------------------------------------

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Purpose)
   */
  @Override
  public Purpose pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Purpose pojo) {
    return pojo == null ? null : new Purpose(pojo.getLabel(), pojo.getDescription(), pojo.getId(), pojo.isRoot());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Purpose)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(final Purpose vo) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(vo.getLabel(), vo.getDescription(), vo.getId(), vo.isRoot());
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Purposes)
   */
  @Override
  public List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> valueObject2Pojo(final Purposes vo) {
    List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> pojo = null;
    if (vo != null) {
      pojo = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Purpose>();
      final List<Purpose> lst = vo.getPurpose();
      for (final Purpose p : lst) {
        pojo.add(valueObject2Pojo(p));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Variant)
   */
  @Override
  public Variant pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Variant pojo) {
    Variant vo = null;
    if (pojo != null) {
      List<String> featureIds = null;
      final List<org.psikeds.resolutionengine.interfaces.pojos.Feature> lst = pojo.getFeatures();
      if (lst != null) {
        featureIds = new ArrayList<String>();
        for (final org.psikeds.resolutionengine.interfaces.pojos.Feature f : lst) {
          if (f != null) {
            featureIds.add(f.getId());
          }
        }
      }
      vo = new Variant(pojo.getLabel(), pojo.getDescription(), pojo.getId(), featureIds);
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variant)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final Variant vo) {
    return valueObject2Pojo(vo, null);
  }

  /**
   * @param vo
   * @param features
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variant, java.util.List)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final Variant vo, final List<org.psikeds.resolutionengine.interfaces.pojos.Feature> features) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Variant(vo.getLabel(), vo.getDescription(), vo.getId(), features);
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variants)
   */
  @Override
  public List<org.psikeds.resolutionengine.interfaces.pojos.Variant> valueObject2Pojo(final Variants vo) {
    List<org.psikeds.resolutionengine.interfaces.pojos.Variant> pojo = null;
    if (vo != null) {
      final List<Variant> lst = vo.getVariant();
      if (lst != null) {
        pojo = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Variant>();
        for (final Variant v : lst) {
          pojo.add(valueObject2Pojo(v));
        }
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.Feature)
   */
  @Override
  public Feature pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Feature pojo) {
    Feature vo = null;
    if (pojo != null) {
      if (pojo.isRange()) {
        vo = new Feature(pojo.getLabel(), pojo.getDescription(), pojo.getId(), pojo.getMinValue(), pojo.getMaxValue(), pojo2ValueObject(pojo.getValueType()));
      }
      else {
        vo = new Feature(pojo.getLabel(), pojo.getDescription(), pojo.getId(), pojo.getValue(), pojo2ValueObject(pojo.getValueType()));
      }
      LOGGER.trace("pojo2ValueObject: pojo = {}\n--> vo = {}", pojo, vo);
    }
    return vo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Feature)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Feature valueObject2Pojo(final Feature vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Feature pojo = null;
    if (vo != null) {
      if (vo.isRange()) {
        pojo = new org.psikeds.resolutionengine.interfaces.pojos.Feature(vo.getLabel(), vo.getDescription(), vo.getId(), vo.getMinValue(), vo.getMaxValue(), valueObject2Pojo(vo.getValueType()));
      }
      else {
        pojo = new org.psikeds.resolutionengine.interfaces.pojos.Feature(vo.getLabel(), vo.getDescription(), vo.getId(), vo.getValue(), valueObject2Pojo(vo.getValueType()));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Features)
   */
  @Override
  public List<org.psikeds.resolutionengine.interfaces.pojos.Feature> valueObject2Pojo(final Features vo) {
    List<org.psikeds.resolutionengine.interfaces.pojos.Feature> pojo = null;
    if (vo != null) {
      pojo = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Feature>();
      final List<Feature> lst = vo.getFeature();
      for (final Feature f : lst) {
        pojo.add(valueObject2Pojo(f));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  /**
   * @param pojo
   * @return
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType)
   */
  @Override
  public FeatureValueType pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType pojo) {
    return pojo == null ? null : FeatureValueType.fromValue(pojo.toString());
  }

  /**
   * @param vo
   * @return
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.FeatureValueType)
   */
  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType valueObject2Pojo(final FeatureValueType vo) {
    return vo == null ? null : org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType.fromValue(vo.toString());
  }

  // ----------------------------------------------------------------

  /**
   * @param p
   * @param vars
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Purpose, java.util.List)
   */
  @Override
  public Choice valueObject2Pojo(final Purpose p, final List<Variant> vars) {
    return valueObject2Pojo(null, p, vars);
  }

  /**
   * @param p
   * @param vars
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Purpose, org.psikeds.resolutionengine.datalayer.vo.Variants)
   */
  @Override
  public Choice valueObject2Pojo(final Purpose p, final Variants vars) {
    return valueObject2Pojo(null, p, vars == null ? null : vars.getVariant());
  }

  /**
   * @param parent
   * @param p
   * @param vars
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variant, org.psikeds.resolutionengine.datalayer.vo.Purpose, java.util.List)
   */
  @Override
  public Choice valueObject2Pojo(final Variant parent, final Purpose p, final List<Variant> vars) {
    final org.psikeds.resolutionengine.interfaces.pojos.Variant pojoParent = valueObject2Pojo(parent);
    final org.psikeds.resolutionengine.interfaces.pojos.Purpose pojoPurpose = valueObject2Pojo(p);
    final List<org.psikeds.resolutionengine.interfaces.pojos.Variant> pojoVariants = valueObject2Pojo(vars);
    return new Choice(pojoParent, pojoPurpose, pojoVariants);
  }

  /**
   * @param parent
   * @param p
   * @param vars
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(org.psikeds.resolutionengine.datalayer.vo.Variant, org.psikeds.resolutionengine.datalayer.vo.Purpose, org.psikeds.resolutionengine.datalayer.vo.Variants)
   */
  @Override
  public Choice valueObject2Pojo(final Variant parent, final Purpose p, final Variants vars) {
    return valueObject2Pojo(parent, p, vars == null ? null : vars.getVariant());
  }

  // ----------------------------------------------------------------

  /**
   * @param vo
   * @return pojo
   * @see org.psikeds.resolutionengine.transformer.Transformer#valueObject2Pojo(java.util.List)
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T extends POJO> List<T> valueObject2Pojo(final List<? extends ValueObject> volst) {
    List<T> pojolst = null;
    if (volst != null) {
      pojolst = new ArrayList<T>();
      for (final ValueObject vo : volst) {
        if (vo instanceof Purpose) {
          final org.psikeds.resolutionengine.interfaces.pojos.Purpose p = valueObject2Pojo((Purpose) vo);
          pojolst.add((T) p);
        }
        else if (vo instanceof Variant) {
          final org.psikeds.resolutionengine.interfaces.pojos.Variant v = valueObject2Pojo((Variant) vo);
          pojolst.add((T) v);
        }
        else if (vo instanceof Feature) {
          final org.psikeds.resolutionengine.interfaces.pojos.Feature f = valueObject2Pojo((Feature) vo);
          pojolst.add((T) f);
        }
        else {
          throw new IllegalArgumentException("Unsupported ValueObject: " + vo);
        }
      }
      LOGGER.trace("valueObject2Pojo: volst = {}\n--> pojolst = {}", volst, pojolst);
    }
    return pojolst;
  }

  /**
   * @param pojo
   * @return vo
   * @see org.psikeds.resolutionengine.transformer.Transformer#pojo2ValueObject(java.util.List)
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T extends ValueObject> List<T> pojo2ValueObject(final List<? extends POJO> pojolst) {
    List<T> volst = null;
    if (pojolst != null) {
      volst = new ArrayList<T>();
      for (final POJO pojo : pojolst) {
        if (pojo instanceof org.psikeds.resolutionengine.interfaces.pojos.Purpose) {
          final Purpose p = pojo2ValueObject((org.psikeds.resolutionengine.interfaces.pojos.Purpose) pojo);
          volst.add((T) p);
        }
        if (pojo instanceof org.psikeds.resolutionengine.interfaces.pojos.Variant) {
          final Variant v = pojo2ValueObject((org.psikeds.resolutionengine.interfaces.pojos.Variant) pojo);
          volst.add((T) v);
        }
        if (pojo instanceof org.psikeds.resolutionengine.interfaces.pojos.Feature) {
          final Feature f = pojo2ValueObject((org.psikeds.resolutionengine.interfaces.pojos.Feature) pojo);
          volst.add((T) f);
        }
        else {
          throw new IllegalArgumentException("Unsupported POJO: " + pojo);
        }
      }
      LOGGER.trace("pojo2ValueObject: pojolst = {}\n--> volst = {}", pojolst, volst);
    }
    return volst;
  }
}
