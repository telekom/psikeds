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
package org.psikeds.common.reqid.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import org.psikeds.common.reqid.RequestIdGenerator;

/**
 * Generates a random ASCII-String by generating random Bytes
 * and encoding them Base64.
 * 
 * Note: The Base64-encoded String is longer than the specified
 * Number of Bytes.
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * 
 * @author marco@juliano.de
 * 
 */
public class RandomStringGenerator implements RequestIdGenerator {

    /**
     * Default algorithm used for generating (pseudo) random bytes
     */
    private static final String DEFAULT_RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * Default length of random String: 12 Bytes = 96 Bits = 16 Chars in Base64
     */
    private static final int DEFAULT_NUMBER_OF_BYTES = 12;

    private String randomAlgorithm;
    private int numberOfBytes;
    private SecureRandom secran;

    public RandomStringGenerator() throws NoSuchAlgorithmException {
        this(DEFAULT_RANDOM_ALGORITHM, DEFAULT_NUMBER_OF_BYTES);
    }

    public RandomStringGenerator(final String randomAlgorithm, final int numberOfBytes) throws NoSuchAlgorithmException {
        this.secran = null;
        setNumberOfBytes(numberOfBytes);
        setRandomAlgorithm(randomAlgorithm);
    }

    /**
     * @param numberOfBytes the numberOfBytes to set
     */
    public void setNumberOfBytes(final int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

    /**
     * @param randomAlgorithm the randomAlgorithm to set
     */
    public void setRandomAlgorithm(final String randomAlgorithm) throws NoSuchAlgorithmException {
        if (this.secran == null || !randomAlgorithm.equals(this.randomAlgorithm)) {
            this.secran = SecureRandom.getInstance(randomAlgorithm);
            this.randomAlgorithm = randomAlgorithm;
        }
    }

    /**
     * @return
     * @see org.psikeds.common.reqid.RequestIdGenerator#getNextReqId()
     */
    @Override
    public String getNextReqId() {
        final byte[] random = new byte[this.numberOfBytes];
        this.secran.nextBytes(random);
        final byte[] encoded = Base64.encodeBase64(random);
        // No need for specifying a Charset, because this
        // is Base64, i.e. 7bit-ASCII
        return new String(encoded);
    }
}
