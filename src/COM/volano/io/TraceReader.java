/*
 * TraceReader.java - writes character input stream to System.out.
 * Copyright (C) 1996-1999 John Neffenger
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

package COM.volano.io;
import  java.io.*;

/**
 * This class is a FilterReader which writes all characters to System.out after
 * reading them from the specified character input stream.
 *
 * @author  John Neffenger
 * @version 09 Nov 1999
 */

public class TraceReader extends FilterReader {
    /**
     * Create a new trace reader.
     *
     * @param out  a reader.
     */

    public TraceReader(Reader in) {
        super(in);
    }

    /**
     * Read a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
        int c = super.read();
        if (c != -1) {
            System.out.print((char) c);
        }
        return c;
    }

    /**
     * Read characters into a portion of an array.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        int count = super.read(cbuf, off, len);
        if (count != -1) {
            System.out.print(new String(cbuf, off, count));
        }
        return count;
    }
}
