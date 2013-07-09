/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate;
import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist;
import org.psikeds.queryagent.interfaces.presenter.services.ChocolateService;
import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.transformer.Transformer;

/**
 * Implementation of the ChocolateService invoking the corresponding
 * functionality of the ResolutionEngine.
 * 
 * @author marco@juliano.de
 */
public class ChocolateBusinessService implements ChocolateService {

  private ResolutionEngineClient client;

  public ChocolateBusinessService() {
    this(null);
  }

  /**
   * @param client
   */
  @Autowired
  public ChocolateBusinessService(final ResolutionEngineClient client) {
    this.client = client;
  }

  /**
   * @param client the client to set
   */
  public void setClient(final ResolutionEngineClient client) {
    this.client = client;
  }

  /**
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ChocolateService#getChocolates()
   */
  @Override
  public Chocolatelist getChocolates() {
    return Transformer.re2qa(this.client.invokeListService());
  }

  /**
   * @param refid
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ChocolateService#selectChocolate(java.lang.String)
   */
  @Override
  public Chocolate selectChocolate(final String refid) {
    return Transformer.re2qa(this.client.invokeSelectService(refid));
  }

  /**
   * @param c
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ChocolateService#addChocolate(org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate)
   */
  @Override
  public Chocolatelist addChocolate(final Chocolate c) {
    return Transformer.re2qa(this.client.invokeAddService(Transformer.qa2re(c)));
  }
}
