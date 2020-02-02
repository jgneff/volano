/*
 * Formatter.java - a Java servlet for getting files.
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

package COM.volano;

import java.io.*;
import java.util.*;

/**
 *  Formats a template with properties from a <CODE>Properties</CODE> object.
 *
 * @author Paul Jack
 */
public class Formatter {

    final static int GREATER_THAN_SIGN = (int)'>';
    final static int SLASH = (int)'/';
    final static int LESS_THAN_SIGN = (int)'<';

    Reader reader;       // reader to read template from
    PrintWriter writer;  // write to write modified template to
    Properties props;    // properties to substitute


    /**
     * Construct a new Formatter object.
     *
     * @param reader The reader to read the template from
     * @param writer The writer to write the modified template to
     * @param props  The properties to substitute
     */
    public Formatter(Reader reader, Writer writer, Properties props) {
        this.props = props;
        this.reader = reader;
        this.writer = (writer instanceof PrintWriter) ? (PrintWriter)writer : new PrintWriter(writer);
    }


    /**
     * Reads characters from this <CODE>Formatter</CODE>'s reader and
     * echoes them to the writer until either the given character or
     * the end of the stream is encountered.
     *
     * @param itarget The character to scan for
     * @throws IOException if an I/O error occurs
     */
    public void scanFor(int itarget) throws IOException {
        for (int ch = reader.read(); (ch != itarget) && (ch != -1); ch = reader.read()) {
            writer.write(ch);
        }
    }


    /**
     * Reads characters from the stream and echoes them to the writer until
     * a property tag is encountered; the name of the property in the tag
     * is then returned.
     * If the end of the stream is encountered, null is returned.
     *
     * @return The name of the next property in the template
     * @throws IOException if an I/O error occurs
     */
    public String getNextPropertyName() throws IOException {
        StringBuffer tag = new StringBuffer(256); // this holds the property name
        while (true) {
            // echo all characters until a potential property name is discovered
            scanFor(LESS_THAN_SIGN);
            boolean searchingForEndOfTag = true;
            while (searchingForEndOfTag) {
                int ch = reader.read();
                switch (ch) {
                case -1:
                    return null; // end of stream
                case LESS_THAN_SIGN:
                    // nested tag.  The contents of the string buffer don't represent
                    // a property name, so we write them to the stream and then
                    // reset the string buffer.  Note that, since we encountered another
                    // less than sign, we're still "searchingForEndOfTag."
                    writer.print((char)LESS_THAN_SIGN);
                    writer.print(tag);
                    tag.setLength(0);
                    break;
                case GREATER_THAN_SIGN:
                    // normal HTML tag.  Since we didn't encounter a SLASH, we want
                    // want to flush the tag to the writer, reset the string buffer
                    // and search for the beginning of the next tag (the next
                    // less than sign) -- we're no longer "searchingForEndOfTag".
                    writer.print((char)LESS_THAN_SIGN);
                    writer.print(tag);
                    writer.print((char)ch);
                    tag.setLength(0);
                    searchingForEndOfTag = false;
                    break;
                case SLASH:
                    // Potential property name; but this could be something like
                    // </A> so we have to check the next character.
                    int ch2 = reader.read();
                    if (ch2 == GREATER_THAN_SIGN) { // it's a property name!
                        return tag.toString();   // so, return it.
                    } else {
                        // It's probably an HTML end tag.  In any case, we don't care
                        // about it.  Write it to the stream, reset the string buffer,
                        // look for the beginning of the next tag.
                        writer.print((char)LESS_THAN_SIGN);
                        writer.print(tag);
                        writer.print((char)ch);
                        writer.print((char)ch2);
                        tag.setLength(0);
                        searchingForEndOfTag = false;
                    }
                    break;
                default:
                    tag.append((char)ch);
                }
            }
        }
    }


    /**
     * Performs the translation.
     * This method reads the template from the reader, finds all of the
     * property tags, and substitutes them with the corresponding property
     * values.  If no such value is found, the property tag itself is written
     * without substitution.
     * <P>
     * This method closes neither the reader nor the writer.
     *
     * @throws IOException if an I/O error occurs during translation.
     */
    public void translate() throws IOException {
        for (String s = getNextPropertyName(); s != null; s = getNextPropertyName()) {
            String value = props.getProperty(s);
            if (value != null) {
                writer.print(value);
            } else { // no value, so write the tag itself.
                writer.print((char)LESS_THAN_SIGN);
                writer.print(s);
                writer.print((char)SLASH);
                writer.print((char)GREATER_THAN_SIGN);
            }
        }
        writer.flush();
    }


    /**
     * Convenience method for testing.  Template and properties files are
     * read, and their translation is written to System.out.
     *
     * @param args The command-line arguments. args[0] is the filename
     *             of the properties file and args[1] is the filename of the
     *             template.
     * @throws Exception No error control is attempted.  This method is for
     *                   debugging purposes only.
     */
    public static void main(String args[]) throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream(args[0]));
        FileReader r = new FileReader(args[1]);
        new Formatter(r, new OutputStreamWriter(System.out), p).translate();
    }

}
