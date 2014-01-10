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
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

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
 * Testcase checking Caching and LRU-Expiry of Sessions and Objects.
 * 
 * @author marco@juliano.de
 * 
 */
public class CacheStressTest {

  private static final String LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "./src/main/resources/log4j.xml");
  private static final Logger LOGGER = LoggerFactory.getLogger(CacheStressTest.class);

  private IdGenerator sidgen;
  private ResolutionCache cache;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    DOMConfigurator.configure(LOG4J);
  }

  @Before
  public void setUp() throws NoSuchAlgorithmException {
    this.sidgen = new SessionIdGenerator();
    this.cache = new ResolutionCache();
  }

  @After
  public void tearDown() {
    this.cache.clear();
  }

  @Test
  public void testCacheAndLRU() {
    boolean ok = true;
    final long start = System.currentTimeMillis();
    long end = start;
    long duration = 0;

    LOGGER.info("Starting test of ResolutionCache ...");
    try {
      final int numSessions = this.cache.getMaxSessionsPerServer();
      LOGGER.info("... max. Sessions per Server = " + numSessions + " ...");
      final int numObjects = this.cache.getMaxObjectsPerSession();
      LOGGER.info("... max. Objects per Session = " + numObjects + " ...");

      final String firstId = this.sidgen.getNextId();
      final String firstKey = firstId;
      final Serializable firstdata = firstId;
      LOGGER.trace("Saving first data: " + firstId);
      this.cache.saveObject(firstId, firstKey, firstdata);

      final String secondId = this.sidgen.getNextId();
      final String secondKey = secondId;
      final Serializable seconddata = secondId;
      LOGGER.trace("Saving second data: " + firstId);
      this.cache.saveObject(secondId, secondKey, seconddata);

      final int sessCount = numSessions - 1;
      LOGGER.info(" ... creating " + sessCount + " additional Cache-Entries ...");
      for (int sessIDX = 0; sessIDX < sessCount; sessIDX++) {
        final String sid = this.sidgen.getNextId();
        final String key = sid;
        final Serializable saved = sid;
        this.cache.saveObject(sid, key, saved);
        final Serializable loaded = this.cache.getObject(sid, key);
        assertNotNull("Cannot load cached Object from Cache.", loaded);
        assertEquals("Original Object and Object loaded from Cache are not equal.", saved, loaded);
        if ((sessIDX % 10) == 0) {
          LOGGER.trace("... checking that first Session Data still exists ...");
          final CacheEntry firstEntry = this.cache.getSession(firstId, false);
          assertNotNull("Cannot find first Cache-Entry.", firstEntry);

          final int objCount = numObjects + 1;
          LOGGER.trace(" ... creating " + objCount + " additional Objects for first Cache-Entry ...");
          for (int objIDX = 0; objIDX < objCount; objIDX++) {
            final Serializable reloadedFirstData = this.cache.getObject(firstId, firstKey);
            assertNotNull("Cannot find Data of first Session in Cache.", firstdata);
            assertEquals("Original first Data and first Data reloaded from Cache are not equal.", firstdata, reloadedFirstData);
            final String newKey = this.sidgen.getNextId();
            final Serializable newObj = newKey;
            this.cache.saveObject(firstId, newKey, newObj);
          }
        }
      }

      LOGGER.info("... checking Expiry of Cache-Entries ...");
      final Serializable reloadedFirstData = this.cache.getObject(firstId, firstKey);
      LOGGER.trace("First Session Data = " + reloadedFirstData);
      assertNotNull("Cannot find first Session Data in Cache.", reloadedFirstData);

      final Serializable reloadedSecondData = this.cache.getObject(secondId, secondKey);
      LOGGER.trace("Second Session Data = " + reloadedSecondData);
      assertNull("Second Session Data did not expire after " + sessCount + " additional Cache-Entries.", reloadedSecondData);

      final int cachesize = this.cache.size();
      LOGGER.trace("Cache size is: " + cachesize);
      assertEquals("Cache size is " + cachesize + ", not expected " + numSessions, cachesize, numSessions);

      ok = true;
    }
    catch (final Throwable t) {
      ok = false;
      final String message = "Cache Error: " + t.getMessage();
      LOGGER.error(message, t);
      if (t instanceof AssertionError) {
        throw (AssertionError) t;
      }
      else {
        fail(message);
      }
    }
    finally {
      end = System.currentTimeMillis();
      duration = end - start;
      LOGGER.info(" ... test of ResolutionCache finished after " + duration + " milliseconds " + (ok ? "without problems." : "with Errors!"));
    }
  }
}
