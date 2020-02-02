/*
 * StatusRecord.java - a class to keep a single server status record.
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
import  java.util.Date;

/**
 * This class keeps a short history of the chat server status for use by the Web
 * monitoring interface.
 *
 * @author  John Neffenger
 * @version 23 Jul 2001
 */

public class StatusRecord {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private Date date;
    private long heapUsedKB;
    private long heapFreeKB;
    private long heapTotalKB;
    private int  heapUsedPercentage;
    private int  heapFreePercentage;
    private int  threadCount;
    private int  connectionCount;
    private int  roomCount;
    private int  personalCount;
    private int  privateCount;
    private long receivedCount;
    private long sentCount;
    private int  receivedPerSec;
    private int  sentPerSec;
    private int  totalPerSec;

    StatusRecord() {
        this.date = new Date();
    }

    StatusRecord(Date date,
                 long heapUsedKB, long heapFreeKB, long heapTotalKB, int heapUsedPercentage, int heapFreePercentage,
                 int threadCount, int connectionCount, int roomCount, int personalCount, int privateCount,
                 long receivedCount, long sentCount, int receivedPerSec, int sentPerSec, int totalPerSec) {
        this.date               = date;
        this.heapUsedKB         = heapUsedKB;
        this.heapFreeKB         = heapFreeKB;
        this.heapTotalKB        = heapTotalKB;
        this.heapUsedPercentage = heapUsedPercentage;
        this.heapFreePercentage = heapFreePercentage;
        this.threadCount        = threadCount;
        this.connectionCount    = connectionCount;
        this.roomCount          = roomCount;
        this.personalCount      = personalCount;
        this.privateCount       = privateCount;
        this.receivedCount      = receivedCount;
        this.sentCount          = sentCount;
        this.receivedPerSec     = receivedPerSec;
        this.sentPerSec         = sentPerSec;
        this.totalPerSec        = totalPerSec;
    }

    public String getTimeStamp() {
        return date.toString();
    }

    public long getHeapMemoryUsed() {
        return heapUsedKB;
    }

    public long getHeapMemoryFree() {
        return heapFreeKB;
    }

    public long getHeapMemoryTotal() {
        return heapTotalKB;
    }

    public int getHeapMemoryUsedPercentage() {
        return heapUsedPercentage;
    }

    public int getHeapMemoryFreePercentage() {
        return heapFreePercentage;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    public int getPublicRoomCount() {
        return roomCount;
    }

    public int getPersonalRoomCount() {
        return personalCount;
    }

    public int getPrivateRoomCount() {
        return privateCount;
    }

    public long getReceivedCount() {
        return receivedCount;
    }

    public long getSentCount() {
        return sentCount;
    }

    public int getAverageReceivedPerSecond() {
        return receivedPerSec;
    }

    public int getAverageSentPerSecond() {
        return sentPerSec;
    }

    public int getAverageTotalPerSecond() {
        return totalPerSec;
    }
}
