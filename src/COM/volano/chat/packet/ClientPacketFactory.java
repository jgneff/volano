/*
 * ClientPacketFactory.java - a factory for creating client API chat packets.
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
import  COM.volano.net.PacketFactory;

/**
 * This class creates the appropriate chat packet based on the given identifier,
 * as described in the Design Patterns book in the Factory Method chapter.  See
 * Section 2 under Implementation called, "Parameterized factory methods."
 *
 * <p>The following chat packets are excluded from this class to reduce the
 * size of the VolanoChat Client API:</p>
 * <pre>
 * COM.volano.chat.packet.Halt
 * COM.volano.chat.packet.Monitor
 * COM.volano.chat.packet.Report
 * </pre>
 *
 * @author  John Neffenger
 * @version 2.2.0
 */

// Comment out the ones we're not using so that they won't be picked up by the
// compiler and by DashO-Pro in the applets.

public class ClientPacketFactory extends PacketFactory {
    public Packet createPacket(int id) throws ClassNotFoundException {
        switch (id) {
        case ChatPacketId.PACKET_ACCESS_OLD:
            return new Access(true);
        case ChatPacketId.PACKET_ACCESS:
            return new Access();
        case ChatPacketId.PACKET_AUTHENTICATE:
            return new Authenticate();
        case ChatPacketId.PACKET_BEEP:
            return new Beep();
        case ChatPacketId.PACKET_CHAT:
            return new Chat();
        case ChatPacketId.PACKET_CREATE_ROOMS:
            return new CreateRooms();
        case ChatPacketId.PACKET_ENTER_PRIVATE:
            return new EnterPrivate();
        case ChatPacketId.PACKET_ENTER_ROOM:
            return new EnterRoom();
        case ChatPacketId.PACKET_EXIT_PRIVATE:
            return new ExitPrivate();
        case ChatPacketId.PACKET_EXIT_ROOM:
            return new ExitRoom();
//    case ChatPacketId.PACKET_HALT:
//      return new Halt();
        case ChatPacketId.PACKET_KICK:
            return new Kick();
//    case ChatPacketId.PACKET_MONITOR:
//      return new Monitor();
        case ChatPacketId.PACKET_PASSWORD_ACCESS:
            return new PasswordAccess();
        case ChatPacketId.PACKET_PING:
            return new Ping();
//    case ChatPacketId.PACKET_REPORT:
//      return new Report();
        case ChatPacketId.PACKET_ROOM_LIST:
            return new RoomList();
        case ChatPacketId.PACKET_USER_LIST:
            return new UserList();
        case ChatPacketId.PACKET_WHISPER:
            return new Whisper();
        default:
            return super.createPacket(id);
        }
    }
}
