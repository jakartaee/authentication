/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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

package ee.jakarta.tck.authentication.test.basic.sam.config;

import static ee.jakarta.tck.authentication.test.basic.servlet.JASPICData.LAYER_SERVLET;
import static ee.jakarta.tck.authentication.test.basic.servlet.JASPICData.LAYER_SOAP;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;

import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.security.auth.message.MessageInfo;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.Name;
import jakarta.xml.soap.Node;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import java.util.Iterator;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

public class SOAPTSServerAuthConfig extends TSServerAuthConfig {

    protected SOAPTSServerAuthConfig(String layer, String applicationCtxt, CallbackHandler cbkHandler, Map<String, Object> props) {
        super(layer, applicationCtxt, cbkHandler, props);
    }

    public SOAPTSServerAuthConfig(String layer, String applicationCtxt, CallbackHandler cbkHandler, Map<String, Object> props, TSLogger tsLogger) {
        super(layer, applicationCtxt, cbkHandler, props);

        if (tsLogger != null) {
            logger = tsLogger;
        }

        String str = "TSServerAuthConfig called for layer=" + layer + " : appContext=" + applicationCtxt;
        logger.log(INFO, str);
    }

    @Override
    public String getAuthContextID(MessageInfo messageInfo) {
        logger.log(INFO, "getAuthContextID called");
        String authContextID = null;

        if (messageLayer.equals(LAYER_SOAP)) {
            authContextID = getOpName((SOAPMessage) messageInfo.getRequestMessage());

            logger.log(INFO, "getAuthContextID() called for layer=" + messageLayer + " shows AuthContextId=" + authContextID);

        } else if (messageLayer.equals(LAYER_SERVLET)) {
            super.getAuthContextID(messageInfo);
        }

        return authContextID;
    }

    private String getOpName(SOAPMessage message) {
        if (message == null) {
            return null;
        }

        String opName = null;

        // First look for a SOAPAction header.
        // this is what .net uses to identify the operation
        MimeHeaders headers = message.getMimeHeaders();
        if (headers != null) {
            String[] actions = headers.getHeader("SOAPAction");
            if (actions != null && actions.length > 0) {
                opName = actions[0];
                if (opName != null && opName.equals("\"\"")) {
                    opName = null;
                }
            }
        }

        // If that doesn't work then we default to trying the name
        // of the first child element of the SOAP envelope.
        if (opName == null) {
            Name name = getName(message);
            if (name != null) {
                opName = name.getLocalName();
            }
        }

        return opName;
    }

    private Name getName(SOAPMessage message) {
        SOAPPart soap = message.getSOAPPart();
        if (soap == null) {
            return null;
        }

        try {
            SOAPEnvelope envelope = soap.getEnvelope();
            if (envelope == null) {
                return null;
            }

            SOAPBody body = envelope.getBody();
            if (body == null) {
                return null;
            }

            Iterator<Node> childElements = body.getChildElements();
            while (childElements.hasNext()) {
                if (childElements.next() instanceof SOAPElement soapElement) {
                    return soapElement.getElementName();
                }
            }
        } catch (SOAPException se) {
            logger.log(FINE, "WSS: Unable to get SOAP envelope", se);
        }

        return null;
    }

}
