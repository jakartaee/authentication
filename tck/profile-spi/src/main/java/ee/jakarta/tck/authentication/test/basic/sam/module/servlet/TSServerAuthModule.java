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

package ee.jakarta.tck.authentication.test.basic.sam.module.servlet;

import static java.util.logging.Level.INFO;

import ee.jakarta.tck.authentication.test.basic.sam.CommonCallbackSupport;
import ee.jakarta.tck.authentication.test.basic.sam.ServerCallbackSupport;
import ee.jakarta.tck.authentication.test.basic.servlet.JASPICData;
import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

/**
 *
 * @author Sun Microsystems
 */
public class TSServerAuthModule implements jakarta.security.auth.message.module.ServerAuthModule {
    private TSLogger logger;

    private static CallbackHandler callbackHandler;

    /**
     * Creates a new instance of TSServerAuthModule
     */
    public TSServerAuthModule() {
        logger = TSLogger.getTSLogger(JASPICData.LOGGER_NAME);
        logMsg("TSServerAuthModule() constructor called");
    }

    public TSServerAuthModule(TSLogger log) {
        if (log != null) {
            logger = log;
        } else {
            logger = TSLogger.getTSLogger(JASPICData.LOGGER_NAME);
        }
        logMsg("TSServerAuthModule(TSLogger) constructor called");
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
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options)
            throws AuthException {

        if ((options != null) && options.get("TSLogger") != null) {
            logger = (TSLogger) options.get("TSLogger");
        }

        callbackHandler = handler;

        // perform some checking to support assertion JASPI:SPEC:87
        verifyRequestPolicy(requestPolicy);

        logger.log(INFO, "CBH for HttpServlet supports type: " + handler.getClass().getName());
    }

    /*
     * This is a convenience method that will do some verification on the request policy to see if it complies with
     * assertion JASPI:SPEC:87. If there are any problems found, appropriate log statements will be made and searched for
     * later on in the Client code.
     */
    private void verifyRequestPolicy(MessagePolicy requestPolicy) {

        String errStr = "Layer=" + JASPICData.LAYER_SERVLET;
        errStr += " requestPolicy=invalid in TSServerAuthModule.initialize()";

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
     * Get the one or more Class objects representing the message types supported by the module.
     *
     * @return an array of Class objects, with at least one element defining a message type supported by the module.
     */
    @Override
    public Class[] getSupportedMessageTypes() {
        logMsg("TSServerAuthModule.getSupportedMessageTypes called");
        Class[] classarray = { jakarta.servlet.http.HttpServletRequest.class, jakarta.servlet.http.HttpServletResponse.class };
        return classarray;
    }

    /**
     * Authenticate a received service request.
     *
     * This method is called to transform the mechanism specific request message acquired by calling getRequestMessage (on
     * messageInfo) into the validated application message to be returned to the message processing runtime. If the received
     * message is a (mechanism specific) meta-message, the method implementation must attempt to transform the meta-message
     * into a corresponding mechanism specific response message, or to the validated application request message. The
     * runtime will bind a validated application message into the the corresponding service invocation.
     * <p>
     * This method conveys the outcome of its message processing either by returning an AuthStatus value or by throwing an
     * AuthException.
     *
     * @param messageInfo a contextual object that encapsulates the client request and server response objects, and that may
     * be used to save state across a sequence of calls made to the methods of this interface for the purpose of completing
     * a secure message exchange.
     *
     * @param clientSubject a Subject that represents the source of the service request. It is used by the method
     * implementation to store Principals and credentials validated in the request.
     *
     * @param serviceSubject a Subject that represents the recipient of the service request, or null. It may be used by the
     * method implementation as the source of Principals or credentials to be used to validate the request. If the Subject
     * is not null, the method implementation may add additional Principals or credentials (pertaining to the recipient of
     * the service request) to the Subject.
     *
     * @return an AuthStatus object representing the completion status of the processing performed by the method. The
     * AuthStatus values that may be returned by this method are defined as follows:
     *
     * <ul>
     * <li>AuthStatus.SUCCESS when the application request message was successfully validated. The validated request message
     * is available by calling getRequestMessage on messageInfo.
     *
     * <li>AuthStatus.SEND_SUCCESS to indicate that validation/processing of the request message successfully produced the
     * secured application response message (in messageInfo). The secured response message is available by calling
     * getResponseMessage on messageInfo.
     *
     * <li>AuthStatus.SEND_CONTINUE to indicate that message validation is incomplete, and that a preliminary response was
     * returned as the response message in messageInfo.
     *
     * When this status value is returned to challenge an application request message, the challenged request must be saved
     * by the authentication module such that it can be recovered when the module's validateRequest message is called to
     * process the request returned for the challenge.
     *
     * <li>AuthStatus.SEND_FAILURE to indicate that message validation failed and that an appropriate failure response
     * message is available by calling getResponseMessage on messageInfo.
     * </ul>
     *
     * @exception AuthException when the message processing failed without establishing a failure response message (in
     * messageInfo).
     */
    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        String servletPath = request.getContextPath() + request.getServletPath();

        String msg = "HttpServlet profile: ";

        if (clientSubject != null) {
            msg += "TSServerAuthModule.validateRequest called with non-null client Subject";
            // msg += " principal=" + getPrincipalNameFromSubject(clientSubject);
        } else {
            msg += "TSServerAuthModule.validateRequest called with null client Subject";
        }
        logMsg(msg);

        // lets ensure we are not pre-logged in
        doCheckForPreLogin(messageInfo, clientSubject, serviceSubject);

        if (serviceSubject != null) {
            msg = msg + " with serviceSubject :" + getPrincipalNameFromSubject(serviceSubject);
        } else {
            msg = msg + " with null serviceSubject";
        }
        logMsg(msg);

        // verify any profile keys
        logMessageTypes(messageInfo, "validateRequest");
        dumpServletProfileKeys(messageInfo, "validateRequest");

        // this is used to help us verify part of JASPI:SPEC:98
        // if auth is optional then we should be able to have a null
        // principal. If auth is mandatory, then we need to set a
        // principal object with some valid creds.
        boolean bIsMandatory = isAuthMandatory(messageInfo);

        // support to test validateRequest is called regardless of whether authN
        // is required (including when isMandatory is false)
        logMsg("validateRequest() called for " + servletPath + ", isMandatory() = " + bIsMandatory);

        // Check Callback Handler support for server runtime
        logMsg("Dispatching to request for servletPath: " + servletPath);
        CommonCallbackSupport commonCallbacks = new CommonCallbackSupport(logger, callbackHandler, JASPICData.LAYER_SERVLET,
                "ServerRuntime");
        commonCallbacks.verify();

        doServerCallbackChecks(messageInfo, clientSubject, serviceSubject);

        // Determine return status based on pre-canned context ids
        AuthStatus returnStatus = getReturnStatus(messageInfo, clientSubject);

        setSpecialRequestAttribute(messageInfo, "validateReqCalled", "true");

        return returnStatus;
    }

    /*
     * This method checks if a user is pre-logged in for a servlet that required no authen. In such a case, we should NOT
     * see any pre-authenticated status. This method should be called before any callback handler invocations occur.
     */
    private boolean doCheckForPreLogin(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) {

        boolean rval = true;
        String theServlet = "OpenToAllServlet";

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        String servletPath = request.getContextPath() + request.getServletPath();

        if (servletPath.contains(theServlet)) {
            // we found our request for the servlet that should have
            // open perms and thus NOT require any authentication.
            Principal callerPrincipal = request.getUserPrincipal();

            if (callerPrincipal == null) {
                // if here, we are good since we are not logged in (based on the
                // premise that if logged in, calling getUserPrincipal() would
                // have returned a non-null value.)
                logMsg("Validated we are not prelogged in for " + theServlet);
            } else {
                // ERROR - something was non-null which indicates that we are
                // pre-logged in (e.g. pre-authenticated) for a scenario that does
                // not require authentication. Either that or we are (correctly)
                // not logged in *but* are incorrectly getting back non-null values
                // when not logged in nor authenticated.
                logMsg("Failed validation check for being pre-logged in.");
                logMsg("doCheckForPreLogin(): request.getUserPrincipal() = null");
                rval = false;
            }
        }

        return rval;
    }

    private void doServerCallbackChecks(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        String servletPath = request.getContextPath() + request.getServletPath();
        String msg = "";

        ServerCallbackSupport serverCallbacks =
            new ServerCallbackSupport(
                    logger, callbackHandler, JASPICData.LAYER_SERVLET, messageInfo,
                    clientSubject, serviceSubject);

        // instead of calling all callbacks in one method, lets call them
        // all individually so ithat we can check return values of each.
        // serverCallbacks.verify();

        if (serverCallbacks.verifyCPCCallback()) {
            msg = "TSServerAuthModule.validateRequest(): verifyCPCCallback returned true";
        } else {
            msg = "TSServerAuthModule.validateRequest(): verifyCPCCallback returned false";
        }
        msg += " for servletPath = " + servletPath;
        logMsg(msg);

        if (serverCallbacks.verifyGPCCallback()) {
            msg = "TSServerAuthModule.validateRequest(): verifyGPCCallback returned true";
        } else {
            msg = "TSServerAuthModule.validateRequest(): verifyGPCCallback returned false";
        }
        msg += " for servletPath = " + servletPath;
        logMsg(msg);

        if (serverCallbacks.verifyPVCCallback()) {
            msg = "TSServerAuthModule.validateRequest(): verifyPVCCallback returned true";
        } else {
            msg = "TSServerAuthModule.validateRequest(): verifyPVCCallback returned false";
        }
        msg += " for servletPath = " + servletPath;
        logMsg(msg);

        return;
    }

    /*
     * This method will have some pre-canned status responses built into based on what the user name is. We will return
     * status codes based on the passed in context info.
     *
     * One goal of this method is to cause a Callbackhandler to be invoked such that the Callbackhandler will return an
     * AuthStatus code that matches our pre-canned (returned) AuthStatus.
     */
    private AuthStatus getReturnStatus(MessageInfo msgInfo, Subject subject) throws AuthException {
        AuthStatus rval = AuthStatus.SUCCESS;
        String strStatus = "AuthStatus.SUCCESS";
        String msg;
        int statusCode = 200;

        // get request object
        HttpServletRequest request = (HttpServletRequest) msgInfo.getRequestMessage();

        // get name of servlet(servletPath) and not the app (eg context path)
        String servletName = request.getServletPath();

        msg = "HttpServlet profile with servletName=" + servletName;
        if (servletName.contains(JASPICData.AUTHSTAT_SENDFAILURE_ND)) {
            rval = AuthStatus.SEND_FAILURE;
            strStatus = "AuthStatus.SEND_FAILURE";
            statusCode = 500;

        } else if (servletName.contains(JASPICData.AUTHSTAT_SENDCONT_ND)) {
            rval = AuthStatus.SEND_CONTINUE;
            strStatus = "AuthStatus.SEND_CONTINUE";
            statusCode = 401;

        } else if (servletName.contains(JASPICData.AUTHSTAT_SENDSUCCESS_ND)) {
            rval = AuthStatus.SEND_SUCCESS;
            strStatus = "AuthStatus.SEND_SUCCESS";
            statusCode = 200;

        } else if (servletName.contains(JASPICData.AUTHSTAT_FAILURE_ND)) {
            rval = AuthStatus.FAILURE;
            strStatus = "AuthStatus.FAILURE";
            statusCode = 500;

        } else if (servletName.contains(JASPICData.AUTHSTAT_THROW_EX_ND)) {
            msg += " returning  AuthStatus=AuthException";
            logger.log(INFO, msg);
            throw new AuthException(msg);

        } else if (servletName.contains(JASPICData.AUTHSTAT_SUCCESS_ND) || servletName.contains(JASPICData.AUTHSTAT_OPT_SUCCESS)
                || servletName.contains(JASPICData.AUTHSTAT_MAND_SUCCESS)) {
            rval = AuthStatus.SUCCESS;
            strStatus = "AuthStatus.SUCCESS";
        }

        msg += " returning  AuthStatus=" + strStatus;
        logger.log(INFO, msg);

        // lets get our response message in case we need to do something...
        Object respObj = msgInfo.getResponseMessage();
        HttpServletResponseWrapper response = null;
        if (respObj != null) {
            if (respObj instanceof HttpServletResponseWrapper) {
                if (response != null) {
                    response.setStatus(statusCode);
                    msgInfo.setResponseMessage(response);
                }
            } else if (respObj instanceof HttpServletResponse) {
                response = new HttpServletResponseWrapper((HttpServletResponse) respObj);
                response.setStatus(statusCode);
                msgInfo.setResponseMessage(response);
            } else {
                msg = "WARNING:  we have some unidentified response object.";
                logger.log(INFO, msg);
            }
        }

        return rval;
    }

    /*
     * This is a convenience method that will likely only be called once. Currently there is only one key to dump from the
     * map, but his may change in the future.
     */
    private void dumpServletProfileKeys(MessageInfo msgInfo, String callerMethod) {
        Map<String, Object> map = msgInfo.getMap();

        // lets pull out some context info that we can use to help uniquely
        // identify the source of this request
        HttpServletRequest request = (HttpServletRequest) msgInfo.getRequestMessage();

        String servletName = request.getServletPath();

        // see assertion JASPI:SPEC:306 for details on this
        // Jakarta Authentication states the following key must exist for servlet profile
        if (map != null) {
            Set keys = map.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Object oKey = iterator.next();
                if (oKey instanceof String) {
                    String key = (String) oKey;
                    if (key != null) {
                        Object obj = map.get(key);
                        if (obj instanceof String) {
                            String keyVal = (String) map.get(key);
                            String msg = "dumpServletProfileKeys() called with attrs: ";
                            msg += " layer=" + JASPICData.LAYER_SERVLET;
                            msg += " servletName=" + servletName;
                            msg += " callerMethod=" + callerMethod;
                            msg += " key=" + key;

                            if (keyVal == null) {
                                msg += " value=NULL";
                            } else if (Boolean.valueOf(keyVal).booleanValue() == true) {
                                msg += " value=Valid";
                            } else {
                                msg += " value=Invalid value of: " + keyVal;
                            }
                            logger.log(INFO, msg);
                        } else {
                            logger.log(INFO, "Map key is of type :" + obj.getClass().getName());
                        }
                    }
                }
            }
        }

    }

    private boolean isAuthMandatory(MessageInfo msgInfo) {
        boolean bval = false;
        Map<String, Object> map = msgInfo.getMap();

        // lets pull out some context info that we can use to help uniquely
        // identify the source of this request
        HttpServletRequest request = (HttpServletRequest) msgInfo.getRequestMessage();

        String servletName = request.getServletPath();

        // see assertion JASPI:SPEC:306 for details on this
        // jsr-196 states the following key must exist for servlet profile
        String strKey = "jakarta.security.auth.message.MessagePolicy.isMandatory";
        String msg;
        if (map != null) {
            String keyVal = (String) map.get(strKey);
            msg = "isAuthMandatory() called with attrs: ";
            msg += " layer=" + JASPICData.LAYER_SERVLET;
            msg += " servletName=" + servletName;
            msg += " key=" + strKey;

            if (keyVal == null) {
                msg += " value=NULL";
                bval = false; // assume false if we cant determine
            } else if (Boolean.valueOf(keyVal).booleanValue() == true) {
                msg += " value=Valid";
                bval = true;
            } else {
                // assume false
                msg += " value=false";
                bval = false;
            }
            logger.log(Level.FINE, msg);
        } else {
            msg = "FAILURE:  No map in MessageInfo thus no key=" + strKey;
            logger.log(Level.SEVERE, msg);
        }

        return bval;
    }

    /*
     * Convenience method to dump messageInfo logging out.
     */
    private void logMessageTypes(MessageInfo messageInfo, String methodName) {
        String msg;

        String requestURI = "";

        if (messageInfo != null) {
            Object reqObj = messageInfo.getRequestMessage();
            if (reqObj != null) {
                // if here, we want to see if our reqObj is type HttpServletRequest
                // and if so print out a log msg stating so.
                // (Jakarta Authentication expects the reqObj to be type HttpServletRequest)
                msg = methodName + ": MessageInfo.getRequestMessage() is of type ";
                if (reqObj instanceof HttpServletRequest) {
                    msg = msg + "jakarta.servlet.http.HttpServletRequest";
                    requestURI = ((HttpServletRequest) reqObj).getRequestURI();

                    // related to assertion JASPI:SPEC:95 , this block of code
                    // may be needed if we need to identify which servlet invoked this
                    if (requestURI != null) {
                        // we want to know which servlet/jsp/html page generated
                        // this action and we want to include that in the log so we
                        // can check if the log gets generated for both servlets and
                        // for static html - which is a jsr-196 requirement.
                        String msg2 = msg + " for requestURI=" + requestURI;
                        logMsg(msg2);
                    }
                } else {
                    msg = msg + messageInfo.getClass().getName();
                }
                logMsg(msg);
            }

            Object respObj = messageInfo.getResponseMessage();
            if (respObj != null) {
                // if here, we want to see if our respObj is type HttpServletResponse
                // and if so print out a log msg stating so. (jsr-196 expects the
                // respObj to be type HttpServletResponse)
                msg = methodName + ": MessageInfo.getResponseMessage() is of type ";
                if (respObj instanceof jakarta.servlet.http.HttpServletResponse) {
                    msg = msg + "jakarta.servlet.http.HttpServletResponse";
                } else {
                    msg = msg + messageInfo.getClass().getName();
                }
                logMsg(msg);
            }

        } else {
            msg = "TSServerAuthModule." + methodName + " called with null MessageInfo object.";
            logMsg(msg);
        }

    }

    /**
     * Secure a service response before sending it to the client.
     *
     * This method is called to transform the response message acquired by calling getResponseMessage (on messageInfo) into
     * the mechanism specific form to be sent by the runtime.
     * <p>
     * This method conveys the outcome of its message processing either by returning an AuthStatus value or by throwing an
     * AuthException.
     *
     * @param messageInfo a contextual object that encapsulates the client request and server response objects, and that may
     * be used to save state across a sequence of calls made to the methods of this interface for the purpose of completing
     * a secure message exchange.
     *
     * @param serviceSubject a Subject that represents the source of the service response, or null. It may be used by the
     * method implementation to retrieve Principals and credentials necessary to secure the response. If the Subject is not
     * null, the method implementation may add additional Principals or credentials (pertaining to the source of the service
     * response) to the Subject.
     *
     * @return an AuthStatus object representing the completion status of the processing performed by the method. The
     * AuthStatus values that may be returned by this method are defined as follows:
     *
     * <ul>
     * <li>AuthStatus.SEND_SUCCESS when the application response message was successfully secured. The secured response
     * message may be obtained by calling by getResponseMessage on messageInfo.
     *
     * <li>AuthStatus.SEND_CONTINUE to indicate that the application response message (within messageInfo) was replaced with
     * a security message that should elicit a security-specific response (in the form of a request) from the peer.
     *
     * This status value serves to inform the calling runtime that (in order to successfully complete the message exchange)
     * it will need to be capable of continuing the message dialog by processing at least one additional request/response
     * exchange (after having sent the response message returned in messageInfo).
     *
     * When this status value is returned, the application response must be saved by the authentication module such that it
     * can be recovered when the module's validateRequest message is called to process the elicited response.
     *
     * <li>AuthStatus.SEND_FAILURE to indicate that a failure occured while securing the response message and that an
     * appropriate failure response message is available by calling getResponseMeessage on messageInfo.
     * </ul>
     *
     * @exception AuthException when the message processing failed without establishing a failure response message (in
     * messageInfo).
     */
    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        logMsg("Enterred secureResponse");

        String msg = "";
        if (serviceSubject != null) {
            msg = "TSServerAuthModule.secureResponse called with serviceSubject :" + getPrincipalNameFromSubject(serviceSubject);
        } else {
            msg = "TSServerAuthModule.secureResponse called with null service Subject";
        }
        logMsg(msg);

        logMessageTypes(messageInfo, "secureResponse");
        dumpServletProfileKeys(messageInfo, "secureResponse");

        // verify that our context was not one that should have avoided a
        // dispatch to service and thus should not have called secureResponse()
        verifySecureResponseShouldHaveBeenCalled(messageInfo);

        // help verify assertion: JASPIC:SPEC:108
        setSpecialRequestAttribute(messageInfo, "secureRespCalled", "true");

        return AuthStatus.SEND_SUCCESS;
    }

    /*
     * This is a specialized method used to set a request attribute that will be checked during the servlet/service
     * invocation the idea is that secureResponse should not be called before the servlet/service invocation.
     */
    public void setSpecialRequestAttribute(MessageInfo msgInfo, String key, String value) {
        String msg;

        logMsg("TSServerAuthModule() setSpecialRequestAttribute called");

        try {
            // get request object
            HttpServletRequest request = (HttpServletRequest) msgInfo.getRequestMessage();

            // we want the servletpath here (not the context path) as this
            // will give us a better idea which servlet was invoked
            String servletName = request.getServletPath();

            if ((servletName.contains(JASPICData.AUTHSTAT_MAND_SUCCESS))) {
                // lets set our cts proprietary request attr indicating
                // flow was in secureResponse
                logMsg("setSpecialRequestAttribute() called for servlet:  " + servletName);

                request.setAttribute(key, value);
                msgInfo.setRequestMessage(request);
                logMsg("setSpecialRequestAttribute(): setAttribute() set for secureRespCalled=true");
            }
        } catch (Exception ex) {
            // should not get here but in case, dump the ex and return true
            msg = "Got Unexpected Exception!";
            msg += "   Exception message was:  " + ex.toString();
            logMsg(msg + "");
            ex.printStackTrace();
        }

    }

    public void verifySecureResponseShouldHaveBeenCalled(MessageInfo msgInfo) {
        String msg;

        try {
            // get request object
            HttpServletRequest request = (HttpServletRequest) msgInfo.getRequestMessage();

            // we want the servletpath here (not the context path) as this
            // will give us a better idea which servlet was invoked
            String servletName = request.getServletPath();

            if ((servletName.contains(JASPICData.AUTHSTAT_SENDFAILURE_ND)) || (servletName.contains(JASPICData.AUTHSTAT_SENDSUCCESS_ND))
                    || (servletName.contains(JASPICData.AUTHSTAT_FAILURE_ND)) || (servletName.contains(JASPICData.AUTHSTAT_THROW_EX_ND))
                    || (servletName.contains(JASPICData.AUTHSTAT_SUCCESS_ND))) {
                // we have a specific context indicating we should NOT have been
                // dispatched to a service (ie should not be in secureResponse)
                msg = "FAILURE:  should not have been dispatched to secureResponse";
                logger.log(INFO, msg);
            }
        } catch (Exception ex) {
            // should not get here but in case, dump the ex and return true
            msg = "Got Unexpected Exception!";
            msg += "   Exception message was:  " + ex.getMessage();
            logger.log(INFO, msg);
            ex.printStackTrace();
        }
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
        logMsg("TSServerAuthModule.cleanSubject called");
        subject = null;
    }

    public void logMsg(String str) {
        if (logger != null) {
            logger.log(INFO, str);
        } else {
            System.out.println("*** TSLogger Not Initialized properly ***");
            System.out.println("*** TSSVLogMessage : ***" + str);
        }
    }

    public String getPrincipalNameFromSubject(Subject subject) {
        String concatPrincipalName = "";

        for (Principal principal : subject.getPrincipals()) {
            concatPrincipalName += principal.getName();
        }

        return concatPrincipalName;
    }

}
