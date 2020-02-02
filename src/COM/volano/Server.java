/*
 * Server.java - a facade for sending packets over a connection.
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
 *
 * 29 June 1998
 *   - Added stage and topic parameters to the password access method.
 *   - Removed the roomType parameter from the enterRoom method.
 * 16 Oct 1999
 *   - Added the showLink boolean parameter to the IClient.enterRoom callback
 *     method.
 */

package COM.volano;

import  COM.volano.net.*;
import  COM.volano.chat.packet.*;
import  COM.volano.chat.security.AppletSecurity;

import  java.io.*;
import  java.util.*;

/**
 * This class defines a facade object for sending and receive packets over a
 * connection to a VolanoChat server.  An object of this class may be used as if
 * it were a remote proxy for the server.
 * <p>None of the methods in this class are blocking.  The results of confirmed
 * requests are returned by callback methods in the VolanoChat client.
 *
 * @author  John Neffenger
 * @version 2.13
 */

public class Server implements Observer {
    private static final String CLOSED = "connection is closed";

    /**
     * Applet version string for the public room applet.
     */
    public static final String APPLET_PUBLIC   = "2.5";
    /**
     * Applet version string for the personal room applet.
     */
    public static final String APPLET_PERSONAL = "2.5p";
    /**
     * Kick method for removing someone from a chat room.
     */
    public static final int REMOVE = 1;
    /**
     * Kick method for temporarily disconnecting someone from the chat server.
     */
    public static final int KICK = 2;
    /**
     * Kick method for permanently banning someone from the chat server.
     */
    public static final int BAN = 3;

    private IClient    client;            // Client callback object
    private Connection connection;        // Connection to the server
    private boolean    open;              // Connection is open
    private Access     accessConfirm;     // Saved access confirmation

    /**
     * Creates a new Server object given a host name and port number.
     *
     * @param client    the VolanoChat client.
     * @param hostname  the name of the host running the server.
     * @param port      the port number on which the server accepts connections.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public Server(IClient client, String hostname, int port) throws IOException {
        Connection.setPacketFactory(new ClientPacketFactory());
        this.client = client;
        this.connection = new Connection(hostname, port);
        connection.addObserver(this);
        connection.startSending(Thread.NORM_PRIORITY);
        connection.startReceiving(Thread.NORM_PRIORITY);
        this.open = true;
    }

    /**
     * Checks that the connection is open.
     */

    private void check() throws IOException {
        if (! open) {
            throw new IOException(CLOSED);
        }
    }

    /**
     * Checks whether this server object is open.
     *
     * @return <code>true</code> if this server object is open; otherwise
     *         <code>false</code>.
     */

    public boolean isOpen() {
        return open;
    }

    /**
     * Closes this server object, releasing its underlying resources.
     */

    public void close() throws IOException {
        connection.deleteObserver(this);
        connection.close();
        open = false;
    }

    /**
     * Requests access to the server by sending information about the client's
     * version, referring Web page, origin, Java environment, and operating system
     * platform.  Also included is the public room name or personal room document
     * base for dynamically creating a room.
     *
     * @param roomName          the name of the room identified on the referring
     *                          Web page by the <i>group</i> applet parameter, or
     *                          an empty string if the parameter is undefined.
     *                          If the client is the MyVolanoChat applet
     *                          requesting access to a personal room, the applet's
     *                          document base is passed as the default room.
     * @param appletVersion     <code>Server.APPLET_PUBLIC</code> for the public
     *                          room applet, or
     *                          <code>Server.APPLET_PERSONAL</code> for the
     *                          personal room applet.
     * @param documentBase      the client applet document base.
     * @param codeBase          the client applet code base.
     * @param javaVendor        the <code>java.vendor</code> property.
     * @param javaVendorUrl     the <code>java.vendor.url</code> property.
     * @param javaVersion       the <code>java.version</code> property.
     * @param javaClassVersion  the <code>java.class.version</code> property.
     * @param osName            the <code>os.name</code> property.
     * @param osVersion         the <code>os.version</code> property.
     * @param osArch            the <code>os.arch</code> property.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void access(String roomName, String appletVersion, String documentBase, String codeBase,
                       String javaVendor, String javaVendorUrl, String javaVersion, String javaClassVersion,
                       String osName, String osVersion, String osArch) throws IOException {
        check();
        connection.send(new Access(roomName, encodeAppletVersion(appletVersion), documentBase, codeBase,
                                   javaVendor, javaVendorUrl, javaVersion, javaClassVersion,
                                   osName, osVersion, osArch));
    }

    /**
     * Requests password protected access to the server.  The parameters are the
     * same as the other access method, with the following additions:
     *
     * @param isMember        <code>true</code> if member access is enabled for
     *                        the client; otherwise <code>false</code>.
     * @param isMonitor       <code>true</code> if monitor access is enabled for
     *                        the client; otherwise <code>false</code>.
     * @param isAdministrator <code>true</code> if administrator access is enabled
     *                        for the client; otherwise <code>false</code>.
     * @param memberName      the member name or an empty string for no name.
     * @param memberPassword  the member password or an empty string for no
     *                        password.
     * @param password        the monitor or administrative password, or an empty
     *                        string for no password.
     * @param stage           <code>true</code> if this client entered from the
     *                        event stage; otherwise <code>false</code>.
     * @param topic           the event topic description.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void access(String roomName, String appletVersion, String documentBase, String codeBase,
                       String javaVendor, String javaVendorUrl, String javaVersion, String javaClassVersion,
                       String osName, String osVersion, String osArch,
                       boolean isMember, boolean isMonitor, boolean isAdministrator,
                       String memberName, String memberPassword, String password,
                       boolean stage, String topic) throws IOException {
        check();
        connection.send(new PasswordAccess(roomName, encodeAppletVersion(appletVersion), documentBase, codeBase,
                                           javaVendor, javaVendorUrl, javaVersion, javaClassVersion,
                                           osName, osVersion, osArch,
                                           isMember, isMonitor, isAdministrator,
                                           memberName, memberPassword, password,
                                           stage, topic));
    }

    /**
     * Requests the list of rooms in the server.
     *
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void roomList() throws IOException {
        check();
        connection.send(new RoomList());
    }

    /**
     * Requests the list of users in a room.
     *
     * @param roomName  the name of the room.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void userList(String roomName) throws IOException {
        check();
        connection.send(new UserList(roomName));
    }

    /**
     * Requests to create a list of dynamic public rooms.
     *
     * @param roomNames  the list of room names, limited to a maximum of 256 names
     *                   with a maximum length of 128 characters each.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void createRooms(String[] roomNames) throws IOException {
        check();
        connection.send(new CreateRooms(roomNames));
    }

    /**
     * Requests to enter a public or personal chat room.
     *
     * @param roomName  the name of the room to enter.
     * @param userName  the name of the user entering the room.
     * @param profile   the profile of the user entering the room.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void enterRoom(String roomName, String userName, String profile) throws IOException {
        check();
        connection.send(new EnterRoom(roomName, userName, profile));
    }

    /**
     * Exits a public or personal chat room.
     *
     * @param roomName  the name of the room to exit.
     * @param userName  the name of the user exiting the room.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void exitRoom(String roomName, String userName) throws IOException {
        check();
        connection.send(new ExitRoom(roomName, userName));
    }

    /**
     * Requests a private chat session.
     *
     * @param roomName  the public room from which this private chat session is
     *                  requested.
     * @param fromName  the name of the user sending the private chat request.
     * @param toName    the name of the user to receive the private chat
     *                  indication.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void enterPrivate(String roomName, String fromName, String toName) throws IOException {
        check();
        connection.send(new EnterPrivate(roomName, fromName, toName));
    }

    /**
     * Exits a private chat session.
     *
     * @param roomId    the identifier of the private room to exit.
     * @param userName  the name of the user exiting the room.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void exitPrivate(int roomId, String userName) throws IOException {
        check();
        connection.send(new ExitPrivate(roomId, userName));
    }

    /**
     * Sends chat text to a public or personal room.
     *
     * @param roomName  the name of the room to receive the chat text.
     * @param userName  the name of the user sending the chat text.
     * @param text      the chat text.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void chat(String roomName, String userName, String text) throws IOException {
        check();
        connection.send(new Chat(roomName, userName, text));
    }

    /**
     * Sends chat text to a private room.
     *
     * @param roomId    the identifier of the private chat session.
     * @param userName  the name of the user sending the private chat text.
     * @param text      the private chat text.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void chat(int roomId, String userName, String text) throws IOException {
        check();
        connection.send(new Chat(roomId, userName, text));
    }

    /**
     * Broadcasts chat text to all public and personal rooms.  The client must
     * have accessed the server with a valid administrator password.
     *
     * @param text  the broadcast chat text.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void chat(String text) throws IOException {
        check();
        connection.send(new Chat(text));
    }

    /**
     * Sends <i>whisper</i> text to a specific user in a specific chat room.
     * This text is not seen by others in the chat room.
     *
     * @param roomName  the name of the room.
     * @param fromName  the name of the user sending the whisper text.
     * @param toName    the name of the user to receive the whisper text.
     * @param text      the whisper text.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void whisper(String roomName, String fromName, String toName, String text) throws IOException {
        check();
        connection.send(new Whisper(roomName, fromName, toName, text));
    }

    /**
     * Sends an audio alert to a user in a chat room.
     *
     * @param roomName  the name of the room.
     * @param fromName  the name of the user sending the audio alert.
     * @param toName    the name of the user to receive the audio alert.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void beep(String roomName, String fromName, String toName) throws IOException {
        check();
        connection.send(new Beep(roomName, fromName, toName));
    }

    /**
     * Removes a user from a chat room, or kicks or bans a user from the server.
     * The client must have accessed the server with a valid monitor or
     * administrator password and must have already entered the room containing
     * the user to be kicked.
     *
     * @param roomName  the room name of the user.
     * @param fromName  the name of the monitor or administrator sending the kick.
     * @param toName    the name of the user to be removed, kicked or banned.
     * @param method    either <code>Server.REMOVE</CODE> to remove the user from
     *                  the room, <code>Server.KICK</code> to temporarily
     *                  disconnect the user from the server, or
     *                  <code>Server.BAN</code> to disconnect and permanently ban
     *                  the user from the server until the next restart.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    public void kick(String roomName, String fromName, String toName, int method) throws IOException {
        check();
        connection.send(new Kick(roomName, fromName, toName, "", method));
    }

    /**
     * Called by the underlying connection when a packet is received from the
     * server.  This method is not meant to be called by VolanoChat clients.
     *
     * @param observable  the underlying connection object.
     * @param object      the packet received from the server.
     */

    public void update(Observable observable, Object object) {
        Connection connection = (Connection) observable;
        // PasswordAccess is an Access, so we don't need to check it first.
        if (object instanceof Access) {
            accessConfirm(connection, (Access) object);
        } else if (object instanceof Authenticate) {
            authenticateConfirm(connection, (Authenticate) object);
        } else if (object instanceof RoomList) {
            RoomList confirm = (RoomList) object;
            client.roomList(confirm.getRooms());
        } else if (object instanceof UserList) {
            UserList confirm = (UserList) object;
            client.userList(decodeUserListResult(confirm.getResult()), confirm.getDocumentBase(), confirm.getUsers());
        } else if (object instanceof EnterRoom) {
            EnterRoom packet = (EnterRoom) object;
            switch (packet.getType()) {
            case Packet.CONFIRM:
                client.enterRoom(decodeEnterRoomResult(packet.getResult()),
                                 packet.getRoomName(), decodeRoomType(packet.getRoomType()),
                                 packet.getCount(), packet.getUsers());
                break;
            case Packet.INDICATION:
                client.enterRoom(packet.getRoomName(), packet.getUserName(), packet.getProfile(),
                                 packet.getAddress(),
                                 Boolean.valueOf(packet.isMember()).booleanValue(),
                                 Boolean.valueOf(packet.showLink()).booleanValue());
                break;
            }
        } else if (object instanceof ExitRoom) {
            ExitRoom indication = (ExitRoom) object;
            client.exitRoom(indication.getRoomName(), indication.getUserName());
        } else if (object instanceof EnterPrivate) {
            EnterPrivate packet = (EnterPrivate) object;
            if (packet.getType() == Packet.INDICATION) {
                client.enterPrivate(packet.getRoomId(), packet.getFromName(), packet.getFromProfile(), packet.getFromHost());
            } else {
                client.enterPrivate(packet.getRoomId(), packet.getToName(), packet.getToProfile(), packet.getToHost());
            }
        } else if (object instanceof ExitPrivate) {
            ExitPrivate indication = (ExitPrivate) object;
            client.exitPrivate(indication.getRoomId(), indication.getFromName());
        } else if (object instanceof Chat) {
            Chat packet = (Chat) object;
            switch (packet.getType()) {
            case Packet.INDICATION:
                int    roomId   = packet.getRoomId();
                String roomName = packet.getRoomName();
                String userName = packet.getUserName();
                String text     = packet.getText();
                if (userName.length() == 0) {
                    client.chat(text);
                } else if (roomId == 0) {
                    client.chat(roomName, userName, text);
                } else {
                    client.chat(roomId, userName, text);
                }
                break;
            case Packet.CONFIRM:
                client.chatConfirm(packet.getRoomName());
                break;
            }
        } else if (object instanceof Whisper) {
            Whisper indication = (Whisper) object;
            client.whisper(indication.getRoomName(), indication.getFromName(), indication.getText());
        } else if (object instanceof Beep) {
            Beep indication = (Beep) object;
            client.beep(indication.getRoomName(), indication.getFromName());
        } else if (object instanceof Ping) {
            Ping packet = (Ping) object;
            packet.response();
            try {
                connection.send(packet);
            } catch (IOException e) {}
        } else if (object == null) {
            connection.deleteObserver(this);
            open = false;
            client.close();
        }
    }

    /**
     * Handles an access confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the access confirmation.
     */

    private void accessConfirm(Connection connection, Access confirm) {
        byte[] bytes = confirm.getBytes();
        if (bytes.length == 0) {      // No authenticate challenge from server
            // PasswordAccess is an Access, so check its instance type first.
            if (confirm instanceof PasswordAccess) {
                client.access(decodeAccessResult(confirm.getResult()), confirm.getRooms(), ((PasswordAccess) confirm).getProfile());
            } else {
                client.access(decodeAccessResult(confirm.getResult()), confirm.getRooms(), "");
            }
        } else {
            accessConfirm = confirm;    // Save for later in authenticateConfirm
            byte[] signature = sign(bytes);
            try {
                check();
                connection.send(new Authenticate(signature));
            } catch (IOException e) {}
            if (signature.length == 0) {     // Missing java.security package
                client.access(IClient.ACCESS_BAD_JAVA_VERSION, confirm.getRooms(), "");
            }
        }
    }

    /**
     * Handles an authenticate confirmation.
     *
     * @param connection  the connection to the server.
     * @param confirm     the authenticate confirmation.
     */

    private void authenticateConfirm(Connection connection, Authenticate confirm) {
        if (confirm.getResult() == Authenticate.OKAY) {
            if (accessConfirm instanceof PasswordAccess) {
                client.access(decodeAccessResult(accessConfirm.getResult()), accessConfirm.getRooms(), ((PasswordAccess) accessConfirm).getProfile());
            } else {
                client.access(decodeAccessResult(accessConfirm.getResult()), accessConfirm.getRooms(), "");
            }
        }
    }

    /**
     * Signs the data with the applet's private key.
     *
     * @param data  the data to sign.
     * @return  the digital signature of the data, or a zero-length array if no
     *          signature could be created.
     */

    protected byte[] sign(byte[] data) {
        AppletSecurity security = AppletSecurity.getInstance();
        security.initialize();
        return security.sign(data);
    }

    /**
     * Encodes the applet version string for sending to the server.
     *
     * @param version  the applet version string as defined by this class.
     * @return the applet version string as defined by Access.
     */

    private String encodeAppletVersion(String version) {
        if (version.equals(APPLET_PUBLIC)) {
            return Access.PUBLIC_VERSION;
        }
        if (version.equals(APPLET_PERSONAL)) {
            return Access.PERSONAL_VERSION;
        }
        throw new IllegalArgumentException("unknown applet version " + version);
    }

    /**
     * Decodes the access result code as received from the server.
     *
     * @param result  the access result code as defined by Access.
     * @return the access result code as defined by IClient.
     */

    private int decodeAccessResult(int result) {
        switch (result) {
        case Access.OKAY:
            return IClient.ACCESS_OKAY;
        case Access.HOST_DENIED:
            return IClient.ACCESS_HOST_DENIED;
        case Access.DOCUMENT_DENIED:
            return IClient.ACCESS_DOCUMENT_DENIED;
        case Access.VERSION_DENIED:
            return IClient.ACCESS_VERSION_DENIED;
        case Access.BAD_PASSWORD:
            return IClient.ACCESS_BAD_PASSWORD;
        case Access.BAD_JAVA_VERSION:
            return IClient.ACCESS_BAD_JAVA_VERSION;
        case Access.HOST_DUPLICATE:
            return IClient.ACCESS_HOST_DUPLICATE;
        default:
            throw new IllegalArgumentException("unknown access result " + result);
        }
    }

    /**
     * Decodes the enter room result code as received from the server.
     *
     * @param result  the enter room result code as defined by EnterRoom.
     * @return the enter room result code as defined by IClient.
     */

    private int decodeEnterRoomResult(int result) {
        switch (result) {
        case EnterRoom.OKAY:
            return IClient.ROOM_OKAY;
        case EnterRoom.ROOM_FULL:
            return IClient.ROOM_FULL;
        case EnterRoom.NAME_TAKEN:
            return IClient.ROOM_NAME_TAKEN;
        case EnterRoom.MEMBER_TAKEN:
            return IClient.ROOM_MEMBER_TAKEN;
        case EnterRoom.NO_SUCH_ROOM:
            return IClient.ROOM_NOT_FOUND;
        default:
            throw new IllegalArgumentException("unknown enter room result " + result);
        }
    }

    /**
     * Decodes the room type received in an enter room confirmation.
     *
     * @param type  the room type as defined by RoomPacket.
     * @return  the room type as defined by IClient.
     */

    private int decodeRoomType(int type) {
        switch (type) {
        case RoomPacket.UNKNOWN:
            return IClient.ROOM_UNKNOWN;
        case RoomPacket.NORMAL:
            return IClient.ROOM_NORMAL;
        case RoomPacket.EVENT:
            return IClient.ROOM_EVENT;
        default:
            throw new IllegalArgumentException("unknown room type " + type);
        }
    }

    /**
     * Decodes the user list result code as received from the server.
     *
     * @param result  the user list result code as defined by UserList.
     * @return the user list result code as defined by IClient.
     */

    private int decodeUserListResult(int result) {
        switch (result) {
        case UserList.OKAY:
            return IClient.ROOM_OKAY;
        case UserList.NO_SUCH_ROOM:
            return IClient.ROOM_NOT_FOUND;
        default:
            throw new IllegalArgumentException("unknown user list result " + result);
        }
    }

    /**
     * Finalizes this object by closing the connection with the server.
     *
     * @exception java.lang.Throwable  when an error occurs finalizing this
     *                                 object.
     */

    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
