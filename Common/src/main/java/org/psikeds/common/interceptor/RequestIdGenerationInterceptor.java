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
package org.psikeds.common.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;
import org.apache.cxf.common.i18n.UncheckedException;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.util.LoggingHelper;

/**
 * Interceptor for generating Request-Ids and passing them to the invoked
 * Service via CXF-Message-Id, HTTP-Header or Request-Parameter. Suitable for
 * both SOAP- and REST-Services.
 * 
 * @author marco@juliano.de
 * 
 */
public class RequestIdGenerationInterceptor<T extends Message> extends AbstractPhaseInterceptor<T> implements InitializingBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestIdGenerationInterceptor.class);

  public static final String DEFAULT_REQ_ID_HTTP_HEADER = "psiKeds-Request-Id";
  public static final String DEFAULT_INTERCEPTOR_PHASE = Phase.PRE_INVOKE;
  public static final boolean DEFAULT_READ_REQ_ID_FROM_HTTP_HEADER = false;
  public static final boolean DEFAULT_WRITE_REQ_ID_TO_HTTP_HEADER = false;
  public static final boolean DEFAULT_READ_REQ_ID_FROM_CXF_MSG_ID = false;
  public static final boolean DEFAULT_WRITE_REQ_ID_TO_CXF_MSG_ID = true;
  // Setting this to false, i.e. adding Req-Id not to the
  // Request-Parameters will most probably result in errors 
  public static final boolean DEFAULT_ADD_REQ_ID_TO_PARAMS = true;

  private IdGenerator generator;
  private String nameOfRequestIdHttpHeader;
  private boolean readRequestIdFromHttpHeader;
  private boolean writeRequestIdToHttpHeader;
  private boolean readRequestIdFromCxfMsgId;
  private boolean writeRequestIdToCxfMsgId;
  private boolean addRequestIdToParameters;

  public RequestIdGenerationInterceptor() {
    this(null);
  }

  public RequestIdGenerationInterceptor(final IdGenerator generator) {
    this(DEFAULT_INTERCEPTOR_PHASE, generator);
  }

  public RequestIdGenerationInterceptor(final String phase, final IdGenerator generator) {
    super(phase);
    this.generator = generator;
    this.nameOfRequestIdHttpHeader = DEFAULT_REQ_ID_HTTP_HEADER;
    this.readRequestIdFromHttpHeader = DEFAULT_READ_REQ_ID_FROM_HTTP_HEADER;
    this.writeRequestIdToHttpHeader = DEFAULT_WRITE_REQ_ID_TO_HTTP_HEADER;
    this.readRequestIdFromCxfMsgId = DEFAULT_READ_REQ_ID_FROM_CXF_MSG_ID;
    this.writeRequestIdToCxfMsgId = DEFAULT_WRITE_REQ_ID_TO_CXF_MSG_ID;
    this.addRequestIdToParameters = DEFAULT_ADD_REQ_ID_TO_PARAMS;
  }

  // ------------------------------------------------------

  public IdGenerator getGenerator() {
    return this.generator;
  }

  public void setGenerator(final IdGenerator generator) {
    this.generator = generator;
  }

  public boolean isReadRequestIdFromHttpHeader() {
    return this.readRequestIdFromHttpHeader;
  }

  public void setReadRequestIdFromHttpHeader(final boolean readRequestIdFromHttpHeader) {
    this.readRequestIdFromHttpHeader = readRequestIdFromHttpHeader;
  }

  public String getNameOfRequestIdHttpHeader() {
    return this.nameOfRequestIdHttpHeader;
  }

  public void setNameOfRequestIdHttpHeader(final String nameOfRequestIdHttpHeader) {
    this.nameOfRequestIdHttpHeader = nameOfRequestIdHttpHeader;
  }

  public boolean isWriteRequestIdToHttpHeader() {
    return this.writeRequestIdToHttpHeader;
  }

  public void setWriteRequestIdToHttpHeader(final boolean writeRequestIdToHttpHeader) {
    this.writeRequestIdToHttpHeader = writeRequestIdToHttpHeader;
  }

  public boolean isReadRequestIdFromCxfMsgId() {
    return this.readRequestIdFromCxfMsgId;
  }

  public void setReadRequestIdFromCxfMsgId(final boolean readRequestIdFromCxfMsgId) {
    this.readRequestIdFromCxfMsgId = readRequestIdFromCxfMsgId;
  }

  public boolean isWriteRequestIdToCxfMsgId() {
    return this.writeRequestIdToCxfMsgId;
  }

  public void setWriteRequestIdToCxfMsgId(final boolean writeRequestIdToCxfMsgId) {
    this.writeRequestIdToCxfMsgId = writeRequestIdToCxfMsgId;
  }

  public boolean isAddRequestIdToParameters() {
    return this.addRequestIdToParameters;
  }

  public void setAddRequestIdToParameters(final boolean addRequestIdToParameters) {
    this.addRequestIdToParameters = addRequestIdToParameters;
  }

  //------------------------------------------------------

  /**
   * Check that RequestIdGenerationInterceptor was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(this.generator, "No Request-ID-Generator!");
  }

  //------------------------------------------------------

  /**
   * @param message
   * @throws UncheckedException
   * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
   */
  @Override
  public void handleMessage(final T message) throws UncheckedException {
    try {
      if (message != null) {
        String reqid = null;

        // First: Try to get Request-Id from HTTP-Header
        // (Not recommended, could be a security issue)
        if (this.readRequestIdFromHttpHeader) {
          reqid = getRequestIdFromHTTPHeader(message);
        }
        // Second: Try to get Request-Id from CXF-Message-ID
        // (Not recommended, not unique)
        if (this.readRequestIdFromCxfMsgId && StringUtils.isEmpty(reqid)) {
          reqid = getCxfMessageId(message);
        }
        // Third: Generate a new Request-Id
        // (Recommended, unique and secure!)
        if (StringUtils.isEmpty(reqid)) {
          reqid = generateNewRequestId();
        }

        // Initialize MDC
        LoggingHelper.init(reqid);

        // First: Write it into the CXF-Message
        // (Recommended for Logging/Debugging)
        if (this.writeRequestIdToCxfMsgId) {
          setCxfMessageId(message, reqid);
        }
        // Second: Write it into the HTTP-Headers
        // (Ok, but not recommended)
        if (this.writeRequestIdToHttpHeader) {
          setRequestIdAsHTTPHeader(message, reqid);
        }
        // Third: Append it to the Service Parameters
        // (Recommended!)
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

  //------------------------------------------------------

  private boolean isREST(final T message) {
    final Object obj = message == null ? null : message.get(Message.REST_MESSAGE);
    return (obj instanceof Boolean) && (Boolean) obj;
  }

  private String generateNewRequestId() {
    final String reqid = this.generator == null ? null : this.generator.getNextId();
    LOGGER.debug("Generated a new Request-Id: {}", reqid);
    return reqid;
  }

  private String getRequestIdFromHTTPHeader(final T message) {
    String reqid = null;
    if (!StringUtils.isEmpty(this.nameOfRequestIdHttpHeader)) {
      final Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
      if (headers != null) {
        final List<String> rih = headers.get(this.nameOfRequestIdHttpHeader);
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
    if (!StringUtils.isEmpty(this.nameOfRequestIdHttpHeader) && !StringUtils.isEmpty(reqid)) {
      Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
      if (headers == null) {
        headers = new HashMap<String, List<String>>();
      }
      List<String> rih = headers.get(this.nameOfRequestIdHttpHeader);
      if (rih == null) {
        rih = new ArrayList<String>();
      }
      if (rih.isEmpty()) {
        LOGGER.debug("Adding new HTTP-Header: {} = {}", this.nameOfRequestIdHttpHeader, reqid);
        rih.add(reqid);
      }
      else {
        LOGGER.debug("Replacing existing HTTP-Header: {} = {}", this.nameOfRequestIdHttpHeader, reqid);
        // same http header could exist several times
        // we simply replace the first one in that case
        rih.set(0, reqid);
      }
      headers.put(this.nameOfRequestIdHttpHeader, rih);
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
    if ((message != null) && !StringUtils.isEmpty(msgid)) {
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
