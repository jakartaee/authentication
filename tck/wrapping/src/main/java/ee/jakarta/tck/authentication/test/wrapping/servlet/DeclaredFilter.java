/*
 * Copyright (c) 2022-2022 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.authentication.test.wrapping.servlet;

import java.io.IOException;
import java.io.Writer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This Filter tests that the request and response objects it receives are the ones marked as wrapped by the SAM that executed
 * before the Servlet was called.
 *
 * @author Arjan Tijms
 *
 */
@WebFilter(urlPatterns="/*")
public class DeclaredFilter implements Filter {

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Writer writer = response.getWriter();

        writer.write("declared filter request isWrapped: " + request.getAttribute("isWrapped"));
        writer.write("\n");
        writer.write("declared filter response isWrapped: " + ((HttpServletResponse)response).getHeader("isWrapped"));
        writer.write("\n");

        chain.doFilter(request, response);
    }

	@Override
    public void destroy() {
	}

}
