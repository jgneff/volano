/*
 * Room.java - an abstract class for common chat room methods.
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
import  java.util.Observable;

/**
 * This abstract class defines the methods common to the public and private chat
 * rooms.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

abstract class Room extends Observable {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    /**
     * Notifies room observers of the event.
     *
     * @param object  the event object to send to all observers.
     */

    protected synchronized void notifyEvent(Object object) {
        setChanged();
        notifyObservers(object);
    }

    /**
     * Gets the key to the room.
     *
     * @return  the integer identifier for private chat rooms or the room name for
     *          public and personal chat rooms.
     */

    abstract Object key();

    /**
     * Gets the size of the room.
     *
     * @return  the number of users in the chat room.
     */

    abstract int size();
}
