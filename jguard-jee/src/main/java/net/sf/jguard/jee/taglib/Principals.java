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
package net.sf.jguard.jee.taglib;

import net.sf.jguard.core.authorization.permissions.RolePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.Iterator;
import java.util.Set;

public class Principals extends LoopTagSupport {

    private static final Logger logger = LoggerFactory.getLogger(Principals.class);
    private static final long serialVersionUID = 5869136846797237440L;
    private final static Class defaultClassName = RolePrincipal.class;
    private Class clazz = defaultClassName;
    private transient Iterator principalsIt;

    public final void setClassName(String className) {
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.info(" 'className' attribute does not map to an existing or reachable class ");
        }
    }

    protected boolean hasNext() throws JspTagException {
        return principalsIt.hasNext();
    }

    protected Object next() throws JspTagException {
        return principalsIt.next();
    }

    protected void prepare() throws JspTagException {
        Subject subject = TagUtils.getSubject(this.pageContext);
        if (subject == null) {
            subject = new Subject();
        }
        Set principals = subject.getPrincipals(clazz);
        if (principals == null) {
            throw new JspTagException("principal's set is null ");
        }
        principalsIt = principals.iterator();
    }
}
