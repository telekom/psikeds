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
package org.psikeds.knowledgebase.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.psikeds.common.util.ObjectDumper;

/**
 * Simple callback handler counting XML-Elements and printing them to the Log. Used by
 * {@link org.psikeds.knowledgebase.xml.impl.XMLParser} for testing purposes.
 * 
 * @author marco@juliano.de
 * 
 */
public class TestCallbackHandler implements KBParserCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestCallbackHandler.class);

  private final ObjectDumper dumper = new ObjectDumper();

  public long counter = 0;

  /**
   * Count and print every XML element found by the XMLParser.
   * 
   * @see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object)
   * 
   */
  @Override
  public void handleElement(final Object element) {
    this.counter++;
    final StringBuilder sb = new StringBuilder();
    sb.append(this.counter);
    sb.append(".: ");
    sb.append(this.dumper.dump(element));
    sb.append('\n');
    LOGGER.debug(sb.toString());
  }
}
