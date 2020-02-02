/*
 * EventSender.java - a queue for asychronously sending room events.
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
import  COM.volano.util.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;

/**
 * This class manages a queue of events to be sent on an HTTP URL connection.
 * For each event that occurs in the room being observed, an HTTP POST is sent
 * to the specified URL with the event information in XML notation.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class EventSender extends NotifyQueue implements Runnable, Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String THREAD_NAME = "EventSender";
    private static final String TEXT_PLAIN  = "text/plain";

    private Observable room;      // The room being observed
    private URL        url;       // The destination URL to send events.

    /**
     * Creates a new event sender for the specified URL.
     *
     * @param room  the room which this event sender is observing.
     * @param url   the URL of the event recipient.
     */

    EventSender(Observable room, URL url) {
        this.room = room;
        this.url  = url;
        room.addObserver(this);
        Thread thread = new Thread(this, THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Writes an event to the destination URL as an HTTP POST.
     *
     * @param event  the event to write.
     */

    // The event recipient should return an HTTP status of 200 (OK) and at least
    // the following headers in order to ensure a keep-alive connection is used:
    //
    //   Content-Type: text/plain
    //   Content-Length: 1
    //   Connection: Keep-Alive
    //
    // with a single newline character ('\n') as the content.  The length of the
    // content may be any non-zero value, but all returned content is ignored.
    //
    // A sample request/response trace is as follows:
    //
    // <- POST /servlet/event HTTP/1.0
    // <- User-Agent: Java1.1.7
    // <- Host: red.volano.com:9080
    // <- Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
    // <- Connection: keep-alive
    // <- Content-type: application/x-www-form-urlencoded
    // <- Content-length: 51
    // <-
    //
    // -> HTTP/1.0 200 OK
    // -> Server: servletrunner/2.0
    // -> Content-Type: text/plain
    // -> Content-Length: 1
    // -> Connection: Keep-Alive
    // -> Date: Sun, 25 Oct 1998 20:56:02 GMT
    // ->

    private void writeEvent(Object event) throws IOException {
        if (event == null) {        // Null means to quit
            close();
        } else {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);     // For HTTP POST request
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            writer.println(event.toString());
            writer.close();

            // Although reading or skipping the content is not necessary to enable a
            // keep-alive connection, we must at least close the input stream if we
            // want to allow the connection to be reused.
            int status = connection.getResponseCode();
            int len    = connection.getContentLength();
            InputStream input = connection.getInputStream();
            if (len != -1) {
                input.skip(len);    // Ignore any returned content
            }
            input.close();                    // Required for keep-alive connection
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP status = " + status);
            }
        }
    }

    /**
     * Writes each event to the destination, one at a time, in the order in which
     * they were added to the queue.
     *
     * @param list  the array of events to be written.
     */

    private void sendList(Object[] list) throws IOException {
        for (int i = 0; isOpen() && i < list.length; i++) {
            writeEvent(list[i]);
        }
    }

    /**
     * The body of the asynchronous send thread.  This thread waits for events to
     * be placed on the queue by the update method.  When one or more events are
     * added to the queue, this thread removes the objects from the queue and
     * sends them on an HTTP connection to their recipient.
     */

    public void run() {
        try {
            while (isOpen()) {
                sendList(getElements());
                Thread.yield();         // Yield to equal priority threads (for Solaris)
            }
        } catch (InterruptedException e) {
        } catch (IOException e) {
            Log.printError(Message.format(Msg.BAD_URL, url.toString()), e);
        } catch (ThreadDeath e) {
            throw e;                                  // Rethrow for cleanup
        } catch (Throwable t) {
            Log.printError(Message.format(Msg.BAD_URL, url.toString()), t);
        } finally {
            room.deleteObserver(this);
            if (isOpen()) {
                close();
            }
        }
    }

    /**
     * Called when an event occurs in the room being observed.
     *
     * @param observable  the room being observed.
     * @param object      the event which occurred in the room.
     */

    public void update(Observable observable, Object object) {
        putElement(object);
    }

    /**
     * Compares the specified object with this event sender for equality.
     *
     * @param object the object to be compared for equality with this
     *               event sender.
     * @return <em>true</em> if the specified object is equal to this
     *         event sender.
     */

    public boolean equals(Object object) {
        // return (this == object);
        return (object instanceof EventSender) && url.equals(((EventSender) object).url);
    }

    /**
     * Returns the hash code value for this event sender.
     *
     * @return the hash code value for this event sender.
     */

    public int hashCode() {
        // return System.identityHashCode(this);
        return url.hashCode();
    }

    /**
     * Gets a string representation of this event sender.
     *
     * @return  a string printing the class name and URL.
     */

    public String toString() {
        return getClass().getName() + "<" + url + ">";
    }
}
