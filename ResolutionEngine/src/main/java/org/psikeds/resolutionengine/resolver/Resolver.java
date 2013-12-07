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
package org.psikeds.resolutionengine.resolver;

import org.psikeds.resolutionengine.interfaces.pojos.Decission;
import org.psikeds.resolutionengine.interfaces.pojos.Knowledge;
import org.psikeds.resolutionengine.interfaces.pojos.Metadata;
import org.psikeds.resolutionengine.rules.RulesAndEventsHandler;

/**
 * The chain of all Resolvers is our actual Resolution-Engine, i.e. this
 * is where the logical Resolution is performed.
 * 
 * @author marco@juliano.de
 */
public interface Resolver {

  /**
   * Resolve the "old" Knowledge based on the made Decission and the
   * supplied Metadata. Returns the resulting "new" Knowledge.
   * 
   * Note: Any Resolver-Implementation must expect that it is also invoked
   * without Client-Interaction and that therefore the Decission can be null
   * (e.g. for Initial Knowledge or for Recursive-Auto-Completion)
   * 
   * @param knowledge
   *          current/old Knowledge
   * @param decission
   *          made Decission (can be null)
   * @param raeh
   *          all Rules and Events currently relevant
   * @param metadata
   *          optional Metadata (can be null)
   * @return Knowledge resulting new Knowledge
   * @throws ResolutionException
   *           if any error occurs
   */
  Knowledge resolve(
      Knowledge knowledge,
      Decission decission,
      RulesAndEventsHandler raeh,
      Metadata metadata) throws ResolutionException;

}
