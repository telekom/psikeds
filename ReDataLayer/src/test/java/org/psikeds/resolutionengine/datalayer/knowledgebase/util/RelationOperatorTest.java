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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import org.psikeds.knowledgebase.jaxb.RelationType;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.impl.Xml2VoTransformer;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;

public class RelationOperatorTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(RelationOperatorTest.class);

  private static String LOG4J;

  @BeforeClass
  public static void setUpBeforeClass() {
    BasicConfigurator.configure();
    LOG4J = System.getProperty("org.psikeds.test.log4j.xml", "../ResolutionEngine/src/main/resources/log4j.xml");
    DOMConfigurator.configure(LOG4J);
  }

  /**
   * Test method for RelationOperator, RelationHelper and Xml2VoTransformer.
   */
  @Test
  public void testRelationOperator() throws Exception {
    boolean ok = false;
    LOGGER.info("Starting Test of RelationOperator ...");
    try {
      LOGGER.info("... creating Operators from XML-Types ...");
      final Transformer trans = new Xml2VoTransformer();
      assertNotNull(trans);
      final RelationOperator eq = trans.xml2ValueObject(RelationType.EQ);
      assertNotNull(eq);
      assertTrue(eq.equals(RelationOperator.EQUAL));
      assertFalse(eq.equals(RelationOperator.NOT_EQUAL));
      final RelationOperator neq = trans.xml2ValueObject(RelationType.NEQ);
      assertNotNull(neq);
      assertTrue(neq.equals(RelationOperator.NOT_EQUAL));
      assertFalse(neq.equals(RelationOperator.LESS_THAN));
      final RelationOperator less = trans.xml2ValueObject(RelationType.LESS);
      assertNotNull(less);
      assertTrue(less.equals(RelationOperator.LESS_THAN));
      assertFalse(less.equals(RelationOperator.LESS_OR_EQUAL));
      final RelationOperator leq = trans.xml2ValueObject(RelationType.LEQ);
      assertNotNull(leq);
      assertTrue(leq.equals(RelationOperator.LESS_OR_EQUAL));
      assertFalse(leq.equals(RelationOperator.EQUAL));
      LOGGER.info("... checking complementary Operators ...");
      RelationOperator comp = RelationHelper.getComplementaryOperator(eq);
      assertNotNull(comp);
      assertTrue(comp.equals(RelationOperator.EQUAL));
      comp = RelationHelper.getComplementaryOperator(neq);
      assertNotNull(comp);
      assertTrue(comp.equals(RelationOperator.NOT_EQUAL));
      comp = RelationHelper.getComplementaryOperator(less);
      assertNotNull(comp);
      assertTrue(comp.equals(RelationOperator.GREATER_THAN));
      comp = RelationHelper.getComplementaryOperator(leq);
      assertNotNull(comp);
      assertTrue(comp.equals(RelationOperator.GREATER_OR_EQUAL));
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
      LOGGER.info("... Test of RelationOperator finished " + (ok ? "without problems." : "with ERRORS!!!"));
    }
  }

}
