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
import org.psikeds.resolutionengine.datalayer.vo.Chocolatelist;

public class KnowledgeBaseFactoryImpl implements KnowledgeBaseFactory {

    private KBParser parser;

    public KnowledgeBaseFactoryImpl() {
        this.parser = null;
    }

    @Autowired
    public KnowledgeBaseFactoryImpl(final KBParser parser) {
        this.parser = parser;
    }

    public KBParser getParser() {
        return this.parser;
    }

    @Autowired
    public void setParser(final KBParser parser) {
        this.parser = parser;
    }

    public KnowledgeBase create() {
        try {
            this.parser.parseXmlElements();
            final ChocoCallbackHandler ccbh = (ChocoCallbackHandler) this.parser.getCallbackHandler();
            final Chocolatelist chocolst = ccbh.getChocolates();
            return new KnowledgeBaseImpl(chocolst);
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
