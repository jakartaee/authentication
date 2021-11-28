/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates and others.
 * All rights reserved.
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

package jakarta.security.auth.message.config;

import java.util.Map;
import javax.security.auth.Subject;

import jakarta.security.auth.message.AuthException;

/**
 * This interface describes a configuration of ServerAuthConfiguration objects for a message layer and application
 * context (for example, the messaging context of a specific application, or set of applications).
 *
 * <p>
 * Implementations of this interface are returned by an AnthConfigProvider.
 *
 * <p>
 * Callers interact with a ServerAuthConfig to obtain ServerAuthContext objects suitable for processing a given message
 * exchange at the layer and within the application context of the ServerAuthConfig.
 *
 * Each ServerAuthContext object is responsible for instantiating, initializing, and invoking the one or more
 * ServerAuthModules encapsulated in the ServerAuthContext.
 *
 * <p>
 * After having acquired a ServerAuthContext, a caller operates on the context to cause it to invoke the encapsulated
 * ServerAuthModules to validate service requests and to secure service responses.
 *
 * @see AuthConfigProvider
 */
public interface ServerAuthConfig extends AuthConfig {

    /**
     * Get a ServerAuthContext instance from this ServerAuthConfig.
     *
     * <p>
     * The implementation of this method returns a ServerAuthContext instance that encapsulates the ServerAuthModules used
     * to validate requests and secure responses associated with the given <i>authContextID</i>.
     *
     * <p>
     * Specifically, this method accesses this ServerAuthConfig object with the argument <i>authContextID</i> to determine
     * the ServerAuthModules that are to be encapsulated in the returned ServerAuthContext instance.
     *
     * <P>
     * The ServerAuthConfig object establishes the request and response MessagePolicy objects that are passed to the
     * encapsulated modules when they are initialized by the returned ServerAuthContext instance. It is the modules'
     * responsibility to enforce these policies when invoked.
     *
     * @param authContextID An identifier used to index the provided <i>config</i>, or null. This value must be identical to
     * the value returned by the <code>getAuthContextID</code> method for all <code>MessageInfo</code> objects passed to the
     * <code>validateRequest</code> method of the returned ServerAuthContext.
     *
     * @param serviceSubject A Subject that represents the source of the service response to be secured by the acquired
     * authentication context. The principal and credentials of the Subject may be used to select or acquire the
     * authentication context. If the Subject is not null, additional Principals or credentials (pertaining to the source of
     * the response) may be added to the Subject. A null value may be passed for this parameter.
     *
     * @param properties A Map object that may be used by the caller to augment the properties that will be passed to the
     * encapsulated modules at module initialization. The null value may be passed for this parameter.
     *
     * @return A ServerAuthContext instance that encapsulates the ServerAuthModules used to secure and validate
     * requests/responses associated with the given <i>authContextID</i>, or null (indicating that no modules are
     * configured).
     *
     * @exception AuthException If this method fails.
     */
    ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map<String, Object> properties) throws AuthException;

}
