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

import java.security.cert.CertStore;
import javax.security.auth.callback.Callback;

/**
 * Callback for CertStore.
 *
 * <p>
 * A CertStore is a generic repository for certificates. CertStores may be searched to locate public key certificates,
 * as well as to put together certificate chains. Such a search may be necessary when the caller needs to verify a
 * signature.
 *
 */
public class CertStoreCallback implements Callback {

	private CertStore certStore;

	/**
	 * Create a CertStoreCallback.
	 */
	public CertStoreCallback() {
	}

	/**
	 * Used by the CallbackHandler to set the CertStore within the Callback.
	 *
	 * @param certStore The certificate store, which may be null
	 */
	public void setCertStore(CertStore certStore) {
		this.certStore = certStore;
	}

	/**
	 * Used by the CertStore user to obtain the CertStore set within the Callback.
	 *
	 * @return The CertStore, or null.
	 */
	public CertStore getCertStore() {
		return certStore;
	}
}
