/*
 * jGuard is a security framework based on top of jaas (java authentication and authorization security).
 * it is written for web applications, to resolve simply, access control problems.
 * version $Name$
 * http://sourceforge.net/projects/jguard/
 *
 * Copyright (C) 2004-2011  Charles Lescot
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * jGuard project home page:
 * http://sourceforge.net/projects/jguard/
 */

package net.sf.jguard.jee.authorization.filters;

import com.google.inject.Provider;
import net.sf.jguard.core.authorization.filters.AuthorizationFilter;
import net.sf.jguard.core.authorization.filters.LastAccessDeniedRegistrationFilter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
public class HttpServletGuestAuthorizationFiltersProvider implements Provider<List<AuthorizationFilter<HttpServletRequest, HttpServletResponse>>> {

    private List<AuthorizationFilter<HttpServletRequest, HttpServletResponse>> authorizationFilters;

    @Inject
    public HttpServletGuestAuthorizationFiltersProvider(LastAccessDeniedRegistrationFilter<HttpServletRequest, HttpServletResponse> lastAccessDeniedTriggerFilter,
                                                        List<AuthorizationFilter<HttpServletRequest, HttpServletResponse>> authorizationFilters) {
        authorizationFilters.add(lastAccessDeniedTriggerFilter);
        this.authorizationFilters = authorizationFilters;
    }

    public List<AuthorizationFilter<HttpServletRequest, HttpServletResponse>> get() {
        return authorizationFilters;
    }
}

