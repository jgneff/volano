/*
 * PrivatePacket.java - an interface for getting the private room id.
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

/**
 * This interface defines the methods for getting the private chat room
 * identifier.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public interface PrivatePacket {

    /**
     * Checks whether this packet has been fully handled, requiring no further
     * processing.
     *
     * @returns <code>true</code> if this packet has been handled; otherwise
     *          <code>false</code>.
     */

    // Causes "java.lang.AbstractMethodError: COM/volano/ak.d" from DashO-Pro.
    // public boolean isHandled();

    /**
     * Gets the private chat room identifier.
     *
     * @return the private chat room identifier.
     */

    public int getRoomId();
}
