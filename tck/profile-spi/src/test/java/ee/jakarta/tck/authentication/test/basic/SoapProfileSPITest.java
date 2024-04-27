package ee.jakarta.tck.authentication.test.basic;

import static ee.jakarta.tck.authentication.test.basic.servlet.JASPICData.TSSV_ACF;
import static jakarta.security.auth.message.config.AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY;
import static org.junit.Assert.assertTrue;

import com.sun.xml.ws.api.client.ClientPipelineHook;
import ee.jakarta.tck.authentication.test.basic.servlet.AuthFactoryContainerInitializer;
import ee.jakarta.tck.authentication.test.basic.servlet.JASPICData;
import ee.jakarta.tck.authentication.test.basic.soap.HelloService;
import ee.jakarta.tck.authentication.test.common.ArquillianBase;
import ee.jakarta.tck.authentication.test.common.logging.client.LogFileProcessor;
import ee.jakarta.tck.authentication.test.common.logging.client.TestUtil;
import jakarta.servlet.ServletContainerInitializer;
import java.net.URL;
import java.security.Security;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.xml.namespace.QName;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * This tests a large number of SPI assertions from the Servlet profile.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SoapProfileSPITest extends ArquillianBase {

    private static boolean setUpIsDone = false;

    String logicalHostName = "localhost";
    String expectedAppContextId = "localhost /Hello_web/Hello";

    // this must be the decoded context path corresponding to the web module
    private String contextPath = "/" + JASPICData.SCP_CONTEXT_PATH;
    private String openToAllServletPath = "OpenToAllServlet";

    private String appContext = System.getProperty("logical.hostname.servlet") + " " + contextPath;

    LogFileProcessor logProcessor = new LogFileProcessor();
    LogFileProcessor clientLogProcessor = new LogFileProcessor(true);

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        WebArchive archive = defaultWebArchive("Hello_web");
        archive.addAsServiceProvider(ServletContainerInitializer.class, AuthFactoryContainerInitializer.class);
        archive.addAsWebInfResource(resource("soap-web.xml"), "web.xml");

        System.out.println(archive.toString(true));

        return archive;
    }


    @Before
    public void runOnce() throws Exception {
        if (setUpIsDone) {
            return;
        }

        Security.setProperty(DEFAULT_FACTORY_SECURITY_PROPERTY, TSSV_ACF);
        Optional<ClientPipelineHook> optionalHook = ServiceLoader.load(ClientPipelineHook.class).findFirst();
        if (optionalHook.isPresent()) {
            int a;
            a = 4;
        }

        HelloService helloService = new HelloService(
                new URL(getBase(), "Hello?wsdl"),
                new QName("http://soap.basic.test.authentication.tck.jakarta.ee/", "HelloService"));

        String text = helloService.getHelloPort().sayHelloProtected("Raja");
        TestUtil.logMsg("Got Output : " + text);

        logProcessor.fetchLogs();

        setUpIsDone = true;
    }

    @Before
    public void fetchLogs() {
        logProcessor.fetchLogs();
        clientLogProcessor.fetchLogs();
    }


    // ### Client side tests

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ValidateResponse
     *
     * @assertion_ids: JASPIC:SPEC:42
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify Whether ClientAuthContext.validateResponse() is called
     *
     * Description The runtime must invoke ClientAuthContext.validateResponse()
     *
     */
    @Test
    public void ValidateResponse() {
        // verify whether the log contains required messages.
        assertTrue(
            "validateResponse failed : " + "ClientAuthContext.validateResponse not called",
            clientLogProcessor.verifyLogContains("TSClientAuthContext.validateResponse called"));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ClientAuthContext
     *
     * @assertion_ids: JASPIC:SPEC:34; JASPIC:JAVADOC:72; JASPIC:JAVADOC:97; JASPIC:JAVADOC:98
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify Whether ClientAuthConfig.getAuthContext() is called
     *
     * Description The runtime must invoke clientAuthConfig().getAuthContext() to obtain the ClientAuthContext.
     *
     */
    @Test
    public void ClientAuthContext() {
        String args[] = { "service/HelloService", getBase() + "Hello" };

        // Verify whether the log contains required messages.
        assertTrue(
            "ClientAuthContext failed : " + "clientAuthConfig.getAuthContext not called",
            clientLogProcessor.verifyLogContainsOneOfSubString(args, "TSClientAuthConfig.getAuthContext:  layer=SOAP : appContext="));
    }

    /**
     * @testName: NameAndPasswordCallbackSupport
     *
     * @assertion_ids: JASPIC:SPEC:123; JASPIC:JAVADOC:103
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify Whether CallbackHandler for client runtime supports
     * NameCallback and PasswordCallback
     *
     * Description Unless the client runtime is embedded in a server runtime (e.g.; an invocation of a web service by a
     * servlet running in a Servlet container), The CallbackHandler passed to ClientAuthModule.initialize must support the
     * following callbacks: NameCallback, PasswordCallback
     *
     */
    @Test
    public void NameAndPasswordCallbackSupport() {
        // verify whether the log contains required messages.
        assertTrue(
            clientLogProcessor.verifyLogContains(
                "In SOAP : ClientRuntime CallbackHandler supports NameCallback",
                "In SOAP : ClientRuntime CallbackHandler supports PasswordCallback"));
    }

    /**
     * @testName: ClientRuntimeCommonCallbackSupport
     *
     * @assertion_ids: JASPIC:SPEC:114; JASPIC:JAVADOC:35; JASPIC:JAVADOC:36; JASPIC:JAVADOC:49; JASPIC:JAVADOC:51;
     * JASPIC:JAVADOC:54; JASPIC:JAVADOC:63; JASPIC:JAVADOC:65; JASPIC:JAVADOC:68; JASPIC:JAVADOC:69; JASPIC:JAVADOC:71;
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify Whether CallbackHandler for client runtime supports
     * CertStoreCallback, PrivateKeyCallback, SecretKeyCallback, TrustStoreCallback
     *
     * Description
     *
     * The CallbackHandler passed to the initialize method of an authentication module should support the following
     * callbacks, and it must be possible to configure the runtime such that the CallbackHandler passed at module
     * initialization module supports the following callbacks (in addition to any others required to be supported by the
     * applicable internal profile): CertStoreCallback, PrivateKeyCallback, SecretKeyCallback, TrustStoreCallback
     *
     *
     */
    @Test
    public void ClientRuntimeCommonCallbackSupport() {
        // verify whether the log contains required messages.
        assertTrue(
            clientLogProcessor.verifyLogContains(
                "In SOAP : ClientRuntime CallbackHandler supports CertStoreCallback",
                "In SOAP : ClientRuntime CallbackHandler supports PrivateKeyCallback",
                "In SOAP : ClientRuntime CallbackHandler supports SecretKeyCallback",
                "In SOAP : ClientRuntime CallbackHandler supports TrustStoreCallback"));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACPClientAuthConfig
     *
     * @assertion_ids: JASPIC:SPEC:124; JASPIC:JAVADOC:77
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify Whether the arguments(layer and appcontextId) passed to
     * obtain AuthConfigProvider is same as the arguments used in calling getClientAuthConfig.
     *
     * Description
     *
     * If a non-null AuthConfigProvider is returned (by the call to getConfigProvider), the messaging runtime must call
     * getClientAuthConfig on the provider to obtain the authentication context configuration object pertaining to the
     * application context at the layer. The layer and appContext arguments of the call to getClientAuthConfig must be the
     * same as those used to acquire the provider.
     *
     *
     */
    @Test
    public void ACPClientAuthConfig() {
        String args[] = { "service/HelloService", getBase() + "Hello" };

        // Verify whether the log contains required messages.
        assertTrue(
            clientLogProcessor.verifyLogContainsOneOfSubString(args, "TSAuthConfigFactory.getConfigProvider returned non-null provider for" + " Layer : SOAP and AppContext :"));

        // ACPClientAuthConfig : Same layer and appContextId used

        // Verify whether the log contains required messages.
        assertTrue(
            clientLogProcessor.verifyLogContainsOneOfSubString(args, "TSAuthConfigProvider.getClientAuthConfig called for " + "layer=SOAP : appContext="));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: CACRequestResponse
     *
     * @assertion_ids: JASPIC:SPEC:130; JASPIC:JAVADOC:7; JASPIC:JAVADOC:9; JASPIC:SPEC:19; JASPIC:SPEC:23; JASPIC:SPEC:129;
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify whether for a non-null ClientAuthContext, secureRequest
     * and validateResponse are called properly.
     *
     * Description
     *
     * If the client runtime obtained a non-null ClientAuthContext by using the operation identifier corresponding to the
     * request message, then at point (1) in the message processing model, the runtime must call secureRequest on the
     * ClientAuthContext, and at point (4) the runtime must call validateResponse on the ClientAuthContext.
     *
     *
     */
    @Test
    public void CACRequestResponse() {
        // Verify whether the log contains required messages.
        assertTrue(clientLogProcessor.verifyLogContains(
            "TSClientAuthConfig.getAuthContext: returned non-null" + " ClientAuthContext for operationId=sayHelloProtected",
            "TSClientAuthContext.secureRequest called",
            "TSClientAuthContext.validateResponse called"));
    }




    /**
     * @keywords: jaspic_soap
     *
     * @testName: ClientRuntimeMessageInfoMap
     *
     * @assertion_ids: JASPIC:SPEC:133; JASPIC:JAVADOC:7
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify whether the Map in messageInfo object passed to
     * secureRequest and validateResponse contains the right value for key jakarta.xml.ws.wsdl.service
     *
     * Description This profile requires that the message processing runtime establish the following key-value pairs within
     * the Map of the MessageInfo passed in the calls to secureRequest and validateResponse Key=jakarta.xml.ws.wsdl.service
     * Value= the value of the qualified service name, represented as a javax.xml.namespace.QName
     *
     */
    @Test
    public void ClientRuntimeMessageInfoMap() {
        QName expectedQName = new QName("http://soap.basic.test.authentication.tck.jakarta.ee/", "HelloService");

        // Verify whether the log contains required messages.
        assertTrue(
            clientLogProcessor.verifyLogContains(
                "TSClientAuthModule.secureRequest messageInfo :" + "jakarta.xml.ws.wsdl.service=" + expectedQName.toString(),
                "TSClientAuthModule.validateResponse messageInfo :" + "jakarta.xml.ws.wsdl.service=" + expectedQName.toString()));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ClientAppContextId
     *
     * @assertion_ids: JASPIC:SPEC:208
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify whether for the client side appilcation context
     * Identifier is correctly used by the runtime.
     *
     * Description A Client application context Identifier must be the String value composed by concatenating the client
     * scope identifier, a blank separator character, and the client reference to the service. The clien scope identifier is
     * not testable but we can check the client reference to the service.
     *
     */
    @Test
    public void ClientAppContextId() {
        String args[] = { "service/HelloService", getBase() + "Hello" };

        // Verify whether the log contains required messages.
        assertTrue(
            clientLogProcessor.verifyLogContainsOneOfSubString(args,
                "TSAuthConfigProvider.getClientAuthConfig called for " + "layer=SOAP : appContext="));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ClientAuthConfig
     *
     * @assertion_ids: JASPIC:SPEC:11; JASPIC:SPEC:12 ; JASPIC:SPEC:16;
     *                 JASPIC:JAVADOC:92; JASPIC:JAVADOC:93; JASPIC:SPEC:110;
     *                 JASPIC:SPEC:111
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether AuthConfigProvider.ClientAuthConfig is
     *                 called in the server.
     *
     *                 Description The runtime must invoke
     *                 AuthConfigProvider.getClientAuthConfig() to obtain the
     *                 AuthConfig. The runtime must also specify
     *                 appropriate(non-null) layer(i.e for this test case "SOAP"
     *                 layer) and application context identifiers in its call to
     *                 getClientAuthConfig.
     *
     */
    @Test
    public void ClientAuthConfig() {
      String args[] = { "service/HelloService", getBase() + "Hello" };

      // verify whether the log contains required messages.
      assertTrue(
          "ClientAuthConfig failed : " + "AuthConfigProvider.getClientAuthConfig not called",
          clientLogProcessor.verifyLogContainsOneOfSubString(args,
              "TSAuthConfigProvider.getClientAuthConfig called for layer=SOAP : appContext="));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: SecureRequest
     *
     * @assertion_ids: JASPIC:SPEC:35; JASPIC:JAVADOC:5; JASPIC:SPEC:36;
     *                 JASPIC:JAVADOC:75
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether ClientAuthContext.secureRequest() is called
     *
     *                 Description The runtime must invoke
     *                 ClientAuthContext.secureRequest()
     *
     */
    @Test
    public void SecureRequest() {
      // Verify whether the log contains required messages.
      assertTrue(
          "SecureRequest failed : " + "ClientAuthContext.secureRequest not called",
          clientLogProcessor.verifyLogContains("TSClientAuthContext.secureRequest called"));
    }



    // ### Server side ###

    /**
     * @keywords: jaspic_soap
     *
     * @testName: GetConfigProvider
     *
     * @assertion_ids: JASPIC:SPEC:8; JASPIC:SPEC:14; JASPIC:SPEC:116; JASPIC:SPEC:117; JASPIC:JAVADOC:77;
     * JASPIC:JAVADOC:79; JASPIC:JAVADOC:80; JASPIC:JAVADOC:84; JASPIC:JAVADOC:85; JASPIC:JAVADOC:91; JASPIC:SPEC:110;
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
     *
     * 2. Use FetchLog servlet to read the server side log to verify Whether AuthConfigFactory.getConfigProvider is called
     * in the server.
     *
     * Description The runtime must invoke AuthConfigFactory.getConfigProvider to obtain the AuthConfigProvider. The runtime
     * must also specify appropriate(non-null) layer and application context identifiers in its call to getConfigProvider.
     *
     */
    @Test
    public void GetConfigProvider() {
        assertTrue(
            "GetConfigProvider failed : " + "AuthConfigFactory.getConfigProvider not called",
            logProcessor.verifyLogContains(
                "TSAuthConfigFactory.getConfigProvider called",
                "getConfigProvider called for Layer : SOAP and AppContext :" + expectedAppContextId ));

    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: GetFactory
     *
     * @assertion_ids: JASPIC:SPEC:7; JASPIC:SPEC:10; JASPIC:JAVADOC:77;
     *                 JASPIC:JAVADOC:80; JASPIC:JAVADOC:84; JASPIC:JAVADOC:79;
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether AuthConfigFactory.getConfigProvider is
     *                 called in the server.
     *
     *                 Description The runtime must invoke
     *                 AuthConfigFactory.getConfigProvider to obtain the
     *                 AuthConfigProvider. By calling getConfigProvider, we can
     *                 assume getFactory() was called.
     *
     */
    @Test
    public void GetFactory() {
      // Verify whether the log contains required messages.
      assertTrue(
          "GetFactory failed : " + "AuthConfigFactory.getFactory not called",
          logProcessor.verifyLogContains("TSAuthConfigFactory.getFactory called Indirectly"));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ServerAuthConfig
     *
     * @assertion_ids: JASPIC:SPEC:11; JASPIC:SPEC:13 ; JASPIC:SPEC:16;
     *                 JASPIC:JAVADOC:95
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether provider.getServerAuthConfig is called in
     *                 the server.
     *
     *                 Description The runtime must invoke
     *                 AuthConfigProvider.getServerAuthConfig() to obtain the
     *                 AuthConfig. The runtime must also specify
     *                 appropriate(non-null) layer(i.e for this test case "SOAP"
     *                 layer) and application context identifiers in its call to
     *                 getServerAuthConfig.
     *
     */
    @Test
    public void ServerAuthConfig() {
      // Verify whether the log contains required messages.
      assertTrue(
          "ServerAuthConfig failed : " + "AuthConfigProvider.getServerAuthConfig not called",
          logProcessor.verifyLogContains(
              "TSAuthConfigProvider.getServerAuthConfig called for layer=SOAP" +
              " : appContext=" + expectedAppContextId));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ValidateRequest
     *
     * @assertion_ids: JASPIC:SPEC:50; JASPIC:JAVADOC:16; JASPIC:JAVADOC:17;
     *                 JASPIC:JAVADOC:23; JASPIC:SPEC:23; JASPIC:SPEC:19
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether ServerAuthContext.validateRequest() is
     *                 called
     *
     *                 Description The runtime must invoke
     *                 ServerAuthContext.validateRequest()
     *
     */
    @Test
    public void ValidateRequest() {
        // Verify whether the log contains required messages.
        assertTrue(
            "ValidateRequest failed : " + "ServerAuthContext.validateRequest not called",
            logProcessor.verifyLogContains("TSServerAuthContext.validateRequest called"));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: SecureResponse
     *
     * @assertion_ids: JASPIC:SPEC:59; JASPIC:JAVADOC:16; JASPIC:JAVADOC:17;
     *                 JASPIC:JAVADOC:23; JASPIC:JAVADOC:26
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether ServerAuthContext.secureResponse() is called
     *
     *                 Description The runtime must invoke
     *                 ServerAuthContext.secureResponse()
     *
     */
    @Test
    public void SecureResponse() {
        // verify whether the log contains required messages.
        assertTrue("SecureResponse failed : " + "ServerAuthContext.secureResponse not called",
                logProcessor.verifyLogContains("TSServerAuthContext.secureResponse called"));

    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ServerAuthContext
     *
     * @assertion_ids: JASPIC:SPEC:34; JASPIC:JAVADOC:100; JASPIC:SPEC:153;
     *                 JASPIC:SPEC:156; JASPIC:JAVADOC:101
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether ServerAuthConfig.getAuthContext() is called
     *
     *                 Description The runtime must invoke
     *                 serverAuthConfig().getAuthContext() to obtain the
     *                 ServerAuthContext.
     *
     */
    @Test
    public void ServerAuthContext() {
        // verify whether the log contains required messages.
        assertTrue(
            "TSServerAuthConfig.getAuthContext:  layer=SOAP : appContext=" + expectedAppContextId,
            logProcessor.verifyLogContains(
                "TSServerAuthConfig.getAuthContext:  layer=SOAP : appContext=" + expectedAppContextId));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: MessageInfo
     *
     * @assertion_ids: JASPIC:SPEC:35; JASPIC:SPEC:44; JASPIC:JAVADOC:5;
     *                 JASPIC:SPEC:112; JASPIC:JAVADOC:9; JASPIC:JAVADOC:10;
     *                 JASPIC:JAVADOC:11; JASPIC:JAVADOC:28; JASPIC:SPEC:23;
     *                 JASPIC:SPEC:19; JASPIC:SPEC:36; JASPIC:SPEC:37;
     *                 JASPIC:SPEC:43; JASPIC:SPEC:51; JASPIC:SPEC:113;
     *                 JASPIC:SPEC:131; JASPIC:SPEC:132;
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether the messageInfo passed to secureRequest()
     *                 validateRequest(), secureResponse() and validateResponse()
     *                 contiains right values for getRequestMessage() and
     *                 getResponseMessage() as per the spec.
     *
     *                 3. clientSubject - a Subject that represents the source of
     *                 the service request, or null.
     *
     *                 Description The MessageInfo argument used in any call made
     *                 by the message processing runtime to secureRequest,
     *                 validateResponse, validateRequest, or secureResponse must
     *                 have been initialized such that the non-null objects
     *                 returned by the getRequestMessage and getResponseMessage
     *                 methods of the MessageInfo are an instanceof
     *                 jakarta.xml.soap.SOAPMessage.
     *
     *
     */
    @Test
    public void MessageInfo() {
      // verify whether the log contains required messages.
      assertTrue(
          "MessageInfo failed : " + "The request and response messages contains incorrect values",
              logProcessor.verifyLogContains(
                      // "secureRequest : MessageInfo.getRequestMessage() is of type jakarta.xml.soap.SOAPMessage",
                  "validateRequest : MessageInfo.getRequestMessage() is of type jakarta.xml.soap.SOAPMessage",
                  "secureResponse : MessageInfo.getRequestMessage() is of type jakarta.xml.soap.SOAPMessage",
                  "secureResponse : MessageInfo.getResponseMessage() is of type jakarta.xml.soap.SOAPMessage"


              ));

      // "validateResponse : MessageInfo.getRequestMessage() is of type jakarta.xml.soap.SOAPMessage",
      //"validateResponse : MessageInfo.getResponseMessage() is of type jakarta.xml.soap.SOAPMessage"
    }

    /**
     * @testName: ServerRuntimeCommonCallbackSupport
     *
     * @assertion_ids: JASPIC:SPEC:114; JASPIC:JAVADOC:35; JASPIC:JAVADOC:36;
     *                 JASPIC:JAVADOC:49; JASPIC:JAVADOC:51; JASPIC:JAVADOC:54;
     *                 JASPIC:JAVADOC:63; JASPIC:JAVADOC:65; JASPIC:JAVADOC:68;
     *                 JASPIC:JAVADOC:69; JASPIC:JAVADOC:71; JASPIC:JAVADOC:106
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether CallbackHandler for server runtime supports
     *                 CertStoreCallback, PrivateKeyCallback, SecretKeyCallback,
     *                 TrustStoreCallback
     *
     *                 Description
     *
     *                 The CallbackHandler passed to the initialize method of an
     *                 authentication module should support the following
     *                 callbacks, and it must be possible to configure the runtime
     *                 such that the CallbackHandler passed at module
     *                 initialization module supports the following callbacks (in
     *                 addition to any others required to be supported by the
     *                 applicable internal profile): CertStoreCallback,
     *                 PrivateKeyCallback, SecretKeyCallback, TrustStoreCallback
     *
     *
     */
    @Test
    public void ServerRuntimeCommonCallbackSupport() {
      // Verify whether the log contains required messages.
      assertTrue(
          logProcessor.verifyLogContains(
              "In SOAP : ServerRuntime CallbackHandler supports CertStoreCallback",
              "In SOAP : ServerRuntime CallbackHandler supports PrivateKeyCallback",
              "In SOAP : ServerRuntime CallbackHandler supports SecretKeyCallback",
              "In SOAP : ServerRuntime CallbackHandler supports TrustStoreCallback"));
    }

    /**
     * @testName: ServerRuntimeCallbackSupport
     *
     * @assertion_ids: JASPIC:SPEC:114; JASPIC:SPEC:149; JASPIC:JAVADOC:38;
     *                 JASPIC:JAVADOC:39; JASPIC:JAVADOC:40; JASPIC:JAVADOC:42;
     *                 JASPIC:JAVADOC:43; JASPIC:JAVADOC:44; JASPIC:JAVADOC:46;
     *                 JASPIC:JAVADOC:30; JASPIC:JAVADOC:41; JASPIC:JAVADOC:45
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether CallbackHandler for server runtime supports
     *                 CallerPrincipalCallback, GroupPrincipalCallback and
     *                 PasswordValidationCallback
     *
     *                 Description
     *
     *                 The CallbackHandler passed to the
     *                 ServerAuthModule.initialize must support the following
     *                 callbacks,
     *
     *                 CallerPrincipalCallback, GroupPrincipalCallback,
     *                 PasswordValidationCallback
     *
     *
     */
    @Test
    public void ServerRuntimeCallbackSupport() {
      // Verify whether the log contains required messages.
      assertTrue(
          logProcessor.verifyLogContains(
              "In SOAP : ServerRuntime CallbackHandler supports CallerPrincipalCallback",
              "In SOAP : ServerRuntime CallbackHandler supports GroupPrincipalCallback",
              "In SOAP : ServerRuntime CallbackHandler supports PasswordValidationCallback"));

    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: OperationId
     *
     * @assertion_ids: JASPIC:SPEC:121; JASPIC:SPEC:125; JASPIC:JAVADOC:73
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether the operationId is "sayHelloProtected"
     *
     *                 Description
     *
     *                 If getOperation returns a non-null operation identifier,
     *                 then the operation identifier must be the String value
     *                 corresponding to the operation name appearing in the
     *                 service description (i.e., WSDL).
     *
     *                 When its getOperation method is called, any authentication
     *                 context configuration object obtained for the SOAP layer,
     *                 must attempt to derive the corresponding operation
     *                 identifier value from the request message (within
     *                 messageInfo).
     *
     *
     */
    @Test
    public void OperationId() {
        assertTrue(
            logProcessor.verifyLogContains(
                "getAuthContextID() called for layer=SOAP shows AuthContextId=" +
                "sayHelloProtected"));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACPAuthContext
     *
     * @assertion_ids: JASPIC:SPEC:125; JASPIC:SPEC:150; JASPIC:JAVADOC:5;
     *                 JASPIC:JAVADOC:28; JASPIC:JAVADOC:73; JASPIC:JAVADOC:79
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether the arguments(operation) passed to obtain
     *                 getAuthContext is same as defined in Section 4.7.1
     *
     *                 Description The authentication context identifier used in
     *                 the call to getAuthContext must be equivalent to the value
     *                 that would be acquired by calling getAuthContextID with the
     *                 MessageInfo that will be used in the corresponding call to
     *                 secureRequest (by a client runtime) or validateRequest (by
     *                 a server runtime).
     *
     */
    @Test
    public void ACPAuthContext() {
        assertTrue(
            logProcessor.verifyLogContains(
                "TSAuthConfigFactory.getConfigProvider returned non-null provider for" + " Layer : SOAP and AppContext :" + expectedAppContextId,
                "TSServerAuthConfig.getAuthContext:  layer=SOAP" + " : appContext=" + expectedAppContextId + " operationId=sayHelloProtected"));


//      String cArgs[] = { "service/HelloService",
//          "http://" + hostname + ":" + portnum + "/Hello_web/Hello" };
//
//      // verify whether the log contains required messages.
//      logProcessor.verifyLogContainsOneOfSubString(cArgs,
//              "TSClientAuthConfig.getAuthContext:  layer=SOAP"
//                      + " : appContext=");

    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACPServerAuthConfig
     *
     * @assertion_ids: JASPIC:SPEC:150; JASPIC:JAVADOC:79; JASPIC:JAVADOC:94
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify Whether the arguments(layer and appcontextId) passed
     *                 to obtain AuthConfigProvider is same as the arguments used
     *                 in calling getServerAuthConfig.
     *
     *                 Description
     *
     *                 If a non-null AuthConfigProvider is returned (by the call
     *                 to getConfigProvider), the messaging runtime must call
     *                 getServerAuthConfig on the provider to obtain the
     *                 authentication context configuration object pertaining to
     *                 the application context at the layer. The layer and
     *                 appContext arguments of the call to getServerAuthConfig
     *                 must be the same as those used to acquire the provider.
     *
     *
     */
    public void ACPServerAuthConfig() {
        // verify whether the log contains required messages.
        assertTrue(
            logProcessor.verifyLogContains(
                "TSAuthConfigFactory.getConfigProvider returned non-null provider for" + " Layer : SOAP and AppContext :" + expectedAppContextId,
                "TSAuthConfigProvider.getServerAuthConfig called for " + "layer=SOAP : appContext=" + expectedAppContextId ));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: SACRequestResponse
     *
     * @assertion_ids: JASPIC:SPEC:130; JASPIC:JAVADOC:13; JASPIC:JAVADOC:16;
     *                 JASPIC:JAVADOC:17; JASPIC:JAVADOC:23; JASPIC:JAVADOC:26;
     *                 JASPIC:JAVADOC:28; JASPIC:SPEC:155;
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify whether for a non-null ServerAuthContext,
     *                 validateRequest and secureResponse are called properly.
     *
     *                 Description
     *
     *                 If the server runtime obtained a non-null ServerAuthContext
     *                 by using the operation identifier corresponding to the
     *                 request message, then at point (2) in the message
     *                 processing model, the runtime must call validateRequest on
     *                 the ClientAuthContext, and at point (3) the runtime must
     *                 call secureResponse on the ServerAuthContext.
     *
     *
     */
    public void SACRequestResponse() {
      // verify whether the log contains required messages.
      assertTrue(
          logProcessor.verifyLogContains(
              "TSServerAuthConfig.getAuthContext: returned non-null" + " ServerAuthContext for operationId=sayHelloProtected",
              "TSServerAuthContext.validateRequest called",
              "TSServerAuthContext.secureResponse called" ));
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ServerAppContextId
     *
     * @assertion_ids: JASPIC:SPEC:209
     *
     * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
     *                 Registering TSSV with your AppServer ).
     *
     *                 2. Use FetchLog servlet to read the server side log to
     *                 verify whether for the server side appilcation context
     *                 Identifier is correctly used by the runtime.
     *
     *                 Description A server application context Identifier shall
     *                 be the String value composed by concatenating a logical
     *                 hostname a blank separator character, and the path
     *                 component of the service endpoint URI corresponding to the
     *                 webservice.
     */
    @Test
    public void ServerAppContextId() {
      String args[] = {
          logicalHostName + " /Hello_web/Hello" };

      // verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContainsOneOfSubString(args,
          "TSAuthConfigProvider.getServerAuthConfig called for " + "layer=SOAP : appContext="));
    }



}