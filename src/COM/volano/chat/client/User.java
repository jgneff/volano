/*
 * User.java - a chat room user.
 * Copyright (C) 1996-2002 John Neffenger
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
import  java.awt.*;

/**
 * This class defines a chat room user by name and address.
 *
 * @author  John Neffenger
 * @version 1 May 2002
 */

class User {
    private String name;
    private String address;

    /**
     * Creates a new chat room user.
     *
     * @param name the user's name.
     * @param address the user's IP address or host name.
     */

    User(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Returns the user's name.
     *
     * @return the user's name.
     */

    String getName() {
        return name;
    }

    /**
     * Returns the user's address.
     *
     * @return the user's address.
     */

    String getAddress() {
        return address;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return the user's address and user's name as one string.
     */

    public String toString() {
        return address + " " + name;
    }
}
