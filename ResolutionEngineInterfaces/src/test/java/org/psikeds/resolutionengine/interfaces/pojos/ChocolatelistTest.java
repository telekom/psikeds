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
package org.psikeds.resolutionengine.interfaces.pojos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class ChocolatelistTest {

    private static final String JSON_FILE = "C:/TEMP/chocolates.json";

    private Chocolatelist cl;
    private ObjectMapper mapper;

    /**
     * Initialize test environment
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.cl = new Chocolatelist();
        this.cl.add(new Chocolate("cid111", "Vollmilch"));
        this.cl.add(new Chocolate("cid222", "Haselnuss"));
        this.cl.add(new Chocolate("cid333", "Traubennuss"));
        this.cl.add(new Chocolate("cid444", "Zartbitter"));
    }

    @Test
    public void testPojosAndJson() throws Exception {
        final File f = new File(JSON_FILE);
        this.mapper.writeValue(f, this.cl);
        assertTrue("Could not write Objects to File " + JSON_FILE,
                f != null && f.exists());
        final Chocolatelist l = this.mapper.readValue(f, Chocolatelist.class);
        assertTrue("Could not read Chocolates from File " + JSON_FILE,
                l != null && l.size() == 4);
        final Chocolate c = l.getChocolates().get(2);
        assertEquals("cid333", c.getRefid());
        assertEquals("Traubennuss", c.getDescription());
        f.deleteOnExit();
    }
}
