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
package org.psikeds.resolutionengine.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.StringUtils;

import org.psikeds.common.cache.LimitedHashMap;
import org.psikeds.resolutionengine.datalayer.vo.Relation;

/**
 * A stack of Relations; Depending on the current State of Resolution and
 * the corresponding Knowledge, all Relations are either on the Active- or
 * the Obsolete-Stack.
 * 
 * @author marco@juliano.de
 * 
 */
public class RelationStack extends LimitedHashMap<String, Relation> implements Serializable {

  private static final long serialVersionUID = 1L;

  public RelationStack() {
    super();
  }

  public RelationStack(final int maxSize) {
    super(maxSize);
  }

  // copy constructor
  public RelationStack(final RelationStack rels) {
    super(rels.getMaxSize());
    addRelations(rels.getRelations());
  }

  // ----------------------------------------------------------------

  public List<Relation> getRelations() {
    return new ArrayList<Relation>(this.values());
  }

  public void setRelations(final Collection<? extends Relation> rels) {
    clear();
    addRelations(rels);
  }

  public void addRelations(final Collection<? extends Relation> rels) {
    if (rels != null) {
      for (final Relation r : rels) {
        addRelation(r);
      }
    }
  }

  public Relation addRelation(final Relation r) {
    final String rid = (r == null ? null : r.getRelationID());
    return (StringUtils.isEmpty(rid) ? null : this.put(rid, r));
  }

  public Relation removeRelation(final Relation r) {
    final String rid = (r == null ? null : r.getRelationID());
    return removeRelation(rid);
  }

  public Relation removeRelation(final String rid) {
    return (StringUtils.isEmpty(rid) ? null : this.remove(rid));
  }

  // ----------------------------------------------------------------

  public boolean containsRelation(final Relation r) {
    return containsRelation(r == null ? null : r.getRelationID());
  }

  public boolean containsRelation(final String rid) {
    return (StringUtils.isEmpty(rid) ? false : this.containsKey(rid));
  }

  public Relation move2stack(final Relation r, final RelationStack destination) {
    final Relation orig = removeRelation(r);
    return ((orig == null) || (destination == null) ? null : destination.addRelation(r));
  }

  public Relation move2stack(final String rid, final RelationStack destination) {
    final Relation r = removeRelation(rid);
    return ((r == null) || (destination == null) ? null : destination.addRelation(r));
  }

  // ----------------------------------------------------------------

  public StringBuilder dumpRelations(final StringBuilder sb, final boolean verbose) {
    sb.append("#Relations = ");
    sb.append(size());
    for (final Relation r : getRelations()) {
      if (verbose) {
        sb.append('\n');
        sb.append(r);
      }
      else {
        sb.append(", ");
        sb.append(r.getRelationID());
      }
    }
    sb.append('\n');
    return sb;
  }
}
