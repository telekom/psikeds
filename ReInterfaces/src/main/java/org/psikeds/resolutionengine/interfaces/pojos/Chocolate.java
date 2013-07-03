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
package org.psikeds.resolutionengine.interfaces.pojos;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing one kind of chocolate. Note: Reading from and
 * writing to JSON works out of the box. However for XML the XmlRootElement
 * annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Chocolate")
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

    // -----------------------------------------------------

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" [ refid = ");
        sb.append(String.valueOf(this.refid));
        sb.append(" | description = ");
        sb.append(String.valueOf(this.description));
        sb.append(" ]");
        return sb.toString();
    }
}
