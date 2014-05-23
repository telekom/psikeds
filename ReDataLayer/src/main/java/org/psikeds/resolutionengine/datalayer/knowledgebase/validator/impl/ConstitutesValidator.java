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
package org.psikeds.resolutionengine.datalayer.knowledgebase.validator.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.ValidationException;
import org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator;
import org.psikeds.resolutionengine.datalayer.vo.Component;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Variant;

/**
 * Validator checking that all Constituents / Constitutes are valid.
 * 
 * @author marco@juliano.de
 * 
 */
public class ConstitutesValidator implements Validator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConstitutesValidator.class);

  /**
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.validator.Validator#validate(org.psikeds.resolutionengine.datalayer.knowledgebase.KnowledgeBase)
   */
  @Override
  public void validate(final KnowledgeBase kb) throws ValidationException {
    try {
      LOGGER.debug("Validating KnowledgeBase regarding Constituents / Constitutes ...");
      boolean valid = true;
      // Note: Constituents are optional, i.e. this Node can be null!
      final Constituents cons = (kb == null ? null : kb.getConstituents());
      final List<Constitutes> lst = (cons == null ? null : cons.getConstitutes());
      if ((lst != null) && !lst.isEmpty()) {
        for (final Constitutes c : lst) {
          final String vid = (c == null ? null : c.getVariantID());
          if (StringUtils.isEmpty(vid)) {
            valid = false;
            LOGGER.warn("No VariantID: {}", c);
            continue;
          }
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Checking Constitutes: {}", c);
          }
          final Variant v = kb.getVariant(vid);
          if ((v == null) || !vid.equals(v.getVariantID())) {
            valid = false;
            LOGGER.warn("Unknown VariantID: {}", vid);
          }
          else if (StringUtils.isEmpty(v.getLabel())) {
            valid = false;
            LOGGER.warn("Variant {} has no Label!", vid);
          }
          final List<Component> components = c.getComponents();
          if ((components == null) || components.isEmpty()) {
            valid = false;
            LOGGER.warn("No constituting Components/Purposes for VariantID: {}", vid);
          }
          else {
            for (final Component comp : components) {
              final String pid = (comp == null ? null : comp.getPurposeID());
              if (StringUtils.isEmpty(pid)) {
                valid = false;
                LOGGER.warn("Empty PurposeID within Component for VariantID: {}", vid);
              }
              else {
                final Purpose p = kb.getPurpose(pid);
                if ((p == null) || !pid.equals(p.getPurposeID())) {
                  valid = false;
                  LOGGER.warn("Unknown PurposeID: {}", pid);
                }
                else {
                  if (StringUtils.isEmpty(p.getLabel())) {
                    valid = false;
                    LOGGER.warn("Purpose {} has no Label!", pid);
                  }
                  if (p.isRoot()) {
                    valid = false;
                    LOGGER.warn("Purpose {} is marked as Root-Purpose but constituting Variant {}", pid, vid);
                  }
                }
                final long qty = comp.getQuantity();
                if (qty < Component.MINIMUM_QUANTITY) {
                  valid = false;
                  LOGGER.warn("Illegal Quantity: " + qty);
                }
              }
            }
          }
        }
      }
      if (!valid) {
        throw new ValidationException("KnowledgeBase is not valid! See logfiles for details.");
      }
      else {
        LOGGER.debug("... finished validating KnowledgeBase regarding Constituents / Constitutes ... OK.");
      }
    }
    catch (final ValidationException vaex) {
      LOGGER.warn("... finished validating KnowledgeBase regarding Constituents / Constitutes ... NOT VALID!", vaex);
      throw vaex;
    }
    catch (final Exception ex) {
      LOGGER.error("... failed to validate KnowledgeBase regarding Constituents / Constitutes: " + ex.getMessage(), ex);
      throw new ValidationException("Failed to validate KnowledgeBase regarding Constituents / Constitutes.", ex);
    }
  }
}
