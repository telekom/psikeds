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

import org.apache.cxf.continuations.Continuation;

import org.psikeds.common.exec.Callback;

/**
 * @author marco@juliano.de
 * 
 */
public class AsyncCallbackImpl extends CallbackImpl implements Callback {

    protected Continuation continuation;

    public AsyncCallbackImpl(final String name, final Continuation continuation, final Object payload) {
        super(name, payload);
        // Callback has reference to Continuation and vice versa
        this.continuation = continuation;
        this.continuation.setObject(this);
    }

    /**
     * @see org.psikeds.common.exec.Callback#done()
     */
    @Override
    public void done() {
        synchronized (this.continuation) {
            this.finished = true;
            // signal continuation/request to wake up
            this.continuation.resume();
        }
    }
}
