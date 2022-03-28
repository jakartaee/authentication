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
package ee.jakarta.tck.authentication.test.invoke;

import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;

/**
 * This tests that a SAM is able to obtain and call an EJB bean when the request is to a public resource
 * (a resource for which no security constraints have been set).
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class InvokeEJBBeanPublicTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void publicInvokeEJBFromValidateRequest() {
        String response = getFromServerPath("public/servlet?tech=ejb");

        assertTrue(
            "Response did not contain output from EJB bean for validateRequest for public resource.",
            response.contains("validateRequest: Called from EJB")
        );
    }

    @Test
    public void publicInvokeEJBFromCleanSubject() {
        String response = getFromServerPath("public/servlet?tech=ejb");

        assertTrue(
            "Response did not contain output from EJB bean for cleanSubject for public resource.",
            response.contains("cleanSubject: Called from EJB")
        );
    }

    @Test
    public void publicInvokeEJBFromSecureResponse() {
        String response = getFromServerPath("public/servlet?tech=ejb");

        assertTrue(
            "Response did not contain output from EJB bean for secureResponse for public resource.",
            response.contains("secureResponse: Called from EJB")
        );
    }

}