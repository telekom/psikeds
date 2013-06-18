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
package org.psikeds.queryagent.presenter.jsf.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate;
import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist;
import org.psikeds.queryagent.interfaces.presenter.services.ChocolateService;
import org.psikeds.queryagent.presenter.jsf.controller.AddItemController;
import org.psikeds.queryagent.presenter.jsf.model.Item;
import org.psikeds.queryagent.presenter.jsf.model.impl.ChocolateItem;
import org.psikeds.queryagent.presenter.jsf.model.impl.ChocolatelistItem;
import org.psikeds.queryagent.presenter.jsf.util.Constants;

/**
 * Implementation of AddItemController for creating a new Chocolate.
 * 
 * @author marco@juliano.de
 * 
 */
public class AddChocolateController extends BaseChocolateController implements AddItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddChocolateController.class);

    private String key;
    private String value;

    public AddChocolateController() {
        this(null, null, null);
    }

    public AddChocolateController(final ChocolateService srvc, final Item all, final Item selected) {
        super(srvc, all, selected);
        setKey(null);
        setValue(null);
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.AddItemController#getKey()
     */
    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * @param k
     * @see org.psikeds.queryagent.presenter.jsf.controller.AddItemController#setKey(java.lang.String)
     */
    @Override
    public void setKey(final String k) {
        this.key = k;
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.AddItemController#getValue()
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * @param v
     * @see org.psikeds.queryagent.presenter.jsf.controller.AddItemController#setValue(java.lang.String)
     */
    @Override
    public void setValue(final String v) {
        this.value = v;
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.AddItemController#doAdd()
     */
    @Override
    public String doAdd() {
        String ret = Constants.RESULT_ERROR;
        try {
            LOGGER.trace("--> doAdd()");
            if (StringUtils.isEmpty(this.key)) {
                throw new IllegalArgumentException("Key (= RefId) is empty!");
            }
            if (StringUtils.isEmpty(this.value)) {
                throw new IllegalArgumentException("Value (= Description) is empty!");
            }
            // create new chocolate and invoke business service
            final Chocolate choco = new Chocolate(this.key, this.value);
            LOGGER.debug("Adding new Chocolate: {}", choco);
            final Chocolatelist chocolst = getService().addChocolate(choco);
            // update cached list of chocolate
            final ChocolatelistItem allItems = (ChocolatelistItem) getAllItemsBean();
            allItems.setChocolatelist(chocolst);
            LOGGER.trace("Updated allItems = {}", allItems);
            // update currently selected item
            final ChocolateItem selectedItem = (ChocolateItem) getSelectedItemBean();
            selectedItem.setChocolate(choco);
            LOGGER.trace("Updated selectedItem = {}", selectedItem);
            // done
            ret = Constants.RESULT_SUCCESS;
        }
        catch (final Exception ex) {
            ret = Constants.RESULT_ERROR;
            LOGGER.error("Could not add Chocolate [{}, {}] - {}", this.key, this.value, ex);
        }
        finally {
            LOGGER.trace("<-- doAdd(); ret = {}", ret);
        }
        return ret;
    }
}
