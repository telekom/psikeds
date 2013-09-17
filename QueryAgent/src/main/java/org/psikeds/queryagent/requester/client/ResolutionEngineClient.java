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
package org.psikeds.queryagent.requester.client;

import org.psikeds.resolutionengine.interfaces.pojos.InitResponse;
import org.psikeds.resolutionengine.interfaces.pojos.SelectRequest;
import org.psikeds.resolutionengine.interfaces.pojos.SelectResponse;

/**
 * A client invoking the Services of the Resolution-Engine. The actual
 * implementation can choose how to do this (REST, SOAP, Mocking, ...)
 *
 * @author marco@juliano.de
 */
public interface ResolutionEngineClient {

  InitResponse invokeInitService();

  SelectResponse invokeSelectService(SelectRequest req);
}
