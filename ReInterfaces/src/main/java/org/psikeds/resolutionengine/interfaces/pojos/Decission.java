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
 * A general Decission, either VariantDecission and FeatureDecission
 * 
 * @author marco@juliano.de
 * 
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = FeatureDecission.class, name = "FeatureDecission"), @JsonSubTypes.Type(value = VariantDecission.class, name = "VariantDecission"), })
public abstract class Decission extends POJO {

  private static final long serialVersionUID = 1L;

  public Decission() {
    super();
  }

  public Decission(final POJO... pojos) {
    super(pojos);
  }

  public Decission(final String... ids) {
    super(ids);
  }

  public Decission(final String id) {
    super(id);
  }
}
