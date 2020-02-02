/*
 * Shutdown.java - a class to shutdown the chat server.
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
import  java.util.*;

/**
 * This class is a Java console application which requests the chat server to
 * shut down.  It must be run on the same system running the chat server.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Shutdown implements Observer {
    public  static final String COPYRIGHT  = Build.COPYRIGHT;
    private static final String PROPERTIES = "conf/properties.txt";
    private static final String LOCALHOST  = "localhost";

    private Connection connection;

    /**
     * Creates a <code>Shutdown</code> object which connects to the chat server
     * administrative port and sends it a halt request.
     *
     * @param port      the administrative port number of the chat server.
     * @param password  the administrative password.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    private Shutdown(String host, int port, String password) throws IOException {
        Connection.setPacketFactory(new ChatPacketFactory());
        connection = new Connection(host, port);
        connection.addObserver(this);
        connection.startSending(Thread.NORM_PRIORITY);
        connection.startReceiving(Thread.NORM_PRIORITY);
        System.out.println("Sending shutdown request to server ...");
        connection.send(new Halt(password));
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
        if (object instanceof Halt) {
            haltConfirm(connection, (Halt) object);
        } else if (object == null) {
            nullObject(connection);
        }
    }

    /**
     * Handles a positive or negative halt confirmation.
     *
     * @param  connection  the connection to the server.
     * @param  confirm     the halt confirmation packet.
     */

    private void haltConfirm(Connection connection, Halt confirm) {
        if (! confirm.isAllowed()) {
            System.err.println("Invalid administrative password.");
            connection.close();
            System.exit(1);
        } else {
            connection.close();
            System.exit(0);
        }
    }

    /**
     * Handles a null object, indicating that the connection is closed.  A null
     * object is received if the shutdown is successful.
     *
     * @param  connection  the connection to the server.
     */

    private void nullObject(Connection connection) {
        System.err.println("Disconnected from server.");
        System.exit(0);
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
     * Starts up the shutdown program.
     *
     * @param args  the optional name of the Java properties file, with a default
     *              of <code>properties.txt</code> if found.
     */

    public static void main (String args[]) {
        if (args.length > 1) {
            System.err.println("Usage: java COM.volano.Shutdown [properties]");
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
            String host     = System.getProperty(Key.SERVER_HOST, Default.SERVER_HOST);
            if (password.length() == 0) {
                System.err.println("Property " + Key.ADMIN_PASSWORD + " is undefined.");
            } else {
                if (host.length() == 0) {
                    host = LOCALHOST;
                }
                int port = Integer.getInteger(Key.ADMIN_PORT, Default.ADMIN_PORT_INT).intValue();
                new Shutdown(host, port, password);
            }
        } catch (IOException e) {
            System.err.println("Unable to send shutdown request.");
            e.printStackTrace(System.err);
        }
    }
}
