/*
 * CreateRooms.java - a packet for creating dynamic public rooms.
 * Copyright (C) 1996-2000 John Neffenger
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

/**
 * This class encapsulates a create rooms request.
 *
 * @author  John Neffenger
 * @version 29 Aug 2000
 */

public class CreateRooms extends Packet {
    private static final int MAX_LIST = 255;  // Maximum unsigned byte value
    private static final int MAX_NAME = 127;
    private static final String LIST_TOO_BIG = "Room list size exceeds " + MAX_LIST;
    private static final String NAME_TOO_BIG = "Room name length exceeds " + MAX_NAME;

    private String[] roomNames = new String[0];

    /**
     * The no-arg constructor required for deserialization.
     */

    public CreateRooms() {}

    /**
     * Creates a creat rooms request packet.
     *
     * @param roomNames  the list of rooms to create.
     */

    public CreateRooms(String[] roomNames) throws IOException {
        this.roomNames = roomNames;
        if (roomNames.length > MAX_LIST) {
            throw new IOException(LIST_TOO_BIG);
        }
        for (int i = 0; i < roomNames.length; i++) {
            if (roomNames[i].length() > MAX_NAME) {
                throw new IOException(NAME_TOO_BIG);
            }
        }
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_CREATE_ROOMS;
    }

    /**
     * Gets the list of room names.
     *
     * @return  the list of dynamic public rooms to create.
     */

    public String[] getRoomNames() {
        return roomNames;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeByte(roomNames.length);  // Less than 255 names
        for (int i = 0; i < roomNames.length; i++) {
            output.writeUTF(roomNames[i]);
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
        int count = input.readUnsignedByte();
        roomNames = new String[count];
        for (int i = 0; i < count; i++) {
            roomNames[i] = input.readUTF();
            if (roomNames[i].length() > MAX_NAME) {
                throw new IOException(NAME_TOO_BIG);
            }
        }
    }
}
