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
package org.psikeds.resolutionengine.datalayer.knowledgebase.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.FloatFeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.IntegerFeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;

public class ComparatorTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorTest.class);

  private static String LOG4J;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "../ResolutionEngine/src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
  }

  /**
   * Test method for FeatureValueComparator and FeatureValueHelper.
   */
  @Test
  public void test1_FeatureValueHelper() throws Exception {
    boolean ok = false;
    LOGGER.info("Starting test of FeatureValueComparator and FeatureValueHelper ...");
    try {
      LOGGER.info("... creating Feature-Values ...");
      final FeatureValue fvi1 = new IntegerFeatureValue("FVI1", "FVI1", "1");
      final FeatureValue fvf1 = new FloatFeatureValue("FVF1", "FVF1", "1.000");
      final FeatureValue fvf2 = new FloatFeatureValue("FVF2", "FVF2", "2.000");
      final FeatureValue fvs2 = new FeatureValue("FVS2", "FVS2", "2.000");
      final FeatureValue fv3 = new IntegerFeatureValue("FV3", "FV3", "3");
      final FeatureValue fv4 = new FloatFeatureValue("FV4", "FV4", "4.0");
      final FeatureValue fv5 = new FeatureValue("FV5", "FV5", "5");
      final IntegerFeatureValue fvi6 = new IntegerFeatureValue("FVI6", "FVI6", "6");
      final FeatureValue fv7 = new FloatFeatureValue("FV7", "FV7", "7");
      final FeatureValue fv8 = new IntegerFeatureValue("FV8", "FV8", "8");
      final FloatFeatureValue fvf9 = new FloatFeatureValue("FVF9", "FVF9", "9.000003267");
      fvf9.setScale(2);
      LOGGER.trace("fvf9 = {}", fvf9);
      final FeatureValue fvs9 = new FeatureValue("FVS9", "FVS9", "9");
      final FeatureValue fv10 = new FeatureValue("FV10", "FV10", "10.000");
      LOGGER.info("... comparing single Feature-Values ...");
      assertTrue(FeatureValueHelper.isEqual(fvi1, fvf1));
      assertFalse(FeatureValueHelper.notEqual(fvi1, fvf1));
      assertTrue(FeatureValueHelper.isEqual(fvf2, fvs2));
      assertFalse(FeatureValueHelper.notEqual(fvf2, fvs2));
      assertTrue(FeatureValueHelper.isEqual(fvf9, fvs9));
      assertTrue(FeatureValueHelper.notEqual(fv3, fvf9));
      assertTrue(FeatureValueHelper.notEqual(fvs2, fv10));
      assertTrue(FeatureValueHelper.greaterThan(fv7, fv5));
      assertTrue(FeatureValueHelper.greaterOrEqual(fv7, fv5));
      assertTrue(FeatureValueHelper.lessThan(fv3, fvs9));
      assertTrue(FeatureValueHelper.lessOrEqual(fv3, fvs9));
      LOGGER.info("... creating and shuffling List of Feature-Values ...");
      final List<FeatureValue> lst = new ArrayList<FeatureValue>();
      lst.add(fvi1);
      lst.add(fvf2);
      lst.add(fv3);
      lst.add(fv4);
      lst.add(fv5);
      lst.add(fvi6);
      lst.add(fv7);
      lst.add(fv8);
      lst.add(fvf9);
      lst.add(fv10);
      FeatureValueHelper.shuffle(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... reversing Order of List of Feature-Values ...");
      FeatureValueHelper.reverse(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... getting minimum Feature-Value ...");
      final FeatureValue min = FeatureValueHelper.min(lst);
      LOGGER.trace("MIN = {}", min);
      assertNotNull(min);
      assertEquals(fvi1, min);
      LOGGER.info("... getting maximum Feature-Value ...");
      final FeatureValue max = FeatureValueHelper.max(lst);
      LOGGER.trace("MAX = {}", min);
      assertNotNull(max);
      assertEquals(fv10, max);
      LOGGER.info("... sorting List of Feature-Values ...");
      FeatureValueHelper.sort(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... looking for certain Feature-Value ...");
      assertEquals(5, FeatureValueHelper.find(lst, fvi6));
      assertEquals(0, FeatureValueHelper.find(lst, fvi1));
      assertEquals(9, FeatureValueHelper.find(lst, fv10));
      LOGGER.info("... shuffling List of Feature-Values once again ...");
      FeatureValueHelper.shuffle(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... comparing Lists and Value-Ranges ...");
      List<FeatureValue> result = FeatureValueHelper.isEqual(lst, new FloatFeatureValue("3", "3", "3"));
      assertEquals(1, result.size());
      assertTrue(result.contains(fv3));
      result = FeatureValueHelper.notEqual(lst, new FeatureValue("3", "3", "3.00"));
      assertEquals(9, result.size());
      assertFalse(result.contains(fv3));
      assertTrue(result.contains(fvi1));
      assertTrue(result.contains(fv10));
      result = FeatureValueHelper.lessThan(lst, new IntegerFeatureValue("4", "4", 4));
      assertEquals(3, result.size());
      assertTrue(result.contains(fv3));
      assertFalse(result.contains(fv4));
      assertFalse(result.contains(fv5));
      result = FeatureValueHelper.lessOrEqual(lst, new FeatureValue("4", "4", "4"));
      assertEquals(4, result.size());
      assertTrue(result.contains(fv3));
      assertTrue(result.contains(fv4));
      assertFalse(result.contains(fv5));
      result = FeatureValueHelper.greaterThan(lst, new FeatureValue("8", "8", "8"));
      assertEquals(2, result.size());
      assertTrue(result.contains(fv10));
      assertTrue(result.contains(fvf9));
      assertFalse(result.contains(fv8));
      assertFalse(result.contains(fv7));
      result = FeatureValueHelper.greaterOrEqual(lst, new FloatFeatureValue("8", "8", 8.000345f, 3));
      assertEquals(3, result.size());
      assertFalse(result.contains(fv7));
      assertTrue(result.contains(fv8));
      assertTrue(result.contains(fvf9));
      assertTrue(result.contains(fv10));
      LOGGER.info("... shuffling List of Feature-Values once again ...");
      FeatureValueHelper.shuffle(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... checking Limits of Value-Range ...");
      final FeatureValue nothing = null;
      result = FeatureValueHelper.isEqual(lst, nothing);
      assertNull(result);
      result = FeatureValueHelper.notEqual(lst, nothing);
      assertNotNull(result);
      assertEquals(10, result.size());
      result = FeatureValueHelper.lessThan(lst, nothing);
      assertNull(result);
      result = FeatureValueHelper.greaterThan(lst, nothing);
      assertNotNull(result);
      assertEquals(10, result.size());
      final FeatureValue tooBig = new IntegerFeatureValue("TooBig", "TooBig", 999999);
      result = FeatureValueHelper.greaterThan(lst, tooBig);
      assertNull(result);
      result = FeatureValueHelper.lessThan(lst, tooBig);
      assertNotNull(result);
      assertEquals(10, result.size());
      result = FeatureValueHelper.lessOrEqual(lst, tooBig);
      assertNotNull(result);
      assertEquals(10, result.size());
      final FeatureValue tooSmall = new IntegerFeatureValue("TooSmall", "TooSmall", -99999);
      result = FeatureValueHelper.greaterOrEqual(lst, tooSmall);
      assertNotNull(result);
      assertEquals(10, result.size());
      result = FeatureValueHelper.greaterThan(lst, tooSmall);
      assertNotNull(result);
      assertEquals(10, result.size());
      result = FeatureValueHelper.lessThan(lst, tooSmall);
      assertNull(result);
      // done
      ok = true;
    }
    catch (final AssertionError ae) {
      ok = false;
      LOGGER.error("Functional Error: " + ae.getMessage(), ae);
      throw ae;
    }
    catch (final Throwable t) {
      ok = false;
      LOGGER.error("Technical Error: " + t.getMessage(), t);
      fail(t.getMessage());
    }
    finally {
      LOGGER.info("... test of FeatureValueComparator and FeatureValueHelper finished " + (ok ? "without problems." : "with ERRORS!!!"));
    }
  }

  /**
   * Test method for RelationHelper.
   */
  @Test
  public void test2_RelationHelper() throws Exception {
    boolean ok = false;
    LOGGER.info("Starting test of RelationHelper ...");
    try {
      LOGGER.info("... creating Feature-Values ...");
      final FeatureValue fvi1 = new IntegerFeatureValue("FVI1", "FVI1", "1");
      final FeatureValue fvf1 = new FloatFeatureValue("FVF1", "FVF1", "1.000");
      final FeatureValue fvf2 = new FloatFeatureValue("FVF2", "FVF2", "2.000");
      final FeatureValue fvs2 = new FeatureValue("FVS2", "FVS2", "2.000");
      final FeatureValue fv3 = new IntegerFeatureValue("FV3", "FV3", "3");
      final FeatureValue fv4 = new FloatFeatureValue("FV4", "FV4", "4.0");
      final FeatureValue fv5 = new FeatureValue("FV5", "FV5", "5");
      final IntegerFeatureValue fvi6 = new IntegerFeatureValue("FVI6", "FVI6", "6");
      final FeatureValue fv7 = new FloatFeatureValue("FV7", "FV7", "7");
      final FeatureValue fv8 = new IntegerFeatureValue("FV8", "FV8", "8");
      final FloatFeatureValue fvf9 = new FloatFeatureValue("FVF9", "FVF9", "9.000003267");
      fvf9.setScale(2);
      LOGGER.trace("fvf9 = {}", fvf9);
      final FeatureValue fvs9 = new FeatureValue("FVS9", "FVS9", "9");
      final FeatureValue fv10 = new FeatureValue("FV10", "FV10", "10.000");
      LOGGER.info("... applying Relation-Operator on single Feature-Values ...");
      assertTrue(RelationHelper.fulfillsOperation(fvi1, RelationOperator.EQUAL, fvf1));
      assertFalse(RelationHelper.fulfillsOperation(fvi1, RelationOperator.NOT_EQUAL, fvf1));
      assertTrue(RelationHelper.fulfillsOperation(fvf2, RelationOperator.EQUAL, fvs2));
      assertFalse(RelationHelper.fulfillsOperation(fvf2, RelationOperator.NOT_EQUAL, fvs2));
      assertTrue(RelationHelper.fulfillsOperation(fvf9, RelationOperator.EQUAL, fvs9));
      assertTrue(RelationHelper.fulfillsOperation(fv3, RelationOperator.NOT_EQUAL, fvf9));
      assertTrue(RelationHelper.fulfillsOperation(fvs2, RelationOperator.NOT_EQUAL, fv10));
      assertTrue(RelationHelper.fulfillsOperation(fv7, RelationOperator.GREATER_THAN, fv5));
      assertTrue(RelationHelper.fulfillsOperation(fv7, RelationOperator.GREATER_OR_EQUAL, fv5));
      assertTrue(RelationHelper.fulfillsOperation(fv3, RelationOperator.LESS_THAN, fvs9));
      assertTrue(RelationHelper.fulfillsOperation(fv3, RelationOperator.LESS_OR_EQUAL, fvs9));
      LOGGER.info("... creating and shuffling List of Feature-Values ...");
      final List<FeatureValue> lst = new ArrayList<FeatureValue>();
      lst.add(fvi1);
      lst.add(fvf2);
      lst.add(fv3);
      lst.add(fv4);
      lst.add(fv5);
      lst.add(fvi6);
      lst.add(fv7);
      lst.add(fv8);
      lst.add(fvf9);
      lst.add(fv10);
      FeatureValueHelper.shuffle(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... applying Relation-Operator on Lists and Value-Ranges ...");
      List<FeatureValue> result = RelationHelper.fulfillsOperation(lst, RelationOperator.EQUAL, new FeatureValue("3", "3", "3"));
      assertEquals(1, result.size());
      assertTrue(result.contains(fv3));
      result = RelationHelper.fulfillsOperation(lst, RelationOperator.NOT_EQUAL, new FloatFeatureValue("3", "3", 3.0000432f, 3));
      assertEquals(9, result.size());
      assertFalse(result.contains(fv3));
      assertTrue(result.contains(fvi1));
      assertTrue(result.contains(fv10));
      result = RelationHelper.fulfillsOperation(lst, RelationOperator.LESS_THAN, new IntegerFeatureValue("4", "4", "4"));
      assertEquals(3, result.size());
      assertTrue(result.contains(fv3));
      assertFalse(result.contains(fv4));
      assertFalse(result.contains(fv5));
      result = RelationHelper.fulfillsOperation(lst, RelationOperator.LESS_OR_EQUAL, new FloatFeatureValue("4", "4", new BigDecimal("4.00")));
      assertEquals(4, result.size());
      assertTrue(result.contains(fv3));
      assertTrue(result.contains(fv4));
      assertFalse(result.contains(fv5));
      result = RelationHelper.fulfillsOperation(lst, RelationOperator.GREATER_THAN, new IntegerFeatureValue("8", "8", 8));
      assertEquals(2, result.size());
      assertTrue(result.contains(fv10));
      assertTrue(result.contains(fvf9));
      assertFalse(result.contains(fv8));
      assertFalse(result.contains(fv7));
      result = RelationHelper.fulfillsOperation(lst, RelationOperator.GREATER_OR_EQUAL, new FeatureValue("8", "8", "8.0"));
      assertEquals(3, result.size());
      assertFalse(result.contains(fv7));
      assertTrue(result.contains(fv8));
      assertTrue(result.contains(fvf9));
      assertTrue(result.contains(fv10));
      LOGGER.info("... shuffling List of Feature-Values once again ...");
      FeatureValueHelper.shuffle(lst);
      assertEquals(10, lst.size());
      LOGGER.info("... applying complementary Operators on Lists and Value-Ranges ...");
      result = RelationHelper.fulfillsOperation(new FeatureValue("3", "3", "3"), RelationOperator.EQUAL, lst);
      assertEquals(1, result.size());
      assertTrue(result.contains(fv3));
      result = RelationHelper.fulfillsOperation(new FloatFeatureValue("3", "3", "3"), RelationOperator.NOT_EQUAL, lst);
      assertEquals(9, result.size());
      assertFalse(result.contains(fv3));
      assertTrue(result.contains(fvi1));
      assertTrue(result.contains(fv10));
      result = RelationHelper.fulfillsOperation(new IntegerFeatureValue("4", "4", 4), RelationOperator.GREATER_THAN, lst);
      assertEquals(3, result.size());
      assertTrue(result.contains(fv3));
      assertFalse(result.contains(fv4));
      assertFalse(result.contains(fv5));
      result = RelationHelper.fulfillsOperation(new FeatureValue("4", "4", "4.0"), RelationOperator.GREATER_OR_EQUAL, lst);
      assertEquals(4, result.size());
      assertTrue(result.contains(fv3));
      assertTrue(result.contains(fv4));
      assertFalse(result.contains(fv5));
      result = RelationHelper.fulfillsOperation(new FeatureValue("8", "8", "8.0"), RelationOperator.LESS_THAN, lst);
      assertEquals(2, result.size());
      assertTrue(result.contains(fv10));
      assertTrue(result.contains(fvf9));
      assertFalse(result.contains(fv8));
      assertFalse(result.contains(fv7));
      result = RelationHelper.fulfillsOperation(new FloatFeatureValue("8", "8", new BigDecimal("8.0000")), RelationOperator.LESS_OR_EQUAL, lst);
      assertEquals(3, result.size());
      assertFalse(result.contains(fv7));
      assertTrue(result.contains(fv8));
      assertTrue(result.contains(fvf9));
      assertTrue(result.contains(fv10));
      // done
      ok = true;
    }
    catch (final AssertionError ae) {
      ok = false;
      LOGGER.error("Functional Error: " + ae.getMessage(), ae);
      throw ae;
    }
    catch (final Throwable t) {
      ok = false;
      LOGGER.error("Technical Error: " + t.getMessage(), t);
      fail(t.getMessage());
    }
    finally {
      LOGGER.info("... test of RelationHelper finished " + (ok ? "without problems." : "with ERRORS!!!"));
    }
  }
}
