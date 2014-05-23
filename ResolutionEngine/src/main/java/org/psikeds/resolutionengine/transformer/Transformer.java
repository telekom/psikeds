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
package org.psikeds.resolutionengine.transformer;

import java.util.List;

/**
 * Helper for transforming Value-Objects from the Datalayer
 * into POJOs of the RE-Interface.
 * 
 * Note: For Safety-Reasons we will never transform POJOs
 * (from the Client!) back into Value-Objects (Server-Data!)
 * 
 * @author marco@juliano.de
 */
public interface Transformer {

  org.psikeds.resolutionengine.interfaces.pojos.Metadata valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.MetaData vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purpose vo);

  List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purposes vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant vo);

  org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant vo,
      org.psikeds.resolutionengine.datalayer.vo.Features features);

  org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant vo,
      org.psikeds.resolutionengine.interfaces.pojos.Features features);

  org.psikeds.resolutionengine.interfaces.pojos.Variants valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variants vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.Concept valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Concept vo);

  org.psikeds.resolutionengine.interfaces.pojos.Concepts valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Concepts vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValue valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.FeatureValue vo);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValues valueObject2Pojo(
      List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> vo);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValues valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.FeatureValues vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.Feature valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Feature vo);

  org.psikeds.resolutionengine.interfaces.pojos.Features valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Features vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      org.psikeds.resolutionengine.datalayer.vo.FeatureValue value);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> values);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      org.psikeds.resolutionengine.datalayer.vo.FeatureValues values);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      String parentVariantID,
      org.psikeds.resolutionengine.datalayer.vo.FeatureValue value);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      String parentVariantID,
      org.psikeds.resolutionengine.datalayer.vo.FeatureValues values);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureChoice valueObject2Pojo(
      String parentVariantID,
      List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> values);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      org.psikeds.resolutionengine.interfaces.pojos.Variants variants,
      long qty);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      org.psikeds.resolutionengine.datalayer.vo.Variants variants,
      long qty);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      String parentVariantID,
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      org.psikeds.resolutionengine.interfaces.pojos.Variants variants,
      long qty);

  org.psikeds.resolutionengine.interfaces.pojos.VariantChoice valueObject2Pojo(
      String parentVariantID,
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      org.psikeds.resolutionengine.datalayer.vo.Variants variants,
      long qty);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      org.psikeds.resolutionengine.datalayer.vo.Concept con);

  org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      org.psikeds.resolutionengine.datalayer.vo.Concepts cons);

  org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      String parentVariantID,
      org.psikeds.resolutionengine.datalayer.vo.Concept con);

  org.psikeds.resolutionengine.interfaces.pojos.ConceptChoice valueObject2Pojo(
      String parentVariantID,
      org.psikeds.resolutionengine.datalayer.vo.Concepts cons);
}
