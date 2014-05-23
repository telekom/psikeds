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
package org.psikeds.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

/**
 * Helper for Writing to and Reading from JSON.
 * 
 * @author marco@juliano.de
 * 
 */
public class JSONHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(JSONHelper.class);

  public static final JSONHelper DEFAULT_JSON_HELPER = new JSONHelper();

  private boolean prettyPrinting;
  private ObjectMapper mapper;
  private ObjectWriter writer;

  public JSONHelper() {
    this(true);
  }

  public JSONHelper(final boolean prettyPrinting) {
    this(null, prettyPrinting);
  }

  public JSONHelper(final ObjectMapper mapper, final boolean prettyPrinting) {
    this(mapper, null, prettyPrinting);
  }

  public JSONHelper(final ObjectMapper mapper, final ObjectWriter writer, final boolean prettyPrinting) {
    setPrettyPrinting(prettyPrinting);
    setObjectMapper(mapper);
    setObjectWriter(writer);
  }

  // ----------------------------------------------------------------

  public boolean isPrettyPrinting() {
    return this.prettyPrinting;
  }

  public void setPrettyPrinting(final boolean prettyPrinting) {
    this.prettyPrinting = prettyPrinting;
  }

  public ObjectMapper getObjectMapper() {
    return this.mapper;
  }

  public void setObjectMapper(final ObjectMapper mapper) {
    this.mapper = (mapper != null ? mapper : new ObjectMapper());
  }

  public ObjectWriter getObjectWriter() {
    return this.writer;
  }

  public void setObjectWriter(final ObjectWriter writer) {
    this.writer = writer;
    if ((this.writer == null) && (this.mapper != null)) {
      this.writer = (this.prettyPrinting ? this.mapper.defaultPrettyPrintingWriter() : this.mapper.writer());
    }
  }

  // ----------------------------------------------------------------

  public <T> T read(final String src, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if ((type != null) && !StringUtils.isEmpty(src)) {
      obj = this.mapper.readValue(src, type);
      LOGGER.debug("Read Object from String: {}", obj);
    }
    return obj;
  }

  public <T> T read(final InputStream src, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if ((type != null) && (src != null)) {
      obj = this.mapper.readValue(src, type);
      LOGGER.debug("Read Object from Stream: {}", obj);
    }
    return obj;
  }

  public <T> T read(final File src, final Class<T> type) throws JsonProcessingException, IOException {
    T obj = null;
    if ((type != null) && (src != null) && src.isFile() && src.exists() && src.canRead()) {
      obj = this.mapper.readValue(src, type);
      LOGGER.debug("Read Object from File {}:\n{}", src, obj);
    }
    return obj;
  }

  public static <T> T readObjectFromJsonFile(final File src, final Class<T> type) throws JsonProcessingException, IOException {
    return DEFAULT_JSON_HELPER.read(src, type);
  }

  // ----------------------------------------------------------------

  public String writeValueAsString(final Object obj) throws JsonProcessingException, IOException {
    return (obj == null ? null : this.writer.writeValueAsString(obj));
  }

  public void write(final OutputStream src, final Object obj) throws JsonProcessingException, IOException {
    if ((src != null) && (obj != null)) {
      LOGGER.debug("Writing Object to Stream: {}", obj);
      this.writer.writeValue(src, obj);
    }
  }

  public void write(final File src, final Object obj) throws JsonProcessingException, IOException {
    if ((src != null) && (obj != null)) {
      LOGGER.debug("Writing Object to File {}\n{}", src, obj);
      this.writer.writeValue(src, obj);
    }
  }

  public static void writeObjectToJsonFile(final File src, final Object obj) throws JsonProcessingException, IOException {
    DEFAULT_JSON_HELPER.write(src, obj);
  }

  // ----------------------------------------------------------------

  public static String dump(final Object obj) {
    try {
      return DEFAULT_JSON_HELPER.writeValueAsString(obj);
    }
    catch (final Exception ex) {
      return ex.toString();
    }
  }

  public static void dump(final Object obj, final OutputStream out) {
    try {
      DEFAULT_JSON_HELPER.write(out, obj);
    }
    catch (final Exception ex) {
      try {
        out.write(ex.toString().getBytes());
      }
      catch (final IOException ioex) {
        // ignore, nothing to do
      }
    }
  }

  public static void print(final Object obj) {
    dump(obj, System.out);
  }
}
