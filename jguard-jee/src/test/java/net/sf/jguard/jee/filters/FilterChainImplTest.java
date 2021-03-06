/*
jGuard is a security framework based on top of jaas (java authentication and authorization security).
it is written for web applications, to resolve simply, access control problems.
version $Name$
http://sourceforge.net/projects/jguard/

Copyright (C) 2004  Charles Lescot

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


jGuard project home page:
http://sourceforge.net/projects/jguard/

*/

package net.sf.jguard.jee.filters;

import com.mycila.testing.junit.MycilaJunitRunner;
import net.sf.jguard.core.authentication.filters.AuthenticationFilter;
import net.sf.jguard.core.authentication.manager.AuthenticationManagerModule;
import net.sf.jguard.core.authorization.AuthorizationBindings;
import net.sf.jguard.core.authorization.filters.AuthorizationFilter;
import net.sf.jguard.core.filters.FilterChain;
import net.sf.jguard.core.lifecycle.Request;
import net.sf.jguard.core.lifecycle.Response;
import net.sf.jguard.ext.authentication.manager.XmlAuthenticationManager;
import net.sf.jguard.jee.HttpServletPolicyEnforcementPoint;
import net.sf.jguard.jee.HttpServletRequestAdapter;
import net.sf.jguard.jee.HttpServletResponseAdapter;
import net.sf.jguard.jee.JGuardJEETest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
@RunWith(MycilaJunitRunner.class)
public class FilterChainImplTest extends JGuardJEETest {


    @Inject
    AuthorizationBindings<HttpServletRequestAdapter, HttpServletResponseAdapter> authorizationBindings;


    @Test
    public void testNominalCase() {

        List<AuthenticationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>> authenticationFilters = new ArrayList<AuthenticationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>>();
        List<AuthorizationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>> authorizationFilters = new ArrayList<AuthorizationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>>();
        final AuthenticationFilter mock = mock(AuthenticationFilter.class);
        authenticationFilters.add(mock);
        final AuthorizationFilter mock2 = mock(AuthorizationFilter.class);
        authorizationFilters.add(mock2);
        final Request request = mock(Request.class);
        final Response response = mock(Response.class);
        HttpServletPolicyEnforcementPoint policyEnforcementPoint = new HttpServletPolicyEnforcementPoint(authenticationFilters, authorizationFilters, propagateThrowable);

        final FilterChain chain = policyEnforcementPoint;

        chain.doFilter(request, response);
        //we want the filterChain call once the filter
        verify(mock).doFilter(request, response, chain);


    }

    /**
     * we test that too many calls to filter.doFilter will throw an IllegalStateExceptions.
     */
    @Test
    public void testTooManyDoFilterCalls() {

        //initialize filters
        List<AuthenticationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>> authenticationFilters = new ArrayList<AuthenticationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>>();
        List<AuthorizationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>> authorizationFilters = new ArrayList<AuthorizationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>>();
        final AuthenticationFilter mock = mock(AuthenticationFilter.class);
        authenticationFilters.add(mock);
        final AuthorizationFilter mock2 = mock(AuthorizationFilter.class);
        authorizationFilters.add(mock2);
        final Request request = mock(Request.class);
        final Response response = mock(Response.class);
        final FilterChain chain = new HttpServletPolicyEnforcementPoint(authenticationFilters, authorizationFilters, propagateThrowable);
        final HttpServletRequestAdapter requestAdapter = new HttpServletRequestAdapter(httpServletRequest);
        final HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(httpServletResponse);


        try {
            chain.doFilter(request, response);
            chain.doFilter(request, response);
            chain.doFilter(request, response);
            chain.doFilter(request, response);


            Assert.fail();
        } catch (IllegalStateException ise) {
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }


    }

    @Override
    protected AuthenticationManagerModule buildAuthenticationManagerModule() {
        return new AuthenticationManagerModule(APPLICATION_NAME, authenticationXmlFileLocation, XmlAuthenticationManager.class);
    }


}
