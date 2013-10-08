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

import org.psikeds.resolutionengine.datalayer.vo.ValueObject;
import org.psikeds.resolutionengine.interfaces.pojos.POJO;

/**
 * Helper for transforming Value-Objects from the Datalayer into POJOs of
 * the RE-Interface (and vice versa).
 * 
 * @author marco@juliano.de
 *
 */
public interface Transformer {

  org.psikeds.resolutionengine.interfaces.pojos.Metadata valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Meta vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.datalayer.vo.Purpose pojo2ValueObject(
      org.psikeds.resolutionengine.interfaces.pojos.Purpose pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purpose vo);

  List<org.psikeds.resolutionengine.interfaces.pojos.Purpose> valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purposes vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.datalayer.vo.Variant pojo2ValueObject(
      org.psikeds.resolutionengine.interfaces.pojos.Variant pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant vo);

  org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant vo,
      List<org.psikeds.resolutionengine.interfaces.pojos.Feature> features);

  List<org.psikeds.resolutionengine.interfaces.pojos.Variant> valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variants vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.datalayer.vo.Feature pojo2ValueObject(
      org.psikeds.resolutionengine.interfaces.pojos.Feature pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Feature valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Feature vo);

  List<org.psikeds.resolutionengine.interfaces.pojos.Feature> valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Features vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.datalayer.vo.FeatureValueType pojo2ValueObject(
      org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType pojo);

  org.psikeds.resolutionengine.interfaces.pojos.FeatureValueType valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.FeatureValueType vo);

  // ----------------------------------------------------------------

  org.psikeds.resolutionengine.interfaces.pojos.Choice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      List<org.psikeds.resolutionengine.datalayer.vo.Variant> vars);

  org.psikeds.resolutionengine.interfaces.pojos.Choice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      org.psikeds.resolutionengine.datalayer.vo.Variants vars);

  org.psikeds.resolutionengine.interfaces.pojos.Choice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      List<org.psikeds.resolutionengine.datalayer.vo.Variant> vars);

  org.psikeds.resolutionengine.interfaces.pojos.Choice valueObject2Pojo(
      org.psikeds.resolutionengine.datalayer.vo.Variant parent,
      org.psikeds.resolutionengine.datalayer.vo.Purpose p,
      org.psikeds.resolutionengine.datalayer.vo.Variants vars);

  // ----------------------------------------------------------------

  <T extends POJO> List<T> valueObject2Pojo(List<? extends ValueObject> volst);

  <T extends ValueObject> List<T> pojo2ValueObject(List<? extends POJO> pojolst);
}
