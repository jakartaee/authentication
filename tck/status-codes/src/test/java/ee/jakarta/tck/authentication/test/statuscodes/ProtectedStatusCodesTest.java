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
package ee.jakarta.tck.authentication.test.statuscodes;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.jakarta.tck.authentication.test.common.ArquillianBase;


/**
 * This tests that a SAM can set a 404 response code when a protected resource is requested.
 * Note the resource is not actual invoked, as the SAM returns SEND_FAILURE.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class ProtectedStatusCodesTest extends ArquillianBase {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return defaultArchive();
    }

    @Test
    public void test404inResponse() throws IOException  {

        int code = getWebClient().getPage(getBase() + "protected/servlet")
                                 .getWebResponse()
                                 .getStatusCode();

        assertEquals(
            "Response should have 404 not found as status code, but did not.",
            404, code
        );
    }

}