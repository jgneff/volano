/*
 * ExitEvent.java - an exit room event.
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

package COM.volano.chat.event;

/**
 * This class encapsulates an exit room event.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class ExitEvent implements EventTags {
    private String room = "";
    private String user = "";

    /**
     * Creates a new exit room event.
     *
     * @param room the name of the room exited.
     * @param user the name of the user who exited.
     */

    public ExitEvent(String room, String user) {
        this.room = room;
        this.user = user;
    }

    /**
     * Converts this event to its XML string representation.
     *
     * @return the XML element describing this event.
     */

    public String toString() {
        return EXIT_START +
               ROOM_START + room + ROOM_END +
               USER_START + user + USER_END +
               EXIT_END;
    }
}
