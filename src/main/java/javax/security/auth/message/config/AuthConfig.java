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

import javax.security.auth.message.MessageInfo;

/**
 * This interface defines the common functionality implemented by Authentication context configuration objects.
 *
 * @see ClientAuthContext
 * @see ServerAuthContext
 */
public interface AuthConfig {
	/**
	 * Get the message layer name of this authentication context configuration object.
	 *
	 * @return The message layer name of this configuration object, or null if the configuration object pertains to an
	 * unspecified message layer.
	 */
	String getMessageLayer();

	/**
	 * Get the application context identifier of this authentication context configuration object.
	 *
	 * @return The String identifying the application context of this configuration object, or null if the configuration
	 * object pertains to an unspecified application context.
	 */
	String getAppContext();

	/**
	 * Get the authentication context identifier corresponding to the request and response objects encapsulated in
	 * messageInfo.
	 *
	 * @param messageInfo A contextual Object that encapsulates the client request and server response objects.
	 *
	 * @return The authentication context identifier corresponding to the encapsulated request and response objects, or
	 * null.
	 *
	 * @throws IllegalArgumentException If the type of the message objects incorporated in messageInfo are not compatible
	 * with the message types supported by this authentication context configuration object.
	 */

	public String getAuthContextID(MessageInfo messageInfo);

	/**
	 * Causes a dynamic authentication context configuration object to update the internal state that it uses to process
	 * calls to its <code>getAuthContext</code> method.
	 *
	 * @exception SecurityException If the caller does not have permission to refresh the configuration object, or if an
	 * error occurred during the update.
	 */
	public void refresh();

	/**
	 * Used to determine whether the authentication context configuration object encapsulates any protected authentication
	 * contexts.
	 *
	 * @return True if the configuration object encapsulates at least one protected authentication context. Otherwise, this
	 * method returns false.
	 */
	public boolean isProtected();
}
