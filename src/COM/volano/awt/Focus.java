/*
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

package COM.volano.awt;

import java.awt.*;
import java.util.*;

/**
 *  Class to keep track of the keyboard focus.
 *
 *  <P>Placing a Focus object onto a panel ensures that the keyboard
 *  focus will be initially set correctly, and will be remembered when
 *  the user minimizes/maximizes the window.
 *
 *  <P>(I wanted to call this FocusManager, but that name conflicts
 *  with a package-protected class in the java.awt package.)
 */
public class Focus extends Canvas {

    private Component first;     // the first component in the focus list
    private Component last;      // the last component in the focus list
    private Component current;   // the component that currently has the focus
    private Hashtable next = new Hashtable();  // maps current to next focusable component
    private Hashtable prev = new Hashtable();  // maps current to previous focusable component


    /**
     *  Constructs a new Focus object.
     *
     *  @param first  The first component in the focus list.
     */
    public Focus(Component first) {
        this.first = first;
        this.last = first;
        this.current = first;
    }

    /**
     *  Adds a component to the focus list.
     *
     *  @param c The component to add.
     */
    public void add(Component c) {
        next.put(last, c);
        prev.put(c, last);
        last = c;
    }

    /**
     *  Replaces one specified component with another in this focus list.
     *
     *  @param c1  The component to replace
     *  @param c2  The replacement component
     */
    public void replace(Component c1, Component c2) {
        Component n = (Component)next.remove(c1);
        if (n != null) {
            next.put(c2, n);
        }
        n = (Component)prev.remove(c1);
        if (n != null) {
            prev.put(c2, n);
        }
        if (c1 == first) {
            first = c2;
        }
        if (c1 == last) {
            last = c2;
        }
        if (c1 == current) {
            current = c2;
            c2.requestFocus();
        }
    }


    /**
     *  Moves the keyboard focus to the previous focusable component.
     */
    public void previous() {
        current = (Component)prev.get(current);
        if (current == null) {
            current = last;
        }
        current.requestFocus();
    }

    /**
     *  Moves the keyboard to the next focusable component.
     */
    public void next() {
        current = (Component)next.get(current);
        if (current == null) {
            current = first;
        }
        current.requestFocus();
    }

    /**
     *  Paints this component.
     *  <P>This method calls requestFocus() on the current focusable component.
     *  Because the AWT only calls this method when the Focus object
     *  (and therefore its parent) is onscreen, requestFocus() is garunteed
     *  to work.
     *
     *  @param g The graphics context on which to paint.
     */
    public void paint(Graphics g) {
        super.paint(g);
        current.requestFocus();
    }

    /**
     *  Updates this component.
     *  <P>This method calls requestFocus() on the current focusable component.
     *  Because the AWT only calls this method when the Focus object
     *  (and therefore its parent) is onscreen, requestFocus() is garunteed
     *  to work.
     *
     *  @param g The graphics context on which to paint.
     */
    public void update(Graphics g) {
        super.update(g);
        current.requestFocus();
    }


    /**
     *  Sets the currently focusable component.
     *
     *  @param component The component to receive the focus.
     */
    public void setCurrent(Component component) {
        current = component;
        repaint();
    }


    /**
     *  Returns 1x1.  A Focus object must be visible in order to work.
     *
     *  @return 1x1.
     */
    public Dimension getPreferredSize() {
        return new Dimension(1, 1);
    }


    /**
     *  Returns 1x1.  A Focus object must be visible in order to work.
     *
     *  @return 1x1.
     */
    public Dimension getMinimumSize() {
        return new Dimension(1, 1);
    }

}
