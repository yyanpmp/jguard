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
package net.sf.jguard.ext.principals;

import net.sf.jguard.core.authorization.permissions.RolePrincipal;
import net.sf.jguard.core.principals.Organization;
import org.hibernate.Session;

import javax.inject.Inject;
import javax.inject.Provider;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
public class HibernatePrincipalUtils {

    private Provider<Session> session;

    @Inject
    public HibernatePrincipalUtils(Provider<Session> session) {
        this.session = session;
    }

    public PersistedPrincipal getPersistedPrincipal(Principal ppal) {

        if (ppal instanceof RolePrincipal) {
            RolePrincipal rp = (RolePrincipal) ppal;
            Long id = rp.getId();
            PersistedPrincipal principal;
            if (id != null) {
                principal = (PersistedPrincipal) session.get().get(PersistedPrincipal.class, id);
                return principal;
            } else {
                principal = new PersistedPrincipal();
                principal.setClassName(ppal.getClass().getName());
                principal.setName(rp.getLocalName());
                principal.setApplicationName(rp.getApplicationName());
            }
            return principal;
        } else {
            return null;
        }

    }

    public static Set<Principal> getjavaSecurityPrincipals(Set<PersistedPrincipal> principals) {
        Set<Principal> ppals = new HashSet<Principal>();
        for (PersistedPrincipal principal : principals) {
            String applicationName = principal.getApplicationName();
            String className = principal.getClassName();
            String name = principal.getName();

            if (RolePrincipal.class.getName().equals(className)) {
                RolePrincipal role = new RolePrincipal(name, applicationName);
                role.setId(principal.getId());
                ppals.add(role);
            }
        }
        return ppals;
    }

    public Set<PersistedPrincipal> getPersistedPrincipals(Set<? extends Principal> ppals) {
        Set<PersistedPrincipal> s = new HashSet<PersistedPrincipal>();
        for (Principal ppal : ppals) {
            PersistedPrincipal principal = getPersistedPrincipal(ppal);
            if (principal != null) {
                s.add(principal);
            }
        }
        return s;
    }


    public static Set<Organization> getOrganizations(Set<PersistedOrganization> persistedOrganizations) {
        Set<Organization> orgas = new HashSet<Organization>();
        for (PersistedOrganization persistedOrganization : persistedOrganizations) {
            Organization orga = persistedOrganization.toOrganization();
            orgas.add(orga);
        }
        return orgas;
    }
}
