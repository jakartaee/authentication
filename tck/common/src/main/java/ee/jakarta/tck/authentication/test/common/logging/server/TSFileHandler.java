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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * FileHandler capable of rolling files.
 */
public class TSFileHandler extends StreamHandler {

    private final File file;
    private boolean closed;

    public TSFileHandler(File file) throws IOException {
        this.file = file;
        setLevel(Level.INFO);
        setEncoding(StandardCharsets.UTF_8.name());
        setFormatter(new TSXMLFormatter());
        setOutputStream(prepareFile(file));
    }

    public File getFile() {
        return this.file;
    }

    /**
     * Rolls to a new file
     */
    public synchronized void roll() {
        try {
            super.close();
            setOutputStream(prepareFile(file));
        } catch (SecurityException | IOException e) {
            throw new IllegalStateException("Failed to roll the file " + file, e);
        }
    }

    @Override
    public synchronized void publish(LogRecord log) {
        super.publish(log);
        flush();
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
        super.close();
    }


    public boolean isClosed() {
        return closed;
    }


    private static OutputStream prepareFile(File file) throws FileNotFoundException {
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
        return new BufferedOutputStream(new FileOutputStream(file, true), 8192);
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
