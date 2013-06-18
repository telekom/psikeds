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
package org.psikeds.common.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.i18n.UncheckedException;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.common.reqid.RequestIdGenerator;
import org.psikeds.common.util.LoggingHelper;

/**
 * Interceptor for generating Request-Ids and passing them to the invoked
 * Service via CXF-Message-Id, HTTP-Header or Request-Parameter. Suitable for
 * both SOAP- and REST-Services.
 * 
 * @author marco@juliano.de
 * 
 */
public class RequestIdGenerationInterceptor<T extends Message> extends AbstractPhaseInterceptor<T> {

    private static final String DEFAULT_REQUEST_ID_HTTP_HEADER = "psiKeds-Request-Id";

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestIdGenerationInterceptor.class);

    private RequestIdGenerator generator;
    private boolean readRequestIdFromHttpHeader;
    private String nameOfRequestIdttpHeader;
    private boolean writeRequestIdToHttpHeader;
    private boolean readRequestIdFromCxfMsgId;
    private boolean writeRequestIdToCxfMsgId;
    private boolean addRequestIdToParameters;

    public RequestIdGenerationInterceptor() {
        this(null);
    }

    @Autowired
    public RequestIdGenerationInterceptor(final RequestIdGenerator generator) {
        this(Phase.PRE_INVOKE, generator);
    }

    public RequestIdGenerationInterceptor(final String phase, final RequestIdGenerator generator) {
        super(phase);
        this.generator = generator;
        this.readRequestIdFromHttpHeader = false;
        this.nameOfRequestIdttpHeader = DEFAULT_REQUEST_ID_HTTP_HEADER;
        this.writeRequestIdToHttpHeader = false;
        this.readRequestIdFromCxfMsgId = false;
        this.writeRequestIdToCxfMsgId = true;
        this.addRequestIdToParameters = true;
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(final RequestIdGenerator generator) {
        this.generator = generator;
    }

    /**
     * @param appendRequestIdToParameters the appendRequestIdToParameters to set
     */
    public void setAddRequestIdToParameters(final boolean addRequestIdToParameters) {
        this.addRequestIdToParameters = addRequestIdToParameters;
    }

    /**
     * @param readRequestIdFromHttpHeader the readRequestIdFromHttpHeader to
     *            set
     */
    public void setReadRequestIdFromHttpHeader(final boolean readRequestIdFromHttpHeader) {
        this.readRequestIdFromHttpHeader = readRequestIdFromHttpHeader;
    }

    /**
     * @param nameOfRequestIdttpHeader the nameOfRequestIdttpHeader to set
     */
    public void setNameOfRequestIdttpHeader(final String nameOfRequestIdttpHeader) {
        this.nameOfRequestIdttpHeader = nameOfRequestIdttpHeader;
    }

    /**
     * @param writeRequestIdToHttpHeader the writeRequestIdToHttpHeader to
     *            set
     */
    public void setWriteRequestIdToHttpHeader(final boolean writeRequestIdToHttpHeader) {
        this.writeRequestIdToHttpHeader = writeRequestIdToHttpHeader;
    }

    /**
     * @param readRequestIdFromCxfMsgId the readRequestIdFromCxfMsgId to set
     */
    public void setReadRequestIdFromCxfMsgId(final boolean readRequestIdFromCxfMsgId) {
        this.readRequestIdFromCxfMsgId = readRequestIdFromCxfMsgId;
    }

    /**
     * @param writeRequestIdToCxfMsgId the writeRequestIdToCxfMsgId to set
     */
    public void setWriteRequestIdToCxfMsgId(final boolean writeRequestIdToCxfMsgId) {
        this.writeRequestIdToCxfMsgId = writeRequestIdToCxfMsgId;
    }

    /**
     * @param message
     * @throws Fault
     * 
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     * 
     */
    public void handleMessage(final T message) throws UncheckedException {
        try {
            if (message != null) {
                String reqid = null;

                // First: Try to get Request-Id from HTTP-Header (Not
                // recommended, could be a security issue)
                if (this.readRequestIdFromHttpHeader) {
                    reqid = getRequestIdFromHTTPHeader(message);
                }
                // Second: Try to get Request-Id from CXF-Message-ID (Not
                // recommended, not unique)
                if (this.readRequestIdFromCxfMsgId && StringUtils.isEmpty(reqid)) {
                    reqid = getCxfMessageId(message);
                }
                // Third: Generate a new Request-Id (Recommended, unique and
                // secure!)
                if (StringUtils.isEmpty(reqid)) {
                    reqid = generateNewRequestId();
                }

                // Initialize MDC
                LoggingHelper.init(reqid);

                // First: Write it into the CXF-Message (Recommended for
                // Logging/Debugging)
                if (this.writeRequestIdToCxfMsgId) {
                    setCxfMessageId(message, reqid);
                }
                // Second: Write it into the HTTP-Headers (Ok, but not
                // Recommended)
                if (this.writeRequestIdToHttpHeader) {
                    setRequestIdAsHTTPHeader(message, reqid);
                }
                // Third: Append it to the Service Parameters (Recommended!)
                if (this.addRequestIdToParameters) {
                    addRequestIdToParams(message, reqid);
                }
            }
        }
        catch (final Exception ex) {
            LOGGER.error("Could not handle Message: {}", ex.getMessage());
            if (isREST(message)) {
                throw new UncheckedException(ex);
            }
            else {
                throw new Fault(ex);
            }
        }
    }

    private boolean isREST(final T message) {
        final Object obj = message == null ? null : message.get(Message.REST_MESSAGE);
        return obj instanceof Boolean && (Boolean) obj;
    }

    private String generateNewRequestId() {
        final String reqid = this.generator == null ? null : this.generator.getNextReqId();
        LOGGER.debug("Generated a new Request-Id: {}", reqid);
        return reqid;
    }

    private String getRequestIdFromHTTPHeader(final T message) {
        String reqid = null;
        if (!StringUtils.isEmpty(this.nameOfRequestIdttpHeader)) {
            final Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
            if (headers != null) {
                final List<String> rih = headers.get(this.nameOfRequestIdttpHeader);
                final int len = rih == null ? 0 : rih.size();
                if (len > 0) {
                    // same http header could exist several times
                    // we simply read the first one in that case
                    reqid = rih.get(0);
                    LOGGER.debug("Got existing Request-Id from HTTP-Header: {}", reqid);
                }
            }
        }
        return reqid;
    }

    private void setRequestIdAsHTTPHeader(final T message, final String reqid) {
        if (!StringUtils.isEmpty(this.nameOfRequestIdttpHeader) && !StringUtils.isEmpty(reqid)) {
            Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
            if (headers == null) {
                headers = new HashMap<String, List<String>>();
            }
            List<String> rih = headers.get(this.nameOfRequestIdttpHeader);
            if (rih == null) {
                rih = new ArrayList<String>();
            }
            if (rih.isEmpty()) {
                LOGGER.debug("Adding new HTTP-Header: {} = {}", this.nameOfRequestIdttpHeader, reqid);
                rih.add(reqid);
            }
            else {
                LOGGER.debug("Replacing existing HTTP-Header: {} = {}", this.nameOfRequestIdttpHeader, reqid);
                // same http header could exist several times
                // we simply replace the first one in that case
                rih.set(0, reqid);
            }
            headers.put(this.nameOfRequestIdttpHeader, rih);
            message.put(Message.PROTOCOL_HEADERS, headers);
        }
    }

    private String getCxfMessageId(final T message) {
        String msgid = null;
        if (message != null) {
            final Exchange exch = message.getExchange();
            final Object obj = exch == null ? null : exch.get(LoggingMessage.ID_KEY);
            msgid = obj == null ? message.getId() : String.valueOf(obj);
        }
        LOGGER.debug("Got CXF-Message-Id: {}", msgid);
        return msgid;
    }

    private void setCxfMessageId(final T message, final String msgid) {
        if (message != null && !StringUtils.isEmpty(msgid)) {
            message.setId(msgid);
            final Exchange exch = message.getExchange();
            if (exch != null) {
                exch.put(LoggingMessage.ID_KEY, msgid);
            }
            LOGGER.trace("Overwriting CXF-Message-Id.");
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addRequestIdToParams(final T message, final String reqid) {
        List parameters = message.getContent(List.class);
        if (parameters == null) {
            parameters = new ArrayList<String>();
        }
        if (parameters.isEmpty()) {
            LOGGER.trace("Adding new Parameter for Request-Id.");
            parameters.add(reqid);
        }
        else {
            LOGGER.trace("Replacing last Parameter with Request-Id.");
            parameters.set(parameters.size() - 1, reqid);
        }
        message.setContent(List.class, parameters);
    }
}
