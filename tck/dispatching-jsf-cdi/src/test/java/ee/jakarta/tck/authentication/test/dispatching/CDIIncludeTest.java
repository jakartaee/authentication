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
public class CDIIncludeTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void testCDIIncludeViaPublicResource() throws IOException {

        String response = getFromServerPath("public/servlet?dispatch=include");

        assertTrue(
            "Response did not contain output from public Servlet with CDI that SAM included to.",
            response.contains("response from includedServlet - Called from CDI")
        );

        assertTrue(
            "Response did not contain output from target Servlet after included one.",
            response.contains("Resource invoked")
        );

        assertTrue(
            "Output from included Servlet with CDI and target Servlet in wrong order.",
            response.indexOf("response from includedServlet - Called from CDI") < response.indexOf("Resource invoked")
        );
    }

}