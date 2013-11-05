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

import org.psikeds.knowledgebase.xml.KBParser;
import org.psikeds.knowledgebase.xml.KBValidator;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBaseFactory;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.impl.Xml2VoTransformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;

/**
 * This implementation of a KnowledgeBaseFactory is reading the Knowledge-Data
 * from an XML-Source.
 * 
 * @author marco@juliano.de
 * 
 */
public class XmlKnowledgeBaseFactory implements KnowledgeBaseFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(XmlKnowledgeBaseFactory.class);

  private boolean validate;
  private List<Validator> validatorChain; // list of semantic validators
  private KBValidator validator; // syntactic validator (xml against xsd)
  private KBParser parser;
  private Transformer trans;
  private final XmlKnowledgeBase kb;

  public XmlKnowledgeBaseFactory() {
    this(null);
  }

  public XmlKnowledgeBaseFactory(final KBParser parser) {
    this(parser, null);
  }

  public XmlKnowledgeBaseFactory(final KBParser parser, final Transformer trans) {
    this(parser, trans, false, null, null);
  }

  public XmlKnowledgeBaseFactory(final KBParser parser, final Transformer trans, final List<Validator> validatorChain) {
    this(parser, trans, true, null, validatorChain);
  }

  public XmlKnowledgeBaseFactory(final KBParser parser, final Transformer trans, final boolean validate, final KBValidator validator, final List<Validator> validatorChain) {
    this.validate = validate;
    this.validatorChain = validatorChain;
    this.validator = validator;
    this.parser = parser;
    this.trans = (trans != null ? trans : new Xml2VoTransformer());
    this.kb = new XmlKnowledgeBase(this.trans);
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

  public KBValidator getValidator() {
    return this.validator;
  }

  public void setValidator(final KBValidator validator) {
    this.validator = validator;
  }

  // ----------------------------------------------------------------

  public KBParser getParser() {
    return this.parser;
  }

  public void setParser(final KBParser parser) {
    this.parser = parser;
  }

  public Transformer getTransformer() {
    return this.trans;
  }

  public void setTransformer(final Transformer trans) {
    this.trans = trans;
    this.kb.setTransformer(trans);
  }

  // ----------------------------------------------------------------

  /**
   * @return KnowledgeBase
   * @throws ValidationException
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBaseFactory#create()
   */
  @Override
  public KnowledgeBase create() throws ValidationException {
    try {
      LOGGER.trace("--> create()");

      // Step 0: Knowledge-Base not loaded and valid yet.
      this.kb.setValid(false);

      // Step 1: Validate syntactical structure of XML against XSD (if specified)
      if (this.validate && (this.validator != null)) {
        LOGGER.debug("Validating XML against XSD.");
        this.validator.validate();
      }

      // Step 2: Parse XML and create data structure of Knowledge-Base
      LOGGER.debug("Parsing XML and creating data structures.");
      this.parser.setCallbackHandler(this.kb);
      this.parser.parseXmlElements();

      // Step 3: Validate data structure of Knowledge-Base regarding logical consistency
      if (this.validate) {
        LOGGER.debug("Validating data structures.");
        for (final Validator val : getValidators()) {
          LOGGER.trace(String.valueOf(val));
          if (val != null) {
            val.validate(this.kb);
          }
        }
      }

      // Step 4: Everything ok!
      this.kb.setValid(true);
      return this.kb;
    }
    catch (final XMLStreamException xmlex) {
      throw new ValidationException(xmlex);
    }
    catch (final SAXException saxex) {
      throw new ValidationException(saxex);
    }
    catch (final JAXBException jaxbex) {
      throw new ValidationException(jaxbex);
    }
    catch (final IOException ioex) {
      throw new ValidationException(ioex);
    }
    finally {
      LOGGER.trace("<-- create()");
    }
  }
}
