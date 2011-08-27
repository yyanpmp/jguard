package net.sf.jguard.jee.authentication.filters;

import javax.inject.Inject;
import net.sf.jguard.core.authentication.AuthenticationServicePoint;
import net.sf.jguard.core.authentication.Guest;
import net.sf.jguard.core.authentication.filters.GuestAuthenticationFilter;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Gay</a>
 */
public class HttpServletGuestAuthenticationFilter extends GuestAuthenticationFilter<HttpServletRequest, HttpServletResponse> {
    @Inject
    public HttpServletGuestAuthenticationFilter(@Guest Subject guestSubject, AuthenticationServicePoint<HttpServletRequest, HttpServletResponse> httpServletRequestHttpServletResponseAuthenticationServicePoint) {
        super(guestSubject, httpServletRequestHttpServletResponseAuthenticationServicePoint);
    }
}