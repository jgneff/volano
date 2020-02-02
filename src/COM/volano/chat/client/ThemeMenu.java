/*
 * ThemeMenu.java - a menu for selecting color themes.
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
import  java.util.*;

/**
 * This class defines the menu for selecting color themes.
 *
 * @author  John Neffenger
 * @version 24 May 2002
 */

class ThemeMenu extends Menu {
    CheckboxMenuItem defaultTheme;

    private CheckboxMenuItem selected;

    /**
     * Creates a new applet People menu.
     *
     * @param value  the applet parameter and property values.
     */

    ThemeMenu(Value value) {
        super(value.textMenuThemesTitle);

        StringTokenizer tokenizer = new StringTokenizer(value.textMenuThemesNames, "|");
        while (tokenizer.hasMoreTokens()) {
            add(new CheckboxMenuItem(tokenizer.nextToken().trim()));
        }
        if (getItemCount() > 0) {
            addSeparator();
        }
        defaultTheme = new CheckboxMenuItem(value.textMenuThemesDefault);
        add(defaultTheme);
        selectTheme(defaultTheme);
    }

    /**
     * Marks the specified theme checkbox menu item, clearing any previously
     * marked theme style checkbox.  In this way the checkbox menu items act like
     * radio buttons, allowing only one selection at a time.
     *
     * @param checkbox  the theme checkbox menu item to mark.
     */

    void selectTheme(CheckboxMenuItem checkbox) {
        if (selected != null) {
            selected.setState(false);
        }
        checkbox.setState(true);
        selected = checkbox;
    }

    /**
     * Marks the specified theme checkbox menu item, clearing any previously
     * marked theme style checkbox.  In this way the checkbox menu items act like
     * radio buttons, allowing only one selection at a time.
     *
     * @param index the index of the theme to select, starting at 1. An index of
     *     zero means the default theme.
     */

    void selectTheme(int index) {
        if (index >= 0 && index <= getItemCount()) {
            selectTheme(index == Theme.DEFAULT ? defaultTheme : (CheckboxMenuItem) getItem(index - 1));
        }
    }

    /**
     * Finds the theme index number of the target menu item.
     *
     * @param target  the target menu item.
     * @return  the corresponding theme number of the target menu item, or -1 if
     *          the target is not found.  A value of zero means the default theme.
     */

    int getThemeIndex(Object target) {
        if (target == defaultTheme) {
            return Theme.DEFAULT;
        }

        int index = -1;
        int count = getItemCount();
        for (int i = 0; index == -1 && i < count; i++) {
            if (target == getItem(i)) {
                index = i;
            }
        }
        return index + 1;
    }
}
