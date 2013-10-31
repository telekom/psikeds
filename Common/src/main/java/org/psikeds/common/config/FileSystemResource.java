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

/**
 * An extension of the Spring-FileSystemResource. All Files are automatically
 * resolved within the Configuration-Directory.
 * 
 * @author marco@juliano.de
 * 
 */
public class FileSystemResource extends org.springframework.core.io.FileSystemResource {

  public FileSystemResource(final File file) {
    super(ConfigDirectoryHelper.resolveConfigFile(file));
  }

  public FileSystemResource(final String path) {
    super(ConfigDirectoryHelper.resolveConfigFile(path));
  }

  public FileSystemResource(final org.springframework.core.io.FileSystemResource fsr) {
    this(fsr.getFile());
  }

  public FileSystemResource(final org.springframework.core.io.UrlResource ur) throws IOException {
    this(ur.getFile());
  }
}
