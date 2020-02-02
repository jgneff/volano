/*
 * MinimalPolicy.java - a minimal Java security policy for the applets
 * Copyright (C) 2014 John Neffenger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package COM.volano.chat.client;

import java.io.FilePermission;
import java.net.SocketPermission;
import java.net.URL;
//import java.net.URLPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;

/**
 *
 * @author John Neffenger
 */
public class MinimalPolicy extends Policy {

    private static final PermissionCollection permissions = new MinimalPermissionCollection();

    public MinimalPolicy(URL codebaseURL) {
        String host = codebaseURL.getHost();
        String codebase = codebaseURL.toString();

        boolean isFile = false;
        if (codebase.startsWith("file:")) {
            isFile = true;
            codebase = codebase.substring(5);
        }
        codebase += codebase.endsWith("/") ? "-" : "/-";
        host = host.length() == 0 ? "localhost" : host;

        permissions.add(new RuntimePermission("createSecurityManager"));
        permissions.add(new RuntimePermission("setSecurityManager"));
        permissions.add(new PropertyPermission("java.vendor", "read"));
        permissions.add(new PropertyPermission("java.vendor.url", "read"));
        permissions.add(new PropertyPermission("java.version", "read"));
        permissions.add(new PropertyPermission("java.class.version", "read"));
        permissions.add(new PropertyPermission("os.name", "read"));
        permissions.add(new PropertyPermission("os.version", "read"));
        permissions.add(new PropertyPermission("os.arch", "read"));
        permissions.add(new RuntimePermission("modifyThreadGroup"));
        permissions.add(new RuntimePermission("modifyThread"));
        permissions.add(new SocketPermission(host, "connect,resolve"));
        if (isFile) {
            permissions.add(new FilePermission(codebase, "read"));
        } else {
            //permissions.add(new URLPermission(codebase, "*:*"));
        }
        permissions.setReadOnly();
    }

    public PermissionCollection getPermissions(CodeSource codesource) {
        return permissions;
    }

    public boolean implies(ProtectionDomain domain, Permission permission) {
        return permissions.implies(permission);
    }

    public void refresh() {
    }
}
