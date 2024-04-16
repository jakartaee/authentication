/*
 * Copyright (c) 2022, 2024 Contributors to the Eclipse Foundation.
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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

package ee.jakarta.tck.authentication.test.common;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static org.apache.http.HttpStatus.SC_MULTIPLE_CHOICES;
import static org.apache.http.HttpStatus.SC_OK;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.jsoup.Jsoup.parse;
import static org.jsoup.parser.Parser.xmlParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import org.htmlunit.DefaultCredentialsProvider;
import org.htmlunit.DefaultCssErrorHandler;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.api.Secured;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class ArquillianBase {

    private static final Logger logger = Logger.getLogger(ArquillianBase.class.getName());
    private static final String WEBAPP_SRC = "src/main/webapp";

    private WebClient webClient;
    private String response;
    private boolean useBaseSecured;
    private String responsePath;

    @ArquillianResource
    private URL base;

    @Secured(port = 8181)
    @ArquillianResource
    private URL baseSecured;

    @Rule
    public TestWatcher ruleExample = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            logger.log(INFO, "Running " + description.getMethodName());
        };

        @Override
        protected void failed(Throwable e, Description description) {
            super.failed(e, description);

            logger.log(SEVERE,
                "\n\nTest failed: " +
                description.getClassName() + "." + description.getMethodName() +

                "\nMessage: " + e.getMessage() +

                "\nLast response: " +

                "\n\n"  + formatHTML(response) + "\n\n");

        }
    };

    public static WebArchive defaultArchive() {
        return defaultWebArchive("test.war");
    }

    public static WebArchive defaultWebArchive() {
        return defaultWebArchive("test.war");
    }

    public static WebArchive defaultWebArchive(String name) {
        return
            removeTestClasses(
                create(WebArchive.class, name + ".war")
                    .addPackages(true, "ee.jakarta.tck.authentication.test")
                    .addAsWebInfResource(resource("web.xml"))
                    );
    }

    private static WebArchive removeTestClasses(WebArchive archive) {
        for (Map.Entry<ArchivePath, Node> content : archive.getContent().entrySet()) {
            if (content.getKey().get().endsWith("Test.class")) {
                archive.delete(content.getKey().get());
            }
        }
        archive.deleteClass(ArquillianBase.class);

        return archive;
    }

    public static File resource(String name) {
        return new File(WEBAPP_SRC + "/WEB-INF", name);
    }

    public static File web(String name) {
        return new File(WEBAPP_SRC, name);
    }

    @Before
    public void setUp() {
        Logger logger = Logger.getLogger(DefaultCssErrorHandler.class.getName());
        logger.setLevel(SEVERE);

        useBaseSecured = false;
        response = null;
        webClient = new WebClient() {

            private static final long serialVersionUID = 1L;

            @Override
            public void printContentIfNecessary(WebResponse webResponse) {
                int statusCode = webResponse.getStatusCode();
                if (getOptions().isPrintContentOnFailingStatusCode() && !(statusCode >= SC_OK && statusCode < SC_MULTIPLE_CHOICES)) {
                    logger.log(SEVERE, webResponse.getWebRequest().getUrl().toExternalForm());
                }
                super.printContentIfNecessary(webResponse);
            }
        };
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        if (System.getProperty("glassfish.suspend") != null) {
            webClient.getOptions().setTimeout(0);
        }
        webClient.getOptions().setUseInsecureSSL(true);
    }

    @After
    public void tearDown() {
        webClient.getCookieManager().clearCookies();
        webClient.close();
    }

    protected String readFromServerWithCredentials(String path, String username, String password) {
        DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        credentialsProvider.addCredentials(username, password.toCharArray());

        getWebClient().setCredentialsProvider(credentialsProvider);

        return readFromServer(path);
    }


    protected String getFromServerPath(String path) {
        return readFromServer(path);
    }

    protected String readFromServer(String path) {
        response = "";
        WebResponse localResponse = responseFromServer(path);
        if (localResponse != null) {
            response = localResponse.getContentAsString();
        }

        return response;
    }

    protected WebResponse responseFromServerWithCredentials(String path, String username, String password) {
        DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        credentialsProvider.addCredentials(username, password.toCharArray());

        getWebClient().setCredentialsProvider(credentialsProvider);

        return responseFromServer(path);
    }

    protected WebResponse responseFromServer(String path) {

        WebResponse webResponse = null;

        Page page = pageFromServer(path);
        if (page != null) {
            webResponse = page.getWebResponse();
            if (webResponse != null) {
                response = webResponse.getContentAsString();
            }
        }

        return webResponse;
    }

    protected <P extends Page> P pageFromServer(String path) {
        URL currentBase = base;
        if (useBaseSecured) {
            currentBase = baseSecured;
        }

        if (currentBase.toString().endsWith("/") && path.startsWith("/")) {
            path = path.substring(1);
        }

        try {
            response = "";

            P page = webClient.getPage(currentBase + path);

            if (page != null) {
                WebResponse localResponse = page.getWebResponse();
                responsePath = page.getUrl().toString();
                if (localResponse != null) {
                    response = localResponse.getContentAsString();

                    if (System.getProperty("tck.log.response") != null) {
                        printLastResponse();
                    }
                }
            }

            return page;

        } catch (FailingHttpStatusCodeException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void printLastResponse() {
        logger.info(
            "\n\n" +
            "Requested path:\n" + responsePath +
            "\n\n" +

            "Response :\n" + formatHTML(response) +
            "\n\n\n");
    }

    protected void printPage(Page page) {
        if (page != null) {
            WebResponse localResponse = page.getWebResponse();
            responsePath = page.getUrl().toString();
            if (localResponse != null) {
                response = localResponse.getContentAsString();
            }

            printLastResponse();
        }
    }

    protected WebClient getWebClient() {
        return webClient;
    }

    /**
     * @return the useBaseSecured
     */
    protected boolean isUseBaseSecured() {
        return useBaseSecured;
    }

    /**
     * @param useBaseSecured the useBaseSecured to set
     */
    protected void setUseBaseSecured(boolean useBaseSecured) {
        this.useBaseSecured = useBaseSecured;
    }

    /**
     * @return the base
     */
    protected URL getBase() {
        return base;
    }

    public static String formatHTML(String html) {
        try {
            return parse(html, "", xmlParser()).toString();
        } catch (Exception e) {
            return html;
        }
    }

}

