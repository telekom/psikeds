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
package org.psikeds.resolutionengine.util;

import java.util.List;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.vo.Relation;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValue;
import org.psikeds.resolutionengine.interfaces.pojos.FeatureValues;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * Helper for handling Relations
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class RelationHelper {

  private RelationHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static KnowledgeEntities findRoot(final Relation r, final Knowledge knowledge) {
    return KnowledgeHelper.findRoot(r, knowledge);
  }

  public static KnowledgeEntity getTargetEntity(final KnowledgeEntity root, final List<String> ctx) {
    return ContextHelper.walkContextPath(root, ctx);
  }

  public static org.psikeds.resolutionengine.datalayer.vo.FeatureValue getConstantFeatureValue(final KnowledgeBase kb, final RelationParameter rp) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.getConstantFeatureValue(kb, rp);
  }

  public static org.psikeds.resolutionengine.datalayer.vo.Feature getFeatureVariable(final KnowledgeBase kb, final RelationParameter rp) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.getFeatureVariable(kb, rp);
  }

  public static RelationOperator getComplementaryOperator(final RelationOperator rp) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.getComplementaryOperator(rp);
  }

  public static boolean fulfillsOperation(final org.psikeds.resolutionengine.datalayer.vo.FeatureValue left, final RelationOperator op,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValue right) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(left, op, right);
  }

  public static List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> fulfillsOperation(final List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> left, final RelationOperator op,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValue right) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(left, op, right);
  }

  public static List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> fulfillsOperation(final org.psikeds.resolutionengine.datalayer.vo.FeatureValue left, final RelationOperator op,
      final List<org.psikeds.resolutionengine.datalayer.vo.FeatureValue> right) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(left, op, right);
  }

  // ----------------------------------------------------------------

  public static boolean fulfillsOperation(final KnowledgeBase kb, final FeatureValue left, final RelationOperator op, final org.psikeds.resolutionengine.datalayer.vo.FeatureValue ref) {
    return org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(FeatureValueHelper.pojo2ValueObject(kb, left), op, ref);
  }

  public static FeatureValues fulfillsOperation(final KnowledgeBase kb, final Transformer trans,
      final FeatureValues left, final RelationOperator op, final org.psikeds.resolutionengine.datalayer.vo.FeatureValue ref) {
    return trans.valueObject2Pojo(org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(FeatureValueHelper.pojo2ValueObject(kb, left), op, ref));
  }

  public static FeatureValues fulfillsOperation(final KnowledgeBase kb, final Transformer trans,
      final org.psikeds.resolutionengine.datalayer.vo.FeatureValue ref, final RelationOperator op, final FeatureValues right) {
    return trans.valueObject2Pojo(org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(ref, op, FeatureValueHelper.pojo2ValueObject(kb, right)));
  }

  public static FeatureValues fulfillsOperation(final KnowledgeBase kb, final Transformer trans, final FeatureValues left, final RelationOperator op, final FeatureValues ref) {
    return trans.valueObject2Pojo(org.psikeds.resolutionengine.datalayer.knowledgebase.util.RelationHelper.fulfillsOperation(FeatureValueHelper.pojo2ValueObject(kb, left), op,
        FeatureValueHelper.pojo2ValueObject(kb, ref)));
  }
}
