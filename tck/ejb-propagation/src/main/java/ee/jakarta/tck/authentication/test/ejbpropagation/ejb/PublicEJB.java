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
package ee.jakarta.tck.authentication.test.ejbpropagation.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;

/**
 * This is a "public" EJB in the sense that all its methods should be accessible and there is no declarative role checking prior
 * to accessing a method.
 *
 * @author Arjan Tijms
 *
 */
@Stateless
public class PublicEJB {

    @Resource
    private EJBContext ejbContext;

    public String getUserName() {
        try {
            return ejbContext.getCallerPrincipal() != null ? ejbContext.getCallerPrincipal().getName() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
