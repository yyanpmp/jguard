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

package net.sf.jguard.core.authentication;

import net.sf.jguard.core.authentication.callbackhandler.JGuardCallbackHandler;
import net.sf.jguard.core.authentication.exception.AuthenticationContinueException;
import net.sf.jguard.core.authentication.exception.AuthenticationException;
import net.sf.jguard.core.authentication.loginmodules.AuthenticationChallengeException;
import net.sf.jguard.core.lifecycle.Request;
import net.sf.jguard.core.lifecycle.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import java.security.AccessControlContext;
import java.security.AccessController;


/**
 * Authenticate a user in an application.
 *
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 * @since 2 .0
 */
public abstract class AbstractAuthenticationServicePoint<Req extends Request, Res extends Response> implements AuthenticationServicePoint<Req, Res> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAuthenticationServicePoint.class.getName());

    private static final String AUTHENTICATION_SUCCEEDED = "authenticationSucceededDuringThisRequest";
    protected LoginContextWrapper loginContextWrapper;

    public AbstractAuthenticationServicePoint(LoginContextWrapper loginContextWrapper) {
        this.loginContextWrapper = loginContextWrapper;
    }


    public AuthenticationResult authenticate(
            JGuardCallbackHandler<Req, Res> callbackHandler) throws AuthenticationException {
        AuthenticationStatus authenticationStatus = null;
        Subject subject = null;
        try {

            //we use the wrapper object bound to user with the dedicated object(callbackHandler)
            //to communicate with him to authenticate

            subject = loginContextWrapper.login(callbackHandler);

            authenticationSucceed(loginContextWrapper, callbackHandler.getRequest());
            //propagate the authentication success
            callbackHandler.authenticationSucceed(subject);
            authenticationStatus = AuthenticationStatus.SUCCESS;

        } catch (AuthenticationContinueException ace) {
            //the current AuthenticationScheme needs multiple roundtrips
            logger.debug("authentication is not yet complete. a new exchange between client and server is required " + ace.getMessage());
            authenticationStatus = AuthenticationStatus.CONTINUE;
        } catch (AuthenticationChallengeException ace) {
            //the callbackHandle handle this case. we only need here to go back the call to the user
            logger.debug("authentication challenge built. a new exchange between client and server is required " + ace.getMessage());
            authenticationStatus = AuthenticationStatus.FAILURE;

        } catch (LoginException e) {

            logger.info("authentication failed " + e.getMessage(), e);
            callbackHandler.authenticationFailed();
            authenticationStatus = AuthenticationStatus.FAILURE;
        } finally {
            return new AuthenticationResult(authenticationStatus, loginContextWrapper);
        }
    }

    /**
     * method called when authentication succeed. it can be overriden by subclasses,
     * for, as an example, do some manipulation on Stateful sessions.
     */
    protected void authenticationSucceed(LoginContextWrapper loginContextWrapper, Req Req) {
    }

    /**
     * grab the authenticated PersistedSubject in the execution stack.
     *
     * @return authenticated PersistedSubject or null if user is not authenticated
     * @throws SecurityException the caller does not have the permission to call the subject
     */
    public Subject getCurrentSubject(Req req) {
        AccessControlContext acc = AccessController.getContext();
        if (acc == null) {
            //acc== null signifies System code,
            //so, no Subject is bound to it
            return null;
        }
        return Subject.getSubject(acc);
    }


    public boolean authenticationSucceededDuringThisRequest(Req request, Res response) {
        String authenticationSucceeded = (String) request.getRequestAttribute(AUTHENTICATION_SUCCEEDED);
        return null != authenticationSucceeded && Boolean.parseBoolean(authenticationSucceeded);
    }

}
