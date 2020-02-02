/*
 * KickEvent.java - a kick event.
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
 * This class encapsulates a kick event.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class KickEvent implements EventTags {
    public static final int REMOVE     = 1;
    public static final int DISCONNECT = 2;
    public static final int BAN        = 3;

    private String room = "";
    private String from = "";
    private String to   = "";
    private int    type = REMOVE;

    /**
     * Creates a new kick event.
     *
     * @param room the name of the room.
     * @param from the name of the user doing the kicking.
     * @param to   the name of the user getting kicked.
     * @param type the type of kick, either REMOVE, DISCONNECT, or BAN.
     */

    public KickEvent(String room, String from, String to, int type) {
        this.room = room;
        this.from = from;
        this.to   = to;
        this.type = type;
    }

    /**
     * Converts this event to its XML string representation.
     *
     * @return the XML element describing this event.
     */

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        switch (type) {
        case REMOVE:
            buffer.append(KICK_REMOVE_START);
            break;
        case DISCONNECT:
            buffer.append(KICK_DISCONNECT_START);
            break;
        case BAN:
            buffer.append(KICK_BAN_START);
            break;
        }
        buffer.append(ROOM_START + room + ROOM_END +
                      FROM_START + from + FROM_END +
                      TO_START + to + TO_END +
                      KICK_END);
        return buffer.toString();
    }
}
