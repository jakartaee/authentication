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

import java.util.Arrays;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

/**
 * Callback for PasswordValidation. This callback may be used by an authentication module to employ the password
 * validation facilities of its containing runtime. This Callback would typically be called by a
 * <code>ServerAuthModule</code> during <code>validateRequest</code> processing.
 *
 */
public class PasswordValidationCallback implements Callback {

	private Subject subject;
	private String username;
	private char[] password;
	private boolean result = false;

	/**
	 * Create a PasswordValidationCallback.
	 *
	 * @param subject The subject for authentication
	 *
	 * @param username The username to authenticate
	 *
	 * @param password tTe user's password, which may be null.
	 */
	public PasswordValidationCallback(Subject subject, String username, char[] password) {
		this.subject = subject;
		this.username = username;
		if (password != null) {
			this.password = (char[]) password.clone();
		}
	}

	/**
	 * Get the subject.
	 *
	 * @return The subject.
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Get the username.
	 *
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password.
	 *
	 * <p>
	 * Note that this method returns a reference to the password. If a clone of the array is created it is the caller's
	 * responsibility to zero out the password information after it is no longer needed.
	 *
	 * @return The password, which may be null.
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * Clear the password.
	 */
	public void clearPassword() {
		if (password != null) {
			Arrays.fill(password, ' ');
		}
	}

	/**
	 * Set the authentication result.
	 *
	 * @param result True if authentication succeeded, false otherwise
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	/**
	 * Get the authentication result.
	 *
	 * @return True if authentication succeeded, false otherwise
	 */
	public boolean getResult() {
		return result;
	}
}
