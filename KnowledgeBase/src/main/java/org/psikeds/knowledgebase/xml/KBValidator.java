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

import org.xml.sax.SAXException;

/**
 * Interface of the Validator for the psiKeds Knowledge Base.
 * Used by other Packages (e.g. Data Layer of Resolution Engine).
 * 
 * @author marco@juliano.de
 * 
 */
public interface KBValidator {

    /**
     * Validate XML reader against specified XSD schmema file.<br>
     * 
     * @throws SAXException if XML is not valid against XSD
     * @throws IOException if XML or XSD are not readable
     * 
     */
    void validate() throws SAXException, IOException;
}
