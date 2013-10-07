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
package org.psikeds.resolutionengine.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.common.idgen.IdGenerator;
import org.psikeds.common.idgen.impl.SessionIdGenerator;

/**
 * Testcase checking Caching and LRU-Expiry
 *
 * @author marco@juliano.de
 *
 */
public class CacheStressTest {

  private static final String LOG4J = "./src/main/resources/log4j.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheStressTest.class);

  private IdGenerator sidgen;
  private ResolutionCache cache;
  private int maxCacheEntries;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws Exception {
    this.sidgen = new SessionIdGenerator();
    final LimitedHashMap<String, Object> map = new LimitedHashMap<String, Object>();
    this.maxCacheEntries = map.getMaxMapSize();
    this.cache = new ResolutionCache(map);
    LOGGER.info("Created Cache with maximum Size of " + this.maxCacheEntries);
  }

  @After
  public void tearDown() {
    this.cache.clear();
  }

  @Test
  public void testCacheAndLRU() throws Exception {
    final String firstId = this.sidgen.getNextId();
    Object firstdata = firstId;
    LOGGER.info("Saving first data: " + firstId);
    this.cache.saveSessionData(firstId, firstdata);
    final String secondId = this.sidgen.getNextId();
    Object seconddata = secondId;
    LOGGER.info("Saving second data: " + firstId);
    this.cache.saveSessionData(secondId, seconddata);
    final int count = this.maxCacheEntries - 1;
    LOGGER.info("Creating " + count + " additional Cache-Entries.");
    for (int idx = 0; idx < count; idx++) {
      final String sid = this.sidgen.getNextId();
      final Object saved = sid;
      this.cache.saveSessionData(sid, saved);
      final Object loaded = this.cache.getSessionData(sid);
      assertEquals(saved, loaded);
      if (idx % 10 == 0) {
        firstdata = this.cache.getSessionData(firstId);
        assertNotNull("Cannot find first Session Data in Cache.", firstdata);
      }
    }
    firstdata = this.cache.getSessionData(firstId);
    LOGGER.info("First Session Data = " + firstdata);
    assertNotNull("Cannot find first Session Data in Cache.", firstdata);
    seconddata = this.cache.getSessionData(secondId);
    LOGGER.info("Second Session Data = " + seconddata);
    assertNull("Second Session Data did not expire after " + count + " additional Cache-Entries.", seconddata);
    final int cachesize = this.cache.size();
    LOGGER.info("Cache size is: " + cachesize);
    assertEquals("Cache size is " + cachesize + ", not expected " + this.maxCacheEntries, cachesize, this.maxCacheEntries);
  }
}
