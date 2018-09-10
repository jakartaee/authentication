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

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

/**
 * Callback establishing group principals within the argument subject.
 * 
 * <p>
 * This callback is intended to be called by a <code>serverAuthModule</code> during its <code>validateRequest</code>
 * processing.
 * 
 */
public class GroupPrincipalCallback implements Callback {

	private Subject subject;
	private String[] groups;

	/**
	 * Create a GroupPrincipalCallback to establish the container's representation of the corresponding group principals
	 * within the Subject.
	 * 
	 * <p>
	 * When a null value is passed to the <code>groups</code> argument, the handler will establish the container's
	 * representation of no group principals within the Subject. Otherwise, the handler's processing of this callback is
	 * additive, yielding the union (without duplicates) of the principals existing within the Subject, and those created
	 * with the names occurring within the argument array. The CallbackHandler will define the type of the created
	 * principals.
	 *
	 * @param subject The Subject in which the container will create group principals.
	 *
	 * @param groups An array of Strings, where each element contains the name of a group that will be used to create a
	 * corresponding group principal within the Subject.
	 */
	public GroupPrincipalCallback(Subject subject, String[] groups) {
		this.subject = subject;
		this.groups = groups;
	}

	/**
	 * Get the Subject in which the handler will establish the group principals.
	 *
	 * @return The subject.
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Get the array of group names.
	 * 
	 * <p>
	 * When the return value is null, the handler will establish the container's representation of no group principals
	 * within the Subject.
	 *
	 * Otherwise, the handler's processing of this callback is additive, yielding the union (without duplicates) of the
	 * principals created with the names in the returned array and those existing within the Subject.
	 *
	 * @return Null, or an array containing 0 or more String group names.
	 */
	public String[] getGroups() {
		return groups;
	}

}
