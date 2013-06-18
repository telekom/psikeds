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
package org.psikeds.resolutionengine.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolate;
import org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist;
import org.psikeds.resolutionengine.interfaces.services.ChocolateService;
import org.psikeds.resolutionengine.transformer.Transformer;

/**
 * This Business Service is the actual implementation of
 * {@link org.psikeds.resolutionengine.interfaces.services.ChocolateService} It
 * wired via Spring to the REST Service and used as a delegate.
 * 
 * @author marco@juliano.de
 * 
 */
public class ChocolateBusinessService implements ChocolateService {

    private KnowledgeBase kb;

    public ChocolateBusinessService() {
        this(null);
    }

    @Autowired
    public ChocolateBusinessService(final KnowledgeBase kb) {
        this.kb = kb;
    }

    public KnowledgeBase getKnowledgeBase() {
        return this.kb;
    }

    @Autowired
    public void setKnowledgeBase(final KnowledgeBase kb) {
        this.kb = kb;
    }

    // -----------------------------------------------------

    /**
     * 
     * @see org.psikeds.resolutionengine.interfaces.services.ChocolateService#getChocolates()
     */
    @Override
    public Chocolatelist getChocolates() {
        return Transformer.valueObject2Pojo(this.kb.getChocolates());
    }

    /**
     * 
     * @see org.psikeds.resolutionengine.interfaces.services.ChocolateService#getChocolate(java.lang.String)
     */
    @Override
    public Chocolate selectChocolate(final String refid) {
        return Transformer.valueObject2Pojo(this.kb.getChocolate(refid));
    }

    /**
     * 
     * @see org.psikeds.resolutionengine.interfaces.services.ChocolateService#addChocolate(org.psikeds.resolutionengine.interfaces.pojos.Chocolate)
     */
    @Override
    public Chocolatelist addChocolate(final Chocolate c) {
        this.kb.addChocolate(Transformer.pojo2ValueObject(c));
        return getChocolates();
    }
}
