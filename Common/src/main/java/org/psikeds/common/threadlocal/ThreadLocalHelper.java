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

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

/**
 * Many Libraries do not handle ThreadLocals correctly, especially do not
 * remove Entries from ThreadLocalMaps when finished. This can result in
 * Memory Leaks or Data leaking between Threads (e.g. when the Application
 * is undeployed from an Application Server and its Threads are reused for
 * handling requests of other Applications).
 * 
 * Problem is that (for very good reason!) you cannot access the local
 * variables of other Threads. So in order to clean the mess up, we will
 * use Reflection to circumvent the visibilty of Fields and Methods
 * (hopefully the Security-Manager does not prevent us!) and will then
 * try to perform some serious magic. ;-)
 * 
 * @author marco@juliano.de
 * 
 */
public final class ThreadLocalHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalHelper.class);

  private ThreadLocalHelper() {
    // static helper, prevent instantiation
  }

  // ---------------------------------------------------------------------

  public static boolean enabled = false;

  /**
   * Check all ThreadLocalMaps of all Threads for Objects created by our
   * Application (i.e. our ClassLoader) and remove those Entries.
   * 
   * Note: Be careful, this Method is intended to be invoked by an
   * Application having a separate ClassLoader (e.g. WAR or EAR deployed
   * on an Application Server). Running it from within a standalone
   * Java-Program or IDE might have unexpected results!
   * 
   */
  public static void cleanThreadLocalMaps() {
    try {
      LOGGER.trace("--> cleanThreadLocalMaps(); enabled = {}", enabled);

      // For each Thread its ThreadLocals are stored in the two Fields named
      // 'threadLocals' and 'inheritableThreadLocal' of the inner Type/Class
      // 'ThreadLocal.ThreadLocalMap'.
      // We need to make both Fields of the Class Thread accessible:
      final Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
      threadLocalsField.setAccessible(true);
      final Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
      inheritableThreadLocalsField.setAccessible(true);

      // Also make the Table of all Threadlocal-Variables accessible:
      // Note: 'table' is of Type 'ThreadLoad.ThreadLocalMap.Entry'
      final Class<?> threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
      final Field tableField = threadLocalMapClass.getDeclaredField("table");
      tableField.setAccessible(true);

      // We also need access to the Method ThreadLoad.ThreadLocalMap.expungeStaleEntries():
      final Method expungeStaleEntriesMethod = threadLocalMapClass.getDeclaredMethod("expungeStaleEntries");
      expungeStaleEntriesMethod.setAccessible(true);

      // Last but not least for removing Entires we must be able to invoke the Method
      // ThreadLoad.ThreadLocalMap.remove(ThreadLocal key):
      final Method removeThreadLocalMethod = threadLocalMapClass.getDeclaredMethod("remove", ThreadLocal.class);
      removeThreadLocalMethod.setAccessible(true);

      // If we reach this Point (i.e. calling setAccessible(true) did not throw any
      // SecurityExceptions), then we can now get all Threads:
      final Thread[] threads = getThreads();
      final int numThreads = threads == null ? 0 : threads.length;
      for (int i = 0; i < numThreads; i++) {
        final Thread t = threads[i];
        if (t != null) {
          LOGGER.debug("Checking ThreadLocalMap of {}", t);
          // Clear the first map (Thread.threadLocals)
          Object threadLocalMap = threadLocalsField.get(t);
          if (threadLocalMap != null) {
            expungeStaleEntriesMethod.invoke(threadLocalMap);
            cleanThreadLocalMaps(threadLocalMap, tableField, removeThreadLocalMethod);
          }
          // Clear the second map (Thread.inheritableThreadLocals)
          threadLocalMap = inheritableThreadLocalsField.get(t);
          if (threadLocalMap != null) {
            expungeStaleEntriesMethod.invoke(threadLocalMap);
            cleanThreadLocalMaps(threadLocalMap, tableField, removeThreadLocalMethod);
          }
        }
      }
    }
    catch (final Throwable t) {
      // Just catch anything, we do not want to let the Application fail because of this.
      LOGGER.warn("Could not clean ThreadLocalMaps: " + t.getMessage(), t);
    }
    finally {
      LOGGER.trace("<-- cleanThreadLocalMaps(); enabled = {}", enabled);
    }
  }

  // ---------------------------------------------------------------------

  /**
   * Get all active Threads of our Root-ThreadGroup.
   * 
   * @return Array of Threads
   */
  private static Thread[] getThreads() {
    int threadCountActual = 0;
    try {
      LOGGER.trace("--> getThreads()");
      ThreadGroup rootgroup = Thread.currentThread().getThreadGroup();
      while (rootgroup.getParent() != null) {
        rootgroup = rootgroup.getParent();
      }
      // Note: ThreadGroup.enumerate(Thread[]) silently ignores any Thread
      // that won't fit into the Array. Therefore we must make sure that
      // we do not miss any Threads by continuously increasing the Size of
      // the Array.
      Thread[] threads = null;
      int threadCountGuess = rootgroup.activeCount();
      do {
        threadCountGuess *= 2;
        threads = new Thread[threadCountGuess];
        threadCountActual = rootgroup.enumerate(threads);
      }
      while (threadCountActual == threadCountGuess);
      return threads;
    }
    finally {
      LOGGER.trace("<-- getThreads(); #Threads = {}", threadCountActual);
    }
  }

  /**
   * Analyzes a given ThreadLocalMap and remove all Entries loaded by our ClassLoader/Application.
   * 
   * @param map
   *          ThreadLocalMap to be analyzed and cleaned.
   * @param internalTableField
   *          Actual Field pointing to 'table'; This avoids re-calculation on every call to this
   *          method.
   * @param removeThreadLocalMethod
   *          Method to be invoked for removing an Entry
   * 
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws NoSuchFieldException
   * @throws InvocationTargetException
   */
  private static void cleanThreadLocalMaps(final Object threadLocalMap, final Field internalTableField, final Method removeThreadLocalMethod)
      throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException, InvocationTargetException {
    try {
      LOGGER.trace("--> cleanThreadLocalMaps()");
      final Object[] table = ((threadLocalMap == null) || (internalTableField == null)) ? null : (Object[]) internalTableField.get(threadLocalMap);
      final int tablength = table == null ? 0 : table.length;
      for (int i = 0; i < tablength; i++) {
        final Object obj = table[i];
        if (obj != null) {
          boolean shouldBeRemoved = false;
          // Check the key
          final Object key = ((Reference<?>) obj).get();
          if (loadedByOurClassloaderOrChild(key)) {
            shouldBeRemoved = true;
          }
          // Check the value
          final Field valueField = obj.getClass().getDeclaredField("value");
          valueField.setAccessible(true);
          final Object value = valueField.get(obj);
          if (loadedByOurClassloaderOrChild(value)) {
            shouldBeRemoved = true;
          }
          if (shouldBeRemoved) {
            remove(threadLocalMap, key, value, removeThreadLocalMethod);
          }
        }
      }
    }
    finally {
      LOGGER.trace("<-- cleanThreadLocalMaps()");
    }
  }

  /**
   * Remove a a given Object (key + value) from the ThreadLocalMap.
   * 
   * @param internalTableField
   *          Field pointing to 'table' holding the ThreadLocals.
   * @param key
   *          Key of the Object to be removed
   * @param value
   *          Value of the Object to be removed
   * @param removeThreadLocalMethod
   *          Method to be invoked for removing an Entry
   * 
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private static void remove(final Object threadLocalMap, final Object key, final Object value, final Method removeThreadLocalMethod)
      throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    try {
      LOGGER.trace("--> remove()");
      final String objStr = prettyPrintObject(key, value);
      if (enabled) {
        LOGGER.debug("Removing Object from ThreadLocal-Table: {}", objStr);
        removeThreadLocalMethod.invoke(threadLocalMap, key);
      }
      else {
        LOGGER.debug("Found Object that should be removed from ThreadLocal-Table: {}", objStr);
      }
    }
    finally {
      LOGGER.trace("<-- remove()");
    }
  }

  /**
   * Check whether an Object was loaded by the same ClassLoader (or one of its Childs)
   * as this Class, i.e. whether Object was created by our Application.
   * 
   * @param obj
   *          Object to be checked.
   * @return True if same ClassLoader was used, false else.
   */
  private static boolean loadedByOurClassloaderOrChild(final Object obj) {
    boolean ret = false;
    try {
      LOGGER.trace("--> loadedByOurClassloaderOrChild()");
      // no object, nothing to do
      if (obj == null) {
        LOGGER.trace("Object is null.");
        ret = false;
        return ret;
      }
      // object loaded by same classloader as ourself?
      final ClassLoader ownCL = getOwnClassLoader();
      ClassLoader objCL = getClassLoader(obj);
      while ((ownCL != null) && (objCL != null)) {
        if (ownCL == objCL) {
          LOGGER.trace("Object was loaded by our ClassLoader: {}", obj);
          ret = true;
          return ret;
        }
        objCL = objCL.getParent();
      }
      // is object a collection? then check its contents
      if (obj instanceof Collection<?>) {
        final Iterator<?> iter = ((Collection<?>) obj).iterator();
        try {
          while (iter.hasNext()) {
            final Object entry = iter.next();
            if (loadedByOurClassloaderOrChild(entry)) {
              LOGGER.trace("Object is Collection and contains Entry loaded by our ClassLoader: {}", entry);
              ret = true;
              return ret;
            }
          }
        }
        catch (final ConcurrentModificationException cme) {
          LOGGER.warn("loadedByOurClassloaderOrChild: Concurrent modification of " + String.valueOf(obj), cme);
        }
      }
      LOGGER.trace("Object was not loaded by our ClassLoader/Application: {}", obj);
      ret = false;
      return ret;
    }
    finally {
      LOGGER.trace("<-- loadedByOurClassloaderOrChild(); ret = {}", ret);
    }
  }

  /**
   * Get ClassLoader that loaded a given Object.
   * 
   * @param obj
   *          Object
   * @return ClassLoader
   */
  private static ClassLoader getClassLoader(final Object obj) {
    Class<?> clazz = null;
    ClassLoader cl = null;
    try {
      LOGGER.trace("--> getClassLoader(); obj = {}", obj);
      if (obj != null) {
        if (obj instanceof Class) {
          clazz = (Class<?>) obj;
        }
        else {
          clazz = obj.getClass();
        }
      }
      cl = (clazz == null ? null : clazz.getClassLoader());
      if (cl == null) {
        cl = ClassLoader.getSystemClassLoader();
      }
      return cl;
    }
    finally {
      LOGGER.trace("<-- getClassLoader(); clazz = {}; cl = {}", clazz, cl);
    }
  }

  /**
   * Get our own ClassLoader. Just for convenience.
   * 
   * @return ClassLoader
   */
  private static ClassLoader getOwnClassLoader() {
    return getClassLoader(ThreadLocalHelper.class);
  }

  /**
   * Create a String like: Key [ Class ] = Value [ Class ]
   * 
   * @param key
   *          Key of the Object
   * @param value
   *          Value of the Object
   * @return String
   * 
   */
  private static String prettyPrintObject(final Object key, final Object value) {
    final StringBuilder sb = new StringBuilder();
    if (key != null) {
      sb.append(getPrettyObjectValue(key));
      sb.append(" [ ");
      sb.append(getPrettyClassName(key));
      sb.append(" ] = ");
    }
    if (value != null) {
      sb.append(getPrettyObjectValue(value));
      sb.append(" [ ");
      sb.append(getPrettyClassName(value));
      sb.append(" ]");
    }
    return sb.toString();
  }

  /**
   * Get canonical class name if possible, simple class name else.
   * 
   * @param obj
   *          Object or Class
   * @return String
   * 
   */
  private static String getPrettyClassName(final Object obj) {
    String pcn = null;
    try {
      if (obj != null) {
        if (obj instanceof Class<?>) {
          final Class<?> clazz = (Class<?>) obj;
          pcn = clazz.getCanonicalName();
          if (StringUtils.isEmpty(pcn)) {
            pcn = clazz.getName();
          }
        }
        else {
          pcn = getPrettyClassName(obj.getClass());
        }
      }
    }
    catch (final Throwable t) {
      pcn = null;
    }
    return (pcn == null ? "" : pcn);
  }

  /**
   * Paranoid version of tostring()
   * 
   * @param obj
   *          Object
   * @return String
   * 
   */
  private static String getPrettyObjectValue(final Object obj) {
    try {
      return obj.toString();
    }
    catch (final Throwable t) {
      // either obj is null or has a very strange toString() method.
      return "";
    }
  }
}
