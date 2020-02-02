/*
 * Ping.java - a packet for pinging the client.
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
import  COM.volano.net.*;

/**
 * This class encapsulates a packet for pinging the client to determine whether
 * the connection is still active.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Ping extends Packet {
    private static long readPause  = 0L;    // Pause after reading
    private static long writePause = 0L;    // Pause after writing

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

    public Ping() {}

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_PING;
    }

    /**
     * Changes this packet into a ping response.
     */

    public void response() {
        setType(RESPONSE);
    }
}
