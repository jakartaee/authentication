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
package ee.jakarta.tck.authentication.test.registersession.servlet;

import java.io.IOException;
import java.security.Principal;

import ee.jakarta.tck.authentication.test.registersession.sam.MyPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 *
 * @author Arjan Tijms
 *
 */
@WebServlet(urlPatterns = "/protected/servlet")
public class ProtectedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.getWriter().write("This is a protected servlet \n");

        String webName = null;
        boolean isCustomPrincipal = false;
        if (request.getUserPrincipal() != null) {
            Principal principal = request.getUserPrincipal();
            isCustomPrincipal = principal instanceof MyPrincipal;
            webName = request.getUserPrincipal().getName();
        }

        boolean webHasRole = request.isUserInRole("architect");

        response.getWriter().write("isCustomPrincipal: " + isCustomPrincipal + "\n");
        response.getWriter().write("web username: " + webName + "\n");
        response.getWriter().write("web user has role \"architect\": " + webHasRole + "\n");

    }

}
