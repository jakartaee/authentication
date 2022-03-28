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
package ee.jakarta.tck.authentication.test.lifecycle;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This tests that the two main methods of a SAM, {@link ServerAuthModule#validateRequest} and
 * {@link ServerAuthModule#secureResponse} are called at the right time, which is resp. before and after the resource (e.g. a
 * Servlet) is invoked.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class AuthModuleMethodInvocationTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    /**
     * Test that the main SAM methods are called and are called in the correct order.
     *
     * The rule seems simple:
     * <ul>
     * <li>First call validateRequest() in the SAM.
     * <li>Then invoke the requested resource (e.g. a Servlet or JSP page)
     * <li>Finally call secureResponse() in the SAM
     * </ul>
     */
    @Test
    public void testBasicSAMMethodsCalled() throws IOException {

        String response = getFromServerPath("protected/servlet");

        // First test if individual methods are called
        assertTrue("SAM method validateRequest not called, but should have been.",
            response.contains("validateRequest invoked"));
        assertTrue("Resource (Servlet) not invoked, but should have been.", response.contains("Resource invoked"));

        // The previous two methods are rare to not be called, but secureResponse is more likely to fail. Seemingly it's hard
        // to understand what this method should do exactly.
        assertTrue("SAM method secureResponse not called, but should have been.",
            response.contains("secureResponse invoked"));

        int validateRequestIndex = response.indexOf("validateRequest invoked");
        int resourceIndex = response.indexOf("Resource invoked");
        int secureResponseIndex = response.indexOf("secureResponse invoked");

        // Finally the order should be correct. More than a few implementations call secureResponse before the resource is
        // invoked.
        assertTrue("SAM methods called in wrong order",
            validateRequestIndex < resourceIndex && resourceIndex <  secureResponseIndex);
    }

    /**
     * Test that the SAM's cleanSubject method is called following a call to {@link HttpServletRequest#logout()}.
     */
    @Test
    public void testLogout() throws IOException {

        // Note that we don't explicitly log-in; the test SAM uses for this test does that automatically before the resource
        // (servlet)
        // is invoked. Once we reach the Servlet we should be logged-in and can proceed to logout.
        String response = getFromServerPath("protected/servlet?doLogout=true");

        assertTrue("SAM method cleanSubject not called, but should have been.",
            response.contains("cleanSubject invoked"));
    }

}