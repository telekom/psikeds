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
package org.psikeds.resolutionengine.datalayer.knowledgebase.validator;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;

/**
 * Helper for validating our Knowledge.
 *
 * @author marco@juliano.de
 */
public interface Validator {

  void validate(KnowledgeBase kb) throws ValidationException;
}
