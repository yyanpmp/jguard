/*
jGuard is a security framework based on top of jaas (java authentication and authorization security).
it is written for web applications, to resolve simply, access control problems.
version $Name:  $
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
package net.sf.jguard.core.jmx;

import javax.security.auth.callback.*;
import java.io.IOException;

/**
 * @author <a href="mailto:diabolo512@users.sourceforge.net ">Charles Lescot</a>
 * @author <a href="mailto:vberetti@users.sourceforge.net">Vincent Beretti</a>
 */
class JMXCallbackHandler implements CallbackHandler {

    private String[] credentials = null;


    public JMXCallbackHandler(String[] credentials) {
        if(credentials==null||credentials.length==0){
            throw new IllegalArgumentException("credentials provided are null or empty");
        }
        int length = credentials.length;
        String[] copies = new String[length];
        System.arraycopy(credentials, 0, copies, 0, length);
        this.credentials = copies;
    }

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (Callback c : callbacks) {
            if (c instanceof NameCallback) {
                ((NameCallback) c).setName(credentials[0]);
            } else if (c instanceof PasswordCallback) {
                ((PasswordCallback) c).setPassword((credentials[1]).toCharArray());
            }
        }
    }
}

