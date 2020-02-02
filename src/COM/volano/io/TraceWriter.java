/*
 * TraceWriter.java - writes character output stream to System.out.
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
 * This class is a FilterWriter which writes all characters to System.out before
 * writing them to the specified character output stream.
 *
 * @author  John Neffenger
 * @version 09 Nov 1999
 */

public class TraceWriter extends FilterWriter {
    /**
     * Create a new trace writer.
     *
     * @param out  a writer.
     */

    public TraceWriter(Writer out) {
        super(out);
    }

    /**
     * Write a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
        System.out.print((char) c);
        super.write(c);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char[] cbuf, int off, int len) throws IOException {
        System.out.print(new String(cbuf, off, len));
        super.write(cbuf, off, len);
    }

    /**
     * Write a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
        System.out.print(str.substring(off, off + len));
        super.write(str, off, len);
    }
}
