/*
 * ChatPacketFactory.java - a factory for creating chat packets.
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
 * <p>All possible chat packets may be created by this class:</p>
 * <pre>
 * COM.volano.chat.packet.Access
 * COM.volano.chat.packet.Authenticate
 * COM.volano.chat.packet.Beep
 * COM.volano.chat.packet.Chat
 * COM.volano.chat.packet.CreateRooms
 * COM.volano.chat.packet.EnterPrivate
 * COM.volano.chat.packet.EnterRoom
 * COM.volano.chat.packet.ExitPrivate
 * COM.volano.chat.packet.ExitRoom
 * COM.volano.chat.packet.Halt
 * COM.volano.chat.packet.Kick
 * COM.volano.chat.packet.Monitor
 * COM.volano.chat.packet.PasswordAccess
 * COM.volano.chat.packet.Ping
 * COM.volano.chat.packet.Report
 * COM.volano.chat.packet.RoomList
 * COM.volano.chat.packet.UserList
 * COM.volano.chat.packet.Whisper
 * </pre>
 *
 * @author  John Neffenger
 * @version 2.2.0
 */

public class ChatPacketFactory extends PacketFactory {
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
        case ChatPacketId.PACKET_HALT:
            return new Halt();
        case ChatPacketId.PACKET_KICK:
            return new Kick();
        case ChatPacketId.PACKET_MONITOR:
            return new Monitor();
        case ChatPacketId.PACKET_PASSWORD_ACCESS:
            return new PasswordAccess();
        case ChatPacketId.PACKET_PING:
            return new Ping();
        case ChatPacketId.PACKET_REPORT:
            return new Report();
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
