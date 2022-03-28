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
package ee.jakarta.tck.authentication.test.programmaticauthentication;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * This tests that a call from a Servlet to HttpServletRequest#authenticate can result
 * in a successful authentication.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class ProgrammaticAuthenticationTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void testAuthenticate() throws IOException {
        assertAuthenticated(getFromServerPath("public/authenticate"));
    }

    @Test
    public void testAuthenticateFailFirstOnce() throws IOException {
        // Before authenticating successfully, call request.authenticate which
        // is known to fail (do nothing)
        assertAuthenticated(getFromServerPath("public/authenticate?failFirst=1"));
    }

    @Test
    public void testAuthenticateFailFirstTwice() throws IOException {
        // Before authenticating successfully, call request.authenticate twice which
        // are both known to fail (do nothing)
        assertAuthenticated(getFromServerPath("public/authenticate?failFirst=2"));
    }

    private void assertAuthenticated(String response) {

        // Should not be authenticated in the "before" case, which is
        // before request.authentiate is called
        assertTrue(
            "Should not be authenticated yet, but a username other than null was encountered. " +
            "This is not correct.",
            response.contains("before web username: null")
        );
        assertTrue(
            "Should not be authenticated yet, but the user seems to have the role \"architect\". " +
            "This is not correct.",
            response.contains("before web user has role \"architect\": false")
        );

        // The main request.authenticate causes the SAM to be called which always authenticates
        assertTrue(
                "Calling request.authenticate should have returned true, but did not.",
                response.contains("request.authenticate outcome: true")
            );

        // Should be authenticated in the "after" case, which is
        // after request.authentiate is called
        assertTrue(
            "User should have been authenticated and given name \"test\", " +
            " but does not appear to have this name",
            response.contains("after web username: test")
        );
        assertTrue(
            "User should have been authenticated and given role \"architect\", " +
            " but does not appear to have this role",
            response.contains("after web user has role \"architect\": true")
        );
    }


}