/*
 * UserList.java - a packet for getting the list of users in a room.
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
 * This class encapsulates a packet for getting the list of users in a room.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see RoomPacket
 */

public class UserList extends Packet implements RoomPacket {
    // Confirmation result codes.
    public static final int OKAY         = 1;
    public static final int NO_SUCH_ROOM = 2;

    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    // Request fields.
    private String roomName = "";

    // Confirmation fields.
    private int        result;            // See constants above
    private int        roomType;          // Room type constants in RoomPacket
    private String     documentBase = "";
    private String[][] users        = new String[0][];

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

    public UserList() {}

    /**
     * Creates a user list request.
     *
     * @param roomName  the name of the room whose list of users is requested.
     */

    public UserList(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_USER_LIST;
    }

    /**
     * Changes this packet into a negative user list confirmation.
     *
     * @param result  the result code indicating why the request failed.
     */

    public void confirm(int result) {
        setType(CONFIRM);
        this.result = result;
    }

    /**
     * Changes this packet into a positive user list confirmation.
     *
     * @param result        the successful result code.
     * @param roomType      the type of the room (normal or event).
     * @param documentBase  the referring Web page through which this room was
     *                      created, or an empty string if this is a permanent
     *                      room.
     * @param users         the list of users in the room.
     */

    public void confirm(int result, int roomType, String documentBase, String[][] users) {
        confirm(result);
        this.roomType     = roomType;
        this.documentBase = documentBase;
        this.users        = users;
    }

    /**
     * Gets the confirmation result code.
     *
     * @return the result code whose values are defined by static fields of this
     *         class.
     */

    public int getResult() {
        return result;
    }

    /**
     * Gets the room type.
     *
     * @return the type of the room (normal or event).
     */

    public int getRoomType() {
        return roomType;
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
     * Gets the address of the referring Web page through which the room was
     * created.
     *
     * @return the room's referring Web page address.
     */

    public String getDocumentBase() {
        return documentBase;
    }

    /**
     * Gets the list of users.
     *
     * @return the list of users, giving the name, profile, host name or IP
     *         address, and whether the user is a member for each user in the
     *         list.
     */

    public String[][] getUsers() {
        return users;
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
        if (getType() == CONFIRM) {
            output.writeInt(result);
            output.writeInt(roomType);
            output.writeUTF(documentBase);
            output.writeInt(users.length);
            for (int i = 0; i < users.length; i++) {
                output.writeUTF(users[i][NAME]);
                output.writeUTF(users[i][PROFILE]);
                output.writeUTF(users[i][HOST]);
                output.writeUTF(users[i][MEMBER]);
                output.writeUTF(users[i][LINK]);
            }
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
        roomName = input.readUTF();
        if (getType() == CONFIRM) {
            result       = input.readInt();
            roomType     = input.readInt();
            documentBase = input.readUTF();
            int count    = input.readInt();
            users        = new String[count][MAX_USERINFO];
            for (int i = 0; i < count; i++) {
                users[i][NAME]    = input.readUTF();
                users[i][PROFILE] = input.readUTF();
                users[i][HOST]    = input.readUTF();
                users[i][MEMBER]  = input.readUTF();
                users[i][LINK]    = input.readUTF();
            }
        }
    }
}
