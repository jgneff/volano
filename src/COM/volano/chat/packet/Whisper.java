/*
 * Whisper.java - a packet for whispering in a public room.
 * Copyright (C) 2000 John Neffenger
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
 * This class encapsulates a whisper request and indication.
 *
 * @author  John Neffenger
 * @version 29 Jan 2000
 * @see     RoomPacket
 */

public class Whisper extends Packet implements RoomPacket {
    private String roomName = "";
    private String fromName = "";
    private String toName   = "";
    private String text     = "";

    /**
     * The no-arg constructor required for deserialization.
     */

    public Whisper() {}

    /**
     * Creates a public whisper request.
     *
     * @param roomName  the name of the room to receive the whisper text.
     * @param fromName  the name of the user sending the whisper text.
     * @param toName    the name of the person to receive the whisper text.
     * @param text      the whisper text.
     */

    public Whisper(String roomName, String fromName, String toName, String text) {
        this.roomName = roomName;
        this.fromName = fromName;
        this.toName   = toName;
        this.text     = text;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_WHISPER;
    }

    /**
     * Changes this packet into a public whisper indication.
     *
     * @param fromName  the name of the user sending the whisper text.
     */

    // 2.1.10 - Override the "from" name given by the client with the actual one.
    public void indication(String fromName) { // 2.1.10
        setType(INDICATION);
        this.fromName = fromName;               // 2.1.10
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
     * Gets the name of the user sending the whisper text.
     *
     * @return the sender's name.
     */

    public String getFromName() {
        return fromName;
    }

    /**
     * Gets the name of the user receiving the whisper text.
     *
     * @return the recipient's name.
     */

    public String getToName() {
        return toName;
    }

    /**
     * Gets the whisper text itself.
     *
     * @return the whisper text.
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
        output.writeUTF(roomName);
        output.writeUTF(fromName);
        output.writeUTF(toName);
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
        roomName = input.readUTF();
        // 2.1.10 - The "from" user name is ignored on the request.
        fromName = input.readUTF();
        toName   = input.readUTF();
        text     = input.readUTF();
    }
}
