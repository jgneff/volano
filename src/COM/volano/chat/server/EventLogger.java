/*
 * EventLogger.java - a queue for asychronously logging room events.
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
import  COM.volano.util.*;
import  java.io.*;
import  java.net.*;
import  java.util.*;
import  java.text.*;

/**
 * This class manages a queue of events to be logged to disk.
 *
 * @author  John Neffenger
 * @version 22 Aug 1998
 */

public class EventLogger extends NotifyQueue implements Runnable, Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String THREAD_NAME = "EventLogger";

    private File          file;           // The log file
    private MessageFormat formatter;      // The format string for logging
    private PrintWriter   writer;

    /**
     * Creates a new event sender for the specified URL.
     *
     * @param file       the log file.
     * @param formatter  the message text formatter.
     */

    EventLogger(File file, MessageFormat formatter) throws IOException {
        this.file      = file;
        this.formatter = formatter;
        // Open print file writer in append mode with autoflush.
        this.writer    = new PrintWriter(new FileWriter(file.getPath(), true), true);
        Thread thread  = new Thread(this, THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Writes an event to the destination URL as an HTTP POST.
     *
     * @param event  the event to write.
     */

    private void writeEvent(Object event) throws IOException {
        if (event instanceof ChatEvent) {
            ChatEvent chatEvent = (ChatEvent) event;
            Object[]  args      = new Object[Default.TRANSCRIPT_SIZE];
            args[Default.FROM_NAME]    = chatEvent.getFromUser();
            args[Default.TO_NAME]      = chatEvent.getToUser();
            args[Default.MESSAGE]      = chatEvent.getMessage();
            args[Default.MESSAGE_DATE] = new Date();
            writer.println(formatter.format(args));
        } else if (event == null) {         // Null means to quit
            close();
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
            Log.printError(Message.format(Msg.WRITE_ERROR, file.getPath()), e);
        } catch (ThreadDeath e) {
            throw e;                                  // Rethrow for cleanup
        } catch (Throwable t) {
            Log.printError(Message.format(Msg.WRITE_ERROR, file.getPath()), t);
        } finally {
            writer.close();
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
     * Compares the specified object with this event logger for equality.
     *
     * @param object the object to be compared for equality with this
     *               event logger.
     * @return <em>true</em> if the specified object is equal to this
     *         event logger.
     */

    public boolean equals(Object object) {
        // return (this == object);
        return (object instanceof EventLogger) && file.equals(((EventLogger) object).file);
    }

    /**
     * Returns the hash code value for this event logger.
     *
     * @return the hash code value for this event logger.
     */

    public int hashCode() {
        // return System.identityHashCode(this);
        return file.hashCode();
    }

    /**
     * Gets a string representation of this event logger.
     *
     * @return  a string printing the class name and log file name.
     */

    public String toString() {
        return getClass().getName() + "<" + file + ">";
    }
}
