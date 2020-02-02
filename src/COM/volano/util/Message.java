/*
 * Message.java - a class for formatting messages from pattern strings.
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

package COM.volano.util;
import  java.util.*;

/**
 * This class formats message strings by filling in patterns with the
 * substitution variables provided.  A pattern string consists of the message
 * text with substitution variables denoted by a percent sign followed by the
 * variable index.  For example:
 * <pre>
 * There were %0 connections at %1.
 * </pre>
 * where an <code>Integer</code> is specified as dropin 0 and a
 * <code>Date</code> is specified for dropin 1.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Message {

    /**
     * Formats a message with multiple substitution variables.  The variables are
     * defined in the message pattern by their index in the <code>Vector</code>
     * containing them.
     *
     * @param   pattern  the pattern determining the message format.
     * @param   source   the list of substitution variables.
     * @returns the formatted message string filled in with the values of its
     *          substitution variables.
     */

    public static String format(String pattern, Vector source) {
        int          length    = pattern.length();
        StringBuffer buffer    = new StringBuffer();
        int          prevIndex = 0;
        for (int index = pattern.indexOf('%'); index != -1; index = pattern.indexOf('%', index + 1)) {
            buffer.append(pattern.substring(prevIndex, index));
            String s = pattern.substring(index + 1, index + 2);
            try {
                // Throws NumberFormatException and ArrayIndexOutOfBoundsException
                buffer.append(source.elementAt(Integer.parseInt(s)));
            } catch (RuntimeException e) {
                buffer.append('%' + s);
            }
            prevIndex = index + 2;
        }
        if (prevIndex < length) {
            buffer.append(pattern.substring(prevIndex, length));
        }
        return buffer.toString();
    }

    /**
     * Formats a message with a single substitution variable.
     *
     * @param   pattern   the pattern determining the message format.
     * @param   variable  the substitution variable.
     * @returns the formatted message string filled in with the values of the
     *          substitution variable.
     */

    public static String format(String pattern, Object variable) {
        Vector source = new Vector(1);
        source.addElement(variable);
        return format(pattern, source);
    }
}
