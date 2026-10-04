/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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

import ee.jakarta.tck.authentication.test.common.logging.server.TSLogger;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.config.ClientAuthConfig;
import jakarta.security.auth.message.config.ServerAuthConfig;
import jakarta.security.auth.message.module.ServerAuthModule;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

/**
 * Creates the classes of the optional SOAP profile, which need the SOAP API (jakarta.xml.soap).
 * <p>
 * They are created by name, so that the Servlet profile code doesn't reference them. A server without
 * the SOAP API then never loads them, also not when it analyses all reachable code ahead of time, like
 * a native image (e.g. Quarkus) does.
 */
final class SoapProfile {

    private static final String CONFIG_PACKAGE = "ee.jakarta.tck.authentication.test.basic.sam.config.";
    private static final String MODULE_PACKAGE = "ee.jakarta.tck.authentication.test.basic.sam.module.soap.";

    private SoapProfile() {
        // Prevent instantiation
    }

    static ServerAuthConfig newServerAuthConfig(String layer, String appContext, CallbackHandler handler,
        Map<String, Object> properties, TSLogger logger) throws AuthException {
        return newInstance(CONFIG_PACKAGE + "SOAPTSServerAuthConfig",
            new Class<?>[] { String.class, String.class, CallbackHandler.class, Map.class, TSLogger.class },
            layer, appContext, handler, properties, logger);
    }

    static ClientAuthConfig newClientAuthConfig(String layer, String appContext, CallbackHandler handler,
        Map<String, Object> properties, TSLogger logger) throws AuthException {
        return newInstance(CONFIG_PACKAGE + "TSClientAuthConfig",
            new Class<?>[] { String.class, String.class, CallbackHandler.class, Map.class, TSLogger.class },
            layer, appContext, handler, properties, logger);
    }

    /**
     * @param simpleName the simple name of a ServerAuthModule in the soap module package
     */
    static ServerAuthModule newServerAuthModule(String simpleName) throws AuthException {
        return newInstance(MODULE_PACKAGE + simpleName, new Class<?>[0]);
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstance(String className, Class<?>[] parameterTypes, Object... arguments) throws AuthException {
        try {
            return (T) Class.forName(className, true, SoapProfile.class.getClassLoader())
                .getConstructor(parameterTypes)
                .newInstance(arguments);
        } catch (InvocationTargetException e) {
            throw (AuthException) new AuthException("Failed to create " + className).initCause(e.getCause());
        } catch (ReflectiveOperationException | LinkageError e) {
            throw (AuthException) new AuthException("The SOAP profile is not available: " + className).initCause(e);
        }
    }
}
