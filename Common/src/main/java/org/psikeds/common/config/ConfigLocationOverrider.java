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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;

/**
 * Helperfor replacing/overriding the location of spring context files: If a
 * file matching *context.xml or *properties.xml was requested and a
 * corresponding file exists in the config directory, then use that instead of
 * the original one packaged in the JAR/WAR/EAR.
 * 
 * @author marco@juliano.de
 */
class ConfigLocationOverrider {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLocationOverrider.class);
  private static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

  private final File configDir;
  private final List<String> configFiles;

  // in order to avoid "dirt" in the config directory we only accept
  // filenames matching *context.xml or *properties.xml
  private final FilenameFilter springFilenameFilter = new FilenameFilter() {

    @Override
    public boolean accept(final File dir, final String name) {
      return StringUtils.endsWithIgnoreCase(name, "context.xml") || StringUtils.endsWithIgnoreCase(name, "properties.xml");
    }
  };

  ConfigLocationOverrider() {
    this.configDir = ConfigDirectoryHelper.resolveConfigDir();
    final String[] contextFiles = this.configDir.list(this.springFilenameFilter);
    final int len = contextFiles == null ? 0 : contextFiles.length;
    if (len > 0) {
      this.configFiles = Arrays.asList(contextFiles);
      LOGGER.debug("Found {} config files.", len);
    }
    else {
      LOGGER.debug("No config files found!");
      this.configFiles = new ArrayList<String>();
    }
  }

  String getContextLocation(final String location) {
    String newloc = location;
    final String[] contextFiles = StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS);
    for (final String contextFile : contextFiles) {
      final String overridingFile = findOverridingFile(contextFile);
      if (overridingFile != null) {
        newloc = location.replaceAll(contextFile, overridingFile);
      }
    }
    return newloc;
  }

  private String findOverridingFile(final String contextFile) {
    String overridingFile = null;
    final String shortFileName = new File(contextFile).getName();
    if (this.configFiles.contains(shortFileName)) {
      overridingFile = "file:" + this.configDir.getPath().replace('\\', '/') + "/" + shortFileName;
      LOGGER.info("Overriding spring context file {} with {}", contextFile, overridingFile);
    }
    return overridingFile;
  }
}
