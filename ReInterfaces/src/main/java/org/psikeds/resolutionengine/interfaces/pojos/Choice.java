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
package org.psikeds.resolutionengine.interfaces.pojos;

import org.codehaus.jackson.annotate.JsonSubTypes;

/**
 * A general Choice, either VariantChoice and FeatureChoice
 * 
 * @author marco@juliano.de
 * 
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = FeatureChoice.class, name = "FeatureChoice"), @JsonSubTypes.Type(value = VariantChoice.class, name = "VariantChoice"), })
public abstract class Choice extends POJO {

  private static final long serialVersionUID = 1L;

  public Choice() {
    super();
  }

  public Choice(final POJO... pojos) {
    super(pojos);
  }

  public Choice(final String... ids) {
    super(ids);
  }

  public Choice(final String id) {
    super(id);
  }

  /**
   * Check whether a made Decission matches to this Choice
   * 
   * @param decission
   * @return POJO if matching, null else
   */
  public abstract POJO matches(final Decission decission);
}
