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
package org.psikeds.resolutionengine.interfaces.services;

import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;

/**
 * Interface of the ResolutionService.
 *
 * Must be implemented by any Business Service Implementation that shall be
 * plugged as an Delegate into the corresponding REST- or SOAP-Service.
 *
 * Note that the actual REST- and SOAP-Service can have additional Parameters
 * like a Request-Id that will be automatically added by the CXF-Interceptor.
 * Therefore the REST- or SOAP-Implementations can but don't have to implement
 * this Interface.
 *
 * However any Client invoking the Service should use this Interface to create
 * its Service-Call.
 *
 * @author marco@juliano.de
 *
 */
public interface ResolutionService {

  /**
   * Get init Resolution-Session, get a SessionID and initial KnowledgeEntity.
   * 
   * @return InitResponse
   */
  InitResponse init();

  /**
   * Select either a Variant or a Purpose. Return new KnowledgeEntity
   * based on that decission
   *
   * @param req
   * @return SelectResponse
   */
  SelectResponse select(SelectRequest req);
}
