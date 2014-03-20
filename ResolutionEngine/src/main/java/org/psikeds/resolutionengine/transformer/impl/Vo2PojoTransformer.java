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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.Features;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for transforming Value-Objects from the Datalayer
 * into POJOs of the RE-Interface.
 * 
 * Note: For Safety-Reasons we will never transform POJOs
 * (from the Client!) back into Value-Objects (Server-Data!)
 * 
 * @author marco@juliano.de
 */
public class Vo2PojoTransformer implements Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Vo2PojoTransformer.class);

  // ----------------------------------------------------------------

  @Override
  public Metadata valueObject2Pojo(final MetaData vo) {
    Metadata pojo = null;
    if (vo != null) {
      pojo = new Metadata();
      final Calendar created = (vo.getLastmodified() == null ? vo.getCreated() : vo.getLastmodified());
      if (created != null) {
        pojo.addInfo(Metadata.KB_CREATED, DateFormat.getDateTimeInstance().format(created.getTime()));
      }
      if (vo.getLoaded() != null) {
        pojo.addInfo(Metadata.KB_LOADED, DateFormat.getDateTimeInstance().format(vo.getLoaded().getTime()));
      }
      if (!StringUtils.isEmpty(vo.getLanguage())) {
        pojo.addInfo(Metadata.KB_LANGUAGE, vo.getLanguage());
      }
      if (!StringUtils.isEmpty(vo.getVersion())) {
        pojo.addInfo(Metadata.KB_VERSION, vo.getVersion());
      }
      pojo.addInfo(vo.getAdditionalInfo());
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(final Purpose vo) {
    return (vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(vo.getLabel(), vo.getDescription(), vo.getPurposeID(), vo.isRoot()));
  }

  @Override
  public List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> valueObject2Pojo(final Purposes vo) {
    List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> pojo = null;
    if (vo != null) {
      pojo = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Purpose>();
      final List<Purpose> lst = vo.getPurpose();
      for (final Purpose p : lst) {
        pojo.add(valueObject2Pojo(p));
      }
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureDescription valueObject2Pojo(final Feature<?> vo) {
    return (vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.FeatureDescription(vo.getLabel(), vo.getDescription(), vo.getFeatureID(), vo.getValueType()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Features valueObject2Pojo(final Features vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Features pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Features();
      final List<Feature<?>> lst = vo.getFeature();
      for (final Feature<?> f : lst) {
        pojo.add(valueObject2Pojo(f));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(final Variant parent, final Feature<?> feature) {
    return valueObject2Pojo((parent == null ? null : parent.getVariantID()), feature);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(final String parentVariantID, final Feature<?> feature) {
    return (feature == null ? null : valueObject2Pojo(parentVariantID, feature.getFeatureID(), feature.getValuesAsStrings()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(final String parentVariantID, final String featureID, final List<String> possibleValues) {
    return new org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice(parentVariantID, featureID, possibleValues);
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValue valueObject2Pojo(final Feature<?> feature, final org.psikeds.resolutionengine.interfaces.pojos.FeatureDecission decission) {
    return new org.psikeds.resolutionengine.interfaces.pojos.FeatureValue(valueObject2Pojo(feature), decission);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValue valueObject2Pojo(final Feature<?> feature, final String value) {
    return new org.psikeds.resolutionengine.interfaces.pojos.FeatureValue(valueObject2Pojo(feature), value);
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final Variant vo) {
    return valueObject2Pojo(vo, (Features) null);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final Variant vo, final Features features) {
    return valueObject2Pojo(vo, valueObject2Pojo(features));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final Variant vo, final org.psikeds.resolutionengine.interfaces.pojos.Features features) {
    return vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Variant(vo.getLabel(), vo.getDescription(), vo.getVariantID(), features);
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(final Purpose p, final List<org.psikeds.resolutionengine.interfaces.pojos.Variant> variants, final long qty) {
    return valueObject2Pojo(null, p, variants, qty);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(final String parentVariantID, final org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      final List<org.psikeds.resolutionengine.interfaces.pojos.Variant> variants, final long qty) {
    final org.psikeds.resolutionengine.interfaces.pojos.Purpose pojoPurpose = valueObject2Pojo(p);
    return new VariantChoice(parentVariantID, pojoPurpose, variants, qty);
  }
}
