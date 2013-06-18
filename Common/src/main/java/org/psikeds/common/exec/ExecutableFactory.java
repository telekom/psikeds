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
package org.psikeds.common.exec;

/**
 * Factory creating an Executable for a given Service Name and
 * based on a given Delegate.
 * 
 * @author marco@juliano.de
 * 
 */
public interface ExecutableFactory {

    Executable getExecutable(Object delegate, String serviceName);
}
