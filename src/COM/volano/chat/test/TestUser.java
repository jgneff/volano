/*
 * TestUser.java - a class for respresenting a chat user in VolanoTest.
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

package COM.volano.chat.test;
import  COM.volano.chat.Build;
import  COM.volano.chat.packet.*;
import  COM.volano.net.*;
import  java.io.*;
import  java.util.*;

class TestUser implements Runnable, Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final boolean TRACE = false;

    private static final int    LEVEL_ACCESS        = 1;
    private static final int    LEVEL_ROOMS         = 2;
    private static final int    LEVEL_PUBLIC        = 3;
    private static final int    LEVEL_PRIVATE       = 4;

    private static final int    CONNECT_ERROR_PAUSE = 1000;
    private static final int    MAX_PUBLIC_CHAT     =    5;
    private static final int    MAX_PUBLIC_LOOP     =    2;
    private static final int    MAX_PRIVATE_CHAT    =   10;

    private static final String ROOM_PREFIX         = "Room";

    private static boolean startDone;
    private static Object  startGate = new Object();

    private String     host;
    private int        port;
    private int        roomNumber;
    private int        userNumber;
    private int        roomSize;
    private int        level;
    private int        loops;
    private boolean    reconnect;

    private String     roomName;
    private String     userName;
    private String     profile;
    private Thread     thread;

    private boolean    initialRoomList;   // First room list request
    private boolean    userDone;
    private Connection connection;
    private int        chatSent;          // Total chat messages sent
    private int        chatReceived;      // Total chat messages received
    private int        loopCount;         // Count of iteration loops
    private int        publicLoopCount;   // Count of public chat loops for this iteration
    private int        privateChatCount;  // Count of private chat messages for this iteration

    private static void waitStart() throws InterruptedException {
        synchronized (startGate) {
            while (! startDone) {
                startGate.wait();
            }
        }
    }

    static void notifyStart() {
        synchronized (startGate) {
            startDone = true;
            startGate.notifyAll();
        }
    }

    TestUser(String host, int port, String prefix, int roomNumber, int userNumber, int roomSize, int level, int loops, boolean reconnect) {
        this.host       = host;
        this.port       = port;
        this.roomNumber = roomNumber;
        this.userNumber = userNumber;
        this.roomSize   = roomSize;
        this.level      = level;
        this.loops      = loops;
        this.reconnect  = reconnect;

        this.roomName   = ROOM_PREFIX + roomNumber;
        this.userName   = prefix + userNumber;
        this.profile    = "This is the profile for " + userName + " in " + roomName + ".";
        this.thread     = new Thread(this);
        thread.start();
    }

    synchronized void waitUser() throws InterruptedException {
        while (! userDone) {
            wait();
        }
        userDone = false;
    }

    private synchronized void notifyUser() {
        userDone = true;
        notify();
    }

    private synchronized int nextChatSent() {
        return ++chatSent;
    }

    private synchronized int getChatSent() {
        return chatSent;
    }

    private void connect() throws IOException, InterruptedException {
        boolean connected = false;
        while (! connected) {
            try {
                connection = new Connection(host, port);
                connection.getSocket().setTcpNoDelay(true);     // Disable Nagle algorithm
                connection.addObserver(this);
                connection.startReceiving(Thread.NORM_PRIORITY);
                connection.startSending(Thread.NORM_PRIORITY);
                connected = true;
            } catch (IOException e) {
                Thread.sleep(CONNECT_ERROR_PAUSE);
            }
        }
    }

    private void requestAccess() throws IOException {
        String documentBase     = "http://www.volano.com/chat.html";
        String codeBase         = "http://chat.volano.net/vcclient";
        String javaVendor       = System.getProperty("java.vendor",        "");
        String javaVendorUrl    = System.getProperty("java.vendor.url",    "");
        String javaVersion      = System.getProperty("java.version",       "");
        String javaClassVersion = System.getProperty("java.class.version", "");
        String osName           = System.getProperty("os.name",            "");
        String osVersion        = System.getProperty("os.version",         "");
        String osArch           = System.getProperty("os.arch",            "");
        connection.send(new Access(roomName, Access.PUBLIC_VERSION, documentBase, codeBase,
                                   javaVendor, javaVendorUrl, javaVersion, javaClassVersion,
                                   osName, osVersion, osArch));
    }

    public void run() {
        try {
            waitStart();              // Wait for test driver to kick off the test
            connect();                // Make initial connection
            requestAccess();          // Send Access request to start it all
        } catch (IOException e) {
            System.err.println("Error for " + userName + " in " + roomName + " (" + e + ").");
        } catch (InterruptedException e) {
            System.err.println(userName + " in " + roomName + " was interrupted (" + e + ").");
        } catch (Throwable t) {
            System.err.println("Unexpected error for " + userName + " in " + roomName + " (" + t + ").");
        }
    }

    public void update(Observable observable, Object object) {
        try {
            Connection connection = (Connection) observable;
            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                switch (packet.getType()) {
                case Packet.INDICATION:
                    if (object instanceof Chat) {
                        chatIndication(connection, (Chat) object);
                    } else if (object instanceof Beep) {
                        beepIndication(connection, (Beep) object);
                    }
                    break;

                case Packet.CONFIRM:
                    if (object instanceof Access) {
                        accessConfirm(connection, (Access) object);
                    } else if (object instanceof RoomList) {
                        roomListConfirm(connection, (RoomList) object);
                    } else if (object instanceof UserList) {
                        userListConfirm(connection, (UserList) object);
                    } else if (object instanceof EnterRoom) {
                        enterRoomConfirm(connection, (EnterRoom) object);
                    } else if (object instanceof EnterPrivate) {
                        enterPrivateConfirm(connection, (EnterPrivate) object);
                    }
                    break;
                }
            } else if (object instanceof StreamableError) {
                streamableError(connection, (StreamableError) object);
            } else if (object == null) {
                nullObject(connection);
            }
        } catch (IOException e) {
            System.err.println(userName + " in " + roomName + " failed sending message (" + e + ").");
        } catch (InterruptedException e) {
            System.err.println(userName + " in " + roomName + " interrupted (" + e + ").");
        }
    }

    private void accessConfirm(Connection connection, Access confirm) throws IOException {
        if (TRACE) {
            System.out.println("Access confirm for " + userName + " in " + roomName + " ...");
        }

        int result = confirm.getResult();
        if (result == Access.OKAY) {
            if (level == LEVEL_ACCESS) {
                connection.close();
            } else {
                sendRoomList(true);
            }
        } else {
            System.err.println("Access denied for " + userName + " in " + roomName + " (" + result + ").");
        }
    }

    private void roomListConfirm(Connection connection, RoomList confirm) throws IOException {
        if (TRACE) {
            System.out.println("RoomList confirm for " + userName + " in " + roomName + " ...");
        }

        String[] roomList = confirm.getRooms();
        if (roomList.length > 0) {
            if (initialRoomList) {
                connection.send(new UserList(roomList[0]));
            } else {
                connection.close();
            }
        } else {
            System.err.println("No rooms to view for " + userName + " in " + roomName + ".");
        }
    }

    private void userListConfirm(Connection connection, UserList confirm) throws IOException {
        if (TRACE) {
            System.out.println("UserList confirm for " + userName + " in " + roomName + " ...");
        }

        if (level == LEVEL_ROOMS) {
            if (reconnect) {
                sendRoomList(false);
            } else if (keepLooping()) {
                sendRoomList(true);
            } else {
                sendRoomList(false);
            }
        } else {
            connection.send(new EnterRoom(roomName, userName, profile));
        }
    }

    private void enterRoomConfirm(Connection connection, EnterRoom confirm) throws IOException, InterruptedException {
        if (TRACE) {
            System.out.println("EnterRoom confirm for " + userName + " in " + roomName + " ...");
        }

        int result = confirm.getResult();
        if (result == EnterRoom.OKAY) {
            sendChat(MAX_PUBLIC_CHAT);
            connection.send(new Beep(roomName, userName, userName));
        } else {
            System.err.println(userName + " is unable to enter " + roomName + " (" + result + ").");
        }
    }

    private void beepIndication(Connection connection, Beep indication) throws IOException {
        if (TRACE) {
            System.out.println("Beep indication for " + userName + " in " + roomName + " ...");
        }

        if (++publicLoopCount < MAX_PUBLIC_LOOP) {
            sendChat(MAX_PUBLIC_CHAT);
            connection.send(new Beep(roomName, userName, userName));
        } else {
            publicLoopCount = 0;
            if (level == LEVEL_PUBLIC) {
                connection.send(new ExitRoom(roomName, userName));
                if (reconnect) {
                    sendRoomList(false);
                } else if (keepLooping()) {
                    sendRoomList(true);
                } else {
                    sendRoomList(false);
                }
            } else {
                connection.send(new EnterPrivate(roomName, userName, userName));
            }
        }
    }

    private void enterPrivateConfirm(Connection connection, EnterPrivate confirm) throws IOException {
        if (TRACE) {
            System.out.println("EnterPrivate confirm for " + userName + " in " + roomName + " ...");
        }

        connection.send(new Chat(confirm.getRoomId(), userName, getText(nextChatSent())));
    }

    private void chatIndication(Connection connection, Chat indication) throws IOException {
        if (TRACE) {
            System.out.println("Chat indication for " + userName + " in " + roomName + " ...");
        }

        if (indication.getRoomId() != 0) {
            if (++privateChatCount < MAX_PRIVATE_CHAT) {
                connection.send(new Chat(indication.getRoomId(), userName, getText(nextChatSent())));
            } else {
                privateChatCount = 0;
                connection.send(new ExitPrivate(indication.getRoomId(), userName));
                connection.send(new ExitRoom(roomName, userName));
                if (reconnect) {
                    sendRoomList(false);
                } else if (keepLooping()) {
                    sendRoomList(true);
                } else {
                    sendRoomList(false);
                }
            }
        }
    }

    private void streamableError(Connection connection, StreamableError error) {
        System.err.println(userName + " in " + roomName + " received error from server (" + error.getText() + ").");
        notifyUser();
    }

    private void nullObject(Connection connection) throws IOException, InterruptedException {
        if (TRACE) {
            System.out.println("Null object for " + userName + " in " + roomName + " ...");
        }

        if (keepLooping()) {
            connect();
            requestAccess();
        } else {
            notifyUser();    // Notify test driver we're done
        }
    }

    private String getText(long number) {
        return "This is message " + number + " from " + userName + " in " + roomName + ".";
    }

    private void sendChat(int count) throws IOException {
        for (int i = 0; i < count; i++) {
            connection.send(new Chat(roomName, userName, getText(nextChatSent())));
        }
    }

    private void sendRoomList(boolean initial) throws IOException {
        initialRoomList = initial;
        connection.send(new RoomList());
    }

    private boolean keepLooping() {
        return loops == 0 || ++loopCount < loops;
    }
}
