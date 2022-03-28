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
package ee.jakarta.tck.authentication.test.authzpropagation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * This tests that the established authenticated identity set from JASPIC propagates correctly
 * to a JACC provider.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class AuthzPropagationProtectedTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void callingJACCWhenAuthenticated() {

     String response = getFromServerPath("protected/servlet?doLogin=true");

        // This can basically only fail if JACC itself somehow doesn't work.
        // Unfortunately this is the case for a bunch of certified servers, which
        // either demand some activation of JACC, or don't ship with a default
        // provider at all (which are both spec violations)
        assertFalse(
            "JACC doesn't seem to be available.",
            response.contains("JACC doesn't seem to be available.")
        );

        // Test if we have access to protected/servlet from within that servlet.
        // If this fails role propagation and/or JACC failed, since this is obviously
        // impossible.
        assertTrue(
            "Did not have access to protected servlet from within that Servlet. " +
            " Perhaps the roles did not propogate from JASPIC to JACC and the" +
            " server didn't use JACC to grant access to invoking said Servlet?",
            response.contains("Has access to /protected/servlet: true")
        );
    }

}