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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.common.util.StringUtils;

/**
 * Helper for handling the configuration directory: You can specify a system
 * property that points to the configuration directory. This directory can
 * contain properties-files and spring-context-files that override the
 * pre-packaged default-config.
 * 
 * @author marco@juliano.de
 * 
 */
final class ConfigDirectoryHelper {

    /**
     * Set this system property (option -D when starting java-vm) to specify the
     * location of the configuration directory.
     */
    private static final String CONFIG_DIR_SYSTEM_PROPERTY = "org.psikeds.config.dir";

    /**
     * IF no config directory was specified via the system property as a
     * fallback we look for ${user.home}/<FALLBACK_SUB_DIR>
     */
    private static final String FALLBACK_SUB_DIR = "psikeds";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDirectoryHelper.class);

    private static File cnfdir;

    /**
     * Try to resolve the configuration directory
     * 
     * Note: Yes, method level synchronization is not the most efficient,
     * but we only use it for reading the configuration during application
     * startup, so no need to worry about performance here.
     * 
     * @return File object of the config dir; never null but might
     *         not exist or not be readable!
     */
    static synchronized File resolveConfigDir() {
        if (cnfdir == null) {
            String dirname = System.getProperty(CONFIG_DIR_SYSTEM_PROPERTY);
            if (StringUtils.isEmpty(dirname)) {
                final StringBuilder sb = new StringBuilder(System.getProperty("user.home"));
                sb.append(File.separatorChar);
                sb.append(FALLBACK_SUB_DIR);
                dirname = sb.toString();
                LOGGER.info("System property {} not set ... fallback to {}", dirname);
            }
            cnfdir = new File(dirname);
            if (!cnfdir.exists() || !cnfdir.isDirectory() || !cnfdir.canRead()) {
                LOGGER.warn("Configuration directory {} does not exist or is not readable ... using only default values!", cnfdir.getAbsolutePath());
            }
        }
        return cnfdir;
    }

    /**
     * Look for a configuration file, can be either absolute or relative within
     * the configuration directory.
     * 
     * @param cnfFile
     *            name of the configuration file
     * @return File object if the config file exists and is readable;
     *         null otherwise
     */
    static File resolveConfigFile(final File cnfFile) {
        File absfile = null;
        if (cnfFile != null) {
            if (cnfFile.isAbsolute()) {
                LOGGER.debug("Configuration file {} is already absolute path.", cnfFile.getName());
                absfile = cnfFile;
            }
            else {
                absfile = new File(resolveConfigDir(), cnfFile.getName());
                LOGGER.debug("Configuration file resolved to {}", absfile.getAbsolutePath());
            }
            if (!absfile.exists() || !absfile.isFile() || !absfile.canRead()) {
                LOGGER.warn("Configuration file {} does not exist or is not readable!", absfile.getAbsolutePath());
                absfile = null;
            }
        }
        return absfile;
    }

    private ConfigDirectoryHelper() {
        // prevent instantiation
    }
}
