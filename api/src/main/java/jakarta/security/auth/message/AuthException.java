/*
 * Copyright (c) 2020, 2021 Contributors to Eclipse Foundation. All rights reserved.
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

package jakarta.security.auth.message;

import javax.security.auth.login.LoginException;

/**
 * A generic authentication exception.
 *
 */
public class AuthException extends LoginException {

    private static final long serialVersionUID = 1560420530928138346L;

    /**
     * Constructs an AuthException with no detail message. A detail message is a String that describes this particular
     * exception.
     */
    public AuthException() {
        super();
    }

    /**
     * Constructs an AuthException with the specified detail message. A detail message is a String that describes this
     * particular exception.
     *
     * @param msg The detail message.
     */
    public AuthException(String msg) {
        super(msg);
    }

    /**
     * Constructs an AuthException with the specified detail message and cause.
     *
     * @param msg The detail message.
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value
     * is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 3.0
     */
    public AuthException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    /**
     * Constructs an AuthException with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value
     * is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 3.0
     */
    public AuthException(Throwable cause) {
        super();
        initCause(cause);
    }
}
