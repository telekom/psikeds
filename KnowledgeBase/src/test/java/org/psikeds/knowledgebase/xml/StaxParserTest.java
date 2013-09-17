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
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.SAXException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

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
  private static final String XML = RESOURCE_PATH + "kb.xml";

  private static final String ENCODING = "UTF-8";

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XSDValidator}
   */
  @Test
  public void testXsdValidatorWithFilenames() {
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
   * {@link org.psikeds.knowledgebase.xml.impl.XSDValidator}
   */
  @Test
  public void testXsdValidatorWithSpringResources() {
    try {
      final Resource xsd = new FileSystemResource(XSD);
      final Resource xml = new FileSystemResource(XML);
      final KBValidator validator = new XSDValidator(xsd, xml);
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
   * {@link org.psikeds.knowledgebase.xml.impl.XMLParser}
   */
  @Test
  public void testXmlParserWithFileAndDefaultSettings() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    final KBParser parser = new XMLParser(XML, ENCODING, tcbh);
    long numElems = 0;
    try {
      numElems = parser.parseXmlElements();
    }
    catch (final Exception ex) {
      // ex.printStackTrace();
      fail("Parsing failed: " + ex.getMessage());
    }
    assertEquals("We expected just 1 XML-Element (RootElement) but got " + numElems, 1, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XMLParser}
   */
  @Test
  public void testXmlParserWithSpringResourceAndDefaultSettings() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    final Resource xml = new FileSystemResource(XML);
    final KBParser parser = new XMLParser(xml, ENCODING, tcbh);
    long numElems = 0;
    try {
      numElems = parser.parseXmlElements();
    }
    catch (final Exception ex) {
      // ex.printStackTrace();
      fail("Parsing failed: " + ex.getMessage());
    }
    assertEquals("We expected just 1 XML-Element (RootElement) but got " + numElems, 1, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XMLParser#parseXmlElements()}
   */
  @Test
  public void testXmlParserWithInputStreamAndRootElement() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    InputStream xmlIS = null;
    long numElems = 0;
    try {
      xmlIS = new FileInputStream(XML);
      final XMLParser parser = new XMLParser(xmlIS, ENCODING, tcbh);
      parser.setNumOfSkippedElements(1); // skip root element
      numElems = parser.parseXmlElements();
    }
    catch (final Exception ex) {
      // ex.printStackTrace();
      fail("Parsing failed: " + ex.getMessage());
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
    assertEquals("We expected 2 XML-Elements (Meta and Data) but got " + numElems, 2, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }

  /**
   * Test method for
   * {@link org.psikeds.knowledgebase.xml.impl.XMLParser#parseXmlElements()}
   */
  @Test
  public void testXmlParserWithFileAndSkippingElements() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    final XMLParser parser = new XMLParser();
    parser.setXmlFilename(XML);
    parser.setCallbackHandler(tcbh);
    parser.setNumOfSkippedElements(8);
    long numElems = 0;
    try {
      numElems = parser.parseXmlElements();
    }
    catch (final Exception ex) {
      // ex.printStackTrace();
      fail("Parsing failed: " + ex.getMessage());
    }
    assertEquals(7, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }
}
