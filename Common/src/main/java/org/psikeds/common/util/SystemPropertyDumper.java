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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple helper logging all system-properties and environment-variables. Can be
 * configured as a Spring-Bean, e.g. in order to display additional debugging
 * infos when application server is started.
 * 
 * @author marco@juliano.de
 * 
 */
public class SystemPropertyDumper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemPropertyDumper.class);

    private static final String SEPARATOR = "----------------------------------------------";

    private static final String SYSPROP_HEADER = "Total of {} system properties:";
    private static final String SYSPROP_TEMPLATE = "SYS: {} = {}";
    private static final String SYSPROP_FOOTER = SEPARATOR;

    private static final String ENVVAR_HEADER = "Total of {} environment variables:";
    private static final String ENVVAR_TEMPLATE = "ENV: {} = {}";
    private static final String ENVVAR_FOOTER = SEPARATOR;

    // ---------------------------------

    private boolean dumpSystemProperties;
    private boolean dumpEnvironmentVariables;

    public SystemPropertyDumper() {
        this.dumpSystemProperties = false;
        this.dumpEnvironmentVariables = false;
    }

    public void setDumpSystemProperties(final boolean dump) {
        this.dumpSystemProperties = dump;
    }

    public boolean isDumpSystemProperties() {
        return this.dumpSystemProperties;
    }

    public void setDumpEnvironmentVariables(final boolean dump) {
        this.dumpEnvironmentVariables = dump;
    }

    public boolean isDumpEnvironmentVariables() {
        return this.dumpEnvironmentVariables;
    }

    // ---------------------------------

    public void init() {
        if (this.dumpSystemProperties) {
            doDumpSystemProperties();
        }
        if (this.dumpEnvironmentVariables) {
            doDumpEnvironmentVariables();
        }
    }

    private void doDumpSystemProperties() {
        final Properties props = System.getProperties();
        printHeader(SYSPROP_HEADER, props.size());
        printMap(SYSPROP_TEMPLATE, props.entrySet());
        LOGGER.debug(SYSPROP_FOOTER);
    }

    private void doDumpEnvironmentVariables() {
        final Map<String, String> env = System.getenv();
        printHeader(ENVVAR_HEADER, env.size());
        printMap(ENVVAR_TEMPLATE, env.entrySet());
        LOGGER.debug(ENVVAR_FOOTER);
    }

    private <T> void printMap(final String template, final Set<Map.Entry<T, T>> entries) {
        for (final Map.Entry<T, T> entry : entries) {
            LOGGER.debug(template, entry.getKey(), entry.getValue());
        }
    }

    private void printHeader(final String template, final int count) {
        LOGGER.debug(SEPARATOR);
        LOGGER.debug(template, count);
        LOGGER.debug(SEPARATOR);
    }
}
