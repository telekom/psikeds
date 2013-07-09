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

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

/**
 * Interface of the Parser for the psiKeds Knowledge Base.
 * Used by other Packages (e.g. Data Layer of Resolution Engine).
 * 
 * @author marco@juliano.de
 */
public interface KBParser {

  /**
   * Parse the specified XML.
   * 
   * @return Total number of found XML elements
   * @throws XMLStreamException
   * @throws SAXException
   * @throws JAXBException
   * @throws IOException
   */
  long parseXmlElements() throws XMLStreamException, SAXException, JAXBException, IOException;

  /**
   * Retrieve the Callback-Object registered for handling Parsing-Events of
   * this Parser.
   * 
   * @return the callbackHandler
   */
  KBParserCallback getCallbackHandler();

  /**
   * Register a Callback-Object for handling Parsing-Events of this Parser.
   * 
   * @param callbackHandler the callbackHandler to set
   */
  void setCallbackHandler(final KBParserCallback callbackHandler);
}
