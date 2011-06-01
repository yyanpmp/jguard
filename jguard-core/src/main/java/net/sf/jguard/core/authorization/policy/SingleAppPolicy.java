/*
jGuard is a security framework based on top of jaas (java authentication and authorization security).
it is written for web applications, to resolve simply, access control problems.
version $Name:  $
http://sourceforge.net/projects/jguard

Copyright (C) 2004  Charles GAY

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
http://sourceforge.net/projects/jguard

*/
package net.sf.jguard.core.authorization.policy;

import javax.inject.Inject;
import net.sf.ehcache.CacheException;
import net.sf.jguard.core.PolicyEnforcementPointOptions;
import net.sf.jguard.core.authorization.manager.AuthorizationManager;
import net.sf.jguard.core.authorization.manager.AuthorizationManagerOptions;
import net.sf.jguard.core.authorization.manager.JGuardAuthorizationManagerMarkups;
import net.sf.jguard.core.authorization.manager.PermissionProvider;
import net.sf.jguard.core.authorization.permissions.PermissionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.*;
import java.util.Map;


/**
 * Single application JGuard Policy implementation. Use SingleAppPolicy if the application
 * is the only one running in the VM. It mainly concerns standalone applications.<br>
 * SingleAppPolicy loads a configuration file (JGuardAuthentication.xml is the default one).<br>
 * A custom location of the configuration file can be passed :
 * <ul>
 * <li>in authorizationManager option "fileLocation" in policy configuration file</li>
 * <li>trough vm arg : <code>-Dnet.sf.jguard.policy.configuration.file="path_to_policy_configuration_file"</code></li>
 * </ul>
 * It also gets the application name. There are several ways to pass it. The following list shows them ordered from
 * the first way handled by the policy to the last one.
 * <ul>
 * <li>in authorizationManager option "applicationName" in policy configuration file</li>
 * <li>trough vm arg : <code>net.sf.jguard.application.name</code> VM arg</li>
 * <li>trough vm arg : <code>com.sun.management.jmxremote.login.config</code>
 * if you have already defined this property because you use JMX.
 * Do not set application name through this property if you are not using JMX !</li>
 * </ul>
 * If no applicationName is passed to the application, default application name "other" is used.
 *
 * @author <a href="mailto:vberetti@users.sourceforge.net">Vincent Beretti</a>
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Gay</a>
 * @see net.sf.jguard.core.authorization.policy.JGuardPolicy
 * @see net.sf.jguard.core.authorization.policy.MultipleAppPolicy
 */
public final class SingleAppPolicy extends JGuardPolicy {

    private static Logger logger = LoggerFactory.getLogger(SingleAppPolicy.class.getName());
    private static final String DEFAULT_POLICY_CONFIGURATION_FILE = "JGuardAuthorization.xml";

    private PermissionProvider permissionProvider;
    private static String POLICY_CONFIGURATION_FILE = "net.sf.jguard.policy.configuration.file";
    private static String APPLICATION_HOME_PATH = "net.sf.jguard.application.home.path";
    public final static String APPLICATION_NAME_SYSTEM_PROPERTY = "net.sf.jguard.application.name";


    /**
     * @param authorizationManager
     * @param authManagerOptions
     * @throws net.sf.jguard.core.authorization.manager.AuthorizationManagerException
     *
     */
    @Inject
    public SingleAppPolicy(AuthorizationManager authorizationManager,
                           @AuthorizationManagerOptions Map<String, String> authManagerOptions,
                           Permissions grantedPermissions) {
        super(grantedPermissions);


        final Map<String, String> authorizationmanagerOptions = authManagerOptions;
        permissionProvider = authorizationManager;

        // call run() method under extended privileges
        AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                logger.info("#######   loading SingleAppPolicy  " + JGuardPolicy.version + " ###########");
                String configurationLocation = System.getProperty(POLICY_CONFIGURATION_FILE);

                if (configurationLocation == null) {
                    logger.info("No configuration file in " + POLICY_CONFIGURATION_FILE + ", using default " + DEFAULT_POLICY_CONFIGURATION_FILE + " location");
                    configurationLocation = DEFAULT_POLICY_CONFIGURATION_FILE;
                }

                String appHomePath = System.getProperty(APPLICATION_HOME_PATH);
                if (appHomePath != null && !appHomePath.endsWith("/")) {
                    appHomePath += "/";
                }

                if (appHomePath == null) {
                    appHomePath = "";
                }


                if (authorizationmanagerOptions.get(PolicyEnforcementPointOptions.APPLICATION_NAME.getLabel()) == null) {
                    String appNameProp = System.getProperty(APPLICATION_NAME_SYSTEM_PROPERTY);

                    if (appNameProp != null) {
                        authorizationmanagerOptions.put(PolicyEnforcementPointOptions.APPLICATION_NAME.getLabel(), appNameProp);
                    } else {

                        // use default application name.
                        authorizationmanagerOptions.put(PolicyEnforcementPointOptions.APPLICATION_NAME.getLabel(), PolicyEnforcementPointOptions.DEFAULT_APPLICATION_NAME.getLabel());
                    }
                }

                if (Boolean.FALSE.toString().equals(authorizationmanagerOptions.get(JGuardAuthorizationManagerMarkups.AUTHORIZATION_PERMISSION_RESOLUTION_CACHING.getLabel()))) {
                    PermissionUtils.setCachesEnabled(false);
                } else {
                    // by default, permission resolution caching is activated
                    try {
                        PermissionUtils.createCaches();
                        PermissionUtils.setCachesEnabled(true);
                    } catch (CacheException e) {
                        logger.warn("Failed to activate permission resolution caching : " + e.getMessage());
                        PermissionUtils.setCachesEnabled(false);
                    }
                }


                return permissionProvider;
            }
        });

        loadDefaultPolicy();
    }


    @Override
    protected PermissionProvider getContextPermissionProvider(Object cl) {
        return permissionProvider;
    }

    public void refresh() {
        if (permissionProvider != null) {
            // Refresh the permission configuration
            permissionProvider.refresh();
        }
    }

    @Override
    public boolean implies(ProtectionDomain domain, Permission permission) {
        return super.implies(domain, permission);
    }


}
