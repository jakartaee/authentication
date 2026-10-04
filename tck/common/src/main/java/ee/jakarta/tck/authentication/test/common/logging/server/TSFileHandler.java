/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * FileHandler capable of rolling files.
 * <p>
 * It doesn't keep the file open: every record is appended by opening the file, writing and closing it
 * again. The handler can therefore be created and used while a native image (e.g. Quarkus) is built,
 * where Servlet listeners may already run, without an open file ending up in the image.
 */
public class TSFileHandler extends Handler {

    private final File file;
    private boolean closed;

    public TSFileHandler(File file) throws IOException {
        this.file = file;
        setLevel(Level.INFO);
        setEncoding(StandardCharsets.UTF_8.name());
        setFormatter(new TSXMLFormatter());
        prepareFile(file);
        write(getFormatter().getHead(this));
    }

    public File getFile() {
        return this.file;
    }

    /**
     * Rolls to a new file
     */
    public synchronized void roll() {
        try {
            write(getFormatter().getTail(this));
            prepareFile(file);
            write(getFormatter().getHead(this));
        } catch (SecurityException | IOException e) {
            throw new IllegalStateException("Failed to roll the file " + file, e);
        }
    }

    @Override
    public synchronized void publish(LogRecord log) {
        if (isClosed() || !isLoggable(log)) {
            return;
        }
        final String message;
        try {
            message = getFormatter().format(log);
        } catch (Exception e) {
            reportError(null, e, ErrorManager.FORMAT_FAILURE);
            return;
        }
        try {
            write(message);
        } catch (IOException e) {
            reportError(null, e, ErrorManager.WRITE_FAILURE);
        }
    }

    @Override
    public void flush() {
        // Every record is written and the file closed right away
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + file + "]";
    }

    @Override
    public synchronized void close() {
        if (isClosed()) {
            return;
        }
        System.err.println("TSFileHandler: closing log handler using file: " + file);
        closed = true;
        try {
            write(getFormatter().getTail(this));
        } catch (IOException e) {
            reportError(null, e, ErrorManager.CLOSE_FAILURE);
        }
    }


    public boolean isClosed() {
        return closed;
    }


    private void write(String text) throws IOException {
        try (OutputStream output = new FileOutputStream(file, true)) {
            output.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }


    private static void prepareFile(File file) {
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("The file must be absolute: " + file);
        }
        final File dir = file.getParentFile();
        if (file.exists()) {
            roll(file);
        } else {
            System.err.println("TSFileHandler: the log file " + file + " will be created as the first in the sequence");
            if (!dir.isDirectory() && !dir.mkdirs()) {
                System.err.println("TSFileHandler: ERROR: failed to create directory for logs: " + dir);
            }
        }
        final File fileLock = new File(file.getAbsolutePath() + ".lck");
        if (fileLock.exists()) {
            throw new IllegalStateException("The lock file exists, another handler probably uses it!");
        }
    }


    private static void roll(File file) {
        File oldFile = findNewName(file);
        System.err.println("TSFileHandler: Rolling the log file " + file + " to " + oldFile);
        if (!file.renameTo(oldFile)) {
            System.err.println("TSFileHandler: ERROR: failed to rename " + file + " to " + oldFile);
        }
    }


    private static File findNewName(File file) {
        while(true) {
            // It is easy to find "the border" by time.
            File renamed = new File(file.getParent(), file.getName() + "." + LocalDateTime.now());
            if (!renamed.exists()) {
                return renamed;
            }
        }
    }
}
