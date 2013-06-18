/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [x] GNU General Public License
 * [ ] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.jsf.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate;
import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist;
import org.psikeds.queryagent.presenter.jsf.model.Item;

/**
 * Item used for displaying the complete List/Tree of all Chocolates.
 * 
 * @author marco@juliano.de
 * 
 */
public class ChocolatelistItem implements Item {

    private final List<Item> siblings = new ArrayList<Item>();

    public ChocolatelistItem() {
        this(null);
    }

    public ChocolatelistItem(final Chocolatelist chocolst) {
        setChocolatelist(chocolst);
    }

    public void setChocolatelist(final Chocolatelist chocolst) {
        clear();
        if (chocolst != null) {
            for (final Chocolate choco : chocolst.getChocolates()) {
                final ChocolateItem citem = new ChocolateItem(choco);
                this.siblings.add(citem);
                for (final IngredientItem iitem : citem.getSiblings()) {
                    this.siblings.add(iitem);
                }
            }
        }
    }

    public void clear() {
        this.siblings.clear();
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.model.Item#getKey()
     */
    @Override
    public String getKey() {
        return this.toString();
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.model.Item#getValue()
     */
    @Override
    public String getValue() {
        return this.toString();
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.model.Item#isHavingSiblings()
     */
    @Override
    public boolean isHavingSiblings() {
        return this.siblings.size() > 0;
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.model.Item#getSiblings()
     */
    @Override
    public List<Item> getSiblings() {
        return this.siblings;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nHierarchy contains total of ");
        sb.append(this.siblings.size());
        sb.append(" items.");
        return sb.toString();
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.model.Item#getHierarchyLevel()
     */
    @Override
    public int getHierarchyLevel() {
        return 0;
    }
}
