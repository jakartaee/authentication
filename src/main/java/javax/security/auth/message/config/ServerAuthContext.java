/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

import javax.security.auth.message.ServerAuth;
import javax.security.auth.message.module.ServerAuthModule;

/**
 * This ServerAuthContext class encapsulates ServerAuthModules that are used
 * to validate service requests received from clients, and to secure any 
 * response returned for those requests.  A caller typically uses this class
 * in the following manner:
 *
 * <ol>
 * <li> Retrieve an instance of this class via
 *      ServerAuthConfig.getAuthContext.
 * <li> Invoke <i>validateRequest</i>.
 *      <br>
 *	ServerAuthContext implementation invokes validateRequest of 
 *      one or more encapsulated
 *	ServerAuthModules.  Modules validate credentials present in request
 *	(for example, decrypt and verify a signature).
 * <li> If credentials valid and sufficient, authentication complete.	
 *      <br>
 *      Perform authorization check on authenticated identity and,
 *	if successful, dispatch to requested service application.
 * <li> Service application finished.
 * <li> Invoke <i>secureResponse</i>.
 *      <br>
 *	ServerAuthContext implementation invokes secureResponse of 
 *      one or more encapsulated
 *	ServerAuthModules.  Modules secure response
 *	(sign and encrypt response, for example), and prepare response message.
 * <li> Send secured response to client.
 * <li> Invoke <i>cleanSubject</i> (as necessary)
 *	to clean up any authentication state in Subject(s).
 * </ol>
 *
 * <p> A ServerAuthContext instance may be used concurrently by multiple
 * callers.
 *
 * <p> Implementations of this interface are responsible for constructing
 * and initializing the encapsulated modules.  The initialization step
 * includes passing the relevant request and response MessagePolicy objects
 * to the encapsulated modules. The MessagePolicy objects are obtained
 * by the ServerAuthConfig instance used to ontain the ServerAuthContext 
 * object.
 * See <code>ServerAuthConfig.getAuthContext</code> for more information.
 *
 * <p> Implementations of this interface are instantiated by their associated
 * configuration object such that they know which modules to invoke, in what 
 * order, and how results returned by preceding modules are to influence 
 * subsequent module invocations.
 *
 * <p> Calls to the inherited methods of this interface delegate to the
 * corresponding methods of the encapsulated authentication modules.
 *
 * @see ServerAuthConfig
 * @see ServerAuthModule
 */
public interface ServerAuthContext extends ServerAuth {

}








