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
package org.psikeds.resolutionengine.datalayer.knowledgebase.transformer;

/**
 * Helper for transforming a JAXB XML Object from the Knowledgebase into a
 * Value Object for the Datalayer.<br>
 * 
 * The Knowledgebase is read-only, therefore only transformations XML to VO
 * are supported / allowed.<br>
 * 
 * @author marco@juliano.de
 */
public interface Transformer {

  org.psikeds.resolutionengine.datalayer.vo.Alternatives xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Alternatives xml);

  org.psikeds.resolutionengine.datalayer.vo.Constituents xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Constituents xml);

  org.psikeds.resolutionengine.datalayer.vo.Constituents xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Derivations xml);

  org.psikeds.resolutionengine.datalayer.vo.Event xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Event xml);

  org.psikeds.resolutionengine.datalayer.vo.Events xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Events xml);

  org.psikeds.resolutionengine.datalayer.vo.Feature xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Sensor xml);

  org.psikeds.resolutionengine.datalayer.vo.Fulfills xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Fulfills xml);

  org.psikeds.resolutionengine.datalayer.vo.MetaData xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Meta xml);

  org.psikeds.resolutionengine.datalayer.vo.Purpose xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Purpose xml);

  org.psikeds.resolutionengine.datalayer.vo.Purposes xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Purposes xml);

  org.psikeds.resolutionengine.datalayer.vo.RelationParameter xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Parameter xml);

  org.psikeds.resolutionengine.datalayer.vo.RelationOperator xml2ValueObject(final org.psikeds.knowledgebase.jaxb.RelationType xml);

  String xml2ValueObject(final org.psikeds.knowledgebase.jaxb.RelParamType xml);

  org.psikeds.resolutionengine.datalayer.vo.Rule xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Rule xml);

  org.psikeds.resolutionengine.datalayer.vo.Rules xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Rules xml);
}
