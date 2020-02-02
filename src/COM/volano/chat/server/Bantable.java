/*
 * Bantable.java - a class for maintaining a list of banned addresses.
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

import  java.util.*;
import  java.text.*;

import  COM.volano.util.Message;
import  COM.volano.net.DNSBlacklist;

/**
 * This class manages a list of banned addresses by creating a thread to remove
 * the addresses when their ban duration expires.
 *
 * @author  John Neffenger
 * @version 2.5
 * @since   2.5
 */

class Bantable extends Hashtable implements Runnable {
    private static final int    STATIC_DURATION   = 1440;
    private static final int    DYNAMIC_DURATION  = 60;
    private static final int    NETBLOCK_DURATION = 60;
    private static final String NETBLOCK_IPV4MASK = "255.0.0.0";

    private static final String THREAD_NAME       = "Bantable";
    private static final long   INTERVAL          = 60 * 1000; // One minute
    private static final long   MILLIS_PER_MINUTE = 60 * 1000;

    private static int    staticDuration   = STATIC_DURATION;
    private static int    dynamicDuration  = DYNAMIC_DURATION;
    private static int    netblockDuration = NETBLOCK_DURATION;
    private static String netblockIpv4mask = NETBLOCK_IPV4MASK;

    private DateFormat    dateFormat;
    private MessageFormat bannedFormat;

    private Thread sweeper;

    static int getStaticDuration() {
        return staticDuration;
    }

    static int getDynamicDuration() {
        return dynamicDuration;
    }

    static int getNetblockDuration() {
        return netblockDuration ;
    }

    static String getNetblockIpv4mask() {
        return netblockIpv4mask;
    }

    /**
     * Sets the static IP address ban duration.
     *
     * @param duration the duration, in minutes, to ban static IP addresses.
     */

    static void setStaticDuration(int duration) {
        staticDuration = duration;
    }

    /**
     * Sets the dynamic IP address ban duration.
     *
     * @param duration the duration, in minutes, to ban dynamic IP addresses.
     */

    static void setDynamicDuration(int duration) {
        dynamicDuration = duration;
    }

    /**
     * Sets the IP address network block ban duration.
     *
     * @param duration the duration, in minutes, to ban IP address network
     *        blocks.
     */

    static void setNetblockDuration(int duration) {
        netblockDuration = duration;
    }

    /**
     * Sets the IPv4 address network mask defining the network block range.
     *
     * @param the network mask defining the network block range.
     */

    static void setNetblockIpv4mask(String mask) {
        netblockIpv4mask = mask;
    }

    /**
     * Creates a new ban table object.
     */

    Bantable(DateFormat dateFormat, MessageFormat bannedFormat) {
        this.dateFormat   = dateFormat;
        this.bannedFormat = bannedFormat;
        sweeper = new Thread(this, THREAD_NAME);
        sweeper.setDaemon(true);
        sweeper.start();
    }

    private String format(Date date) {
        synchronized (dateFormat) {
            return dateFormat.format(date);
        }
    }

    private long getDuration(int type) {
        long duration = 0;
        switch (type) {
        case Ban.STATIC:
            duration = staticDuration == -1 ? staticDuration : staticDuration * MILLIS_PER_MINUTE;
            break;
        case Ban.DYNAMIC:
            duration = dynamicDuration == -1 ? dynamicDuration : dynamicDuration * MILLIS_PER_MINUTE;
            break;
        case Ban.NETBLOCK:
            duration = netblockDuration == -1 ? netblockDuration : netblockDuration * MILLIS_PER_MINUTE;
            break;
        default:
            break;
        }
        return duration;
    }

    private void log(Ban ban) {
        try {
            if (Log.ban != null && bannedFormat.toPattern().length() > 0) {
                Object[] info = new Object[Default.BAN_SIZE];
                info[Default.BAN_DATE] = format(ban.getDate());
                info[Default.BAN_ADDRESS] = ban.getAddress();
                info[Default.BAN_TYPE] = new Integer(ban.getType());
                info[Default.BAN_ROOM_NAME] = ban.getRoomName();
                info[Default.BAN_USER_NAME] = ban.getUserName();
                info[Default.BAN_MONITOR_NAME] = ban.getMonitorName();
                Log.ban.log(bannedFormat.format(info));
            }
        } catch (IllegalArgumentException e) {
            Log.printError(Msg.BAD_BAN_FORMAT, e);
        }
    }

    public Object put(Object key, Object value) {
        String address = (String) key;
        Ban    ban     = (Ban) value;
        Object oldban  = null;
        boolean isDynamic = DNSBlacklist.contains(DNSBlacklist.getDynamic(), address);
        if (isDynamic) {
            if (dynamicDuration != 0) {
                Ban copy = (Ban) ban.clone();
                copy.setType(Ban.DYNAMIC);
                oldban = super.put(address, copy);
                log(copy);
            }
            if (netblockDuration != 0) {
                String netblock = AccessControl.getNetblock(address, netblockIpv4mask);
                Ban copy = (Ban) ban.clone();
                copy.setType(Ban.NETBLOCK);
                copy.setAddress(netblock);
                oldban = super.put(netblock, copy);
                log(copy);
            }
        } else {
            if (staticDuration != 0) {
                Ban copy = (Ban) ban.clone();
                copy.setType(Ban.STATIC);
                oldban = super.put(address, copy);
                log(copy);
            }
        }
        return oldban;
    }

    /**
     * The body of the ban table <i>sweeper</i> thread.  This method checks the
     * list at its specified interval and removes expired bans from the list.
     */

    public void run() {
        Thread thisThread = Thread.currentThread();
        try {
            while (sweeper == thisThread) {
                Thread.sleep(INTERVAL);
                synchronized (this) {
                    Enumeration enumeration = elements();
                    while (enumeration.hasMoreElements()) {
                        Ban ban = (Ban) enumeration.nextElement();
                        long duration = getDuration(ban.getType());
                        if (duration >= 0) { // -1 means forever
                            long start = ban.getDate().getTime();
                            long now   = System.currentTimeMillis();
                            if (now - start > duration) {
                                remove(ban.getAddress());
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException e) {}
        catch (Throwable t) {
            Log.printError(Message.format(Msg.UNEXPECTED, THREAD_NAME), t);
        } finally {
            sweeper = null;
            Log.printError(Message.format(Msg.STOPPING, THREAD_NAME));
        }
    }

    /**
     * Finalizes this object by stopping its thread.
     *
     * @exception java.lang.Throwable if an error occurs finalizing this object.
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
