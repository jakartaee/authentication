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
package ee.jakarta.tck.authentication.test.registersession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * Variant of the RegisterSessionTest, where it's tested
 * if the authenticated identity restored by the runtime correctly propagates
 * to EJB.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class RegisterSessionEJBPropagationTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void testRemembersSession() throws IOException {

        // -------------------- Request 1 ---------------------------

        // Accessing protected page without login
        String response = getFromServerPath("protected/servlet");

        // Not logged-in thus should not be accessible.
        assertFalse(response.contains("This is a protected servlet"));


        // -------------------- Request 2 ---------------------------

        // We access the protected page again and now login

        response = getFromServerPath("protected/servlet?doLogin=true");

        // Now has to be logged-in so page is accessible
        assertTrue(
            "Could not access protected page, but should be able to. " +
            "Did the container remember the previously set 'unauthenticated identity'?",
            response.contains("This is a protected servlet")
        );

        // Check principal has right name and right type and roles are available
        checkAuthenticatedIdentity(response);


        // -------------------- Request 3 ---------------------------

        // JASPIC is normally stateless, but for this test the SAM uses the register session feature so now
        // we should be logged-in when doing a call without explicitly logging in again.

        response = getFromServerPath("protected/servlet?continueSession=true");

        // Logged-in thus should be accessible.
        assertTrue(
            "Could not access protected page, but should be able to. " +
            "Did the container not remember the authenticated identity via 'jakarta.servlet.http.registerSession'?",
            response.contains("This is a protected servlet")
        );

        // Both the user name and roles/groups have to be restored

        // *** NOTE ***: The JASPIC 1.1 spec is NOT clear about remembering roles, but spec lead Ron Monzillo clarified that
        // this should indeed be the case. The next JASPIC revision of the spec will have to mention this explicitly.
        // Intuitively it should make sense though that the authenticated identity is fully restored and not partially,
        // but again the spec should make this clear to avoid ambiguity.

        checkAuthenticatedIdentity(response);


        // -------------------- Request 4 ---------------------------

        // The session should also be remembered and propagated to a public EJB

        response = getFromServerPath("public/servlet-public-ejb?continueSession=true");

        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the same
        // user name.
        assertTrue(
            "User should have been authenticated in the web layer and given name \"test\", " +
            " but does not appear to have this name",
            response.contains("web username: test")
        );
        assertTrue(
            "Web has user principal set, but EJB not.",
            response.contains("EJB username: test")
        );


        // -------------------- Request 5 ---------------------------

        // The session should also be remembered and propagated to a protected EJB

        response = getFromServerPath("public/servlet-protected-ejb?continueSession=true");

        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the same
        // user name.
        assertTrue(
            "User should have been authenticated in the web layer and given name \"test\", " +
            " but does not appear to have this name",
            response.contains("web username: test")
        );
        assertTrue(
            "Web has user principal set, but EJB not.",
            response.contains("EJB username: test")
        );

        // Both the web (HttpServletRequest) and EJB (EJBContext) should see that the
        // user has the role "architect".
        assertTrue(response.contains("web user has role \"architect\": true"));
        assertTrue("Web user principal has role \"architect\", but one in EJB doesn't.",
            response.contains("EJB user has role \"architect\": true"));

    }

    private void checkAuthenticatedIdentity( String response) {

        // Has to be logged-in with the right principal
        assertTrue(
            "Authenticated but username is not the expected one 'test'",
            response.contains("web username: test")
        );
        assertTrue(
            "Authentication succeeded and username is correct, but the expected role 'architect' is not present.",
            response.contains("web user has role \"architect\": true"));

        assertTrue(
            "Authentication succeeded and username and roles are correct, but principal type is not the expected custom type.",
            response.contains("isCustomPrincipal: false")
        );
    }



}