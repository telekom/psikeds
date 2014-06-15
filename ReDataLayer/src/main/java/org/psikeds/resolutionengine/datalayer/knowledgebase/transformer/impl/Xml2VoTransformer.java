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
package org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.impl;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.InitializingBean;

import org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer;
import org.psikeds.resolutionengine.datalayer.knowledgebase.util.FeatureValueHelper;
import org.psikeds.resolutionengine.datalayer.vo.Alternatives;
import org.psikeds.resolutionengine.datalayer.vo.Component;
import org.psikeds.resolutionengine.datalayer.vo.Constituents;
import org.psikeds.resolutionengine.datalayer.vo.Constitutes;
import org.psikeds.resolutionengine.datalayer.vo.Event;
import org.psikeds.resolutionengine.datalayer.vo.Events;
import org.psikeds.resolutionengine.datalayer.vo.Feature;
import org.psikeds.resolutionengine.datalayer.vo.FeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.FloatFeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.Fulfills;
import org.psikeds.resolutionengine.datalayer.vo.IntegerFeatureValue;
import org.psikeds.resolutionengine.datalayer.vo.MetaData;
import org.psikeds.resolutionengine.datalayer.vo.Purpose;
import org.psikeds.resolutionengine.datalayer.vo.Purposes;
import org.psikeds.resolutionengine.datalayer.vo.RelationOperator;
import org.psikeds.resolutionengine.datalayer.vo.RelationParameter;
import org.psikeds.resolutionengine.datalayer.vo.Rule;
import org.psikeds.resolutionengine.datalayer.vo.Rules;

/**
 * Helper for transforming a JAXB XML Object from the Knowledgebase into a
 * Value Object for the Datalayer.<br>
 * 
 * The Knowledgebase is read-only, therefore only transformations XML to VO
 * are supported / allowed.<br>
 * 
 * @author marco@juliano.de
 */
public class Xml2VoTransformer implements InitializingBean, Transformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Xml2VoTransformer.class);

  private int defaultFloatValueRoundingMode;
  private int defaultMaxValueRangeSize;

  public Xml2VoTransformer() {
    this(FloatFeatureValue.DEFAULT_ROUNDING_MODE, FeatureValueHelper.DEFAULT_MAXIMUM_SIZE);
  }

  public Xml2VoTransformer(final String roundingMode, final int maxRangeSize) {
    this(FloatFeatureValue.DEFAULT_ROUNDING_MODE, maxRangeSize);
    setDefaultFloatValueRoundingMode(roundingMode);
  }

  public Xml2VoTransformer(final int roundingMode, final int maxRangeSize) {
    super();
    setDefaultFloatValueRoundingMode(roundingMode);
    setDefaultMaxValueRangeSize(maxRangeSize);
  }

  public int getDefaultMaxValueRangeSize() {
    return this.defaultMaxValueRangeSize;
  }

  public void setDefaultMaxValueRangeSize(final int maxRangeSize) {
    this.defaultMaxValueRangeSize = (maxRangeSize < 0 ? FeatureValueHelper.DEFAULT_MAXIMUM_SIZE : maxRangeSize);
  }

  public int getDefaultFloatValueRoundingMode() {
    return this.defaultFloatValueRoundingMode;
  }

  public void setDefaultFloatValueRoundingMode(final String roundingMode) {
    this.defaultFloatValueRoundingMode = roundingModeStr2Int(roundingMode);
  }

  public void setDefaultFloatValueRoundingMode(final int roundingMode) {
    this.defaultFloatValueRoundingMode = (roundingMode < 0 ? FloatFeatureValue.DEFAULT_ROUNDING_MODE : roundingMode);
  }

  // ----------------------------------------------------------------

  /**
   * Check that ResolutionBusinessService was configured/wired correctly.
   * 
   * @throws Exception
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    // nothing todo, just print settings
    if (LOGGER.isInfoEnabled()) {
      final StringBuilder sb = new StringBuilder("Config: Float-Value Rounding-Mode: {}\n");
      sb.append("Maximum size of Value-Ranges: {}");
      LOGGER.info(sb.toString(), this.defaultFloatValueRoundingMode, this.defaultMaxValueRangeSize);
    }
  }

  // ----------------------------------------------------------------

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Alternatives)
   */
  @Override
  public Alternatives xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Alternatives xml) {
    Alternatives vo = null;
    if (xml != null) {
      vo = new Alternatives();
      final List<org.psikeds.knowledgebase.jaxb.Fulfills> lst = xml.getFulfills();
      for (final org.psikeds.knowledgebase.jaxb.Fulfills f : lst) {
        vo.addFulfills(xml2ValueObject(f));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Constituents)
   */
  @Override
  public Constituents xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Constituents xml) {
    Constituents vo = null;
    if (xml != null) {
      vo = new Constituents();
      final List<org.psikeds.knowledgebase.jaxb.Constitutes> lst = xml.getConstitutes();
      for (final org.psikeds.knowledgebase.jaxb.Constitutes c : lst) {
        vo.addConstitutes(xml2ValueObject(c));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  private Constitutes xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Constitutes xml) {
    Constitutes vo = null;
    if (xml != null) {
      vo = new Constitutes();
      vo.setVariantID(xml.getPvRef());
      for (final org.psikeds.knowledgebase.jaxb.Component comp : xml.getComponent()) {
        if (comp != null) {
          vo.addComponent(xml2ValueObject(comp));
        }
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  private Component xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Component xml) {
    Component vo = null;
    if (xml != null) {
      final String purposeID = xml.getPsRef();
      final long quantity = (xml.getQuantity() == null ? Component.DEFAULT_QUANTITY : xml.getQuantity().longValue());
      vo = new Component(purposeID, quantity);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Derivations)
   */
  @Override
  public Constituents xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Derivations xml) {
    Constituents vo = null;
    if (xml != null) {
      vo = new Constituents();
      final List<org.psikeds.knowledgebase.jaxb.Setup> lst = xml.getSetup();
      for (final org.psikeds.knowledgebase.jaxb.Setup s : lst) {
        vo.addConstitutes(xml2ValueObject(s));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  private Constitutes xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Setup xml) {
    Constitutes vo = null;
    if (xml != null) {
      vo = new Constitutes();
      vo.setVariantID(xml.getPvImplRef());
      final long quantity = Component.DEFAULT_QUANTITY;
      for (final String purposeID : xml.getPsRefs()) {
        if (!StringUtils.isEmpty(purposeID)) {
          vo.addComponent(new Component(purposeID, quantity));
        }
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Events)
   */
  @Override
  public Events xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Events xml) {
    Events vo = null;
    if (xml != null) {
      vo = new Events();
      final List<org.psikeds.knowledgebase.jaxb.Event> lst = xml.getEvent();
      for (final org.psikeds.knowledgebase.jaxb.Event e : lst) {
        vo.addEvent(xml2ValueObject(e));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Event)
   */
  @Override
  public Event xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Event xml) {
    Event vo = null;
    if (xml != null) {
      final String eventID = xml.getId();
      final String label = xml.getLabel();
      final String description = xml.getDescription();
      final String variantID = xml.getNexusRef();
      final List<String> context = xml.getContextPath();
      String triggerID = null;
      String triggerType = null;
      boolean notEvent = Event.DEFAULT_NOT_EVENT;
      final org.psikeds.knowledgebase.jaxb.Trigger trigger = xml.getTrigger();
      if (trigger != null) {
        triggerID = trigger.getRef();
        triggerType = xml2ValueObject(trigger.getType());
        if (trigger.isNotEvent() != null) {
          notEvent = trigger.isNotEvent().booleanValue();
        }
      }
      vo = new Event(label, description, eventID, variantID, context, triggerID, triggerType, notEvent);
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  private String xml2ValueObject(final org.psikeds.knowledgebase.jaxb.TriggerType xml) {
    String type = Event.TRIGGER_TYPE_VARIANT;
    if (xml != null) {
      if (org.psikeds.knowledgebase.jaxb.TriggerType.CONCEPT.value().equals(xml.value())) {
        type = Event.TRIGGER_TYPE_CONCEPT;
      }
      else if (org.psikeds.knowledgebase.jaxb.TriggerType.ATTRIBUTE.value().equals(xml.value())) {
        type = Event.TRIGGER_TYPE_FEATURE_VALUE;
      }
    }
    return type;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Sensor)
   */
  @Override
  public Feature xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Sensor xml) {
    Feature vo = null;
    if (xml != null) {
      final String featureID = xml.getId();
      final String label = xml.getLabel();
      final String description = xml.getDescription();
      final String unit = xml.getUnit();
      vo = new Feature(label, description, featureID, unit);
      final org.psikeds.knowledgebase.jaxb.Values values = xml.getValues();
      if (values != null) {
        if ((values.getStrValue() != null) && !values.getStrValue().isEmpty()) {
          for (final org.psikeds.knowledgebase.jaxb.SensedStringValue strval : values.getStrValue()) {
            if (strval != null) {
              vo.setType(Feature.VALUE_TYPE_STRING);
              vo.addValue(new FeatureValue(featureID, strval.getId(), strval.getValue()));
            }
          }
        }
        if ((values.getIntValueOrIntRange() != null) && !values.getIntValueOrIntRange().isEmpty()) {
          for (final Serializable serial : values.getIntValueOrIntRange()) {
            if (serial instanceof org.psikeds.knowledgebase.jaxb.SensedIntValue) {
              final org.psikeds.knowledgebase.jaxb.SensedIntValue intval = (org.psikeds.knowledgebase.jaxb.SensedIntValue) serial;
              final long val = (intval.getValue() == null ? 0 : intval.getValue().longValue());
              vo.setType(Feature.VALUE_TYPE_INTEGER);
              vo.addValue(new IntegerFeatureValue(featureID, intval.getId(), val));
            }
            else if (serial instanceof org.psikeds.knowledgebase.jaxb.IntRange) {
              final org.psikeds.knowledgebase.jaxb.IntRange intrange = (org.psikeds.knowledgebase.jaxb.IntRange) serial;
              final String rangeID = intrange.getId();
              final long min = (intrange.getMin() == null ? 0 : intrange.getMin().longValue());
              final long max = (intrange.getMax() == null ? 0 : intrange.getMax().longValue());
              final long inc = (intrange.getInc() == null ? FeatureValueHelper.DEFAULT_RANGE_STEP : intrange.getInc().longValue());
              final List<IntegerFeatureValue> intvallst = FeatureValueHelper.calculateIntegerRange(featureID, rangeID, min, max, inc, this.defaultMaxValueRangeSize);
              vo.setType(Feature.VALUE_TYPE_INTEGER);
              vo.addValue(intvallst);
            }
            else {
              LOGGER.warn("Skipping unexpected XML-Element in Sensor {} / Values / IntValueOrIntRange: {}", featureID, serial);
            }
          }
        }
        if ((values.getFloatValueOrFloatRange() != null) && !values.getFloatValueOrFloatRange().isEmpty()) {
          for (final Serializable serial : values.getFloatValueOrFloatRange()) {
            if (serial instanceof org.psikeds.knowledgebase.jaxb.SensedFloatValue) {
              final org.psikeds.knowledgebase.jaxb.SensedFloatValue floatval = (org.psikeds.knowledgebase.jaxb.SensedFloatValue) serial;
              final FloatFeatureValue ffv = new FloatFeatureValue(featureID, floatval.getId(), floatval.getValue(), xml2ValueObject(floatval.getRoundingMode()));
              if (floatval.getScale() != null) {
                ffv.setScale(floatval.getScale().intValue());
              }
              vo.setType(Feature.VALUE_TYPE_FLOAT);
              vo.addValue(ffv);
            }
            else if (serial instanceof org.psikeds.knowledgebase.jaxb.FloatRange) {
              final org.psikeds.knowledgebase.jaxb.FloatRange floatrange = (org.psikeds.knowledgebase.jaxb.FloatRange) serial;
              final String rangeID = floatrange.getId();
              int scale = FloatFeatureValue.MIN_FLOAT_SCALE;
              if (floatrange.getScale() != null) {
                scale = floatrange.getScale().intValue();
              }
              final List<FloatFeatureValue> floatvallst = FeatureValueHelper.calculateFloatRange(featureID, rangeID, floatrange.getMin(), floatrange.getMax(), floatrange.getInc(),
                  scale, xml2ValueObject(floatrange.getRoundingMode()), this.defaultMaxValueRangeSize);
              vo.setType(Feature.VALUE_TYPE_FLOAT);
              vo.addValue(floatvallst);
            }
            else {
              LOGGER.warn("Skipping unexpected XML-Element in Sensor {} / Values / FloatValueOrFloatRange: {}", featureID, serial);
            }
          }
        }
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return int
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.FloatValueRoundingMode)
   */
  @Override
  public int xml2ValueObject(final org.psikeds.knowledgebase.jaxb.FloatValueRoundingMode xml) {
    return roundingModeStr2Int((xml == null) ? null : xml.value());
  }

  private int roundingModeStr2Int(final String str) {
    int mode = this.defaultFloatValueRoundingMode;
    if (!StringUtils.isEmpty(str)) {
      if (org.psikeds.knowledgebase.jaxb.FloatValueRoundingMode.MATHEMATICAL.value().equalsIgnoreCase(str)) {
        mode = FloatFeatureValue.ROUNDING_MATHEMATICAL;
      }
      else if (org.psikeds.knowledgebase.jaxb.FloatValueRoundingMode.TRUNCATE.value().equalsIgnoreCase(str)) {
        mode = FloatFeatureValue.ROUNDING_TRUNCATE;
      }
      else if (org.psikeds.knowledgebase.jaxb.FloatValueRoundingMode.EXACT.value().equalsIgnoreCase(str)) {
        mode = FloatFeatureValue.ROUNDING_EXACT;
      }
      LOGGER.trace("roundingModeStr2Int: str = {}  -->  mode = {}", str, mode);
    }
    return mode;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Fulfills)
   */
  @Override
  public Fulfills xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Fulfills xml) {
    return (xml == null ? null : new Fulfills(xml.getPsRef(), xml.getPvRefs()));
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Meta)
   */
  @Override
  public MetaData xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Meta xml) {
    return (xml == null ? null : new MetaData(
        xml.getId(), xml.getName(), xml.getTeaser(),
        xml.getRelease(), xml.getCopyright(), xml.getLicense(), xml.getLanguage(),
        xml.getCreated(), xml.getLastmodified(),
        xml.getCreator(), xml.getDescription()));
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Purpose)
   */
  @Override
  public Purpose xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Purpose xml) {
    Purpose vo = null;
    if (xml != null) {
      final boolean root = (xml.isRoot() == null ? Purpose.DEFAULT_IS_ROOT : xml.isRoot().booleanValue());
      vo = new Purpose(xml.getLabel(), xml.getDescription(), xml.getId(), root);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Purposes)
   */
  @Override
  public Purposes xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Purposes xml) {
    Purposes vo = null;
    if (xml != null) {
      vo = new Purposes();
      final List<org.psikeds.knowledgebase.jaxb.Purpose> lst = xml.getPurpose();
      for (final org.psikeds.knowledgebase.jaxb.Purpose p : lst) {
        vo.addPurpose(xml2ValueObject(p));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Parameter)
   */
  @Override
  public RelationParameter xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Parameter xml) {
    RelationParameter vo = null;
    if (xml != null) {
      final String label = xml.getLabel();
      final String description = xml.getDescription();
      final String parameterID = xml.getId();
      final String variantID = xml.getNexusRef();
      final List<String> context = xml.getContextPath();
      final org.psikeds.knowledgebase.jaxb.ValueSet vs = xml.getValueSet();
      final String featureID = (vs == null ? null : vs.getSensorRef());
      vo = new RelationParameter(label, description, parameterID, variantID, context, featureID);
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.RelationType)
   */
  @Override
  public RelationOperator xml2ValueObject(final org.psikeds.knowledgebase.jaxb.RelationType xml) {
    return RelationOperator.fromValue(xml == null ? null : xml.value());
  }

  /**
   * @param xml
   * @return String
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.RelParamType)
   */
  @Override
  public String xml2ValueObject(final org.psikeds.knowledgebase.jaxb.RelParamType xml) {
    String type;
    if ((xml != null) && org.psikeds.knowledgebase.jaxb.RelParamType.V_VAL.value().equals(xml.value())) {
      type = RelationParameter.PARAMETER_TYPE_FEATURE_VALUE;
    }
    else {
      type = RelationParameter.PARAMETER_TYPE_CONSTANT_VALUE;
    }
    return type;
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Rule)
   */
  @Override
  public Rule xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Rule xml) {
    return xml == null ? null : new Rule(xml.getLabel(), xml.getDescription(), xml.getId(), xml.getNexusRef(), xml.getPremiseRefs(), xml.getConclusioRef());
  }

  /**
   * @param xml
   * @return vo
   * @see org.psikeds.resolutionengine.datalayer.knowledgebase.transformer.Transformer#xml2ValueObject(org.psikeds.knowledgebase.jaxb.Rules)
   */
  @Override
  public Rules xml2ValueObject(final org.psikeds.knowledgebase.jaxb.Rules xml) {
    Rules vo = null;
    if (xml != null) {
      vo = new Rules();
      final List<org.psikeds.knowledgebase.jaxb.Rule> lst = xml.getRule();
      for (final org.psikeds.knowledgebase.jaxb.Rule r : lst) {
        vo.addRule(xml2ValueObject(r));
      }
      LOGGER.trace("xml2ValueObject: xml = {}\n--> vo = {}", xml, vo);
    }
    return vo;
  }
}
