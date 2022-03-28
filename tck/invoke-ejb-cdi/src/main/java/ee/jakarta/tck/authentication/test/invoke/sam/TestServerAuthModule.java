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
package ee.jakarta.tck.authentication.test.invoke.sam;

import static jakarta.security.auth.message.AuthStatus.SEND_SUCCESS;
import static jakarta.security.auth.message.AuthStatus.SUCCESS;
import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import ee.jakarta.tck.authentication.test.invoke.bean.CDIBean;
import ee.jakarta.tck.authentication.test.invoke.bean.EJBBean;
import jakarta.enterprise.inject.spi.CDI;
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

    private final static Logger logger = Logger.getLogger(TestServerAuthModule.class.getName());

    private CallbackHandler handler;
    private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map<String, Object> options) throws AuthException {
        this.handler = handler;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        if ("cdi".equals(request.getParameter("tech"))) {
            callCDIBean(request, response, "validateRequest");
        } else  if ("ejb".equals(request.getParameter("tech"))) {
            callEJBBean(response, "validateRequest");
        }

        try {
            handler.handle(new Callback[] {
                new CallerPrincipalCallback(clientSubject, "test"),
                new GroupPrincipalCallback(clientSubject, new String[] { "architect" })
            });

            return SUCCESS;

        } catch (IOException | UnsupportedCallbackException e) {
            throw (AuthException) new AuthException().initCause(e);
        }
    }

    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        if ("cdi".equals(request.getParameter("tech"))) {
            callCDIBean(request, response, "secureResponse");
        } else if ("ejb".equals(request.getParameter("tech"))) {
            callEJBBean(response, "secureResponse");
        }

        return SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();

        if ("cdi".equals(request.getParameter("tech"))) {
            callCDIBean(request, response, "cleanSubject");
        } else if ("ejb".equals(request.getParameter("tech"))) {
            callEJBBean(response, "cleanSubject");
        }
    }

    private void callCDIBean(HttpServletRequest request, HttpServletResponse response, String phase) {
        try {
            CDIBean cdiBean = CDI.current().select(CDIBean.class).get();
            response.getWriter().write(phase + ": " + cdiBean.getText() + "\n");

            cdiBean.setTextViaInjectedRequest();

            response.getWriter().write(phase + ": " + request.getAttribute("text")+ "\n");

        } catch (Exception e) {
            logger.log(SEVERE, "", e);
        }
    }

    private void callEJBBean(HttpServletResponse response, String phase) {
        try {
            EJBBean ejbBean = (EJBBean) new InitialContext().lookup("java:module/EJBBean");
            response.getWriter().write(phase + ": " + ejbBean.getText() + "\n");
        } catch (Exception e) {
            logger.log(SEVERE, "", e);
        }
    }


}