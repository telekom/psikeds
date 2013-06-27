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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a list of chocolates. Note: Reading from and
 * writing to JSON works out of the box. However for XML the XmlRootElement
 * annotation is required.
 * 
 * @author marco@juliano.de
 * 
 */
@XmlRootElement(name = "Chocolatelist")
public class Chocolatelist {

    private List<Chocolate> chocolates;

    public Chocolatelist() {
        this.chocolates = new ArrayList<Chocolate>();
    }

    public Chocolatelist(final List<Chocolate> chocolates) {
        this.chocolates = chocolates;
    }

    public List<Chocolate> getChocolates() {
        return this.chocolates;
    }

    public void setChocolates(final List<Chocolate> chocolates) {
        this.chocolates = chocolates;
    }

    // -----------------------------------------------------

    public int size() {
        return this.chocolates == null ? 0 : this.chocolates.size();
    }

    public void clear() {
        this.chocolates.clear();
    }

    public boolean add(final Chocolate choco) {
        return this.chocolates.add(choco);
    }

    public boolean addAll(final Collection<? extends Chocolate> col) {
        return this.chocolates.addAll(col);
    }

    // -----------------------------------------------------

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        for (int i = 0; i < size(); i++) {
            sb.append('\n');
            sb.append(String.valueOf(this.chocolates.get(i)));
        }
        return sb.toString();
    }
}
