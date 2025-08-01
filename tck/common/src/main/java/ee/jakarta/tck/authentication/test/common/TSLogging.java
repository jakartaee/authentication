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
 */
public final class TSLogging {

    public static final boolean IS_TEST_SIDE = isTestSide();
    public static final File DIR;
    public static final File FILE_WEBAPP;
    public static final File FILE_TEST;

    static {
        String dirPath = System.getProperty("log.file.location");
        if (dirPath == null || dirPath.isEmpty()) {
            DIR = null;
            FILE_WEBAPP = null;
            FILE_TEST = null;
        } else {
            DIR = new File(dirPath).getAbsoluteFile();
            FILE_WEBAPP = new File(DIR, System.getProperty("log.file.name.webapp", "authentication-tck-webapp.log"));
            FILE_TEST = new File(DIR, System.getProperty("log.file.name.test", "authentication-tck-test.log"));
        }
        System.err.println("Log file locations:"
            + "\n  test side: " + IS_TEST_SIDE
            + "\n       test: " + FILE_TEST
            + "\n     webapp: " + FILE_WEBAPP);
    }

    private TSLogging() {
        // Prevent instantiation
    }

    private static boolean isTestSide() {
        try {
            // This class should be excluded from war files, but should be available in the test side classpath.
            return Class.forName("ee.jakarta.tck.authentication.test.common.ArquillianBase", false,
                TSLogging.class.getClassLoader()) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
