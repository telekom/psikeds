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
package org.psikeds.common.util;

import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trivial helper, setting all properties passed to the constructor as java
 * system properties. Helpful for e.g. configuring http(s) proxy settings.
 * 
 * @author marco@juliano.de
 * 
 */
public class SystemPropertySetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertySetter.class);

    public SystemPropertySetter(final Properties props) {
        final Enumeration<?> names = props.propertyNames();
        while (names.hasMoreElements()) {
            final String name = (String) names.nextElement();
            final String value = props.getProperty(name);
            try {
                System.setProperty(name, value);
                LOGGER.info("Set system property {} to {}", name, value);
            }
            catch (final Exception ex) {
                LOGGER.error("Failed to set system property {}  -->  {}", name, ex);
                // log error but do not fail
            }
        }
    }
}
