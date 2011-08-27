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

package net.sf.jguard.core.authentication;

import net.sf.jguard.core.authentication.callbackhandler.JGuardCallbackHandler;
import net.sf.jguard.core.authentication.credentials.JGuardCredential;
import net.sf.jguard.core.authentication.loginmodules.UserLoginModule;
import net.sf.jguard.core.authentication.schemes.AuthenticationSchemeHandler;
import net.sf.jguard.core.authentication.schemes.StatefulAuthenticationSchemeHandler;
import net.sf.jguard.core.technology.Scopes;
import net.sf.jguard.core.technology.StatefulScopes;
import net.sf.jguard.core.util.SubjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;
import java.security.Permission;
import java.util.*;

public abstract class StatefulAuthenticationServicePoint<Req, Res> extends AbstractAuthenticationServicePoint<Req, Res> {

    protected StatefulScopes scopes;
    private static final Logger logger = LoggerFactory.getLogger(StatefulAuthenticationServicePoint.class.getName());
    public static final String LOGIN_CONTEXT_WRAPPER = "loginContextWrapper";

    public StatefulAuthenticationServicePoint(Configuration configuration,
                                              Configuration guestConfiguration,
                                              Collection<AuthenticationSchemeHandler<Req, Res>> authenticationSchemeHandlers,
                                              String applicationName,
                                              StatefulScopes scopes,
                                              JGuardCallbackHandler guestCallbackHandler) {
        super(configuration, guestConfiguration, authenticationSchemeHandlers, applicationName, scopes, guestCallbackHandler);
        this.scopes = scopes;
    }


    /**
     * for security reasons(man-in-the-middle attack for sessionID cookie),
     * we remove the old session if authentication is successful
     * and create a new session.
     *
     * @param loginContextWrapper
     */
    protected void authenticationSucceed(LoginContextWrapper loginContextWrapper) {


        //we remove this variable before invalidate the session
        //to prevent the session listener to erase the subject
        scopes.removeSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER);

        //we move variables bound , from the old session to the new one
        Map<String, Object> sessionEntries = new HashMap<String, Object>();
        Iterator<String> it = scopes.getSessionAttributeNames();
        while (it.hasNext()) {
            String key = it.next();
            Object value = scopes.getSessionAttribute(key);
            sessionEntries.put(key, value);
        }

        scopes.invalidateSession();

        scopes.setSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER, loginContextWrapper);
        for (Map.Entry<String, Object> stringObjectEntry : sessionEntries.entrySet()) {
            scopes.setSessionAttribute(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }

    }


    /**
     * return the <i>current</i> {@link Subject}:
     * this method is looking for from the local scope to the global scope.
     * - firstly, looking for the AccessControlContext bound to the Thread.
     * - if not present, and if the scopes implements StatefulScopes,
     * looking for the Subject present in the session.
     * - if not present or not stateful, looking for the Guest Subject present in the application scope.
     *
     * @return current Subject
     */
    public Subject getCurrentSubject() {
        Subject subject = getSubjectInAccessControlContext();
        LoginContextWrapper loginContextWrapperImpl = (LoginContextWrapperImpl) ((StatefulScopes) scopes).getSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER);
        if (loginContextWrapperImpl != null) {
            subject = loginContextWrapperImpl.getSubject();
        }
        if (subject == null) {
            subject = (Subject) scopes.getApplicationAttribute(SubjectUtils.GUEST_SUBJECT);
        }
        return subject;

    }


    /**
     * we remove the LoginContextWrapper(wrapper around Subject) and invalidate the session.
     *
     * @see net.sf.jguard.core.technology.StatefulScopes
     * @see net.sf.jguard.core.technology.Scopes
     */
    public void logout() {
        logger.debug(" logout phase ");
        StatefulScopes statefulAuthenticationBindings = scopes;
        //remove Subject from session
        LoginContextWrapper loginContext = (LoginContextWrapper) statefulAuthenticationBindings.getSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER);
        if (loginContext != null) {
            loginContext.logout();
            logger.debug(" user logout ");
        }

        statefulAuthenticationBindings.removeSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER);

        logger.debug("doFilter() -  user logout ");

        //we invalidate the session to unbound all objects, including subject
        try {
            statefulAuthenticationBindings.invalidateSession();
        } catch (java.lang.IllegalStateException ise) {
            logger.error(" session is already invalidated ", ise);
        }
    }


    /**
     * checks user has been authenticated with a <b>StatefulScopes</b>AuthenticationSchemeHandler (only those
     * has got the logoff feature which is meanless in other cases), and check that the permission requested
     * is implied by the logoff permission of the StatefulAuthenticationHandler.
     *
     * @param permissionToCheck permission enforced
     * @return <i>true</i> if user tries to logoff, <i>false</i> otherwise
     */
    public boolean userTriesToLogout(Permission permissionToCheck) {
        Subject currentSubject = getCurrentSubject();
        boolean userLogoff = false;

        //we check that authenticationSchemeHandler is stateful and if logoff is called
        AuthenticationSchemeHandler authSchemeHandler = getAuthenticationSchemeHandler(currentSubject, getAuthenticationSchemeHandlers());
        if (authSchemeHandler == null) {
            //when user is guest which use impersonationAuthenticationBindings not present here
            return false;
        }
        if (StatefulAuthenticationSchemeHandler.class.isAssignableFrom(authSchemeHandler.getClass())) {
            StatefulAuthenticationSchemeHandler statefulAuthenticationSchemeHandler = (StatefulAuthenticationSchemeHandler) authSchemeHandler;
            if (statefulAuthenticationSchemeHandler.getLogoffPermission().implies(permissionToCheck)) {
                userLogoff = true;
            }
        } else {
            logger.debug(" no authenticationSchemeHandler is a StatefulAuthenticationSchemeHandler. we cannot logoff a stateless AuthenticationSchemeHandler");
        }

        return userLogoff;
    }


    protected LoginContextWrapper getLoginContextWrapper(Scopes scopes) {
        LoginContextWrapper loginContextWrapperImpl = (LoginContextWrapper) ((StatefulScopes) scopes).getSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER);
        if (loginContextWrapperImpl == null) {
            loginContextWrapperImpl = super.getLoginContextWrapper(scopes);
        }
        ((StatefulScopes) scopes).setSessionAttribute(StatefulAuthenticationServicePoint.LOGIN_CONTEXT_WRAPPER, loginContextWrapperImpl);
        return loginContextWrapperImpl;
    }


    /**
     * grab into the {@link javax.security.auth.Subject} the name of the AuthenticationSchemeHandler,
     * to select among ones registered into the list passed as an argument and return it.
     *
     * @param subject
     * @param authSchemeHandlers
     * @return
     */
    private AuthenticationSchemeHandler getAuthenticationSchemeHandler(Subject subject, Collection<AuthenticationSchemeHandler<Req, Res>> authSchemeHandlers) {
        String authSchemeHandlerName = getAuthenticationSchemeHandlerName(subject);
        if (authSchemeHandlerName == null) {
            throw new IllegalArgumentException(" Subject does not contains a JGuardCredential with a key='authSchemeHandlerName' and a value not null ");
        }
        AuthenticationSchemeHandler<Req, Res> authSchemeHandler = null;
        for (AuthenticationSchemeHandler<Req, Res> authHandler : authSchemeHandlers) {
            if (authSchemeHandlerName.equals(authHandler.getName())) {
                authSchemeHandler = authHandler;
                break;
            }
        }
        return authSchemeHandler;
    }

    /**
     * authSchemeHandler name is stored into the Subject credentials set.
     *
     * @param subject
     * @return
     */
    private static String getAuthenticationSchemeHandlerName(Subject subject) {
        Set<JGuardCredential> credentials = subject.getPublicCredentials(JGuardCredential.class);
        for (JGuardCredential cred : credentials) {
            if (UserLoginModule.AUTHENTICATION_SCHEME_HANDLER_NAME.equals(cred.getName())) {
                return (String) cred.getValue();
            }
        }
        return null;
    }

}