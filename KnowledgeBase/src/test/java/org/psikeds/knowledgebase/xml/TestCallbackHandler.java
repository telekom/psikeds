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
package org.psikeds.knowledgebase.xml;

import org.psikeds.common.util.ObjectDumper;

/**
 * Simple callback handler counting XML-Elements and dumping them to StdOut.
 * Used by {@link org.psikeds.knowledgebase.xml.impl.XMLParser} for testing
 * purposes.
 * 
 * @author marco@juliano.de
 * 
 */
public class TestCallbackHandler implements KBParserCallback {

  long counter;
  ObjectDumper dumper;

  public TestCallbackHandler() {
    this.counter = 0;
    this.dumper = new ObjectDumper();
  }

  /**
   * Count and print every XML element found by the XMLParser.
   * 
   * @see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object)
   * 
   */
  @Override
  public void handleElement(final Object element) {
    this.counter++;
    System.out.println(this.counter + ".: " + element);
    this.dumper.print(element);
    System.out.println();
  }
}
