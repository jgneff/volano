/*
 * EnterPrivate.java - a packet for starting a private chat session.
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
 * This class encapsulates an enter private request, indication, and
 * confirmation.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     RoomPacket
 * @see     PrivatePacket
 */

public class EnterPrivate extends Packet implements RoomPacket, PrivatePacket {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    private int    roomId;
    private String roomName    = "";
    private String fromName    = "";
    private String toName      = "";

    private String fromHost    = "";
    private String fromProfile = "";
    private String toHost      = "";
    private String toProfile   = "";

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

    public EnterPrivate() {}

    /**
     * Creates an enter private room request.
     *
     * @param roomName  the public room from which this private chat session is
     *                  requested.
     * @param fromName  the name of the user sending the private chat request.
     * @param toName    the name of the user receiving the private chat
     *                  indication.
     */

    public EnterPrivate(String roomName, String fromName, String toName) {
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
        return ChatPacketId.PACKET_ENTER_PRIVATE;
    }

    /**
     * Changes this packet into a private chat indication.
     *
     * @param roomId       the identifier for this private chat session.
     * @param fromName     the name of the user sending the private chat request.
     * @param fromHost     the name or IP address of the sending user's host.
     * @param fromProfile  the profile of the sending user.
     */

    public void indication(int roomId, String fromName, String fromHost, String fromProfile) { // 2.1.10
        setType(INDICATION);
        this.roomId      = roomId;
        this.fromName    = fromName;        // 2.1.10
        this.fromHost    = fromHost;
        this.fromProfile = fromProfile;
    }

    /**
     * Changes this packet into a private chat confirmation.
     *
     * @param roomId     the identifier for this private chat session.
     * @param fromName   the name of the user sending the private chat request.
     * @param toHost     the name or IP address of the receiving user's host.
     * @param toProfile  the profile of the receiving user.
     */

    public void confirm(int roomId, String fromName, String toHost, String toProfile) {    // 2.1.10
        setType(CONFIRM);
        this.roomId    = roomId;
        this.fromName  = fromName;          // 2.1.10
        this.toHost    = toHost;
        this.toProfile = toProfile;
    }

    /**
     * Gets the room identifier for this private chat session.
     *
     * @return the room identifier.
     */

    public int getRoomId() {
        return roomId;
    }

    /**
     * Gets the public room name from which the private chat session was created.
     *
     * @return the public room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the name of the user sending the private chat request.
     *
     * @return the sending user's name.
     */

    public String getFromName() {
        return fromName;
    }

    /**
     * Gets the name or IP address of the sending user's host.
     *
     * @return the sending user's host name or IP address.
     */

    public String getFromHost() {
        return fromHost;
    }

    /**
     * Gets the profile of the sending user.
     *
     * @return the sending user's profile.
     */

    public String getFromProfile() {
        return fromProfile;
    }

    /**
     * Gets the name of the user receiving the private chat indication.
     *
     * @return the receiving user's name.
     */

    public String getToName() {
        return toName;
    }

    /**
     * Gets the name or IP address of the receiving user's host.
     *
     * @return the receiving user's host name or IP address.
     */

    public String getToHost() {
        return toHost;
    }

    /**
     * Gets the profile of the receiving user.
     *
     * @return the receiving user's profile.
     */

    public String getToProfile() {
        return toProfile;
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
        switch (getType()) {
        case INDICATION:
            output.writeInt(roomId);
            output.writeUTF(fromHost);
            output.writeUTF(fromProfile);
            break;

        case CONFIRM:
            output.writeInt(roomId);
            output.writeUTF(toHost);
            output.writeUTF(toProfile);
            break;
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
        roomName    = input.readUTF();
        // 2.1.10 - The "from" user name is ignored.
        fromName    = input.readUTF();
        toName      = input.readUTF();
        switch (getType()) {
        case INDICATION:
            roomId      = input.readInt();
            fromHost    = input.readUTF();
            fromProfile = input.readUTF();
            break;

        case CONFIRM:
            roomId    = input.readInt();
            toHost    = input.readUTF();
            toProfile = input.readUTF();
            break;
        }
    }
}
