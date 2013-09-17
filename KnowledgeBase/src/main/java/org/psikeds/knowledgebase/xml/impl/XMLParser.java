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
package org.psikeds.knowledgebase.xml.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.xml.sax.SAXException;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import org.psikeds.knowledgebase.xml.KBParser;
import org.psikeds.knowledgebase.xml.KBParserCallback;

/**
 * This is a streaming parser suitable for big XML-Files, i.e. the
 * <b>psiKeds Knowledge Base</b>. It uses JAXB in combination with
 * the Iterator-API of StAX.<br>
 * XML-Data can be supplied as either File, Inputstream or Spring-Resource.<br>
 * 
 * @author marco@juliano.de
 */
public class XMLParser implements KBParser {

  private static final String DEFAULT_PACKAGE = "org.psikeds.knowledgebase.jaxb";
  private static final String DEFAULT_ENCODING = "UTF-8";
  private static final int DEFAULT_SKIPPED_ELEMENTS = 0;

  /**
   * Accept every Event triggered by the start of a new XML-Element.
   */
  private static final EventFilter DEFAULT_EVENT_FILTER = new EventFilter() {

    @Override
    public boolean accept(final XMLEvent event) {
      return event != null && event.isStartElement();
    }
  };

  // -------------------------------------------------------------

  private String encoding;
  private String xmlFilename;
  private InputStream xmlStream;
  private Resource xmlResource;
  private String packageName;
  private Class<?> elementClass;
  private KBParserCallback callbackHandler;
  private EventFilter eventFilter;
  private int numOfSkippedElements;

  /**
   * Default constructor
   */
  public XMLParser() {
    this.encoding = DEFAULT_ENCODING;
    this.xmlFilename = null;
    this.xmlStream = null;
    this.xmlResource = null;
    this.packageName = DEFAULT_PACKAGE;
    this.elementClass = null;
    this.callbackHandler = null;
    this.eventFilter = DEFAULT_EVENT_FILTER;
    this.numOfSkippedElements = DEFAULT_SKIPPED_ELEMENTS;
  }

  /**
   * Constructor using filename and encoding type.
   * 
   * @param xmlFilename
   *          Name of the XML-File
   * @param encoding
   *          Encoding type, e.g. ISO-8859-1 or UTF-8
   * @param callbackHandler
   *          Callback handler used to process every single found XML-Element
   *          (@see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object))
   */
  public XMLParser(final String xmlFilename, final String encoding, final KBParserCallback callbackHandler) {
    this();
    this.xmlFilename = xmlFilename;
    this.encoding = encoding;
    this.callbackHandler = callbackHandler;
  }

  /**
   * Constructor using filename and UTF-8 encoding.
   * 
   * @param xmlFilename
   *          Name of the XML-File
   */
  public XMLParser(final String xmlFilename) {
    this(xmlFilename, DEFAULT_ENCODING, null);
  }

  /**
   * Constructor using input stream and encoding type.
   * 
   * @param xmlStream
   *          Stream to the XML-Data
   * @param encoding
   *          Encoding type, e.g. ISO-8859-1 or UTF-8
   * @param callbackHandler
   *          Callback handler used to process every single found XML-Element
   *          (@see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object))
   */
  public XMLParser(final InputStream xmlStream, final String encoding, final KBParserCallback callbackHandler) {
    this();
    this.xmlStream = xmlStream;
    this.encoding = encoding;
    this.callbackHandler = callbackHandler;
  }

  /**
   * Constructor using input stream and UTF-8 encoding.
   * 
   * @param xmlStream
   *          Stream to the XML-Data
   */
  public XMLParser(final InputStream xmlStream) {
    this(xmlStream, DEFAULT_ENCODING, null);
  }

  /**
   * Constructor using Spring-Resource and encoding type.
   * 
   * @param xmlResource
   *          Spring-Resource for the XML-Data
   * @param encoding
   *          Encoding type, e.g. ISO-8859-1 or UTF-8
   * @param callbackHandler
   *          Callback handler used to process every single found XML-Element
   *          (@see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object))
   */
  public XMLParser(final Resource xmlResource, final String encoding, final KBParserCallback callbackHandler) {
    this();
    this.xmlResource = xmlResource;
    this.encoding = encoding;
    this.callbackHandler = callbackHandler;
  }

  /**
   * Constructor using Spring-Resource and UTF-8 encoding.
   * 
   * @param xmlResource
   *          Spring-Resource for the XML-Data
   */
  public XMLParser(final Resource xmlResource) {
    this(xmlResource, DEFAULT_ENCODING, null);
  }

  // -------------------------------------------------------------

  /**
   * @return the xmlFilename
   */
  public String getXmlFilename() {
    return this.xmlFilename;
  }

  /**
   * @param xmlFilename
   *          the xmlFilename to set
   */
  public void setXmlFilename(final String xmlFilename) {
    this.xmlFilename = xmlFilename;
  }

  /**
   * @return the encoding
   */
  public String getEncoding() {
    return this.encoding;
  }

  /**
   * @param encoding
   *          the encoding to set
   */
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

  /**
   * @return the xmlStream
   */
  public InputStream getXmlStream() {
    return this.xmlStream;
  }

  /**
   * @param xmlStream
   *          the xmlStream to set
   */
  public void setXmlStream(final InputStream xmlStream) {
    this.xmlStream = xmlStream;
  }

  /**
   * @return the xmlResource
   */
  public Resource getXmlResource() {
    return this.xmlResource;
  }

  /**
   * @param xmlResource
   *          the xmlResource to set
   */
  public void setXmlResource(final Resource xmlResource) {
    this.xmlResource = xmlResource;
  }

  /**
   * @return the packageName
   */
  public String getPackageName() {
    return this.packageName;
  }

  /**
   * @param packageName
   *          the packageName to set
   */
  public void setPackageName(final String packageName) {
    this.packageName = packageName;
  }

  /**
   * @return the elementClass
   */
  public Class<?> getElementClass() {
    return this.elementClass;
  }

  /**
   * @param elementClass
   *          the elementClass to set
   */
  public void setElementClass(final Class<?> elementClass) {
    this.elementClass = elementClass;
  }

  /**
   * @return the callbackHandler
   */
  @Override
  public KBParserCallback getCallbackHandler() {
    return this.callbackHandler;
  }

  /**
   * @param callbackHandler
   *          the callbackHandler to set
   */
  @Override
  public void setCallbackHandler(final KBParserCallback callbackHandler) {
    this.callbackHandler = callbackHandler;
  }

  /**
   * @return the eventFilter
   */
  public EventFilter getEventFilter() {
    return this.eventFilter;
  }

  /**
   * @param eventFilter
   *          the eventFilter to set
   */
  public void setEventFilter(final EventFilter eventFilter) {
    this.eventFilter = eventFilter;
  }

  /**
   * @return the numOfSkippedElements
   */
  public int getNumOfSkippedElements() {
    return this.numOfSkippedElements;
  }

  /**
   * @param numSkipped
   *          the numOfSkippedElements to set
   */
  public void setNumOfSkippedElements(final int numSkipped) {
    this.numOfSkippedElements = numSkipped;
  }

  // -------------------------------------------------------------
  // ---- Methods for parsing XML
  // -------------------------------------------------------------

  /**
   * Parse the specified XML and unmarshal it to JAXB-Object-Structures.
   * Suitable for big XML files by using JAXB combination with StAX.<br>
   * All classes in the specified package can be parsed.<br>
   * 
   * @return Total number of unmarshalled XML-Elements
   * @throws XMLStreamException
   * @throws SAXException
   * @throws JAXBException
   * @throws IOException
   */
  @Override
  public long parseXmlElements() throws XMLStreamException, SAXException, JAXBException, IOException {
    Reader xml = null;
    try {
      xml = createXmlReader();
      return parseXmlElements(xml);
    }
    finally {
      if (xml != null) {
        try {
          xml.close();
        }
        catch (final IOException ex) {
          // ignore
        }
        finally {
          xml = null;
        }
      }
    }
  }

  private Reader createXmlReader() throws UnsupportedEncodingException, IOException {
    if (!StringUtils.isEmpty(this.encoding)) {
      if (this.xmlResource != null) {
        return new InputStreamReader(this.xmlResource.getInputStream(), this.encoding);
      }
      if (this.xmlStream != null) {
        return new InputStreamReader(this.xmlStream, this.encoding);
      }
      if (!StringUtils.isEmpty(this.xmlFilename)) {
        return new InputStreamReader(new FileInputStream(this.xmlFilename), this.encoding);
      }
    }
    throw new IllegalArgumentException("Unsupported configuration settings!");
  }

  private long parseXmlElements(final Reader xml) throws XMLStreamException, JAXBException {
    if (this.callbackHandler != null) {
      if (this.elementClass != null) {
        return parseXmlElements(xml, this.elementClass, this.callbackHandler, this.eventFilter, this.numOfSkippedElements);
      }
      if (!StringUtils.isEmpty(this.packageName)) {
        return parseXmlElements(xml, this.packageName, this.callbackHandler, this.eventFilter, this.numOfSkippedElements);
      }
    }
    throw new IllegalArgumentException("Unsupported configuration settings! Must specify a Callback-Handler and either a Package-Name or an Element-Class.");
  }

  // -------------------------------------------------------------
  // ---- Static helpers for parsing XML
  // -------------------------------------------------------------

  /**
   * Helper for parsing big XML files using JAXB in combination with StAX.<br>
   * All classes within the Package <b>org.psikeds.knowledgebase.jaxb</b> can
   * be parsed.<br>
   * The XML-Root-Element will be skipped, i.e. all Elements below will be
   * unmarshalled.<br>
   * <b>Note:</b> The XML reader will not be closed. This must be invoked by
   * the caller afterwards!<br>
   * 
   * @param xml
   *          Reader for XML-Data
   * @param handler
   *          Callback handler used to process every single found XML
   *          element (@see
   *          org.psikeds.knowledgebase.xml.KBParserCallback#handleElement
   *          (java.lang.Object))
   * @return Total number of unmarshalled XML-Elements
   * @throws XMLStreamException
   * @throws JAXBException
   */
  public static long parseXmlElements(final Reader xml, final KBParserCallback handler)
      throws XMLStreamException, JAXBException {

    return parseXmlElements(xml, DEFAULT_PACKAGE, handler, DEFAULT_EVENT_FILTER, DEFAULT_SKIPPED_ELEMENTS);
  }

  /**
   * Helper for parsing big XML files using JAXB in combination with StAX.<br>
   * All classes in the specified package can be parsed.<br>
   * <b>Note:</b> The XML reader will not be closed. This must be invoked by
   * the caller afterwards!<br>
   * 
   * @param xml
   *          Reader for XML-Data
   * @param packageName
   *          Name of the package containing the JAXB-Classes,
   *          e.g. org.psikeds.knowledgebase.jaxb
   * @param handler
   *          Callback handler used to process every single found
   *          XML-Element (@see
   *          org.psikeds.knowledgebase.xml.KBParserCallback#handleElement
   *          (java.lang.Object))
   * @param filter
   *          EventFilter used for StAX-Parsing
   * @param numSkipped
   *          Number of Elements to be skipped,
   *          e.g. numSkipped = 1 for skipping the XML-Root-Element.
   * @return Total number of unmarshalled XML-Elements
   * @throws XMLStreamException
   * @throws JAXBException
   */
  public static long parseXmlElements(final Reader xml, final String packageName, final KBParserCallback handler, final EventFilter filter, final int numSkipped)
      throws XMLStreamException, JAXBException {

    // init stream reader
    final XMLInputFactory staxFactory = XMLInputFactory.newInstance();
    final XMLEventReader staxReader = staxFactory.createXMLEventReader(xml);
    final XMLEventReader filteredReader = filter == null ? staxReader : staxFactory.createFilteredReader(staxReader, filter);

    skipXmlElements(filteredReader, numSkipped);

    // JAXB with specific package
    final JAXBContext jaxbCtx = JAXBContext.newInstance(packageName);
    final Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

    // parsing und unmarshalling
    long counter = 0;
    while (filteredReader.peek() != null) {
      final Object element = unmarshaller.unmarshal(staxReader);
      handleElement(handler, element);
      counter++;
    }
    return counter;
  }

  /**
   * Helper for parsing big XML files using JAXB in combination with StAX.<br>
   * Only XML-Elements of the specified Top-Level-Class will be parsed.<br>
   * <b>Note:</b> The XML reader will not be closed. This must be invoked by
   * the caller afterwards!<br>
   * 
   * @param xml
   *          Reader for XML-Data
   * @param elementClass
   *          Top-Level-Class used for JAXB-Unmarshalling
   * @param handler
   *          Callback handler used to process every single found XML
   *          element (@see
   *          org.psikeds.knowledgebase.xml.KBParserCallback#handleElement
   *          (java.lang.Object))
   * @return Total number of unmarshalled XML-Elements
   * @throws XMLStreamException
   * @throws JAXBException
   */
  public static long parseXmlElements(final Reader xml, final Class<?> elemClazz, final KBParserCallback handler)
      throws XMLStreamException, JAXBException {

    return parseXmlElements(xml, elemClazz, handler, DEFAULT_EVENT_FILTER, DEFAULT_SKIPPED_ELEMENTS);
  }

  /**
   * Helper for parsing big XML files using JAXB in combination with StAX.<br>
   * Only XML-Elements of the specified Top-Level-Class will be parsed.<br>
   * <b>Note:</b> The XML reader will not be closed. This must be invoked by
   * the caller afterwards!<br>
   * 
   * @param xml
   *          Reader for XML-Data
   * @param elementClass
   *          Top-Level-Class used for JAXB-Unmarshalling
   * @param handler
   *          Callback handler used to process every single found XML
   *          element (@see
   *          org.psikeds.knowledgebase.xml.KBParserCallback#handleElement
   *          (java.lang.Object))
   * @param filter
   *          EventFilter used for StAX-Parsing
   * @param numSkipped
   *          Number of Elements to be skipped,
   *          e.g. numSkipped = 1 for skipping the XML-Root-Element.
   * @return Total number of unmarshalled XML-Elements
   * @throws XMLStreamException
   * @throws JAXBException
   */
  public static long parseXmlElements(final Reader xml, final Class<?> elemClazz, final KBParserCallback handler, final EventFilter filter, final int numSkipped)
      throws XMLStreamException, JAXBException {

    // init stream reader
    final XMLInputFactory staxFactory = XMLInputFactory.newInstance();
    final XMLEventReader staxReader = staxFactory.createXMLEventReader(xml);
    final XMLEventReader filteredReader = filter == null ? staxReader : staxFactory.createFilteredReader(staxReader, filter);

    skipXmlElements(filteredReader, numSkipped);

    // JAXB with specific top-level-class
    final JAXBContext jaxbCtx = JAXBContext.newInstance(elemClazz);
    final Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

    // parsing und unmarshalling
    long counter = 0;
    while (filteredReader.peek() != null) {
      final Object element = unmarshaller.unmarshal(staxReader, elemClazz);
      handleElement(handler, element);
      counter++;
    }
    return counter;
  }

  private static void handleElement(final KBParserCallback handler, final Object element) {
    // Note1: The unmarshalled "element" is a complete JAXB-Object-Structure
    // containing not only the current XML-Element but also the Content-Tree
    // of all its Sub-Elements!

    // Note2: Depending on the XSD-Design (Venetian Blind vs. Salami Slice)
    // and the Kind of Invocation of the XML-Parser (Package vs. Class)
    // the unmarshalled Element might not be the expected JAXB-Object but
    // an Instance of JAXBElement wrapping the actual JAXB-Object:
    if (element instanceof JAXBElement && ((JAXBElement<?>) element).getValue() != null) {
      handler.handleElement(((JAXBElement<?>) element).getValue());
    }
    else {
      handler.handleElement(element);
    }
  }

  private static void skipXmlElements(final XMLEventReader filteredReader, final int numSkipped) throws XMLStreamException {
    for (int idx = 0; idx < numSkipped; idx++) {
      filteredReader.nextEvent();
    }
  }
}
