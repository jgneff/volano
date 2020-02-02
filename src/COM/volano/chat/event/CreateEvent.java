/*
 * CreateEvent.java - a create room event.
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
 * This class encapsulates a create room event.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class CreateEvent implements EventTags {
    private String room = "";

    /**
     * Creates a new create room event.
     *
     * @param room the name of the room created.
     */

    public CreateEvent(String room) {
        this.room = room;
    }

    /**
     * Converts this event to its XML string representation.
     *
     * @return the XML element describing this event.
     */

    public String toString() {
        return CREATE_START +
               ROOM_START + room + ROOM_END +
               CREATE_END;
    }
}
