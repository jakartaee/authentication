package ee.jakarta.tck.authentication.test.basic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ee.jakarta.tck.authentication.test.basic.servlet.AuthFactoryContainerInitializer;
import ee.jakarta.tck.authentication.test.basic.servlet.JASPICData;
import ee.jakarta.tck.authentication.test.common.ArquillianBase;
import ee.jakarta.tck.authentication.test.common.logging.client.LogFileProcessor;
import jakarta.servlet.ServletContainerInitializer;
import org.htmlunit.WebResponse;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This tests a large number of SPI assertions from the Servlet profile.
 *
 * @author Arjan Tijms
 *
 */
@RunWith(Arquillian.class)
public class ServletProfileSPITest extends ArquillianBase {

    // this must be the decoded context path corresponding to the web module
    private String contextPath = "/" + JASPICData.SCP_CONTEXT_PATH;
    private String servletPath = "ModTestServlet";
    private String acfServletPath = "ACFTestServlet";
    private String wrapperServletPath = "WrapperServlet";
    private String openToAllServletPath = "OpenToAllServlet";
    private String allAccessServletPath = openToAllServletPath;
    private String staticPagePath = "client.html";
    private String noConstraintPath = "OptionalAuthen";

    private String appContext = System.getProperty("logical.hostname.servlet") + " " + contextPath;
    private String ACF_MSG_1 = "TSAuthConfigFactory.getConfigProvider " + "returned non-null provider for Layer : HttpServlet" + " and AppContext :" + appContext;

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        WebArchive archive = defaultWebArchive("spitests_servlet_web");
        archive.addAsServiceProvider(ServletContainerInitializer.class, AuthFactoryContainerInitializer.class);

        System.out.println(archive.toString(true));

        return archive;
    }

    /**
    *
    * @keywords: jaspic_servlet
    *
    * @testName: CheckSecureRespForOptionalAuth
    *
    * @assertion_ids: JASPIC:SPEC:59; JASPIC:SPEC:304; JASPIC:JAVADOC:27;
    *                 JASPIC:JAVADOC:10; JASPIC:JAVADOC:11; JASPIC:JAVADOC:28;
    *                 JASPIC:JAVADOC:13;
    *
    * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
    *                 Registering TSSV with your AppServer ).
    *
    *                 2. read the server side log to
    *                 verify secureResponse was called.
    *
    *                 Description This method will make a URL post that has
    *                 optional security Authen which means that we should NOT
    *                 need to set any principals in the validateRequest methods
    *                 nor the callbacks. The Runtime should allow this to proceed
    *                 to call the secureResponse even though there is no
    *                 principal values explicitly being set. Note: we need to
    *                 specifically hardwire the particular context used in this
    *                 test to NOT have any principals set on the server side.
    *
    */
   @Test
   public void CheckSecureRespForOptionalAuth() {
     getFromServerPath("OptionalAuthen");

     LogFileProcessor logProcessor = new LogFileProcessor();
     logProcessor.fetchLogs();

     assertTrue(
         logProcessor.verifyLogContains(
             "HttpServlet profile with servletName=/" +
             "OptionalAuthen returning  AuthStatus=AuthStatus.SUCCESS"));

     assertTrue(
         logProcessor.verifyLogContains(
             "secureResponse called for layer=HttpServlet for requestURI=" +
             contextPath + "/OptionalAuthen"));
   }

   /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckSecureRespForMandatoryAuth
   *
   * @assertion_ids: JASPIC:SPEC:98; JASPIC:SPEC:304; JASPIC:JAVADOC:26;
   *                 JASPIC:JAVADOC:31; JASPIC:JAVADOC:10; JASPIC:JAVADOC:9;
   *                 JASPIC:SPEC:103;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. read the server side log to
   *                 verify secureResponse and CallerPrincipalCallback are
   *                 called.
   *
   *                 Description This method will make a URL post that has
   *                 Mandatory security Authen which means that we need to set a
   *                 principal objects in the validateRequest method
   *                 clientSubject object (or in a callback). Either way, once
   *                 we set a principal object in the clientSubject - The
   *                 Runtime should proceed to call the secureResponse and
   *                 should invoke the CallbackHandler passed to it by the
   *                 runtime to handle a CallerPrincipalCallback using the
   *                 clientSubject as argument to it. For this case, we want to
   *                 ensure the following occur: - AuthStatus returns SUCCESS
   *                 (we know this is occuriing otherwise we would not make it
   *                 into secureResponse) - isMandatory() == true -
   *                 secureResponse is called - CallerPrincipalCallback handler
   *                 gets invoked.
   *
   *                 NOTE: We need our server side to check and ensure there are
   *                 principal values being set for this particular context
   *                 posting.
   *
   */
  @Test
  public void CheckSecureRespForMandatoryAuth() {
    String strHDR = "HttpServlet profile with servletName=/";
    String theContext = contextPath + "/" + JASPICData.AUTHSTAT_MAND_SUCCESS;

    String strMsg1 = strHDR + JASPICData.AUTHSTAT_MAND_SUCCESS
                     + " returning  AuthStatus=AuthStatus.SUCCESS";
    String strMsg2 = "secureResponse called for layer=HttpServlet for requestURI="
                     + theContext;
    String strMsg3 = "In HttpServlet : ServerRuntime CallerPrincipalCallback";
    strMsg3 += " called for profile=HttpServlet for servletPath=" + theContext;

    getFromServerPath(JASPICData.AUTHSTAT_MAND_SUCCESS);

    LogFileProcessor logProcessor = new LogFileProcessor();
    logProcessor.fetchLogs();

    assertTrue(
            logProcessor.verifyLogContains(
                    strMsg1, strMsg2, strMsg3));
  }

   /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: testSecRespCalledAfterSvcInvoc
   *
   * @assertion_ids: JASPIC:SPEC:108;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. read the server side log to
   *                 verify secureResponse and CallerPrincipalCallback are
   *                 called.
   *
   *                 Description This method will make a URL post that has
   *                 Mandatory security Authen and it will verify that the
   *                 secureResponse() method has NOT been called before the
   *                 servlet/service invocation by checking for the existance of
   *                 a cts proprietary request property that would only get set
   *                 from in secureResponse. So if the property is found, we
   *                 know a call to secureResponse was incorrectly made BEFORE
   *                 the servlet/service invocation but if the request property
   *                 is NOT found, then we can assume that secureResponse was
   *                 NOT called before the servlet/service invocation.
   *
   */
   @Test
   public void testSecRespCalledAfterSvcInvoc() {
       String response = readFromServerWithCredentials(
           JASPICData.AUTHSTAT_MAND_SUCCESS + "?" + "method.under.test=" + "testSecRespCalledAfterSvcInvoc", "j2ee", "j2ee");

       assertTrue(response.contains("testSecRespCalledAfterSvcInvoc() passed"));
   }

   /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckNoAuthReturnsValidStatusCode
   *
   * @assertion_ids: JASPIC:SPEC:93
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. Make servlet invocations and check the return status to
   *                 verify proper server side handling.
   *
   *                 Description This method will make a request that is not
   *                 authorized and then verify that the status code returned is
   *                 not indicating the request was OK==200. If we get back a
   *                 statuscode = 200 then there was a problem since we were
   *                 trying to access a page that had mandatory authentication
   *                 set AND we were trying to use an invalid user/pwd to
   *                 perform that access - thus we should NOT get an okay status
   *                 code returned since our bad username/pwd will not have
   *                 perms.
   *
   */
   @Test
   public void CheckNoAuthReturnsValidStatusCode() {
       // InvokeServlet will attempt to access the resource that does
       // not have the proper role creds assigned to it
       // thus we expect a return status code != 200
       WebResponse response = responseFromServerWithCredentials("AnotherMandatoryAuthen", "j2ee", "j2ee");

       // If status of 200 was returned then there is a problem since
       // we should have been forbidden from completing our request
       // as our username/pwd change means we should not be authenticated
       assertTrue(response.getStatusCode() != 200);
   }

   /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: VerifyRequestDispatchedProperly
   *
   * @assertion_ids: JASPIC:SPEC:317; JASPIC:JAVADOC:27; JASPIC:JAVADOC:28;
   *
   *
   * @test_Strategy: 1. issue request that has mandatory auth and verify it gets
   *                 authorized.
   *
   *                 2. issue a request that is mandatory but NOT
   *                 authorized (eg submit request with invalid user/pwd) and
   *                 verify that secureResponse is still called by MPR.
   *
   *                 Description The request must be dispatched to the resource
   *                 if the request was determined to be authorized; otherwise
   *                 it must NOT be dispatched and the runtime must proceed to
   *                 point (3) in the message processing model.
   *
   */
   @Test
   public void VerifyRequestDispatchedProperly() {
       // We should see that our request is properly dispatched
       // we will assume that a proper response code indicates success
       // as we are using correct creds
       WebResponse response = responseFromServerWithCredentials(
           JASPICData.AUTHSTAT_MAND_SUCCESS +
           "?method.under.test=VerifyRequestDispatchedProperly", "j2ee", "j2ee");

       assertTrue(response.getStatusCode() == 200);

       // InvokeServlet will attempt to access the resource that
       // does not have proper role creds assigned
       // thus we expect a return status code != 200
       response = responseFromServerWithCredentials("AnotherMandatoryAuthen", "j2ee", "j2ee");

       assertTrue(response.getStatusCode() != 200);

       LogFileProcessor logProcessor = new LogFileProcessor();
       logProcessor.fetchLogs();

       // Now verify that the MPR went into the secureResponse
       assertTrue(
           logProcessor.verifyLogContains(
               "secureResponse called for layer=HttpServlet for requestURI=" +
               (getBase().getPath() + JASPICData.AUTHSTAT_MAND_SUCCESS)));
  }

   /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckValidateReqAuthException
   *
   * @assertion_ids: JASPIC:SPEC:58; JASPIC:SPEC:320; JASPIC:SPEC:94;
   *                 JASPIC:JAVADOC:1; JASPIC:JAVADOC:28;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. read the server side log to
   *                 verify secureResponse was not called for the given context
   *                 and that an AuthException was called.
   *
   *                 Description This method will make a URL post using a
   *                 context that should cause an AuthException to get thrown
   *                 from the validateRequest. This should cause the
   *                 secureResponse to NOT get called.
   *
   */
  @Test
  public void CheckValidateReqAuthException() {
    WebResponse response = responseFromServerWithCredentials(
            JASPICData.AUTHSTAT_THROW_EX_ND, "j2ee", "j2ee");

    assertTrue(
        response.getStatusCode() == 403 ||
        response.getStatusCode() == 404 ||
        response.getStatusCode() == 500);

    LogFileProcessor logProcessor = new LogFileProcessor();
    logProcessor.fetchLogs();

    // Verify whether the log contains required messages.
    assertTrue(
        logProcessor.verifyLogContains(
            "HttpServlet profile with servletName=/" +
            JASPICData.AUTHSTAT_THROW_EX_ND + " returning  AuthStatus=AuthException"));

    // Also verify that no call to secureResponse was made
    assertFalse(
        logProcessor.verifyLogContains(
            "secureResponse called for layer=HttpServlet for requestURI=" +
            contextPath + "/" + JASPICData.AUTHSTAT_THROW_EX_ND));
  }

  /**
  *
  * @keywords: jaspic_servlet
  *
  * @testName: CheckValidateReqAlwaysCalled
  *
  * @assertion_ids: JASPIC:SPEC:58; JASPIC:SPEC:320; JASPIC:SPEC:89;
  *                 JASPIC:SPEC:94; JASPIC:JAVADOC:1; JASPIC:JAVADOC:28;
  *
  * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
  *                 Registering TSSV with your AppServer ).
  *
  *                 2. read the server side log to
  *                 verify secureResponse was not called for the given context
  *                 and that an AuthException was called.
  *
  *                 Description This method will make mulitple posts using
  *                 contexts w/ authN that is both required and non-required.
  *                 We want to confirm that validateRequest is called
  *                 independent of whether authN is required (i.e. including
  *                 when isMandatory() would be false). Among other things,
  *                 This is validating JASPIC spec section 3.8 which states
  *                 "validateRequest must be called for all requests".
  *
  */
  @Test
  public void CheckValidateReqAlwaysCalled() {
      responseFromServerWithCredentials(
          "ModTestServlet" + "?method.under.test=CheckValidateReqAlwaysCalled", "j2ee", "j2ee");

      responseFromServerWithCredentials(
          "OptionalAuthen" + "?method.under.test=CheckValidateReqAlwaysCalled", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(
          "validateRequest() called for " + contextPath + "/ModTestServlet" + ", isMandatory() = true"));

      assertTrue(logProcessor.verifyLogContains(
          "validateRequest() called for " + contextPath + "/OptionalAuthen" + ", isMandatory() = false"));
  }

  /**
  *
  * @keywords: jaspic_servlet
  *
  * @testName: AuthConfigFactoryGetFactory
  *
  * @assertion_ids: JASPIC:SPEC:7; JASPIC:SPEC:10; JASPIC:JAVADOC:84;
  *                 JASPIC:JAVADOC:94; JASPIC:JAVADOC:79; JASPIC:JAVADOC:97;
  *                 JASPIC:JAVADOC:77; JASPIC:JAVADOC:80; JASPIC:JAVADOC:84;
  *                 JASPIC:JAVADOC:85;
  *
  * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
  *                 Registering TSSV with your AppServer ).
  *
  *                 2. read the server side log to
  *                 verify Whether AuthConfigFactory.getFactory is called and
  *                 instantiated in the server.
  *
  *                 Description The AuthConfigFactory.getFactory method must be
  *                 used during the container bootstrap processs.
  *
  */
  @Test
  public void AuthConfigFactoryGetFactory() {
      responseFromServerWithCredentials(
          "ModTestServlet" + "?method.under.test=AuthConfigFactoryGetFactory", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(
          "TSAuthConfigFactory.getFactory called Indirectly"));
  }

  /**
  *
  * @keywords: jaspic_servlet
  *
  * @testName: GetConfigProvider
  *
  * @assertion_ids: JASPIC:SPEC:8; JASPIC:SPEC:14; JASPIC:JAVADOC:79;
  *                 JASPIC:JAVADOC:84; JASPIC:JAVADOC:85; JASPIC:JAVADOC:91
  *
  * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
  *                 Registering TSSV with your AppServer ).
  *
  *                 2. read the server side log to
  *                 verify Whether AuthConfigFactory.getConfigProvider is
  *                 called in the server.
  *
  *                 Description The runtime must invoke
  *                 AuthConfigFactory.getConfigProvider to obtain the
  *                 AuthConfigProvider.
  *
  */
  @Test
  public void GetConfigProvider() {
      responseFromServerWithCredentials(
          "ModTestServlet" + "?method.under.test=GetConfigProvider", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(
          "TSAuthConfigFactory.getConfigProvider called"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckAuthContextId
   *
   * @assertion_ids: JASPIC:SPEC:80
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. read the server side log to
   *                 verify that the authentication context identifier used in
   *                 the call to getAuthContext is equivalent to the value that
   *                 is acquired in the call to getAuthContextID with the
   *                 MessageInfo that is be used in the call to validateRequest.
   *
   *                 Description The authentication context identifier used in
   *                 the call to getAuthContext must be equivalent to the value
   *                 that would be acquired by calling getAuthContextID with the
   *                 MessageInfo that will be used in the call to
   *                 validateRequest.
   *
   */
  @Test
  public void CheckAuthContextId() {
      responseFromServerWithCredentials(
          "ModTestServlet" + "?method.under.test=CheckAuthContextId", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(
          "getAuthContextID() called for layer=HttpServlet shows"+ " AuthContextId=/ModTestServlet GET",

          "TSServerAuthConfig.getAuthContext:  layer=HttpServlet" +
          " : appContext=" + System.getProperty("logical.hostname.servlet") + " " + contextPath +
          " operationId=/ModTestServlet GET"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: AuthConfigFactoryRegistration
   *
   * @assertion_ids: JASPIC:SPEC:80;
   *
   * @test_Strategy: this was originally in register directory but was moved
   *                 here and the code pulled out and centralized into
   *                 ACFTestServlet.
   *
   */
  @Test
  public void AuthConfigFactoryRegistration() {
    String response = readFromServerWithCredentials(
            acfServletPath + "?method.under.test=AuthConfigFactoryRegistration", "j2ee", "j2ee");

    assertTrue(response.contains("ACFTestServlet->AuthConfigFactoryRegistration() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFInMemoryNotifyOnUnReg
   *
   * @assertion_ids: JASPIC:SPEC:336; JASPIC:SPEC:337; JASPIC:SPEC:338;
   *                 JASPIC:SPEC:339; JASPIC:SPEC:342; JASPIC:SPEC:344;
   *
   * @test_Strategy: this was originally in register directory but was moved
   *                 here and the code pulled out and centralized into
   *                 ACFTestServlet.
   *
   */
  @Test
  public void ACFInMemoryNotifyOnUnReg() {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFInMemoryNotifyOnUnReg", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFInMemoryNotifyOnUnReg() passed"));

  }


  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFPersistentNotifyOnUnReg
   *
   * @assertion_ids: JASPIC:SPEC:336; JASPIC:SPEC:337; JASPIC:SPEC:338;
   *                 JASPIC:SPEC:339; JASPIC:SPEC:341; JASPIC:SPEC:344;
   *
   * @test_Strategy: this was originally in register directory but was moved
   *                 here and the code pulled out and centralized into
   *                 ACFTestServlet.
   *
   */
  @Test
  public void ACFPersistentNotifyOnUnReg()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFPersistentNotifyOnUnReg", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFPersistentNotifyOnUnReg() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFInMemoryPrecedenceRules
   *
   * @assertion_ids: JASPIC:SPEC:328; JASPIC:SPEC:337; JASPIC:SPEC:338;
   *                 JASPIC:SPEC:338; JASPIC:SPEC:339; JASPIC:SPEC:344;
   *
   * @test_Strategy: this was originally in register directory but was moved
   *                 here and the code pulled out and centralized into
   *                 ACFTestServlet.
   *
   */
  @Test
  public void ACFInMemoryPrecedenceRules()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFInMemoryPrecedenceRules", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFInMemoryPrecedenceRules() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFPersistentPrecedenceRules
   *
   * @assertion_ids: JASPIC:SPEC:328; JASPIC:SPEC:337; JASPIC:SPEC:338;
   *                 JASPIC:SPEC:338; JASPIC:SPEC:339; JASPIC:SPEC:344;
   *
   * @test_Strategy: this was originally in register directory but was moved
   *                 here and the code pulled out and centralized into
   *                 ACFTestServlet.
   *
   */
  @Test
  public void ACFPersistentPrecedenceRules()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFPersistentPrecedenceRules", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFPersistentPrecedenceRules() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFGetRegistrationContext
   *
   * @assertion_ids: JASPIC:JAVADOC:81;
   *
   * @test_Strategy: This is testing that acf.getRegistrationContext(string)
   *                 returns NULL for an unrecognized string. (this requirement
   *                 described in javadoc for api)
   */
  @Test
  public void ACFGetRegistrationContext()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFGetRegistrationContext", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFGetRegistrationContext() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFGetRegistrationIDs
   *
   * @assertion_ids: JASPIC:JAVADOC:82;
   *
   * @test_Strategy: This is testing that acf.getRegistrationIDs(acp) NEVER
   *                 returns null hint: this must return empty array even if
   *                 unrecognized acp. (this requirement described in javadoc
   *                 for api)
   *
   */
  @Test
  public void ACFGetRegistrationIDs()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFGetRegistrationIDs", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFGetRegistrationIDs() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFRemoveRegistration
   *
   * @assertion_ids: JASPIC:JAVADOC:86;
   *
   * @test_Strategy: This is testing that acf.removeRegistration(arg) will
   *                 return FALSE when invalid arg supplied. (this requirement
   *                 described in javadoc for api)
   *
   */
  @Test
  public void ACFRemoveRegistration()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFRemoveRegistration", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFRemoveRegistration() passed"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFDetachListener
   *
   * @assertion_ids: JASPIC:JAVADOC:78;
   *
   * @test_Strategy: This is testing that acf.detachListener(...) will return
   *                 non-null when unregistered listener supplied. (this
   *                 requirement described in javadoc for api)
   *
   */
  @Test
  public void ACFDetachListener()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFDetachListener", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFDetachListener() passed"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFGetFactory
   *
   * @assertion_ids: JASPIC:SPEC:329; JASPIC:SPEC:335; JASPIC:SPEC:7;
   *
   * @test_Strategy: This s mainly concerned with testing the runtimes handling
   *                 of ACF as follows: - get current (CTS) ACF - switch to use
   *                 different (CTS) ACF - verify calls to ACF use the
   *                 newer/expected ACF - restore original ACF
   */
  @Test
  public void ACFGetFactory()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFGetFactory", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFGetFactory() passed"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testACFComesFromSecFile
   *
   * @assertion_ids: JASPIC:SPEC:329; JASPIC:SPEC:330;
   *
   * @test_Strategy: This is calling a method on the server(actually servlet)
   *                 side that will invoke getFactory() to verify a non-null
   *                 facotry instance is returned. It will also verify that the
   *                 authconfigprovider.factory security property is properly
   *                 set/used.
   *
   */
  @Test
  public void testACFComesFromSecFile()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=testACFComesFromSecFile", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->testACFComesFromSecFile() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFPersistentRegisterOnlyOneACP
   *
   * @assertion_ids: JASPIC:SPEC:329; JASPIC:SPEC:331; JASPIC:SPEC:332;
   *                 JASPIC:SPEC:340; JASPIC:SPEC:341;
   *
   * @test_Strategy: This will make a server (actually servlet) side method call
   *                 that will do the following: - load vendors ACF -
   *                 (persistent) register of CTS ACP's in the vendors ACF - get
   *                 list of vendors registered provider ID's - register another
   *                 persistent ACP (this is standalone ACP profile) - verify
   *                 another regId was added for standalone ACP - try to
   *                 re-register (persistently) standalone provider - verify 2nd
   *                 attempt at added standalone provider REPLACED the first but
   *                 it should NOT have added another nor failed. - also confirm
   *                 that regID for standalone ACP stayed the same after 1st and
   *                 2nd attempt to register standalone ACP - verify that the
   *                 2nd re-registering of ACP replaced the ACP AND the
   *                 description. - unregister standalone ACP and setFactory
   *                 back to CTS default
   *
   */
  @Test
  public void ACFPersistentRegisterOnlyOneACP()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFPersistentRegisterOnlyOneACP", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFPersistentRegisterOnlyOneACP() passed"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFInMemoryRegisterOnlyOneACP
   *
   * @assertion_ids: JASPIC:SPEC:334; JASPIC:SPEC:342; JASPIC:SPEC:343;
   *
   * @test_Strategy: This will make a server (actually servlet) side method call
   *                 that will do the following: - load vendors ACF -
   *                 (persistent) register of CTS ACP's in the vendors ACF - get
   *                 list of vendors registered provider ID's - register
   *                 (in-memory) ACP (this is standalone ACP profile) - verify
   *                 another regId was added for standalone ACP - try to
   *                 re-register (in-memory) standalone provider - verify 2nd
   *                 attempt at added standalone provider REPLACED the first but
   *                 it should NOT have added another nor failed. - also confirm
   *                 that regID for standalone ACP stayed the same after 1st and
   *                 2nd attempt to register standalone ACP - verify that the
   *                 2nd re-registering of ACP replaced the ACP AND the
   *                 description. - unregister standalone ACP and setFactory
   *                 back to CTS default
   *
   */
  @Test
  public void ACFInMemoryRegisterOnlyOneACP()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFInMemoryRegisterOnlyOneACP", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFInMemoryRegisterOnlyOneACP() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: ACFUnregisterACP
   *
   * @assertion_ids: JASPIC:SPEC:344;
   *
   * @test_Strategy: This will make a server (actually servlet) side method call
   *                 that will do the following: - load vendors ACF -
   *                 (persistent) register of CTS ACP's in the vendors ACF - get
   *                 list of vendors registered provider ID's - register
   *                 (in-memory) ACP (this is standalone ACP profile) - verify
   *                 another regId was added for standalone ACP - unregister the
   *                 in-memory ACP we just registered - verify
   *                 removeRegistration() method call returned proper boolean -
   *                 verify expected # of registry eentries - verify 2nd call to
   *                 removeRegistration() with regId that was previously removed
   *                 and should expect return val of false
   *
   */
  @Test
  public void ACFUnregisterACP()  {
      String response = readFromServerWithCredentials(
              acfServletPath + "?method.under.test=ACFUnregisterACP", "j2ee", "j2ee");

      assertTrue(response.contains("ACFTestServlet->ACFUnregisterACP() passed"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckACFVerifyPersistence
   *
   * @assertion_ids: JASPIC:SPEC:75; JASPIC:SPEC:335;
   *
   * @test_Strategy: This tests the (persistent) registration of CTS ACP's
   *                 within the the vendors ACF.
   *
   */
  @Test
  public void CheckACFVerifyPersistence()  {
      String response = readFromServerWithCredentials(
              acfServletPath, "j2ee", "j2ee");

      assertTrue(response.contains("AuthConfigFactoryVerifyPersistence() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckRegistrationContextId
   *
   * @assertion_ids: JASPIC:SPEC:77;
   *
   * @test_Strategy: This registers CTS ACP's within vendors ACF and then
   *                 verifies the RegistrationContext info can be obtained from
   *                 the vendors ACF.
   *
   */
  @Test
  public void CheckRegistrationContextId()  {
      String response = readFromServerWithCredentials(
              acfServletPath, "j2ee", "j2ee");

      assertTrue(response.contains("getRegistrationContextId() TSProviders registration passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testRequestWrapper
   *
   * @assertion_ids: JASPIC:SPEC:108; JASPIC:SPEC:311;
   *
   * @test_Strategy:
   *
   *                 From JASPIC Spec section 3.8.3.5: When a ServerAuthModule
   *                 returns a wrapped message in MessageInfo, or unwraps a
   *                 message in MessageInfo, the message processing runtime must
   *                 ensure that the HttpServletRequest and HttpServletResponse
   *                 objects established by the ServerAuthModule are used in
   *                 downstream processing.
   *
   *                 This test uses a Server Authentication Module(SAM) that
   *                 wraps and unwraps the HttpRequest as specified by the
   *                 JASPIC spec and expects the runtime to handle the Wrapped
   *                 HttpRequest.
   *
   */
  @Test
  public void testRequestWrapper() {
      String response = readFromServerWithCredentials(
              wrapperServletPath + "?method.under.test=testRequestWrapper", "j2ee", "j2ee");

      assertTrue(response.contains("isRequestWrapped = true"));
      assertFalse(response.contains("Incorrect request type"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testResponseWrapper
   *
   * @assertion_ids: JASPIC:SPEC:108; JASPIC:SPEC:311;
   *
   * @test_Strategy: From JASPIC Spec section 3.8.3.5: When a ServerAuthModule
   *                 returns a wrapped message in MessageInfo, or unwraps a
   *                 message in MessageInfo, the message processing runtime must
   *                 ensure that the HttpServletRequest and HttpServletResponse
   *                 objects established by the ServerAuthModule are used in
   *                 downstream processing.
   *
   *                 This test uses a Server Authentication Module(SAM) that
   *                 wraps and unwraps the HttpResponse as specified by the
   *                 JASPIC spec and expects the runtime to handle the Wrapped
   *                 HttpResponse.
   */
  @Test
  public void testResponseWrapper() {
      String response = readFromServerWithCredentials(
              wrapperServletPath + "?method.under.test=testResponseWrapper", "j2ee", "j2ee");

      assertTrue(response.contains("isResponseWrapped = true"));
      assertFalse(response.contains("Incorrect request type"));

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckCallbackSupport
   *
   * @assertion_ids: JASPIC:SPEC:72; JASPIC:SPEC:103; JASPIC:JAVADOC:27; JASPIC:JAVADOC:40; JASPIC:JAVADOC:46;
   * JASPIC:JAVADOC:31; JASPIC:JAVADOC:32; JASPIC:JAVADOC:33; JASPIC:JAVADOC:34; JASPIC:JAVADOC:38; JASPIC:JAVADOC:39;
   * JASPIC:JAVADOC:41; JASPIC:JAVADOC:42; JASPIC:JAVADOC:43; JASPIC:JAVADOC:44; JASPIC:JAVADOC:35; JASPIC:JAVADOC:36;
   * JASPIC:JAVADOC:51; JASPIC:JAVADOC:49; JASPIC:JAVADOC:54; JASPIC:JAVADOC:65; JASPIC:JAVADOC:63; JASPIC:JAVADOC:68;
   * JASPIC:JAVADOC:69; JASPIC:JAVADOC:71; JASPIC:JAVADOC:28; JASPIC:JAVADOC:107;
   *
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. read the server side log to verify Whether the following callbackHandlers are supported:
   *       CallerPrincipalCallback, GroupPrincipalCallback, and PasswordValidationCallback.
   *
   * Description The CallbackHandler passed to ServerAuthModule.initialize must support the following callbacks:
   * CallerPrincipalCallback, GroupPrincipalCallback, and PasswordValidationCallback.
   *
   */
  @Test
  public void CheckCallbackSupport() {
      String strMsg1 = "In HttpServlet : ServerRuntime CallbackHandler supports CallerPrincipalCallback";
      String strMsg2 = "In HttpServlet : ServerRuntime CallbackHandler supports GroupPrincipalCallback";
      String strMsg3 = "In HttpServlet : ServerRuntime CallbackHandler supports PasswordValidationCallback";

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see which CBH's are supported.
      responseFromServerWithCredentials(
          "ModTestServlet" + "?method.under.test=CheckCallbackSupport", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(strMsg1, strMsg2, strMsg3));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testGPCIsUserInRole
   *
   * @assertion_ids: JASPIC:SPEC:90;
   * @test_Strategy: The ModTestServlet is called which forces our SAM to invoke
   *                 callbacks - including the GPC. This test expects and checks
   *                 that the GPC was invoked with non-null principal and that
   *                 auth was mandatory and that upon return from
   *                 validateRequest, there are values set in
   *                 HttpServletRequest.isUserInRole()
   *
   *                 Description This is checking that the group callbacks work
   *                 for case of being called with CPC when auth is required and
   *                 the check that the validateRequest ensures there was a
   *                 return value set for calls to
   *                 HttpServletRequest.getRemoteUser()
   *
   */
  @Test
  public void testGPCIsUserInRole() {
      String strMsg1 = "In HttpServlet : ServerRuntime GroupPrincipalCallbackSupport():";
      strMsg1 += " successfully called callbackHandler.handle(callbacks)";
      strMsg1 += " for servlet: /ModTestServlet";
      strMsg1 += " with isServletAuthMandatory = " + true;

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see that GPC is supported and
      // that the IsUserInRole() is properly populated.
      String str = readFromServerWithCredentials("ModTestServlet" + "?method.under.test=testGPCIsUserInRole", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(strMsg1));

      String msg =
          "testGPCIsUserInRole did not have proper credential values returned in " +
          "ModTestServlet" + ".  this could be due to CBH's not properly doing authentication.";

      assertTrue(msg, str.contains("ModTestServlet->testGPCIsUserInRole() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testGPCGetUserPrincipal
   *
   * @assertion_ids: JASPIC:SPEC:90;
   * @test_Strategy: The ModTestServlet is called which forces our SAM to invoke
   *                 callbacks - including the GPC. This test expects and checks
   *                 that the GPC was invoked with non-null principal and that
   *                 auth was mandatory and that upon return from
   *                 validateRequest, there are values set in
   *                 HttpServletRequest.getUserPrincipal()
   *
   *                 Description This is checking that the group callbacks work
   *                 for case of being called with CPC when auth is required and
   *                 the check that the validateRequest ensures there was a
   *                 return value set for calls to
   *                 HttpServletRequest.getUserPrincipal()
   *
   */
  @Test
  public void testGPCGetUserPrincipal() {

      String strMsg1 = "In HttpServlet : ServerRuntime GroupPrincipalCallbackSupport():";
      strMsg1 += " successfully called callbackHandler.handle(callbacks)";
      strMsg1 += " for servlet: /ModTestServlet";
      strMsg1 += " with isServletAuthMandatory = " + true;

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see that GPC is supported and
      // that the GetUserPrincipal() is properly populated.
      String str = readFromServerWithCredentials("ModTestServlet" + "?method.under.test=testGPCGetUserPrincipal", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(strMsg1));

      String msg = "testGPCGetUserPrincipal did not have proper credential values returned in ";
      msg += "ModTestServlet" + ".  this could be due to CBH's not properly doing authentication.";

      assertTrue(msg, str.contains("ModTestServlet->testGPCGetUserPrincipal() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testGPCGetAuthType
   *
   * @assertion_ids: JASPIC:SPEC:90;
   * @test_Strategy: The ModTestServlet is called which forces our SAM to invoke
   *                 callbacks - including the GPC. This test expects and checks
   *                 that the GPC was invoked with non-null principal and that
   *                 auth was mandatory and that upon return from
   *                 validateRequest, there are values set in
   *                 HttpServletRequest.getAuthType()
   *
   *                 Description This is checking that the group callbacks work
   *                 for case of being called with CPC when auth is required and
   *                 the check that the validateRequest ensures there was a
   *                 return value set for calls to
   *                 HttpServletRequest.getAuthType()
   *
   */
  @Test
  public void testGPCGetAuthType() {

      String strMsg1 = "In HttpServlet : ServerRuntime GroupPrincipalCallbackSupport():";
      strMsg1 += " successfully called callbackHandler.handle(callbacks)";
      strMsg1 += " for servlet: /ModTestServlet";
      strMsg1 += " with isServletAuthMandatory = " + true;

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see that GPC is supported and
      // that the GetAuthType() is properly populated.
      String str = readFromServerWithCredentials("ModTestServlet" + "?method.under.test=testGPCGetAuthType", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify whether the log contains required messages.
      assertTrue(logProcessor.verifyLogContains(strMsg1));

      String msg = "testGPCGetAuthType did not have proper credential values returned in ";
      msg += "ModTestServlet" + ".  this could be due to CBH's not properly doing authentication.";

      assertTrue(msg, str.contains("ModTestServlet->testGPCGetAuthType() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckAuthenInValidateRequest
   *
   * @assertion_ids: JASPIC:SPEC:90;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. read the server side log to
   *                 verify if the validateRequest() was called under the
   *                 scenario of already being logged in. If we are already
   *                 logged in, then there are (3) HttpServletRequest methods
   *                 that must return non-null values:` - getAuthType() -
   *                 getRemoteUser() - getUserPrincipal()
   *
   *                 Description The TSSVLog.txt file looking for string that
   *                 indicates we found a scenario with only some of the
   *                 forementioned methods returned non-null. If a user is
   *                 authenticated, they should return non-null values. If the
   *                 user is NON authenticated, these methods should return
   *                 null.
   *
   */
  @Test
  public void CheckAuthenInValidateRequest() {
      String strMsg1 = "validateRequest():  ERROR - invalid authen scenario.";
      String tempArgs[] = { strMsg1 };

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see which CBH's are supported.
      readFromServerWithCredentials("ModTestServlet" + "?method.under.test=CheckAuthenInValidateRequest", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertFalse(logProcessor.verifyLogContains(tempArgs));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testAuthenResultsOnHttpServlet
   *
   * @assertion_ids: JASPIC:SPEC:90; JASPIC:SPEC:322;
   *
   * @test_Strategy: When authentication is mandatory, after AuthStatus.SUCCESS
   *                 is returned from validateRequest, we should be able to
   *                 verify that HttpServletRequest has non-null values set for
   *                 the following (3) method calls: request.getAuthType()
   *                 request.getRemoteUser() request.getUserPrincipal()
   *
   *                 This is based on javadoc for HttpServletRequest as well as
   *                 on jsr-196 v1.1 spec section 3.8.1.3 and 3.8.4.
   *
   */
  @Test
  public void testAuthenResultsOnHttpServlet() {
      String str = readFromServerWithCredentials("ModTestServlet" + "?method.under.test=testAuthenResultsOnHttpServlet", "j2ee", "j2ee");

      assertTrue(str.contains("ModTestServlet->testAuthenResultsOnHttpServlet() passed"));
  }



  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testRemoteUserCorrespondsToPrin
   *
   * @assertion_ids: JASPIC:SPEC:321;
   *
   * @test_Strategy: When authentication is mandatory, after AuthStatus.SUCCESS
   *                 is returned from validateRequest, we should be able to
   *                 verify that (a) HttpServletRequest has non-null values set
   *                 for the following methods: request.getRemoteUser() and
   *                 request.getUserPrincipal() (b) that getRemoteUser() and
   *                 getUserPrincipal().getName() are equal.
   *
   *                 This is based on javadoc for HttpServletRequest as well as
   *                 on jsr-196 v1.1 spec section 3.8.4. Specifically, this is
   *                 validating that: "In both cases, the HttpServletRequest
   *                 must be modified as necessary to ensure that the Principal
   *                 returned by getUserPrincipal and the String returned by
   *                 getRemoteUser correspond, ..." Using the above spec
   *                 reference COMBINED with the following Servlet spec
   *                 reference, we see that:
   *
   *                 (Using Servlet spec v3.1, section 13.3 - it states: "The
   *                 getRemoteUser method returns the name of the remote user"
   *                 AND "Calling the getName() method on the Principal returned
   *                 by getUserPrincipal() returns the name of the remote user."
   *                 We can use these two Servlet spec references combined with
   *                 our JASPIC reference to validate that principal.getName()
   *                 equals (i.e. "corresponds") to the same value returned by
   *                 getRemoteUser().
   *
   */
  @Test
  public void testRemoteUserCorrespondsToPrin() {
      String str = readFromServerWithCredentials(
              "ModTestServlet" + "?method.under.test=testRemoteUserCorrespondsToPrin", "j2ee", "j2ee");

      assertTrue(str.contains("ModTestServlet->testRemoteUserCorrespondsToPrin() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testAuthenIsUserInRole
   *
   * @assertion_ids: JASPIC:SPEC:321;
   *
   * @test_Strategy: When authentication is mandatory, after AuthStatus.SUCCESS
   *                 is returned from validateRequest, we should be able to
   *                 verify that HttpServletRequest has non-null values set for
   *                 the following method call: request.isUserInRole()
   *
   *                 This is based on javadoc for HttpServletRequest as well as
   *                 on jsr-196 v1.1 spec section 3.8.1.3 and 3.8.4. There is no
   *                 direct spec reference, but it is a scenario that can be
   *                 inferred.
   */
  @Test
  public void testAuthenIsUserInRole()  {
      String str = readFromServerWithCredentials(
              "ModTestServlet" + "?method.under.test=testAuthenIsUserInRole", "j2ee", "j2ee");

      assertTrue(str.contains("ModTestServlet->testAuthenIsUserInRole() passed"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testAuthenAfterLogout
   *
   * @assertion_ids: JASPIC:SPEC:98;
   *
   * @test_Strategy: When authentication is mandatory, after AuthStatus.SUCCESS
   *                 is returned from validateRequest, we should be able to
   *                 verify that we can properly login and invoke callback
   *                 handlers.
   *
   *                 This test ensures that we can authN when we are not already
   *                 pre-logged in. This will call a servlet that is open to
   *                 all, (thus no security constraints) and it will invoke the
   *                 servlet logout() command. Then it will make a call to a
   *                 servlet which requires mandatory authentication. The
   *                 premise being that authentication does occur - especially
   *                 if not pre-logged in.
   *
   *                 Note the spec does not state that you can not be pre-logged
   *                 in but it is saying that authentication has to be able to
   *                 succeed in a particular manner. (see spec section 3.8.3.1)
   *
   */
  @Test
  public void testAuthenAfterLogout() {
      // Call a servlet that is open to all and requires NO authN to access
      // validate we were not pre-authenticated and if so, force logout
      String str = readFromServerWithCredentials(allAccessServletPath + "?method.under.test=TestAuthenAfterLogout", "j2ee", "j2ee");

      // Now make a call to a servlet that requires mandatory authN and
      // make sure we still successfully support callbacks
      str = readFromServerWithCredentials(servletPath + "?method.under.test=testAuthenAfterLogout", "j2ee", "j2ee");

      assertTrue(str.contains("ModTestServlet->testAuthenAfterLogout() passed"));

      // Next make sure we see our CPC being properly called.
      String strMsg2 = "In HttpServlet : ServerRuntime CallerPrincipalCallback";
      strMsg2 += " called for profile=HttpServlet for servletPath=" + contextPath + "/" + servletPath;
      String strMsg3 = "Validated we are not prelogged in for OpenToAllServlet";

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(strMsg2, strMsg3));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testGPCWithNoRequiredAuth
   *
   * @assertion_ids: JASPIC:SPEC:90;
   * @test_Strategy: This is checking that the group callbacks work for case of
   *                 being called with CPC when no auth is required.
   *
   *                 Description This ultmately calls
   *                 ServerCallbackSupport.GroupPrincipalCallbackSupport() with
   *                 no required creds.
   */
  @Test
  public void testGPCWithNoRequiredAuth() {

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see which CBH's are supported.
      readFromServerWithCredentials(allAccessServletPath + "?method.under.test=testGPCWithNoRequiredAuth", "j2ee", "j2ee");

      String strMsg1 = "In HttpServlet : ServerRuntime GroupPrincipalCallbackSupport():";
      strMsg1 += " successfully called callbackHandler.handle(callbacks)";
      strMsg1 += " for servlet: " + "/" + openToAllServletPath;
      strMsg1 += " with isServletAuthMandatory = " + false;

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(strMsg1));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: testGPCGetRemoteUser
   *
   * @assertion_ids: JASPIC:SPEC:90;
   * @test_Strategy: The ModTestServlet is called which forces our SAM to invoke
   *                 callbacks - including the GPC. This test expects and checks
   *                 that the GPC was invoked with non-null principal and that
   *                 auth was mandatory and that upon return from
   *                 validateRequest, there are values set in
   *                 HttpServletRequest.getRemoteUser()
   *
   *                 Description This is checking that the group callbacks work
   *                 for case of being called with CPC when auth is required and
   *                 the check that the validateRequest ensures there was a
   *                 return value set for calls to
   *                 HttpServletRequest.getRemoteUser()
   *
   */
  @Test
  public void testGPCGetRemoteUser() {

      String strChkServlet = "ModTestServlet->testGPCGetRemoteUser() passed";

      String strMsg1 = "In HttpServlet : ServerRuntime GroupPrincipalCallbackSupport():";
      strMsg1 += " successfully called callbackHandler.handle(callbacks)";
      strMsg1 += " for servlet: /ModTestServlet";
      strMsg1 += " with isServletAuthMandatory = " + true;

      // By invoking this servlet, we are causing code to access our
      // servlet profile TSServerAuthModule.validateRequest() method
      // which will perform checks to see that GPC is supported and
      // that the GetRemoteUser() is properly populated.
      String str = readFromServerWithCredentials(servletPath + "?method.under.test=testGPCGetRemoteUser", "j2ee", "j2ee");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // First, lets make sure CBH's were actually called
      assertTrue(logProcessor.verifyLogContains(strMsg1));

      // Next, lets make sure CBH's set principal values as expected
      String msg = "testGPCGetRemoteUser did not have proper credential values returned in ";
      msg += servletPath + ".  this could be due to CBH's not properly doing authentication.";
      assertTrue(msg, str.contains(strChkServlet));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: verifyRuntimeCallOrder
   *
   * @assertion_ids: JASPIC:SPEC:317; JASPIC:SPEC:304;
   *
   * @test_Strategy: This test verifies that the runtime is invoking the proper
   *                 calling order of: validateRequest, process request,
   *                 secureResponse.
   *
   *                 Details After AuthStatus.SUCCESS is returned from
   *                 validateRequest, we need to verify that the request was
   *                 dispatched, and then that the secureResponse() was called -
   *                 in that order. We do this by verifying that the messages
   *                 (i.e. strMsg1, strMsg2, and strMsg3) were called in the
   *                 specific order. The verifyLogContains() method checks these
   *                 strings exist within the TSSVLog.txt file AND that they
   *                 exist within the log file in order that matches the way
   *                 they are declared via "theArgs[]".
   *
   */
  @Test
  public void verifyRuntimeCallOrder() {
      // Strings that MUST exist within the authentication-trace-log.xml file
      String strMsg1 = "TSServerAuthContext.validateRequest called for layer=HttpServlet for requestURI=" + contextPath + "/" + wrapperServletPath;
      String strMsg2 = "WrapperServlet.doTests() content processed for requestURI";
      String strMsg3 = "secureResponse called for layer=HttpServlet for requestURI=" + contextPath + "/" + wrapperServletPath;

      invokeServletAndGetResponse(wrapperServletPath, "POST", "testRequestWrapper");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(true, strMsg1, strMsg2, strMsg3));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: VerifyClientSubjects
   *
   * @assertion_ids: JASPIC:SPEC:96; JASPIC:SPEC:52; JASPIC:JAVADOC:28;
   *                 JASPIC:JAVADOC:106; JASPIC:SPEC:23; JASPIC:SPEC:19;
   *                 JASPIC:SPEC:51;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for
   *                 Registering TSSV with your AppServer ).
   *
   *                 2. read the server side log to
   *                 verify whether the ClientSubject passed to validateRequest
   *                 was null or not.
   *
   *                 Description According to spec section 3.8.2: A new
   *                 clientSubject must be instantiated and passed in the call
   *                 to validateRequest.
   *
   */
  @Test
  public void VerifyClientSubjects()  {
    String strMsg1 = "HttpServlet profile: ";
    strMsg1 += "TSServerAuthContext.validateRequest called with non-null client Subject";

    invokeServlet(servletPath, "POST", "VerifyClientSubjects");

    LogFileProcessor logProcessor = new LogFileProcessor();
    logProcessor.fetchLogs();

    // First, verify we have a non-null clientSubject
    assertTrue(logProcessor.verifyLogContains(strMsg1));

    // Now verify we have no occurances of null clientSubjects
    checkForInvalidClientSubjects(logProcessor);

  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckMessageInfo
   *
   * @assertion_ids: JASPIC:SPEC:95; JASPIC:JAVADOC:28; JASPIC:JAVADOC:10; JASPIC:JAVADOC:11; JASPIC:SPEC:23;
   * JASPIC:SPEC:19; JASPIC:SPEC:69;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   * 2. read the server side log to verify Whether the messageInfo passed to validateRequest()
   * contains proper values for getRequestMessage() and getResponseMessage().
   *
   * Description The MessageInfo argument used in any call made by the message processing runtime to validateRequest must
   * have been initialized such that the non-null objects returned by the getRequestMessage and getResponseMessage methods
   * of the MessageInfo are an instanceof HttpServletRequest and HttpServletResponse.
   *
   */
  @Test
  public void CheckMessageInfo() {
      String strMsg1 = "validateRequest: MessageInfo.getRequestMessage() is of type ";
      String strMsg2 = "validateRequest: MessageInfo.getResponseMessage() is of type ";

      // This should work for servlets and static pages
      // invoking a static should cause the validateRequest to be called
      invokeServlet(staticPagePath, "POST", "CheckMessageInfo");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(
          logProcessor.verifyLogContains(
              strMsg1 + "jakarta.servlet.http.HttpServletRequest",
              strMsg2 + "jakarta.servlet.http.HttpServletResponse" ));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: VerifyReqPolicy
   *
   * @assertion_ids: JASPIC:SPEC:87; JASPIC:JAVADOC:107; JASPIC:JAVADOC:17; JASPIC:JAVADOC:14; JASPIC:JAVADOC:21
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   * 2. read the server side log to verify we have more than 1 target policy AND that
   * ProtectionPolicy.getID() returns a proper value.
   *
   *
   * Description Calling getTargetPolicies on the request MessagePolicy must return an array containing at least one
   * TargetPolicy whose ProtectionPolicy will be interpreted by the modules of the context to mean that the source of the
   * corresponding targets within the message is to be authenticated. To that end, calling the getID method on the
   * ProtectionPolicy must return one of the following values: ProtectionPolicy.AUTHENTICATE_SENDER,
   * ProtectionPolicy.AUTHENTICATE_CONTENT
   *
   */
  @Test
  public void VerifyReqPolicy() {
      invokeServlet(servletPath, "POST", "VerifyReqPolicy");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // First make sure we have an array with more than 1 TargetPolicy
      checkForInvalidReqPolicy(logProcessor);

      // Next, make sure ProtectionPolicy.getID() returns valid value
      checkForInvalidProtectionPolicyID(logProcessor);
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: checkSACValidateRequestWithVaryingAccess
   *
   * @assertion_ids: JASPIC:SPEC:89; JASPIC:JAVADOC:28
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   * 2. read the server side log to verify ServerAuthContext.validateRequest() is called for our
   * servlet container even when no access will be granted.
   *
   * Description This will test that the message processing runtime must call validateRequest independent of whether or
   * not access to the resource would have been permitted prior to the call to validateRequest. Note: This particular
   * assertion actually only applies to the case of acceptable connections that have been negotiated and will not be
   * required for cases where the WebUserDataPermission objects allow a container to determine when to reject a request
   * befor redirection of that request would ultimately be rejected as a result of an excluding auth-constraint. (an
   * excluding auth-constraint is an auth-constraint that has no roles. (see JSR-115 spec, section 3.1.3.1, footnote 1)
   */
  @Test
  public void checkSACValidateRequestWithVaryingAccess() {
      String HDR = "TSServerAuthContext.validateRequest called for layer=HttpServlet";
      String FTR = " AuthStatus=AuthStatus.SUCCESS";
      String strMsg1 = HDR + " for requestURI=" + contextPath + "/" + noConstraintPath + FTR;
      String strMsg2 = HDR + " for requestURI=" + contextPath + "/" + servletPath + FTR;

      // Invoking a servlet should cause the validateRequest to be called
      // and here we are invoking a url that has no auth-constraints
      invokeServlet(noConstraintPath, "POST", "checkSACValidateRequestWithVaryingAccess");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(strMsg1));

      // Invoking a servlet should cause the validateRequest to be called
      // and here we are invoking a url that has auth-constraints
      invokeServlet(servletPath, "POST", "checkSACValidateRequestWithVaryingAccess");

      assertTrue(logProcessor.verifyLogContains(strMsg2));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: VerifyMessageInfoObjects
   *
   * @assertion_ids: JASPIC:SPEC:60; JASPIC:JAVADOC:27; JASPIC:JAVADOC:28; JASPIC:SPEC:305;
   *
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. Invoke a servlet to cause a validateRequest invocation, (make sure it is a servlet that has Authen Permissions
   *    enabled so that we can return AuthStatus.SUCCESS from validateRequest() so that we can be sure to enter into
   *    secureResponse(). Once in secureResponse, we want to validate the MessageInfo object is the same as the MessageInfo
   *    object passed into the validateRequest()
   *
   * Description Verify that the MessageInfo object passed into the validateRequest() method is the same MessageInfo
   * object that is passed into secureResponse().
   *
   */
  @Test
  public void VerifyMessageInfoObjects() {
      String strMsg1 = "MessageInfo object from secureRequest matches ";
      strMsg1 += "the  messageInfo object from validateRequest";

      // Invoking this servlet should cause the validateRequest and
      // the secureResponse to be called.
      invokeServlet(JASPICData.AUTHSTAT_MAND_SUCCESS, "POST", "VerifyMessageInfoObjects");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(strMsg1));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: VerifyServiceSubjects
   *
   * @assertion_ids: JASPIC:SPEC:53; JASPIC:SPEC:61; JASPIC:SPEC:314; JASPIC:SPEC:313; JASPIC:JAVADOC:27;
   * JASPIC:JAVADOC:28; JASPIC:SPEC:24
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. Invoke a servlet to cause a validateRequest invocation, then read the server side log to
   *    verify we have no valid service subject messages.
   *
   * Description Verify that If a non-null Subject was used to acquire the ServerAuthContext, the same Subject must be
   * passed as the serviceSubject in this call (ie to Point 2 in MPR model).
   *
   */
  @Test
  public void VerifyServiceSubjects() {

      // Ideally we'd like to find a platform independant way to
      // also verify that we have valid (non-null) serviceSubjects
      // but I don't believe this is possible so for now, we will
      // be content to verify no invalid serviceSubjects were found

      // Invoking a servlet should cause the validateRequest to be called
      // but does not guarantee a non-null serviceSubject
      invokeServlet(servletPath, "POST", "VerifyServiceSubjects");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Currently we verify that there were no invalid serviceSubject
      checkForInvalidServiceSubjects(logProcessor);
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: VerifySAContextVerifyReqIsCalled
   *
   * @assertion_ids: JASPIC:SPEC:88; JASPIC:SPEC:50; JASPIC:JAVADOC:28
   *
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. Use FetchLog servlet to read the server side log to verify ServerAuthContext.validateRequest() is called for our
   *    servlet container.
   *
   * Description The inbound processing of requests by a servlet container corresponds to point (2) in the message
   * processing model. At point (2) in the message processing model, the runtime must call validateRequest on the
   * ServerAuthContext.
   *
   */
  @Test
  public void VerifySAContextVerifyReqIsCalled() {
      // Invoking a servlet should cause the validateRequest to be called
      invokeServlet(servletPath, "POST", "VerifySAContextVerifyReqIsCalled");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains("TSServerAuthContext.validateRequest called for layer=HttpServlet"));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: CheckMsgInfoKey
   *
   * @assertion_ids: JASPIC:SPEC:306; JASPIC:SPEC:300; JASPIC:JAVADOC:73; JASPIC:SPEC:92; JASPIC:SPEC:24; JASPIC:SPEC:103;
   *
   *
   * @test_Strategy:
   *    1. invoke servlet that requires authentication
   *    2. verify that the MesageInfo object passed into getAuthContextID, validateRequest, and secureResponse
   *       all had valid key-value pairs.
   *
   * Description: This profile requires that the message processing runtime conditionally establish the following
   * key-value pair within the Map of the MessageInfo object passed in the calls to getAuthContextID, validateRequest, and
   * secureResponse. (key) jakarta.security.auth.message.MessagePolicy.isMandatory (val) Any non-null String value, s, for
   * which Boolean.valueOf(s).booleanValue() == true. The MessageInfo map must contain this key and its associated value,
   * if and only if authentication is required to perform the resource access corresponding to the HttpServletRequest to
   * which the ServerAuthContext will be applied.
   *
   * If we are Jakarta Authorization compatible, there is an additional test we must make. The key=jakarta.security.jacc.PolicyContext
   * better exist in the Properties arg passed into getAuthContext().
   *
   */
  @Test
  public void CheckMsgInfoKey() {
      String CMN_HDR = "dumpServletProfileKeys() called with attrs:  layer=HttpServlet";
      CMN_HDR += " servletName=/ModTestServlet callerMethod=";

      String CMN_TAIL = " key=jakarta.security.auth.message.MessagePolicy.isMandatory";
      CMN_TAIL += " value=Valid";

      String strAuthCtxt = CMN_HDR + "getAuthContextID" + CMN_TAIL;
      String strValReq = CMN_HDR + "validateRequest" + CMN_TAIL;

      // See if we can successfully call into the servlet!
      invokeServlet(servletPath, "POST", "CheckMsgInfoKey");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(strAuthCtxt, strValReq));

      boolean bIs115Compatible = true;

      // ONLY if we are Jakarta Authorization compatible, we want to make an additional key test
      if (bIs115Compatible) {
          String HDR = "layer=HttpServlet appContext=" + appContext;
          String str1 = HDR + " Key=jakarta.security.jacc.PolicyContext does exist thus 115 compatible";

          invokeServlet(servletPath, "POST", "CheckMsgInfoKey");

          // verify whether the log contains required messages.
          assertTrue(logProcessor.verifyLogContains(str1));

      } else {
          // Not Jakarta Authorization compatible so the identity must include the caller Principal
          // (established during the validateRequest processing using the
          // corresponding CallerPrincipalCallback)
          //
          // Note: by testing the subject=non-null, we are actually verifying that
          // assertion JASPIC:SPEC:103 is met (e.g. non-null clientSubject used in
          // CallerPrincipalCallback)
          String strMsg1 = "In HttpServlet : ServerRuntime CallerPrincipalCallback";
          strMsg1 += " called for profile=HttpServlet for servletPath=" + servletPath;
          strMsg1 += " subject=non-null principal set = j2ee";

          assertTrue(logProcessor.verifyLogContains(strMsg1));
      }
  }

  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckServletAppContext
   *
   * @assertion_ids: JASPIC:SPEC:67; JASPIC:SPEC:68; JASPIC:JAVADOC:84; JASPIC:JAVADOC:85;
   *
   * @test_Strategy: 1. Verify TSAuthConfigProviderServlet was registered with layer="HttpServlet" and that
   * TSServerAuthConfig was invoked with this layer value also to satisfy assertion 67.
   *
   * 2. Our ProviderConfiguration should have registered a provider for us AND doing that should have caused the MPR to
   * use the same appContextId for the ACP and ServerAuthConfig. We will check that both were invoked with the
   * same/expected appContextId to satisfy assertion 68.
   *
   * Description: This test will verify that the runtime uses the same appContextId for both the AuthConfigProvider and
   * the ServerAuthConfig.
   *
   */
  @Test
  public void CheckServletAppContext() {
      String acfMsg = "getConfigProvider called for Layer : " + "HttpServlet" + " and AppContext :" + appContext;
      String sacMsg = "TSServerAuthConfig called for layer=" + "HttpServlet" + " : appContext=" + appContext;

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify TSAuthConfigProviderServlet was registered properly
      assertTrue(logProcessor.verifyLogContains(acfMsg));

      // Verify runtime used same appContextId for the AuthConfigProvider and ServerAuthConfig
      assertTrue(logProcessor.verifyLogContains(sacMsg));
  }

  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckACPConfigObjAppContext
   *
   * @assertion_ids: JASPIC:SPEC:74; JASPIC:SPEC:75; JASPIC:SPEC:11; JASPIC:SPEC:13; JASPIC:JAVADOC:94
   *
   * @test_Strategy: 1. Verify that a non-null provider was returned during our registration of
   * TSAuthConfigProviderServlet and that by fact that non-null provider was returned, we need to verify that the MPR
   * called getServerAuthConfig for that provider. All these verifications need to be done for the corresponding layer and
   * appContextId values. (this satisfys assertion 75)
   *
   * 2. Verify the MPR called getConfigProvider for the layer and appContextId in order to satisfy assertion 74)
   *
   * Description: This test will verify that the ACP was properly registered and that the MPR was able to successfully use
   * getConfigPRovider.
   *
   */
  @Test
  public void CheckACPConfigObjAppContext() {
      String sacMsg = "TSAuthConfigProviderServlet.getServerAuthConfig" + " : layer=" + "HttpServlet" + " : appContext=" + appContext;

      // Make sure everything we need is properly setup
      invokeServlet(servletPath, "POST", "CheckACPConfigObjAppContext");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify non-null provider returned during registration
      assertTrue(logProcessor.verifyLogContains(ACF_MSG_1));

      // verify MPR called getConfigProvider for the layer and appContextId
      assertTrue(logProcessor.verifyLogContains(sacMsg));
  }

  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckACPContextObjAppContext
   *
   * @assertion_ids: JASPIC:SPEC:74; JASPIC:SPEC:75; JASPIC:SPEC:77
   *
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. Read the server side log to verify the following: - verify non-null ACP is returned by ACF
   *    - verify same info used to initialize ACP was also used to initialize the ServerAuthConfig (SAC) for this provider. -
   *    verify same info used in ACP and SAC was also used to init the auth context.
   *
   * Description: This test verifies that AuthConfigFactory returns a non-null ACP and that the same info is used to init
   * the ACP, SAC, and the ServerAuthContext.
   *
   */
  @Test
  public void CheckACPContextObjAppContext() {
      String sacMsg = "TSServerAuthConfig called for layer=" + "HttpServlet" + " : appContext=" + appContext;
      String saConfigMsg = "TSServerAuthContext called for messageLayer=" + "HttpServlet" + " : appContext=" + appContext;

      // Make sure everything we need is properly setup
      invokeServlet(servletPath, "POST", "CheckACPContextObjAppContext");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // verify non-null ACP is returned by ACF
      assertTrue(logProcessor.verifyLogContains(ACF_MSG_1));

      // verify same info used to initialize ACP was also used to init SAC
      assertTrue(logProcessor.verifyLogContains(sacMsg));

      // verify same info used in ACP and SAC was also used to init auth context
      assertTrue(logProcessor.verifyLogContains(saConfigMsg));
  }

  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckMPRCallsGetAuthContext
   *
   * @assertion_ids: JASPIC:SPEC:74; JASPIC:SPEC:78; JASPIC:JAVADOC:100
   *
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. Use FetchLog servlet to read the server side log to verify the following: - verify non-null ACP is returned by ACF
   *    - verify ServerAuthConfig.getAuthContext() is called by MPR
   *
   * Description: This test is used to test that the MPR properly called ServerAuthConfig.getAuthContext(). This will also
   * perform an additional test of verifying a non-null ACP is returned by ACF.
   *
   */
  @Test
  public void CheckMPRCallsGetAuthContext() {
      String layer = "HttpServlet";
      String saConfigMsg = "TSServerAuthConfig.getAuthContext:  layer=" + layer + " : appContext=" + appContext;

      // Make sure everything we need is properly setup
      invokeServlet(servletPath, "POST", "CheckMPRCallsGetAuthContext");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Verify non-null ACP is returned by ACF
      assertTrue(logProcessor.verifyLogContains(ACF_MSG_1));

      // Verify TSServerAuthConfig.getAuthContext() is called by the runtime
      assertTrue(logProcessor.verifyLogContains(saConfigMsg));
  }


  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckforNonNullAuthContext
   *
   * @assertion_ids: JASPIC:SPEC:79; JASPIC:JAVADOC:100
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   * 2. Use FetchLog servlet to read the server side log to verify the following: - verify there were no null values
   * returned for all calls to getAuthContext() regardless of what operationId is. (this satisfies assertion 79) - verify
   * we ARE seeing at least one successful value returned from our call to getAuthContext() to satisfy oursleves that the
   * test really is working correctly as expected.
   *
   * Description: This test verifies that there were no null instances of ServerAuthContext objects AND we are also
   * verifying that we' are getting at least one non-null ServerAuthContext instanace.
   *
   */
  @Test
  public void CheckforNonNullAuthContext() {
      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // This string should NOT/NEVER appear in the log file as
      // the spec states:
      // "For all values of the operation argument, the call to
      // getAuthContext must return a non-null authentication context"
      assertFalse(logProcessor.verifyLogContains("TSServerAuthConfig.getAuthContext: returned null ServerAuthContext"));

      // We know we should have gotten at least one successful (non-null)
      // value from our call go getAuthContext so lets check to make sure.
      assertTrue(logProcessor.verifyLogContains("TSServerAuthConfig.getAuthContext: returned non-null ServerAuthContext"));
  }

  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: CheckforNonNullCallback
   *
   * @assertion_ids: JASPIC:SPEC:71
   *
   * @test_Strategy:
   *    1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   *    2. Read the server side log to verify the following: - verify there was a non-null cbh passed
   *    into the TSAuthConfigProviderServlet.getServerAuthConfig() method. (spec section 3.5 says the cbh must not be null)
   *
   * Description: This test verifies that a non-null CakkbackHandler instance passed into the
   * AuthConfigProvider.getServerAuthConfig().
   *
   */
  @Test
  public void CheckforNonNullCallback() {
      String strMsg1 = "layer=HttpServlet appContext=" + appContext;
      strMsg1 += " getServerAuthConfig() received CallbackHandler=non-null";

      // make sure everything we need is properly setup
      invokeServlet(servletPath, "POST", "CheckforNonNullCallback");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      assertTrue(logProcessor.verifyLogContains(strMsg1));
  }

  /**
   * @keywords: jaspic_servlet
   *
   * @testName: VerifyNoInvalidEntries
   *
   * @assertion_ids: JASPIC:SPEC:52; JASPIC:SPEC:96; JASPIC:SPEC:53; JASPIC:SPEC:87; JASPIC:SPEC:313; JASPIC:SPEC:60;
   * JASPIC:JAVADOC:106; JASPIC:SPEC:322; JASPIC:SPEC:323;
   *
   * @test_Strategy: 1. Register TSSV with the AppServer. (See User guide for Registering TSSV with your AppServer ).
   *
   * 2. Invoke a series of web pages with varying servlet perms and access/connection scenarios.
   *
   * 3. Use FetchLog servlet to read the server side log to verify no invalid scenarios were encountered.
   *
   *
   * Description This is a negative test case that is used to assist in verifying several different assertions. The
   * intention of this test is to make sure accessing web resources under different circumstances should NOT cause any
   * invalid issues to be encountered.
   *
   */
  @Test
  public void VerifyNoInvalidEntries() {
      // Try to invoke different servlet pages to see if this
      // causes an invalid condition to occur
      invokeServlet(staticPagePath, "POST", "VerifyNoInvalidEntries");
      invokeServlet(noConstraintPath, "POST", "VerifyNoInvalidEntries");
      invokeServlet(servletPath, "POST", "VerifyNoInvalidEntries");

      LogFileProcessor logProcessor = new LogFileProcessor();
      logProcessor.fetchLogs();

      // Now check log file looking for invalid entries
      doCommonVerificationChecks(logProcessor);
  }

  /**
  *
  * @keywords: jaspic_servlet
  *
  * @testName: ACFSwitchFactorys
  *
  * @assertion_ids:
  *
  *
  * @test_Strategy: This test does the following: - gets current (CTS) factory - sets the vendors ACF thus replacing the
  * CTS ACF - verify ACF's were correctly set - reset factory back to the original CTS factory
  *
  * 1. Use the static setFactory method to set an ACF and this should always work. and use the getFactory to verify it
  * worked.
  *
  * Description
  *
  */
  @Test
  public void ACFSwitchFactorys() {
      String strMsg1 = "ACFTestServlet->ACFSwitchFactorys() passed";

      String str = invokeServletAndGetResponse(acfServletPath, "POST", "ACFSwitchFactorys");

      assertTrue(str.contains(strMsg1));
  }

  /**
   *
   * @keywords: jaspic_servlet
   *
   * @testName: ACFRemoveRegistrationWithBadId
   *
   * @assertion_ids: JASPIC:SPEC:345;
   *
   *
   * @test_Strategy: This test verifies we get a return value of False when invoking ACF.removeRegistration(some_bad_id);
   *
   * 1. Use the static setFactory method to get an ACF and then attempt to invoke removeRegistration() with an invalid (ie
   * non-existant) regId. This should return False (per javadoc).
   *
   * Description
   *
   */
  @Test
  public void ACFRemoveRegistrationWithBadId() {
      String strMsg1 = "ACFTestServlet->ACFRemoveRegistrationWithBadId() passed";

      String str = invokeServletAndGetResponse(acfServletPath, "POST", "ACFRemoveRegistrationWithBadId");

      assertTrue(str.contains(strMsg1));
  }

  /**
   * This is convenience method that can be called anywhere and is intended to be used to make sure no unwanted log
   * messages appear which would indicate a failure.
   *
   * <p>
   * To clarify, this means that a particular test case ~may~ cause some
   * unexpected error to occur which would result in a log message getting dumped to the log file - even though we may not
   * be testing that particular assertion at that given moment. This method can be used as a final pass at the end of each
   * test to ensure that no accidental/unexpected errors were encountered.
   *
   */
  private void doCommonVerificationChecks(LogFileProcessor logProcessor) {
      // Checks that should ALWAYS pass
      checkForInvalidReqPolicy(logProcessor);
      checkForInvalidProtectionPolicyID(logProcessor);
      checkForInvalidServiceSubjects(logProcessor);
      checkForInvalidClientSubjects(logProcessor);
      checkForInvalidMessageInfoObjs(logProcessor);
      checkForNullCallback(logProcessor);
      checkForNullAuthConfigObj(logProcessor);
      checkForAuthenMisMatch(logProcessor);
      checkForinvalidMsgInfoMapKey(logProcessor);
  }

  /**
   * This is used to verify assertion JASPIC:SPEC:52 and JASPIC:SPEC:96 which
   * states that A new clientSubject must be instantiated and passed in the call
   * to validateRequest. (see spec section 3.8.2) We look for occurances of
   * strings that indicate we have a null clientSubject passed into our
   * validateRequest and we issue a failure if a match is found.
   */
  private void checkForInvalidClientSubjects(LogFileProcessor logProcessor) {
      String strMsg2 = "FAILURE detected - ClientSubjects should not be read-only.";
      String strMsg3 = "FAILURE detected - ClientSubjects should not be null.";
      String strMsg1 = "HttpServlet profile: ";
      strMsg1 += "TSServerAuthContext.validateRequest called with null client Subject";
      String tempArgs1[] = { strMsg1, strMsg2, strMsg3 };

      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs1));
  }

  /**
   * This is convenience method that helps verify assertion JASPIC:SPEC:87 which states that for the Servlet profile,
   * Calling getTargetPolicies on the request MessagePolicy must return an array containing at least one TargetPolicy. We
   * look for a message that will be printed out if any calls to getTargetPolicies return an array with less than one
   * TargetPolicy.
   *
   */
  private void checkForInvalidReqPolicy(LogFileProcessor logProcessor) {
      String strMsg1 = "Layer=HttpServlet requestPolicy=invalid";
      strMsg1 += " in TSServerAuthModule.initialize()";
      String tempArgs[] = { strMsg1 };

      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs));
  }

  /**
   * This is convenience method that helps verify assertion JASPIC:SPEC:87 which states that for the Servlet profile,
   * Calling the getID() method on the ProtectionPolicy must return a valid ID type (as defined in spec section 3.7.4). We
   * look for a message that will be printed out if any calls to getID() returned an invalid ProtectionPolicy ID.
   *
   */
  private void checkForInvalidProtectionPolicyID(LogFileProcessor logProcessor) {
      String strMsg1 = "Layer=HttpServlet  Invalid ProtectionPolicy.getID()";
      String tempArgs[] = { strMsg1 };

      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs));
  }

  /**
   * This is convenience method that helps verify assertions related to the validateRequest and secureResponse methods.
   * This tests assertion JASPIC:SPEC:53 which states that for the Servlet profile, If a non-null Subject was used to
   * acquire the ServerAuthContext, the same Subject must be passed as the serviceSubject in this call.
   *
   * This also tests assertion JASPIC:SPEC:313 which states that along with the above requirments if a non-null
   * serviceSubject is used in this call, it must not be read-only. (see spec section 2.1.5.2).
   *
   * Also tested are assertions JASPIC:SPEC:61, JASPIC:SPEC:314 and JASPIC:SPEC:313.
   *
   * We look for occurances of strings that indicate we have a serviceSubject mismatch in our runtime - which we should
   * not have.
   */
  private void checkForInvalidServiceSubjects(LogFileProcessor logProcessor) {
      String strMsg1 = "FAILURE detected - ServiceSubjects should be the same and are not.";
      String strMsg5 = "FAILURE detected - SecureResponse ServiceSubjects should be the same and are not.";
      String strMsg6 = "FAILURE detected - SecureResponse ServiceSubjects should not be read-only.";
      String tempArgs1[] = { strMsg1 };
      String tempArgs5[] = { strMsg5 }; // assertion: JASPIC:SPEC:61 and JASPIC:SPEC:314
      String tempArgs6[] = { strMsg6 }; // assertion: JASPIC:SPEC:313

      String strMsg2 = "ServiceSubjects correctly matched.";
      String strMsg3 = "found a non-null serviceSubject in getAuthContext()";
      String strMsg4 = "FAILURE detected - ServiceSubjects should not be read-only.";

      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs1));
      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs5));
      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs6));

      if (logProcessor.verifyLogContains(strMsg3)) {

          // We know that there was a non-null subject passed into getAuthContext()
          // so we better find strMsg2 in our log file too!
          assertTrue(logProcessor.verifyLogContains(strMsg2));

          // If we made it to here, then we have matching serviceSubjects
          // and we should NOT have a read-only service subject
          assertFalse(logProcessor.verifyLogContains(strMsg4));
      }
  }

  /**
   * This is used to verify assertion JASPIC:SPEC:323 which states in spec section 3.8.4 (para 2)
   *
   */
  private void checkForinvalidMsgInfoMapKey(LogFileProcessor logProcessor) {
      String strMsg1 = "ERROR - invalid setting for jakarta.servlet.http.authType = null";
      String strMsg2 = "ERROR - mismatch value set for jakarta.servlet.http.authType and getAuthType()";
      String tempArgs1[] = { strMsg1, strMsg2 };

      // verify that we had NO mismatches between getAuthType() and
      // getRemoteUser()
      assertFalse(logProcessor.verifyLogContainsOneOf(tempArgs1));
  }

  /**
   * This is used to verify assertion JASPIC:SPEC:322 which states in spec section 3.8.4 (para 1) "Both cases, must also
   * ensure that the value returned by calling getAuthType on the HttpServletRequest is consistent in terms of being null
   * or non-null with the value returned by getUserPrincipal."
   *
   */
  private void checkForAuthenMisMatch(LogFileProcessor logProcessor) {
      String strMsg1 = "ERROR - HttpServletRequest authentication result mis-match with getAuthType() and getRemoteUser()";

      // Verify that we had NO mismatches between getAuthType() and
      // getRemoteUser()
      assertFalse(logProcessor.verifyLogContains(strMsg1));
  }

  /**
   * This is used to verify assertion JASPIC:SPEC:16 which states that the AuthConfigProvider must NOT return a null auth
   * config object (in the getServerAuthConfig() call)
   */
  private void checkForNullAuthConfigObj(LogFileProcessor logProcessor) {
      String strMsg1 = "WARNING: getServerAuthConfig() returned null";

      // verify that we had NO bad callbacks passed in
      assertFalse(logProcessor.verifyLogContains(strMsg1));
  }

  /**
   * This is used to verify assertion JASPIC:SPEC:71 which states that the cbh passed into
   * AuthConfigProvider.getServerAuthConfig() must NOT be null. (see spec section 3.5)
   */
  private void checkForNullCallback(LogFileProcessor logProcessor) {
      String strMsg1 = "FAILURE: layer=HttpServlet appContext=" + appContext;
      strMsg1 += " getServerAuthConfig() received CallbackHandler=null";

      // verify that we had NO bad callbacks passed in
      assertFalse(logProcessor.verifyLogContains(strMsg1));
  }

  /**
   * This is used to verify assertion JASPIC:SPEC:60 which states that the runtime must pass the same MessageInfo instance
   * (that was passed to validateRequest) into secureRequest. (see spec section 2.1.5.2)
   */
  private void checkForInvalidMessageInfoObjs(LogFileProcessor logProcessor) {
      String strMsg1 = "FAILURE:  MessageInfo object in secureRequest does not";
      strMsg1 += " match the messageInfo object from validateRequest";

      assertFalse(logProcessor.verifyLogContains(strMsg1));

  }


  private void invokeServlet(String path, String method, String test) {
      readFromServerWithCredentials(path + "?method.under.test=test", "j2ee", "j2ee");
  }

  private String invokeServletAndGetResponse(String path, String method, String test) {
      return readFromServerWithCredentials(path + "?method.under.test=" + test, "j2ee", "j2ee");
  }


}
