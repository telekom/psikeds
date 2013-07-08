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

import org.psikeds.common.exec.Callback;

/**
 * @author marco@juliano.de
 * 
 */
public class CallbackImpl implements Callback {

    protected String reqId;
    protected Object payload;
    protected boolean finished;

    public CallbackImpl(final String reqId, final Object payload) {
        this.reqId = reqId;
        this.payload = payload;
        this.finished = false;
    }

    /**
     * @return
     * @see org.psikeds.common.exec.Callback#getReqId()
     */
    @Override
    public String getReqId() {
        return this.reqId;
    }

    /**
     * @return
     * @see org.psikeds.common.exec.Callback#getPayload()
     */
    @Override
    public Object getPayload() {
        return this.payload;
    }

    /**
     * @param payload
     * @see org.psikeds.common.exec.Callback#setPayload(java.lang.Object)
     */
    @Override
    public void setPayload(final Object payload) {
        this.payload = payload;
    }

    /**
     * @see org.psikeds.common.exec.Callback#done()
     */
    @Override
    public void done() {
        this.finished = true;
    }

    /**
     * @return
     * @see org.psikeds.common.exec.Callback#isFinished()
     */
    @Override
    public boolean isFinished() {
        return this.finished;
    }
}
