/*
 * Copyright (c)  2014, 2020 Oracle and/or its affiliates. All rights reserved.
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

package ee.jakarta.tck.authentication.test.basic.servlet;

import static java.util.logging.Level.INFO;

import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.annotation.security.DeclareRoles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.HttpMethodConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@DeclareRoles({ "Administrator", "Manager", "Employee" })
@ServletSecurity(value = @HttpConstraint(rolesAllowed = { "Administrator" }), httpMethodConstraints = {
        @HttpMethodConstraint(value = "GET", rolesAllowed = "Administrator"),
        @HttpMethodConstraint(value = "POST", rolesAllowed = "Administrator") })
@WebServlet(name = "WrapperServlet", urlPatterns = { "/WrapperServlet" })
public class WrapperServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String providerConfigFileLocation;
    private String vendorACFClass;
    private String testMethod;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doTests(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doTests(request, response);
    }

    public void doTests(HttpServletRequest request, HttpServletResponse response) {
        debug("in WrapperServlet.doTests");

        TSLogger logger = TSLogger.getTSLogger();

        // get some common props
        getPropsAndParams(request, response);

        //
        // now send string out to logger so we can verify that the content
        // gets processed BEFORE secureResponse.
        debug("in WrapperServlet.doTests:  calling logger.log()");
        logger.log(INFO, "WrapperServlet.doTests() content processed for requestURI");

        if (testMethod.equals("testRequestWrapper")) {
            testRequestWrapper(request, response);
        } else if (testMethod.equals("testResponseWrapper")) {
            testResponseWrapper(request, response);
        }

        // Restore original (CTS) factory class
        // note: this should be done in many of the calls but its a safety measure
        // to ensure we are resetting things back to expected defaults
        try {
            CommonUtils.resetDefaultACF();
        } catch (Exception ex) {
            debug("ACFTestServlet:  error calling CommonUtils.resetDefaultACF(): " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void getPropsAndParams(HttpServletRequest req, HttpServletResponse response) {
        // set provider config file
        providerConfigFileLocation = System.getProperty("provider.configuration.file");
        debug("TS Provider ConfigFile = " + providerConfigFileLocation);
        if (providerConfigFileLocation == null) {
            debug("ERROR:  getPropsAndParams(): providerConfigFileLocation = null");
        } else {
            debug("getPropsAndParams(): providerConfigFileLocation = " + providerConfigFileLocation);
        }

        // set testMethod
        testMethod = req.getParameter("method.under.test");

        // set vendor class
        vendorACFClass = System.getProperty("vendor.authconfig.factory");
        if (vendorACFClass == null) {
            debug("ERROR:  getPropsAndParams(): vendorACFClass = null");
        } else {
            debug("getPropsAndParams(): vendorACFClass = " + vendorACFClass);
        }

        return;
    }

    public void testRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            out.println("WrapperServlet->testRequestWrapper()");
            out.println("isRequestWrapped = " + request.getAttribute("isRequestWrapped"));
            out.flush();
        } catch (Exception ex) {
            System.out.println("WrapperServlet->testRequestWrapper() failed");
            ex.printStackTrace();
        }
    }

    public void testResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            out.println("WrapperServlet->testResponseWrapper()");
            out.println("isResponseWrapped = " + response.getHeader("isResponseWrapped"));
            out.flush();
        } catch (Exception ex) {
            System.out.println("WrapperServlet->testResponseWrapper() failed");
            ex.printStackTrace();
        }
    }

    public void debug(String str) {
        System.out.println(str);
    }

}
