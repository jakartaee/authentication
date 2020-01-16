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

package jakarta.security.auth.message.callback;

import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

/**
 * Callback for setting the container's caller (or remote user) principal.
 * 
 * <p>
 * This callback is intended to be called by a <code>serverAuthModule</code> during its <code>validateRequest</code>
 * processing.
 *
 */
public class CallerPrincipalCallback implements Callback {

	private Subject subject;
	private Principal principal;
	private String name;

	/**
	 * Create a CallerPrincipalCallback to set the container's representation of the caller principal
	 * 
	 * <p>
	 * The CallbackHandler must use the argument <code>Principal</code> to establish the caller principal associated with
	 * the invocation being processed by the container. When the argument <code>Principal</code> is null, the handler must
	 * establish the container's representation of the unauthenticated caller principal.
	 * 
	 * <p>
	 * The handler may perform principal mapping of non-null argument <code>Principal</code> values, but it must be possible
	 * to configure the handler such that it establishes the non-null argument <code>Principal</code> as the caller
	 * principal.
	 *
	 * @param subject The Subject in which the container will distinguish the caller identity.
	 *
	 * @param principal The Principal that will be distinguished as the caller principal. This value may be null.
	 */
	public CallerPrincipalCallback(Subject subject, Principal principal) {
		this.subject = subject;
		this.principal = principal;
	}

	/**
	 * Create a CallerPrincipalCallback to set the container's representation of the caller principal.
	 * 
	 * <p>
	 * The CallbackHandler must use the <code>name</code> argument to establish the caller principal associated with the
	 * invocation being processed by the container. When the <code>name</code> argument is null, the handler must establish
	 * the container's representation of the unauthenticated caller principal (which may or may not be equal to null,
	 * depending on the requirements of the container type).
	 * 
	 * <p>
	 * The handler may perform principal mapping of non-null values of <code>name</code>, but it must be possible to
	 * configure the handler such that it establishes the non-null argument value as the value returned when
	 * <code>getName</code> is called on the established principal.
	 *
	 * @param subject The Subject in which the container will distinguish the caller identity.
	 *
	 * @param name The String value that will be returned when <code>getName()</code> is called on the principal established
	 * as the caller principal or null.
	 */
	public CallerPrincipalCallback(Subject subject, String name) {
		this.subject = subject;
		this.name = name;
	}

	/**
	 * Get the Subject in which the handler will distinguish the caller principal
	 *
	 * @return The subject.
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Get the caller principal.
	 * 
	 * <p>
	 * When the values returned by this method and the getName methods are null, the handler must establish the container's
	 * representation of the unauthenticated caller principal within the Subject.
	 *
	 * @return The principal or null.
	 */
	public Principal getPrincipal() {
		return principal;
	}

	/**
	 * Get the caller principal name.
	 * 
	 * <p>
	 * When the values returned by this method and the getPrincipal methods are null, the handler must establish the
	 * container's representation of the unauthenticated caller principal within the Subject.
	 *
	 * @return The principal name or null.
	 */
	public String getName() {
		return name;
	}
}
