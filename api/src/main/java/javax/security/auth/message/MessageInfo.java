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

import java.util.Map;

/**
 * A message processing runtime uses this interface to pass messages and message processing state to authentication
 * contexts for processing by authentication modules.
 * <p>
 * This interface encapsulates a request message object and a response message object for a message exchange. This
 * interface may also be used to associate additional context in the form of key/value pairs, with the encapsulated
 * messages.
 * <p>
 * Every implementation of this interface should provide a zero argument constructor, and a constructor which takes a
 * single Map argument. Additional constructors may also be provided.
 *
 * @see Map
 */
public interface MessageInfo {

	/**
	 * Get the request message object from this MessageInfo.
	 *
	 * @return An object representing the request message, or null if no request message is set within the MessageInfo.
	 */
	Object getRequestMessage();

	/**
	 * Get the response message object from this MessageInfo.
	 *
	 * @return an object representing the response message, or null if no response message is set within the MessageInfo.
	 */
	Object getResponseMessage();

	/**
	 * Set the request message object in this MessageInfo.
	 * 
	 * @param request An object representing the request message.
	 */
	void setRequestMessage(Object request);

	/**
	 * Set the response message object in this MessageInfo.
	 * 
	 * @param response An object representing the response message.
	 */
	void setResponseMessage(Object response);

	/**
	 * Get (a reference to) the Map object of this MessageInfo. Operations performed on the acquired Map must effect the Map
	 * within the MessageInfo.
	 * 
	 * @return the Map object of this MessageInfo. This method never returns null. If a Map has not been associated with the
	 * MessageInfo, this method instantiates a Map, associates it with this MessageInfo, and then returns it.
	 */
	Map getMap();

}
