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
package org.psikeds.resolutionengine.datalayer.knowledgebase.impl;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.knowledgebase.xml.KBParser;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBaseFactory;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.impl.Xml2VoTransformer;

/**
 * This implementation of a KnowledgeBaseFactory is reading the Knowledge-Data
 * from an XML-Source.
 *
 * @author marco@juliano.de
 *
 */
public class XmlKnowledgeBaseFactory implements KnowledgeBaseFactory {

  private KBParser parser;
  private Transformer trans;
  private final XmlKnowledgeBase kb;

  public XmlKnowledgeBaseFactory() {
    this(null);
  }

  public XmlKnowledgeBaseFactory(final KBParser parser) {
    this(parser, new Xml2VoTransformer());
  }

  @Autowired
  public XmlKnowledgeBaseFactory(final KBParser parser, final Transformer trans) {
    this.parser = parser;
    this.trans = trans;
    this.kb = new XmlKnowledgeBase(trans);
  }

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
  }

  /**
   * @return KnowledgeBase
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBaseFactory#create()
   */
  @Override
  public KnowledgeBase create() {
    try {
      this.parser.setCallbackHandler(this.kb);
      this.parser.parseXmlElements();
      return this.kb;
    }
    catch (final XMLStreamException xmlex) {
      throw new IllegalArgumentException(xmlex);
    }
    catch (final SAXException saxex) {
      throw new IllegalArgumentException(saxex);
    }
    catch (final JAXBException jaxbex) {
      throw new IllegalArgumentException(jaxbex);
    }
    catch (final IOException ioex) {
      throw new IllegalArgumentException(ioex);
    }
  }
}
