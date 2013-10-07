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
 * A maximum map size < 0 is interpreted as unlimited map.
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

  private int maxMapSize;

  public LimitedHashMap() {
    this(DEFAULT_MAX_MAP_SIZE);
  }

  public LimitedHashMap(final int maxMapSize) {
    this(maxMapSize, DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, LRU_ACCESS_ORDER);
  }

  public LimitedHashMap(final int maxMapSize, final int initialCapacity, final float loadFactor, final boolean accessOrder) {
    super(initialCapacity, loadFactor, accessOrder);
    this.maxMapSize = maxMapSize;
  }

  public int getMaxMapSize() {
    return this.maxMapSize;
  }

  public void setMaxMapSize(final int maxMapSize) {
    this.maxMapSize = maxMapSize;
  }

  @Override
  protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
    return this.maxMapSize > 0 && this.maxMapSize < size();
  }
}
