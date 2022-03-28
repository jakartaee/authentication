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
import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.logging.Logger;

import ee.jakarta.tck.authentication.test.registersession.ejb.ProtectedEJB;
import jakarta.ejb.EJB;
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
@WebServlet(urlPatterns = "/public/servlet-protected-ejb")
public class PublicServletProtectedEJB extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final static Logger logger = Logger.getLogger(PublicServletProtectedEJB.class.getName());

    @EJB
    private ProtectedEJB protectedEJB;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String webName = null;
        if (request.getUserPrincipal() != null) {
            webName = request.getUserPrincipal().getName();
        }

        String ejbName = "";
        try {
            ejbName = protectedEJB.getUserName();
        } catch (Exception e) {
            logger.log(SEVERE, "", e);
        }

        response.getWriter().write("web username: " + webName + "\n" + "EJB username: " + ejbName + "\n");

        boolean webHasRole = request.isUserInRole("architect");

        boolean ejbHasRole = false;
        try {
            ejbHasRole = protectedEJB.isUserArchitect();
        } catch (Exception e) {
            logger.log(SEVERE, "", e);
        }

        response.getWriter().write(
            "web user has role \"architect\": " + webHasRole + "\n" + "EJB user has role \"architect\": " + ejbHasRole
                + "\n");

    }

}
