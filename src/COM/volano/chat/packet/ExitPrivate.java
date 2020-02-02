/*
 * ExitPrivate.java - a packet for exiting a private chat session.
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

/**
 * This class encapsulates an exit private request and indication.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class ExitPrivate extends Packet implements PrivatePacket {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    private int    roomId;
    private String fromName;

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

    public ExitPrivate() {}

    /**
     * Creates an exit private request packet containing the room id and user
     * name.
     *
     * @param roomId    the identifier of the private room to exit.
     * @param userName  the name of the user exiting the room.
     */

    public ExitPrivate(int roomId, String fromName) {
        this.roomId   = roomId;
        this.fromName = fromName;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_EXIT_PRIVATE;
    }

    /**
     * Changes this packet into an exit private indication.
     */

    public void indication() {
        setType(INDICATION);
    }

    /**
     * Changes this packet into an exit private indication.
     *
     * @param userName  the name of the user exiting the room.
     */

    public void indication(String fromName) { // 2.1.10
        setType(INDICATION);
        this.fromName = fromName;               // 2.1.10
    }

    /**
     * Gets the identifier of the private room to leave.
     *
     * @return the room identifier.
     */

    public int getRoomId() {
        return roomId;
    }

    /**
     * Gets the name of the user leaving the private room.
     *
     * @return the user name.
     */

    public String getFromName() {
        return fromName;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeInt(roomId);
        output.writeUTF(fromName);
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        roomId   = input.readInt();
        // 2.1.10 - The "from" user name is ignored.
        fromName = input.readUTF();
    }
}
