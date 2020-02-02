/*
 * Packet.java - defines a streamable packet for dynamic protocols.
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
 * This class defines a streamable packet for creating dynamic protocols based
 * on requests, indications, responses, and confirmations between a client and a
 * server application.  This class defines a compile-time switch for turning on
 * the tracing of all packets, allows for the cloning of packets, and lets
 * packets be marked as handled to avoid unnecessary processing by unintended
 * observers.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     Connection
 */

public class Packet implements Streamable, Cloneable {
    public  static boolean trace;            // Trace each packet
    private static long    readPause  = 0L;  // Pause after reading
    private static long    writePause = 0L;  // Pause after writing

    // These values should remain backward compatible with VolanoChat 1.2
    // for a graceful failure of access (incorrect applet version).
    public static final int REQUEST    = 1;
    public static final int INDICATION = 2;
    public static final int RESPONSE   = 3;
    public static final int CONFIRM    = 4;

    private static final String[] TYPE_NAME = {"request", "indication", "response", "confirm"};

    private int     type = REQUEST;               // Initialize to REQUEST
    private boolean handled;                      // Initialized to false

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
     * Clones this object.
     *
     * @return a copy of this object.
     */

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {    // Should not occur
            return null;
        }
    }

    /**
     * Sets the type of this packet.
     *
     * @param value  the type of this packet as either a request, indication,
     *               response, or confirmation.
     * @see #REQUEST
     * @see #INDICATION
     * @see #RESPONSE
     * @see #CONFIRM
     */

    protected void setType(int value) {
        type = value;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return PacketFactory.PACKET_BASE;
    }

    /**
     * Gets the type of this packet.
     *
     * @return the type of this packet as either a request, indication, response,
     *         or confirmation.
     */

    public int getType() {
        return type;
    }

    /**
     * Marks this packet as handled, with no further processing required.
     */

    public void setHandled() {
        handled = true;
    }

    /**
     * Checks whether this packet is handled, requiring no further processing.
     *
     * @return <code>true</code> if this packed is handled; otherwise
     *         <code>false</code>.
     */

    public boolean isHandled() {
        return handled;
    }

    /**
     * Writes this packet to the data output stream.
     *
     * @param output  the data output stream for writing packets.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        output.writeInt(type);
        if (trace) {
            System.out.println("<== " + toString());
        }
    }

    /**
     * Reads the packet from the data input stream.
     *
     * @param input  the data input stream for reading packets.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        type = input.readInt();
        if (trace) {
            System.out.println("==> " + toString());
        }
    }

    public String toString() {
        return getClass().getName() + " " + TYPE_NAME[type - 1];
    }
}
