/*
 * Chat.java - a packet for chatting in a public room.
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

public class Chat extends Packet implements RoomPacket, PrivatePacket {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    private boolean question;
    private int     roomId;
    private String  roomName = "";
    private String  userName = "";
    private String  text     = "";

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

    public Chat() {}

    /**
     * Creates a public chat request.
     *
     * @param roomName  the name of the room to receive the chat text.
     * @param userName  the name of the user sending the chat text.
     * @param text      the chat text.
     */

    public Chat(String roomName, String userName, String text) {
        this.roomName = roomName;
        this.userName = userName;
        this.text     = text;
    }

    /**
     * Creates a private chat request.
     *
     * @param roomId    the identifier of the private chat session.
     * @param userName  the name of the user sending the private chat text.
     * @param text      the private chat text.
     */

    public Chat(int roomId, String userName, String text) {
        this.roomId   = roomId;
        this.userName = userName;
        this.text     = text;
    }

    /**
     * Creates an administrator broadcast request.
     *
     * @param text  the broadcast chat text.
     */

    public Chat(String text) {
        this.text = text;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_CHAT;
    }

    /**
     * Changes this packet into a public chat indication.
     */

    public void indication() {
        setType(INDICATION);
    }

    /**
     * Changes this packet into a public chat confirmation.
     */

    public void confirm() {
        setType(CONFIRM);
        userName = "";
        text     = "";
    }

    /**
     * Changes this packet into a private chat indication.
     *
     * @param roomId   the identifier of the private chat session.
     * @param userName the name of the user sending the message.
     */

    public void indication(int roomId, String userName) { // 2.1.10
        setType(INDICATION);
        this.roomId   = roomId;
        this.userName = userName;                           // 2.1.10
    }

    /**
     * Changes this packet into an administrator broadcast public chat indication.
     *
     * @param roomName  the name of the room for this broadcast text.
     */

    public void indication(String roomName) {
        setType(INDICATION);
        this.roomName = roomName;
    }

    /**
     * Changes this packet into a public chat indication and sets the user name.
     *
     * @param userName  the user name for this message text.
     */

    public void indicationUser(String userName) {       // 2.1.10
        setType(INDICATION);
        this.userName = userName;                         // 2.1.10
    }

    /**
     * Gets the question flag.
     *
     * @return whether this chat message is a question for the moderator of a
     *         live event.
     */

    public boolean isQuestion() {
        return question;
    }

    /**
     * Sets the question flag.
     */

    public void setQuestion() {
        this.question = true;
    }

    /**
     * Gets the private chat session identifier.
     *
     * @return the private chat room identifier.
     */

    public int getRoomId() {
        return roomId;
    }

    /**
     * Gets the public room name.
     *
     * @return the room name.
     */

    public String getRoomName() {
        return roomName;
    }

    /**
     * Sets the public room name.
     *
     * @param roomName  the room name.
     */

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Gets the name of the user sending the chat text.
     *
     * @return the user name.
     */

    public String getUserName() {
        return userName;
    }

    /**
     * Gets the chat text itself.
     *
     * @return the chat text.
     */

    public String getText() {
        return text;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeBoolean(question);
        output.writeInt(roomId);
        output.writeUTF(roomName);
        output.writeUTF(userName);
        output.writeUTF(text);
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        question = input.readBoolean();
        roomId   = input.readInt();
        roomName = input.readUTF();
        userName = input.readUTF().trim();  // 2.1.10 - The user name is ignored.
        text     = input.readUTF().trim();  // 2.1.10
    }
}
