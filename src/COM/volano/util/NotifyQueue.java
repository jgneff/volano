/*
 * NotifyQueue.java - a vector for managing producer/consumer threads.
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
 * This class implements a queue which allows producing threads to put elements
 * on a queue and at the same time notify a consuming thread of their presence.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class NotifyQueue extends Vector {
    private boolean open = true;

    /**
     * Determines whether this queue is open to handle new elements.
     *
     * @return <code>true</code> if the queue is open; <code>false</code>
     *         otherwise.
     */

    public boolean isOpen() {
        return open;
    }

    /**
     * Closes this queue, discards any elements it contains, and allows no
     * additional elements to be added.
     */

    public synchronized void close() {
        open = false;
        removeAllElements();
        notify();
    }

    /**
     * Adds an element to the queue behind any existing elements.
     *
     * @param   object  the object to be added to the end of the queue.
     * @returns the number of elements in the queue after this elements is added.
     */

    public synchronized int putElement(Object object) {
        if (open) {
            addElement(object);
            notify();
        }
        return elementCount;
    }

    /**
     * Removes all elements from the queue and returns them to the caller, waiting
     * for an element to be placed on the queue if the queue is empty when called.
     *
     * @returns an array containing the elements in the queue.
     * @exception java.lang.InterruptedException
     *              if the calling thread is interrupted while waiting for an
     *              element to be placed on the queue.
     */

    public synchronized Object[] getElements() throws InterruptedException {
        while (open && elementCount == 0) {
            wait();
        }
        Object[] list = new Object[elementCount];
        System.arraycopy(elementData, 0, list, 0, elementCount);
        removeAllElements();
        return list;
    }
}
