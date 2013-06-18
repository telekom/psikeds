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
package org.psikeds.common.exec.impl;

import org.psikeds.common.exec.Executable;
import org.psikeds.common.exec.ExecutableFactory;

/**
 * Implementation of the ExecutableFactory always returning a new Instance of
 * the GenericExecutable.
 * 
 * @author marco@juliano.de
 * 
 */
public class GenericExecutableFactory implements ExecutableFactory {

    /**
     * @param delegate
     * @param serviceName
     * @return GenericExecutable
     * @see org.psikeds.common.exec.ExecutableFactory#getExecutable(java.lang.Object,
     *      java.lang.String)
     */
    public Executable getExecutable(final Object delegate, final String serviceName) {
        return new GenericExecutable(delegate, serviceName);
    }
}
