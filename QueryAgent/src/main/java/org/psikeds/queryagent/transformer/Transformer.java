/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.transformer;

import java.util.List;

/**
 * Helper for transforming POJOs from the Interface of the Resolution-Engine
 * (RE)
 * into POJOs of the Interface of the Query-Agent (QA) ... and vice versa.
 * 
 * @author marco@juliano.de
 * 
 */
public final class Transformer {

    public static org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Chocolate reChoco) {
        org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate qaChoco = null;
        if (reChoco != null) {
            qaChoco = new org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate(reChoco.getRefid(), reChoco.getDescription());
        }
        return qaChoco;
    }

    public static org.psikeds.resolutionengine.interfaces.pojos.Chocolate qa2re(final org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate qaChoco) {
        org.psikeds.resolutionengine.interfaces.pojos.Chocolate reChoco = null;
        if (qaChoco != null) {
            reChoco = new org.psikeds.resolutionengine.interfaces.pojos.Chocolate(qaChoco.getRefid(), qaChoco.getDescription());
        }
        return reChoco;
    }

    public static org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist re2qa(final org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist reLst) {
        return reLst == null ? null : re2qa(reLst.getChocolates());
    }

    public static org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist re2qa(final List<org.psikeds.resolutionengine.interfaces.pojos.Chocolate> reLst) {
        org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist qaLst = null;
        if (reLst != null) {
            qaLst = new org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist();
            for (final org.psikeds.resolutionengine.interfaces.pojos.Chocolate reChoco : reLst) {
                qaLst.add(Transformer.re2qa(reChoco));
            }
        }
        return qaLst;
    }

    public static org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist qa2re(final org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist qaLst) {
        return qaLst == null ? null : qa2re(qaLst.getChocolates());
    }

    public static org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist qa2re(final List<org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate> qaLst) {
        org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist reLst = null;
        if (qaLst != null) {
            reLst = new org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist();
            for (final org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate qaChoco : qaLst) {
                reLst.add(Transformer.qa2re(qaChoco));
            }
        }
        return reLst;
    }

    private Transformer() {
        // prevent instantiation
    }
}
