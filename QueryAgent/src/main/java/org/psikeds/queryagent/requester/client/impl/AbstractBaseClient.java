/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.requester.client.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.slf4j.Logger;

import org.apache.cxf.jaxrs.client.WebClient;

import org.psikeds.queryagent.requester.client.WebClientFactory;

/**
 * @author marco@juliano.de
 */
public abstract class AbstractBaseClient {

  protected WebClientFactory clientFactory;

  public AbstractBaseClient(final WebClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  /**
   * @param clientFactory the clientFactory to set
   */
  public void setClientFactory(final WebClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  // -------------------------------------------------------------

  protected <T> T invokeService(final String url, final String httpMethod, final Class<T> responseClass) {
    return invokeService(url, httpMethod, null, null, responseClass);
  }

  protected <T> T invokeService(final String url, final String httpMethod, final Object body, final Class<?> requestClass, final Class<T> responseClass) {
    try {
      getLogger().trace("--> invokeService({}, {}, {}, {}, {})", url, httpMethod, body, requestClass, responseClass);
      final WebClient client = this.clientFactory.getClient(url);
      Response resp = null;
      if (body == null) {
        resp = client.invoke(httpMethod, null);
      }
      else if (requestClass != null) {
        resp = client.invoke(httpMethod, requestClass.cast(body));
      }
      else {
        resp = client.invoke(httpMethod, body.getClass().cast(body));
      }
      return getContent(resp, responseClass);
    }
    finally {
      getLogger().trace("<-- invokeService({}, {}, {}, {}, {})", url, httpMethod, body, requestClass, responseClass);
    }
  }

  private <T> T getContent(final Response resp, final Class<T> valueType) {
    InputStream stream = null;
    try {
      getLogger().trace("--> getContent({}, {})", resp, valueType);
      final Object obj = resp == null ? null : resp.getEntity();
      if (!(obj instanceof InputStream)) {
        throw new IllegalStateException("Could not get content, no input stream!");
      }
      stream = (InputStream) obj;
      if (isJsonResponse(resp)) {
        return parseJsonContent(stream, valueType);
      }
      else if (isXmlResponse(resp)) {
        return parseXmlContent(stream, valueType);
      }
      else {
        throw new IOException("Unsupported MediaType!");
      }
    }
    catch (final JsonParseException jpex) {
      throw new IllegalStateException("Cannot parse JSON Repsonse.", jpex);
    }
    catch (final IOException ioex) {
      throw new IllegalStateException("Cannot get Content from Input Stream.", ioex);
    }
    finally {
      if (stream != null) {
        try {
          stream.close();
        }
        catch (final IOException ioex) {
          getLogger().debug("Could not close input stream.", ioex);
        }
        finally {
          stream = null;
        }
      }
      getLogger().trace("<-- getContent({}, {})", resp, valueType);
    }
  }

  private <T> T parseJsonContent(final InputStream stream, final Class<T> valueType) throws JsonParseException, IOException {
    T content = null;
    try {
      getLogger().trace("--> parseJsonContent({}, {})", stream, valueType);
      final MappingJsonFactory factory = new MappingJsonFactory();
      final JsonParser parser = factory.createJsonParser(stream);
      content = parser.readValueAs(valueType);
      return content;
    }
    finally {
      getLogger().trace("<-- parseJsonContent({}, {}); JSON-Content:\n{}", stream, valueType, content);
    }
  }

  private <T> T parseXmlContent(final InputStream stream, final Class<T> valueType) {
    final T content = null;
    try {
      getLogger().trace("--> parseXmlContent({}, {})", stream, valueType);
      // TODO: implement
      return content;
    }
    finally {
      getLogger().trace("<-- parseXmlContent({}, {}); XML-Content:\n{}", stream, valueType, content);
    }
  }

  // -------------------------------------------------------------

  protected boolean isJsonResponse(final Response resp) {
    if (resp == null) {
      return false;
    }
    return MediaType.APPLICATION_JSON_TYPE.equals(resp.getMediaType());
  }

  protected boolean isXmlResponse(final Response resp) {
    if (resp == null) {
      return false;
    }
    return MediaType.TEXT_XML_TYPE.equals(resp.getMediaType()) || MediaType.APPLICATION_XML_TYPE.equals(resp.getMediaType());
  }

  protected String getResponseStatus(final Response resp) {
    String status = null;
    try {
      getLogger().trace("--> getResponseStatus({})", resp);
      final StringBuilder sb = new StringBuilder("Response = ");
      if (resp != null) {
        final StatusType stat = resp.getStatusInfo();
        if (stat != null) {
          sb.append(stat.getStatusCode());
          sb.append(' ');
          sb.append(stat.getReasonPhrase());
        }
        sb.append(" (Length: ");
        sb.append(resp.getLength());
        sb.append(" Bytes) (Type: ");
        sb.append(resp.getMediaType());
        sb.append(')');
      }
      status = sb.toString();
      return status;
    }
    finally {
      getLogger().trace("<-- getResponseStatus({})\n{}", resp, status);
    }
  }

  protected abstract Logger getLogger();
}
