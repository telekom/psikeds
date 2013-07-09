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
package org.psikeds.resolutionengine.datalayer.knowledgebase.transformer;

/**
 * Helper for transforming a JAXB XML Object from the Knowledgebase into a Value
 * Object for the Datalayer. The knowledgebase is (currently) read-only,
 * therefore only transformation xml->vo is supported/implemented.
 * 
 * @author marco@juliano.de
 */
public final class Transformer {

  public static org.psikeds.resolutionengine.datalayer.vo.Chocolate xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Chocolate xml) {
    org.psikeds.resolutionengine.datalayer.vo.Chocolate vo = null;
    if (xml != null) {
      vo = new org.psikeds.resolutionengine.datalayer.vo.Chocolate(xml.getChocokey(), xml.getDescription());
    }
    return vo;
  }

  public static org.psikeds.resolutionengine.datalayer.vo.Chocolatelist xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Chocolatelist xmllst) {
    org.psikeds.resolutionengine.datalayer.vo.Chocolatelist volst = null;
    if (xmllst != null) {
      volst = new org.psikeds.resolutionengine.datalayer.vo.Chocolatelist();
      if (xmllst.getChocolate() != null) {
        for (final org.psikeds.knowledgebase.jaxb.Chocolate xc : xmllst.getChocolate()) {
          volst.add(Transformer.xml2ValueObject(xc));
        }
      }
    }
    return volst;
  }

  private Transformer() {
    // prevent instantiation
  }
}
