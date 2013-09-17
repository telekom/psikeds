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
package org.psikeds.resolutionengine.transformer;

/**
 * Helper for transforming Value Objects from the Datalayer into POJOs of the
 * Interface (and vice versa).
 * 
 * @author marco@juliano.de
 */
public interface Transformer {

  org.psikeds.resolutionengine.datalayer.vo.Alternatives pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Alternatives pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Alternatives valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Alternatives vo);

  org.psikeds.resolutionengine.datalayer.vo.Constituents pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Constituents pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Constituents valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Constituents vo);

  org.psikeds.resolutionengine.datalayer.vo.Constitutes pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Constitutes pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Constitutes valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Constitutes vo);

  org.psikeds.resolutionengine.datalayer.vo.Event pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Event pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Event valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Event vo);

  org.psikeds.resolutionengine.datalayer.vo.Events pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Events pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Events valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Events vo);

  org.psikeds.resolutionengine.datalayer.vo.Feature pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Feature pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Feature valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Feature vo);

  org.psikeds.resolutionengine.datalayer.vo.Features pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Features pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Features valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Features vo);

  org.psikeds.resolutionengine.datalayer.vo.Fulfills pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Fulfills pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Fulfills valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Fulfills vo);

  org.psikeds.resolutionengine.datalayer.vo.Purpose pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Purpose pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Purpose valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Purpose vo);

  org.psikeds.resolutionengine.datalayer.vo.Purposes pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Purposes pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Purposes valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Purposes vo);

  org.psikeds.resolutionengine.datalayer.vo.Rule pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Rule pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Rule valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Rule vo);

  org.psikeds.resolutionengine.datalayer.vo.Rules pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Rules pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Rules valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Rules vo);

  org.psikeds.resolutionengine.datalayer.vo.Variant pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Variant pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Variant valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Variant vo);

  org.psikeds.resolutionengine.datalayer.vo.Variants pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Variants pojo);

  org.psikeds.resolutionengine.interfaces.pojos.Variants valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Variants vo);
}
