/*
 * Auditorium.java - an auditorium for moderated events.
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

package COM.volano.chat.server;
import  COM.volano.chat.*;
import  COM.volano.chat.event.*;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  java.io.*;
import  java.util.*;

/**
 * This class represents an auditorium for use with moderated events.
 *
 * @author  John Neffenger
 * @version 29 June 1998
 */

class Auditorium extends PublicChat {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final boolean TRACE = false;
    private static final int     INITIAL_AUDIENCE_SIZE = 100;

    private Vector audience;

    /**
     * Returns a snapshot of the audience.
     *
     * @param vector  the vector containing the audience connections.
     */

    private static Connection[] snapshot(Vector vector) {
        return (Connection[]) vector.toArray(new Connection[0]);
    }

    /**
     * Creates a new auditorium.
     *
     * @param value         the server property values.
     * @param privateList   the global list of private chat rooms.
     * @param groupName     the name of this room.
     * @param documentBase  the Web address from which this room was created.
     */

    Auditorium(Value value, Grouptable privateList, String groupName, String documentBase) {
        super(value, privateList, groupName, documentBase);
        this.roomType = RoomPacket.EVENT;
        this.audience = new Vector(INITIAL_AUDIENCE_SIZE);
    }

    /**
     * Gets the count of people in the room, including those in the audience.
     *
     * @return  the number of users in this chat room, both on stage and in the
     *          audience.
     */

    public int count() {
        return usertable.size() + audience.size();
    }

    /**
     * Prints this room for debugging purposes.
     */

    protected void printRoom() {
        synchronized (System.out) {
            super.printRoom();
            System.out.println("Audience count = " + audience.size());
        }
    }

    /**
     * Broadcasts the packet to everyone on stage and in the audience.
     *
     * @param origin  the origin of the packet and the one on which to avoid
     *                sending.
     * @param packet  the packet to send to all users except for the packet's
     *                origin.
     */

    protected void broadcast(Connection origin, Packet packet) {
        // First send to those on stage.
        super.broadcast(origin, packet);

        // Then send to everyone in the audience.
        Connection[] list = snapshot(audience);
        for (int i = 0; i < list.length; i++) {
            try {
                list[i].send(packet);
            } catch (IOException e) {} // Error means connection is closed -- ignore
        }
    }

    /**
     * Sends the packet to the moderators on stage.
     *
     * @param packet  the packet to send to the event moderators on stage.
     */

    private void sendToModerators(Packet packet) {
        Connection connection = null;
        User[]     list       = usertable.snapshot();
        for (int i = 0; i < list.length; i++) {
            connection = list[i].getConnection();
            if (connection.getBoolean(Attr.IS_STAGE) && connection.getBoolean(Attr.IS_ADMIN)) {
                send(connection, packet);
            }
        }
    }

    /**
     * Handles an enter room request.
     *
     * @param connection  the connection to the client.
     * @param request     the enter room request.
     */

    protected void enterRoomRequest(Connection connection, EnterRoom request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " enterRoomRequest ...");
        }

        if (connection.getBoolean(Attr.IS_STAGE)) {
            super.enterRoomRequest(connection, request);
        } else {
            request.setHandled();
            connection.addObserver(this);
            audience.addElement(connection);
            EnterRoom copy = (EnterRoom) request.clone();
            copy.audienceIndication();
            broadcast(connection, copy);
            request.confirm(EnterRoom.OKAY, roomType, count(), getUserInfo(value.addressBroadcast || connection.getBoolean(Attr.IS_MONITOR)));
            send(connection, request);
        }

        if (TRACE) {
            printRoom();
        }
    }

    /**
     * Handles an enter private room request.
     *
     * @param connection  the connection to the client.
     * @param request     the enter private room request.
     */

    protected void enterPrivateRequest(Connection connection, EnterPrivate request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " enterPrivateRequest ...");
        }

        if (connection.getBoolean(Attr.IS_STAGE)) {
            super.enterPrivateRequest(connection, request);
        }
    }

    /**
     * Handles a chat request.
     *
     * @param connection  the connection to the client.
     * @param request     the chat request.
     */

    protected void chatRequest(Connection connection, Chat request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " chatRequest ...");
        }

        request.setHandled();
        request.indication();
        if (connection.getBoolean(Attr.IS_STAGE)) {
            broadcast(connection, request);
            notifyEvent(new ChatEvent(groupName, request.getUserName(), request.getText()));
        } else {
            Chat question = (Chat) request.clone();
            question.setQuestion();
            sendToModerators(question);
        }

        // Send back confirmation for pacing.
        Chat copy = (Chat) request.clone();
        copy.confirm();
        send(connection, copy);
    }

    /**
     * Handles a beep request.
     *
     * @param connection  the connection to the client.
     * @param request     the beep request.
     */

    protected void beepRequest(Connection connection, Beep request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " beepRequest ...");
        }

        if (connection.getBoolean(Attr.IS_STAGE)) {
            super.beepRequest(connection, request);
        }
    }

    /**
     * Handles a kick request.
     *
     * @param connection  the connection to the client.
     * @param request     the kick request.
     */

    protected void kickRequest(Connection connection, Kick request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " kickRequest ...");
        }

        if (connection.getBoolean(Attr.IS_STAGE)) {
            super.kickRequest(connection, request);
        }
    }

    /**
     * Handles an exit room request.
     *
     * @param connection  the connection to the client.
     * @param request     the exit room request.
     */

    protected void exitRoomRequest(Connection connection, ExitRoom request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " exitRoomRequest ...");
        }

        if (connection.getBoolean(Attr.IS_STAGE)) {
            super.exitRoomRequest(connection, request);
        } else {
            request.setHandled();
            connection.deleteObserver(this);
            request.audienceIndication();
            broadcast(connection, request);
            audience.removeElement(connection);
        }

        if (TRACE) {
            printRoom();
        }
    }

    /**
     * Handles the <code>null</code> object indicating that the connection is
     * closed.
     *
     * @param connection  the connection to the client.
     */

    protected void nullObject(Connection connection) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " nullObject ...");
        }

        if (connection.getBoolean(Attr.IS_STAGE)) {
            super.nullObject(connection);
        } else {
            connection.deleteObserver(this);
            ExitRoom packet = new ExitRoom(groupName, "");
            packet.audienceIndication();
            broadcast(connection, packet);
            audience.removeElement(connection);
        }

        if (TRACE) {
            printRoom();
        }
    }
}
