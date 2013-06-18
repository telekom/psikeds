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
package org.psikeds.queryagent.presenter.jsf.util;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for get JSF Beans from within other Beans and also for adding
 * (Error-)Messages
 * 
 * @author marco@juliano.de
 * 
 */
public final class FacesContextHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacesContextHelper.class);

    public static FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

    public static <T> T eval(final String ELExpression, final Class<? extends T> expectedType) {
        LOGGER.debug("Evaluating ELExpression: {} - Expecting Type: {}", ELExpression, expectedType.getName());
        final FacesContext ctx = getContext();
        final Application app = ctx.getApplication();
        final T result = app.evaluateExpressionGet(ctx, ELExpression, expectedType);
        LOGGER.debug("Got: {}", result);
        return result;
    }

    public static <T> T getBean(final String beanName, final Class<? extends T> expectedType) {
        final StringBuilder ELExpression = new StringBuilder("#{");
        ELExpression.append(beanName);
        ELExpression.append('}');
        return eval(ELExpression.toString(), expectedType);
    }

    public static Object getBean(final String beanName) {
        return getBean(beanName, Object.class);
    }

    // ----------------------------------------------------

    public void addMessage(final FacesContext ctx, final String clientId, final FacesMessage message) {
        ctx.addMessage(clientId, message);
    }

    public void addMessage(final String clientId, final FacesMessage message) {
        final FacesContext ctx = getContext();
        addMessage(ctx, clientId, message);
    }

    public void addMessage(final String clientId, final String summary) {
        addMessage(clientId, new FacesMessage(summary));
    }

    public void addMessage(final UIComponent component, final FacesMessage message) {
        final FacesContext ctx = getContext();
        final String clientId = component.getClientId(ctx);
        addMessage(ctx, clientId, message);
    }

    public void addMessage(final UIComponent component, final String summary) {
        addMessage(component, new FacesMessage(summary));
    }

    // ----------------------------------------------------

    private FacesContextHelper() {
        // prevent instantiation
    }
}
