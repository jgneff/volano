/*
 * StreamableError.java - a streamable error or exception.
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

package COM.volano.net;
import  java.io.*;

/**
 * This class defines a wrapper for throwable errors and exceptions in order to
 * make them streamable on data input and output streams.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     Connection
 */

public class StreamableError extends Packet {
    private String text;

    /**
     * The no-arg constructor required of all streamable objects.
     */

    public StreamableError() {
        text = "";
    }

    /**
     * Creates a new streamable error from the throwable error or exception.
     *
     * @param t  the throwable error or exception.
     */

    public StreamableError(Throwable t) {
        this.text = t.toString();
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return PacketFactory.PACKET_ERROR;
    }

    /**
     * Gets the message text associated with this streamable error.
     *
     * @return  the error or exception message text.
     */

    public String getText() {
        return text;
    }

    /**
     * Writes this streamable error to the data output stream.
     *
     * @param output  the data output stream for writing packets.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeUTF(text);
    }

    /**
     * Reads the streamable error from the data input stream.
     *
     * @param input  the data input stream for reading packets.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        text = input.readUTF();
    }
}
