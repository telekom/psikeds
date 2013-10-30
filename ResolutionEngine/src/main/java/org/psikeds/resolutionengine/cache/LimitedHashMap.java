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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Extension of the LinkedHashMap with limited size. If the size of the Map
 * exceeds the defined limit the least recently used entries will be removed.
 * 
 * A maximum map size <= 0 is interpreted as unlimited map.
 * 
 * For further information also see {@link java.util.LinkedHashMap}
 * 
 * @author marco@juliano.de
 * 
 */
public class LimitedHashMap<K, V> extends LinkedHashMap<K, V> {

  private static final long serialVersionUID = 1L;

  public static final float DEFAULT_LOAD_FACTOR = 0.75f;
  public static final int DEFAULT_INITIAL_CAPACITY = 1 << 6;
  public static final int DEFAULT_MAX_MAP_SIZE = DEFAULT_INITIAL_CAPACITY << 5;
  public static final boolean LRU_ACCESS_ORDER = true;

  private int maxSize;

  protected LimitedHashMap() {
    this(DEFAULT_MAX_MAP_SIZE);
  }

  protected LimitedHashMap(final int maxSize) {
    this(maxSize, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, LRU_ACCESS_ORDER);
  }

  protected LimitedHashMap(final int maxSize, final int initialCapacity, final float loadFactor, final boolean accessOrder) {
    super(((maxSize > 0) && (maxSize < initialCapacity) ? maxSize : initialCapacity), loadFactor, accessOrder);
    this.maxSize = maxSize;
  }

  public int getMaxSize() {
    return this.maxSize;
  }

  public void setMaxSize(final int maxSize) {
    this.maxSize = maxSize;
  }

  /**
   * Remove the eldest Entry whenever we exceed the maximum size.
   */
  @Override
  protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
    return (this.maxSize > 0) && (this.maxSize < size());
  }

  /**
   * Clear all references to Objects when Map is finalized.
   */
  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    this.clear();
  }
}
