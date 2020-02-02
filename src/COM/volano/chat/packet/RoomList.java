/*
 * RoomList.java - a packet for getting the list of rooms.
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

package COM.volano.chat.packet;
import  COM.volano.net.Packet;
import  java.io.*;
import  java.util.*;

/**
 * This class encapsulates a packet for getting the list of rooms in the server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class RoomList extends Packet {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    // Request fields.
    private String filter = "";

    // Confirmation fields.
    private String[] rooms = new String[0];

    /**
     * Sets the read pause for this class of objects.
     *
     * @param pause the pause, in milliseconds, after reading an object of this
     *              class.
     */

    public static void setReadPause(long pause) {
        readPause = pause;
    }

    /**
     * Sets the write pause for this class of objects.
     *
     * @param pause the pause, in milliseconds, after writing an object of this
     *              class.
     */

    public static void setWritePause(long pause) {
        writePause = pause;
    }

    /**
     * Gets the read pause for this object.
     *
     * @return the pause, in milliseconds, after reading this object.
     */

    public long getReadPause() {
        return readPause;
    }

    /**
     * Gets the write pause for this object.
     *
     * @return the pause, in milliseconds, after writing this object.
     */

    public long getWritePause() {
        return writePause;
    }

    /**
     * The no-arg constructor required for deserialization.
     */

    public RoomList() {}

    /**
     * Creates a user list request.
     *
     * @param filter the filter for the list of rooms.
     */

    public RoomList(String filter) {
        this.filter = filter;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_ROOM_LIST;
    }

    /**
     * Changes this packet into a room list confirmation.
     *
     * @param rooms  the list of names of the rooms in the server.
     */

    public void confirm(String[] rooms) {
        setType(CONFIRM);
        this.rooms = rooms;
    }

    /**
     * Gets the room name filter.
     *
     * @return the filter on the list of room names.
     */

    public String getFilter() {
        return filter;
    }

    /**
     * Gets the list of names of rooms in the server.
     *
     * @return the list of room names.
     */

    public String[] getRooms() {
        return rooms;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeUTF(filter);
        output.writeInt(rooms.length);
        for (int i = 0; i < rooms.length; i++) {
            output.writeUTF(rooms[i]);
        }
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        filter = input.readUTF();
        int count = input.readInt();
        rooms = new String[count];
        for (int i = 0; i < count; i++) {
            rooms[i] = input.readUTF();
        }
    }
}
