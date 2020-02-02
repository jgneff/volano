/*
 * StatusSnapshot.java - a snapshot of the server status for the Web interface.
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

/**
 * This class keeps a short history of the chat server status for use by the Web
 * monitoring interface.
 *
 * @author  John Neffenger
 * @version 23 Jul 2001
 */

public class StatusSnapshot {
    public static final String COPYRIGHT = Build.COPYRIGHT;

    private StatusRecord[] history;
    private StatusRecord   latest;

    StatusSnapshot(StatusRecord[] history, StatusRecord latest) {
        this.history = history;
        this.latest  = latest;
    }

    public StatusRecord[] getHistoryList() {
        return history;
    }

    public String getTimeStamp() {
        return latest.getTimeStamp();
    }

    public long getHeapMemoryUsed() {
        return latest.getHeapMemoryUsed();
    }

    public long getHeapMemoryFree() {
        return latest.getHeapMemoryFree();
    }

    public long getHeapMemoryTotal() {
        return latest.getHeapMemoryTotal();
    }

    public int getHeapMemoryUsedPercentage() {
        return latest.getHeapMemoryUsedPercentage();
    }

    public int getHeapMemoryFreePercentage() {
        return latest.getHeapMemoryFreePercentage();
    }

    public int getThreadCount() {
        return latest.getThreadCount();
    }

    public int getConnectionCount() {
        return latest.getConnectionCount();
    }

    public int getPublicRoomCount() {
        return latest.getPublicRoomCount();
    }

    public int getPersonalRoomCount() {
        return latest.getPersonalRoomCount();
    }

    public int getPrivateRoomCount() {
        return latest.getPrivateRoomCount();
    }

    public long getReceivedCount() {
        return latest.getReceivedCount();
    }

    public long getSentCount() {
        return latest.getSentCount();
    }

    public int getAverageReceivedPerSecond() {
        return latest.getAverageReceivedPerSecond();
    }

    public int getAverageSentPerSecond() {
        return latest.getAverageSentPerSecond();
    }

    public int getAverageTotalPerSecond() {
        return latest.getAverageTotalPerSecond();
    }
}
