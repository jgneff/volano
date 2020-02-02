/*
 * PacketFactory.java - a factory for creating packets.
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

/**
 * This class creates the appropriate packet based on the given identifier, as
 * described in the Design Patterns book in the Factory Method chapter.  See
 * Section 2 under Implementation called, "Parameterized factory methods."
 *
 * <p>The following packets may be created by this class:</p>
 * <pre>
 * COM.volano.net.Packet
 * COM.volano.net.StreamableError
 * </pre>
 *
 * @author  John Neffenger
 * @version 26 Oct 2001
 */

public class PacketFactory {
    // Leave room in the packet identifiers for the VolanoChat version 2.1
    // protocol whose largest first 2-byte value is 17 for the length of the
    // string "COM.volano.Access".
    //     Hex: 0011 434F4D2D ...
    //   Value:   17  C O M . v o l a n o . A c c e s s ...
    // Note that these values are written as unsigned shorts with values 0 to
    // 65,535.
    private static final int PACKET_START = 100;  // Must be greater than 17
    static final int PACKET_BASE  = PACKET_START + 1;
    static final int PACKET_ERROR = PACKET_START + 2;

    // Keep below 1,000 so we don't interfere with the VolanoMark packet ids.
    public  static final int START = 200;  // Must be greater than anything above

    public Packet createPacket(int id) throws ClassNotFoundException {
        switch (id) {
        case PACKET_BASE:
            return new Packet();
        case PACKET_ERROR:
            return new StreamableError();
        default:
            throw new ClassNotFoundException(Integer.toString(id));
        }
    }
}
