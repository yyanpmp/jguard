/*
jGuard is a security framework based on top of jaas (java authentication and authorization security).
it is written for web applications, to resolve simply, access control problems.
 
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
package net.sf.jguard.jee.authentication.http;

import javax.servlet.ServletContext;

public class HttpSessionSimulator extends
		com.kizna.servletunit.HttpSessionSimulator {

	private ServletContext servletContext = null;
	private boolean invalidated = false;
	
        public HttpSessionSimulator(){
            servletContext = new ServletContextSimulator();
        }
        
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public boolean isInvalidated() {
		return invalidated;
	}

	public void setInvalidated(boolean illegalStateException) {
		this.invalidated = illegalStateException;
	}
	
	public Object getAttribute(String attributeName){
		if(invalidated){
			throw new IllegalStateException("session is invalidated ");
		}
		return super.getAttribute(attributeName);
	}
	

}
