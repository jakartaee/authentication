package ee.jakarta.tck.authentication.test.basic;

import static ee.jakarta.tck.authentication.test.basic.servlet.JASPICData.TSSV_ACF;
import static jakarta.security.auth.message.config.AuthConfigFactory.DEFAULT_FACTORY_SECURITY_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ee.jakarta.tck.authentication.test.basic.sam.ProviderConfigurationEntry;
import ee.jakarta.tck.authentication.test.basic.sam.ProviderConfigurationXMLFileProcessor;
import ee.jakarta.tck.authentication.test.basic.sam.config.TSAuthConfigFactoryForStandalone;
import ee.jakarta.tck.authentication.test.basic.sam.config.TSRegistrationListener;
import ee.jakarta.tck.authentication.test.basic.servlet.CommonTests;
import ee.jakarta.tck.authentication.test.basic.servlet.JASPICData;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import jakarta.security.auth.message.config.RegistrationListener;
import java.security.Security;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;

public class SoapUnitTest {

    Logger logger = Logger.getLogger(SoapUnitTest.class.getName());

    private String logFileLocation = System.getProperty("log.file.location");
    private String providerConfigFileLocation = System.getProperty("provider.configuration.file");
    private String vendorACFClass = System.getProperty("vendor.authconfig.factory");
    private String soapAppContext = "localhost /Hello_web/Hello";

    private transient ProviderConfigurationXMLFileProcessor configFileProcessor;

    private Collection<ProviderConfigurationEntry> providerConfigurationEntriesCollection;

    private CommonTests commonTests = new CommonTests();

    @Before
    public void setup() {
        AuthConfigFactory.setFactory(null);
        Security.setProperty(DEFAULT_FACTORY_SECURITY_PROPERTY, TSSV_ACF);
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACFGetFactory
     *
     * @assertion_ids: JASPIC:SPEC:329; JASPIC:SPEC:335; JASPIC:SPEC:7;
     *
     * @test_Strategy: This s mainly concerned with testing the runtimes handling of ACF as follows: - get current (CTS) ACF
     * - switch to use different (CTS) ACF - verify calls to ACF use the newer/expected ACF - restore original ACF
     */
    @Test
    public void ACFGetFactory() {
        boolean passed = false;
        try {
            commonTests._ACF_getFactory();
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     *
     * @keywords: jaspic_soap
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
        boolean passed = false;
        try {
            commonTests._ACFSwitchFactorys(vendorACFClass);
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: testACFComesFromSecFile
     *
     * @assertion_ids: JASPIC:SPEC:329; JASPIC:SPEC:330;
     *
     * @test_Strategy: This is calling a method on the server(actually servlet) side that will invoke getFactory() to verify
     * a non-null facotry instance is returned. It will also verify that the authconfigprovider.factory security property is
     * properly set/used.
     *
     */
    @Test
    public void testACFComesFromSecFile() {
        boolean passed = false;
        try {
            commonTests._testACFComesFromSecFile();
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACFPersistentRegisterOnlyOneACP
     *
     * @assertion_ids: JASPIC:SPEC:329; JASPIC:SPEC:331; JASPIC:SPEC:332; JASPIC:SPEC:340; JASPIC:SPEC:341;
     *
     * @test_Strategy: This will make a server (actually servlet) side method call that will do the following: - load
     * vendors ACF - (persistent) register of CTS ACP's in the vendors ACF - get list of vendors registered provider ID's -
     * register another persistent ACP (this is standalone ACP profile) - verify another regId was added for standalone ACP
     * - try to re-register (persistently) standalone provider - verify 2nd attempt at added standalone provider REPLACED
     * the first but it should NOT have added another nor failed. - also confirm that regID for standalone ACP stayed the
     * same after 1st and 2nd attempt to register standalone ACP - verify that the 2nd re-registering of ACP replaced the
     * ACP AND the description. - unregister standalone ACP and setFactory back to CTS default
     *
     */
    @Test
    public void ACFPersistentRegisterOnlyOneACP() {
        boolean passed = false;
        try {
            commonTests._ACFRegisterOnlyOneACP(logFileLocation, providerConfigFileLocation, vendorACFClass, true);
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACFInMemoryRegisterOnlyOneACP
     *
     * @assertion_ids: JASPIC:SPEC:334; JASPIC:SPEC:342; JASPIC:SPEC:343;
     *
     * @test_Strategy: This will make a server (actually servlet) side method call that will do the following: - load
     * vendors ACF - (persistent) register of CTS ACP's in the vendors ACF - get list of vendors registered provider ID's -
     * register (in-memory) ACP (this is standalone ACP profile) - verify another regId was added for standalone ACP - try
     * to re-register (in-memory) standalone provider - verify 2nd attempt at added standalone provider REPLACED the first
     * but it should NOT have added another nor failed. - also confirm that regID for standalone ACP stayed the same after
     * 1st and 2nd attempt to register standalone ACP - verify that the 2nd re-registering of ACP replaced the ACP AND the
     * description. - unregister standalone ACP and setFactory back to CTS default
     *
     */
    @Test
    public void ACFInMemoryRegisterOnlyOneACP() {
        boolean passed = false;
        try {
            commonTests._ACFRegisterOnlyOneACP(logFileLocation, providerConfigFileLocation, vendorACFClass, false);
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: ACFUnregisterACP
     *
     * @assertion_ids: JASPIC:SPEC:344;
     *
     * @test_Strategy: This will make a server (actually servlet) side method call that will do the following: - load
     * vendors ACF - (persistent) register of CTS ACP's in the vendors ACF - get list of vendors registered provider ID's -
     * register (in-memory) ACP (this is standalone ACP profile) - verify another regId was added for standalone ACP -
     * unregister the in-memory ACP we just registered - verify removeRegistration() method call returned proper boolean -
     * verify expected # of registry eentries - verify 2nd call to removeRegistration() with regId that was previously
     * removed and should expect return val of false
     *
     */
    @Test
    public void ACFUnregisterACP() {
        boolean passed = false;
        try {
            commonTests._ACFUnregisterACP(logFileLocation, providerConfigFileLocation, vendorACFClass);
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     *
     * @keywords: jaspic_soap
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
        boolean passed = false;
        try {
            commonTests._ACFRemoveRegistrationWithBadId();
            passed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertTrue(passed);
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: getRegistrationContextId
     *
     * @assertion_ids: JASPIC:JAVADOC:77
     *
     * @test_Strategy: 1. Get System properties log.file.location, provider.configuration.file and vendor.authconfig.factory
     *
     * 2. Use the system properties to read the TestSuite providers defined in ProviderConfigruation.xml file and register
     * them with vendor's authconfig factory.
     *
     *
     * Description This will use an appContext value that was used to register a provider, and it will see if it can use the
     * AuthConfigFactory.RegistrationContext API to try and access the same appContext value that was used during the
     * registration process.
     *
     */
    @Test
    public void getRegistrationContextId() {
        String appContext = "localhost /Hello_web/Hello";

        // register providers in vendor factory
        assertTrue(register(logFileLocation, providerConfigFileLocation, vendorACFClass));

        // verify we can access a given provider (any provider) appcontext id
        boolean bVerified = false;

        AuthConfigFactory acf = null;

        try {
            acf = AuthConfigFactory.getFactory();
        } catch (SecurityException ex) {
            // if here we may not have permission to invoke ACF.getFactory...
            String msg = "SecurityException:  make sure you have permission to call ACF.getFactory.";
            msg = msg + " Check your server side security policies.";
            logger.info(msg);
            ex.printStackTrace();
        }

        assertNotNull(
            "getRegistrationContextId failed : could not get acf",
            acf);


        String[] regIDs = acf.getRegistrationIDs(null);
        for (int ii = 0; ii < regIDs.length; ii++) {
            // loop through the ACF's registration ids

            if (regIDs[ii] != null) {
                AuthConfigFactory.RegistrationContext acfReg;
                acfReg = acf.getRegistrationContext(regIDs[ii]);
                if (acfReg != null) {
                    logger.info("appContext = " + appContext);
                    logger.info("acfReg.getAppContext() = " + acfReg.getAppContext());
                    logger.info("layer = " + acfReg.getMessageLayer());
                    String str = acfReg.getAppContext();
                    if ((str != null) && (str.equals(appContext))) {
                        // we found our provider info
                        logger.info("Found it : RegistrationID for our ACP=" + regIDs[ii]);
                        bVerified = true;
                        break;
                    }
                }
            }
        }

        String msg = "Could not find appContext=" + appContext;
        msg += " in the ACF's list of registration id info";

        assertTrue(msg, bVerified);

        logger.info("TestSuite Providers registration successful");
    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: AuthConfigFactoryRegistration
     *
     * @assertion_ids: JASPIC:JAVADOC:80
     *
     * @test_Strategy: 1. Get System properties log.file.location, provider.configuration.file and vendor.authconfig.factory
     *
     * 2. Use the system properties to read the TestSuite providers defined in ProviderConfigruation.xml file and register
     * them with vendor's authconfig factory.
     *
     *
     * Description
     *
     *
     */
    @Test
    public void AuthConfigFactoryRegistration() {
        assertTrue(
            register(logFileLocation, providerConfigFileLocation, vendorACFClass));
    }

    public boolean register(String logFileLocation, String providerConfigFileLocation, String vendorACFClass) {
        try {
            printVerticalIndent();

            // Get an instance of Vendor's AuthConfigFactory
            AuthConfigFactory vendorFactory = getVendorAuthConfigFactory(vendorACFClass);

            // Set vendor's AuthConfigFactory
            AuthConfigFactory.setFactory(vendorFactory);

            // Get system default AuthConfigFactory
            AuthConfigFactory systemAuthConfigFactory = AuthConfigFactory.getFactory();

            if (systemAuthConfigFactory != null) {
                logger.info("Default AuthConfigFactory class name = " + systemAuthConfigFactory.getClass().getName());

            } else {
                logger.severe("Default AuthConfigFactory is null" + " can't register TestSuite Providers with null");
                return false;
            }

            /**
             * Read the ProviderConfiguration XML file This file contains the list of providers that needs to be loaded by the
             * vendor's default AuthConfigFactory
             */
            providerConfigurationEntriesCollection = readProviderConfigurationXMLFile();

            ProviderConfigurationEntry pce = null;

            printVerticalIndent();
            Iterator<ProviderConfigurationEntry> iterator = providerConfigurationEntriesCollection.iterator();
            while (iterator.hasNext()) {
                // obtain each ProviderConfigurationEntry and register it
                // with vendor's default AuthConfigFactory
                pce = iterator.next();

                if (pce != null) {
                    logger.info("Registering Provider " + pce.getProviderClassName() + " ...");
                    systemAuthConfigFactory.registerConfigProvider(
                        pce.getProviderClassName(),
                        pce.getProperties(),
                        pce.getMessageLayer(),
                        pce.getApplicationContextId(),
                        pce.getRegistrationDescription());

                    logger.info("Registration Successful");
                }
            }

            printVerticalIndent();

            // Check whether the providers are registered for the right message layer
            // and appContext
            // verifyRegistrations(acf);

        } catch (SecurityException ex) {
            // if here we may not have permission to invoke ACF.getFactory...
            String msg = "SecurityException:  make sure you have permission to call ACF.getFactory";
            msg = msg + "or ACF.setFactory().  Check your server side security policies.";
            logger.info(msg);
            ex.printStackTrace();

        } catch (Exception e) {
            logger.info("Error Registering TestSuite AuthConfig Providers");
            e.printStackTrace();
        }

        return true;

    }

    /**
     * @keywords: jaspic_soap
     *
     * @testName: AuthConfigFactoryVerifyPersistence
     *
     * @assertion_ids: JASPIC:JAVADOC:75
     *
     * @test_Strategy: 1. Get System properties log.file.location, provider.configuration.file and vendor.authconfig.factory
     *
     * 2. Load vendor's AuthConfigFactory and make sure the registered providers return properly for the right message layer
     * and appContextId
     *
     * Note: We test the persistance behaviour for vendor's AuthConfigFactory by registering providers from a persisted
     * file, then we verify the registrations went correctly.
     *
     * Description
     *
     *
     */
    public void AuthConfigFactoryVerifyPersistence() {
        try {

            // register providers in vendor factory
            assertTrue(register(logFileLocation, providerConfigFileLocation, vendorACFClass));

            // Get system default AuthConfigFactory
            AuthConfigFactory acf = AuthConfigFactory.getFactory();

            if (acf != null) {
                logger.info("Default AuthConfigFactory class name = " + acf.getClass().getName());

                assertTrue(verifyRegistrations(acf));

            } else {
                logger.severe("Default AuthConfigFactory is null" + " can't verify registrations for TestSuite Providers");
            }
        } catch (SecurityException ex) {
            // if here we may not have permission to invoke ACF.getFactory...
            String msg = "SecurityException:  make sure you have permission to call ACF.getFactory.";
            msg = msg + " Check your server side security policies.";
            logger.info(msg);
            ex.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    *
    * @keywords: jaspic_soap
    *
    * @testName: AuthConfigFactorySetFactory
    *
    * @assertion_ids: JASPIC:SPEC:7; JASPIC:SPEC:10; JASPIC:JAVADOC:87; JASPIC:JAVADOC:80
    *
    * @test_Strategy: 1. Use the static setFactory method to set an ACF and this should always work.
    *
    * Description This method uses the getFactory() method to get the current factory, then it uses the setFactory() to
    * change the current ACF. This method then verifies that the ACF's were indeed changed. We know that the setFactory()
    * works as it is used in the bootstrap process - but this is an additional level of testing that allows us to set the
    * factory in a slightly different manner than the bootstrap (eg reading it out of the java.security file.
    */
   @Test
   public void AuthConfigFactorySetFactory() {
       // Get current AuthConfigFactory
       AuthConfigFactory currentAcf = AuthConfigFactory.getFactory();

       assertNotNull(
           "FAILURE - Could not get current AuthConfigFactory.",
           currentAcf);

       String currentACFClass = currentAcf.getClass().getName();
       logger.info("currentACFClass = " + currentACFClass);

       // Set our ACF to a new/different AuthConfigFactory
       TSAuthConfigFactoryForStandalone newAcf = new TSAuthConfigFactoryForStandalone();
       AuthConfigFactory.setFactory(newAcf);
       String newACFClass = newAcf.getClass().getName();
       logger.info("newACFClass = " + newACFClass);

       // Verify our calls to getFactory() are getting the newly set factory
       // instance and not the original ACF instance
       AuthConfigFactory testAcf = AuthConfigFactory.getFactory();

       assertNotNull(
           "FAILURE - Could not get newly set AuthConfigFactory.",
           testAcf);

       String newlySetACFClass = testAcf.getClass().getName();
       logger.info("newlySetACFClass = " + newlySetACFClass);

       assertEquals(
           "FAILURE - our current ACF does not match our newly set ACF",
           newACFClass, newlySetACFClass);

       logger.info("newlySetACFClass == newACFClass == " + newACFClass);

       // Restore original factory class
       AuthConfigFactory.setFactory(currentAcf);

       logger.info("AuthConfigFactorySetFactory : PASSED");
   }



    private void printVerticalIndent() {
        logger.info("**********************************************************");
        logger.info("\n");
    }

    /**
     * This method instantiates and returns a AuthConfigFactory based on the specified className
     */
    private AuthConfigFactory getVendorAuthConfigFactory(String className) {
        AuthConfigFactory vFactory = null;

        if (className != null) {
            try {
                vFactory = (AuthConfigFactory)
                    Class.forName(className, true, Thread.currentThread()
                         .getContextClassLoader())
                         .getDeclaredConstructor()
                         .newInstance();
                logger.info("Instantiated Vendor's AuthConfigFactory");

            } catch (Exception e) {
                logger.info("Error instantiating vendor's " + "AuthConfigFactory class :" + className);
                e.printStackTrace();

            }
        }

        return vFactory;
    }

    /*
     * Read the provider configuration XML file and registers each provider with Vendor's default AuthConfigFactory
     */
    private Collection<ProviderConfigurationEntry> readProviderConfigurationXMLFile() {
        Collection<ProviderConfigurationEntry> providerConfigurationEntriesCollection = null;

        logger.info("Reading TestSuite Providers from :" + providerConfigFileLocation);
        try {
            // Given the provider configuration xml file
            // This reader parses the xml file and stores the configuration
            // entries as a collection.
            configFileProcessor = new ProviderConfigurationXMLFileProcessor(providerConfigFileLocation);

            // Retrieve the ProviderConfigurationEntries collection
            providerConfigurationEntriesCollection = configFileProcessor.getProviderConfigurationEntriesCollection();

            logger.info("TestSuite Providers read successfully " + "from ProviderConfiguration.xml file");

            return providerConfigurationEntriesCollection;

        } catch (Exception e) {
            logger.info("Error loading Providers");
            e.printStackTrace();
        }

        return null;
    }

    private boolean verifyRegistrations(AuthConfigFactory acf) {
        logger.info("Verifying Provider Registrations ...");

        try {
            // Create a Registration listener
            RegistrationListener rlis = new TSRegistrationListener();

            // Get AuthConfigProvider for soap layer
            AuthConfigProvider acp = acf.getConfigProvider(JASPICData.LAYER_SOAP, soapAppContext, rlis);

            if (acp != null) {
                if (acp.getClass().getName().equals("com.sun.ts.tests.jaspic.tssv.config.TSAuthConfigProvider")) {
                    logger.info("TSAuthConfigProvider registered for" + " message layer=SOAP" + " and appContextId=" + soapAppContext);
                } else {
                    logger.info("Wrong provider registerd for " + " message layer=SOAP" + " and appContextId=" + soapAppContext);
                    return false;

                }

            } else {
                logger.info(
                    "Error : No AuthConfigprovider registerd for" + " message layer=SOAP" + " and appContextId=" + soapAppContext);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }
}
