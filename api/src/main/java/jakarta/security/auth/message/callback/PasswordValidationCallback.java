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

package jakarta.security.auth.message.callback;

import java.util.Arrays;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

/**
 * Callback for PasswordValidation.
 *
 * <p>
 * This callback may be used by an authentication module to employ the password validation facilities of its containing
 * runtime. This Callback would typically be called by a <code>ServerAuthModule</code> during
 * <code>validateRequest</code> processing.
 *
 * <p>
 * This callback causes the following actions to be done:
 *
 * <ol>
 * <li> Validate the credentials
 * <li> If validated set caller principal (conceptually just like <code>CallerPrincipalCallback</code> does)
 * <li> If validated and groups available set groups (conceptually just like <code>GroupPrincipalCallback</code> does)
 * </ol>
 *
 * The code below shows a hypothetical example of how a <code>PasswordValidationCallback</code> could be
 * implemented by a Jakarta Authentication implementation provided <code>CallbackHandler</code>:
 *
 * <pre>
 * {@code
 * protected void processPasswordValidation(PasswordValidationCallback pwdCallback) {
 *
 *    // 1. Validate the credentials
 *    Caller caller = ContainerSpecificStore.validate(pwdCallback.getUsername(), getPassword(pwdCallback));
 *
 *    if (caller != null) {
 *        // 2. If validated set caller principal, just like CallerPrincipalCallback does
 *        processCallerPrincipal(new CallerPrincipalCallback(pwdCallback.getSubject(), caller.getCallerPrincipal()));
 *
 *        if (!caller.getGroups().isEmpty()) {
 *            // 3. If validated and groups available set groups, just like GroupPrincipalCallback does
 *            processGroupPrincipal(new GroupPrincipalCallback(pwdCallback.getSubject(), caller.getGroupsAsArray()));
 *        }
 *
 *        pwdCallback.setResult(true);
 *    }
 * }
 * }
 * </pre>
 *
 * Note that in this example: <br>
 * <ul>
 * <li> <code>processCallerPrincipal</code> represents how the <code>CallbackHandler</code> would handle
 * the <code>CallerPrincipalCallback</code>.
 * <li> <code>processGroupPrincipal</code> represents how the <code>CallbackHandler</code> would handle
 * the <code>GroupPrincipalCallback</code>.
 * <li> <code>Caller</code> and <code>ContainerSpecificStore</code> are hypothetical implementation specific types.
 * </ul>
 */
public class PasswordValidationCallback implements Callback {

    private Subject subject;
    private String username;
    private char[] password;
    private boolean result;

    /**
     * Create a PasswordValidationCallback.
     *
     * @param subject The subject for authentication
     *
     * @param username The username to authenticate
     *
     * @param password The user's password, which may be null.
     */
    public PasswordValidationCallback(Subject subject, String username, char[] password) {
        this.subject = subject;
        this.username = username;
        if (password != null) {
            this.password = password.clone();
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
