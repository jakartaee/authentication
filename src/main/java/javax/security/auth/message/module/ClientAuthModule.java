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

package javax.security.auth.message.module;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.ClientAuth;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ClientAuthContext;

/**
 * A ClientAuthModule secures request messages, and validates received response messages.
 *
 * <p>
 * A module implementation should assume it may be used to secure different requests as different clients. A module
 * should also assume it may be used concurrently by multiple callers. It is the module implementation's responsibility
 * to properly save and restore any state as necessary. A module that does not need to do so may remain completely
 * stateless.
 *
 * <p>
 * Every implementation of the interface must provide a public zero argument constructor.
 *
 * @see ClientAuthContext
 */
public interface ClientAuthModule extends ClientAuth {

	/**
	 * Initialize this module with request and response message policies to enforce, a CallbackHandler, and any
	 * module-specific configuration properties.
	 *
	 * <p>
	 * The request policy and the response policy must not both be null.
	 *
	 * @param requestPolicy The request policy this module must enforce, or null.
	 *
	 * @param responsePolicy The response policy this module must enforce, or null.
	 *
	 * @param handler CallbackHandler used to request information.
	 *
	 * @param options A Map of module-specific configuration properties.
	 *
	 * @exception AuthException If module initialization fails, including for the case where the options argument contains
	 * elements that are not supported by the module.
	 */
	void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException;

	/**
	 * Get the one or more Class objects representing the message types supported by the module.
	 *
	 * @return An array of Class objects where each element defines a message type supported by the module. A module should
	 * return an array containing at least one element. An empty array indicates that the module will attempt to support any
	 * message type. This method never returns null.
	 */
	public Class[] getSupportedMessageTypes();

}
