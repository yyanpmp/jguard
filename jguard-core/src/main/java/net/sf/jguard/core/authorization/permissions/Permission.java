/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
package net.sf.jguard.core.authorization.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.BasicPermission;

/**
 * POJO counterpart of {@link java.security.Permission}.
 *
 * @author <a href="mailto:diabolo512@users.sourceforge.net">Charles Lescot</a>
 */
@Entity
@Table(name = "jg_permission")
public class Permission<T extends java.security.Permission> {


    private static final Logger logger = LoggerFactory.getLogger(Permission.class.getName());
    public static final int BASIC_PERMISSION_PARAMETERS_NUMBER_CONS = 2;
    public static final int FIRST_CONSTRUCTOR_PARAMETER_INDEX = 0;
    public static final int SECOND_CONSTRUCTOR_PARAMETER_INDEX = 1;


    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String actions;

    private String clazz;

    @ManyToOne
    @JoinColumn(nullable = true)
    private RolePrincipal rolePrincipal;
    private static final int BASIC_PERMISSION_PARAMETERS_CONSTRUCTOR_NUMBER = 2;

    public Permission() {

    }

    public Permission(Class<T> clazz, String name, String actions) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null to instantiate Permission");
        }
        if (name == null) {
            throw new IllegalArgumentException("name  must not be null to instantiate Permission");
        }
        this.clazz = clazz.getName();
        this.name = name;
        this.actions = actions;
        if (actions == null) {
            actions = "";
        }
    }

    /**
     * instantiate a java.security.Permission subclass.
     *
     * @param clazz   class name
     * @param name    permission name
     * @param actions actions name split by comma ','
     * @return a java.security.Permission subclass, or a java.security.BasicPermission subclass
     *         (which inherit java.security.Permission)
     * @throws ClassNotFoundException
     */
    public static java.security.Permission getPermission(Class clazz, String name, String actions) throws ClassNotFoundException {
        if (!java.security.Permission.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("clazz[" + clazz.getName() + "] is not a subclass of java.security.Permission");
        }
        String className = clazz.getName();

        Class[] permArgsBasicPermClass = {String.class, String.class};
        Class[] permArgsPermClass = {String.class};
        Object[] objBasicArray = {name, actions};
        java.security.Permission newPerm = null;
        try {
            //check if className inherit from the Abstract BasicPermission class which
            // has got a two string argument constructor to speed up the lookup
            if (BasicPermission.class.isAssignableFrom(clazz)) {
                newPerm = (java.security.Permission) clazz.getConstructor(permArgsBasicPermClass).newInstance(objBasicArray);
                return newPerm;
            }

            Object[] objArray = {name};

            Constructor[] constructors = clazz.getConstructors();
            boolean constructorWithActions = false;
            for (Constructor tempConstructor : constructors) {
                Class[] classes = tempConstructor.getParameterTypes();
                if (classes.length == BASIC_PERMISSION_PARAMETERS_CONSTRUCTOR_NUMBER
                        && classes[FIRST_CONSTRUCTOR_PARAMETER_INDEX].equals(String.class)
                        && classes[SECOND_CONSTRUCTOR_PARAMETER_INDEX].equals(String.class) && !"".equals(objBasicArray[1])) {
                    constructorWithActions = true;
                    break;
                }
            }

            // a class which does not inherit from BasicPermission but has got a two string arguments constructor
            if (constructorWithActions) {
                newPerm = (java.security.Permission) clazz.getConstructor(permArgsBasicPermClass).newInstance(objBasicArray);
            } else {
                //Permission subclass which has got a constructor with name argument
                newPerm = (java.security.Permission) clazz.getConstructor(permArgsPermClass).newInstance(objArray);
            }
        } catch (IllegalArgumentException e) {
            logger.error(" illegal argument ", e);
        } catch (SecurityException e) {
            logger.error("className=" + className);
            logger.error("name=" + name);
            logger.error("actions=" + actions);
            logger.error(" you don't have right to instantiate a permission ", e);
        } catch (InstantiationException e) {
            logger.error("className=" + className);
            logger.error("name=" + name);
            logger.error("actions=" + actions);
            logger.error(" you cannot instantiate a permission ", e);
        } catch (IllegalAccessException e) {
            logger.error("className=" + className);
            logger.error("name=" + name);
            logger.error("actions=" + actions);
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error("className=" + className);
            logger.error("name=" + name);
            logger.error("actions=" + actions);
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error("method not found =", e);
        }
        if (newPerm == null) {
            throw new IllegalArgumentException("permission with class='" + clazz.getName() + "' name='" + name + "' actions='" + actions + "' cannot be instantiated ");
        }
        return newPerm;
    }

    public T toJavaPermission() {
        try {
            Class cl = Thread.currentThread().getContextClassLoader().loadClass(clazz);
            return (T) getPermission(cl, this.getName(), this.getActions());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public int hashCode() {
        return clazz.hashCode() + name.hashCode() + actions.hashCode();
    }

    public boolean equals(Object object) {
        if (!Permission.class.isAssignableFrom(object.getClass())) {
            return false;
        } else {
            Permission permission = (Permission) object;
            return this.getClazz().equals(permission.getClazz())
                    && this.getName().equals(permission.getName())
                    && this.getActions().equals(permission.getActions());
        }
    }

    public RolePrincipal getRolePrincipal() {
        return rolePrincipal;
    }

    public void setRolePrincipal(RolePrincipal rolePrincipal) {
        this.rolePrincipal = rolePrincipal;
    }


    @PreRemove
    public void preRemove() {
        if (rolePrincipal != null) {
            rolePrincipal.getPermissions().remove(this);
        }

    }
}
