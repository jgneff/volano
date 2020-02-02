/*
 * EnterRoom.java - a packet for entering a room.
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
 * This class encapsulates an enter room request, indication, and confirmation.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     RoomPacket
 */

public class EnterRoom extends Packet implements RoomPacket {
    // Values for result.
    public static final int OKAY         = 1;
    public static final int ROOM_FULL    = 2;
    public static final int NAME_TAKEN   = 3;
    public static final int MEMBER_TAKEN = 4;
    public static final int NO_SUCH_ROOM = 5;

    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    // Request fields.
    private String roomName = "";
    private String userName = "";
    private String profile  = "";

    // Indication fields.
    private String address  = "";
    private String member   = "";
    private String linked   = "";

    // Confirmation fields.
    private int        result;
    private int        roomType;          // Room type constants in RoomPacket
    private int        count;             // Room count (including audience)
    private String[][] users = new String[0][];

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

    public EnterRoom() {}

    /**
     * Creates an enter room request packet.
     *
     * @param roomName  the name of the room to enter.
     * @param userName  the name of the user entering the room.
     * @param profile   the profile of the user entering the room.
     */

    public EnterRoom(String roomName, String userName, String profile) {
        this.roomName = roomName;
        this.userName = userName;
        this.profile  = profile;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_ENTER_ROOM;
    }

    /**
     * Changes this packet into an enter room indication.
     *
     * @param address  the name or numeric IP address of the host from which the
     *                 user is connected to the server.
     * @param member   "true" if the user is a member; otherwise "false".
     */

    public void indication(String address, String member, String linked) {
        setType(INDICATION);
        this.address = address;
        this.member  = member;
        this.linked  = linked;
    }

    /**
     * Changes this packet into an enter room indication for an audience member
     * of a moderated event.
     */

    public void audienceIndication() {
        setType(INDICATION);
        this.userName = "";
        this.profile  = "";
    }

    /**
     * Changes this packet into a negative enter room confirmation.
     *
     * @param result  the reason the enter room request was denied.
     */

    public void confirm(int result) {
        setType(CONFIRM);
        this.result = result;
    }

    /**
     * Changes this packet into a positive enter room confirmation.
     *
     * @param result    the successful result code.
     * @param roomType  the type of the room (normal or event).
     * @param count     the count of people in the room, including the audience
     *                  for event auditoriums.
     * @param users     the list of users with each element giving the name,
     *                  profile, address, and whether they're a member.
     */

    public void confirm(int result, int roomType, int count, String[][] users) {
        confirm(result);
        this.roomType = roomType;
        this.count    = count;
        this.users    = users;
    }

    /**
     * Gets the name of the room.
     *
     * @returns the room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets the name of the user.
     *
     * @returns the user name.
     */

    public String getUserName() {
        return userName;
    }

    /**
     * Gets the profile of the user.
     *
     * @returns the user profile.
     */

    public String getProfile() {
        return profile;
    }

    /**
     * Gets the result code from an enter room confirmation.
     *
     * @returns the result code.
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
     * Gets the room count.
     *
     * @return the count of people in the room, including audience members.
     */

    public int getCount() {
        return count;
    }

    /**
     * Gets the name or numeric IP address of the client entering the room from
     * an enter room indication.
     *
     * @returns the result code.
     */

    public String getAddress() {
        return address;
    }

    /**
     * Checks whether or not the client entering the room from an enter room
     * indication is a member.
     *
     * @returns  "true" if the user is a member; otherwise "false".
     */

    public String isMember() {
        return member;
    }

    /**
     * Checks whether or not the member link should be displayed for this member.
     *
     * @returns  "true" if the member link should be displayed; otherwise "false".
     */

    public String showLink() {
        return linked;
    }

    /**
     * Gets the list of users in the room from a positive enter room confirmation.
     *
     * @returns the list of users in the room.
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
        output.writeUTF(profile);
        output.writeUTF(roomName);
        output.writeUTF(userName);
        switch (getType()) {
        case INDICATION:
            output.writeUTF(address);
            output.writeUTF(member);
            output.writeUTF(linked);
            break;

        case CONFIRM:
            output.writeInt(result);
            output.writeInt(roomType);
            output.writeInt(count);
            output.writeInt(users.length);
            for (int i = 0; i < users.length; i++) {
                output.writeUTF(users[i][NAME]);
                output.writeUTF(users[i][PROFILE]);
                output.writeUTF(users[i][HOST]);
                output.writeUTF(users[i][MEMBER]);
                output.writeUTF(users[i][LINK]);
            }
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
        profile  = input.readUTF().trim();  // 2.1.10
        roomName = input.readUTF();
        userName = input.readUTF().trim();  // 2.1.10
        switch (getType()) {
        case INDICATION:
            address = input.readUTF();
            member  = input.readUTF();
            linked  = input.readUTF();
            break;

        case CONFIRM:
            result    = input.readInt();
            roomType  = input.readInt();
            count     = input.readInt();
            int n     = input.readInt();
            users = new String[n][MAX_USERINFO];
            for (int i = 0; i < n; i++) {
                users[i][NAME]    = input.readUTF();
                users[i][PROFILE] = input.readUTF();
                users[i][HOST]    = input.readUTF();
                users[i][MEMBER]  = input.readUTF();
                users[i][LINK]    = input.readUTF();
            }
            break;
        }
    }
}
