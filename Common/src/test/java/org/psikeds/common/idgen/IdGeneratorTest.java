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
package org.psikeds.common.idgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.apache.commons.lang.StringUtils;

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

  @Test
  public void testRandomStringGenerator() {
    try {
      final RandomStringGenerator gen = new RandomStringGenerator();
      checkRandomness(gen, 200);
    }
    catch (final NoSuchAlgorithmException nsae) {
      // nsae.printStackTrace();
      fail(nsae.getMessage());
    }
  }

  @Test
  public void testRequestIdGenerator() {
    try {
      final RequestIdGenerator gen = new RequestIdGenerator();
      checkRandomness(gen, 200);
    }
    catch (final NoSuchAlgorithmException nsae) {
      // nsae.printStackTrace();
      fail(nsae.getMessage());
    }
  }

  @Test
  public void testSessionIdGenerator() {
    try {
      final SessionIdGenerator gen = new SessionIdGenerator();
      checkRandomness(gen, 200);
    }
    catch (final NoSuchAlgorithmException nsae) {
      // nsae.printStackTrace();
      fail(nsae.getMessage());
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
      // System.out.println("===> Beginning test of " + String.valueOf(gen));
      final List<String> prevlst = new ArrayList<String>();
      for (int i = 0; i < numOfIterations; i++) {
        final String cur = gen.getNextId();
        assertFalse("Empty ReqId after " + i + " iterations.", StringUtils.isEmpty(cur));
        // System.out.println("" + i + ".: " + cur);
        assertFalse("Duplicate ReqIds " + cur + " after " + i + " iterations.", prevlst.contains(cur));
        prevlst.add(cur);
      }
      final int num = prevlst.size();
      assertEquals("Number of generated IDs should be " + numOfIterations + " but is " + num, numOfIterations, num);
    }
    finally {
      // System.out.println("<=== Finished test of " + String.valueOf(gen));
    }
  }
}
