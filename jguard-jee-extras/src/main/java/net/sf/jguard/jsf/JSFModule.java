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

package net.sf.jguard.jsf;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import net.sf.jguard.core.authentication.AuthenticationServicePoint;
import net.sf.jguard.core.authentication.Stateful;
import net.sf.jguard.core.authentication.callbackhandler.AsynchronousJGuardCallbackHandler;
import net.sf.jguard.core.authentication.filters.AuthenticationChallengeFilter;
import net.sf.jguard.core.authentication.filters.AuthenticationFilter;
import net.sf.jguard.core.authentication.schemes.AuthenticationSchemeHandler;
import net.sf.jguard.core.authorization.AuthorizationBindings;
import net.sf.jguard.core.authorization.filters.AuthorizationFilter;
import net.sf.jguard.core.authorization.filters.PolicyDecisionPoint;
import net.sf.jguard.core.enforcement.GuestPolicyEnforcementPointFilter;
import net.sf.jguard.core.enforcement.PolicyEnforcementPoint;
import net.sf.jguard.core.lifecycle.Request;
import net.sf.jguard.core.lifecycle.Response;
import net.sf.jguard.jee.authentication.http.JGuardServletRequestWrapper;
import net.sf.jguard.jsf.authentication.JSFAuthenticationServicePoint;
import net.sf.jguard.jsf.authentication.filters.JSFAuthenticationChallengeFilter;
import net.sf.jguard.jsf.authentication.filters.JSFAuthenticationFiltersProvider;
import net.sf.jguard.jsf.authentication.filters.JSFGuestPolicyEnforcementPointFilter;
import net.sf.jguard.jsf.authentication.filters.JSFStatefulAuthenticationFiltersProvider;
import net.sf.jguard.jsf.authentication.schemes.JSFAuthenticationSchemeHandlerProvider;
import net.sf.jguard.jsf.authorization.JSFAuthorizationBindings;
import net.sf.jguard.jsf.authorization.JSFPolicyDecisionPoint;
import net.sf.jguard.jsf.authorization.filters.JSFAuthorizationFiltersProvider;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
public class JSFModule extends AbstractModule {
    @Override
    protected void configure() {

        //filterChain part
        bind(new TypeLiteral<Request<FacesContext>>() {
        }).to(FacesContextAdapter.class);
        bind(new TypeLiteral<Response<FacesContext>>() {
        }).to(FacesContextAdapter.class);
        bind(new TypeLiteral<PolicyEnforcementPoint<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFPolicyEnforcementPoint.class);

        bind(HttpServletRequestWrapper.class).to(JGuardServletRequestWrapper.class);

        //authentication part


        bind(new TypeLiteral<Collection<AuthenticationSchemeHandler<FacesContextAdapter, FacesContextAdapter>>>() {

        }).toProvider(JSFAuthenticationSchemeHandlerProvider.class);

        bind(new TypeLiteral<AuthenticationServicePoint<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFAuthenticationServicePoint.class);
        bind(new TypeLiteral<AsynchronousJGuardCallbackHandler<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFCallbackHandler.class);


        bind(new TypeLiteral<AuthenticationChallengeFilter<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFAuthenticationChallengeFilter.class);


        //Stateful part
        bind(new TypeLiteral<List<AuthenticationFilter<FacesContextAdapter, FacesContextAdapter>>>() {
        }).annotatedWith(Stateful.class).toProvider(JSFStatefulAuthenticationFiltersProvider.class);


        //restful part
        bind(new TypeLiteral<List<AuthenticationFilter<FacesContextAdapter, FacesContextAdapter>>>() {
        }).toProvider(JSFAuthenticationFiltersProvider.class);


        //guest part
        bind(new TypeLiteral<GuestPolicyEnforcementPointFilter<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFGuestPolicyEnforcementPointFilter.class);

        //guest part
        /*bind(new TypeLiteral<Collection<AuthenticationSchemeHandler<HttpServletRequest, HttpServletResponse>>>() {
        }).annotatedWith(Guest.class).toProvider(HttpServletGuestAuthenticationSchemeHandlersProvider.class);*/


        //authorization part

        bind(new TypeLiteral<AuthorizationBindings<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFAuthorizationBindings.class);

        bind(new TypeLiteral<PolicyDecisionPoint<FacesContextAdapter, FacesContextAdapter>>() {
        }).to(JSFPolicyDecisionPoint.class);

        bind(new TypeLiteral<List<AuthorizationFilter<FacesContextAdapter, FacesContextAdapter>>>() {
        }).toProvider(JSFAuthorizationFiltersProvider.class);

    }
}
