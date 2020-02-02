/*
 * RingEvent.java - a ring (audio alert) event.
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
 * This class encapsulates a ring (audio alert) event.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class RingEvent implements EventTags {
    private String room = "";
    private String from = "";
    private String to   = "";

    /**
     * Creates a new ring (audio alert) event.
     *
     * @param room the name of the room.
     * @param from the name of the user sending the audio alert.
     * @param to   the name of the user receiving the audio alert.
     */

    public RingEvent(String room, String from, String to) {
        this.room = room;
        this.from = from;
        this.to   = to;
    }

    /**
     * Converts this event to its XML string representation.
     *
     * @return the XML element describing this event.
     */

    public String toString() {
        return RING_START +
               ROOM_START + room + ROOM_END +
               FROM_START + from + FROM_END +
               TO_START + to + TO_END +
               RING_END;
    }
}
