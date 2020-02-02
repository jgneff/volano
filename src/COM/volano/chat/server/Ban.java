/*
 * Ban.java - a class for ban details.
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

import java.util.Date;

/**
 * This class holds the details about a host ban.
 *
 * @author  John Neffenger
 * @version 22 June 2002
 */

class Ban implements Cloneable {
    static final int STATIC = 0;
    static final int DYNAMIC = 1;
    static final int NETBLOCK = 2;

    private Date date;
    private String address;
    private int type;
    private String roomName;
    private String userName;
    private String monitorName;

    Ban(Date date, String address, String roomName, String userName, String monitorName) {
        this.date = date;
        this.address = address;
        this.roomName = roomName;
        this.userName = userName;
        this.monitorName = monitorName;
    }

    Date getDate() {
        return date;
    }

    String getAddress() {
        return address;
    }

    void setAddress(String address) {
        this.address = address;
    }

    int getType() {
        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    String getRoomName() {
        return roomName;
    }

    String getUserName() {
        return userName;
    }

    String getMonitorName() {
        return monitorName;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
