/*
 * Copyright (c) 2024, 2025 Contributors to the Eclipse Foundation.
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.jakarta.tck.authentication.test.common.logging.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ee.jakarta.tck.authentication.test.common.TSLogging;


/**
 * TSLogger is the custom Logger which extends java.util.Logger
 *
 * @author Raja Perumal 07/13/02
 * @author David Matejcek 2025
 **/
public class TSLogger extends Logger {

    private static final String DEFAULT_LOGGER_NAME = "jacc";
    private static final LogManager LOG_MANAGER = LogManager.getLogManager();
    private static final Map<String, TSLogger> LOGGERS = new HashMap<>();
    private static final Map<File, TSFileHandler> HANDLERS = new HashMap<>();

    private TSFileHandler handler;
    private boolean handlerResolved;

    private TSLogger(String name) {
        super(name, null);
        setLevel(Level.INFO);
    }

    /**
     * Opens the log file on first use rather than when the logger is created: loggers may be created
     * while a native image (e.g. Quarkus) is built, where no file may be opened yet, and where the
     * system properties with the log file location are not the ones the application runs with.
     */
    private void resolveHandler() {
        synchronized (TSLogger.class) {
            if (handlerResolved) {
                return;
            }
            handlerResolved = true;
            TSLogging.printConfiguration();
            File file = TSLogging.isTestSide() ? TSLogging.getTestFile() : TSLogging.getWebappFile();
            if (file == null) {
                return;
            }
            handler = HANDLERS.computeIfAbsent(file, TSLogger::createFileHandler);
            addHandler(handler);
        }
    }

    /**
     * Log a message, with no arguments.
     * <p>
     * If the logger is currently enabled for the given message level then the given message is forwarded to all the
     * registered output Handler objects.
     * <p>
     *
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg The string message (or a key in the message catalog)
     */
    @Override
    public void log(Level level, String msg) {
        // assign default context (authentication_ctx) to all messages ???
        log(level, msg, "authentication_ctx");
    }

    /**
     * Log a message, with no arguments.
     * <p>
     * If the logger is currently enabled for the given message level then the given message is forwarded to all the
     * registered output Handler objects.
     * <p>
     *
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg The string message (or a key in the message catalog)
     * @param contextId the logging context Id
     */
    public void log(Level level, String msg, String contextId) {
        if (!isLoggable(level)) {
            return;
        }

        TSLogRecord lr = new TSLogRecord(level, msg, contextId);
        String rbn = null;

        Logger target = this;
        while (target != null) {
            rbn = target.getResourceBundleName();
            if (rbn != null) {
                break;
            }
            target = target.getParent();
        }

        if (rbn != null) {
            lr.setResourceBundleName(rbn);
        }

        log(lr);
    }

    @Override
    public void log(LogRecord record) {
        resolveHandler();
        super.log(record);
    }

    /**
     * Log a TSLogRecord.
     *
     * @param record the TSLogRecord to be published
     */
    public void log(TSLogRecord record) {
        if (!isLoggable(record.getLevel())) {
            return;
        }
        resolveHandler();

        // Post the LogRecord to all our Handlers, and then to
        // our parents' handlers, all the way up the tree.
        TSLogger logger = this;
        while (logger != null) {
            Handler targets[] = logger.getHandlers();
            if (targets != null) {
                for (int i = 0; i < targets.length; i++) {
                     targets[i].publish(record);
                }
            }
            if (!logger.getUseParentHandlers()) {
                break;
            }
            logger = null;
        }
    }

    /**
     * Closes and unregisters the handler.
     * Despite we share handlers between loggers, webapp and test loggers usually stop all at the same time.
     */
    public void stop() {
        synchronized (TSLogger.class) {
            stopLogger();
        }
    }

    private void stopLogger() {
        LOGGERS.remove(getName());
        handlerResolved = false;
        if (handler == null) {
            return;
        }
        removeHandler(handler);
        HANDLERS.remove(handler.getFile());
        handler.close();
        handler = null;
    }

    public static synchronized TSLogger getTSLogger() {
        return getTSLogger(DEFAULT_LOGGER_NAME);
    }

    /**
     * Find or create a logger for a named subsystem. If a logger has already been created with the given name it is
     * returned. Otherwise a new logger is created.
     * <p>
     * If a new logger is created its log level will be configured based on the LogManager configuration and it will
     * configured to also send logging output to its parent's handlers. It will be registered in the LogManager global
     * namespace.
     *
     * @param name A name for the logger. This should be a dot-separated name and should normally be based on the package
     * name or class name of the subsystem, such as java.net or javax.swing
     * @return a suitable Logger
     */
    public static synchronized TSLogger getTSLogger(String name) {
        final TSLogger logger = LOGGERS.get(name);
        if (logger != null) {
            return logger;
        }
        final TSLogger newlogger = new TSLogger(name);
        LOGGERS.put(name, newlogger);
        // Some LogManagers (e.g. JBoss LogManager) create a logger for every name asked for, so a logger
        // with this name may exist already. TSLogger doesn't need the LogManager: it has its own handler,
        // and the LOGGERS map returns this instance next time.
        final Logger existing = LOG_MANAGER.getLogger(name);
        if (existing == null) {
            LOG_MANAGER.addLogger(newlogger);
        }
        return newlogger;
    }

    /**
     * Closes all TSLoggers and their handlers.
     */
    public static synchronized void closeAll() {
        Set<TSLogger> values = new HashSet<>(LOGGERS.values());
        for (TSLogger logger : values) {
            logger.stop();
        }
        LOGGERS.clear();
        HANDLERS.clear();
    }

    private static TSFileHandler createFileHandler(File file) {
        try {
            return new TSFileHandler(file);
        } catch (IOException e) {
            throw new RuntimeException("FileHandler initialization failed", e);
        }
    }
}
