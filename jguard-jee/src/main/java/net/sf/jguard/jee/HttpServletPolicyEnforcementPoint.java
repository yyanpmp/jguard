package net.sf.jguard.jee;

import com.google.inject.servlet.RequestScoped;
import net.sf.jguard.core.authentication.Stateful;
import net.sf.jguard.core.authentication.filters.AuthenticationFilter;
import net.sf.jguard.core.authorization.filters.AuthorizationFilter;
import net.sf.jguard.core.enforcement.PolicyEnforcementPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
@RequestScoped
public class HttpServletPolicyEnforcementPoint extends PolicyEnforcementPoint<HttpServletRequestAdapter, HttpServletResponseAdapter> {


    private static final Logger logger = LoggerFactory.getLogger(HttpServletPolicyEnforcementPoint.class.getName());

    @Inject
    public HttpServletPolicyEnforcementPoint(@Stateful List<AuthenticationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>> authenticationFilters,
                                             List<AuthorizationFilter<HttpServletRequestAdapter, HttpServletResponseAdapter>> authorizationFilters,
                                             boolean propagateThrowable) {
        super(authenticationFilters, authorizationFilters, propagateThrowable);
    }

    @Override
    protected void sendThrowable(HttpServletResponseAdapter response, Throwable throwable) {
        logger.error(throwable.getMessage(), throwable);
        HttpServletResponse res = response.get();
        try {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
