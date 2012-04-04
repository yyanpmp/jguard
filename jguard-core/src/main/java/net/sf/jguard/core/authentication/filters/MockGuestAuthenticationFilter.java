package net.sf.jguard.core.authentication.filters;

import net.sf.jguard.core.authentication.AuthenticationServicePoint;
import net.sf.jguard.core.authentication.Guest;
import net.sf.jguard.core.lifecycle.MockRequest;
import net.sf.jguard.core.lifecycle.MockResponse;

import javax.inject.Inject;
import javax.security.auth.Subject;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
public class MockGuestAuthenticationFilter extends GuestAuthenticationFilter<MockRequest, MockResponse> {
    @Inject
    public MockGuestAuthenticationFilter(@Guest Subject subject,
                                         AuthenticationServicePoint<MockRequest, MockResponse> authenticationServicePoint) {
        super(subject, authenticationServicePoint);
    }
}
