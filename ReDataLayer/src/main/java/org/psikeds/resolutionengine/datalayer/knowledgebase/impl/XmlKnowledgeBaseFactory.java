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
package org.psikeds.resolutionengine.datalayer.knowledgebase.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.knowledgebase.xml.KBParser;
import org.psikeds.knowledgebase.xml.KBValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBaseFactory;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;

/**
 * This implementation of a KnowledgeBaseFactory is reading the Knowledge-Data
 * from an XML-Source.
 * 
 * @author marco@juliano.de
 * 
 */
public class XmlKnowledgeBaseFactory implements InitializingBean, KnowledgeBaseFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlKnowledgeBaseFactory.class);

  private boolean validate;
  private List<Validator> validatorChain; // list of semantic validators
  private KBValidator xsdValidator; // syntactic validator
  private KBParser xmlParser;
  private final XmlKnowledgeBase kb;

  public XmlKnowledgeBaseFactory() {
    this(null);
  }

  public XmlKnowledgeBaseFactory(final KBParser xmlParser) {
    this(xmlParser, null, null, null);
  }

  public XmlKnowledgeBaseFactory(final KBParser xmlParser, final KBValidator xsdValidator) {
    this(xmlParser, null, xsdValidator, null);
  }

  public XmlKnowledgeBaseFactory(final KBParser xmlParser, final List<Validator> validatorChain) {
    this(xmlParser, null, null, validatorChain);
  }

  public XmlKnowledgeBaseFactory(final KBParser xmlParser, final Transformer trans, final KBValidator xsdValidator, final List<Validator> validatorChain) {
    this(xmlParser, trans, xsdValidator, validatorChain, ((xsdValidator != null) || ((validatorChain != null) && !validatorChain.isEmpty()))); // validate if there are any validators
  }

  public XmlKnowledgeBaseFactory(final KBParser xmlParser, final Transformer trans, final KBValidator xsdValidator, final List<Validator> validatorChain, final boolean validate) {
    this.validate = validate;
    this.validatorChain = validatorChain;
    this.xsdValidator = xsdValidator;
    this.xmlParser = xmlParser;
    this.kb = new XmlKnowledgeBase(trans);
  }

  // ----------------------------------------------------------------

  /**
   * Check that XmlKnowledgeBaseFactory was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(this.xmlParser, "No XML-Parser!");
    Validate.notNull(this.kb, "No Knowledge-Base!");
    Validate.notNull(this.kb.getTransformer(), "No XML-Transformer!");
    if (!this.validate) {
      LOGGER.info("Knowledge-Base is NOT validated!");
    }
  }

  // ----------------------------------------------------------------

  public boolean doValidate() {
    return this.validate;
  }

  public void setValidate(final boolean validate) {
    this.validate = validate;
  }

  public List<Validator> getValidators() {
    if (this.validatorChain == null) {
      this.validatorChain = new ArrayList<Validator>();
    }
    return this.validatorChain;
  }

  public void setValidators(final List<Validator> validatorChain) {
    this.validatorChain = validatorChain;
  }

  public void addValidator(final Validator val) {
    getValidators().add(val);
  }

  public KBValidator getXsdValidator() {
    return this.xsdValidator;
  }

  public void setXsdValidator(final KBValidator xsdValidator) {
    this.xsdValidator = xsdValidator;
  }

  public KBParser getXmlParser() {
    return this.xmlParser;
  }

  public void setXmlParser(final KBParser xmlParser) {
    this.xmlParser = xmlParser;
  }

  public Transformer getTransformer() {
    return this.kb.getTransformer();
  }

  public void setTransformer(final Transformer trans) {
    this.kb.setTransformer(trans);
  }

  // ----------------------------------------------------------------

  /**
   * Factory Method: Parse XML and create new Knowledge-Base
   * 
   * @return KnowledgeBase
   * @throws ValidationException
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBaseFactory#create()
   */
  @Override
  public KnowledgeBase create() throws ValidationException {
    try {
      LOGGER.trace("--> create()");

      // Step 0: Knowledge-Base not loaded and not valid yet.
      this.kb.setValid(false);

      // Step 1: Validate syntactical structure of XML against XSD (if specified)
      if (this.validate && (this.xsdValidator != null)) {
        LOGGER.debug("Syntax-Validation of XML against XSD.");
        this.xsdValidator.validate();
      }

      // Step 2: Parse XML and create data structure of Knowledge-Base
      LOGGER.debug("Parsing XML and creating data structures.");
      this.xmlParser.setCallbackHandler(this.kb);
      this.xmlParser.parseXmlElements();

      // Step 3: Validate data structure of Knowledge-Base regarding logical consistency
      if (this.validate && (this.validatorChain != null) && !this.validatorChain.isEmpty()) {
        LOGGER.debug("Semantic-Validation of Data Structures regarding logical Consistency.");
        for (final Validator val : getValidators()) {
          if (val != null) {
            if (LOGGER.isTraceEnabled()) {
              LOGGER.trace(String.valueOf(val));
            }
            val.validate(this.kb);
          }
        }
      }

      // Step 4: No Exception, everything ok!
      this.kb.setValid(true);
      return this.kb;
    }
    catch (final XMLStreamException xmlex) {
      throw new ValidationException("XML or XSD not readable.", xmlex);
    }
    catch (final SAXException saxex) {
      throw new ValidationException("XML data is not valid against the XSD.", saxex);
    }
    catch (final JAXBException jaxbex) {
      throw new ValidationException("Cannot create Java Objects from XML data.", jaxbex);
    }
    catch (final IOException ioex) {
      throw new ValidationException("Cannot read data from Stream.", ioex);
    }
    finally {
      LOGGER.trace("<-- create()");
    }
  }
}
