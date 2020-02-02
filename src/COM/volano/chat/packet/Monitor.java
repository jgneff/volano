/*
 * Monitor.java - a packet for monitoring the server status.
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
 * This class encapsulates a packet for monitoring the server status from the
 * Status application program.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Monitor extends Packet {
    private String  password = "";
    private int     interval;
    private boolean allowed;

    /**
     * The no-arg constructor required for deserialization.
     */

    public Monitor() {}

    /**
     * Creates the monitor request packet.
     *
     * @param password  the administrative password to the server.
     * @param interval  the interval at which the status reports should be sent,
     *                  in seconds.
     */

    public Monitor(String password, int interval) {
        this.password = password;
        this.interval = interval;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_MONITOR;
    }

    /**
     * Changes this packet into a monitor packet confirmation.
     *
     * @param allowed  <code>true</code> if the password is correct and monitoring
     *                 by the client is allowed; otherwise <code>false</code>.
     */

    public void confirm(boolean allowed) {
        setType(CONFIRM);
        this.allowed = allowed;
    }

    /**
     * Gets the administrative password sent by the client.
     *
     * @return the adminstrative password supplied.
     */

    public String getPassword() {
        return password;
    }

    /**
     * Gets the requested interval for the status reports.
     *
     * @return the requested interval in seconds.
     */

    public int getInterval() {
        return interval;
    }

    /**
     * Checks whether the status monitoring is allowed.
     *
     * @return <code>true</code> if the password is correct and monitoring by the
     *         client is allowed; otherwise <code>false</code>.
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
            output.writeInt(interval);
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
            interval = input.readInt();
            break;
        case CONFIRM:
            allowed = input.readBoolean();
            break;
        }
    }
}
