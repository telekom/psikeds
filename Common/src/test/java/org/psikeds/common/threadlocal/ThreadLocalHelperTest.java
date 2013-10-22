/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.common.threadlocal;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Unit-Test for ThreadLocalHelper
 * 
 * @author marco@juliano.de
 * 
 */
public class ThreadLocalHelperTest {

  private static final String LOG4J = "../ResolutionEngine/src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalHelperTest.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  /**
   * Test method for {@link org.psikeds.common.threadlocal.ThreadLocalHelper#cleanThreadLocalMaps()}
   * .
   */
  @Test
  public void testCleanThreadLocalMaps() {
    try {
      ThreadLocalHelper.enabled = false;
      ThreadLocalHelper.cleanThreadLocalMaps();
    }
    catch (final Exception ex) {
      final String message = "Cannot check Thread-Local-Maps: " + ex.getMessage();
      LOGGER.error(message, ex);
      fail(message);
    }
  }

  public static void main(final String[] args) {
    setUpBeforeClass();
    new ThreadLocalHelperTest().testCleanThreadLocalMaps();
  }
}
