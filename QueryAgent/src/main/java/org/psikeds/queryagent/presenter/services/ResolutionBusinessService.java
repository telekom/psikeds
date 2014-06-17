/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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

import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest;
import org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionResponse;
import org.psikeds.queryagent.interfaces.presenter.services.ResolutionService;
import org.psikeds.queryagent.requester.client.ResolutionEngineClient;
import org.psikeds.queryagent.transformer.Transformer;
import org.psikeds.queryagent.transformer.impl.Re2QaTransformer;

/**
 * Implementation of the ResolutionService invoking the corresponding
 * functionality of the ResolutionEngine by calling a ResolutionEngineClient.
 * 
 * @author marco@juliano.de
 */
public class ResolutionBusinessService implements ResolutionService {

  private static final Transformer DEFAULT_TRANSFORMER = new Re2QaTransformer();
  private ResolutionEngineClient client;
  private Transformer trans;

  public ResolutionBusinessService() {
    this(null, DEFAULT_TRANSFORMER);
  }

  public ResolutionBusinessService(final ResolutionEngineClient client, final Transformer trans) {
    this.client = client;
    this.trans = trans;
  }

  public void setClient(final ResolutionEngineClient client) {
    this.client = client;
  }

  public ResolutionEngineClient getClient() {
    return this.client;
  }

  public Transformer getTransformer() {
    if (this.trans == null) {
      this.trans = DEFAULT_TRANSFORMER;
    }
    return this.trans;
  }

  public void setTransformer(final Transformer trans) {
    this.trans = trans;
  }

  /**
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#init()
   */
  @Override
  public ResolutionResponse init() {
    return getTransformer().re2qa(getClient().invokeInitService());
  }

  /**
   * @param sessionID
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#current(java.lang.String)
   */
  @Override
  public ResolutionResponse current(final String sessionID) {
    return getTransformer().re2qa(getClient().invokeCurrentService(sessionID));
  }

  /**
   * @param req
   *          ResolutionRequest
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#select(org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse select(final ResolutionRequest req) {
    return getTransformer().re2qa(getClient().invokeSelectService(getTransformer().qa2re(req)));
  }

  /**
   * @param req
   *          ResolutionRequest
   * @return ResolutionResponse
   * @see org.psikeds.queryagent.interfaces.presenter.services.ResolutionService#predict(org.psikeds.queryagent.interfaces.presenter.pojos.ResolutionRequest)
   */
  @Override
  public ResolutionResponse predict(final ResolutionRequest req) {
    return getTransformer().re2qa(getClient().invokePredictionService(getTransformer().qa2re(req)));
  }
}
