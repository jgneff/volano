/*
 * Report.java - a server status report indication.
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

package COM.volano.chat.packet;
import  COM.volano.net.Packet;
import  java.io.*;

/**
 * This class encapsulates a server status report indication.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Report extends Packet {
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
     * The no-arg constructor required for deserialization.
     */

    public Report() {}

    /**
     * Creates a status report indication.
     *
     * @param time             the time of the report.
     * @param freeMemory       the amount of free memory in the heap.
     * @param totalMemory      the total amount of memory available to the heap.
     * @param threadCount      the number of active threads.
     * @param connectionCount  the number of connections.
     * @param uniqueCount      the number of connections with a unique IP address.
     * @param roomCount        the number of public rooms.
     * @param privateCount     the number of private chat sessions.
     * @param receivedCount    the total number of packets received by the server.
     * @param sentCount        the total number of packets sent by the server.
     */

    public Report(long time, long freeMemory, long totalMemory, int threadCount, int connectionCount, int uniqueCount,
                  int roomCount, int personalCount, int privateCount, long receivedCount, long sentCount) {
        setType(INDICATION);
        this.time            = time;
        this.freeMemory      = freeMemory;
        this.totalMemory     = totalMemory;
        this.threadCount     = threadCount;
        this.connectionCount = connectionCount;
        this.uniqueCount     = uniqueCount;
        this.roomCount       = roomCount;
        this.personalCount   = personalCount;
        this.privateCount    = privateCount;
        this.receivedCount   = receivedCount;
        this.sentCount       = sentCount;
    }

    /**
     * Gets the integer identifier of this packet.
     *
     * @return the packet id.
     */

    public int getId() {
        return ChatPacketId.PACKET_REPORT;
    }

    /**
     * Gets the time of the status report.
     *
     * @returns the time this status information was taken.
     */

    public long getTime() {
        return time;
    }

    /**
     * Gets the amount of free memory in the heap.
     *
     * @returns the free heap memory.
     */

    public long getFreeMemory() {
        return freeMemory;
    }

    /**
     * Gets the total amount of memory available to the heap.
     *
     * @returns the total heap memory.
     */

    public long getTotalMemory() {
        return totalMemory;
    }

    /**
     * Gets the number of active threads.
     *
     * @returns the active thread count.
     */

    public int getThreadCount() {
        return threadCount;
    }

    /**
     * Gets the number of connections.
     *
     * @returns the connection count.
     */

    public int getConnectionCount() {
        return connectionCount;
    }

    /**
     * Gets the number of connections with a unique IP address.
     *
     * @returns the unique IP address count.
     */

    public int getUniqueCount() {
        return uniqueCount;
    }

    /**
     * Gets the number of public rooms.
     *
     * @returns the public room count.
     */

    public int getRoomCount() {
        return roomCount;
    }

    /**
     * Gets the number of personal rooms.
     *
     * @returns the public room count.
     */

    public int getPersonalCount() {
        return personalCount;
    }

    /**
     * Gets the number of private chat sessions.
     *
     * @returns the private chat count.
     */

    public int getPrivateCount() {
        return privateCount;
    }

    /**
     * Gets the total number of packets received by the server since the
     * status monitoring began.
     *
     * @returns the total received count.
     */

    public long getReceivedCount() {
        return receivedCount;
    }

    /**
     * Gets the total number of packets sent by the server since the status
     * monitoring began.
     *
     * @returns the total sent count.
     */

    public long getSentCount() {
        return sentCount;
    }

    /**
     * Serializes this object to a data output stream.
     *
     * @param output  the data output stream for serializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void writeTo(DataOutputStream output) throws IOException {
        super.writeTo(output);
        output.writeLong(time);
        output.writeLong(freeMemory);
        output.writeLong(totalMemory);
        output.writeInt(threadCount);
        output.writeInt(connectionCount);
        output.writeInt(uniqueCount);
        output.writeInt(roomCount);
        output.writeInt(personalCount);
        output.writeInt(privateCount);
        output.writeLong(receivedCount);
        output.writeLong(sentCount);
    }

    /**
     * Deserializes this object from a data input stream.
     *
     * @param input  the data input stream for deserializing this object.
     * @exception java.io.IOException  if an I/O error occurs.
     */

    public void readFrom(DataInputStream input) throws IOException {
        super.readFrom(input);
        time            = input.readLong();
        freeMemory      = input.readLong();
        totalMemory     = input.readLong();
        threadCount     = input.readInt();
        connectionCount = input.readInt();
        uniqueCount     = input.readInt();
        roomCount       = input.readInt();
        personalCount   = input.readInt();
        privateCount    = input.readInt();
        receivedCount   = input.readLong();
        sentCount       = input.readLong();
    }
}
