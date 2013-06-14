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

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.common.exec.Callback;
import org.psikeds.common.exec.Executable;

/**
 * Generic Executable to be used to "wire" REST- and SOAP-Services to their
 * corresponding Business-Service-Implementation (= delegate). This executable
 * expects exactly one Parameter Object and passes it to the specified Method of
 * the Delegate. Type of Parameter and Type of Method's Return Value may both be
 * Null or Void.
 * 
 * Note: Every request needs its own instance of Executable and Callback. Do not
 * share them between Threads!
 * 
 * @author marco@juliano.de
 * 
 */
public class GenericExecutable implements Executable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericExecutable.class);

    private Callback callback;
    private Object delegate;
    private Class<?> delegateType;
    private String methodName;
    private Class<?> parameterType;
    private Class<?> returnType;

    /**
     * @param delegate
     * @param methodName
     */
    public GenericExecutable(final Object delegate, final String methodName) {
        this(delegate, methodName, null);
    }

    /**
     * @param delegate
     * @param methodName
     * @param parameterType
     */
    public GenericExecutable(final Object delegate, final String methodName, final Class<?> parameterType) {
        this(delegate, null, methodName, parameterType, null);
    }

    /**
     * @param delegate
     * @param delegateType
     * @param methodName
     * @param parameterType
     * @param returnType
     */
    public GenericExecutable(final Object delegate, final Class<?> delegateType, final String methodName, final Class<?> parameterType, final Class<?> returnType) {
        this.delegate = delegate;
        this.delegateType = delegateType;
        this.methodName = methodName;
        this.parameterType = parameterType;
        this.returnType = returnType;
        this.callback = null;
    }

    /**
     * @param callback
     * @see org.psikeds.common.exec.Executable#setCallback(org.psikeds.common.exec.Callback)
     */
    public void setCallback(final Callback callback) {
        this.callback = callback;
    }

    /**
     * @param delegate the delegate to set
     */
    public void setDelegate(final Object delegate) {
        this.delegate = delegate;
    }

    /**
     * @param delegateType the delegateType to set
     */
    public void setDelegateType(final Class<?> delegateType) {
        this.delegateType = delegateType;
    }

    /**
     * @param methodName the methodName to set
     */
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    /**
     * @param parameterType the parameterType to set
     */
    public void setParameterType(final Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    /**
     * @param returnType the returnType to set
     */
    public void setReturnType(final Class<?> returnType) {
        this.returnType = returnType;
    }

    /**
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            LOGGER.trace("--> GenericExecutable.run(); delegate = {}; methodName = {}", this.delegate, this.methodName);
            if (this.delegateType == null) {
                // fallback to delegates class
                this.delegateType = this.delegate.getClass();
            }
            final Object reqData = this.callback.getPayload();
            if (this.parameterType == null && reqData != null) {
                // fallback to class of payload object
                this.parameterType = reqData.getClass();
            }
            Class<?>[] parameterTypes;
            Object[] args;
            if (isVoid(this.parameterType)) {
                // no parameters
                parameterTypes = new Class<?>[0];
                args = new Object[0];
            }
            else {
                // we currently support exactly one parameter
                parameterTypes = new Class<?>[] { this.parameterType };
                args = new Object[] { this.parameterType.cast(reqData) };
            }
            final Method m = this.delegateType.getMethod(this.methodName, parameterTypes);
            if (this.returnType == null) {
                // fallback to methods declared return type
                this.returnType = m.getReturnType();
            }
            if (isVoid(this.returnType)) {
                // invoke method without return value
                m.invoke(this.delegate, args);
                this.callback.setPayload(null);
            }
            else {
                // invoke function with return value
                final Object respData = m.invoke(this.delegate, args);
                this.callback.setPayload(this.returnType.cast(respData));
            }
        }
        catch (final Exception ex) {
            this.callback.setPayload(null);
            final StringBuffer sb = new StringBuffer("Could not invoke Method ");
            sb.append(this.methodName);
            sb.append(" with Parameter ");
            sb.append(this.parameterType);
            sb.append(" on Delegate ");
            sb.append(this.delegate);
            final String str = sb.toString();
            LOGGER.debug("{}  {}", str, ex);
            throw new IllegalArgumentException(str, ex);
        }
        finally {
            // whatever happens, always signal the waiting request to resume!
            this.callback.done();
            LOGGER.trace("<-- GenericExecutable.run()");
        }
    }

    private boolean isVoid(final Class<?> c) {
        return c == null || "void".equalsIgnoreCase(c.getName());
    }
}
