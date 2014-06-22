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
package org.psikeds.resolutionengine.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntities;
import org.psikeds.resolutionengine.interfaces.pojos.KnowledgeEntity;
import org.psikeds.resolutionengine.interfaces.pojos.Purpose;
import org.psikeds.resolutionengine.interfaces.pojos.Variant;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoice;
import org.psikeds.resolutionengine.interfaces.pojos.VariantChoices;
import org.psikeds.resolutionengine.resolver.ResolutionException;

/**
 * Helper for handling Context-Paths
 * 
 * @author marco@juliano.de
 * 
 */
public abstract class ContextHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContextHelper.class);

  private ContextHelper() {
    // prevent instantiation
  }

  // ----------------------------------------------------------------

  public static KnowledgeEntity walkContextPath(final KnowledgeEntity root, final List<String> ctx) {
    final KnowledgeEntity result = walkContextPath(root, ctx, true);
    LOGGER.debug("walkContextPath: {} --> {}  -->  {}", shortDisplayKE(root), ctx, shortDisplayKE(result));
    return result;
  }

  private static KnowledgeEntity walkContextPath(final KnowledgeEntity ke, final List<String> ctx, final boolean isVariant) {
    KnowledgeEntity result = null;
    boolean stillPossible = false;
    boolean matching = false;
    try {
      LOGGER.trace("--> walkContextPath(); isVariant = {}; Path = {}\nKE = {}", isVariant, ctx, ke);
      final int len = (ctx == null ? 0 : ctx.size());
      if (len <= 0) {
        // we walked the complete path
        result = ke;
        matching = true;
        return result;
      }
      // check current path element
      final String currentPathElement = ctx.get(0);
      matching = checkPathElement(ke, currentPathElement, isVariant);
      if (!matching) {
        // Current Path Element was not matching, but
        // let's see whether there is a matching Choice
        stillPossible = checkChoices(ke.getPossibleVariants(), currentPathElement, isVariant);
        if (stillPossible) {
          LOGGER.debug("Context-Path {} is matching to Choice of this KE: {}", ctx, shortDisplayKE(ke));
          result = null;
          return result;
        }
        else {
          LOGGER.debug("Context-Path {} is matching neither KE nor any Choice: {}", ctx, shortDisplayKE(ke));
          throw new ResolutionException("Context-Path is not possible!");
        }
      }
      // Current Element was matching
      if (len == 1) {
        // ... and this was the last Element.
        LOGGER.debug("Context-Path {} ends with current PE at this KE: {}", ctx, shortDisplayKE(ke));
        result = ke;
        return result;
      }
      // Length > 1, check next Path Element
      final String nextPathElement = ctx.get(1);
      matching = checkPathElement(ke, nextPathElement, !isVariant);
      if (matching) {
        if (len == 2) {
          LOGGER.debug("Context-Path {} ends with next PE at this KE: {}", ctx, shortDisplayKE(ke));
          result = ke;
          return result;
        }
        else { // len > 2
          LOGGER.debug("Next PE of Context-Path {} matches to this KE: {}", ctx, shortDisplayKE(ke));
          result = walkContextPath(ke, ke.getChildren(), ctx.subList(2, len), isVariant);
          return result;
        }
      }
      // next Path Element not matching to KE. Let's see whether it is matching to a Choice
      stillPossible = checkChoices(ke, ctx.subList(1, len), !isVariant);
      if (stillPossible) {
        LOGGER.debug("Next PE of Context-Path {} matches to a Choice of this KE: {}", ctx, shortDisplayKE(ke));
        matching = false;
        result = null;
        return result;
      }
      // Next PE is matching to neither this KE nor one of the Choices, but maybe to one of the Children?
      if (len >= 2) {
        // Let's do some Recursion ;-)
        matching = false;
        result = walkContextPath(ke, ke.getChildren(), ctx.subList(1, len), !isVariant);
        return result;
      }
      // len == 1 and not matching
      LOGGER.debug("Context-Path {} is matching neither KE nor any Choice: {}", ctx, shortDisplayKE(ke));
      throw new ResolutionException("Context-Path is not possible!");
    }
    finally {
      LOGGER.trace("<-- walkContextPath(); isVariant = {}; matching = {}; stillPossible = {}; Path = {}\nResult = {}", isVariant, matching, stillPossible, ctx, result);
    }
  }

  private static KnowledgeEntity walkContextPath(final KnowledgeEntity parent, final KnowledgeEntities entities, final List<String> ctx, final boolean isVariant) {
    boolean possible = false;
    KnowledgeEntity result = null;
    try {
      LOGGER.trace("--> walkContextPath(); isVariant = {}; Path = {}; Parent = {}", isVariant, ctx, shortDisplayKE(parent));
      final int len = (ctx == null ? 0 : ctx.size());
      if (len <= 0) {
        // we walked the complete path
        result = parent;
        return result;
      }
      for (final KnowledgeEntity ke : entities) {
        try {
          result = walkContextPath(ke, ctx, isVariant);
          if (result != null) {
            possible = false;
            return result;
          }
          // no exception, still possible
          possible = true;
        }
        catch (final Exception ex) {
          // ignore
        }
      }
      if (!possible) {
        LOGGER.debug("Context-Path {} was matching none of the Children of Parent: {}", ctx, shortDisplayKE(parent));
        throw new ResolutionException("Context-Path is not possible!");
      }
      return result;
    }
    finally {
      LOGGER.trace("<-- walkContextPath(); isVariant = {}; Path = {}; Parent = {}; possible = {}\nResult = {}", isVariant, ctx, shortDisplayKE(parent), possible, result);
    }
  }

  // ----------------------------------------------------------------

  private static boolean checkPathElement(final KnowledgeEntity ke, final String pathElement, final boolean isVariant) {
    boolean matching = false;
    try {
      LOGGER.trace("--> checkPathElement(); PE = {}; isVariant = {}", pathElement, isVariant);
      if (StringUtils.isEmpty(pathElement)) {
        throw new ResolutionException("Path Element is empty!");
      }
      if (isVariant) {
        // we are expecting a variant
        LOGGER.trace("Checking whether {} is matching to Variant.", pathElement);
        final Variant v = (ke == null ? null : ke.getVariant());
        final String vid = (v == null ? null : v.getVariantID());
        if (pathElement.equals(vid)) {
          LOGGER.debug("Path Element is matching to Variant-ID: {}", vid);
          matching = true;
        }
      }
      else {
        // we are expecting a purpose
        LOGGER.trace("Checking whether {} is matching to Purpose.", pathElement);
        final Purpose p = (ke == null ? null : ke.getPurpose());
        final String pid = (p == null ? null : p.getPurposeID());
        if (pathElement.equals(pid)) {
          LOGGER.debug("Path Element is matching to Purpose-ID: {}", pid);
          matching = true;
        }
      }
      return matching;
    }
    finally {
      LOGGER.trace("<-- checkPathElement(); PE = {}; isVariant = {}; matching = {}", pathElement, isVariant, matching);
    }
  }

  // ----------------------------------------------------------------

  private static boolean checkChoices(final KnowledgeEntity ke, final List<String> ctx, final boolean isVariant) {
    if ((ke != null) && (ctx != null) && !ctx.isEmpty()) {
      final String currentPathElement = ctx.get(0);
      final VariantChoices choices = ke.getPossibleVariants();
      return checkChoices(choices, currentPathElement, isVariant);
    }
    return false;
  }

  private static boolean checkChoices(final VariantChoices choices, final String currentPathElement, final boolean isVariant) {
    boolean matchingChoice = false;
    try {
      LOGGER.trace("--> checkChoices(); PE = {}; isVariant = {}", currentPathElement, isVariant);
      for (final VariantChoice vc : choices) {
        // Loop over all Choices and as soon as one is matching we are finished
        matchingChoice = checkChoice(vc, currentPathElement, isVariant);
        if (matchingChoice) {
          // fail/succeed fast
          return matchingChoice;
        }
      }
      return matchingChoice;
    }
    finally {
      LOGGER.trace("<-- checkChoices(); PE = {}; matchingChoice = {}", currentPathElement, matchingChoice);
    }
  }

  private static boolean checkChoice(final VariantChoice vc, final String currentPathElement, final boolean isVariant) {
    boolean matchingChoice = false;
    try {
      LOGGER.trace("--> checkChoice(); PE = {}; isVariant = {}\nVariantChoice = {}", currentPathElement, isVariant, vc);
      if (isVariant) {
        // Is Path Element matching any selectable Variant of this Choice?
        final List<Variant> lst = vc.getVariants();
        if (lst != null) {
          for (final Variant v : lst) {
            final String vid = (v == null ? null : v.getVariantID());
            matchingChoice = currentPathElement.equals(vid);
            if (matchingChoice) {
              LOGGER.debug("PE {} is matching Variant of VariantChoice {}", currentPathElement, vc);
              return matchingChoice;
            }
          }
        }
      }
      else {
        // Is Path Element matching Purpose of this Choice?
        final Purpose p = vc.getPurpose();
        final String pid = (p == null ? null : p.getPurposeID());
        matchingChoice = currentPathElement.equals(pid);
        if (matchingChoice) {
          LOGGER.debug("PE {} is matching Purpose of VariantChoice {}", currentPathElement, vc);
          return matchingChoice;
        }
      }
      return matchingChoice;
    }
    finally {
      LOGGER.trace("<-- checkChoice(); PE = {}; isVariant = {}; matchingChoice = {}", currentPathElement, isVariant, matchingChoice);
    }
  }

  // ----------------------------------------------------------------

  public static String shortDisplayKE(final KnowledgeEntity ke) {
    return KnowledgeEntityHelper.shortDisplayKE(ke);
  }
}