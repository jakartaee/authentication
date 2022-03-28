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

/**
 * This tests that the "jakarta.security.auth.message.MessagePolicy.isMandatory" key
 * in the message info map is "true" for a protected resource, and not "true" for
 * a public resource.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class IsMandatoryTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void testPublicIsNonMandatory() throws IOException {
        String response = getFromServerPath("public/servlet");

        assertTrue("Resource (Servlet) not invoked, but should have been.", response.contains("Public resource invoked"));

        assertTrue("isMandatory should be false for public resource, but was not.",
            response.contains("isMandatory: false"));
    }

    @Test
    public void testProtectedIsMandatory() throws IOException {
        String response = getFromServerPath("protected/servlet");

        assertTrue("Resource (Servlet) not invoked, but should have been.", response.contains("Resource invoked"));

        assertTrue("isMandatory should be true for protected resource, but was not.",
                response.contains("isMandatory: true"));
    }


}