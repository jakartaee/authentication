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
 * This interface encapsulates the configuration of ClientAuthContext objects for a message layer and application
 * context (for example, the messaging context of a specific application, or set of applications).
 *
 * <p>
 * Implementations of this interface are returned by an AuthConfigProvider.
 *
 * <p>
 * Callers interact with a ClientAuthConfig to obtain ClientAuthContext objects suitable for processing a given message
 * exchange at the layer and within the application context of the ClientAuthConfig.
 *
 * Each ClientAuthContext object is responsible for instantiating, initializing, and invoking the one or more
 * ClientAuthModules encapsulated in the ClientAuthContext.
 *
 * <p>
 * After having acquired a ClientAuthContext, a caller operates on the context to cause it to invoke the encapsulated
 * ClientAuthModules to secure client requests and to validate server responses.
 *
 * @see AuthConfigProvider
 */
public interface ClientAuthConfig extends AuthConfig {

    /**
     * Get a ClientAuthContext instance from this ClientAuthConfig.
     *
     * <p>
     * The implementation of this method returns a ClientAuthContext instance that encapsulates the ClientAuthModules used
     * to secure and validate requests/responses associated with the given <i>authContextID</i>.
     *
     * <p>
     * Specifically, this method accesses this ClientAuthConfig object with the argument <i>authContextID</i> to determine
     * the ClientAuthModules that are to be encapsulated in the returned ClientAuthContext instance.
     * 
     * <P>
     * The ClientAuthConfig object establishes the request and response MessagePolicy objects that are passed to the
     * encapsulated modules when they are initialized by the returned ClientAuthContext instance. It is the modules'
     * responsibility to enforce these policies when invoked.
     * 
     * @param authContextID An String identifier used to index the provided <i>config</i>, or null. This value must be
     * identical to the value returned by the <code>getAuthContextID</code> method for all <code>MessageInfo</code> objects
     * passed to the <code>secureRequest</code> method of the returned ClientAuthContext.
     *
     * @param clientSubject A Subject that represents the source of the service request to be secured by the acquired
     * authentication context. The principals and credentials of the Subject may be used to select or acquire the
     * authentication context. If the Subject is not null, additional Principals or credentials (pertaining to the source of
     * the request) may be added to the Subject. A null value may be passed for this parameter.
     *
     * @param properties A Map object that may be used by the caller to augment the properties that will be passed to the
     * encapsulated modules at module initialization. The null value may be passed for this parameter.
     *
     * @return A ClientAuthContext instance that encapsulates the ClientAuthModules used to secure and validate
     * requests/responses associated with the given <i>authContextID</i>, or null (indicating that no modules are
     * configured).
     *
     * @exception AuthException If this method fails.
     */
    ClientAuthContext getAuthContext(String authContextID, Subject clientSubject, Map properties) throws AuthException;

}
