/*******************************************************************************
 * psiKeds :- ps induced knowledge entity delivery system
 *
 * Copyright (c) 2013, 2014 Karsten Reincke, Marco Juliano, Deutsche Telekom AG
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
package org.psikeds.queryagent.interfaces.presenter.pojos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Interface object representing a Response of the Init-Request (which is void).
 *
 * Note: Reading from and writing to JSON works out of the box.
 *       However for XML the XmlRootElement annotation is required.
 *
 * @author marco@juliano.de
 *
 */
@XmlRootElement(name = "InitRespMsg")
public class InitResponse extends POJO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String sessionID;
  private KnowledgeEntity knowledgeEntity;

  public InitResponse() {
    this(null, null);
  }

  public InitResponse(final String id, final KnowledgeEntity ke) {
    super();
    this.sessionID = id;
    this.knowledgeEntity = ke;
  }

  public String getSessionID() {
    return this.sessionID;
  }

  public void setSessionID(final String sessionID) {
    this.sessionID = sessionID;
  }

  public KnowledgeEntity getKnowledgeEntity() {
    return this.knowledgeEntity;
  }

  public void setKnowledgeEntity(final KnowledgeEntity knowledgeEntity) {
    this.knowledgeEntity = knowledgeEntity;
  }
}
