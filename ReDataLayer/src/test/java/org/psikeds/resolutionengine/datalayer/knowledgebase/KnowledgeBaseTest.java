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
package org.psikeds.resolutionengine.datalayer.knowledgebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.psikeds.knowledgebase.xml.KBParser;
import org.psikeds.knowledgebase.xml.KBParserCallback;
import org.psikeds.knowledgebase.xml.impl.XMLParser;
import org.psikeds.resolutionengine.datalayer.knowledgebase.impl.ChocoCallbackHandler;
import org.psikeds.resolutionengine.datalayer.knowledgebase.impl.KnowledgeBaseFactoryImpl;
import org.psikeds.resolutionengine.datalayer.vo.Chocolate;

public class KnowledgeBaseTest {

    private static final String RESOURCE_PATH = "../KnowledgeBase/src/main/resources/";
    private static final String XML = RESOURCE_PATH + "example.xml";
    private static final String ENCODING = "UTF-8";

    private KnowledgeBaseFactory factory;
    private KBParser parser;
    private KBParserCallback cbh;

    /**
     * Initialize test environment
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.cbh = new ChocoCallbackHandler();
        this.parser = new XMLParser(XML, ENCODING, this.cbh);
        this.factory = new KnowledgeBaseFactoryImpl(this.parser);
    }

    /**
     * Test method for KnowledgeBase and KnowledgeBaseFactory.
     */
    @Test
    public void testKnowledgeBase() {
        final KnowledgeBase kb = this.factory.create();
        assertTrue("Failed to load KB from File " + XML, kb != null);
        final List<Chocolate> lst = kb.getChocolates();
        assertTrue("KB is empty", lst != null);
        assertEquals("KB has " + lst.size() + " not expected 4", 4, lst.size());
        final int idx = 3;
        final Chocolate c1 = lst.get(idx);
        assertTrue("c1 is null", c1 != null);
        final String ref = c1.getRefid();
        final Chocolate c2 = kb.getChocolate(ref);
        assertTrue("c2 is null", c2 != null);
        assertEquals("Different ids", c1.getRefid(), c2.getRefid());
        assertEquals("Different descriptions", c1.getDescription(), c2.getDescription());
    }
}
