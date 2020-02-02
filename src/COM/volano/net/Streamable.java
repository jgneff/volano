/*
 * Streamable.java - an interface for creating streamable objects
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

package COM.volano.net;
import  java.io.*;

/**
 * This interface defines the methods required for an object to be streamable.
 * A streamable object is one that can write itself to a
 * <code>DataOutputStream</code> or read itself from a
 * <code>DataInputStream</code>.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @see     Connection
 */

public interface Streamable {

    /**
     * Returns this streamable object's integer identifier.
     *
     * @return this objects integer identifier.
     */

    public int getId();

    /**
     * Gets the read pause for this object.
     *
     * @return the pause, in milliseconds, after reading this object.
     */

    public long getReadPause();

    /**
     * Gets the write pause for this object.
     *
     * @return the pause, in milliseconds, after writing this object.
     */

    public long getWritePause();

    /**
     * Writes this object to the data output stream.
     *
     * @param output  the output stream to which the object is to be written.
     * @exception java.io.IOException
     *              when an I/O error occurs writing the object.
     * @see #readFrom
     */

    public void writeTo(DataOutputStream output) throws IOException;

    /**
     * Reads this object from the data input stream.
     *
     * @param input  the input stream from which the object is to be read.
     * @exception java.io.IOException
     *              when an I/O error occurs reading the object.
     * @see #writeTo
     */

    public void readFrom(DataInputStream input) throws IOException;
}
