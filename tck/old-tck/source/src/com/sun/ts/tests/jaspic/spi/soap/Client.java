/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.jaspic.spi.soap;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import com.sun.javatest.Status;
import com.sun.ts.lib.harness.EETest;
import com.sun.ts.lib.porting.TSURL;
import com.sun.ts.lib.util.TestUtil;
import com.sun.ts.tests.jaspic.spi.common.CommonTests;
import com.sun.ts.tests.jaspic.tssv.config.TSAuthConfigFactory;
import com.sun.ts.tests.jaspic.tssv.config.TSAuthConfigFactoryForStandalone;
import com.sun.ts.tests.jaspic.tssv.config.TSRegistrationListener;
import com.sun.ts.tests.jaspic.tssv.util.IdUtil;
import com.sun.ts.tests.jaspic.tssv.util.JASPICData;
import com.sun.ts.tests.jaspic.tssv.util.ProviderConfigurationEntry;
import com.sun.ts.tests.jaspic.tssv.util.ProviderConfigurationXMLFileProcessor;
import com.sun.ts.tests.jaspic.util.LogFileProcessor;
import com.sun.ts.tests.jaspic.util.WebServiceUtils;

import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.RegistrationListener;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceRef;

/**
 *
 * @author Raja Perumal
 */
public class Client extends EETest {
    @WebServiceRef(name = "service/HelloService")
    static HelloService service;

    private Hello port;

    private Properties props = null;

    private static final String UserNameProp = "user";

    private static final String UserPasswordProp = "password";

    private String username = "";

    private String password = "";

    private TSURL ctsurl = new TSURL();

    private String hostname = "localhost";

    private String logicalHostName = "server";

    private String PROTOCOL = "http";

    private String urlString = null;

    private int portnum = 8000;

    private String platformMode = null;

    private transient LogFileProcessor logProcessor = null;

    private transient CommonTests commonTests;

    // server side application context identifier
    private String expectedAppContextId = null;

    // ServiceName and PortName mapping configuration going java-to-wsdl
    private static final String SERVICE_NAME = "HelloService";

    private static final String PORT_NAME = "HelloPort";

    private static final String NAMESPACEURI = "http://soap.spi.jaspic.tests.ts.sun.com/";

    private QName SERVICE_QNAME = new QName(NAMESPACEURI, SERVICE_NAME);

    private QName PORT_QNAME = new QName(NAMESPACEURI, PORT_NAME);

    private transient ProviderConfigurationXMLFileProcessor configFileProcessor = null;

    private Collection providerConfigurationEntriesCollection = null;

    

    private String servletAppContext = null;

    private String logFileLocation;

    private String providerConfigFileLocation;

    private String vendorACFClass;

    public static void main(String[] args) {
        Client theTests = new Client();
        Status s = theTests.run(args, System.out, System.err);
        s.exit();
    }

    /*
     * @class.setup_props: log.file.location; provider.configuration.file; vendor.authconfig.factory; webServerHost;
     * webServerPort; platform.mode; user; password; logical.hostname.soap;
     */
    public void setup(String[] args, Properties p) {
        props = p;
        try {
            username = props.getProperty(UserNameProp);
            password = props.getProperty(UserPasswordProp);
            hostname = props.getProperty("webServerHost");
            platformMode = props.getProperty("platform.mode");
            logicalHostName = props.getProperty("logical.hostname.soap");
            portnum = Integer.parseInt(props.getProperty("webServerPort"));
            urlString = ctsurl.getURLString(PROTOCOL, hostname, portnum, "/Hello_web/Hello");

            // Get app-context-id from ProviderConfiguration.xml file
            expectedAppContextId = IdUtil.getAppContextId("soap");

            logFileLocation = props.getProperty("log.file.location");
            logger.info("Log file location = " + logFileLocation);

            providerConfigFileLocation = props.getProperty("provider.configuration.file");
            logger.info("TestSuite Provider ConfigFile = " + providerConfigFileLocation);

            vendorACFClass = props.getProperty("vendor.authconfig.factory");
            logger.info("Vendor AuthConfigFactory class = " + vendorACFClass);

            // If there is no matching app-context-id then try with default id
            if (expectedAppContextId.equals("")) {
                expectedAppContextId = logicalHostName + " " + "/Hello_web/Hello";
            }

            sayHelloProtected();

            // create LogProcessor
            logProcessor = new LogFileProcessor(props);

            // retrieve logs based on application Name
            logProcessor.fetchLogs("pullAllLogRecords|fullLog");

            // this is class that holds generic tests in it
            commonTests = new CommonTests();

        } catch (Exception e) {
            throw new Fault("Setup failed:", e);
        }

        logger.info("setup ok");
    }

    /*
     * sayHelloProtected
     *
     * This method is invoked first by all tests, as a result of this client and server soap runtime generates corresponding
     * messages in the log and the other tests verify those messages.
     */
    private void sayHelloProtected() {

        try {
            Hello port = null;

            if (platformMode.equals("jakartaEE")) {
                port = (Hello) getJavaEEPort();
            } else {
                port = (Hello) getStandAlonePort();
            }

            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> map = bindingProvider.getRequestContext();

            logger.info("Setting the target endpoint address on WS port: " + urlString);
            map.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlString);

            logger.info("Invoking sayHelloProtected on Hello port");
            String text = port.sayHelloProtected("Raja");
            logger.info("Got Output : " + text);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Fault("Test sayHelloProtected failed");
        }

    }

    public Object getJavaEEPort() throws Exception {
        logger.info("Get Hello Port from HelloService");
        Object port = service.getPort(Hello.class);
        return port;
    }

    public Object getStandAlonePort() throws Exception {
        URL wsdlurl = new URL(urlString + "?WSDL");
        return WebServiceUtils.getPort(wsdlurl, SERVICE_QNAME, HelloService.class, PORT_QNAME, Hello.class);
    }

    public void cleanup() {
        logMsg("cleanup ok");
    }
   

}
