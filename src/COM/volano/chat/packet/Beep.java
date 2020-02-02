/*
 * Beep.java - a packet for beeping someone in a public room.
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
 * This class encapsulates a beep request and indication.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     RoomPacket
 */

public class Beep extends Packet implements RoomPacket {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    private String roomName = "";
    private String fromName = "";
    private String toName   = "";

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

    public Beep() {}

    /**
     * Creates a new beep request packet.
     *
     * @param roomName  the name of the room.
     * @param fromName  the name of the user sending the beep.
     * @param toName    the name of the user to receive the beep.
     */

    public Beep(String roomName, String fromName, String toName) {
        this.roomName = roomName;
        this.fromName = fromName;
        this.toName   = toName;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_BEEP;
    }

    /**
     * Changes this packet into a beep indication.
     *
     * @param fromName  the name of the user sending the beep.
     */

    public void indication(String fromName) {     // 2.1.10
        setType(INDICATION);
        this.fromName = fromName;                   // 2.1.10
    }

    /**
     * Gets the room name for the beep.
     *
     * @return the room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the name of the user who sent the beep.
     *
     * @return the sending user's name.
     */

    public String getFromName() {
        return fromName;
    }

    /**
     * Gets the name of the user to receive the beep.
     *
     * @return the receiving user's name.
     */

    public String getToName() {
        return toName;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeUTF(roomName);
        output.writeUTF(fromName);
        output.writeUTF(toName);
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        roomName = input.readUTF();
        // 2.1.10 - The "from" user name is ignored.
        fromName = input.readUTF();
        toName   = input.readUTF();
    }
}
