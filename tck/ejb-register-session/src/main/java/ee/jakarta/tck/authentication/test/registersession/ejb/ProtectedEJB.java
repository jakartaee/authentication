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
package ee.jakarta.tck.authentication.test.registersession.ejb;

import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;

/**
 * This is a "protected" EJB in the sense that there is role checking done prior to accessing (some) methods.
 * <p>
 * In JBoss EAP 6.1+ the use of any declarative security annotation switches the bean to a different mode, called "secured" in
 * JBoss terms.
 * <p>
 * GlassFish requires the <code>@DeclareRoles</code> annotation when programmatic role checking is done (making dynamic role
 * checking impossible).
 *
 * @author Arjan Tijms
 */
@Stateless
//Required by GlassFish
@DeclareRoles({ "architect" })
//JBoss EAP 6.1+ defaults unchecked methods to DenyAll
@PermitAll
public class ProtectedEJB {

    @Resource
    private EJBContext ejbContext;

    @RolesAllowed("architect")
    public String getUserName() {
        try {
            return ejbContext.getCallerPrincipal() != null ? ejbContext.getCallerPrincipal().getName() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUserArchitect() {
        try {
            return ejbContext.isCallerInRole("architect");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

}
