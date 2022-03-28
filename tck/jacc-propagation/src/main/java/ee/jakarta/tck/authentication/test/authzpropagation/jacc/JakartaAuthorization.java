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
package ee.jakarta.tck.authentication.test.authzpropagation.jacc;

import static java.security.Policy.getPolicy;
import static java.util.logging.Level.SEVERE;

import java.security.CodeSource;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import jakarta.security.jacc.PolicyContext;
import jakarta.security.jacc.WebResourcePermission;

/**
 *
 * @author Arjan Tijms
 *
 */
public class JakartaAuthorization {

    private final static Logger logger = Logger.getLogger(JakartaAuthorization.class.getName());

    public static Subject getSubject() {
        try {
            return (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        } catch (Exception e) {
            logger.log(SEVERE, "", e);
        }

        return null;
    }

    public static boolean hasAccess(String uri, Subject subject) {
        return getPolicy().implies(
            new ProtectionDomain(
                new CodeSource(null, (Certificate[]) null),
                null, null,
                subject.getPrincipals().toArray(new Principal[subject.getPrincipals().size()])
            ),
            new WebResourcePermission(uri, "GET")
        );
    }
}
