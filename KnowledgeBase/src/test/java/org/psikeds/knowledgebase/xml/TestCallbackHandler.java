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

import java.util.List;

import org.psikeds.knowledgebase.jaxb.Chocolate;
import org.psikeds.knowledgebase.jaxb.Chocolatelist;

/**
 * Simple callback handler used by
 * {@link org.psikeds.knowledgebase.xml.impl.XMLParser} for testing purposes.
 * 
 * @author m.juliano
 */
public class TestCallbackHandler implements KBParserCallback {

  public long counter;

  public TestCallbackHandler() {
    this.counter = 0;
  }

  /**
   * Count and print every XML element found by the XMLParser.
   * 
   * @see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object)
   */
  @Override
  public void handleElement(final Object element) {
    this.counter++;
    System.out.println(this.counter + ".: " + element);
    if (element != null) {
      if (element instanceof Chocolatelist) {
        final Chocolatelist chocolst = (Chocolatelist) element;
        printChocolateList(chocolst);
      }
      else if (element instanceof Chocolate) {
        final Chocolate choco = (Chocolate) element;
        printChocolate(choco);
      }
      else {
        System.out.println("  Unknown XML object: " + element);
      }
    }
  }

  private void printChocolateList(final Chocolatelist chocolst) {
    final List<Chocolate> lst = chocolst.getChocolate();
    System.out.println("  Found list of " + lst.size() + " chocolates.");
    for (final Chocolate choco : lst) {
      printChocolate(choco);
    }
  }

  private void printChocolate(final Chocolate choco) {
    System.out.println("    " + choco.getChocokey() + " = " + choco.getDescription());
  }
}
