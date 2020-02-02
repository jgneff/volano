/*
 * MonitorMenu.java - a menu for removing, kicking or banning.
 * Copyright (C) 1996-2002 John Neffenger
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
 * This class defines the applet Monitor menu for the Remove, Kick and Ban menu
 * items.
 *
 * @author  John Neffenger
 * @version 1 May 2002
 */

class MonitorMenu extends Menu {
    MenuItem removeMenuItem;
    MenuItem kickMenuItem;
    MenuItem banMenuItem;

    /**
     * Creates a new applet Room menu.
     *
     * @param value  the applet parameter and property values.
     */

    MonitorMenu(Value value) {
        super(value.textMenuMonitor);
        removeMenuItem = new MenuItem(value.textMenuMonitorRemove);
        kickMenuItem   = new MenuItem(value.textMenuMonitorKick);
        banMenuItem    = new MenuItem(value.textMenuMonitorBan);
        if (value.textMenuMonitorRemove.length() > 0) {
            add(removeMenuItem);
        }
        if (value.textMenuMonitorKick.length() > 0) {
            add(kickMenuItem);
        }
        if (value.textMenuMonitorBan.length() > 0) {
            add(banMenuItem);
        }
    }
}
