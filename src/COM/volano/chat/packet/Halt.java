/*
 * Halt.java - a packet for halting the server.
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
 * This class encapsulates a server shutdown request and confirmation.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Halt extends Packet {
    private String  password = "";
    private boolean allowed;

    /**
     * The no-arg constructor required for deserialization.
     */

    public Halt() {}

    /**
     * Creates a shutdown request packet.
     *
     * @param password  the server administrative password.
     */

    public Halt(String password) {
        this.password = password;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_HALT;
    }

    /**
     * Changes this packet into a shutdown confirmation.
     *
     * @param allowed  <code>true</code> if the password is correct and the
     *                 shutdown is allowed to proceed; otherwise
     *                 <code>false</code>.
     */

    public void confirm(boolean allowed) {
        setType(CONFIRM);
        this.allowed = allowed;
    }

    /**
     * Gets the administrative password.
     *
     * @return the administrative password.
     */

    public String getPassword() {
        return password;
    }

    /**
     * Checks whether the shutdown request was allowed.
     *
     * @return <code>true</code> if the password is correct and the shutdown was
     *         allowed to proceed; otherwise <code>false</code>.
     */

    public boolean isAllowed() {
        return allowed;
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
            output.writeUTF(password);
            break;
        case CONFIRM:
            output.writeBoolean(allowed);
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
            password = input.readUTF();
            break;
        case CONFIRM:
            allowed = input.readBoolean();
            break;
        }
    }
}
