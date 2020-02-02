/*
 * SendQueue.java - a queue for asychronously sending data on a connection.
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

package COM.volano.net;
import  COM.volano.util.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class manages a queue of streamable objects to be sent on a connection.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     Connection
 */

public class SendQueue extends NotifyQueue implements Runnable {
    private Connection connection;

    /**
     * Creates a new send queue for the specified connection.
     *
     * @param connection  the connection associated with this send queue.
     */

    SendQueue(Connection connection) {
        this.connection = connection;
    }

    /**
     * Pauses for the configured time if the object is streamable.
     *
     * @param object  the object read.
     * @exception java.io.InterruptedException if this thread is interrupted.
     */

    private void pause(Streamable object) throws InterruptedException {
        long pause = object.getWritePause();
        if (pause > 0L) {
            Thread.sleep(pause);
        }
    }

    /**
     * Writes an array of streamable objects to a connection, one at a time, in
     * the order in which they were added to the queue.
     *
     * @param list  the array of streamable objects to be written.
     */

    private void sendList(Object[] list) throws IOException, InterruptedException {
        for (int i = 0; i < list.length; i++) {
            Streamable object = (Streamable) list[i];
            connection.write(object);
            pause(object);
        }
    }

    /**
     * The body of the asynchronous send thread started by
     * <code>Connection.startSending</code>.  This thread waits for objects to be
     * placed on the send queue.  When one or more objects are added to the queue,
     * this thread removes the objects from the queue and sends them on the
     * connection.
     */

    public void run() {
        int status = Connection.HTTP_OK;
        try {
            while (isOpen()) {
                sendList(getElements());
                Thread.yield();         // Yield to equal priority threads (for Solaris)
            }
        } catch (InterruptedException e) {
            // Caught when interrupted by the receive thread.
        } catch (IOException e) {
            // Caught when the socket output stream is closed by the receive thread.
        } catch (ThreadDeath e) {
            status = Connection.HTTP_UNAVAILABLE;     // Shutting down (server) or pruned (applet)
            throw e;                                  // Rethrow for cleanup
        } catch (Throwable t) {
            Connection.printError("Error sending to " + connection.getHostAddress() + ".", t);
            status = Connection.HTTP_INTERNAL_ERROR;  // Set error status code
        } finally {
            connection.close(status);                 // Ignored if already closed
        }
    }
}
