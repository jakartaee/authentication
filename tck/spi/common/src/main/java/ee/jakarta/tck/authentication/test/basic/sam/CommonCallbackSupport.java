/*
 * Copyright (c) 2024 Contributors to Eclipse Foundation.
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

package ee.jakarta.tck.authentication.test.basic.sam;

import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.security.auth.message.callback.CertStoreCallback;
import jakarta.security.auth.message.callback.PrivateKeyCallback;
import jakarta.security.auth.message.callback.SecretKeyCallback;
import jakarta.security.auth.message.callback.TrustStoreCallback;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 *
 * @author Raja Perumal
 */
public class CommonCallbackSupport {
    private static TSLogger logger;
    private static CallbackHandler callbackHandler;
    private static String profile;
    private static String runtimeType;

    public CommonCallbackSupport(TSLogger tsLogger, CallbackHandler cbkHandler, String profile, String runtimeType) {
        logger = tsLogger;
        callbackHandler = cbkHandler;

        CommonCallbackSupport.profile = profile;
        CommonCallbackSupport.runtimeType = runtimeType;
    }

    public boolean verify() {
        try {
            CertStoreCallbackSupport();
            PrivateKeyCallbackSupport();
            SecretKeyCallbackSupport();
            TrustStoreCallbackSupport();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void CertStoreCallbackSupport() {
        if (callbackHandler != null) {
            try {
                CertStoreCallback certStoreCallback = new CertStoreCallback();
                Callback[] callbacks = new Callback[] { certStoreCallback };

                callbackHandler.handle(callbacks);
                CertStore certStore = certStoreCallback.getCertStore();

                if (certStore != null) {
                    logMsg("CertStore type =" + certStore.getType());
                }
                logMsg("CallbackHandler supports CertStoreCallback");

            } catch (UnsupportedCallbackException | IOException e) {
                logMsg("CallbackHandler failed to support CertStoreCallback :" + e.getMessage());
                e.printStackTrace();
            }

        }

    }

    private void PrivateKeyCallbackSupport() {
        if (callbackHandler != null) {
            try {

                PrivateKeyCallback.AliasRequest aliasRequest = new PrivateKeyCallback.AliasRequest("s1as");

                PrivateKeyCallback privateKeyCallback = new PrivateKeyCallback(aliasRequest);

                Callback[] callbacks = new Callback[] { privateKeyCallback };

                callbackHandler.handle(callbacks);

                PrivateKey privateKey = privateKeyCallback.getKey();

                if (privateKey != null) {
                    logMsg("Private Key for s1as =" + privateKey.getAlgorithm());
                }
                logMsg("CallbackHandler supports PrivateKeyCallback");
            } catch (UnsupportedCallbackException | IOException usce) {
                logMsg("CallbackHandler failed to support PrivateKeyCallback :" + usce.getMessage());
                usce.printStackTrace();
            }

        }
    }

    private void SecretKeyCallbackSupport() {
        if (callbackHandler != null) {
            try {

                SecretKeyCallback.AliasRequest aliasRequest = new SecretKeyCallback.AliasRequest("s1as");

                SecretKeyCallback secretKeyCallback = new SecretKeyCallback(aliasRequest);

                Callback[] callbacks = new Callback[] { secretKeyCallback };

                callbackHandler.handle(callbacks);

                SecretKey secretKey = secretKeyCallback.getKey();

                if (secretKey != null) {
                    logMsg("Secret Key for s1as =" + secretKey.getAlgorithm());
                }
                logMsg("CallbackHandler supports SecretKeyCallback");
            } catch (UnsupportedCallbackException | IOException usce) {
                logMsg("CallbackHandler failed to support secretKeyCallback :" + usce.getMessage());
                usce.printStackTrace();
            }

        }

    }

    private void TrustStoreCallbackSupport() {
        if (callbackHandler != null) {
            try {
                TrustStoreCallback trustStoreCallback = new TrustStoreCallback();
                Callback[] callbacks = new Callback[] { trustStoreCallback };

                callbackHandler.handle(callbacks);
                KeyStore trustStore = trustStoreCallback.getTrustStore();

                if (trustStore != null) {
                    logMsg("TrustStore type =" + trustStore.getType());
                }
                logMsg("CallbackHandler supports TrustStoreCallback");
            } catch (UnsupportedCallbackException | IOException usce) {
                logMsg("CallbackHandler failed to support TrustStoreCallback :" + usce.getMessage());
                usce.printStackTrace();
            }
        }
    }

    public void logMsg(String str) {
        if (logger != null) {
            logger.log(Level.INFO, "In " + profile + " : " + runtimeType + " " + str);
        } else {
            System.out.println("*** TSLogger Not Initialized properly ***");
            System.out.println("*** TSSVLogMessage : ***" + str);
        }
    }
}
