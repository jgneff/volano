/*
 * HelpMenu.java - a menu for help menu items.
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

/**
 * This class defines the applet Help menu.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class HelpMenu extends Menu {
    MenuItem topicsMenuItem;
    MenuItem aboutMenuItem;

    /**
     * Creates a new applet Help menu.
     *
     * @param value  the applet parameter and property values.
     */

    HelpMenu(Value value) {
        super(value.textMenuHelp);
        topicsMenuItem = new MenuItem(value.textMenuTopics);
        aboutMenuItem  = new MenuItem(value.textMenuAbout);
        if (value.textMenuTopics.length() > 0) {
            add(topicsMenuItem);
        }
        if (value.textMenuAbout.length() > 0) {
            add(aboutMenuItem);
        }
    }
}
