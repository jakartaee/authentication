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
package ee.jakarta.tck.authentication.test.programmaticauthentication.servlet;

import java.io.IOException;

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
@WebServlet(urlPatterns = "/public/authenticate")
public class AuthenticateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.getWriter().write("This is a public servlet \n");

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        response.getWriter().write("before web username: " + webName + "\n");
        boolean webHasRole = request.isUserInRole("architect");
        response.getWriter().write("before web user has role \"architect\": " + webHasRole + "\n");

        // By *not* setting the "doLogin" request attribute the request.authenticate call
        // would do nothing. request.authenticate is a mandatory authentication, so doing
        // nothing is not allowed. But one or more initial failures should not prevent
        // a later successful authentication.
        if (request.getParameter("failFirst") != null) {
            try {
                request.authenticate(response);
            } catch (IOException | ServletException e) {
                // GlassFish returns false when either authentication is in progress or authentication
                // failed (or was not done at all), but JBoss throws an exception.
                // TODO: Get clarification what is actually expected, likely exception is most correct.
                //       Then test for the correct return value.
            }
        }

        if ("2".equals(request.getParameter("failFirst"))) {
            try {
                request.authenticate(response);
            } catch (IOException | ServletException e) {
            }
        }

        // Programmatically trigger the authentication chain
        request.setAttribute("doLogin", true);
        boolean authenticateOutcome = request.authenticate(response);

        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        response.getWriter().write("request.authenticate outcome: " + authenticateOutcome + "\n");

        response.getWriter().write("after web username: " + webName + "\n");
        webHasRole = request.isUserInRole("architect");
        response.getWriter().write("after web user has role \"architect\": " + webHasRole + "\n");

    }

}
