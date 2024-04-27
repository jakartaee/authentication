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
package ee.jakarta.tck.authentication.test.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ee.jakarta.tck.authentication.test.basic.sam.ProviderConfigurationEntry;
import ee.jakarta.tck.authentication.test.basic.sam.ProviderConfigurationXMLFileProcessor;
import ee.jakarta.tck.authentication.test.basic.sam.TSAuthConfigProviderServlet;
import ee.jakarta.tck.authentication.test.basic.sam.config.TSAuthConfigFactoryForStandalone;
import ee.jakarta.tck.authentication.test.basic.servlet.IdUtil;
import ee.jakarta.tck.authentication.test.basic.servlet.JASPICData;
import jakarta.security.auth.message.config.AuthConfigFactory;
import jakarta.security.auth.message.config.AuthConfigProvider;
import java.util.Collection;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * A small collection of unit tests for testing the AuthConfigFactory.
 *
 * <p>
 * This only uses code directly in the test context, and does not start up a server.
 *
 * @author Arjan Tijms
 *
 */
public class ServletUnitTest {

    Logger logger = Logger.getLogger(ServletUnitTest.class.getName());

    /**
    *
    * @keywords: jaspic_servlet
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

       try {
           // Set our AuthConfigFactory to a new/different AuthConfigFactory
           TSAuthConfigFactoryForStandalone newAuthConfigFactory = new TSAuthConfigFactoryForStandalone();
           AuthConfigFactory.setFactory(newAuthConfigFactory);
           String newAuthConfigFactoryClass = newAuthConfigFactory.getClass().getName();

           // Verify our calls to getFactory() are getting the newly set factory
           // instance and not the original ACF instance
           AuthConfigFactory testAuthConfigFactory = AuthConfigFactory.getFactory();
           assertNotNull(testAuthConfigFactory);

           String newlySetACFClass = testAuthConfigFactory.getClass().getName();

           assertEquals("FAILURE - our current ACF does not match our newly set ACF",newlySetACFClass, newAuthConfigFactoryClass);

       } catch (SecurityException ex) {
           ex.printStackTrace();
       }
   }

   /**
    * @keywords: jaspic_servlet
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
       String appContext = System.getProperty("logical.hostname.servlet") + " /spitests_servlet_web";

       // register providers in vendor factory
       assertTrue(register(
               System.getProperty("log.file.location"),
               System.getProperty("provider.configuration.file"),
               System.getProperty("vendor.authconfig.factory")));

       // verify we can access a given provider (any provider) appcontext id
       boolean bVerified = false;

       AuthConfigFactory authConfigFactory = null;

       try {
           authConfigFactory = AuthConfigFactory.getFactory();
       } catch (SecurityException ex) {
           // if here we may not have permission to invoke ACF.getFactory...
           String msg = "SecurityException:  make sure you have permission to call ACF.getFactory.";
           msg = msg + " Check your server side security policies.";
           logger.info(msg);
           ex.printStackTrace();
       }

       assertNotNull(
           "getRegistrationContextId():  Error - call to getFactory() returned null ACF.",
           authConfigFactory);

       String[] regIDs = authConfigFactory.getRegistrationIDs(null);
       for (int ii = 0; ii < regIDs.length; ii++) {
           // loop through the AuthConfigFactory's registration ids

           if (regIDs[ii] != null) {
               AuthConfigFactory.RegistrationContext acfReg;
               acfReg = authConfigFactory.getRegistrationContext(regIDs[ii]);
               if (acfReg != null) {
                   logger.info("appContext = " + appContext);
                   logger.info("acfReg.getAppContext() = " + acfReg.getAppContext());
                   logger.info("layer = " + acfReg.getMessageLayer());
                   String str = acfReg.getAppContext();
                   if ((str != null) && (str.equals(appContext))) {
                       // We found our provider info
                       logger.info("Found it : RegistrationID for our ACP=" + regIDs[ii]);
                       bVerified = true;
                       break;
                   }
               }
           }
       }

       assertTrue(
           "Could not find appContext=" + appContext + " in the ACF's list of registration id info",
           bVerified);
   }

   /**
    * @keywords: jaspic_servlet
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
   @Test
   public void AuthConfigFactoryVerifyPersistence() {
       boolean verified = false;

       try {
           // Register providers in vendor factory
           assertTrue(register(
                   System.getProperty("log.file.location"),
                   System.getProperty("provider.configuration.file"),
                   System.getProperty("vendor.authconfig.factory")));

           // Get system default AuthConfigFactory
           AuthConfigFactory acf = AuthConfigFactory.getFactory();

           if (acf != null) {
               logger.info("Default AuthConfigFactory class name = " + acf.getClass().getName());

               verified = verifyRegistrations(acf);

           } else {
               logger.info("Default AuthConfigFactory is null" + " can't verify registrations for TestSuite Providers");
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

       assertTrue(
           "AuthConfigFactoryPersistence failed",
           verified);
   }

   private boolean register(String logFileLocation, String providerConfigurationFileLocation, String vendorAuthConfigFactoryClass) {
       try {
           printVerticalIndent();

           // Get an instance of Vendor's AuthConfigFactory
           AuthConfigFactory vendorFactory = getAuthConfigFactory(vendorAuthConfigFactoryClass);

           // Set vendor's AuthConfigFactory
           AuthConfigFactory.setFactory(vendorFactory);

           // Get system default AuthConfigFactory (which we just set to vendor)
           AuthConfigFactory authConfigFactory = AuthConfigFactory.getFactory();

           if (authConfigFactory != null) {
               logger.info("Default AuthConfigFactory class name = " + authConfigFactory.getClass().getName());

           } else {
               logger.info("Default AuthConfigFactory is null" + " can't register TestSuite Providers with null");
               return false;
           }

           /**
            * Read the ProviderConfiguration XML file This file contains the list of providers that needs to be loaded by the
            * vendor's default AuthConfigFactory
            */
           Collection<ProviderConfigurationEntry> providerConfigurationEntries =
               readProviderConfigurationXMLFile(providerConfigurationFileLocation);


           printVerticalIndent();

           for (ProviderConfigurationEntry providerConfigurationEntry : providerConfigurationEntries) {
               // Obtain each ProviderConfigurationEntry and register it with vendor's default AuthConfigFactory

               if (providerConfigurationEntry != null) {
                   logger.info("Registering Provider " + providerConfigurationEntry.getProviderClassName() + " ...");
                   authConfigFactory.registerConfigProvider(
                           providerConfigurationEntry.getProviderClassName(),
                           providerConfigurationEntry.getProperties(),
                           providerConfigurationEntry.getMessageLayer(),
                           providerConfigurationEntry.getApplicationContextId(),
                           providerConfigurationEntry.getRegistrationDescription());
                   logger.info("Registration Successful");
               }
           }

           printVerticalIndent();

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
    * This method instantiates and ruturns a AuthConfigFactory based on the specified className
    */
   private AuthConfigFactory getAuthConfigFactory(String className) {
       AuthConfigFactory authConfigFactory = null;

       if (className != null) {
           try {
               authConfigFactory = (AuthConfigFactory)
                       Class.forName(className, true, Thread.currentThread().getContextClassLoader())
                            .getDeclaredConstructor()
                            .newInstance();
               logger.info("Instantiated AuthConfigFactory for: " + className);

           } catch (Exception e) {
               logger.info("Error instantiating vendor's " + "AuthConfigFactory class :" + className);
               e.printStackTrace();

           }
       }

       return authConfigFactory;
   }

   /*
    * Read the provider configuration XML file and registers each provider with Vendor's default AuthConfigFactory
    */
   private Collection<ProviderConfigurationEntry> readProviderConfigurationXMLFile(String providerConfigurationFileLocation) {
       Collection<ProviderConfigurationEntry> providerConfigurationEntriesCollection = null;

       logger.info("Reading TestSuite Providers from :" + providerConfigurationFileLocation);
       try {
           // Given the provider configuration xml file
           // This reader parses the xml file and stores the configuration
           // entries as a collection.
           ProviderConfigurationXMLFileProcessor configFileProcessor =
               new ProviderConfigurationXMLFileProcessor(providerConfigurationFileLocation);

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

   private boolean verifyRegistrations(AuthConfigFactory authConfigFactory) {
       logger.info("Verifying Provider Registrations ...");

       String servletAppContext = IdUtil.getAppContextId(JASPICData.LAYER_SERVLET);

       try {
           // Get AuthConfigProviderServlet
           AuthConfigProvider servletAuthConfigProvider =
               authConfigFactory.getConfigProvider(JASPICData.LAYER_SERVLET, servletAppContext, null);

           if (servletAuthConfigProvider != null) {
               if (servletAuthConfigProvider.getClass().getName().equals(TSAuthConfigProviderServlet.class.getName())) {
                   logger.info("TSAuthConfigProviderServlet registered for" + " message layer=HttpServlet" + " and appContextId="
                           + servletAppContext);
               } else {
                   logger.info("Wrong provider registerd for " + " message layer=HttpServlet" + " and appContextId=" + servletAppContext);
                   return false;
               }

           } else {
               logger.info("Error : No AuthConfigprovider registerd for" + " message layer=HttpServlet" + " and appContextId="
                       + servletAppContext);
               return false;
           }

       } catch (Exception e) {
           e.printStackTrace();
       }

       return true;
   }

   private void printVerticalIndent() {
       logger.info("**********************************************************");
       logger.info("\n");

   }


}