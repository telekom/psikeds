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
package org.psikeds.resolutionengine.services;

// import static org.junit.Assert.assertEquals;
//
// import java.io.InputStream;
//
// import javax.ws.rs.core.MediaType;
// import javax.ws.rs.core.Response;
//
// import org.apache.cxf.helpers.IOUtils;
// import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChocolateBusinessServiceTest {

  private static String endpointUrl;

  @BeforeClass
  public static void beforeClass() {
    endpointUrl = System.getProperty("service.url");
    System.out.println("Service URL = " + endpointUrl);
  }

  @Test
  public void testList() throws Exception {
    // WebClient client = WebClient.create(endpointUrl + "/chocolate/list");
    // Response r = client.accept(MediaType.APPLICATION_JSON).get();
    // assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
    // String value = IOUtils.toString((InputStream) r.getEntity());
    // System.out.println(value);
  }

  @Test
  public void testJsonRoundtrip() throws Exception {
    // WebClient client = WebClient.create(endpointUrl +
    // "/chocolate/select");
    // Response r = client.accept(MediaType.APPLICATION_JSON)
    // .type(MediaType.APPLICATION_JSON)
    // .post("IDX4711");
    // assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
    // assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
    // String value = IOUtils.toString((InputStream) r.getEntity());
    // System.out.println(value);
  }
}
