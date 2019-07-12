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

package javax.security.auth.message;

/**
 * The AuthStatus class is used to represent return values from Authentication modules and Authentication Contexts. An
 * AuthStatus value is returned when the module processing has established a corresponding request or response message
 * within the message parameters exchanged with the runtime.
 */
public class AuthStatus {

	/**
	 * Indicates that the message processing by the authentication module was successful and that the runtime is to proceed
	 * with its normal processing of the resulting message.
	 */
	public static final AuthStatus SUCCESS = new AuthStatus(1);

	/**
	 * Indicates that the message processing by the authentication module was NOT successful, and that the module replaced
	 * the application message with an error message.
	 */
	public static final AuthStatus FAILURE = new AuthStatus(2);

	/**
	 * Indicates that the message processing by the authentication module was successful and that the runtime is to proceed
	 * by sending a message returned by the authentication module.
	 */
	public static final AuthStatus SEND_SUCCESS = new AuthStatus(3);

	/**
	 * Indicates that the message processing by the authentication module was NOT successful, that the module replaced the
	 * application message with an error message, and that the runtime is to proceed by sending the error message.
	 */
	public static final AuthStatus SEND_FAILURE = new AuthStatus(4);

	/**
	 * Indicates the message processing by the authentication module is NOT complete, that the module replaced the
	 * application message with a security message, and that the runtime is to proceed by sending the security message.
	 */
	public static final AuthStatus SEND_CONTINUE = new AuthStatus(5);

	private final int value;

	private AuthStatus(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		switch (value) {
		case 1:
			return "AuthStatus.SUCCESS";
		case 2:
			return "AuthStatus.FAILURE";
		case 3:
			return "AuthStatus.SEND_SUCCESS";
		case 4:
			return "AuthStatus.SEND_FAILUR";
		case 5:
			return "AuthStatus.SEND_CONTINUE";
		default:
			return "Unknown AuthStatus";
		}
	}
}
