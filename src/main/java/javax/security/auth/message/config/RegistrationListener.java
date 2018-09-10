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

/**
 * An implementation of this interface may be associated with an AuthConfigProvider registration at an AuthConfigFactory
 * at the time the AuthConfigProvider is obtained for use from the factory. The AuthConfigFactory will invoke the notify
 * method of the RegistrationListener if the corresponding provider registration is unregistered or replaced at the
 * factory.
 */

public interface RegistrationListener {

	/**
	 * Notify the listener that a registration with which it was associated was replaced or unregistered.
	 *
	 * <p>
	 * When a RegistrationListener is associated with a provider registration within the factory, the factory must call its
	 * <code>notify</code> method when the corresponding registration is unregistered or replaced.
	 * 
	 * @param layer A String identifying the one or more message layers corresponding to the registration for which the
	 * listerner is being notified.
	 *
	 * @param appContext A String value identifying the application contexts corresponding to the registration for which the
	 * listener is being notified.
	 *
	 * The factory detaches the listener from the corresponding registration once the listener has been notified for the
	 * registration.
	 * 
	 * The <code>detachListerner</code> method must be called to detach listeners that are no longer in use.
	 */
	public void notify(String layer, String appContext);

}
