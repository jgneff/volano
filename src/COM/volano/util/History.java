/*
 * History.java - a vector for managing a command history.
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
 * This class manages a command history in a fashion similar to DOS DOSKEY
 * or the UNIX tcsh command history.  For example, to emulate DOSKEY you would
 * call <code>add</code> when the Enter key is pressed with text,
 * <code>back</code> when the up arrow key is pressed, <code>forward</code> when
 * the down arrow key is pressed, and <code>reset</code> when the Esc key is
 * pressed.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 * @since   20 May 1996
 */

public class History extends Vector {
    private int limit;
    private int index;

    /**
     * Creates the command history with the specified limit.
     *
     * @param limit  the number of commands to keep in the history.
     */

    public History(int limit) {
        super(limit + 1);           // Allow room for an empty 0th string
        this.limit = limit;
        addElement("");
    }

    /**
     * Moves back one element in the command history.
     *
     * @return the previous command in the history.
     */

    public String back() {
        if (index < size() - 1) {
            return (String) elementAt(++index);
        } else {
            return (String) elementAt(index);
        }
    }

    /**
     * Moves forward one element in the command history.
     *
     * @return the next command in the history.
     */

    public String forward() {
        if (index > 0) {
            return (String) elementAt(--index);
        } else {
            return (String) elementAt(index);
        }
    }

    /**
     * Adds a command to the command history.
     *
     * @param text  the command to add to the history.
     */

    public void add(String text) {
        if (size() == limit) {
            removeElementAt(limit - 1);
        }
        insertElementAt(text, 1);
    }

    /**
     * Resets the command history, setting its internal index back to the last
     * command entered.
     */

    public void reset() {
        index = 0;
    }
}
