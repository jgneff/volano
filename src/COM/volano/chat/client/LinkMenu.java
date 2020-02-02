/*
 * LinkMenu.java - a menu for Web links.
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

package COM.volano.chat.client;
import  java.awt.*;
import  java.net.*;
import  java.util.*;

/**
 * This class defines the customizable applet links menu.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class LinkMenu extends Menu {
    Value    value;
    String[] locations;

    /**
     * Creates a new applet People menu.
     *
     * @param value  the applet parameter and property values.
     */

    LinkMenu(Value value) {
        super(value.textMenuLinksTitle);
        this.value = value;

        StringTokenizer tokenizer = new StringTokenizer(value.textMenuLinksNames, "|");
        while (tokenizer.hasMoreTokens()) {
            add(tokenizer.nextToken().trim());
        }

        Vector links = new Vector();
        tokenizer = new StringTokenizer(value.textMenuLinksLocations, "|");
        while (tokenizer.hasMoreTokens()) {
            links.addElement(tokenizer.nextToken().trim());
        }
        locations = new String[links.size()];
        links.copyInto(locations);
    }

    /**
     * Finds the index of the target menu item.
     *
     * @param target  the target menu item.
     * @return  the corresponding index of the target menu item, or -1 if the
     *          target is not found.
     */

    private int getIndex(Object target) {
        int index = -1;
        int count = getItemCount();
        for (int i = 0; index == -1 && i < count; i++) {
            if (target == getItem(i)) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Checks whether the menu contains the specified target component.
     *
     * @param target  the target component.
     * @return  <code>true</code> if the menu contains the target component;
     *          otherwise <code>false</code>.
     */

    boolean contains(Object target) {
        return getIndex(target) != -1;
    }

    /**
     * Gets the Web location corresponding to the target component.
     *
     * @param target  the target component.
     * @return  the URL corresponding to the target menu item if found; otherwise
     *          <code>null</code>.
     */

    URL getLink(Object target) {
        URL url   = null;
        int index = getIndex(target);
        if (index != -1 && index < locations.length) {
            try {
                url = new URL(locations[index]);
            } catch (MalformedURLException e) {
                System.out.println(e);
            }
        }
        return url;
    }
}
