/*
 * Copyright (c) 2022-2022 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.authentication.test.wrapping;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * This tests that the wrapped request and response a SAM puts into the MessageInfo structure reaches the Servlet that's
 * invoked as well as all filters executed before that.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class WrappingTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void testProgrammaticFilterRequestWrapping() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // The SAM wrapped a request so that it always contains the request attribute "isWrapped" with value true.
        assertTrue("Request wrapped by SAM did not arrive in programmatic Filter.",
            response.contains("programmatic filter request isWrapped: true"));
    }

    @Test
    public void testProgrammaticFilterResponseWrapping() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // The SAM wrapped a response so that it always contains the header "isWrapped" with value true.
        assertTrue("Response wrapped by SAM did not arrive in programmatic Filter.",
            response.contains("programmatic filter response isWrapped: true"));
    }

    @Test
    public void testDeclaredFilterRequestWrapping() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // The SAM wrapped a request so that it always contains the request attribute "isWrapped" with value true.
        assertTrue("Request wrapped by SAM did not arrive in declared Filter.",
            response.contains("declared filter request isWrapped: true"));
    }

    @Test
    public void testDeclaredFilterResponseWrapping() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // The SAM wrapped a response so that it always contains the header "isWrapped" with value true.
        assertTrue("Response wrapped by SAM did not arrive in declared Filter.",
            response.contains("declared filter response isWrapped: true"));
    }

    @Test
    public void testRequestWrapping() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // The SAM wrapped a request so that it always contains the request attribute "isWrapped" with value true.
        assertTrue("Request wrapped by SAM did not arrive in Servlet.",
            response.contains("servlet request isWrapped: true"));
    }

    @Test
    public void testResponseWrapping() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // The SAM wrapped a response so that it always contains the header "isWrapped" with value true.
        assertTrue("Response wrapped by SAM did not arrive in Servlet.",
            response.contains("servlet response isWrapped: true"));
    }

}