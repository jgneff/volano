/*
 * MarkUser.java - a class for respresenting a chat user in VolanoMark.
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

class MarkUser implements Observer, Runnable {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String ROOM_PREFIX = "Room";
    private static final String USER_PREFIX = "Mark";
    private static final String TEXT = "123456789 123456789 123456789 123456789 123456789 123456789 ";

    private static boolean startDone;
    private static Object  startGate = new Object();
    private static boolean endDone;
    private static Object  endGate = new Object();
    private static boolean endOkay = true;

    private static Object  sentLock     = new Object();
    private static long    sent         = 0;
    private static Object  receivedLock = new Object();
    private static long    received     = 0;

    private Connection connection;
    private int        roomNumber;
    private int        userNumber;
    private int        roomSize;
    private int        count;
    private int        pause;

    private String     roomName;
    private String     userName;
    private String     profile;
    private int        myTurn;

    private boolean    testDone;
    private boolean    userDone;
    private int        enterCount;
    private int        exitCount;
    private long       chatSent;
    private long       chatReceived;

    // Start synchronization.

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

    // End synchronization.

    static boolean waitEnd() throws InterruptedException {
        synchronized (endGate) {
            while (! endDone) {
                endGate.wait();
            }
        }
        return endOkay;
    }

    private static void notifyEnd(boolean okay) {
        synchronized (endGate) {
            endOkay = endOkay && okay;
            if (Connection.getCount() == 0) {
                endDone = true;
                endGate.notify();
            }
        }
    }

    // Message traffic counters.

    static long getSent() {
        return sent;
    }

    static long getReceived() {
        return received;
    }

    private static void addSent(long value) {
        synchronized (sentLock) {
            sent += value;
        }
    }

    private static void addReceived(long value) {
        synchronized (receivedLock) {
            received += value;
        }
    }

    MarkUser(Connection connection, int roomNumber, int userNumber, int roomSize, int count, int pause) throws IOException {
        this.connection = connection;
        this.roomNumber = roomNumber;
        this.userNumber = userNumber;
        this.roomSize   = roomSize;
        this.count      = count;
        this.pause      = pause;
        this.roomName   = ROOM_PREFIX + roomNumber;
        this.userName   = USER_PREFIX + userNumber;
        this.profile    = "This is the profile for " + userName + " in " + roomName + ".";
        this.myTurn     = userNumber - 1;

        connection.addObserver(this);
        connection.startReceiving(Thread.NORM_PRIORITY);
        connection.startSending(Thread.NORM_PRIORITY);
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

    String getRoomName() {
        return roomName;
    }

    String getUserName() {
        return userName;
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

    // These messages are 38 to 44 characters in length.
    private String getText(long number) {
        return "This is message " + number + " from " + userName + " in " + roomName + ".";
    }

    private void sendChat() throws IOException {
        // connection.send(new Chat(roomName, userName, getText(++chatSent)));
        ++chatSent;
        connection.send(new Chat(roomName, userName, TEXT));
    }

    public void run() {
        long msPause = pause * 1000;        // Get pause in milliseconds
        try {
            waitStart();                      // Wait for test driver to kick off the chatting
            Thread.sleep((long) ((double) msPause * Math.random()));
            while (true) {
                sendChat();
                Thread.sleep(msPause);
            }
        } catch (IOException e) {
            System.err.println("I/O error for " + userName + " in " + roomName + " (" + e + ").");
        } catch (InterruptedException e) {
            System.err.println(userName + " in " + roomName + " was interrupted (" + e + ").");
        } catch (Throwable t) {
            System.err.println("Unexpected error for " + userName + " in " + roomName + " (" + t + ").");
        } finally {
            testDone = true;
            connection.close();
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
                    } else if (object instanceof EnterRoom) {
                        enterRoomIndication(connection, (EnterRoom) object);
                    } else if (object instanceof ExitRoom) {
                        exitRoomIndication(connection, (ExitRoom) object);
                    }
                    break;

                case Packet.CONFIRM:
                    if (object instanceof Access) {
                        accessConfirm(connection, (Access) object);
                    } else if (object instanceof EnterRoom) {
                        enterRoomConfirm(connection, (EnterRoom) object);
                    } else if (object instanceof RoomList) {
                        roomListConfirm(connection, (RoomList) object);
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

    private void chatIndication(Connection connection, Chat indication) throws IOException {
        int roomId = indication.getRoomId();
        if (roomId == 0) {
            chatReceived++;
            // If we're taking turns and it's my turn
            if (pause == 0 && ((chatSent + chatReceived) % roomSize) == myTurn) {
                if (count == 0 || chatSent < count) {
                    sendChat();
                } else if (userNumber == 1) {
                    connection.send(new ExitRoom(roomName, userName));
                    connection.send(new RoomList());
                }
            }
        } else {
            connection.send(new Chat(roomId, userName, indication.getText()));
        }
    }

    private void enterRoomIndication(Connection connection, EnterRoom indication) throws IOException, InterruptedException {
        if (indication.getUserName().startsWith(USER_PREFIX)) {     // If a test user just entered
            enterCount++;                                             // Keep track of test users in room
            // If we're taking turns, we're the first, and everybody is in the room
            if (pause == 0 && userNumber == 1 && enterCount == roomSize) {
                waitStart();    // Wait for test driver to kick off the chatting
                sendChat();     // Kick off the first chat message
            }
        }
    }

    private void accessConfirm(Connection connection, Access confirm) throws IOException {
        int result = confirm.getResult();
        if (result == Access.OKAY) {
            connection.send(new EnterRoom(roomName, userName, profile));
        } else {
            System.err.println("Access denied for " + userName + " in " + roomName + " (" + result + ").");
            notifyUser();     // Notify test driver that I'm quitting
        }
    }

    private void enterRoomConfirm(Connection connection, EnterRoom confirm) throws IOException {
        int result = confirm.getResult();
        if (result == EnterRoom.OKAY) {
            enterCount++;                     // Add myself to test user count
            if (pause > 0) {                  // If we're supposed to chat and pause
                new Thread(this).start();    // Start up chatting thread
            }
        } else {
            System.err.println(userName + " is unable to enter " + roomName + " (" + result + ").");
        }
        notifyUser();       // Notify test driver that I'm in
    }

    private void exitRoomIndication(Connection connection, ExitRoom indication) throws IOException {
        exitCount++;
        if (exitCount == myTurn) {
            connection.send(new ExitRoom(roomName, userName));
            connection.send(new RoomList());
        }
    }

    private void roomListConfirm(Connection connection, RoomList confirm) throws IOException {
        testDone = true;
        connection.close();
    }

    private void streamableError(Connection connection, StreamableError error) {
        System.err.println(userName + " in " + roomName + " received error from server (" + error.getText() + ").");
        notifyUser();
    }

    private void nullObject(Connection connection) {
        if (! testDone) {
            System.err.println(userName + " in " + roomName + " disconnected from server.");
        }
        connection.deleteObserver(this);
        addSent(chatSent);
        addReceived(chatReceived);
        notifyEnd(testDone);
    }
}
