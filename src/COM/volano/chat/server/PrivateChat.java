/*
 * PrivateChat.java - a private chat room.
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
import  COM.volano.chat.Build;
import  COM.volano.chat.event.*;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  java.io.*;
import  java.net.*;
import  java.text.*;
import  java.util.*;

/**
 * This class represents a one-on-one private chat room.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

// This bug causes a big problem for VolanoChat:
//
//   BugId 4086574, "java.util.Observable never lets go of its observers"
//   http://developer.java.sun.com/developer/bugParade/bugs/4086574.html
//
// To make a long story short, it turns out that if you went into the
// VolanoChat server and chatted and private chatted and then left, the
// major objects you created in the server were not released until
// *everyone* with whom you private chatted also left the server and
// *everyone* with whom they private chatted also left.
//
// Picture a big sticky ball in the server to which you get stuck if you
// private chat.  The big sticky ball (roughly 80-some megabytes) doesn't
// go away until everyone who happened to touch it leaves.
//
// The fix is to upgrade to JDK 1.2 or run JDK 1.1.7 with a fixed
// java.util.Observable class.

class PrivateChat extends Room implements Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static Object lastRoomIdLock = new Object();  // Synchronization lock
    private static int    lastRoomId;                     // Last used private chat room identifier

    private Value      value;
    private String     roomName;
    private Connection fromConn;
    private String     fromName;
    private Connection toConn;
    private String     toName;

    private int        count     = 2;
    private boolean    open      = true;
    private long       startTime = System.currentTimeMillis();
    private int        roomId    = nextRoomId();

    /**
     * Gets the next private chat room identifier.
     *
     * @return  the next globally unique private chat room identifier.
     */

    private static int nextRoomId() {
        synchronized (lastRoomIdLock) {
            return ++lastRoomId;
        }
    }

    /**
     * Creates a new private chat room.
     *
     * @param value     the server property values.
     * @param roomName  the room name from which this private chat session was
     *                  initiated.
     * @param fromConn  the connection of the initiator.
     * @param fromName  the name of the initiator.
     * @param toConn    the connection of the responder.
     * @param toName    the name of the responder.
     */

    PrivateChat(Value value, String roomName, Connection fromConn, String fromName, Connection toConn, String toName) {
        this.value    = value;
        this.roomName = roomName;
        this.fromConn = fromConn;
        this.fromName = fromName;
        this.toConn   = toConn;
        this.toName   = toName;
    }

    /**
     * Gets the key to this room.
     *
     * @return  the key to this room in the list of private chat rooms.
     */

    public Object key() {
        return new Integer(roomId);
    }

    /**
     * Gets the size of this room.
     *
     * @return  the number of people in this private chat room.
     */

    public int size() {
        return count;
    }

    /**
     * Gets the integer identifier of this private chat session.
     *
     * @return  the integer identifier for this private chat session.
     */

    int getId() {
        return roomId;
    }

    // Old deadlock problem ...
    //
    // We need to start up an independent thread for this private chat session
    // because Observable.addObserver and Observable.notifyObservers are both
    // synchronized methods in JDK 1.0.2.
    //
    // If we use the connection's receive thread (calling PrivateChat.update) to
    // add and delete this as an observer of the connections, we can get a
    // deadlock where:
    //   - thread 1 has a lock on connection A (through notifyObservers) but needs
    //     the lock on B (for addObserver or deleteObserver), and
    //   - thread 2 has a lock on connection B (through notifyObservers) but needs
    //     the lock on A (for addObserver or deleteObserver).
    // Since thread 1 has A and needs B, but thread 2 has B and needs A, we're
    // deadlocked.  Using an independent thread for handling the received objects
    // avoids the problem.  But a better solution is to simply start observing the
    // second connection when the first packet arrives on it for this room.

    /**
     * Called when a packet is received from the client, or when the connection to
     * the client is closed.
     *
     * @param observable  the connection to the client.
     * @param object      the packet received from the client, or
     *                    <code>null</code> if the connection is closed.
     */

    public void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Private " + roomId + " update ...");
        }

//  if (object instanceof PrivatePacket) {
//    PrivatePacket packet = (PrivatePacket) object;
        if (object instanceof PrivatePacket && object instanceof Packet) {
            PrivatePacket privatePacket = (PrivatePacket) object;
            Packet        packet        = (Packet) object;
            if (! packet.isHandled() && privatePacket.getRoomId() == roomId) {
                Connection connection = (Connection) observable;
                if (packet instanceof Chat) {
                    chatRequest(connection, (Chat) packet);
                } else if (packet instanceof ExitPrivate) {
                    exitPrivateRequest(connection, (ExitPrivate) packet);
                }
            }
        } else if (object == null) {
            nullObject((Connection) observable);
        }
    }

    /**
     * Handles a private chat request.
     *
     * @param connection  the connection to the client.
     * @param request     the private chat request.
     */

    private void chatRequest(Connection connection, Chat request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Private " + roomId + " chatRequest ...");
        }

        request.setHandled();
        String userName = request.getUserName();
        String text     = request.getText();
        if (userName.length() > value.lengthUsername || text.length() > value.lengthChattext) {
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);    // Not our VolanoChat Client
        } else {
            // 2.1.10 - Do not trust user name given by client.
            userName = getName(connection);
            request.indication(roomId, userName);
            send(getOther(connection), request);

            String toName = getName(getOther(connection));
            notifyEvent(new ChatEvent(roomId, userName, toName, text));
        }
    }

    /**
     * Handles an exit private request.
     *
     * @param connection  the connection to the client.
     * @param request     the exit private request.
     */

    private void exitPrivateRequest(Connection connection, ExitPrivate request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Private " + roomId + " exitPrivateRequest ...");
        }

        // 2.1.10 - Do not trust "from" name given by client.
        request.setHandled();
        connection.deleteObserver(this);
        if (fromConn != toConn) {
            String fromName = getName(connection);
            request.indication(fromName);
            send(getOther(connection), request);
        }
        close();
    }

    /**
     * Handles the <code>null</code> object indicating that the connection is
     * closed.
     *
     * @param connection  the connection to the client.
     */

    private void nullObject(Connection connection) {
        if (Build.UPDATE_TRACE) {
            System.out.println("Private " + roomId + " nullObject ...");
        }

        connection.deleteObserver(this);
        if (fromConn != toConn) {
            ExitPrivate packet = new ExitPrivate(roomId, getName(connection));
            packet.indication();
            send(getOther(connection), packet);
        }
        close();
    }

    /**
     * Closes this private chat room.
     */

    private synchronized void close() {
        if (open) {                 // Make sure we do this only once
            open = false;
            writeLog();
            count = 0;
            deleteObservers();
        }
    }

    /**
     * Sends a packet on a connection.
     *
     * @param connection  the connection on which to send the packet.
     * @param packet      the packet to send.
     */

    private void send(Connection connection, Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {}  // Error means connection is closed -- ignore
    }

    /**
     * Writes an entry to the private room log file.
     */

    private void writeLog() {
        try {
            if (Log.pvt != null && value.formatChatPrivate.toPattern().length() > 0) {
                long endTime  = System.currentTimeMillis();
                int  duration = Math.round((endTime - startTime) / 1000.0f);

                Object[] info = new Object[Default.PRIVATE_SIZE];
                info[Default.PVT_DATE]       = value.format(new Date(endTime));
                info[Default.PVT_DURATION]   = new Integer(duration);
                info[Default.PVT_ROOM]       = roomName;
                info[Default.PVT_USER1_NAME] = fromName;
                info[Default.PVT_USER1_HOST] = fromConn.getHostAddress();
                info[Default.PVT_USER2_NAME] = toName;
                info[Default.PVT_USER2_HOST] = toConn.getHostAddress();
                // Log.pvt.println(value.formatPrivate.format(info));
                Log.pvt.log(value.formatPrivate.format(info));
            }
        } catch (IllegalArgumentException e) {
            Log.printError(Msg.BAD_PRIVATE_FORMAT, e);
        }
    }

    /**
     * Gets the other connection.
     *
     * @param connection  this connection.
     * @return  the other connection.
     */

    private Connection getOther(Connection connection) {
        return connection == fromConn ? toConn : fromConn;
    }

    /**
     * Gets the name of the user whose connection is specified.
     *
     * @param connection  the connection to the user whose name is requested.
     * @return  the name of the user with this connection.
     */

    private String getName(Connection connection) {
        return connection == fromConn ? fromName : toName;
    }

    public String toString() {
        return fromName + " -> " + toName + " (" + roomName + ")";
    }
}
