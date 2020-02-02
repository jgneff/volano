/*
 * Grouptable.java - a class for maintaining a list of rooms.
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
import  COM.volano.util.Message;
import  java.util.*;

/**
 * This class manages a list of rooms by creating a thread to remove the empty
 * rooms in the list.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class Grouptable extends Hashtable implements Runnable {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private String name;
    private int    interval;
    private Thread sweeper;

    /**
     * Creates a new group table object.
     *
     * @param name      the name of this room list and its associated list
     *                  <i>sweeper</i> thread.
     * @param priority  the priority of the sweeper thread.
     * @param interval  the interval at which the sweeper thread checks the list
     *                  for empty rooms.
     */

    Grouptable(String name, int priority, int interval) {
        this.name     = name;
        this.interval = interval;
        this.sweeper  = new Thread(this, name);
        sweeper.setPriority(priority);
        sweeper.setDaemon(true);
        sweeper.start();
    }

    /**
     * Gets a snapshot of the room list.
     *
     * @return  the rooms in the list at the time this method was called.
     */

    synchronized Observer[] snapshot() {
        int         count = size();
        Observer[]  list  = new Observer[count];
        Enumeration enumeration  = elements();
        for (int i = 0; i < count; i++) {
            list[i] = (Observer) enumeration.nextElement();
        }
        return list;
    }

    /**
     * The body of the room list <i>sweeper</i> thread.  This method checks the
     * list at its specified interval and removes empty rooms from the list.
     */

    public void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (sweeper == thisThread) {
                Thread.sleep(interval);
                synchronized (this) {
                    Enumeration enumeration = elements();
                    while (enumeration.hasMoreElements()) {
                        Room room = (Room) enumeration.nextElement();
                        if (room.size() == 0) {
                            remove(room.key());
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            // Interrupted when finalized.
        } catch (Throwable t) {
            Log.printError(Message.format(Msg.UNEXPECTED, name), t);
        } finally {
            sweeper = null;
            Log.printError(Message.format(Msg.STOPPING, name));
        }
    }

    /**
     * Finalizes this object by stopping its thread.
     *
     * @exception java.lang.Throwable  if an error occurs finalizing this object.
     */

    protected void finalize() throws Throwable {
        super.finalize();
        if (sweeper != null) {
            Thread thread = sweeper;
            sweeper = null;
            thread.interrupt();
        }
    }
}
