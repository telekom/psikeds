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
package org.psikeds.common.idgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.idgen.impl.RandomStringGenerator;
import org.psikeds.common.idgen.impl.RequestIdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;

/**
 * Unit-Tests for IdGenerators
 * 
 * @author marco@juliano.de
 * 
 */
public class IdGeneratorTest {

  private static final String LOG4J = "../ResolutionEngine/src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(IdGeneratorTest.class);
  private static final int NUM_ITERATIONS = 200;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Test
  public void testRandomStringGenerator() {
    try {
      final RandomStringGenerator gen = new RandomStringGenerator();
      checkRandomness(gen, NUM_ITERATIONS);
    }
    catch (final Exception ex) {
      final String message = "Test of RandomStringGenerator failed: " + ex.getMessage();
      LOGGER.error(message, ex);
      fail(message);
    }
  }

  @Test
  public void testRequestIdGenerator() {
    try {
      final RequestIdGenerator gen = new RequestIdGenerator();
      checkRandomness(gen, NUM_ITERATIONS);
    }
    catch (final Exception ex) {
      final String message = "Test of RequestIdGenerator failed: " + ex.getMessage();
      LOGGER.error(message, ex);
      fail(message);
    }
  }

  @Test
  public void testSessionIdGenerator() {
    try {
      final SessionIdGenerator gen = new SessionIdGenerator();
      checkRandomness(gen, NUM_ITERATIONS);
    }
    catch (final Exception ex) {
      final String message = "Test of SessionIdGenerator failed: " + ex.getMessage();
      LOGGER.error(message, ex);
      fail(message);
    }
  }

  /**
   * Check that the Generator gen is random enough to generate at least
   * numOfIterations ids without any duplicates
   * 
   * @param gen
   * @param numOfIterations
   * @throws Exception
   */
  private void checkRandomness(final IdGenerator gen, final int numOfIterations) {
    try {
      LOGGER.info("Starting test of " + String.valueOf(gen) + " ...");
      final List<String> prevlst = new ArrayList<String>();
      for (int i = 1; i <= numOfIterations; i++) {
        final String cur = gen.getNextId();
        assertFalse("Empty ReqId after " + i + " iterations.", StringUtils.isEmpty(cur));
        LOGGER.trace("" + i + ".: " + cur);
        assertFalse("Duplicate ReqIds " + cur + " after " + i + " iterations.", prevlst.contains(cur));
        prevlst.add(cur);
      }
      final int num = prevlst.size();
      assertEquals("Number of generated IDs should be " + numOfIterations + " but is " + num, numOfIterations, num);
    }
    finally {
      LOGGER.info(" ... test of " + String.valueOf(gen) + " finished.");
    }
  }
}
