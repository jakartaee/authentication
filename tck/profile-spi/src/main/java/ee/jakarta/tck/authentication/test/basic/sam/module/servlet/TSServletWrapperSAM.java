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

package ee.jakarta.tck.authentication.test.basic.sam.module.servlet;

import static java.util.logging.Level.INFO;

import ee.jakarta.tck.authentication.test.basic.sam.ServerCallbackSupport;
import ee.jakarta.tck.authentication.test.basic.servlet.JASPICData;
import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

public class TSServletWrapperSAM implements ServerAuthModule {
    private TSLogger logger;

    private static CallbackHandler callbackHandler;

    private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

    /**
     * Creates a new instance of TSServletWrapperSAM
     */
    public TSServletWrapperSAM() {
        logger = TSLogger.getTSLogger(JASPICData.LOGGER_NAME);
        logMsg("TSServletWrapperSAM() constructor called");
    }

    public TSServletWrapperSAM(TSLogger log) {
        if (log != null) {
            logger = log;
        } else {
            logger = TSLogger.getTSLogger(JASPICData.LOGGER_NAME);
        }
        logMsg("TSServletWrapperSAM(TSLogger) constructor called");
    }

    /**
     * Initialize this module with request and response message policies to enforce, a CallbackHandler, and any
     * module-specific configuration properties.
     *
     * <p>
     * The request policy and the response policy must not both be null.
     *
     * @param requestPolicy the request policy this module must enforce, or null.
     *
     * @param responsePolicy the response policy this module must enforce, or null.
     *
     * @param handler CallbackHandler used to request information.
     *
     * @param options a Map of module-specific configuration properties.
     *
     * @exception AuthException if module initialization fails, including for the case where the options argument contains
     * elements that are not supported by the module.
     */
    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map<String, Object> options) throws AuthException {
        if ((options != null) && options.get("TSLogger") != null) {
            logger = (TSLogger) options.get("TSLogger");
        }

        callbackHandler = handler;

        // perform some checking to support assertion JASPI:SPEC:87
        verifyRequestPolicy(requestPolicy);

        logger.log(INFO, "CBH for HttpServlet supports type: " + handler.getClass().getName());
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        doServerCallbackChecks(messageInfo, clientSubject, serviceSubject);

        // Wrap the request - the resource to be invoked should get to see this
        TSRequestWrapper tswrap = new TSRequestWrapper((HttpServletRequest) messageInfo.getRequestMessage());
        tswrap.setOptionsMap(messageInfo.getMap());
        messageInfo.setRequestMessage(tswrap);

        // Wrap the response - the resource to be invoked should get to see this
        messageInfo.setResponseMessage(new TSResponseWrapper((HttpServletResponse) messageInfo.getResponseMessage()));

        return AuthStatus.SUCCESS;
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

        // Unwrap the request
        if (request instanceof TSRequestWrapper) {
            messageInfo.setRequestMessage(((TSRequestWrapper) request).getRequest());
        } else {
            logMsg("Incorrect request type : " + request.getClass().getName());
        }

        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        if (response instanceof TSResponseWrapper) {
            messageInfo.setResponseMessage(((TSResponseWrapper) response).getResponse());
        } else {
            logMsg("Incorrect response type : " + response.getClass().getName());
        }

        return AuthStatus.SEND_SUCCESS;
    }

    /*
     * This is a convenience method that will do some verification on the request policy to see if it complies with
     * assertion JASPI:SPEC:87. If there are any problems found, appropriate log statements will be made and searched for
     * later on in the Client code.
     */
    private void verifyRequestPolicy(MessagePolicy requestPolicy) {

        String errStr = "Layer=" + JASPICData.LAYER_SERVLET;
        errStr += " requestPolicy=invalid in TSServletWrapperSAM.initialize()";

        if (requestPolicy == null) {
            // we should never have a null requestpolicy here
            logger.log(Level.SEVERE, errStr);
        } else {
            MessagePolicy.TargetPolicy[] tp = requestPolicy.getTargetPolicies();
            if (tp.length < 1) {
                // must return an array containing at least one TargetPolicy
                logger.log(INFO, errStr);
            } else {
                for (int ii = 0; ii < tp.length; ii++) {
                    MessagePolicy.ProtectionPolicy pp = tp[ii].getProtectionPolicy();
                    if ((pp != null) && (!isProtectionPolicyIDValid(pp.getID()))) {
                        String str = "Layer=" + JASPICData.LAYER_SERVLET;
                        str += " Invalid ProtectionPolicy.getID()";
                        logger.log(INFO, str);
                    }
                }
            }
        }
    }

    /*
     * (spec section 3.7.4) For servlet profile, calling the getID() method on the ProtectionPolicy must return one of the
     * following values: ProtectionPolicy.AUTHENTICATE_SENDER ProtectionPolicy.AUTHENTICATE_CONTENT
     */
    public boolean isProtectionPolicyIDValid(String strId) {
        boolean bval = false;

        if ((strId.equals(MessagePolicy.ProtectionPolicy.AUTHENTICATE_CONTENT))
                || (strId.equals(MessagePolicy.ProtectionPolicy.AUTHENTICATE_SENDER))) {
            bval = true;
        }

        return bval;
    }


    /**
     * Remove method specific principals and credentials from the subject.
     *
     * @param messageInfo a contextual object that encapsulates the client request and server response objects, and that may
     * be used to save state across a sequence of calls made to the methods of this interface for the purpose of completing
     * a secure message exchange.
     *
     * @param subject the Subject instance from which the Principals and credentials are to be removed.
     *
     * @exception AuthException if an error occurs during the Subject processing.
     */
    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        logMsg("TSServletWrapperSAM.cleanSubject called");
        subject = null;
    }

    private void logMsg(String str) {
        if (logger != null) {
            logger.log(INFO, str);
        } else {
            System.out.println("*** TSLogger Not Initialized properly ***");
            System.out.println("*** TSSVLogMessage : ***" + str);
        }
    }

    private void doServerCallbackChecks(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        String servletPath = request.getContextPath() + request.getServletPath();
        String msg = "";

        ServerCallbackSupport serverCallbacks = new ServerCallbackSupport(logger, callbackHandler, JASPICData.LAYER_SERVLET, messageInfo,
                clientSubject, serviceSubject);

        // instead of calling all callbacks in one method, lets call them
        // all individually so ithat we can check return values of each.
        // serverCallbacks.verify();

        if (serverCallbacks.verifyCPCCallback()) {
            msg = "TSServletWrapperSAM.validateRequest(): verifyCPCCallback returned true";
        } else {
            msg = "TSServletWrapperSAM.validateRequest(): verifyCPCCallback returned false";
        }
        msg += " for servletPath = " + servletPath;
        logMsg(msg);

        if (serverCallbacks.verifyGPCCallback()) {
            msg = "TSServletWrapperSAM.validateRequest(): verifyGPCCallback returned true";
        } else {
            msg = "TSServletWrapperSAM.validateRequest(): verifyGPCCallback returned false";
        }
        msg += " for servletPath = " + servletPath;
        logMsg(msg);

        if (serverCallbacks.verifyPVCCallback()) {
            msg = "TSServletWrapperSAM.validateRequest(): verifyPVCCallback returned true";
        } else {
            msg = "TSServletWrapperSAM.validateRequest(): verifyPVCCallback returned false";
        }
        msg += " for servletPath = " + servletPath;
        logMsg(msg);

        return;
    }

}
