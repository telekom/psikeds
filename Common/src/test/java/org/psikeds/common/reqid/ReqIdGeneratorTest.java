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
package org.psikeds.common.reqid;

import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import org.psikeds.common.reqid.impl.RandomStringGenerator;
import org.psikeds.common.reqid.impl.UniqueIdGenerator;

/**
 * Unit-Tests for ReqIdGenerators
 * 
 * @author marco@juliano.de
 */
public class ReqIdGeneratorTest {

  @Test
  public void testRandomIdGenerator() {
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
      final UniqueIdGenerator gen = new UniqueIdGenerator();
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
  private void checkRandomness(final RequestIdGenerator gen, final int numOfIterations) {
    // System.out.println("===== Testing " + String.valueOf(gen));
    final List<String> prevlst = new ArrayList<String>();
    for (int i = 0; i < numOfIterations; i++) {
      final String cur = gen.getNextReqId();
      if (StringUtils.isEmpty(cur)) {
        fail("Empty ReqId after " + i + " iterations.");
      }
      // System.out.println(cur);
      if (prevlst.contains(cur)) {
        fail("Duplicate ReqIds " + cur + " after " + i + " iterations.");
      }
      prevlst.add(cur);
    }
  }
}
