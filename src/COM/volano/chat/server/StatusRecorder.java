/*
 * StatusRecorder.java - a class to keep a limited history of the server status.
 * Copyright (C) 2001 John Neffenger
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
import  COM.volano.chat.packet.Report;
import  java.util.Date;
import  java.util.Observable;
import  java.util.Observer;
import  java.util.Vector;

/**
 * This class keeps a short history of the chat server status for use by the Web
 * monitoring interface.
 *
 * @author  John Neffenger
 * @version 23 Jul 2001
 */

class StatusRecorder implements Observer {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private static final float BYTES_PER_KB   = 1024.0f;
    private static final float MILLIS_PER_SEC = 1000.0f;

    private int          historyMax;
    private Vector       history;
    private StatusRecord latest;

    private boolean gotFirst;
    private long    oldTime;
    private long    oldReceivedCount;
    private long    oldSentCount;

    /**
     * Creates a <code>Status</code> object which connects to the chat server
     * administrative port and sends a request for status reports.
     *
     */

    StatusRecorder(int historyMax) {
        this.historyMax = historyMax;
        this.history    = new Vector(historyMax);
        this.latest     = new StatusRecord();
    }

    int getHistoryMax() {
        return historyMax;
    }

    synchronized void setHistoryMax(int historyMax) {
        this.historyMax = historyMax;
        history.setSize(historyMax);
    }

    synchronized StatusSnapshot getSnapshot() {
        return new StatusSnapshot((StatusRecord[]) history.toArray(new StatusRecord[0]), latest);
    }

    private synchronized void addRecord(StatusRecord record) {
        latest = record;
        if (historyMax > 0) {
            if (history.size() == historyMax) {
                history.removeElementAt(historyMax - 1);
            }
            history.insertElementAt(record, 0);
        }
    }

    /**
     * Called whenever the observed connection has changed.  The observed
     * connection changes when it receives a packet from the server.
     *
     * @param observable  the observed connection object.
     * @param object      the packet received on the connection.
     */

    public void update(Observable observable, Object object) {
        Report report = (Report) object;
        long time            = report.getTime();
        long receivedCount   = report.getReceivedCount();
        long sentCount       = report.getSentCount();
        long freeMemory      = report.getFreeMemory();
        long totalMemory     = report.getTotalMemory();
        int  threadCount     = report.getThreadCount();
        int  connectionCount = report.getConnectionCount();
        int  roomCount       = report.getRoomCount();
        int  personalCount   = report.getPersonalCount();
        int  privateCount    = report.getPrivateCount();

        Date date               = new Date(time);
        long usedMemory         = totalMemory   - freeMemory;
        long heapUsedKB         = Math.round((float) usedMemory  / BYTES_PER_KB);
        long heapFreeKB         = Math.round((float) freeMemory  / BYTES_PER_KB);
        long heapTotalKB        = Math.round((float) totalMemory / BYTES_PER_KB);
        int  heapUsedPercentage = Math.round(((float) usedMemory / (float) totalMemory) * 100.0f);
        int  heapFreePercentage = 100 - heapUsedPercentage;

        int receivedPerSec = 0;
        int sentPerSec     = 0;
        int totalPerSec    = 0;
        if (gotFirst) {     // Need first two reports in order to do calculations
            long  receivedInterval = receivedCount - oldReceivedCount;
            long  sentInterval     = sentCount     - oldSentCount;
            float seconds          = (float) (time - oldTime) / MILLIS_PER_SEC;
            receivedPerSec = Math.round((float) receivedInterval / seconds);
            sentPerSec     = Math.round((float) sentInterval     / seconds);
            totalPerSec    = receivedPerSec + sentPerSec;
        }

        addRecord(new StatusRecord(date, heapUsedKB, heapFreeKB, heapTotalKB, heapUsedPercentage, heapFreePercentage,
                                   threadCount, connectionCount, roomCount, personalCount, privateCount,
                                   receivedCount, sentCount, receivedPerSec, sentPerSec, totalPerSec));
        gotFirst         = true;
        oldTime          = time;
        oldReceivedCount = receivedCount;
        oldSentCount     = sentCount;
    }
}
