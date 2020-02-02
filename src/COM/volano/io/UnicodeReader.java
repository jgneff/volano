/*
 * UnicodeReader.java - reads a character stream with Unicode escape sequences.
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

package COM.volano.io;
import  java.io.*;

/**
 * This class is a FilterReader which converts all ASCII Unicode escape
 * sequences into their corresponding Unicode characters.  This filter is
 * suitable for reading files created by the JDK 1.1 <b>native2ascii</b> tool.
 * Note that this filter still takes advantage of any translation performed by
 * the underlying input stream reader, allowing for a mixture of both ASCII
 * Unicode escape sequences and natively encoded characters to be properly
 * converted into their Unicode values.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class UnicodeReader extends FilterReader {
    /**
     * Create a new Unicode reader.
     *
     * @param in  a reader.
     */

    public UnicodeReader(Reader in) {
        super(in);
    }

    /**
     * Read a single character.  Unicode escape sequences, consisting of six
     * characters in the underlying input stream reader, are compressed into the
     * single Unicode character value they represent.
     *
     * @return the character read, or -1 if the end of the stream has been
     *         reached.
     * @throws java.io.IOException  if an I/O error occurs.
     */

    public int read() throws IOException {
        int c = in.read();
        if (c == '\\') {                    // If escape sequence
            switch (c = in.read()) {
            case 't':                       // Escaped tab
                c = '\t';
                break;
            case 'n':                       // Escaped line feed
                c = '\n';
                break;
            case 'r':                       // Escaped carriage return
                c = '\r';
                break;
            case 'u':                       // Escaped Unicode character
                int d = 0;
                for (int i = 0; i < 4 && d != -1; i++) {
                    switch (c = in.read()) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        d = (d << 4) + c - '0';
                        break;
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                        d = (d << 4) + 10 + c - 'a';
                        break;
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                        d = (d << 4) + 10 + c - 'A';
                        break;
                    case -1:
                        d = -1;
                        break;
                    default:
                        d = (d << 4);
                        break;
                    }
                }
                c = d;
                break;
            default:
                break;
            }                 // End switch on c
        }                   // End if escape sequence
        return c;
    }

    /**
     * Reads characters into a portion of an array.
     *
     * @param  cbuf  the destination buffer.
     * @param  off   the offset at which to start storing characters.
     * @param  len   the maximum number of characters to read.
     * @return the number of bytes read, or -1 if the end of the stream has been
     *         reached.
     * @throws java.io.IOException  if an I/O error occurs.
     */

    public int read(char[] cbuf, int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }

        cbuf[off] = (char) c;
        int count = 1;
        try {
            while (count < len && c != -1) {
                c = read();
                if (c != -1) {
                    cbuf[off + count] = (char) c;
                    count++;
                }
            }
        } catch (IOException e) {}
        return count;
    }
}
