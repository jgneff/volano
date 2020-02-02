/*
 * Kick.java - a packet for kicking or banning a user.
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
 * This class encapsulates a packet for kicking or banning a user from the
 * server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Kick extends Packet implements RoomPacket {
    public static final int REMOVE = 1;   // Remove from room
    public static final int KICK   = 2;   // Disconnect from server
    public static final int BAN    = 3;   // Disconnect and ban from server

    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    private String  roomName    = "";
    private String  kickerName  = "";     // Ignored
    private String  userName    = "";
    private String  userAddress = "";
    private int     method      = REMOVE;

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

    public Kick() {}

    /**
     * Creates a kick request packet.
     *
     * @param roomName    the room name of the user.
     * @param kickerName  the name of the user requesting the kick or ban.
     * @param userName    the name of the user to be kicked or banned.
     * @param userAddress the IP address of the user to be kicked or banned.
     * @param method      either remove, kick, or ban.
     */

    public Kick(String roomName, String kickerName, String userName, String userAddress, int method) {
        this.roomName    = roomName;
        this.kickerName  = kickerName;
        this.userName    = userName;
        this.userAddress = userAddress;
        this.method      = method;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_KICK;
    }

    /**
     * Gets the room name.
     *
     * @return the room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the name of the user requesting the kick or ban.
     *
     * @return the kicking user's name.
     */

    public String getKickerName() {
        return kickerName;
    }

    /**
     * Gets the name of the user to be kicked or banned.
     *
     * @return the kicked user's name.
     */

    public String getUserName() {
        return userName;
    }

    /**
     * Gets the IP address of the user to be kicked or banned.
     *
     * @return the kicked user's IP address.
     */

    public String getUserAddress() {
        return userAddress;
    }

    /**
     * Gets the method of the kick -- either remove, kick, or ban.
     *
     * @return the method of the kick. <code>REMOVE</code> means remove from the
     *         room. <code>KICK</code> means disconnect from the server.
     *         <code>BAN</code> means disconnect and ban from the server.
     */

    public int getMethod() {
        return method;
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
        output.writeUTF(kickerName);
        output.writeUTF(userName);
        output.writeUTF(userAddress);
        output.writeInt(method);
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        roomName    = input.readUTF();
        // 2.1.10 - The "kicker" user name is ignored.
        kickerName  = input.readUTF();
        userName    = input.readUTF();
        userAddress = input.readUTF();
        method      = input.readInt();
    }
}
