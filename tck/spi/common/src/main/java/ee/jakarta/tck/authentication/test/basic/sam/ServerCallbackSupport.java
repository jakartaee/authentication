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

import static ee.jakarta.tck.authentication.test.basic.servlet.JASPICData.LAYER_SERVLET;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import ee.jakarta.tck.authentication.test.basic.sam.util.BASE64Decoder;
import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.auth.message.callback.GroupPrincipalCallback;
import jakarta.security.auth.message.callback.PasswordValidationCallback;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 *
 * @author Raja Perumal
 */
public class ServerCallbackSupport {

    private static TSLogger logger;
    private static CallbackHandler callbackHandler;
    private static String profile;
    private static final String runtimeType = "ServerRuntime";
    private static MessageInfo messageInfo;
    private static Subject clientSubject;

    // User corresponds to system property "j2eelogin.name" (e.g. "j2ee")
    private static String user = System.getProperty("j2eelogin.name");

    // Password corresponds to system property "j2eelogin.password" (e.g. "j2ee")
    private static String passwd = System.getProperty("j2eelogin.password");

    /** Creates a new instance of ServerCallbackSupport */
    public ServerCallbackSupport(TSLogger tsLogger, CallbackHandler cbkHandler, String profile) {
        logger = tsLogger;
        callbackHandler = cbkHandler;
        ServerCallbackSupport.profile = profile;
    }

    public ServerCallbackSupport(TSLogger tsLogger, CallbackHandler cbkHandler, String profile, MessageInfo msgInfo, Subject clientSubj, Subject serverSubj) {
        logger = tsLogger;
        callbackHandler = cbkHandler;

        ServerCallbackSupport.profile = profile;
        ServerCallbackSupport.messageInfo = msgInfo;
        ServerCallbackSupport.clientSubject = clientSubj;
    }

    public boolean verify() {
        try {
            CallerPrincipalCallbackSupport();
            GroupPrincipalCallbackSupport();
            PasswordValidationCallbackSupport();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyCPCCallback() {
        boolean bval = CallerPrincipalCallbackSupport();
        logMsg("verifyCPCCallback returning " + Boolean.toString(bval));
        return bval;
    }

    public boolean verifyGPCCallback() {
        boolean bval = GroupPrincipalCallbackSupport();
        logMsg("verifyGPCCallback returning " + Boolean.toString(bval));
        return bval;
    }

    public boolean verifyPVCCallback() {
        boolean bval = PasswordValidationCallbackSupport();
        logMsg("verifyPVCCallback returning " + Boolean.toString(bval));
        return bval;
    }

    private boolean CallerPrincipalCallbackSupport() {
        boolean bval = false;
        HttpServletRequest request = null;

        try {
            request = (HttpServletRequest) messageInfo.getRequestMessage();
        } catch (Exception ex) {
        }

        if (callbackHandler != null) {
            try {
                // Note: we should be able to have a subject that has NO
                // principals for the case of optional authen. Which means
                // we should not have to explicitly set the principal here.
                // however, for the case of mandatory authen, we will want
                // to create a CPC using a username as opposed to a null principal.

                CallerPrincipalCallback callerPrincipalCallback = null;
                if (profile.equals(LAYER_SERVLET)) {
                    Principal principal = null;
                    if (messageInfo != null) {
                        HttpServletRequest httpServletRequest = (HttpServletRequest) messageInfo.getRequestMessage();
                        String username = getServletUsername(httpServletRequest);
                        String principalName = getPrincipalNameFromSubject(clientSubject);
                        String nameToLog = null;

                        // Better to call cbh with a null principal when the policy is Not
                        // mandatory
                        // and with a legitimate principal when the policy is mandatory
                        // (unless testing send-failure or send-continue - which we are
                        // not!)
                        boolean bIsMandatory = isServletAuthMandatory(messageInfo);
                        if (bIsMandatory) {
                            logMsg("CallerPrincipalCallbackSupport() Authentication mandatory");
                            if (username != null) {
                                logMsg("CallerPrincipalCallbackSupport() auth mandatory, username != null");
                                callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, username);
                                nameToLog = username;
                            } else if (principalName != null) {
                                logMsg("CallerPrincipalCallbackSupport() auth mandatory, principalName != null");
                                callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, principalName);
                                nameToLog = principalName;
                            } else {
                                logMsg("CallerPrincipalCallbackSupport() auth mandatory, username and principalName both == null");
                            }
                        } else {
                            logMsg("CallerPrincipalCallbackSupport() Authentication NOT mandatory");
                            callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, (Principal) null);
                        }

                        // Now for some simple invocations to ensure we can call the API's
                        // these lines serve no other purpose than to validate we can
                        // invoke the api's in order to satisfy the javadoc assertions of:
                        // JSR-196:JAVADOC:32, JSR-196:JAVADOC:33, JSR-196:JAVADOC:34
                        String cpcbkName = callerPrincipalCallback.getName();
                        Principal cpcbkPrin = callerPrincipalCallback.getPrincipal();
                        Subject cpcbkSubj = callerPrincipalCallback.getSubject();

                        String msg = "CallerPrincipalCallback called for profile=" + profile;
                        if (request != null) {
                            String servletPath = request.getContextPath() + request.getServletPath();
                            msg += " for servletPath=" + servletPath;
                        } else {
                            msg += " messageInfo contained null request";
                        }
                        logMsg(msg);

                        // this helps test JASPIC:SPEC:103
                        if (clientSubject == null) {
                            msg += " subject=null";
                        } else {
                            msg += " subject=non-null";
                        }
                        msg += " principal set = " + nameToLog;
                        logMsg(msg);

                    } else {
                        // Uses a null principal
                        callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, principal);
                    }
                } else {
                    // Should not get into here.
                    logMsg("WARNING:  ServerCallbackSupport.CallerPrincipalCallbackSupport() - profile != servlet.");
                    Subject subject = new Subject();
                    callerPrincipalCallback = new CallerPrincipalCallback(subject, (Principal) null);
                }

                Callback[] callbacks = new Callback[] { callerPrincipalCallback };

                callbackHandler.handle(callbacks);

                logMsg("CallbackHandler supports CallerPrincipalCallback");
                bval = true; // if here assume successful authen

            } catch (UnsupportedCallbackException usce) {
                logMsg("CallbackHandler failed to support CallerPrincipalCallback :" + usce.getMessage());
                usce.printStackTrace();

            } catch (Exception ex) {
                // failed CPC authentication for unknown reason
                String servletPath = "";
                if (request != null) {
                    servletPath = request.getContextPath() + request.getServletPath();
                } else {
                    servletPath = "WARNING:  can't determine servletpath";
                }
                logMsg("CPC Exception failure for servletPath=" + servletPath);
                ex.printStackTrace();
            }

        } else {
            String msg = "CallerPrincipalCallback has a null callbackHandler";
            msg += "  profile=" + profile;
            if (profile.equals(LAYER_SERVLET) && messageInfo != null) {
                if (request != null) {
                    String servletPath = request.getContextPath() + request.getServletPath();
                    msg += " for servletPath=" + servletPath;
                } else {
                    msg += " messageInfo contained null request";
                }
                logMsg(msg);

                if (clientSubject == null) {
                    msg += " subject=null";
                } else {
                    msg += " subject=non-null";
                }
                String principalConcatString = getPrincipalNameFromSubject(clientSubject);
                msg += " principal set = " + principalConcatString;

            }
            logMsg(msg);
        }

        return bval;
    }

    /*
     * This is a convenience method. It is used to help pull out the username from the request. This method will only pull
     * out a username if there was a client side servlet request made where Basic auth was used. This will only succeed if:
     * 1. We have a ServletRequest that was populated correctly 2. The client side used BASE64Encoder() to encode user/pwd
     * info 3. The user/pwd info were encoded in a format similar to the the following: String authData =
     * username+":"+password BASE64Encode.encode(authData.getBytes())
     *
     * This returns just the decoded username.
     *
     */
    private String getServletUsername(HttpServletRequest request) {
        String username = null;

        String authorization = request.getHeader("authorization");
        BASE64Decoder decoder = new BASE64Decoder();

        if (authorization != null && authorization.startsWith("Basic ")) {
            try {
                String authStr = authorization.substring(6).trim();
                String value = new String(decoder.decodeBuffer(authStr));
                logMsg("decoded (request) authorization string of: " + value);

                // At this point value should be in the form of <username>:<pwd>
                if (value != null) {
                    username = value.substring(0, value.indexOf(":"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return username;
    }

    /*
     * This is a convenience method that is used to determine if Authentication is mandatory. Based on the answer, there are
     * certain requirements that will need to be met wrt setting of principals.
     */
    private boolean isServletAuthMandatory(MessageInfo msgInfo) {
        boolean servletAuthMandatory = false;
        Map<String, Object> map = msgInfo.getMap();

        // Lets pull out some context info that we can use to help uniquely
        // identify the source of this request
        HttpServletRequest request = (HttpServletRequest) msgInfo.getRequestMessage();

        String servletName = request.getServletPath();

        // See assertion JASPI:SPEC:306 for details on this
        // jsr-196 states the following key must exist for servlet profile
        String strKey = "jakarta.security.auth.message.MessagePolicy.isMandatory";
        String msg;
        if (map != null) {
            String keyVal = (String) map.get(strKey);
            msg = "isServletAuthMandatory() called with attrs: ";
            msg += " layer=" + LAYER_SERVLET;
            msg += " servletName=" + servletName;
            msg += " key=" + strKey;

            if (keyVal == null) {
                msg += " value=NULL";
                servletAuthMandatory = false; // assume false if we cant determine
            } else if (Boolean.valueOf(keyVal).booleanValue() == true) {
                msg += " value=Valid";
                servletAuthMandatory = true;
            } else {
                // assume false
                msg += " value=false";
                servletAuthMandatory = false;
            }
            logger.log(FINE, msg);
        } else {
            msg = "FAILURE:  No map in MessageInfo thus no key=" + strKey;
            logger.log(SEVERE, msg);
        }

        return servletAuthMandatory;
    }

    public String getPrincipalNameFromSubject(Subject subject) {
        if (subject == null) {
            return null;
        }

        String concatPrincipalName = "";
        for (Principal principal : subject.getPrincipals()) {
            concatPrincipalName += principal.getName();
        }

        return concatPrincipalName;
    }

    private boolean GroupPrincipalCallbackSupport() {
        boolean bval = false;
        boolean isAuthMandatory = false;
        String strServletContext = "";
        String principalName = "";

        if (callbackHandler != null) {
            try {
                // Note: we should be able to have a subject that has NO
                // principals for the case of optional authen. Which means
                // we should not have to explicitly set the principal here.
                // however, for the case of mandatory authen, we will want
                // to create a CPC using a username as opposed to a null principal.

                Subject subject = clientSubject != null? clientSubject : new Subject();
                String strArray[] = { "Administrator" };
                GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(subject, strArray);

                CallerPrincipalCallback callerPrincipalCallback = null;
                if (profile.equals(LAYER_SERVLET)) {
                    if (messageInfo != null) {
                        HttpServletRequest req = (HttpServletRequest) messageInfo.getRequestMessage();
                        String username = getServletUsername(req);
                        principalName = getPrincipalNameFromSubject(clientSubject);
                        strServletContext = req.getServletPath();

                        // Better to call cbh with a null principal when the policy is Not
                        // mandatory
                        // and with a legitimate principal when the policy is mandatory
                        // (unless testing send-failure or send-continue - which we are
                        // not!)
                        boolean bIsMandatory = isServletAuthMandatory(messageInfo);
                        if (bIsMandatory) {
                            isAuthMandatory = true;
                            debug("GroupPrincipalCallbackSupport() Authentication mandatory");
                            if (username != null) {
                                debug("GroupPrincipalCallbackSupport() auth mandatory, username != null");
                                callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, username);
                            } else if (principalName != null) {
                                debug("GroupPrincipalCallbackSupport() auth mandatory, principalName != null");
                                callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, principalName);
                            } else {
                                logMsg("GroupPrincipalCallbackSupport() auth mandatory, username and principalName both == null");
                            }
                        } else {
                            debug("GroupPrincipalCallbackSupport() Authentication NOT mandatory");
                            callerPrincipalCallback = new CallerPrincipalCallback(subject, (Principal) null);
                        }
                    } else {
                        // Uses a null principal
                        debug("GroupPrincipalCallbackSupport(): messageInfo == null, using null principal");
                        callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, (Principal) null);
                    }
                } else {
                    // If here, we were erroneously called by non-servlet profile
                    debug("WARNING:  ServerCallbackSupport.CallerPrincipalCallbackSupport() - profile != servlet.");
                    callerPrincipalCallback = new CallerPrincipalCallback(subject, (Principal) null);
                }

                Callback[] callbacks = new Callback[] { groupPrincipalCallback, callerPrincipalCallback };
                callbackHandler.handle(callbacks);

                // This string will be searched for on client side
                String theMessage = "GroupPrincipalCallbackSupport():";
                theMessage += " successfully called callbackHandler.handle(callbacks)";
                theMessage += " for servlet: " + strServletContext;
                theMessage += " with isServletAuthMandatory = " + isAuthMandatory;
                logMsg(theMessage);

                logMsg("CallbackHandler supports GroupPrincipalCallback");

                bval = true; // if here assume successful authen

            } catch (UnsupportedCallbackException | IOException usce) {
                logMsg("CallbackHandler failed to support GroupPrincipalCallback :" + usce.getMessage());
                usce.printStackTrace();
            }
        }

        return bval;
    }

    private boolean PasswordValidationCallbackSupport() {
        boolean bval = false;

        if (callbackHandler != null) {
            try {
                Subject subject = new Subject();
                String username = user; // e.g. "j2ee";
                char[] password = passwd.toCharArray(); // e.g. {'j','2','e','e'};

                PasswordValidationCallback passwordValidationCallback = new PasswordValidationCallback(subject, username, password);
                CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(subject, (Principal) null);

                Callback[] callbacks = new Callback[] { passwordValidationCallback, callerPrincipalCallback };

                callbackHandler.handle(callbacks);

                Subject returnedSubject = passwordValidationCallback.getSubject();
                boolean result = passwordValidationCallback.getResult();
                String userName = passwordValidationCallback.getUsername();
                char[] returnedPassword = passwordValidationCallback.getPassword();
                passwordValidationCallback.clearPassword();

                logMsg("PasswordValidation callback returned result =" + result);
                logMsg("CallbackHandler supports PasswordValidationCallback");

                bval = result;

            } catch (UnsupportedCallbackException | IOException usce) {
                logMsg("CallbackHandler failed to support PasswordValidationCallback :" + usce.getMessage());
                usce.printStackTrace();
            }
        }

        return bval;
    }

    public void logMsg(String str) {
        if (logger != null) {
            logger.log(INFO, "In " + profile + " : " + runtimeType + " " + str);
        } else {
            System.out.println("*** TSLogger Not Initialized properly ***");
            System.out.println("*** TSSVLogMessage : ***" + str);
        }
    }

    public void debug(String str) {
        System.out.println(str);
    }

}
