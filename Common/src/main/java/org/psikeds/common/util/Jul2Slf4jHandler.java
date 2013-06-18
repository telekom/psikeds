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
package org.psikeds.common.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some tools/libraries insist on using Java Util Logging (JUL), but
 * we prefer SLF4J.
 * This implementation of a {@link java.util.logging.Handler} therefore forwards
 * all log records from JUL to SLF4J.
 * 
 * @author marco@juliano.de
 * 
 */
public class Jul2Slf4jHandler extends Handler {

    /**
     * @param record LogRecord
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(final LogRecord record) {
        try {
            final Logger slf4jLogger = LoggerFactory.getLogger(record.getLoggerName());
            final String message = record.getMessage();
            final Throwable exception = record.getThrown();
            final Level level = record.getLevel();
            if (level == Level.SEVERE) {
                slf4jLogger.error(message, exception);
            }
            else if (level == Level.WARNING) {
                slf4jLogger.warn(message, exception);
            }
            else if (level == Level.INFO) {
                slf4jLogger.info(message, exception);
            }
            else if (level == Level.CONFIG || level == Level.FINE) {
                slf4jLogger.debug(message, exception);
            }
            else { // level == Level.FINER || level == Level.FINEST
                slf4jLogger.trace(message, exception);
            }
        }
        catch (final Exception ex) {
            // nothing to do, just don't crash because of logging
        }
    }

    /**
     * 
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
        // nothing to do
    }

    /**
     * @throws SecurityException
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() throws SecurityException {
        // nothing to do
    }
}
