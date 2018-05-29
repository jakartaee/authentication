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

import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;

/**
 * Callback for setting the container's caller (or Remote user) principal.
 * This callback is intended to be called by a <code>serverAuthModule</code> 
 * during its <code>validateRequest</code> processing. 
 *
 */
public class CallerPrincipalCallback implements Callback {

    private Subject subject;
    private Principal principal;
    private String name;

    /**
     * Create a CallerPrincipalCallback to set the container's 
     * representation of the caller principal
     *
     * @param s The Subject in which the container will distinguish the
     * caller identity.
     *
     * @param p The Principal that will be distinguished as the caller
     * principal. This value may be null.
     * <p> 
     * The CallbackHandler must use the argument Principal to establish the caller
     * principal associated with the invocation being processed by the container.
     * When the argument Principal is null, the handler must establish the 
     * container's representation of the unauthenticated caller principal. The 
     * handler may perform principal mapping of non-null argument Principal 
     * values, but it must be possible to configure the handler such that it 
     * establishes the non-null argument Principal as the caller principal.
     */
    
    
    public CallerPrincipalCallback(Subject s, Principal p) { 
	subject = s;
	principal = p;
        name = null;
    }

    /**
     * Create a CallerPrincipalCallback to set the container's 
     * representation of the caller principal.
     *
     * @param s The Subject in which the container will distinguish the
     * caller identity.
     *
     * @param n The String value that will be returned when getName() is
     * called on the principal established as the caller principal or null.
     * <p> 
     *  The CallbackHandler must use the n argument to establish the caller 
     * principal associated with the invocation being processed by the container.
     * When the n argument is null, the handler must establish the container's
     * representation of the unauthenticated caller principal (which may or may 
     * not be equal to null, depending on the requirements of the container type
     * ). The handler may perform principal mapping of non-null values of n, but
     * it must be possible to configure the handler such that it establishes the
     * non-null argument value as the value returned when getName is called on 
     * the established principal.
     */
    public CallerPrincipalCallback(Subject s, String n) { 
	subject = s;
	principal = null;
        name = n;
    }

    /**
     * Get the Subject in which the handler will distinguish the caller 
     * principal
     *
     * @return The subject.
     */
    public Subject getSubject() {
	return subject;
    }

    /**
     * Get the caller principal.
     *
     * @return The principal or null.
     * <p> 
     * When the values returned by this method and the getName methods 
     * are null, the handler must establish the container's representation 
     * of the unauthenticated caller principal within the Subject.
     */
    public Principal getPrincipal() {
	return principal;
    }

    /**
     * Get the caller principal name.
     *
     * @return The principal name or null.
     * <p> 
     * When the values returned by this method and the getPrincipal methods 
     * are null, the handler must establish the container's representation 
     * of the unauthenticated caller principal within the Subject.
     */
    public String getName() {
	return name;
    }
}
