/*
 * Status.java - a class to monitor the status of the chat server.
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

package COM.volano.chat.admin;
import  COM.volano.chat.Build;
import  COM.volano.chat.packet.*;
import  COM.volano.chat.server.Default;
import  COM.volano.chat.server.Key;
import  COM.volano.net.Connection;
import  java.io.*;
import  java.text.*;
import  java.util.*;

/**
 * This class is a Java console application which receives and prints out
 * status information from the chat server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Status implements Observer {
    public  static final String COPYRIGHT  = Build.COPYRIGHT;
    private static final String PROPERTIES = "conf/properties.txt";
    private static final String LOCALHOST  = "localhost";

    private Connection    connection;
    private MessageFormat statusFormatter;
    private DateFormat    dateFormatter;
    private MessageFormat heapFormatter;
    private MessageFormat resFormatter;

    private boolean       gotFirst;
    private long          oldTime;
    private long          oldReceivedCount;
    private long          oldSentCount;

    /**
     * Creates a <code>Status</code> object which connects to the chat server
     * administrative port and sends a request for status reports.
     *
     * @param  host          the name of the host running the chat server.
     * @param  port          the administrative port number of the chat server.
     * @param  interval      the interval in seconds at which to receive status
     *                       reports.
     * @param  statusFormat  the message pattern for the output status record.
     * @param  heapFormat    the format for the heap memory information within the
     *                       output status record.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    private Status(String host, int port, String password, int interval,
                   String statusFormat, String dateFormat, String heapFormat, String resFormat) throws IOException {
        Connection.setPacketFactory(new ChatPacketFactory());
        this.connection = new Connection(host, port);
        connection.addObserver(this);
        connection.startSending(Thread.NORM_PRIORITY);
        connection.startReceiving(Thread.NORM_PRIORITY);
        connection.send(new Monitor(password, interval));
        statusFormatter = new MessageFormat(statusFormat);
        dateFormatter   = new SimpleDateFormat(dateFormat);
        heapFormatter   = new MessageFormat(heapFormat);
        resFormatter    = new MessageFormat(resFormat);
        dateFormatter.setTimeZone(TimeZone.getDefault());
    }

    /**
     * Called whenever the observed connection has changed.  The observed
     * connection changes when it receives a packet from the server.
     *
     * @param observable  the observed connection object.
     * @param object      the packet received on the connection.
     */

    public void update(Observable observable, Object object) {
        Connection connection = (Connection) observable;
        try {
            if (object instanceof Report) {
                report((Report) object);
            } else if (object instanceof Ping) {
                ping(connection, (Ping) object);
            } else if (object instanceof Monitor) {
                monitor(connection, (Monitor) object);
            } else if (object == null) {
                nullObject(connection);
            }
        } catch (IOException e) {
            System.err.println("Error on socket connection.");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Formats the information contained in the status report packet and prints
     * it to standard output.
     *
     * @param indication  the status report indication packet.
     */

    private void report(Report indication) {
        long time          = indication.getTime();
        long receivedCount = indication.getReceivedCount();
        long sentCount     = indication.getSentCount();

        if (gotFirst) {     // Need first two reports in order to do calculations
            long  freeMemory       = indication.getFreeMemory();
            long  totalMemory      = indication.getTotalMemory();
            int   threadCount      = indication.getThreadCount();
            int   connectionCount  = indication.getConnectionCount();
            int   uniqueCount      = indication.getUniqueCount();
            int   roomCount        = indication.getRoomCount();
            int   personalCount    = indication.getPersonalCount();
            int   privateCount     = indication.getPrivateCount();

            long  usedMemory       = totalMemory   - freeMemory;
            long  receivedInterval = receivedCount - oldReceivedCount;
            long  sentInterval     = sentCount     - oldSentCount;
            float seconds          = (float) (time - oldTime) / 1000.0f;
            int   receivedPerSec   = Math.round((float) receivedInterval / seconds);
            int   sentPerSec       = Math.round((float) sentInterval     / seconds);
            int   packetsPerSec    = receivedPerSec + sentPerSec;
            Date  date             = new Date(time);

            Object[] heap = new Object[Default.HEAP_SIZE];
            heap[Default.HEAP_USED]       = new Integer(Math.round((float) usedMemory  / 1024.0f));
            heap[Default.HEAP_TOTAL]      = new Integer(Math.round((float) totalMemory / 1024.0f));
            heap[Default.HEAP_PERCENTAGE] = new Float((float) usedMemory / (float) totalMemory);
            String heapInfo = heapFormatter.format(heap);

            Object[] res = new Object[Default.RES_SIZE];
            res[Default.RES_THREADS]     = new Integer(threadCount);
            res[Default.RES_CONNECTIONS] = new Integer(connectionCount);
            res[Default.RES_UNIQUE]      = new Integer(uniqueCount);
            String resInfo = resFormatter.format(res);

            Object[] status = new Object[Default.STATUS_SIZE];
            status[Default.STATUS_DATE]        = dateFormatter.format(date);
            status[Default.STATUS_HEAP]        = heapInfo;
            status[Default.STATUS_RESOURCES]   = resInfo;
            status[Default.STATUS_ROOMS]       = new Integer(roomCount);
            status[Default.STATUS_PERSONAL]    = new Integer(personalCount);
            status[Default.STATUS_PRIVATE]     = new Integer(privateCount);
            status[Default.STATUS_RCVD_PERSEC] = new Integer(receivedPerSec);
            status[Default.STATUS_SENT_PERSEC] = new Integer(sentPerSec);
            status[Default.STATUS_PKTS_PERSEC] = new Integer(packetsPerSec);
            System.out.println(statusFormatter.format(status));
        }

        gotFirst         = true;
        oldTime          = time;
        oldReceivedCount = receivedCount;
        oldSentCount     = sentCount;
    }

    /**
     * Replies to a ping indication with a ping response.
     *
     * @param  connection  the connection to the server.
     * @param  indication  the ping indication packet.
     * @exception java.io.IOException  when an I/O error occurs.
     */

    private void ping(Connection connection, Ping indication) throws IOException {
        indication.response();
        connection.send(indication);
    }

    /**
     * Handles a positive or negative monitor confirmation.
     *
     * @param  connection  the connection to the server.
     * @param  confirm     the monitor confirmation packet.
     */

    private void monitor(Connection connection, Monitor confirm) {
        if (! confirm.isAllowed()) {
            System.err.println("Invalid administrative password.");
            connection.close();
            System.exit(1);
        }
    }

    /**
     * Handles a null object, indicating that the connection is closed.
     *
     * @param  connection  the connection to the server.
     */

    private void nullObject(Connection connection) {
        System.err.println("Disconnected from server.");
        System.exit(1);
    }

    /**
     * Sets the properties defined in the specified file to be part of the system
     * properties.
     *
     * @param file  the Java properties file.
     * @exception java.io.IOException  if an I/O error occurs loading the
     *                                 properties.
     */

    private static void setProperties(File file) throws IOException {
        Properties properties = new Properties(System.getProperties());
        FileInputStream input = new FileInputStream(file);
        properties.load(input);
        input.close();
        System.setProperties(properties);
    }

    /**
     * Starts up the status monitoring program.
     *
     * @param args  the optional name of the Java properties file, with a default
     *              of <code>properties.txt</code> if found.
     */

    public static void main (String args[]) {
        if (args.length > 1) {
            System.err.println("Usage: java COM.volano.Status [properties]");
            return;
        }

        try {
            String path = args.length == 1 ? args[0] : PROPERTIES;
            File   file = new File(path);
            if (! file.isAbsolute()) {
                file = new File(System.getProperty(Key.INSTALL_ROOT), path);
            }

            if (args.length == 1) {
                setProperties(file);
            } else {
                try {
                    setProperties(file);
                } catch (FileNotFoundException e) {}
            }

            String password = System.getProperty(Key.ADMIN_PASSWORD, Default.ADMIN_PASSWORD);
            if (password.length() == 0) {
                System.err.println("Property " + Key.ADMIN_PASSWORD + " is undefined.");
            } else {
                String host         = System.getProperty(Key.SERVER_HOST,             Default.SERVER_HOST);
                int    port         = Integer.getInteger(Key.ADMIN_PORT,              Default.ADMIN_PORT_INT).intValue();
                int    interval     = Integer.getInteger(Key.STATUS_INTERVAL,         Default.STATUS_INTERVAL_INT).intValue();
                String statusFormat = System.getProperty(Key.FORMAT_STATUS,           Default.FORMAT_STATUS);
                String dateFormat   = System.getProperty(Key.FORMAT_DATE,             Default.FORMAT_DATE);
                String heapFormat   = System.getProperty(Key.FORMAT_STATUS_MEMORY,    Default.FORMAT_STATUS_MEMORY);
                String resFormat    = System.getProperty(Key.FORMAT_STATUS_RESOURCES, Default.FORMAT_STATUS_RESOURCES);

                if (host.length() == 0) {
                    host = LOCALHOST;
                }
                new Status(host, port, password, interval, statusFormat, dateFormat, heapFormat, resFormat);
            }
        } catch (IOException e) {
            System.err.println("Unable to send status monitoring request.");
            e.printStackTrace(System.err);
        }
    }
}
