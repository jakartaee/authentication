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
package ee.jakarta.tck.authentication.test.registersession.sam;

import static jakarta.security.auth.message.AuthStatus.SUCCESS;
import static java.lang.Boolean.TRUE;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.auth.message.MessageInfo;
import jakarta.security.auth.message.MessagePolicy;
import jakarta.security.auth.message.callback.CallerPrincipalCallback;
import jakarta.security.auth.message.callback.GroupPrincipalCallback;
import jakarta.security.auth.message.module.ServerAuthModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 *
 * @author Arjan Tijms
 *
 */
public class TestServerAuthModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map<String, Object> options) throws AuthException {
        this.handler = handler;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
        throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        Callback[] callbacks;

        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal != null && request.getParameter("continueSession") != null) {

            // ### If already authenticated before, continue this session

            // Execute protocol to signal container registered authentication session be used.
            callbacks = new Callback[] { new CallerPrincipalCallback(clientSubject, userPrincipal) };

        } else if (request.getParameter("doLogin") != null) {

            // ### If not authenticated before, do a new login if so requested

            // For the test perform a login by directly "returning" the details of the authenticated user.
            // Normally credentials would be checked and the details fetched from some repository

            callbacks = new Callback[] {
                // The name of the authenticated user

                    request.getParameter("customPrincipal") == null?
                        // Name based Callback
                        new CallerPrincipalCallback(clientSubject, "test") :

                        // Custom principal based Callback
                        new CallerPrincipalCallback(clientSubject, new MyPrincipal("test")),


                // the roles of the authenticated user
                new GroupPrincipalCallback(clientSubject, new String[] { "architect" }) };

            // Tell container to register an authentication session.
            messageInfo.getMap().put("jakarta.servlet.http.registerSession", TRUE.toString());
        } else {

            // ### If no registered session and no login request "do nothing"

            // The Jakarta Authentication protocol for "do nothing"
            callbacks = new Callback[] { new CallerPrincipalCallback(clientSubject, (Principal) null) };
        }

        try {

            // Communicate the details of the authenticated user to the container. In many
            // cases the handler will just store the details and the container will actually handle
            // the login after we return from this method.
            handler.handle(callbacks);

        } catch (IOException | UnsupportedCallbackException e) {
            throw (AuthException) new AuthException().initCause(e);
        }

        return SUCCESS;
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

}