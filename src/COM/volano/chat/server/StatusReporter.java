/*
 * StatusReporter.java - a class to report chat server status.
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
import  COM.volano.net.Connection;
import  COM.volano.chat.packet.*;
import  COM.volano.util.Message;
import  java.util.Observable;

/**
 * This class collects statistical information about the chat server environment
 * and reports it to all observers at the specified interval.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class StatusReporter extends Observable implements Runnable {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final String THREAD_NAME = "StatusReporter";

    private long       interval;
    private PublicList roomList;
    private PublicList personalList;
    private Grouptable privateList;
    private Runtime    runtime;

    private boolean keepReporting;
    private Thread  reporter;

    private long time;
    private long freeMemory;
    private long totalMemory;
    private int  threadCount;
    private int  connectionCount;
    private int  uniqueCount;
    private int  roomCount;
    private int  personalCount;
    private int  privateCount;
    private long receivedCount;
    private long sentCount;

    /**
     * Creates a new status reporter.
     *
     * @param seconds       the interval at which the reports are to be generated,
     *                      in seconds.
     * @param roomList      the list of public chat rooms.
     * @param personalList  the list of personal chat rooms.
     * @param privateList   the list of private chat rooms.
     */

    StatusReporter(int seconds, PublicList roomList, PublicList personalList, Grouptable privateList) {
        this.roomList     = roomList;
        this.personalList = personalList;
        this.privateList  = privateList;
        this.interval     = seconds * 1000L;
        this.runtime      = Runtime.getRuntime();
    }

    /**
     * Performs a full garbage collection, returning only when there are no more
     * objects to be removed from the heap.
     */

    private void fullGC() {
        long isFree = runtime.freeMemory();
        long wasFree;
        do {
            wasFree = isFree;
            runtime.gc();
            isFree  = runtime.freeMemory();
        } while (isFree > wasFree);
        runtime.runFinalization();
    }

    /**
     * Starts the status reporting.
     *
     * @param group     the thread group for the status reporting thread.
     * @param priority  the priority of the status reporting thread.
     */

    synchronized void start(ThreadGroup group, int priority) {
        if (reporter == null) {
            Connection.setCounting(true);
            reporter = new Thread(group, this, THREAD_NAME);
            reporter.setPriority(priority);
            reporter.setDaemon(true);
            reporter.start();
        }
    }

    /**
     * Stops the status reporting.
     */

    synchronized void stop() {
        if (reporter != null) {
            Connection.setCounting(false);
            Thread thread = reporter;
            reporter = null;
            thread.interrupt();
            deleteObservers();
        }
    }

    /**
     * The body of the status reporting thread.  This method repeatedly collects
     * the status information, notifies all observers, and waits the specified
     * interval.
     */

    public void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (reporter == thisThread) {
                // fullGC();            // Perform full garbage collection
                time            = System.currentTimeMillis();
                freeMemory      = runtime.freeMemory();
                totalMemory     = runtime.totalMemory();
                threadCount     = Thread.activeCount();
                connectionCount = Connection.getCount();
                uniqueCount     = Connection.getUniqueCount();
                roomCount       = roomList.size();
                personalCount   = personalList.size();
                privateCount    = privateList.size();
                receivedCount   = Connection.getReceived();
                sentCount       = Connection.getSent();
                setChanged();
                notifyObservers(new Report(time, freeMemory, totalMemory, threadCount, connectionCount, uniqueCount,
                                           roomCount, personalCount, privateCount, receivedCount, sentCount));
                Thread.sleep(interval);
            }
        } catch (InterruptedException e) {
            // Caught when the status reporting is stopped.
        } catch (ThreadDeath e) {
            throw e;          // Rethrow for cleanup
        } catch (Throwable t) {
            Log.printError(Message.format(Msg.UNEXPECTED, THREAD_NAME), t);
        }
    }

    /**
     * Finalizes this object by stopping its thread.
     *
     * @exception java.lang.Throwable  if an error occurs finalizing this object.
     */

    protected void finalize() throws Throwable {
        super.finalize();
        stop();
    }
}
