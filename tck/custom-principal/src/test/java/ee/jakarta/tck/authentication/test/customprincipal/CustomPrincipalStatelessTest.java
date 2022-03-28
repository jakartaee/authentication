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
package ee.jakarta.tck.authentication.test.customprincipal;

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
 * Idential test as in basic-authentication, but now performed against a SAM which sets a custom principal.
 * Therefore tests that for this kind of usage of the PrincipalCallback JASPIC is stateless just as well.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class CustomPrincipalStatelessTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    /**
     * Tests that access to a protected page does not depend on the authenticated identity that was established in a previous
     * request.
     */
    @Test
    public void testProtectedAccessIsStateless() throws IOException {

        // -------------------- Request 1 ---------------------------

        // Accessing protected page without login
        String response = getFromServerPath("protected/servlet");

        // Not logged-in thus should not be accessible.
        assertFalse(response.contains("This is a protected servlet"));

        // -------------------- Request 2 ---------------------------

        // JASPIC is stateless and login (re-authenticate) has to happen for every request
        //
        // If the following fails but "testProtectedPageLoggedin" has succeeded,
        // the container has probably remembered the "unauthenticated identity", e.g. it has remembered that
        // we're not authenticated and it will deny further attempts to authenticate. This may happen when
        // the container does not correctly recognize The Jakarta Authentication protocol for "do nothing".

        response = getFromServerPath("protected/servlet?doLogin=true");

        // Now has to be logged-in so page is accessible
        assertTrue("Could not access protected page, but should be able to. "
            + "Did the container remember the previously set 'unauthenticated identity'?",
            response.contains("This is a protected servlet"));

        // -------------------- Request 3 ---------------------------

        // JASPIC is stateless and login (re-authenticate) has to happen for every request
        //
        // In the following method we do a call without logging in after one where we did login.
        // The container should not remember this login and has to deny access.
        response = getFromServerPath("protected/servlet");

        // Not logged-in thus should not be accessible.
        assertFalse("Could access protected page, but should not be able to. "
            + "Did the container remember the authenticated identity that was set in previous request?",
            response.contains("This is a protected servlet"));
    }

    /**
     * Tests that access to a protected page does not depend on the authenticated identity that was established in a previous
     * request, but use a different request order than the previous test.
     */
    @Test
    public void testProtectedAccessIsStateless2() throws IOException {

        // -------------------- Request 1 ---------------------------

        // Start with doing a login
        String response = getFromServerPath("protected/servlet?doLogin=true");

        // -------------------- Request 2 ---------------------------

        // JASPIC is stateless and login (re-authenticate) has to happen for every request
        //
        // In the following method we do a call without logging in after one where we did login.
        // The container should not remember this login and has to deny access.

        // Accessing protected page without login
        response = getFromServerPath("protected/servlet");

        // Not logged-in thus should not be accessible.
        assertFalse(
            "Could access protected page, but should not be able to. " +
	        "Did the container remember the authenticated identity that was set in previous request?",
            response.contains("This is a protected servlet")
        );
    }

    @Test
    public void testPublicAccessIsStateless() throws IOException {

        // -------------------- Request 1 ---------------------------

        String response = getFromServerPath("public/servlet");

        // Not logged-in
        assertTrue(response.contains("web username: null"));
        assertTrue(response.contains("web user has role \"architect\": false"));

        // -------------------- Request 2 ---------------------------

        response = getFromServerPath("public/servlet?doLogin=true");

        // Now has to be logged-in
        assertTrue(
            "Username is not the expected one 'test'",
            response.contains("web username: test")
        );
        assertTrue(
            "Username is correct, but the expected role 'architect' is not present.",
            response.contains("web user has role \"architect\": true")
        );

        // -------------------- Request 3 ---------------------------

        response = getFromServerPath("public/servlet");

        // Not logged-in
        assertTrue(
            "Should not be authenticated, but username was not null. Did the container remember it from previous request?",
            response.contains("web username: null")
        );
        assertTrue(
            "Request was not authenticated (username correctly null), but unauthenticated user incorrectly has role 'architect'",
            response.contains("web user has role \"architect\": false")
        );
    }

    /**
     * Tests independently from being able to access a protected resource if any details of a previously established
     * authenticated identity are remembered
     */
    @Test
    public void testProtectedThenPublicAccessIsStateless() throws IOException {

        // -------------------- Request 1 ---------------------------

        // Accessing protected page with login
        String response = getFromServerPath("protected/servlet?doLogin=true");

        // -------------------- Request 2 ---------------------------

        // Accessing public page without login
        response = getFromServerPath("public/servlet");

        // No details should linger around
        assertFalse(
            "User principal was 'test', but it should be null here. " +
            "The container seemed to have remembered it from the previous request.",
            response.contains("web username: test")
        );
        assertTrue(
            "User principal was not null, but it should be null here. ",
            response.contains("web username: null")
        );
        assertTrue(
            "The unauthenticated user has the role 'architect', which should not be the case. " +
            "The container seemed to have remembered it from the previous request.",
            response.contains("web user has role \"architect\": false")
        );
    }

}