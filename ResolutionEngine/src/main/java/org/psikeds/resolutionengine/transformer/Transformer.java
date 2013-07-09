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
package org.psikeds.resolutionengine.transformer;

import java.util.List;

/**
 * Helper for transforming Value Objects from the Datalayer into POJOs of the
 * Interface (and vice versa).
 * 
 * @author marco@juliano.de
 */
public final class Transformer {

  public static org.psikeds.resolutionengine.datalayer.vo.Chocolate pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Chocolate pojoChoco) {
    org.psikeds.resolutionengine.datalayer.vo.Chocolate voChoco = null;
    if (pojoChoco != null) {
      voChoco = new org.psikeds.resolutionengine.datalayer.vo.Chocolate(pojoChoco.getRefid(), pojoChoco.getDescription());
    }
    return voChoco;
  }

  public static org.psikeds.resolutionengine.interfaces.pojos.Chocolate valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Chocolate voChoco) {
    org.psikeds.resolutionengine.interfaces.pojos.Chocolate pojoChoco = null;
    if (voChoco != null) {
      pojoChoco = new org.psikeds.resolutionengine.interfaces.pojos.Chocolate(voChoco.getRefid(), voChoco.getDescription());
    }
    return pojoChoco;
  }

  public static org.psikeds.resolutionengine.datalayer.vo.Chocolatelist pojo2ValueObject(final org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist pojoLst) {
    return pojoLst == null ? null : pojo2ValueObject(pojoLst.getChocolates());
  }

  public static org.psikeds.resolutionengine.datalayer.vo.Chocolatelist pojo2ValueObject(final List<org.psikeds.resolutionengine.interfaces.pojos.Chocolate> pojoLst) {
    org.psikeds.resolutionengine.datalayer.vo.Chocolatelist voLst = null;
    if (pojoLst != null) {
      voLst = new org.psikeds.resolutionengine.datalayer.vo.Chocolatelist();
      for (final org.psikeds.resolutionengine.interfaces.pojos.Chocolate pc : pojoLst) {
        voLst.add(Transformer.pojo2ValueObject(pc));
      }
    }
    return voLst;
  }

  public static org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist valueObject2Pojo(final org.psikeds.resolutionengine.datalayer.vo.Chocolatelist volst) {
    return volst == null ? null : valueObject2Pojo(volst.getChocolates());
  }

  public static org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist valueObject2Pojo(final List<org.psikeds.resolutionengine.datalayer.vo.Chocolate> volst) {
    org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist pojolst = null;
    if (volst != null) {
      pojolst = new org.psikeds.resolutionengine.interfaces.pojos.Chocolatelist();
      for (final org.psikeds.resolutionengine.datalayer.vo.Chocolate vc : volst) {
        pojolst.add(Transformer.valueObject2Pojo(vc));
      }
    }
    return pojolst;
  }

  private Transformer() {
    // prevent instantiation
  }
}
