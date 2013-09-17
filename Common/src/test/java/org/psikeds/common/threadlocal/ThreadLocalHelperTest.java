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
package org.psikeds.common.threadlocal;

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
  public static void setUpBeforeClass() throws Exception {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  /**
   * Test method for {@link org.psikeds.common.threadlocal.ThreadLocalHelper#cleanThreadLocalMaps()}.
   */
  @Test
  public void testCleanThreadLocalMaps() throws Exception {
    ThreadLocalHelper.enabled = false;
    ThreadLocalHelper.cleanThreadLocalMaps();
  }

  public static void main(final String[] args) {
    try {
      setUpBeforeClass();
      new ThreadLocalHelperTest().testCleanThreadLocalMaps();
    }
    catch (final Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }
}
