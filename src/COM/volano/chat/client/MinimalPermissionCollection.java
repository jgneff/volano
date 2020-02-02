/*
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

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author John Neffenger
 */
class MinimalPermissionCollection extends PermissionCollection {

    private static final Vector vector = new Vector();

    public void add(Permission permission) {
        vector.add(permission);
    }

    public boolean implies(Permission permission) {
        Enumeration e = vector.elements();
        while (e.hasMoreElements()) {
            Permission p = (Permission) e.nextElement();
            if (p.implies(permission)) {
                return true;
            }
        }
        return false;
    }

    public Enumeration elements() {
        return vector.elements();
    }
}
