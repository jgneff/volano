/*
 * Usertable.java - a table for looking up users.
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
import  COM.volano.chat.packet.EnterRoom;
import  java.util.*;

/**
 * This class maintains a list of users in a public or personal chat room.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class Usertable extends Vector {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private int limit;                    // The maximum number of users allowed
    private boolean matchcase;            // Match case on user names

    // Don't use the String.intern method since such strings are not garbage
    // collected until a fix for bugs 4035345 and 4072736 is available in
    // Java 1.2.

    /**
     * Creates a new list of users.
     *
     * @param limit  the maximum number of users allowed in the list.
     * @param matchcase  <i>true</i> to match the case when checking names;
     *                   otherwise <i>false</i>.
     */

    Usertable(int limit, boolean matchcase) {
        super(limit);
        this.limit = limit;
        this.matchcase = matchcase;
    }

    /**
     * Gets the index in the list of the user with the given name.
     *
     * @param name  the name of the user whose index is requested.
     * @return  the index of the user with the given name, or -1 if no such user
     *          is found in the list.
     */

    private synchronized int indexOf(String name) {
        for (int i = 0; i < elementCount; i++) {
            String string = ((User) elementData[i]).getName();
            if (matchcase ? name.equals(string) : name.equalsIgnoreCase(string)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the index in the list of the user with the given connection
     * identifier.
     *
     * @param id  the connection identifier of the user whose index is requested.
     * @return  the index of the user with the given connection identifier, or -1
     *          if no such user is found in the list.
     */

    private synchronized int indexOf(int id) {
        for (int i = 0; i < elementCount; i++) {
            if (id == ((User) elementData[i]).getId()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets a snapshot of all users in the list.
     *
     * @return  an array of all users in the list.
     */

    synchronized User[] snapshot() {
        User[] list = new User[elementCount];
        System.arraycopy(elementData, 0, list, 0, elementCount);
        return list;
    }

    /**
     * Checks whether a user with the given name exists in the list.
     *
     * @param name  the name to check.
     * @return  <code>true</code> if a user with the given name exists in the
     *          list; otherwise <code>false</code>.
     */

    boolean contains(String name) {
        return indexOf(name) >= 0;
    }

    /**
     * Checks whether a user with the given connection identifier exists in the
     * list.
     *
     * @param id  the connection identifier to check.
     * @return  <code>true</code> if a user with the given connection identifier
     *          exists in the list; otherwise <code>false</code>.
     */

    boolean contains(int id) {
        return indexOf(id) >= 0;
    }

    /**
     * Gets the user with the specified name.
     *
     * @param name  the name of the user to get.
     * @return  the user with the specified name, or <code>null</code> if no such
     *          user is found.
     */

    synchronized User getUser(String name) {
        int index = indexOf(name);
        if (index >= 0) {
            return (User) elementAt(index);
        }
        return null;
    }

    /**
     * Gets the user with the specified connection identifier.
     *
     * @param id  the connection identifier of the user to get.
     * @return  the user with the specified connection identifier, or
     *          <code>null</code> if no such user is found.
     */

    synchronized User getUser(int id) {
        int index = indexOf(id);
        if (index >= 0) {
            return (User) elementAt(index);
        }
        return null;
    }

    /**
     * Gets the name of the user with the specified connection identifier.
     *
     * @param id  the connection identifier of the user to get.
     * @return  the name of the user with the specified connection identifier, or
     *          <code>null</code> if no such user is found.
     */

    // 2.1.10
    String getName(int id) {
        User user = getUser(id);
        if (user != null) {
            return user.getName();
        }
        return null;
    }

    /**
     * Adds a user to the list without allowing any two users with the same name
     * and optionally checking whether the limit on the maximum number of users
     * is reached.
     *
     * @param user           the user to add.
     * @param unconditional  <code>true</code> to add this user regardless of the
     *                       current number of users; <code>false</code> to add
     *                       this user only if it would not exceed the limit.
     * @return  the enter room result indicating whether the user was added, or
     *          instead the list was full or the name was already taken.
     * @see EnterRoom
     */

    synchronized int putUser(User user, boolean unconditional) {
        int result = EnterRoom.OKAY;
        if (elementCount >= limit && ! unconditional) {
            result = EnterRoom.ROOM_FULL;
        } else if (contains(user.getName())) {
            result = EnterRoom.NAME_TAKEN;
        } else {
            addElement(user);
        }
        return result;
    }

    /**
     * Removes a user from the list by name.
     *
     * @param name  the name of the user to remove.
     * @return  the user removed from the list, or <code>null</code> if no such
     *          user is found.
     */

    synchronized User removeUser(String name) {
        User user  = null;
        int  index = indexOf(name);
        if (index >= 0) {
            user = (User) elementAt(index);
            removeElementAt(index);
        }
        return user;
    }

    /**
     * Removes a user from the list by connection identifier.
     *
     * @param id  the connection identifier of the user to remove.
     * @return  the user removed from the list, or <code>null</code> if no such
     *          user is found.
     */

    synchronized User removeUser(int id) {
        User user  = null;
        int  index = indexOf(id);
        if (index >= 0) {
            user = (User) elementAt(index);
            removeElementAt(index);
        }
        return user;
    }
}
