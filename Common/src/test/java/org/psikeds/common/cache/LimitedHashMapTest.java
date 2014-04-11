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
package org.psikeds.common.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

/**
 * Testcase checking that the Limited-Hash-Map is really limited
 * and working as expected.
 * 
 * @author marco@juliano.de
 * 
 */
public class LimitedHashMapTest {

  private LimitedHashMap<String, Integer> map;

  @After
  public void tearDown() {
    clearMap();
  }

  @Test
  public void testLimited() {
    final int maxSize = 200;
    initMap(maxSize);
    for (int i = 0; i <= maxSize; i++) {
      // store values from 0 to 200 in map, i.e. one more than maximum size!
      final String key = String.valueOf(i);
      final Integer value = new Integer(i);
      this.map.put(key, value);
    }
    checkMissingElement(0); // must be expired and removed from cache
    checkExistingElement(100);
    checkExistingElement(200);
    checkMissingElement(300); // never added
  }

  @Test
  public void testUnlimited() {
    initMap(-1);
    for (int i = 0; i <= 300; i++) {
      // store values from 0 to 300 in map
      final String key = String.valueOf(i);
      final Integer value = new Integer(i);
      this.map.put(key, value);
    }
    // all values must be present
    checkExistingElement(0);
    checkExistingElement(100);
    checkExistingElement(200);
    checkExistingElement(300);
  }

  // ----------------------------------------------------------------

  private void clearMap() {
    if (this.map != null) {
      this.map.clear();
      this.map = null;
    }
  }

  private void initMap() {
    clearMap();
    this.map = new LimitedHashMap<String, Integer>();
  }

  private void initMap(final int maxSize) {
    initMap();
    this.map.setMaxSize(maxSize);
  }

  // ----------------------------------------------------------------

  private void checkMissingElement(final int index) {
    assertNull("Element " + index + " still in Map!", getElement(index));
  }

  private void checkExistingElement(final int index) {
    final Integer element = getElement(index);
    assertNotNull("Element " + index + " not found in Map!", element);
    final int intval = element.intValue();
    assertTrue("Value was " + intval + " and not expected " + index, intval == index);
  }

  private Integer getElement(final int index) {
    return this.map.get(String.valueOf(index));
  }
}
