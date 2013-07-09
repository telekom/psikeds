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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.SAXException;

import org.psikeds.knowledgebase.xml.impl.XMLParser;
import org.psikeds.knowledgebase.xml.impl.XSDValidator;

/**
 * Test case for classes {@link org.psikeds.knowledgebase.xml.impl.XMLParser}
 * and {@link org.psikeds.knowledgebase.xml.impl.XSDValidator}
 * 
 * @author marco@juliano.de
 */
public class StaxParserTest {

  private static final String RESOURCE_PATH = "./src/main/resources/";
  private static final String XSD = RESOURCE_PATH + "kb.xsd";
  private static final String XML = RESOURCE_PATH + "example.xml";

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XSDValidator#validate()}
   */
  @Test
  public void testXsdValidator() {
    try {
      final KBValidator validator = new XSDValidator(XSD, XML);
      validator.validate();
      System.out.println("XML " + XML + " is valid against XSD " + XSD);
    }
    catch (final SAXException saxex) {
      // saxex.printStackTrace();
      fail("Invalid XML: " + saxex.getMessage());
    }
    catch (final IOException ioex) {
      // ioex.printStackTrace();
      fail("Could not validate XML: " + ioex.getMessage());
    }
  }

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XMLParser#parseXmlElements()}
   */
  @Test
  public void testXmlParserWithFileAndDefaultSettings() {
    final KBParserCallback tcbh = new TestCallbackHandler();
    final KBParser parser = new XMLParser(XML, tcbh);
    long numElems = 0;
    try {
      numElems = parser.parseXmlElements();
    }
    catch (final XMLStreamException xmlex) {
      // xmlex.printStackTrace();
      fail(xmlex.getMessage());
    }
    catch (final SAXException saxex) {
      // saxex.printStackTrace();
      fail(saxex.getMessage());
    }
    catch (final JAXBException jaxbex) {
      // jaxbex.printStackTrace();
      fail(jaxbex.getMessage());
    }
    catch (final IOException ioex) {
      // ioex.printStackTrace();
      fail(ioex.getMessage());
    }
    assertEquals(numElems, 4);
    assertEquals(numElems, ((TestCallbackHandler) tcbh).counter);
  }

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XMLParser#parseXmlElements()}
   */
  @Test
  public void testXmlParserWithInputStreamAndRootElement() {
    final KBParserCallback tcbh = new TestCallbackHandler();
    InputStream xmlIS = null;
    long numElems = 0;
    try {
      xmlIS = new FileInputStream(XML);
      final XMLParser parser = new XMLParser(xmlIS, "UTF-8", tcbh);
      parser.setNumOfSkippedElements(0);
      numElems = parser.parseXmlElements();
    }
    catch (final FileNotFoundException fnfex) {
      // fnfex.printStackTrace();
      fail(fnfex.getMessage());
    }
    catch (final XMLStreamException xmlex) {
      // xmlex.printStackTrace();
      fail(xmlex.getMessage());
    }
    catch (final SAXException saxex) {
      // saxex.printStackTrace();
      fail(saxex.getMessage());
    }
    catch (final JAXBException jaxbex) {
      // jaxbex.printStackTrace();
      fail(jaxbex.getMessage());
    }
    catch (final IOException ioex) {
      // ioex.printStackTrace();
      fail(ioex.getMessage());
    }
    finally {
      if (xmlIS != null) {
        try {
          xmlIS.close();
        }
        catch (final IOException ioex) {
          // ignore
        }
        finally {
          xmlIS = null;
        }
      }
    }
    assertEquals(numElems, 1);
    assertEquals(numElems, ((TestCallbackHandler) tcbh).counter);
  }
}
