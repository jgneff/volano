/*
 * ChatPacketId.java - an interface for defining chat packet identfiers.
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
import  COM.volano.chat.Build;
import  COM.volano.net.PacketFactory;

/**
 * This interface defines the chat packet integer identifiers.
 *
 * @author  John Neffenger
 * @version 2.2.0
 */

public interface ChatPacketId {
    // Note that these values are written as unsigned shorts with values 0 to
    // 65,535.  Don't change the old or new Access packet identifier between
    // builds or versions.  If you do, the new applet gets the "unable to connect"
    // page when it should get "wrong applet version", failing with:
    //   Error reading from 192.107.71.14. (java.lang.ClassNotFoundException: 201)
    int PACKET_ACCESS_OLD      = Access.OLD_NAME_LENGTH;              // 17
    int PACKET_ACCESS          = Build.PACKET_ID_OFFSET + Access.ID;  // 1 for VolanoChat, (x + 1) for VolanoMark

    int PACKET_AUTHENTICATE    = PacketFactory.START + Build.PACKET_ID_OFFSET +  1;
    int PACKET_BEEP            = PacketFactory.START + Build.PACKET_ID_OFFSET +  2;
    int PACKET_CHAT            = PacketFactory.START + Build.PACKET_ID_OFFSET +  3;
    int PACKET_CREATE_ROOMS    = PacketFactory.START + Build.PACKET_ID_OFFSET +  4;
    int PACKET_ENTER_PRIVATE   = PacketFactory.START + Build.PACKET_ID_OFFSET +  5;
    int PACKET_ENTER_ROOM      = PacketFactory.START + Build.PACKET_ID_OFFSET +  6;
    int PACKET_EXIT_PRIVATE    = PacketFactory.START + Build.PACKET_ID_OFFSET +  7;
    int PACKET_EXIT_ROOM       = PacketFactory.START + Build.PACKET_ID_OFFSET +  8;
    int PACKET_HALT            = PacketFactory.START + Build.PACKET_ID_OFFSET +  9;
    int PACKET_KICK            = PacketFactory.START + Build.PACKET_ID_OFFSET + 10;
    int PACKET_MONITOR         = PacketFactory.START + Build.PACKET_ID_OFFSET + 11;
    int PACKET_PASSWORD_ACCESS = PacketFactory.START + Build.PACKET_ID_OFFSET + 12;
    int PACKET_PING            = PacketFactory.START + Build.PACKET_ID_OFFSET + 13;
    int PACKET_REPORT          = PacketFactory.START + Build.PACKET_ID_OFFSET + 14;
    int PACKET_ROOM_LIST       = PacketFactory.START + Build.PACKET_ID_OFFSET + 15;
    int PACKET_USER_LIST       = PacketFactory.START + Build.PACKET_ID_OFFSET + 16;
    int PACKET_WHISPER         = PacketFactory.START + Build.PACKET_ID_OFFSET + 17;
}

