/*
 * User.java - a user in a chat group.
 * Copyright (C) 1996-1998 John Neffenger
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

package COM.volano.chat.server;
import  COM.volano.chat.Build;
import  COM.volano.net.*;

/**
 * This class represents a user in a public or personal chat room.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class User {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private Connection connection;
    private String     name;
    private String     profile;

    private int        id;
    private boolean    member;
    private boolean    linked;
    private long       startTime = System.currentTimeMillis();

    /**
     * Creates a new user.
     *
     * @param connection  the connection to the client.
     * @param name        the name of the user.
     * @param profile     the profile of the user.
     */

    User(Connection connection, String name, String profile) {
        this.connection = connection;
        this.name       = name;
        this.profile    = profile;
        this.id         = connection.getId();
        this.member     = name.equalsIgnoreCase((String) connection.getAttribute(Attr.MEMBER_NAME));
        this.linked     = Boolean.valueOf((String) connection.getAttribute(Attr.MEMBER_LINK)).booleanValue();
    }

    /**
     * Gets the user's connection.
     *
     * @return  the connection to the client.
     */

    Connection getConnection() {
        return connection;
    }

    /**
     * Gets the user's name.
     *
     * @return  the name of the user.
     */

    String getName() {
        return name;
    }

    /**
     * Gets the user's profile.
     *
     * @return  the profile of the user.
     */

    String getProfile() {
        return profile;
    }

    /**
     * Gets the user's connection identifier.
     *
     * @return  the user's connection identifier.
     */

    int getId() {
        return id;
    }

    /**
     * Gets the user's member information as a boolean.
     *
     * @return  <code>true</code> if the user is a member; otherwise
     *          <code>false</code>.
     */

    boolean getMember() {
        return member;
    }

    /**
     * Gets the user's member information.
     *
     * @return  the string "true" if the user is a member; otherwise the string
     *          "false".
     */

    String isMember() {
        return (new Boolean(member)).toString();
    }

    /**
     * Gets whether or not to display the member link.
     *
     * @return  the string "true" if the link should be shown; otherwise the
     *          string "false".
     */

    String showLink() {
        return (new Boolean(linked)).toString();
    }

    /**
     * Gets the starting time that this representation of the end user was
     * created.
     *
     * @return  the starting time that this user object was created.
     */

    long getStartTime() {
        return startTime;
    }

    /**
     * Gets a string representation of this user object for debugging purposes.
     *
     * @return  a string representation of this user.
     */

    public String toString() {
        return name;
    }
}
