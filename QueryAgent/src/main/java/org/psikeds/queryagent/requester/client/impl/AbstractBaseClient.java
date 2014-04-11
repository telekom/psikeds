/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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

  protected AbstractBaseClient(final WebClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  public WebClientFactory getClientFactory() {
    return this.clientFactory;
  }

  public void setClientFactory(final WebClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  // ----------------------------------------------------------------

  protected Response invokeService(final String url, final String httpMethod) {
    return invokeService(url, httpMethod, null, null);
  }

  protected Response invokeService(final String url, final String httpMethod, final Object body, final Class<?> requestClass) {
    Response resp = null;
    try {
      getLogger().trace("--> invokeService( {}, {}, {}, {} )", url, httpMethod, body, requestClass);
      final WebClient client = this.clientFactory.getClient(url);
      if (body == null) {
        resp = client.invoke(httpMethod, null);
      }
      else if (requestClass != null) {
        resp = client.invoke(httpMethod, requestClass.cast(body));
      }
      else {
        resp = client.invoke(httpMethod, body.getClass().cast(body));
      }
      return resp;
    }
    finally {
      getLogger().trace("<-- invokeService( {} ); Response = {}", url, resp);
    }
  }

  // ----------------------------------------------------------------

  protected boolean isJsonResponse(final Response resp) {
    return ((resp != null) && MediaType.APPLICATION_JSON_TYPE.equals(resp.getMediaType()));
  }

  protected boolean isXmlResponse(final Response resp) {
    return (((resp != null) && MediaType.TEXT_XML_TYPE.equals(resp.getMediaType())) || MediaType.APPLICATION_XML_TYPE.equals(resp.getMediaType()));
  }

  // ----------------------------------------------------------------

  protected int getStatusCode(final Response resp) {
    final StatusType stat = (resp == null ? null : resp.getStatusInfo());
    return (stat == null ? Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() : stat.getStatusCode());
  }

  protected String getReasonPhrase(final Response resp) {
    final StatusType stat = (resp == null ? null : resp.getStatusInfo());
    return (stat == null ? Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase() : stat.getReasonPhrase());
  }

  protected boolean isOK(final Response resp) {
    return (getStatusCode(resp) == Response.Status.OK.getStatusCode());
  }

  protected boolean isError(final Response resp) {
    return !isOK(resp);
  }

  // ----------------------------------------------------------------

  protected <T> T getContent(final Response resp, final Class<T> responseClass) {
    T content = null;
    InputStream stream = null;
    try {
      getLogger().trace("--> getContent( {}, {} )", resp, responseClass);
      if (responseClass == Response.class) {
        // shortcut
        content = responseClass.cast(resp);
      }
      else {
        final Object entity = (resp == null ? null : resp.getEntity());
        if (entity instanceof InputStream) {
          // we have an raw stream --> unmarshall
          stream = (InputStream) entity;
          if (isJsonResponse(resp)) {
            content = parseJsonContent(stream, responseClass);
          }
          else {
            throw new IOException("Unsupported MediaType!");
          }
        }
        else {
          // no stream --> try to cast
          content = responseClass.cast(entity);
        }
      }
      return content;
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
          getLogger().debug("Could not close Input Stream.", ioex);
        }
        finally {
          stream = null;
        }
      }
      getLogger().trace("<-- getContent(); Content = {}", content);
    }
  }

  private <T> T parseJsonContent(final InputStream stream, final Class<T> responseClass) throws JsonParseException, IOException {
    T json = null;
    try {
      getLogger().trace("--> parseJsonContent(); Class = {}", responseClass);
      final MappingJsonFactory factory = new MappingJsonFactory();
      final JsonParser parser = factory.createJsonParser(stream);
      json = parser.readValueAs(responseClass);
      return json;
    }
    finally {
      getLogger().trace("<-- parseJsonContent(); JSON = {}", json);
    }
  }

  // ----------------------------------------------------------------

  protected abstract Logger getLogger();
}
