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

import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist;
import org.psikeds.queryagent.interfaces.presenter.services.ChocolateService;
import org.psikeds.queryagent.presenter.jsf.controller.NavigationController;
import org.psikeds.queryagent.presenter.jsf.model.Item;
import org.psikeds.queryagent.presenter.jsf.model.impl.ChocolatelistItem;
import org.psikeds.queryagent.presenter.jsf.util.Constants;

/**
 * Implementation of NavigationController for handling Lists of Chocolates.
 * 
 * @author marco@juliano.de
 * 
 */
public class ChocolateListController extends BaseChocolateController implements NavigationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChocolateListController.class);

    public ChocolateListController() {
        this(null, null);
    }

    public ChocolateListController(final ChocolateService cs, final Item all) {
        super(cs, all, null);
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.NavigationController#home()
     */
    @Override
    public String home() {
        return Constants.RESULT_HOME;
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.NavigationController#back()
     */
    @Override
    public String back() {
        return Constants.RESULT_BACK;
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.NavigationController#addItem()
     */
    @Override
    public String addItem() {
        return Constants.RESULT_ADD;
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.NavigationController#loadItems()
     */
    @Override
    public String loadItems() {
        return handleItemList(true, Constants.RESULT_LOAD);
    }

    /**
     * @return
     * @see org.psikeds.queryagent.presenter.jsf.controller.NavigationController#displayItems()
     */
    @Override
    public String displayItems() {
        return handleItemList(false, Constants.RESULT_DISPLAY);
    }

    private String handleItemList(final boolean loadItems, final String desiredResult) {
        String ret = Constants.RESULT_ERROR;
        try {
            LOGGER.trace("--> handleItemList({}, {})", loadItems, desiredResult);
            final ChocolatelistItem allItems = (ChocolatelistItem) getAllItemsBean();
            if (loadItems || !allItems.isHavingSiblings()) {
                final Chocolatelist chocolst = getService().getChocolates();
                allItems.setChocolatelist(chocolst);
                LOGGER.debug("(Re)Loaded allItems = {}", allItems);
            }
            ret = desiredResult;
        }
        catch (final Exception ex) {
            ret = Constants.RESULT_ERROR;
            LOGGER.error("Could not handle [{}] Item List: {}", desiredResult, ex);
        }
        finally {
            LOGGER.trace("<-- handleItemList({}, {}); ret = {}", loadItems, desiredResult, ret);
        }
        return ret;
    }
}
