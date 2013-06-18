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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import org.psikeds.knowledgebase.xml.KBValidator;

/**
 * Validate whether an XML is correct against a given XSD, i.e. check whether
 * the Knowledge Base meets the specification.<br>
 * Note: XML and XSD can be both files or both input streams, but not a
 * combination!<br>
 * 
 * @author marco@juliano.de
 * 
 */
public class XSDValidator implements KBValidator {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private String xsdFilename;
    private String xmlFilename;
    private String encoding;
    private InputStream xsdStream;
    private InputStream xmlStream;

    /**
     * Default constructor
     */
    public XSDValidator() {
        this.xsdFilename = null;
        this.xmlFilename = null;
        this.encoding = DEFAULT_ENCODING;
        this.xsdStream = null;
        this.xmlStream = null;
    }

    /**
     * Constructor using filenames and encoding type
     * 
     * @param xsdFilename
     *            Name of the XSD schema file
     * @param xmlFilename
     *            Name of the XML file
     * @param encoding
     *            Encoding type, e.g. ISO-8859-1 or UTF-8
     * 
     */
    public XSDValidator(final String xsdFilename, final String xmlFilename, final String encoding) {
        this.xsdFilename = xsdFilename;
        this.xmlFilename = xmlFilename;
        this.encoding = encoding;
        this.xsdStream = null;
        this.xmlStream = null;
    }

    /**
     * Constructor using filenames and UTF-8 encoding
     * 
     * @param xsdFilename
     *            Name of the XSD schema file
     * @param xmlFilename
     *            Name of the XML file
     * 
     */
    public XSDValidator(final String xsdFilename, final String xmlFilename) {
        this(xsdFilename, xmlFilename, DEFAULT_ENCODING);
    }

    /**
     * Constructor using input streams for xsd and xml
     * 
     * @param xsdStream
     *            Stream to the XSD schema
     * @param xmlStream
     *            Stream to the XML data
     * 
     */
    @Autowired
    public XSDValidator(final InputStream xsdStream, final InputStream xmlStream) {
        this.xsdFilename = null;
        this.xmlFilename = null;
        this.encoding = null;
        this.xsdStream = xsdStream;
        this.xmlStream = xmlStream;
    }

    /**
     * @return the xsdFilename
     */
    public String getXsdFilename() {
        return this.xsdFilename;
    }

    /**
     * @param xsdFilename
     *            the xsdFilename to set
     */
    public void setXsdFilename(final String xsdFilename) {
        this.xsdFilename = xsdFilename;
    }

    /**
     * @return the xmlFilename
     */
    public String getXmlFilename() {
        return this.xmlFilename;
    }

    /**
     * @param xmlFilename
     *            the xmlFilename to set
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
     *            the encoding to set
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the xsdStream
     */
    public InputStream getXsdStream() {
        return this.xsdStream;
    }

    /**
     * @param xsdStream
     *            the xsdStream to set
     */
    @Autowired
    public void setXsdStream(final InputStream xsdStream) {
        this.xsdStream = xsdStream;
    }

    /**
     * @return the xmlStream
     */
    public InputStream getXmlStream() {
        return this.xmlStream;
    }

    /**
     * @param xmlStream
     *            the xmlStream to set
     */
    @Autowired
    public void setXmlStream(final InputStream xmlStream) {
        this.xmlStream = xmlStream;
    }

    // -------------------------------------------------------------
    // ---- Method for validation of XML against XSD
    // -------------------------------------------------------------

    /**
     * Validate XML against specified XSD schmema file.<br>
     * 
     * @throws SAXException
     *             if XML is not valid against XSD
     * @throws IOException
     * 
     */
    public void validate() throws SAXException, IOException {
        if (this.xsdStream != null && this.xmlStream != null) {
            validate(this.xsdStream, this.xmlStream);
            // Note: We do not close the streams here.
            // It's the responsibility of the caller
            return;
        }
        if (!StringUtils.isEmpty(this.xsdFilename) && !StringUtils.isEmpty(this.xmlFilename) && !StringUtils.isEmpty(this.encoding)) {
            Reader xml = null;
            try {
                xml = new InputStreamReader(new FileInputStream(this.xmlFilename), this.encoding);
                validate(this.xsdFilename, xml);
                return;
            }
            finally {
                // We opened the file, therefore we also
                // must close the Reader/Stream again!
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
        throw new IllegalArgumentException("Unsupported configuration settings!");
    }

    // -------------------------------------------------------------
    // ---- Static helpers for validation of XML against XSD
    // -------------------------------------------------------------

    /**
     * Validate XML reader against specified XSD schmema file.<br>
     * <b>Note:</b> The XML reader will not be closed. This must be invoked by
     * the caller afterwards!<br>
     * 
     * @param xsd
     *            Filename of XSD-schema that will be used to validate the
     *            XML-file
     * @param xml
     *            Reader for XML file
     * @throws SAXException
     *             if XML is not valid against XSD
     * @throws IOException
     * 
     */
    public static void validate(final String xsd, final Reader xml) throws SAXException, IOException {
        validate(new StreamSource(new File(xsd)), new StreamSource(xml));
    }

    /**
     * Validate XML against specified XSD schmema.<br>
     * <b>Note:</b> The XML source/stream will not be closed. This must be
     * invoked by the caller afterwards!<br>
     * 
     * @param xsd
     *            Input stream for XSD-schema that will be used to validate the
     *            XML
     * @param xml
     *            Input stream for XML
     * @throws SAXException
     *             if XML is not valid against XSD
     * @throws IOException
     * 
     */
    public static void validate(final InputStream xsd, final InputStream xml) throws SAXException, IOException {
        validate(new StreamSource(xsd), new StreamSource(xml));
    }

    /**
     * Validate XML against specified XSD schmema.<br>
     * <b>Note:</b> The XML source/stream will not be closed. This must be
     * invoked by the caller afterwards!<br>
     * 
     * @param xsd
     *            Source for XSD-schema that will be used to validate the XML
     * @param xml
     *            Source for XML
     * @throws SAXException
     *             if XML is not valid against XSD
     * @throws IOException
     * 
     */
    public static void validate(final Source xsd, final Source xml) throws SAXException, IOException {
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = factory.newSchema(xsd);
        final Validator validator = schema.newValidator();
        validator.validate(xml);
    }
}
