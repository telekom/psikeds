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

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
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
  public Metadata valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.MetaData vo) {
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
      if (!StringUtils.isEmpty(vo.getRelease())) {
        pojo.addInfo(Metadata.KB_VERSION, vo.getRelease());
      }
      if (!StringUtils.isEmpty(vo.getName())) {
        pojo.addInfo(Metadata.KB_NAME, vo.getName());
      }
      if (!StringUtils.isEmpty(vo.getId())) {
        pojo.addInfo(Metadata.KB_NAME, vo.getId());
      }
      pojo.addInfo(vo.getAdditionalInfo());
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Purpose vo) {
    return (vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Purpose(vo.getLabel(), vo.getDescription(), vo.getPurposeID(), vo.isRoot()));
  }

  @Override
  public List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Purposes vo) {
    List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> pojo = null;
    if (vo != null) {
      pojo = new ArrayList<org.psikeds.resolutionengine.interfaces.pojos.Purpose>();
      final List<org.psikeds.resolutionengine.datalayer.vo.Purpose> lst = vo.getPurpose();
      for (final org.psikeds.resolutionengine.datalayer.vo.Purpose p : lst) {
        pojo.add(valueObject2Pojo(p));
      }
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Variant vo) {
    return valueObject2Pojo(vo, (org.psikeds.resolutionengine.interfaces.pojos.Features) null, (org.psikeds.resolutionengine.interfaces.pojos.Concepts) null);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant vo,
      final org.psikeds.resolutionengine.datalayer.vo.Features features,
      final org.psikeds.resolutionengine.datalayer.vo.Concepts concepts) {
    return valueObject2Pojo(vo, valueObject2Pojo(features), valueObject2Pojo(concepts));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant vo,
      final org.psikeds.resolutionengine.interfaces.pojos.Features features,
      final org.psikeds.resolutionengine.interfaces.pojos.Concepts concepts) {
    org.psikeds.resolutionengine.interfaces.pojos.Variant pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Variant(vo.getLabel(), vo.getDescription(), vo.getVariantID(), features, concepts);
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Variants valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Variants vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Variants pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Variants();
      final List<org.psikeds.resolutionengine.datalayer.vo.Variant> lst = vo.getVariant();
      for (final org.psikeds.resolutionengine.datalayer.vo.Variant v : lst) {
        pojo.add(valueObject2Pojo(v));
      }
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Concept valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Concept vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Concept pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Concept(vo.getLabel(), vo.getDescription(), vo.getConceptID(), null, valueObject2Pojo(vo.getValues()));
      pojo.addFeatureIds(vo.getFeatureIds()); // need a copy, not a reference!
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Concepts valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Concepts vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Concepts pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Concepts();
      final List<org.psikeds.resolutionengine.datalayer.vo.Concept> lst = vo.getConcept();
      for (final org.psikeds.resolutionengine.datalayer.vo.Concept c : lst) {
        pojo.add(valueObject2Pojo(c));
      }
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValue valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.FeatureValue vo) {
    return (vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.FeatureValue(vo.getFeatureID(), vo.getFeatureValueID(), vo.getValue()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValues valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.FeatureValues vo) {
    return (vo == null ? null : valueObject2Pojo(vo.getValue()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureValues valueObject2Pojo(final List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> vo) {
    org.psikeds.resolutionengine.interfaces.pojos.FeatureValues pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.FeatureValues();
      for (final org.psikeds.resolutionengine.datalayer.vo.FeatureValue fv : vo) {
        pojo.add(valueObject2Pojo(fv));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Feature valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Feature vo) {
    // IF and DL currently use the same Constants for Types, so we can just use it without conversion
    return (vo == null ? null : new org.psikeds.resolutionengine.interfaces.pojos.Feature(vo.getLabel(), vo.getDescription(), vo.getFeatureID(), vo.getType(), vo.getUnit()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.Features valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Features vo) {
    org.psikeds.resolutionengine.interfaces.pojos.Features pojo = null;
    if (vo != null) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.Features();
      final List<org.psikeds.resolutionengine.datalayer.vo.Feature> lst = vo.getFeature();
      for (final org.psikeds.resolutionengine.datalayer.vo.Feature f : lst) {
        pojo.add(valueObject2Pojo(f));
      }
      LOGGER.trace("valueObject2Pojo: vo = {}\n--> pojo = {}", vo, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValue value) {
    return ((parent == null) ? null : valueObject2Pojo(parent.getVariantID(), value));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      final String parentVariantID,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValue value) {
    org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice pojo = null;
    if (!StringUtils.isEmpty(parentVariantID) && (value != null)) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice(parentVariantID, valueObject2Pojo(value));
      LOGGER.trace("valueObject2Pojo: parentVariantID = {}\nvalue = {}\n--> pojo = {}", parentVariantID, value, pojo);
    }
    return pojo;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValues values) {
    return ((values == null) ? null : valueObject2Pojo(parent, values.getValue()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      final List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> values) {
    return ((parent == null) ? null : valueObject2Pojo(parent.getVariantID(), values));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      final String parentVariantID,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValues values) {
    return ((values == null) ? null : valueObject2Pojo(parentVariantID, values.getValue()));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      final String parentVariantID,
      final List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> values) {
    org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice pojo = null;
    if (!StringUtils.isEmpty(parentVariantID) && (values != null)) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice(parentVariantID);
      pojo.addPossibleValues(valueObject2Pojo(values));
      LOGGER.trace("valueObject2Pojo: parentVariantID = {}\nvalues = {}\n--> pojo = {}", parentVariantID, values, pojo);
    }
    return pojo;
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      final org.psikeds.resolutionengine.interfaces.pojos.Variants variants,
      final long qty) {
    return valueObject2Pojo(null, p, variants, qty);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      final org.psikeds.resolutionengine.datalayer.vo.Variants variants,
      final long qty) {
    return valueObject2Pojo(p, valueObject2Pojo(variants), qty);
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      final String parentVariantID,
      final org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      final org.psikeds.resolutionengine.interfaces.pojos.Variants variants,
      final long qty) {
    org.psikeds.resolutionengine.interfaces.pojos.VariantChoice pojo = null;
    if ((p != null) && (variants != null)) {
      pojo = new org.psikeds.resolutionengine.interfaces.pojos.VariantChoice(parentVariantID, valueObject2Pojo(p), variants, qty);
      LOGGER.trace("valueObject2Pojo: parentVariantID = {}\nPurpose = {}\nVariants = {}\n--> pojo = {}", parentVariantID, p, variants, pojo);
    }
    return pojo;
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      final String parentVariantID,
      final org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      final org.psikeds.resolutionengine.datalayer.vo.Variants variants,
      final long qty) {
    return valueObject2Pojo(parentVariantID, p, valueObject2Pojo(variants), qty);
  }

  // ----------------------------------------------------------------

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      final org.psikeds.resolutionengine.datalayer.vo.Concept con) {
    return (parent == null ? null : valueObject2Pojo(parent.getVariantID(), con));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      final org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      final org.psikeds.resolutionengine.datalayer.vo.Concepts cons) {
    return (parent == null ? null : valueObject2Pojo(parent.getVariantID(), cons));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      final String parentVariantID,
      final org.psikeds.resolutionengine.datalayer.vo.Concept con) {
    return ((StringUtils.isEmpty(parentVariantID) || (con == null)) ? null : new org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice(parentVariantID, valueObject2Pojo(con)));
  }

  @Override
  public org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      final String parentVariantID,
      final org.psikeds.resolutionengine.datalayer.vo.Concepts cons) {
    return ((StringUtils.isEmpty(parentVariantID) || (cons == null)) ? null : new org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice(parentVariantID, valueObject2Pojo(cons)));
  }
}
