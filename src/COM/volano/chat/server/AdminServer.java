/*
 * AdminServer.java - a class for managing administration connections.
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
import  COM.volano.net.*;
import  COM.volano.chat.Build;
import  COM.volano.util.Message;
import  COM.volano.chat.packet.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class manages administrative connections to the VolanoChat server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class AdminServer implements Runnable, Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final int MILLIS_PER_MINUTE = 1000 * 60;       // Milliseconds per minute
    private static final String THREAD_NAME = "AdminServer";

    private Main           server;        // For shutdown request
    private Value          value;         // For server properties
    private StatusReporter reporter;      // For observing status reports

    private ThreadGroup    threadGroup;
    private ServerSocket   serverSocket;
    private Thread         listener;
    private Vector         clientList = new Vector();

    /**
     * Creates a new administrative server.
     *
     * @param value         the server property values.
     * @param roomList      the list of public rooms in the server.
     * @param personalList  the list of personal chat rooms in the server.
     * @param privateList   the list of private chat rooms in the server.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    AdminServer(Main server, Value value, StatusReporter reporter) throws IOException {
        this.server   = server;
        this.value    = value;
        this.reporter = reporter;

        threadGroup  = Thread.currentThread().getThreadGroup();
        serverSocket = new ServerSocket(value.adminPort, value.serverBacklog, value.license.getInetAddress());
        listener     = new Thread(this, THREAD_NAME);
        listener.setPriority(Thread.MAX_PRIORITY);  // So we can always get in
        listener.setDaemon(true);
        listener.start();
    }

    /**
     * Adds the given client connection to the list of connections receiving
     * status reports.
     *
     * @param connection  the connection to add.
     * @param seconds     the requested interval for receiving status reports.
     */

    private synchronized void addClient(Connection connection, int seconds) {
        clientList.addElement(connection);
        // if (clientList.size() == 1) {
        //   reporter = new StatusReporter(seconds, roomList, personalList, privateList);
        //   reporter.addObserver(this);
        //   reporter.start(threadGroup, Thread.MAX_PRIORITY);
        // }
        if (clientList.size() == 1) {
            reporter.addObserver(this);
        }
    }

    /**
     * Removes the given client connection from the list of connections receiving
     * status reports.
     *
     * @param connection  the connection to remove.
     */

    private synchronized void removeClient(Connection connection) {
        connection.deleteObserver(this);
        // if (clientList.removeElement(connection)) {
        //   if ((clientList.size() == 0) && (reporter != null)) {
        //     reporter.stop();
        //     reporter = null;
        //   }
        // }
        if (clientList.removeElement(connection)) {
            if (clientList.size() == 0) {
                reporter.deleteObserver(this);
            }
        }
    }

    /**
     * Gets the list of clients to receive status reports.
     *
     * @return  the list of connections to clients receiving status reports.
     */

    private synchronized Object[] getClients() {
        return clientList.toArray();
    }

    /**
     * Broadcasts a packet to all clients receiving status reports.
     *
     * @param packet  the packet to broadcast.
     */

    private void broadcast(Packet packet) {
        Object[] list = getClients();
        for (int i = 0; i < list.length; i++) {
            send((Connection) list[i], packet);
        }
    }

    /**
     * Closes the connections to all clients receiving status reports.
     */

    private void closeClients() {
        Object[] list = getClients();
        for (int i = 0; i < list.length; i++) {
            Connection connection = (Connection) list[i];
            connection.close(HttpURLConnection.HTTP_UNAVAILABLE);
        }
    }

    /**
     * The body of the administrative manager.  This method accepts and starts up
     * new administrative connections.
     */

    public void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (listener == thisThread) {
                try {
                    Socket socket = serverSocket.accept();

                    // 2.1.8 - Check client IP address on administrative connections.
                    if (value.adminClientAddr != null) {
                        InetAddress clientAddr = socket.getInetAddress();
                        if (! clientAddr.equals(value.adminClientAddr)) {
                            socket.close();
                            throw new IOException("Administrative client from unauthorized host " + clientAddr);
                        }
                    }

                    if (value.serverTimeout > 0) {
                        socket.setSoTimeout(value.serverTimeout * MILLIS_PER_MINUTE);
                    }
                    Connection connection = new Connection(socket);
                    connection.addObserver(this);
                    connection.startSending(Thread.MAX_PRIORITY);         // Give send thread top priority
                    connection.startReceiving(Thread.MAX_PRIORITY);       // Give receive thread top priority
                    Thread.yield();       // Yield to other threads with the same priority (for Solaris)
                } catch (IOException e) {
                    if (listener == thisThread) {
                        Log.printError(Msg.BAD_CONNECTION, e);
                    }
                }
            }
        } catch (ThreadDeath e) {
            throw e;          // Rethrow for cleanup
        } catch (Throwable t) {
            Log.printError(Message.format(Msg.UNEXPECTED, THREAD_NAME), t);
        } finally {
            Log.printError(Message.format(Msg.STOPPING, THREAD_NAME));
            closeClients();
            try {
                listener = null;
                if (serverSocket != null) {
                    serverSocket.close();
                    serverSocket = null;
                }
            } catch (IOException e) {}
        }
    }

    /**
     * Called when an object being observed has changed.  This class observes
     * two types of objects:  the status reporter and client connections.
     *
     * @param observable  the status reporter or the connection to a client.
     * @param object      the status report, if the status reporter has changed;
     *                    otherwise the packet received from the client, or
     *                    <code>null</code> if the connection is closed.
     */

    public void update(Observable observable, Object object) {
        if (Build.UPDATE_TRACE) {
            System.out.println("AdminServer update ...");
        }

        if (observable instanceof StatusReporter) {
            if (object instanceof Report) {
                report((Report) object);
            }
        } else if (observable instanceof Connection) {
            Connection connection = (Connection) observable;
            if (object instanceof Monitor) {
                monitor(connection, (Monitor) object);
            } else if (object instanceof Halt) {
                halt(connection, (Halt) object);
            } else if (object == null) {
                nullObject(connection);
            }
        }
    }

    /**
     * Handles a report indication from the status reporter.
     *
     * @param indication  the status report indication.
     */

    private void report(Report indication) {
        if (Build.UPDATE_TRACE) {
            System.out.println("AdminServer report ...");
        }

        broadcast(indication);
    }

    /**
     * Handles a request to monitor the performance of the server (not to be
     * confused with monitoring a room in the chat server).
     *
     * @param connection  the connection to the client.
     * @param request     the performance monitor request.
     */

    private void monitor(Connection connection, Monitor request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("AdminServer monitor ...");
        }

        if (isValid(request.getPassword())) {
            request.confirm(true);
            addClient(connection, request.getInterval());
        } else {
            request.confirm(false);
        }
        send(connection, request);
    }

    /**
     * Handles a request to shutdown the chat server.
     *
     * @param connection  the connection to the client.
     * @param request     the server shutdown request.
     */

    private void halt(Connection connection, Halt request) {
        if (Build.UPDATE_TRACE) {
            System.out.println("AdminServer halt ...");
        }

        if (isValid(request.getPassword())) {
            server.shutdown();
        } else {
            request.confirm(false);
            send(connection, request);
        }
    }

    /**
     * Handles the <code>null</code> object, indicating the connection was closed.
     *
     * @param connection  the connection to the client.
     */

    private void nullObject(Connection connection) {
        if (Build.UPDATE_TRACE) {
            System.out.println("AdminServer nullObject ...");
        }

        removeClient(connection);
    }

    /**
     * Sends a packet on the specified connection.
     *
     * @param connection  the connection to the client.
     * @param packet      the packet to send.
     */

    private void send(Connection connection, Packet packet) {
        try {
            connection.send(packet);
        } catch (IOException e) {}  // Error means connection is closed -- ignore
    }

    /**
     * Checks whether a string matches the administrative password.
     *
     * @param test  the string to test against the password.
     * @return  <code>true</code> if the string matches the adminstrative
     *          password; otherwise <code>false</code>.
     */

    private boolean isValid(String test) {
        return (value.adminPassword.length() > 0) && test.equals(value.adminPassword);
    }

    /**
     * Finalizes this object by stopping its thread and closing its server socket.
     *
     * @exception java.lang.Throwable  if an error occurs finalizing this object.
     */

    protected void finalize() throws Throwable {
        super.finalize();
        if (listener != null) {
            Thread thread = listener;
            listener = null;
            thread.interrupt();
        }
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }
}
