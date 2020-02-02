/*
 * Attr.java - an interface for defining connection attribute keys.
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

/**
 * This interface defines the names of the attributes assigned to connections by
 * the server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface Attr {
    String ACCESS_REQ   = "access";       // Access request packet with client statistics
    String ROOM_NAME    = "room";         // Default public room name or personal document base
    String ROOM_LIST    = "roomlist";     // List of rooms created with createRooms method - 2.1.9
    String CLIENT_VER   = "client";       // Client version string
    String IS_ADMIN     = "admin";        // Connection is with an administrator
    String IS_MONITOR   = "monitor";      // Connection is with a monitor
    String IS_STAGE     = "stage";        // Client is on event stage
    String MEMBER_NAME  = "member";       // Member name or null if not a member
    String MEMBER_LINK  = "link";         // "true" or "false" for member link
    String RANDOM_BYTES = "random";       // Random byte array for signing
    String EXPECTED     = "expected";     // The packet expected next
}
