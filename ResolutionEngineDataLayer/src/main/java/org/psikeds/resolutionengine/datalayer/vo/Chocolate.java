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
package org.psikeds.resolutionengine.datalayer.vo;

public class Chocolate {

    private String refid;
    private String description;

    public Chocolate() {
        this.refid = null;
        this.description = null;
    }

    public Chocolate(final String refid, final String description) {
        this.refid = refid;
        this.description = description;
    }

    public String getRefid() {
        return this.refid;
    }

    public void setRefid(final String refid) {
        this.refid = refid;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
