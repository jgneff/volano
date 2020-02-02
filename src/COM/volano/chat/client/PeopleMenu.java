/*
 * PeopleMenu.java - a menu for people menu items.
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
import  COM.volano.util.Message;
import  java.awt.*;
import  java.util.*;

/**
 * This class defines the applet People menu.
 *
 * @author  John Neffenger
 * @version 29 Aug 2000
 */

class PeopleMenu extends Menu {
    Value    value;
    Vector   ignored;
    String   selected;
    String   address;

    MenuItem ringMenuItem;
    MenuItem ignoreMenuItem;
    MenuItem countMenuItem;
    // MenuItem removeMenuItem;
    // MenuItem kickMenuItem;
    // MenuItem banMenuItem;

    /**
     * Creates a new applet People menu.
     *
     * @param value  the applet parameter and property values.
     */

    PeopleMenu(Value value) {
        super(value.textMenuPeople);
        this.value   = value;
        this.ignored = new Vector();
        setUser("", "");
    }

    /**
     * Sets the name of the currently selected user.
     *
     * @param name  the selected user's name, or an empty string if no user is
     *              selected.
     */

    synchronized void setUser(String name, String address) {
        this.selected = name;
        this.address  = address;
        // boolean extra = value.isMonitor || value.admin;  // 2.1.8

        // Remove all menu items.
        remove(ringMenuItem);
        remove(ignoreMenuItem);
        remove(countMenuItem);
        /*
            if (extra) {
              remove(removeMenuItem);
              remove(kickMenuItem);
              remove(banMenuItem);
            }
        */
        // Create and add new normal menu items.
        ringMenuItem   = new MenuItem(Message.format(value.textMenuPeopleRing, name));
        ignoreMenuItem = ignored.contains(name) ?
                         new MenuItem(Message.format(value.textMenuPeopleUnignore, name)) :
                         new MenuItem(Message.format(value.textMenuPeopleIgnore, name));
        countMenuItem  = new MenuItem(value.textMenuPeopleCount);

        if (value.textMenuPeopleRing.length() > 0) {
            add(ringMenuItem);
        }
        if (value.textMenuPeopleIgnore.length() > 0 && value.textMenuPeopleUnignore.length() > 0) {
            add(ignoreMenuItem);
        }
        if (value.textMenuPeopleCount.length() > 0) {
            add(countMenuItem);
        }

        // Create and add new monitor/admin menu items.
        /*
            if (extra) {
              removeMenuItem = new MenuItem(Message.format(value.textMenuPeopleRemove, name));
              kickMenuItem   = new MenuItem(Message.format(value.textMenuPeopleKick, address));
              banMenuItem    = new MenuItem(Message.format(value.textMenuPeopleBan, address));

              if (value.textMenuPeopleRemove.length() > 0)
                add(removeMenuItem);
              if (value.textMenuPeopleKick.length() > 0)
                add(kickMenuItem);
              if (value.textMenuPeopleBan.length() > 0)
                add(banMenuItem);
            }
        */

        // Enable menu items if appropriate.
        if (name.length() > 0) {
            ringMenuItem.setEnabled(true);
            ignoreMenuItem.setEnabled(true);
        } else {
            ringMenuItem.setEnabled(false);
            ignoreMenuItem.setEnabled(false);
        }
        /*
            if (extra) {
              if (name.length() > 0) {
                removeMenuItem.setEnabled(true);
              }
              else {
                removeMenuItem.setEnabled(false);
              }

              if (address.length() > 0) {
                kickMenuItem.setEnabled(true);
                banMenuItem.setEnabled(true);
              }
              else {
                kickMenuItem.setEnabled(false);
                banMenuItem.setEnabled(false);
              }
            }
        */
    }

    /**
     * Gets the name of the last selected user.
     *
     * @return  the name of the last selected user, or an empty string if no user
     *          is selected.
     */

    String getUserName() {
        return selected;
    }

    /**
     * Gets the IP address of the last selected user.
     *
     * @return  the IP address of the last selected user, or an empty string if no
     *          user is selected.
     */

    String getUserAddress() {
        return address;
    }

    /**
     * Ignores or stops ignoring a user.
     *
     * @param name    the name of the user to ignore or stop ignoring.
     * @param ignore  <code>true</code> to ignore the user; <code>false</code> to
     *                stop ignoring the user.
     */

    synchronized void setIgnored() {
        if (selected.length() > 0) {
            if (ignored.contains(selected)) {
                ignored.removeElement(selected);
            } else {
                ignored.addElement(selected);
            }
            setUser(selected, address);
        }
    }

    /**
     * Checks whether the user is ignored.
     *
     * @return  <code>true</code> if the user is ignored; otherwise
     *          <code>false</code>.
     */

    boolean isIgnored(String name) {
        return ignored.contains(name);
    }

    /**
     * Removes the user from the ignore list and as the selected user.
     *
     * @param name  the name to remove if ignored or selected.
     */
    /* 2.2.0
      synchronized void removeUser(String name) {
        ignored.removeElement(name);
        if (selected.equals(name))
          setUser("", "");
      }
    */
}
