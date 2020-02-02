/*
 * PublicChat.java - a public or personal chat room.
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
import  COM.volano.util.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class represents a public or personal chat room.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class PublicChat extends Room implements Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final boolean TRACE = false;

    protected Value      value;
    private   Grouptable privateList;
    protected String     groupName;
    protected int        roomType;
    private   String     documentBase;
    protected Usertable  usertable;       // Maps names and connections to users
    private   int        guestCount;      // Count of guests in room

    /**
     * Creates a new private chat room.
     *
     * @param value         the server property values.
     * @param privateList   the global list of private chat rooms.
     * @param groupName     the name of this room.
     * @param documentBase  the Web address from which this room was created.
     */

    PublicChat(Value value, Grouptable privateList, String groupName, String documentBase) {
        this.value        = value;
        this.privateList  = privateList;
        this.groupName    = groupName;
        this.roomType     = RoomPacket.NORMAL;
        this.documentBase = documentBase;
        this.usertable    = new Usertable(value.roomLimit, value.usernameMatchcase);
    }

    /**
     * Called after the room is created in order to notify any observers.
     */

    void addNotify() {
        notifyEvent(new CreateEvent(groupName));
    }

    /**
     * Called after the room is removed in order to notify any observers.
     */

    void removeNotify() {
        notifyEvent(new DeleteEvent(groupName));
        notifyEvent(null);
        deleteObservers();          // Just in case they didn't delete themsevles
    }

    /**
     * Gets the key to this room.
     *
     * @return  the key to this room in the list of public or personal chat rooms.
     */

    public Object key() {
        return groupName;
    }

    /**
     * Gets the size of this room.
     *
     * @return  the number of users or guests in this public or personal chat
     *          room.
     */

    public int size() {
        return usertable.size() + guestCount;
    }

    /**
     * Gets the count of people in the room.
     *
     * @return  the number of users in this chat room.
     */

    public int count() {
        return usertable.size();
    }

    /**
     * Gets the name of this room.
     *
     * @return  the room name.
     */

    String name() {
        return groupName;
    }

    /**
     * Gets the Web address from which this room was created.
     *
     * @return  the Web address from which this room was created, or an empty
     *          string if this is a permanent room in the server.
     */

    String getDocumentBase() {
        return documentBase;
    }

    /**
     * Increments the count of guests in the room.
     *
     * @return  the number of guests in the room after incrementing the count.
     */

    synchronized int incrementGuest() {
        return ++guestCount;                // Return after increment
    }

    /**
     * Decrements the count of guests in the room.
     *
     * @return  the number of guests in the room after decrementing the count.
     */

    synchronized int decrementGuest() {
        return --guestCount;                // Return after decrement
    }

    /**
     * Prints this room for debugging purposes.
     */

    protected void printRoom() {
        synchronized (System.out) {
            System.out.println(groupName + " has " + usertable.size() + " users and " + guestCount + " guests.");
            System.out.println("Users are:");
            System.out.println(usertable);
        }
    }

    /**
     * Broadcasts the packet to all users in the room except the one with the
     * specified connection.
     *
     * @param origin  the origin of the packet and the one on which to avoid
     *                sending.
     * @param packet  the packet to send to all users except for the packet's
     *                origin.
     */

    protected void broadcast(Connection origin, Packet packet) {
        Connection connection = null;
        User[]     list       = usertable.snapshot();
        for (int i = 0; i < list.length; i++) {
            connection = list[i].getConnection();
            if (connection != origin) {
                try {
                    connection.send(packet);
                } catch (IOException e) {}      // Error means connection is closed -- ignore
            }
        }
    }

    /**
     * Broadcasts the packet to all users in the room except the one with the
     * specified connection, and sends a different packet to monitors than to
     * normal users.
     *
     * @param origin  the origin of the packet and the one on which to avoid
     *                sending.
     * @param normalPacket   the packet for normal users.
     * @param monitorPacket  the packet for monitors.
     */

    protected void broadcast(Connection origin, Packet normalPacket, Packet monitorPacket) {
        Connection connection = null;
        User[]     list       = usertable.snapshot();
        for (int i = 0; i < list.length; i++) {
            connection = list[i].getConnection();
            if (connection != origin) {
                try {
                    if (connection.getBoolean(Attr.IS_MONITOR)) {
                        connection.send(monitorPacket);
                    } else {
                        connection.send(normalPacket);
                    }
                } catch (IOException e) {}      // Error means connection is closed -- ignore
            }
        }
    }

    /**
     * Gets a list of all users in the room with their associated profiles, host
     * names, and member strings.
     *
     * @return  an array of users, each consisting of an array of strings with the
     *          user name, profile, host name, and member string.
     */

    protected String[][] getUserInfo(boolean includeHost) {
        User[]     list      = usertable.snapshot();
        String[][] userArray = new String[list.length][RoomPacket.MAX_USERINFO];
        for (int i = 0; i < list.length; i++) {
            userArray[i][RoomPacket.NAME]    = list[i].getName();
            userArray[i][RoomPacket.PROFILE] = list[i].getProfile();
            userArray[i][RoomPacket.HOST]    = includeHost ? list[i].getConnection().getHostAddress() : "";
            userArray[i][RoomPacket.MEMBER]  = list[i].isMember();
            userArray[i][RoomPacket.LINK]    = list[i].showLink();
        }
        return userArray;
    }

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
            System.out.println(groupName + " update ...");
        }

//  if (object instanceof RoomPacket) {
//    RoomPacket packet = (RoomPacket) object;
        if (object instanceof RoomPacket && object instanceof Packet) {
            RoomPacket roomPacket = (RoomPacket) object;
            Packet     packet     = (Packet) object;
            if (! packet.isHandled() && groupName.equals(roomPacket.getRoomName())) {
                Connection connection = (Connection) observable;
                if (packet instanceof Chat) {
                    chatRequest(connection, (Chat) packet);
                } else if (packet instanceof UserList) {
                    userListRequest(connection, (UserList) packet);
                } else if (packet instanceof EnterRoom) {
                    enterRoomRequest(connection, (EnterRoom) packet);
                } else if (packet instanceof EnterPrivate) {
                    enterPrivateRequest(connection, (EnterPrivate) packet);
                } else if (packet instanceof ExitRoom) {
                    exitRoomRequest(connection, (ExitRoom) packet);
                } else if (packet instanceof Beep) {
                    beepRequest(connection, (Beep) packet);
                } else if (packet instanceof Kick) {
                    kickRequest(connection, (Kick) packet);
                } else if (packet instanceof Whisper) {
                    whisperRequest(connection, (Whisper) packet);
                }
            }
        } else if (object == null) {
            nullObject((Connection) observable);
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

        request.setHandled();
        String userName = request.getUserName();
        String profile  = request.getProfile();
        if (userName.length() > value.lengthUsername || profile.length() > value.lengthProfile) {
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);    // Not our VolanoChat Client
        }
        // 2.1.10 - Check for blank name (which you cannot remove, kick, or ban).
        else if (userName.length() == 0) {
            connection.close(HttpURLConnection.HTTP_BAD_REQUEST);    // Not our VolanoChat Client
        }
        // 2.1.10 - Make sure this connection has no duplicates in the room.
        else if (usertable.contains(connection.getId())) {
            connection.close(HttpURLConnection.HTTP_BAD_REQUEST);    // Not our VolanoChat client
        } else {
            User user   = new User(connection, userName, profile);
            int  result = usertable.putUser(user, connection.getBoolean(Attr.IS_MONITOR));
            if (result == EnterRoom.OKAY) {
                connection.addObserver(this);
                String host = connection.getHostAddress();
                EnterRoom hostCopy = (EnterRoom) request.clone();
                hostCopy.indication(host, user.isMember(), user.showLink());
                if (value.addressBroadcast) {
                    broadcast(connection, hostCopy);
                } else {
                    EnterRoom noHostCopy = (EnterRoom) request.clone();
                    noHostCopy.indication("", user.isMember(), user.showLink());
                    broadcast(connection, noHostCopy, hostCopy);
                }
                request.confirm(result, roomType, count(), getUserInfo(value.addressBroadcast || connection.getBoolean(Attr.IS_MONITOR)));
                notifyEvent(new EnterEvent(groupName, userName, host, profile, user.getMember()));
            } else {
                request.confirm(result);
            }
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

        request.setHandled();
        String fromName = request.getFromName();
        String toName   = request.getToName();
        if (fromName.length() > value.lengthUsername || toName.length() > value.lengthUsername) {
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);    // Not our VolanoChat Client
        } else {
            User toUser   = usertable.getUser(toName);
            // 2.1.10 - Do not trust "from" name given by client.
            // User fromUser = usertable.getUser(fromName);
            User fromUser = usertable.getUser(connection.getId());
            if (toUser != null && fromUser != null) {
                fromName = fromUser.getName();      // 2.1.10
                Connection  toConn = toUser.getConnection();
                PrivateChat room   = new PrivateChat(value, groupName, connection, fromName, toConn, toName);
                privateList.put(room.key(), room);
                connection.addObserver(room);
                // FIXME:
                // We need to add both connections as observers here:
                //   toConn.addObserver(room);
                // See comments above PrivateChat.update and Main.privatePacket
                // for deadlock concerns.

                // If transcribing private chat sessions, attach the global private
                // event logging observer.
                if (value.transcribeRoomPrivate) {
                    room.addObserver(Main.getPrivateChatLogger());
                }

                int roomId = room.getId();
                if (connection != toConn) {
                    EnterPrivate copy = (EnterPrivate) request.clone();
                    String fromHost = (value.addressBroadcast || toConn.getBoolean(Attr.IS_MONITOR)) ? connection.getHostAddress() : "";
                    copy.indication(roomId, fromName, fromHost, fromUser.getProfile());
                    send(toConn, copy);
                }
                String toHost = (value.addressBroadcast || connection.getBoolean(Attr.IS_MONITOR)) ? toConn.getHostAddress() : "";
                request.confirm(roomId, fromName, toHost, toUser.getProfile());
                send(connection, request);
            }
        }
    }

    /**
     * Handles a user list request.
     *
     * @param connection  the connection to the client.
     * @param request     the user list request.
     */

    private void userListRequest(Connection connection, UserList request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " userListRequest ...");
        }

        request.setHandled();
        request.confirm(UserList.OKAY, roomType, documentBase, getUserInfo(value.addressBroadcast || connection.getBoolean(Attr.IS_MONITOR)));
        send(connection, request);
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
        // 2.1.10 - Do not trust user name given by client unless this is an
        // administrator broadcast.
        String userName = request.getUserName();
        String text     = request.getText();
        if (userName.length() > value.lengthUsername || text.length() > value.lengthChattext) {
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);    // Not our VolanoChat Client
        } else {
            // 2.1.10 - Make sure this is an administrator connection before
            // broadcasting into the room.
            // if (userName.length() == 0) {
            if (userName.length() == 0 && connection.getBoolean(Attr.IS_ADMIN)) {
                request.indication(groupName);
                broadcast(null, request);
            }
            // 2.1.10 - Check for blank name or message text.
            else if (userName.length() == 0 || text.length() == 0) {
                connection.close(HttpURLConnection.HTTP_BAD_REQUEST);    // Not our VolanoChat Client
            } else {
                // 2.1.10 - Replace user name given by client with actual one.
                userName = usertable.getName(connection.getId());
                if (userName != null) {
                    request.indicationUser(userName);
                    broadcast(connection, request);
                    // Send back confirmation for pacing.
                    Chat copy = (Chat) request.clone();
                    copy.confirm();
                    send(connection, copy);
                }
            }
            notifyEvent(new ChatEvent(groupName, userName, text));
        }
    }

    /**
     * Handles a whisper request.
     *
     * @param connection  the connection to the client.
     * @param request     the whisper request.
     */

    protected void whisperRequest(Connection connection, Whisper request) {
        if (Build.UPDATE_TRACE) {
            System.out.println(groupName + " whisperRequest ...");
        }

        request.setHandled();
        String fromName = request.getFromName();
        String toName   = request.getToName();
        String text     = request.getText();
        if (fromName.length() > value.lengthUsername ||
                toName.length()   > value.lengthUsername ||
                text.length()     > value.lengthChattext) {
            connection.close(HttpURLConnection.HTTP_ENTITY_TOO_LARGE);    // Not our VolanoChat Client
        } else {
            // 2.1.10 - Do not trust "from" name given by client.
            fromName  = usertable.getName(connection.getId());    // 2.1.10
            User user = usertable.getUser(toName);
            if (user != null && fromName != null) {               // 2.1.10
                request.indication(fromName);
                send(user.getConnection(), request);
                notifyEvent(new WhisperEvent(groupName, fromName, toName, text));
            }
        }
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

        request.setHandled();
        // 2.1.10 - Do not trust "from" name given by client.
        // String fromName = request.getFromName();
        String fromName = usertable.getName(connection.getId());    // 2.1.10
        String toName   = request.getToName();
        User user = usertable.getUser(toName);
        if (user != null) {
            request.indication(fromName);     // 2.1.10
            send(user.getConnection(), request);
            notifyEvent(new RingEvent(groupName, fromName, toName));
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

        if (connection.getBoolean(Attr.IS_MONITOR)) {
            String name    = request.getUserName();
            String address = request.getUserAddress();
            int    method  = request.getMethod();

            User user = usertable.getUser(name);
            if (user != null) {
                request.setHandled();
                Connection targetConnection = user.getConnection();
                // 2.1.8 - Don't let monitors or administrators kick other monitors or
                // administrators or themselves.
                if (! targetConnection.getBoolean(Attr.IS_MONITOR)) {
                    if (method == Kick.REMOVE) {
                        targetConnection.deleteObserver(this);
                        user = usertable.removeUser(name);
                        if (user != null) {
                            ExitRoom packet = new ExitRoom(groupName, name);
                            packet.indication();
                            broadcast(targetConnection, packet);
                            send(targetConnection, packet);
                            writeLog(user);
                        }
                    } else if (method == Kick.KICK || method == Kick.BAN) {
                        String targetHost = targetConnection.getHostAddress();
                        String kickerHost = connection.getHostAddress();
                        // 2.1.10 - Do not trust "kicker" name given by client.
                        // String kickerName = request.getKickerName();
                        String kickerName = usertable.getName(connection.getId());
                        String memberName = (String) connection.getAttribute(Attr.MEMBER_NAME);
                        String monitorId = memberName != null ? memberName : kickerHost + "/" + kickerName;

                        // targetConnection.close(HttpURLConnection.HTTP_FORBIDDEN, kickerID);
                        // 2.2.0 - Close all connections with this IP address.
                        boolean addressInUseByMonitor = false;
                        Connection[] list = Connection.get(targetHost);
                        for (int i = 0; i < list.length; i++) {
                            if (list[i].getBoolean(Attr.IS_MONITOR)) {
                                addressInUseByMonitor = true;
                            } else {
                                list[i].close(HttpURLConnection.HTTP_FORBIDDEN, monitorId);
                            }
                        }
                        if (method == Kick.BAN && ! addressInUseByMonitor) {
                            AccessControl.banHost(targetHost, groupName, name, monitorId);
                        }
                    }
                    int type = KickEvent.REMOVE;
                    switch (method) {
                    case Kick.REMOVE:
                        type = KickEvent.REMOVE;
                        break;
                    case Kick.KICK:
                        type = KickEvent.DISCONNECT;
                        break;
                    case Kick.BAN:
                        type = KickEvent.BAN;
                        break;
                    }
                    // 2.1.10 - Do not trust "kicker" name given by client.
                    String kickerName = usertable.getName(connection.getId());
                    if (kickerName != null) {
                        notifyEvent(new KickEvent(groupName, kickerName, name, type));
                    }
                }
            }
        } else {                           // Not a monitor
            request.setHandled();
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

        request.setHandled();
        connection.deleteObserver(this);
        // 2.1.10 - Don't trust the client to specify the same name used to enter.
        // Instead, handle this one just like the nullObject method.  This method
        // assumes a connection is allowed to have only one user in a given room.
        // String name = request.getUserName();
        // User   user = usertable.removeUser(name);
        User user = usertable.removeUser(connection.getId());   // 2.1.10
        if (user != null) {
            String name = user.getName();                     // 2.1.10
            request.indication(name);                         // 2.1.10
            broadcast(connection, request);
            writeLog(user);
            notifyEvent(new ExitEvent(groupName, name));
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

        connection.deleteObserver(this);
        User user = usertable.removeUser(connection.getId());
        if (user != null) {
            String   name   = user.getName();
            ExitRoom packet = new ExitRoom(groupName, name);
            packet.indication();
            broadcast(connection, packet);
            writeLog(user);
            notifyEvent(new ExitEvent(groupName, name));
        }

        if (TRACE) {
            printRoom();
        }
    }

    /**
     * Sends a packet on a connection.
     *
     * @param connection  the connection on which to send the packet.
     * @param packet      the packet to send.
     */

    protected void send(Connection connection, Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {}  // Error means connection is closed -- ignore
    }

    /**
     * Writes an entry to the public and personal room log file.
     */

    private void writeLog(User user) {
        try {
            if (Log.pub != null && value.formatChatPublic.toPattern().length() > 0) {
                long endTime  = System.currentTimeMillis();
                int  duration = Math.round((endTime - user.getStartTime()) / 1000.0f);

                Object[] info = new Object[Default.PUBLIC_SIZE];
                info[Default.PUB_DATE]      = value.format(new Date(endTime));
                info[Default.PUB_DURATION]  = new Integer(duration);
                info[Default.PUB_ROOM]      = groupName;
                info[Default.PUB_USER_NAME] = user.getName();
                info[Default.PUB_USER_HOST] = user.getConnection().getHostAddress();
                // Log.pub.println(value.formatPublic.format(info));
                Log.pub.log(value.formatPublic.format(info));
            }
        } catch (IllegalArgumentException e) {
            Log.printError(Msg.BAD_PUBLIC_FORMAT, e);
        }
    }

    /**
     * Gets a string representation of this room.
     *
     * @return  a string giving the room name.
     */

    public String toString() {
        return "PublicChat (" + groupName + ")";
    }
}
