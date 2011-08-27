/*
 * jGuard is a security framework based on top of jaas (java authentication and authorization security).
 * it is written for web applications, to resolve simply, access control problems.
 * version $Name$
 * http://sourceforge.net/projects/jguard/
 *
 * Copyright (C) 2004-2011  Charles GAY
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

package net.sf.jguard.jsf.authentication.filters;

import javax.inject.Inject;
import net.sf.jguard.core.authentication.filters.AuthenticationFilter;
import net.sf.jguard.core.authorization.filters.AuthorizationFilter;
import net.sf.jguard.core.enforcement.GuestPolicyEnforcementPointFilter;

import javax.faces.context.FacesContext;
import java.util.List;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Gay</a>
 */
public class JSFGuestPolicyEnforcementPointFilter extends GuestPolicyEnforcementPointFilter<FacesContext, FacesContext> {

    @Inject
    public JSFGuestPolicyEnforcementPointFilter(List<AuthenticationFilter<FacesContext, FacesContext>> guestAuthenticationFilters,
                                                List<AuthorizationFilter<FacesContext, FacesContext>> guestAuthorizationFilters,
                                                List<AuthenticationFilter<FacesContext, FacesContext>> restfulAuthenticationFilters,
                                                List<AuthorizationFilter<FacesContext, FacesContext>> restfulAuthorizationFilters) {
        super(guestAuthenticationFilters, guestAuthorizationFilters, restfulAuthenticationFilters, restfulAuthorizationFilters);
    }
}