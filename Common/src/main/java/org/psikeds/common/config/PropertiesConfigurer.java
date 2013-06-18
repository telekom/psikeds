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
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Extension of
 * org.springframework.beans.factory.config.PropertyPlaceholderConfigurer You
 * can specified relative filenames for properties files, that will be resolved
 * within the config directory.
 * 
 * @author marco@juliano.de
 * 
 */
public class PropertiesConfigurer extends PropertyPlaceholderConfigurer {

    public static final String PROPERTIES_FILE_ENCODING = "UTF-8";
    public static final boolean IGNORE_MISSING_PROPERTIES_FILES = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigurer.class);

    public PropertiesConfigurer() {
        super();
        setIgnoreResourceNotFound(IGNORE_MISSING_PROPERTIES_FILES);
        setFileEncoding(PROPERTIES_FILE_ENCODING);
    }

    /**
     * @see org.springframework.core.io.support.PropertiesLoaderSupport#setLocation
     *      (org.springframework.core.io.Resource)
     */
    @Override
    public void setLocation(final Resource location) {
        this.setLocations(new Resource[] { location });
    }

    /**
     * @see org.springframework.core.io.support.PropertiesLoaderSupport#setLocations
     *      (org.springframework.core.io.Resource[])
     */
    @Override
    public void setLocations(final Resource[] locations) {
        for (int idx = 0; idx < locations.length; idx++) {
            final Resource loc = locations[idx];
            // We are only looking for files or URLs pointing to local files!
            if (loc instanceof FileSystemResource || loc instanceof UrlResource) {
                File res = null;
                try {
                    res = ConfigDirectoryHelper.resolveConfigFile(loc.getFile());
                }
                catch (final IOException ioex) {
                    LOGGER.debug("Failed to resolve config file: {}", ioex);
                    // Do not touch resources that we could not handle.
                    // Whatever it is, the springframework hopefully knows
                    // what to do.
                    res = null;
                }
                if (res != null) {
                    final FileSystemResource fsr = new FileSystemResource(res);
                    locations[idx] = fsr;
                    LOGGER.debug("Resolved config file to {}", fsr);
                }
            }
        }
        super.setLocations(locations);
    }
}
