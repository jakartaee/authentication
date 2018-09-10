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

package javax.security.auth.message.callback;

import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;

/**
 * Callback for acquiring a shared secret from a key repository. This Callback may be used by client or server
 * authentication modules to obtain shared secrets (for example, passwords) without relying on a user during the
 * Callback processing. This Callback is typically empoyed by <code>ClientAuthModules</code> invoked from intermediate
 * components that need to acquire a password to authenticate to their target service.
 *
 */
public class SecretKeyCallback implements Callback {

	private Request request;
	private SecretKey key;

	/**
	 * Marker interface for secret key request types.
	 */
	public static interface Request {
	};

	/**
	 * Request type for secret keys that are identified using an alias.
	 */
	public static class AliasRequest implements Request {
		private String alias;

		/**
		 * Construct an AliasRequest with an alias.
		 *
		 * <p>
		 * The alias is used to directly identify the secret key to be returned.
		 *
		 * <p>
		 * If the alias is null, the handler of the callback relies on its own default.
		 *
		 * @param alias Name identifier for the secret key, or null.
		 */
		public AliasRequest(String alias) {
			this.alias = alias;
		}

		/**
		 * Get the alias.
		 *
		 * @return The alias, or null.
		 */
		public String getAlias() {
			return alias;
		}
	}

	/**
	 * Constructs this SecretKeyCallback with a secret key Request object.
	 *
	 * <p>
	 * The <i>request</i> object identifies the secret key to be returned.
	 *
	 * If the alias is null, the handler of the callback relies on its own default.
	 *
	 * @param request Request object identifying the secret key, or null.
	 */
	public SecretKeyCallback(Request request) {
		this.request = request;
	}

	/**
	 * Used by the CallbackHandler to get the Request object which identifies the secret key to be returned.
	 *
	 * @return The Request object which identifies the private key to be returned, or null. If null, the handler of the
	 * callback relies on its own default.
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Used by the CallbackHandler to set the requested secret key within the Callback.
	 *
	 * @param key The secret key, or null if no key was found.
	 */
	public void setKey(SecretKey key) {
		this.key = key;
	}

	/**
	 * Used to obtain the secret key set within the Callback.
	 *
	 * @return The secret key, or null if no key was found.
	 */
	public SecretKey getKey() {
		return key;
	}
}
