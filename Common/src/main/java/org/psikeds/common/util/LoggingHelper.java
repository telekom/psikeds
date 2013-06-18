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
package org.psikeds.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.apache.commons.lang.StringUtils;

/**
 * Helper for setting up the Mapped Diagnostic Context of the Logging System.
 * 
 * REQ = The current Request-Id, initialized by CXF-Interceptor
 * org.psikeds.common.interceptor.RequestIdGenerationInterceptor
 * 
 * SRVC = Name of the Target SOAP- or REST-Service
 * 
 * CTX = additional context information
 * 
 * @author marco@juliano.de
 * 
 */
public final class LoggingHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingHelper.class);

    private static final String MDC_KEY_REQUEST_ID = "REQ";
    private static final String MDC_KEY_SERVICE_NAME = "SRVC";
    private static final String MDC_KEY_ADDITIONAL_CONTEXT = "CTX";

    // ------------------------------------------------------------

    public static void init() {
        init(null);
    }

    public static void init(final String requestId) {
        init(requestId, null);
    }

    public static void init(final String requestId, final String serviceName) {
        init(requestId, serviceName, null);
    }

    public static void init(final String requestId, final String serviceName, final String additionalContext) {
        clear();
        setRequestId(requestId);
        setServiceName(serviceName);
        setAdditionalContext(additionalContext);
    }

    public static void clear() {
        LOGGER.trace("MDC-->clear()");
        MDC.clear();
    }

    // ------------------------------------------------------------

    public static void setRequestId(final String requestId) {
        if (!StringUtils.isEmpty(requestId)) {
            LOGGER.trace("MDC-->{} = {}", MDC_KEY_REQUEST_ID, requestId);
            MDC.put(MDC_KEY_REQUEST_ID, requestId);
        }
    }

    public static void setServiceName(final String serviceName) {
        if (!StringUtils.isEmpty(serviceName)) {
            LOGGER.trace("MDC-->{} = {}", MDC_KEY_SERVICE_NAME, serviceName);
            MDC.put(MDC_KEY_SERVICE_NAME, serviceName);
        }
    }

    public static void setAdditionalContext(final String additionalContext) {
        if (!StringUtils.isEmpty(additionalContext)) {
            LOGGER.trace("MDC-->{} = {}", MDC_KEY_ADDITIONAL_CONTEXT, additionalContext);
            MDC.put(MDC_KEY_ADDITIONAL_CONTEXT, additionalContext);
        }
    }

    // ------------------------------------------------------------

    private LoggingHelper() {
        // prevent instantiation
    }
}
