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

import java.util.List;

import org.psikeds.knowledgebase.xml.KBParserCallback;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;

/**
 * Callback Handler for KnowledgeBase XML Parser used by the KnowledgeBase
 * Factory. Note: This class (and the Transformer) should be the only location
 * within the Datalayer being aware of JAXB and the XML format.
 * 
 * @author m.juliano
 * 
 */
public class ChocoCallbackHandler implements KBParserCallback {

    private final org.psikeds.resolutionengine.datalayer.vo.Chocolatelist voCL;

    public ChocoCallbackHandler() {
        this.voCL = new org.psikeds.resolutionengine.datalayer.vo.Chocolatelist();
    }

    public org.psikeds.resolutionengine.datalayer.vo.Chocolatelist getChocolates() {
        return this.voCL;
    }

    /**
     * 
     * @param element
     * @see org.psikeds.knowledgebase.xml.KBParserCallback#handleElement(java.lang.Object)
     */
    @Override
    public void handleElement(final Object element) {
        if (element != null) {
            if (element instanceof org.psikeds.knowledgebase.jaxb.Chocolatelist) {
                final org.psikeds.knowledgebase.jaxb.Chocolatelist jaxbCL = (org.psikeds.knowledgebase.jaxb.Chocolatelist) element;
                handleChocolatelist(jaxbCL);
            }
            else if (element instanceof org.psikeds.knowledgebase.jaxb.Chocolate) {
                final org.psikeds.knowledgebase.jaxb.Chocolate jaxbChoco = (org.psikeds.knowledgebase.jaxb.Chocolate) element;
                handleChocolate(jaxbChoco);
            }
            else {
                throw new IllegalArgumentException("Unknown XML object: " + element);
            }
        }
    }

    private void handleChocolatelist(final org.psikeds.knowledgebase.jaxb.Chocolatelist jaxbCL) {
        final List<org.psikeds.knowledgebase.jaxb.Chocolate> lst = jaxbCL.getChocolate();
        for (final org.psikeds.knowledgebase.jaxb.Chocolate jaxbChoco : lst) {
            handleChocolate(jaxbChoco);
        }
    }

    private void handleChocolate(final org.psikeds.knowledgebase.jaxb.Chocolate jaxbChoco) {
        final org.psikeds.resolutionengine.datalayer.vo.Chocolate voChoco = Transformer.xml2ValueObject(jaxbChoco);
        this.voCL.add(voChoco);
    }
}
