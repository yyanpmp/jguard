/*
 * jGuard is a security framework based on top of jaas (java authentication and authorization security).
 * it is written for web applications, to resolve simply, access control problems.
 * version $Name$
 * http://sourceforge.net/projects/jguard/
 *
 * Copyright (C) 2004-2010  Charles Lescot
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

package net.sf.jguard.core.enforcement;

import net.sf.jguard.core.authentication.Stateful;
import net.sf.jguard.core.authentication.filters.AuthenticationFilter;
import net.sf.jguard.core.authorization.filters.AuthorizationFilter;
import net.sf.jguard.core.filters.Filter;
import net.sf.jguard.core.lifecycle.MockRequestAdapter;
import net.sf.jguard.core.lifecycle.MockResponseAdapter;

import javax.inject.Inject;
import java.util.List;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
public class MockPolicyEnforcementPoint extends PolicyEnforcementPoint<MockRequestAdapter, MockResponseAdapter> {
    @Inject
    public MockPolicyEnforcementPoint(@Stateful List<AuthenticationFilter<MockRequestAdapter, MockResponseAdapter>> authenticationFilters,
                                      List<AuthorizationFilter<MockRequestAdapter, MockResponseAdapter>> authorizationFilters,
                                      boolean propagateThrowable) {
        super(authenticationFilters, authorizationFilters, propagateThrowable);
    }

    @Override
    protected void sendThrowable(MockResponseAdapter response, Throwable throwable) {

    }

    public void setFilters(List<Filter<MockRequestAdapter, MockResponseAdapter>> filters) {
        this.filters = filters;
    }
}
