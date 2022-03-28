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
package ee.jakarta.tck.authentication.test.ejbpropagation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * This tests that the established authenticated identity propagates correctly
 * from the web layer to a "public" EJB (an EJB without declarative role
 * checking) and that after logging out but still within the same request this
 * identity is cleared.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class PublicEJBPropagationLogoutTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void publicServletCallingPublicEJBThenLogout() {

        String response = getFromServerPath("public/servlet-public-ejb-logout?doLogin=true");

        System.out.println(response);

        // Both the web (HttpServletRequest) and EJB (EJBContext) should see the
        // same user name.

        assertTrue(
            "User should have been authenticated in the web layer and given name \"test\", " +
            " but does not appear to have this name",
            response.contains("web username: test")
        );
        assertTrue(
            "Web has user principal set, but EJB not.",
            response.contains("EJB username: test")
        );


        // After logging out, both the web and EJB should no longer see the user
        // name

        assertFalse(
            "Web module did not clear authenticated identity after logout",
            response.contains("web username after logout: test")
        );
        assertFalse(
            "EJB did not clear authenticated identity after logout",
            response.contains("EJB username after logout: test")
        );

    }

}