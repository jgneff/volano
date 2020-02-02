/*
 * Authenticate.java - a packet for responding to an authentication challenge.
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
 * This class encapsulates a packet for responding to a client authentication
 * challenge.
 *
 * @author  John Neffenger
 * @version 31 Jan 2002
 */

public class Authenticate extends Packet {
    // Authentication result codes.
    public static final int OKAY   = 1;
    public static final int DENIED = 2;

    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

    // Request fields.
    private byte[] signature = new byte[0];

    // Confirmation fields.
    private int      result;              // See constants above
    private String[] rooms = new String[0];

    /*
      private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7',
                                            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

      private static String hexEncode(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
          byte b = bytes[i];
          buffer.append(DIGITS[(b & 0xF0) >> 4]);
          buffer.append(DIGITS[b & 0x0F]);
        }
        return buffer.toString();
      }
    */

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

    public Authenticate() {}

    /**
     * The constructor for sending the authentication request.
     *
     * @param signature the challenge response bytes.
     */

    public Authenticate(byte[] signature) {
        this.signature = signature;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_AUTHENTICATE;
    }

    /**
     * Changes this packet into a negative authenticate confirmation.
     *
     * @param result the result of the authentication challenge.
     */

    public void confirm(int result) {
        setType(CONFIRM);
        this.result = result;
    }

    /**
     * Changes this packet into a positive authenticate confirmation.
     *
     * @param result the result of the authentication challenge.
     * @param rooms  an array listing the names of all the public rooms in the
     *               server.
     */

    public void confirm(int result, String[] rooms) {
        confirm(result);
        this.rooms = rooms;
    }

    /**
     * Gets the digital signature response bytes.
     *
     * @return the signature response bytes.
     */

    public byte[] getSignature() {
        return signature;
    }

    /**
     * Gets the challenge result code returned by the server.
     *
     * @param result  the result code indicating success or failure of the
     *                challenge.
     */

    public int getResult() {
        return result;
    }

    /**
     * Gets the names of the rooms in the server.
     *
     * @return the list of room names.
     */

    public String[] getRooms() {
        return rooms;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        switch (getType()) {
        case REQUEST:
            output.writeByte(signature.length);  // Signature less than 255 bytes
            if (signature.length > 0) {
                output.write(signature);
            }
            // System.out.println("Response  = " + hexEncode(signature));
            break;
        case CONFIRM:
            output.writeInt(result);
            output.writeInt(rooms.length);
            for (int i = 0; i < rooms.length; i++) {
                output.writeUTF(rooms[i]);
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
        switch (getType()) {
        case REQUEST:
            int size = input.readUnsignedByte();
            signature = new byte[size];
            if (size > 0) {
                input.read(signature);
            }
            break;
        case CONFIRM:
            result = input.readInt();
            int count = input.readInt();
            rooms = new String[count];
            for (int i = 0; i < count; i++) {
                rooms[i] = input.readUTF();
            }
            break;
        }
    }
}
