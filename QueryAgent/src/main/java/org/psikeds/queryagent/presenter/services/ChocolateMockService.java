/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
 *
 * This file is free software: you can redistribute
 * it and/or modify it under the terms of the
 * [ ] GNU Affero General Public License
 * [ ] GNU General Public License
 * [x] GNU Lesser General Public License
 * [ ] Creatice Commons ShareAlike License
 *
 * For details see file LICENSING in the top project directory
 *******************************************************************************/
package org.psikeds.queryagent.presenter.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate;
import org.psikeds.queryagent.interfaces.presenter.pojos.Chocolatelist;
import org.psikeds.queryagent.interfaces.presenter.pojos.Ingredient;
import org.psikeds.queryagent.interfaces.presenter.services.ChocolateService;

/**
 * Mock-Business-Service for Testing only!
 * 
 * @author marco@juliano.de
 */
@SuppressWarnings("PMD")
// System.out.print is used
public class ChocolateMockService implements ChocolateService {

  private final Chocolatelist clst;

  public ChocolateMockService() {
    final Ingredient i11 = new Ingredient("i11", "Kakao");
    final Ingredient i12 = new Ingredient("i12", "Milch");
    final List<Ingredient> il1 = new ArrayList<Ingredient>();
    il1.add(i11);
    il1.add(i12);
    final Chocolate c1 = new Chocolate("c1", "Vollmilch", il1);

    final Ingredient i21 = new Ingredient("i21", "Viele Kakao");
    final Ingredient i22 = new Ingredient("i22", "Wenig Zucker");
    final Ingredient i23 = new Ingredient("i23", "Bitterkeit");
    final List<Ingredient> il2 = new ArrayList<Ingredient>();
    il2.add(i21);
    il2.add(i22);
    il2.add(i23);
    final Chocolate c2 = new Chocolate("c2", "Zartbitter", il2);

    final Ingredient i31 = new Ingredient("i31", "Schokolade");
    final Ingredient i32 = new Ingredient("i32", "Marzipan");
    final List<Ingredient> il3 = new ArrayList<Ingredient>();
    il3.add(i31);
    il3.add(i32);
    final Chocolate c3 = new Chocolate("c3", "Marzipan", il3);

    final Ingredient i41 = new Ingredient("i41", "Kakao");
    final Ingredient i42 = new Ingredient("i42", "Schokolade");
    final Ingredient i43 = new Ingredient("i43", "Haselnuss");
    final Ingredient i44 = new Ingredient("i44", "Milch");
    final List<Ingredient> il4 = new ArrayList<Ingredient>();
    il4.add(i41);
    il4.add(i42);
    il4.add(i43);
    il4.add(i44);
    final Chocolate c4 = new Chocolate("c4", "Volle Nuss", il4);

    final Ingredient i51 = new Ingredient("i51", "Kakaobutter");
    final Ingredient i52 = new Ingredient("i52", "Milch");
    final Ingredient i53 = new Ingredient("i53", "Zucker");
    final List<Ingredient> il5 = new ArrayList<Ingredient>();
    il5.add(i51);
    il5.add(i52);
    il5.add(i53);
    final Chocolate c5 = new Chocolate("c5", "Weiss", il5);

    this.clst = new Chocolatelist();
    this.clst.add(c1);
    this.clst.add(c2);
    this.clst.add(c3);
    this.clst.add(c4);
    this.clst.add(c5);
  }

  /**
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ChocolateService#getChocolates()
   */
  @Override
  public Chocolatelist getChocolates() {
    return this.clst;
  }

  /**
   * @param refid
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ChocolateService#selectChocolate(java.lang.String)
   */
  @Override
  public Chocolate selectChocolate(final String refid) {
    if (StringUtils.isEmpty(refid)) {
      throw new IllegalArgumentException("RefId is empty!");
    }
    for (final Chocolate choco : this.clst.getChocolates()) {
      if (choco.getRefid().equalsIgnoreCase(refid)) {
        System.out.println("Selected Chocolate: " + choco);
        return choco;
      }
      for (final Ingredient ingr : choco.getIngredients()) {
        if (ingr.getRefid().equalsIgnoreCase(refid)) {
          System.out.println("Selected Ingredient: " + ingr);
          return choco;
        }
      }
    }
    throw new IllegalArgumentException("Unknown RefId: " + refid);
  }

  /**
   * @param c
   * @return
   * @see org.psikeds.queryagent.interfaces.presenter.services.ChocolateService#addChocolate(org.psikeds.queryagent.interfaces.presenter.pojos.Chocolate)
   */
  @Override
  public Chocolatelist addChocolate(final Chocolate choco) {
    if (choco == null) {
      throw new IllegalArgumentException("Chocolate is null!");
    }
    if (this.clst == null || this.clst.size() > 10) {
      throw new IllegalStateException("Oooooopps! :-)");
    }

    final String refid = choco.getRefid();
    final List<Ingredient> ingredients = new ArrayList<Ingredient>();
    ingredients.add(new Ingredient(refid + "1", "Luft"));
    ingredients.add(new Ingredient(refid + "2", "Liebe"));
    ingredients.add(new Ingredient(refid + "3", "Test"));
    choco.setIngredients(ingredients);

    this.clst.add(choco);
    System.out.println("Added " + choco + " to List. New Size = " + this.clst.size());
    return this.clst;
  }
}
