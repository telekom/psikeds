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

import java.io.IOException;
import java.io.OutputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 * Simple helper dumping an Object-Tree to JSON.
 *
 * @author marco@juliano.de
 */
public class ObjectDumper {

  private final ObjectWriter writer;

  public ObjectDumper() {
    this(new ObjectMapper(), true);
  }

  public ObjectDumper(final ObjectMapper om, final boolean prettyPrinting) {
    this(prettyPrinting ? om.defaultPrettyPrintingWriter() : om.writer());
  }

  public ObjectDumper(final ObjectWriter ow) {
    this.writer = ow;
  }

  public String dump(final Object obj) {
    try {
      return this.writer.writeValueAsString(obj);
    }
    catch (final Exception ex) {
      return ex.toString();
    }
  }

  public void dump(final Object obj, final OutputStream out) {
    try {
      this.writer.writeValue(out, obj);
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

  public void print(final Object obj) {
    dump(obj, System.out);
  }
}
