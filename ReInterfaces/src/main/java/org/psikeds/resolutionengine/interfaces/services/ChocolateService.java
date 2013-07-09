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
package org.psikeds.resolutionengine.interfaces.services;

import org.psikeds.resolutionengine.interfaces.pojos.Chocolate;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist;

/**
 * Interface of the ChocolateService. Must be implemented by any Business
 * Service Implementation that shall be plugged as an Delegate into the
 * corresponding REST- or SOAP-Service.
 * Note that the actual REST- and SOAP-Service can have additional Parameters
 * like a Request-Id that will be automatically added by the CXF-Interceptor.
 * Therefore the REST- or SOAP-Implementations can but don't have to implement
 * this Interface.
 * However any Client invoking the Service should use this Interface to create
 * its Service-Call.
 * 
 * @author marco@juliano.de
 */
public interface ChocolateService {

  /**
   * Get List of all Chocolates
   * 
   * @return Chocolatelist
   */
  Chocolatelist getChocolates();

  /**
   * Get one Chocolate selected by the specified reference key.
   * 
   * @param refid
   * @return Chocolate
   */
  Chocolate selectChocolate(String refid);

  /**
   * Add the specified Chocolate to the List of Chocolates.
   * 
   * @param c Chocolate
   * @return Chocolatelist New List of all Chocolates including the new
   *         Chocolate
   */
  Chocolatelist addChocolate(Chocolate choco);
}
