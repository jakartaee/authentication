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

package javax.security.auth.message.callback;

import java.security.KeyStore;
import javax.security.auth.callback.Callback;

/**
 * Callback for trusted certificate KeyStore.
 *
 * <p>
 * A trusted certificate KeyStore may be used to determine whether a given certificate chain can be trusted.
 *
 */
public class TrustStoreCallback implements Callback {

	private KeyStore trustStore;

	/**
	 * Used by the CallbackHandler to set the trusted certificate keystore within the Callback.
	 *
	 * @param trustStore The trusted certificate KeyStore, which must already be loaded.
	 */
	public void setTrustStore(KeyStore trustStore) {
		this.trustStore = trustStore;
	}

	/**
	 * Used by the TrustStore user to obtain the TrustStore set within the Callback.
	 *
	 * @return The trusted certificate KeyStore. The KeyStore is guaranteed to already be loaded.
	 */
	public KeyStore getTrustStore() {
		return trustStore;
	}
}
