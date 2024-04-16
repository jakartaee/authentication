/*
 * Copyright (c) 2007, 2021 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaspic.spi.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.BASE64Encoder;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaspic.tssv.config.TSAuthConfigFactoryForStandalone;
import com.sun.ts.tests.jaspic.tssv.util.IdUtil;
import com.sun.ts.tests.jaspic.tssv.util.JASPICData;
import com.sun.ts.tests.jaspic.tssv.util.ProviderConfigurationEntry;
import com.sun.ts.tests.jaspic.tssv.util.ProviderConfigurationXMLFileProcessor;
import com.sun.ts.tests.jaspic.util.LogFileProcessor;

import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;

/**
 * This class will be used to perform simple servlet invocations. The servlet invocations should be used to test some
 * different security constraints so we can validate that proper AuthModule invocations are occurring. (The servlet
 * security constraints we set will affect which AuthModules will be tested.)
 *
 * This Client class should NOT be testing any of the JSR-196 API's within here. Instead, the actual beef of our testing
 * should be done within the actual factory or provider code which will be called/used from within the MPR. Calling
 * things like the AuthConfigFactory.getDefault() from within this client code is not a valid thing to do. We want a MPR
 * to make this invocations. So for out testing, we will have the bulk of our tests live in the ACF's or ACP's. When
 * those tests are autorun (maybe by having the respective constructors automatically kick them off), then they will
 * perform any server side logging.
 *
 * We will check for success or failure from within this file. So the actual testcases in this class will simply consist
 * of checking the server side log file for key strings which will indicate success or failure.
 *
 * Important: Exercise caution when modifying the search strings that are passed to the logProcessor.verifyLogContains()
 * method calls. These strings should be written to the log file by some of the code in the server side
 * tssv/config/*.java files.
 *
 */
public class Client extends EETest {
    private Properties props = null;

    private String hostname = null;

    private int portnum = 0;

    // this must be the decoded context path corresponding to the web module
    private String contextPath = "/" + JASPICData.SCP_CONTEXT_PATH;

    private String acfServletPath = contextPath + "/ACFTestServlet";

    private String wrapperServletPath = contextPath + "/WrapperServlet";

    private String servletPath = contextPath + "/ModTestServlet";

    private String openToAllServletPath = "/OpenToAllServlet";

    private String allAccessServletPath = contextPath + openToAllServletPath;

    private String staticPagePath = contextPath + "/client.html";

    private String noConstraintPath = contextPath + "/OptionalAuthen";

    private String username = "";

    private String password = "";

    private String appContextHostname;

    // appContext must be in the form of "hostname context-path"
    private String appContext;

    private LogFileProcessor logProcessor = null;

    private boolean initialized = false;

    private boolean bIs115Compatible = false;

    private String logFileLocation;

    private String vendorACF;

    private String providerConfigFileLoc;

    // some private static strings we need to verify ahead of time
    private String ACF_MSG_1 = null;

    private ProviderConfigurationXMLFileProcessor configFileProcessor = null;

    private Collection<ProviderConfigurationEntry> providerConfigurationEntriesCollection = null;

    private String servletAppContext = null;

    private String providerConfigurationFileLocation;

    private String vendorAuthConfigFactoryClass;

    public static void main(String args[]) {
        Client theTests = new Client();
        Status s = theTests.run(args, System.out, System.err);
        s.exit();
    }

    /**
     * @class.setup_props: log.file.location; provider.configuration.file; vendor.authconfig.factory;
     * logical.hostname.servlet; webServerHost; webServerPort; authuser; authpassword; user; password;
     * securedWebServicePort; servlet.is.jsr115.compatible;
     *
     */
    public void setup(String[] args, Properties p) {
        props = p;

        try {
            hostname = p.getProperty("webServerHost");
            portnum = Integer.parseInt(p.getProperty("webServerPort"));
            username = p.getProperty("user");
            password = p.getProperty("password");

            logFileLocation = p.getProperty("log.file.location");
            vendorACF = p.getProperty("vendor.authconfig.factory");
            providerConfigFileLoc = p.getProperty("provider.configuration.file");
            appContextHostname = p.getProperty("logical.hostname.servlet");
            servletAppContext = IdUtil.getAppContextId(JASPICData.LAYER_SERVLET);

            String str115Compat = p.getProperty("servlet.is.jsr115.compatible");
            if (str115Compat != null) {
                bIs115Compatible = Boolean.valueOf(str115Compat).booleanValue();
            } else {
                bIs115Compatible = false;
            }

            appContext = servletAppContext;
            ACF_MSG_1 = "TSAuthConfigFactory.getConfigProvider ";
            ACF_MSG_1 += "returned non-null provider for Layer : HttpServlet";
            ACF_MSG_1 += " and AppContext :" + appContext;

            TestUtil.logMsg("setup(): logFileLocation = " + logFileLocation);
            TestUtil.logMsg("setup(): providerConfigFileLoc = " + providerConfigFileLoc);
            TestUtil.logMsg("setup(): appContextHostname = " + appContextHostname);
            TestUtil.logMsg("setup(): contextPath = " + contextPath);
            TestUtil.logMsg("setup(): appContext = " + appContext);
            TestUtil.logMsg("setup(): servletAppContext = " + servletAppContext);

            // we know what the logFileLocation is, but we can't always be sure it
            // as initialized as a system property so explicitly set it here.
            System.setProperty("log.file.location", logFileLocation);
            System.setProperty("provider.configuration.file", providerConfigFileLoc);
            System.setProperty("logical.hostname.servlet", appContextHostname);

            providerConfigurationFileLocation = props.getProperty("provider.configuration.file");
            TestUtil.logMsg("TestSuite Provider ConfigFile = " + providerConfigurationFileLocation);

            vendorAuthConfigFactoryClass = props.getProperty("vendor.authconfig.factory");
            TestUtil.logMsg("Vendor AuthConfigFactory class = " + vendorAuthConfigFactoryClass);

            if (!initialized) {
                // create LogFileProcessor
                logProcessor = new LogFileProcessor(props);

                // LogProcessor can take only the path without the file name - it will ad to it the default log file
                // the init logger in Servlet or provider does the same so let's check more carefuly
                String strFilePathAndName = "";
                if (logFileLocation.indexOf(com.sun.ts.tests.jaspic.tssv.util.JASPICData.DEFAULT_LOG_FILE) <= 0) {
                    strFilePathAndName = logFileLocation + "/" + com.sun.ts.tests.jaspic.tssv.util.JASPICData.DEFAULT_LOG_FILE;
                }

                // if no TSSVLog.txt file exists, then we want to do something that
                // cause one to be generated.
                File logfile = new File(strFilePathAndName);
                if (!logfile.exists()) {
                    TestUtil.logMsg("setup(): no TSSVLog.txt so access url to indirectly create one");
                    String theContext = contextPath + "/" + JASPICData.AUTHSTAT_MAND_SUCCESS;
                    int statusCode = invokeServlet(theContext, "POST");
                    TestUtil.logMsg("setup(): invokeServlet() returned statusCode =" + statusCode);
                }

                // retrieve logs based on application Name
                logProcessor.fetchLogs("pullAllLogRecords|fullLog");

                initialized = true;
            }

        } catch (Exception e) {
            logErr("Error: got exception: ", e);
        }
    }

  }

    /*
     * helper method to help us verify the various AuthStatus return vals.
     *
     */
    private void checkAuthStatus(String sContext, String statusType, String requestMethod, boolean isDispatchingToSvc) {

        // add some servlet params onto our context
        sContext = sContext + "?" + "log.file.location=" + logFileLocation;
        sContext = sContext + "&" + "provider.configuration.file=" + providerConfigFileLoc;
        sContext = sContext + "&" + "vendor.authconfig.factory=" + vendorACF;

        TestUtil.logMsg("sContext = " + sContext);
        TestUtil.logMsg("passing to servlet:  log.file.location = " + logFileLocation);
        TestUtil.logMsg("passing to servlet:  provider.configuration.file = " + providerConfigFileLoc);
        TestUtil.logMsg("passing to servlet:  vendor.authconfig.factory = " + vendorACF);

        TSURL ctsurl = new TSURL();
        String url = ctsurl.getURLString("http", hostname, portnum, sContext);
        String msg;

        try {
            URL newURL = new URL(url);

            String authData = username + ":" + password;
            TestUtil.logMsg("authData : " + authData);

            BASE64Encoder encoder = new BASE64Encoder();
            String encodedAuthData = encoder.encode(authData.getBytes());
            TestUtil.logMsg("encoded authData : " + encodedAuthData);

            // open URLConnection, set request props and actually connect
            HttpURLConnection conn = (HttpURLConnection) newURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuthData.trim());
            conn.setRequestMethod(requestMethod); // POST or GET etc
            conn.connect();

            TestUtil.logMsg("called HttpURLConnection.connect() for url: " + url);
            int code = conn.getResponseCode();
            TestUtil.logMsg("Got response code of: " + code);
            String responseMsg = conn.getResponseMessage();
            TestUtil.logMsg("Got response string of: " + responseMsg);

            if (responseMsg != null) {
                if (statusType.equals("SEND_FAILURE")) {
                    // Spec section 3.8.3.2
                    // for SEND_FAILURE, *if* there is a response sent back by the
                    // runtime, then the http status code must = 500
                    if (code != 500) {
                        msg = "SEND_FAILURE returned Response with invalid HTTP status code";
                        msg += " returned status code was " + code;
                        throw new Fault(msg + " : FAILED");
                    } else {
                        TestUtil.logMsg("Got correct return status for SEND_FAILURE");
                    }
                } else if (statusType.equals("AuthException")) {
                    if ((code != 403) && (code != 404) && (code != 500)) {
                        msg = "AuthException returned Response with invalid HTTP status code";
                        msg += " returned status code was " + code;
                        throw new Fault(msg + " : FAILED");
                    } else {
                        TestUtil.logMsg("Got correct return status for AuthException");
                    }
                }
            }

            // this section does checks for spec section 3.8.3.1
            if (isDispatchingToSvc) {
                // this section does checks for spec section 3.8.3.1
                if ((statusType.equals("SEND_CONTINUE")) && (responseMsg != null)) {
                    // spec section 3.8.3.1
                    // NOTE: our test must return SEND_CONTINUE befor calling the
                    // service invocation in order to expect these status codes
                    // If response, http status code must be: 401, 303, or 307
                    if ((code != 401) || (code != 303) || (code != 307)) {
                        msg = "SEND_CONTINUE returned Response with invalid HTTP status code";
                        msg += " returned status code was " + code;
                        throw new Fault(msg + " : FAILED");
                    } else {
                        TestUtil.logMsg("Got correct return status for SEND_CONTINUE");
                    }
                }
            }

            InputStream content = (InputStream) conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            try {
                String line;
                while ((line = in.readLine()) != null) {
                    TestUtil.logMsg(line);
                }
            } finally {
                in.close();
            }

        } catch (Exception e) {
            TestUtil.logMsg("Abnormal return status encountered while invoking " + sContext);
            TestUtil.logMsg("Exception Message was:  " + e.getMessage());
            // e.printStackTrace();
        }

        // lets force an update of our log file
        logProcessor.fetchLogs("pullAllLogRecords|fullLog");

        TestUtil.logMsg("Leaving checkAuthStatus()");
    }

    /*
     * Convenience method that will establish a url connections and perform a get/post request. A username and password will
     * be passed in the request header and they will be encoded using the BASE64Encoder class.
     */
    private int invokeServlet(String sContext, String requestMethod) {
        return invokeServlet(sContext, requestMethod, null);
    }

    private int invokeServlet(String sContext, String requestMethod, String testMethod) {
        int code = 200;

        TSURL ctsurl = new TSURL();
        if (!sContext.startsWith("/")) {
            sContext = "/" + sContext;
        }

        // lets set some other request params to be passed into servlet calls
        sContext = sContext + "?" + "log.file.location=" + logFileLocation;
        sContext = sContext + "&" + "provider.configuration.file=" + providerConfigFileLoc;
        sContext = sContext + "&" + "vendor.authconfig.factory=" + vendorACF;

        TestUtil.logMsg("passing to servlet:  log.file.location = " + logFileLocation);
        TestUtil.logMsg("passing to servlet:  provider.configuration.file = " + providerConfigFileLoc);
        TestUtil.logMsg("passing to servlet:  vendor.authconfig.factory = " + vendorACF);

        if (testMethod != null) {
            sContext = sContext + "&" + "method.under.test=" + testMethod;
            TestUtil.logMsg("passing to servlet:  testMethod = " + testMethod);
        }

        TestUtil.logMsg("sContext = " + sContext);

        String url = ctsurl.getURLString("http", hostname, portnum, sContext);
        try {
            URL newURL = new URL(url);

            // Encode authData
            // hint: make sure username and password are valid for your
            // (J2EE) security realm otherwise you recieve http 401 error.
            String authData = username + ":" + password;
            TestUtil.logMsg("authData : " + authData);

            BASE64Encoder encoder = new BASE64Encoder();

            String encodedAuthData = encoder.encode(authData.getBytes());
            TestUtil.logMsg("encoded authData : " + encodedAuthData);

            // open URLConnection
            HttpURLConnection conn = (HttpURLConnection) newURL.openConnection();

            // set request property
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuthData.trim());
            conn.setRequestMethod(requestMethod); // POST or GET etc
            conn.connect();

            TestUtil.logMsg("called HttpURLConnection.connect() for url: " + url);
            code = conn.getResponseCode();
            TestUtil.logMsg("Got response code of: " + code);
            String str = conn.getResponseMessage();
            TestUtil.logMsg("Got response string of: " + str);

            InputStream content = (InputStream) conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));

            try {
                String line;
                while ((line = in.readLine()) != null) {
                    TestUtil.logMsg(line);
                }
            } finally {
                in.close();
            }

        } catch (Exception e) {
            TestUtil.logMsg("Abnormal return status encountered while invoking " + sContext);
            TestUtil.logMsg("Exception Message was:  " + e.getMessage());
            // e.printStackTrace();
        }

        // lets force an update of our log file
        logProcessor.fetchLogs("pullAllLogRecords|fullLog");

        return code;
    } // invokeServlet()

    /*
     * This is a convenience method used to post a url to a servlet so that our servlet can do some tests and send back
     * status about success or failure. This passes some params onto the request/context so that the servlet will have info
     * it needs in order to properly perform its serverside ACF and ACP tests.
     *
     */
    private String invokeServletAndGetResponse(String sContext, String requestMethod) {
        return invokeServletAndGetResponse(sContext, requestMethod, null);
    }

    private String invokeServletAndGetResponse(String sContext, String requestMethod, String testMethod) {

        TSURL ctsurl = new TSURL();
        if (!sContext.startsWith("/")) {
            sContext = "/" + sContext;
        }

        // add some servlet params onto our context
        sContext = sContext + "?" + "log.file.location=" + logFileLocation;
        sContext = sContext + "&" + "provider.configuration.file=" + providerConfigFileLoc;
        sContext = sContext + "&" + "vendor.authconfig.factory=" + vendorACF;
        if (testMethod != null) {
            sContext = sContext + "&" + "method.under.test=" + testMethod;
        }

        TestUtil.logMsg("sContext = " + sContext);
        TestUtil.logMsg("passing to servlet:  log.file.location = " + logFileLocation);
        TestUtil.logMsg("passing to servlet:  provider.configuration.file = " + providerConfigFileLoc);
        TestUtil.logMsg("passing to servlet:  vendor.authconfig.factory = " + vendorACF);
        TestUtil.logMsg("passing to servlet:  testMethod = " + testMethod);

        String url = ctsurl.getURLString("http", hostname, portnum, sContext);
        String retVal = null;

        try {
            URL newURL = new URL(url);

            // Encode authData
            // hint: make sure username and password are valid for your
            // (J2EE) security realm otherwise you recieve http 401 error.
            String authData = username + ":" + password;
            TestUtil.logMsg("authData : " + authData);

            BASE64Encoder encoder = new BASE64Encoder();

            String encodedAuthData = encoder.encode(authData.getBytes());
            TestUtil.logMsg("encoded authData : " + encodedAuthData);

            // open URLConnection
            HttpURLConnection conn = (HttpURLConnection) newURL.openConnection();

            // set request property
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", "Basic " + encodedAuthData.trim());
            conn.setRequestMethod(requestMethod); // POST or GET etc
            conn.connect();

            TestUtil.logMsg("called HttpURLConnection.connect() for url: " + url);
            retVal = conn.getResponseMessage();
            TestUtil.logMsg("Got response string of: " + retVal);

            InputStream content = (InputStream) conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    retVal = retVal + line;
                    TestUtil.logMsg(line);
                }
            } finally {
                in.close();
            }
        } catch (Exception e) {
            TestUtil.logMsg("Abnormal return status encountered while invoking " + sContext);
            TestUtil.logMsg("Exception Message was:  " + e.getMessage());
        }

        // lets force an update of our log file
        logProcessor.fetchLogs("pullAllLogRecords|fullLog");

        return retVal;
    } // invokeServletAndGetResponse()




}
