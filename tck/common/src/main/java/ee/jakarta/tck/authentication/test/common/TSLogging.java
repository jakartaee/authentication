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

package ee.jakarta.tck.authentication.test.common;

import java.io.File;


/**
 * Resolves system properties for log file locations and determines if the code is running on the
 * test side.
 * If the system property "log.file.location" is not set, the log file locations will be null and
 * logging for tests will be disabled.
 * <p>
 * The system properties are read when used, not when this class is initialized: a native image
 * (e.g. Quarkus) may initialize classes when the image is built, with the build's system properties.
 */
public final class TSLogging {

    private static boolean configurationPrinted;

    private TSLogging() {
        // Prevent instantiation
    }

    /**
     * @return true if running on the test side, false in the web application
     */
    public static boolean isTestSide() {
        try {
            // This class should be excluded from war files, but should be available in the test side classpath.
            return Class.forName("ee.jakarta.tck.authentication.test.common.ArquillianBase", false,
                TSLogging.class.getClassLoader()) != null;
        } catch (Exception | LinkageError e) {
            return false;
        }
    }

    /**
     * @return the directory of the log files, or null if logging is disabled
     */
    public static File getDirectory() {
        String dirPath = System.getProperty("log.file.location");
        if (dirPath == null || dirPath.isEmpty()) {
            return null;
        }
        return new File(dirPath).getAbsoluteFile();
    }

    /**
     * @return the log file of the web application, or null if logging is disabled
     */
    public static File getWebappFile() {
        File dir = getDirectory();
        return dir == null ? null : new File(dir, System.getProperty("log.file.name.webapp", "authentication-tck-webapp.log"));
    }

    /**
     * @return the log file of the tests, or null if logging is disabled
     */
    public static File getTestFile() {
        File dir = getDirectory();
        return dir == null ? null : new File(dir, System.getProperty("log.file.name.test", "authentication-tck-test.log"));
    }

    /**
     * Prints the log file locations, once.
     */
    public static synchronized void printConfiguration() {
        if (configurationPrinted) {
            return;
        }
        configurationPrinted = true;
        System.err.println("Log file locations:"
            + "\n  test side: " + isTestSide()
            + "\n       test: " + getTestFile()
            + "\n     webapp: " + getWebappFile());
    }
}
