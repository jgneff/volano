/*
 * ExitRoom.java - a packet for exiting a room.
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
 * This class encapsulates an exit room request and indication.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class ExitRoom extends Packet implements RoomPacket {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    private String roomName;
    private String userName;

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

    public ExitRoom() {}

    /**
     * Creates an exit room request packet containing the room name and user name.
     *
     * @param roomName  the name of the room to exit.
     * @param userName  the name of the user exiting the room.
     */

    public ExitRoom(String roomName, String userName) {
        this.roomName = roomName;
        this.userName = userName;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_EXIT_ROOM;
    }

    /**
     * Changes this packet into an exit room indication.
     */

    public void indication() {
        setType(INDICATION);
    }

    /**
     * Changes this packet into an exit room indication, identifying the user.
     *
     * @param userName  the name of the user exiting the room.
     */

    // 2.1.10 - Add name to indication, in case client lied about it.
    public void indication(String userName) {     // 2.1.10
        setType(INDICATION);
        this.userName = userName;                   // 2.1.10
    }

    /**
     * Changes this packet into an exit room indication for an audience member
     * of a moderated event.
     */

    public void audienceIndication() {
        setType(INDICATION);
        this.userName = "";
    }

    /**
     * Gets the name of the room from which the user is exiting.
     *
     * @return the room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the name of the user exiting from the room.
     *
     * @return the user name.
     */

    public String getUserName() {
        return userName;
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
        output.writeUTF(userName);
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
        // 2.1.10 - The user name is ignored.
        userName = input.readUTF();
    }
}
