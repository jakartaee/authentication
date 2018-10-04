/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates and others.
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

package javax.security.auth.message.config;

import javax.security.auth.message.ClientAuth;
import javax.security.auth.message.module.ClientAuthModule;

/**
 * This ClientAuthContext class encapsulates ClientAuthModules that are used to secure service requests made by a
 * client, and to validate any responses received to those requests. A caller typically uses this class in the following
 * manner:
 *
 * <ol>
 *   <li>Retrieve an instance of this class by using ClientAuthConfig.getAuthContext.
 *   <li>Invoke <i>secureRequest</i>. <br>
 *       ClientAuthContext implementation invokes secureRequest of one or more encapsulated ClientAuthModules. Modules might
 *       attach credentials to request (for example, a user name and password), and/or secure the request (for example, sign
 *       and encrypt the request).
 *   <li>Send request and receive response.
 *   <li>Invoke <i>validateResponse</i>. <br>
 *       ClientAuthContext implementation invokes validateResponse of one or more encapsulated ClientAuthModules. Modules
 *       verify or decrypt response as necessary.
 *   <li>Invoke <i>cleanSubject</i> method (as necessary) to clean up any authentication state in Subject.
 * </ol>
 * 
 * <p>
 * A ClientAuthContext instance may be used concurrently by multiple callers.
 *
 * <p>
 * Implementations of this interface are responsible for constructing and initializing the encapsulated modules. The
 * initialization step includes passing the relevant request and response MessagePolicy objects to the encapsulated
 * modules. The MessagePolicy objects are obtained by the ClientAuthConfig instance used to obtain the ClientAuthContext
 * object. See <code>ClientAuthConfig.getAuthContext</code> for more information.
 *
 * <p>
 * Implementations of this interface are instantiated by their associated configuration object such that they know which
 * modules to invoke, in what order, and how results returned by preceding modules are to influence subsequent module
 * invocations.
 *
 * <p>
 * Calls to the inherited methods of this interface delegate to the corresponding methods of the encapsulated
 * authentication modules.
 *
 * @see ClientAuthConfig
 * @see ClientAuthModule
 */
public interface ClientAuthContext extends ClientAuth {

}
