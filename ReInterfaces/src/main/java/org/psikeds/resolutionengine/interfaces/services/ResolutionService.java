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

import org.psikeds.resolutionengine.interfaces.pojos.ResolutionRequest;
import org.psikeds.resolutionengine.interfaces.pojos.ResolutionResponse;

/**
 * Interface of the ResolutionService.
 * 
 * Must be implemented by any Business Service Implementation that shall be
 * plugged as an Delegate into the corresponding REST- or SOAP-Service.
 * 
 * Note that the actual REST- and SOAP-Service has some additional Parameters
 * like a Request-Id that will be automatically added by an CXF-Interceptor.
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
   * Create a new Resolution-Session, i.e. get a new Context, a SessionID and
   * an initial Knowledge.
   * 
   * @return ResolutionResponse
   */
  ResolutionResponse init();

  /**
   * Return the current state of Knowledge for a given Session-ID.
   * 
   * @param sessionID
   * @return ResolutionResponse
   */
  ResolutionResponse current(String sessionID);

  /**
   * Make a Decission (i.e. select a Variant for a Purpose or a Value for a
   * Feature) and request a Resolution.
   * 
   * Returns new Knowledge resulting from that Decission.
   * 
   * @param req
   * @return ResolutionResponse
   */
  ResolutionResponse select(ResolutionRequest req);
}
