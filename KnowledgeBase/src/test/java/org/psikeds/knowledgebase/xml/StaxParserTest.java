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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.psikeds.knowledgebase.xml.impl.XMLParser;
import org.psikeds.knowledgebase.xml.impl.XSDValidator;

/**
 * Test case for classes {@link org.psikeds.knowledgebase.xml.impl.XMLParser} and
 * {@link org.psikeds.knowledgebase.xml.impl.XSDValidator}
 * 
 * @author marco@juliano.de
 */
public class StaxParserTest {

  private static final String LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "../ResolutionEngine/src/main/resources/log4j.xml");
  private static final Logger LOGGER = LoggerFactory.getLogger(StaxParserTest.class);

  private static final String RESOURCE_PATH = "./src/main/resources/";
  private static final String XSD = System.getProperty("org.psikeds.test.kb.xsd", RESOURCE_PATH + "kb.xsd");
  private static final String XML = System.getProperty("org.psikeds.test.kb.xml", RESOURCE_PATH + "kb.xml");

  private static final String ENCODING = System.getProperty("org.psikeds.test.encoding", "UTF-8");

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  /**
   * Test method for {@link org.psikeds.knowledgebase.xml.impl.XSDValidator}
   */
  @Test
  public void testXsdValidatorWithFilenames() {
    try {
      LOGGER.info("Validating XML " + XML + " against XSD " + XSD + " using Filenames ...");
      final KBValidator validator = new XSDValidator(XSD, XML);
      validator.validate();
      LOGGER.info("... done. XML " + XML + " is valid against XSD " + XSD);
    }
    catch (final SAXException saxex) {
      final String message = "Invalid XML: " + saxex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
    catch (final IOException ioex) {
      final String message = "Could not validate XML: " + ioex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
  }

  /**
   * Test method for {@link org.psikeds.knowledgebase.xml.impl.XSDValidator}
   */
  @Test
  public void testXsdValidatorWithSpringResources() {
    try {
      LOGGER.info("Validating XML " + XML + " against XSD " + XSD + " using Spring-Resources ...");
      final Resource xsd = new FileSystemResource(XSD);
      final Resource xml = new FileSystemResource(XML);
      final KBValidator validator = new XSDValidator(xsd, xml);
      validator.validate();
      LOGGER.info("... done. XML " + XML + " is valid against XSD " + XSD);
    }
    catch (final SAXException saxex) {
      final String message = "Invalid XML: " + saxex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
    catch (final IOException ioex) {
      final String message = "Could not validate XML: " + ioex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
  }

  /**
   * Test method for {@link org.psikeds.knowledgebase.xml.impl.XMLParser}
   */
  @Test
  public void testXmlParserWithFileAndDefaultSettings() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    long numElems = 0;
    try {
      LOGGER.info("Parsing XML " + XML + " using Filename ...");
      final KBParser parser = new XMLParser(XML, ENCODING, tcbh);
      numElems = parser.parseXmlElements();
      LOGGER.info("... done.");
    }
    catch (final Exception ex) {
      final String message = "XML Parsing failed: " + ex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
    assertEquals("We expected just 1 XML-Element (RootElement) but got " + numElems, 1, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }

  /**
   * Test method for {@link org.psikeds.knowledgebase.xml.impl.XMLParser}
   */
  @Test
  public void testXmlParserWithSpringResourceAndDefaultSettings() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    long numElems = 0;
    try {
      LOGGER.info("Parsing XML " + XML + " using Spring-Resource ...");
      final Resource xml = new FileSystemResource(XML);
      final KBParser parser = new XMLParser(xml, ENCODING, tcbh);
      numElems = parser.parseXmlElements();
      LOGGER.info("... done.");
    }
    catch (final Exception ex) {
      final String message = "XML Parsing failed: " + ex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
    assertEquals("We expected just 1 XML-Element (RootElement) but got " + numElems, 1, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }

  /**
   * Test method for {@link org.psikeds.knowledgebase.xml.impl.XMLParser#parseXmlElements()}
   */
  @Test
  public void testXmlParserWithInputStreamAndRootElement() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    InputStream xmlIS = null;
    long numElems = 0;
    try {
      LOGGER.info("Parsing XML " + XML + " using Inputstream ...");
      xmlIS = new FileInputStream(XML);
      final XMLParser parser = new XMLParser(xmlIS, ENCODING, tcbh);
      parser.setNumOfSkippedElements(1); // skip root element
      numElems = parser.parseXmlElements();
      LOGGER.info("... done.");
    }
    catch (final Exception ex) {
      final String message = "XML Parsing failed: " + ex.getMessage();
      LOGGER.info(message);
      fail(message);
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
    final long expected = 2;
    assertEquals("We expected " + expected + " XML-Elements (Meta and Data) but got " + numElems, expected, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }

  /**
   * Test method for {@link org.psikeds.knowledgebase.xml.impl.XMLParser#parseXmlElements()}
   */
  @Test
  public void testXmlParserWithFileAndSkippingElements() {
    final TestCallbackHandler tcbh = new TestCallbackHandler();
    final int numSkipped = 10; // skip all metadata and data-element
    final long expected = 7; // count elements within data, i.e. features, purposes, variants, ...
    long numElems = 0;
    try {
      LOGGER.info("Parsing XML " + XML + " skipping " + numSkipped + " Elements ...");
      final XMLParser parser = new XMLParser();
      parser.setXmlFilename(XML);
      parser.setCallbackHandler(tcbh);
      parser.setNumOfSkippedElements(numSkipped);
      numElems = parser.parseXmlElements();
      LOGGER.info("... done.");
    }
    catch (final Exception ex) {
      final String message = "XML Parsing failed: " + ex.getMessage();
      LOGGER.info(message);
      fail(message);
    }
    assertEquals("We expected " + expected + " XML-Elements but got " + numElems, expected, numElems);
    assertEquals("Number of Elements counted by Parser (" + numElems + ") and CallBackHandler (" + tcbh.counter + ") are not equal.", numElems, tcbh.counter);
  }
}
