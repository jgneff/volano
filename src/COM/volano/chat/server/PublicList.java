/*
 * PublicList.java - a class for maintaining a list of public rooms.
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
import  java.net.*;
import  java.util.*;

/**
 * This class maintains a list of public or personal rooms.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class PublicList extends Grouptable {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private Vector    nameList;   // List of room names
    private Hashtable pending;    // Maps room names to list of pending observers

    /**
     * Creates a new public room list.
     *
     * @param name      the name of this room list and its associated list
     *                  <i>sweeper</i> thread.
     * @param priority  the priority of the sweeper thread.
     * @param interval  the interval at which the sweeper thread checks the list
     *                  for empty rooms.
     */

    PublicList(String name, int priority, int interval) {
        super(name, priority, interval);
        nameList = new Vector(size());
        pending  = new Hashtable();
    }

    /**
     * Adds an observer of a public chat room.  Each event in the specified room
     * will be sent to the callback URL with an HTTP POST containing the event in
     * XML notation.  If the room does not yet exist, the event notifications
     * will start once the room is created.
     *
     * @param name  the name of the room to observe.
     * @param url   the callback URL to receive each event as an HTTP POST.
     */

    public synchronized void addRoomObserver(String name, URL url) {
        Observable room = (Observable) super.get(name);
        if (room == null) {                 // Room not yet created
            Vector urlList = (Vector) pending.get(name);
            if (urlList == null) {            // First observer of this room
                urlList = new Vector();
            }
            urlList.addElement(url);
            pending.put(name, urlList);
        } else {                            // Room already exists
            new EventSender(room, url);
        }
    }

    /**
     * Adds all pending observers to the new room and notifies the room that it
     * has just been created.
     *
     * @param name  the name of the room just created.
     * @param room  the public chat room.
     */

    // Called only from other synchronized methods.
    private void addPendingObservers(String name, PublicChat room) {
        Vector vector = (Vector) pending.remove(name);
        if (vector != null) {
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                new EventSender(room, (URL) enumeration.nextElement());
            }
            room.addNotify();
        }
    }

    /**
     * Adds a room to the list.
     *
     * @param name  the name of the room to add.
     * @param room  the room to add.
     * @return  the previous room in the list with this name, or <code>null</code>
     *          if there was no room in the list with this name.
     */

    // Don't call this "put" since Hashtable.put has a recursive call when the
    // table is rehashed.  The recursive call to "put" causes us to add the room
    // name to our Vector twice.
    public synchronized Object add(String name, PublicChat room) {
        nameList.addElement(name);          // Add name to vector
        Object old = super.put(name, room); // Add pair to hashtable
        addPendingObservers(name, room);    // Add and notify pending observers
        return old;
    }

    /**
     * Adds a room to the top of the list.
     *
     * @param name  the name of the room to add to the top of the list.
     * @param room  the room to add.
     * @return  the previous room in the list with this name, or <code>null</code>
     *          if there was no room in the list with this name.
     */

    public synchronized Object addFirst(String name, PublicChat room) {
        nameList.insertElementAt(name, 0);  // Add name as first element in vector
        Object old = super.put(name, room); // Add pair to hashtable
        addPendingObservers(name, room);    // Add and notify pending observers
        return old;
    }

    /**
     * Removes a room from the list.
     *
     * @param name  the name of the room to remove.
     * @return  the room with the given name, or <code>null</code> if there is no
     *          such room in the list.
     */

    // Do not modify this method signature so that the super class Grouptable will
    // call this method when it calls remove(Object).
    public synchronized Object remove(Object name) {
        nameList.removeElement(name);       // Remove name from vector
        PublicChat room = (PublicChat) super.remove(name);  // Remove pair
        room.removeNotify();                // Notify and remove all observers
        return room;
    }

    /**
     * Gets the names of all the rooms in the list.
     *
     * @return  the list of room names for all the rooms in the server.
     */

    synchronized String[] getNames() {
        return (String[]) nameList.toArray(new String[0]);
    }

    /**
     * Gets the names of all the rooms in the list with the specific substring.
     *
     * @param filter  the filter substring, trimmed of leading and trailing
     *                spaces.
     * @return  the list of room names with the specified substring.
     */

    synchronized String[] getNames(String filter) {
        String[] list = getNames();
        if (filter.length() == 0) {
            return list;
        }

        filter = filter.toLowerCase();
        Vector vector = new Vector();
        for (int i = 0; i < list.length; i++) {
            if (list[i].toLowerCase().indexOf(filter) != -1) {
                vector.addElement(list[i]);
            }
        }
        return (String[]) vector.toArray(new String[0]);
    }
}
