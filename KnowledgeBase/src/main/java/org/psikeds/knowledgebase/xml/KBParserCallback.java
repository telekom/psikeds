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

/**
 * This interface must be implemented by all callback handlers. The XML-Parser
 * {@link org.psikeds.knowledgebase.xml.KBParser} will invoke handleElement()
 * for every XML-Element.
 * 
 * @author marco@juliano.de
 */
public interface KBParserCallback {

  /**
   * Will be invoked by the XML-Parser for every XML-Element.<br>
   * <b>Note:</b>Element is a complete JAXB-Object-Structure
   * representing not only the current XML-Element but also
   * the Content-Tree of all its Sub-Elements!<br>
   * 
   * @param element The unmarshalled XML-Element / JAXB-Object.
   */
  void handleElement(final Object element);
}
