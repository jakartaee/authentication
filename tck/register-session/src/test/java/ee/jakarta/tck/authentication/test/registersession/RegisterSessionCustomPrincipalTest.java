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
 * Variant of the {@link RegisterSessionTest}, where a custom principal is used instead
 * of a container provided one. This is particularly challenging since the SAM has to
 * pass the principal obtained from HttpServletRequest into the CallbackHandler, which
 * then somehow has to recognize this as the signal to continue an authenticated session.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class RegisterSessionCustomPrincipalTest extends ArquillianBase {

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

        response = getFromServerPath("protected/servlet?doLogin=true&customPrincipal=true");

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

        // The session should also be remembered for other resources, including public ones

        response = getFromServerPath("public/servlet?continueSession=true");

        // This test almost can't fail, but include for clarity
        assertTrue(response.contains("This is a public servlet"));

        // When accessing the public page, the username and roles should be restored and be available
        // just as on protected pages
        checkAuthenticatedIdentity(response);
    }

    @Test
    public void testJoinSessionIsOptional() throws IOException {

        // -------------------- Request 1 ---------------------------

        // We access a protected page and login
        //

        String response = getFromServerPath("protected/servlet?doLogin=true&customPrincipal=true");

		// Now has to be logged-in so page is accessible
		assertTrue(
			"Could not access protected page, but should be able to. " +
	        "Did the container remember the previously set 'unauthenticated identity'?",
			response.contains("This is a protected servlet")
		);

		 // Check principal has right name and right type and roles are available
        checkAuthenticatedIdentity(response);




        // -------------------- Request 2 ---------------------------

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
        // Check principal has right name and right type and roles are available
        checkAuthenticatedIdentity(response);


        // -------------------- Request 3 ---------------------------

        // Although the container remembers the authentication session, the SAM needs to OPT-IN to it.
        // If the SAM instead "does nothing", we should not have access to the protected resource anymore
        // even within the same HTTP session.

        response = getFromServerPath("protected/servlet");
        assertFalse(response.contains("This is a protected servlet"));


        // -------------------- Request 4 ---------------------------

        // Access to a public page is unaffected by joining or not joining the session, but if we do not join the
        // session we shouldn't see the user's name and roles.

        response = getFromServerPath("public/servlet");

        assertTrue(
            "Could not access public page, but should be able to. " +
            "Does the container have an automatic session fixation prevention?",
            response.contains("This is a public servlet")
        );
        assertFalse(
            "SAM did not join authentication session and should be anonymous, but username is name of session identity.",
            response.contains("web username: test")
        );
        assertFalse(
            "SAM did not join authentication session and should be anonymous without roles, but has role of session identity.",
            response.contains("web user has role \"architect\": true")
        );
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
            response.contains("isCustomPrincipal: true")
        );
    }



}