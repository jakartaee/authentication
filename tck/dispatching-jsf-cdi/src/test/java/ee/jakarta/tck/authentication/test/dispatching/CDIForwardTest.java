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
package ee.jakarta.tck.authentication.test.dispatching;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * The basic forward test tests that a SAM is able to forward to a simple Servlet.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class CDIForwardTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return 
            defaultWebArchive()
                .addAsWebInfResource(resource("beans.xml")
        );
    }

    /**
     * Tests that the forwarded resource can utilize a CDI bean
     *
     * @throws IOException
     */
    @Test
    public void testCDIForwardViaPublicResource() throws IOException {
        String response = getFromServerPath("public/servlet");

        assertTrue(
            "Response did not contain output from public Servlet with CDI that SAM forwarded to.",
            response.contains("response from forwardedServlet - Called from CDI")
        );
    }

    /**
     * Tests that the forwarded resource can utilize a CDI bean
     *
     * @throws IOException
     */
    @Test
    public void testCDIForwardViaProtectedResource() throws IOException {
        String response = getFromServerPath("protected/servlet");

        assertTrue(
            "Response did not contain output from protected Servlet with CDI that SAM forwarded to.",
            response.contains("response from forwardedServlet - Called from CDI")
        );
    }

    /**
     * Tests that the forwarded resource has the correct servlet path
     *
     * @throws IOException
     */
    @Test
    public void testCDIForwardWithRequestPublic() throws IOException {
        String response = getFromServerPath("public/servlet");

        assertTrue(
            "Servletpath reported by servlet request after forward from SAM not as expected.",
            response.contains("servletPath via Servlet - /forwardedServlet")
        );
    }

    /**
     * Tests that the forwarded resource has the correct servlet path
     *
     * @throws IOException
     */
    @Test
    public void testCDIForwardWithRequestProtected() throws IOException {
        String response = getFromServerPath("protected/servlet");

        assertTrue(
            "Servletpath reported by servlet request after forward from SAM not as expected.",
            response.contains("servletPath via Servlet - /forwardedServlet")
        );
    }

}