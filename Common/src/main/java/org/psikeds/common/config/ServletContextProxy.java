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
package org.psikeds.common.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;

/**
 * Proxy for javax.servlet.ServletContext: Intercepts all read access (Method
 * getInitParameter()) to Spring's servlet config parameter
 * "contextConfigLocation".
 * 
 * @author marco@juliano.de
 * 
 */
final class ServletContextProxy implements InvocationHandler {

    private static final String INIT_PARAM_METHOD = "getInitParameter";
    private static final String SERVLET_CTX_PARAM = ContextLoader.CONFIG_LOCATION_PARAM;

    private final ServletContext servletContext;
    private final ConfigLocationOverrider overrider;

    private ServletContextProxy(final ServletContext ctx) {
        super();
        this.servletContext = ctx;
        this.overrider = new ConfigLocationOverrider();
    }

    // -----------------------------------------------------

    public static ServletContext newInstance(final ServletContext ctx) {
        return ServletContext.class.cast(createProxy(ctx));
    }

    /**
     * @param proxy
     * @param method
     * @param args
     * @return result
     * @throws Throwable
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        // Always invoke the original first
        Object result = method.invoke(this.servletContext, args);
        // Override invocations of getting spring context config location
        final int len = args == null ? 0 : args.length;
        if (len == 1 && INIT_PARAM_METHOD.equals(method.getName()) && SERVLET_CTX_PARAM.equals(args[0])) {
            result = this.overrider.getContextLocation(String.class.cast(result));
        }
        return result;
    }

    // -----------------------------------------------------

    private static ClassLoader getClassloader(final Class<? extends ServletContext> clazz) {
        // get classloader from class (might eventually not work in some j2ee
        // environments)
        final ClassLoader loader = clazz == null ? null : clazz.getClassLoader();
        // fallback to classloader of creator of current thread
        return loader == null ? Thread.currentThread().getContextClassLoader() : loader;
    }

    private static Object createProxy(final ServletContext ctx) {
        // Get runtime class of CTX which is something that implements
        // ServletContext
        final Class<? extends ServletContext> clazz = ctx.getClass();
        // Get the Classloader for that runtime class
        final ClassLoader loader = getClassloader(clazz);
        // Create a new Proxy for that Class using our ServletContextProxy
        return Proxy.newProxyInstance(loader, clazz.getInterfaces(), new ServletContextProxy(ctx));
    }
}
