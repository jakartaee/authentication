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
package ee.jakarta.tck.authentication.test.ejbasyncauthentication.bean;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

import java.io.IOException;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.servlet.AsyncContext;

/**
 *
 * @author Arjan Tijms
 *
 */
@Stateless
public class AsyncBean {

    @Asynchronous
    public void doAsync(AsyncContext asyncContext) {

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            interrupted();
        }

        try {
            asyncContext.getResponse().getWriter().write("async response");
        } catch (IOException e) {
            e.printStackTrace();
        }

        asyncContext.complete();
    }

}
