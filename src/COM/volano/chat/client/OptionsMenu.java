/*
 * OptionsMenu.java - a menu for option menu items.
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
 * This class defines the applet Options menu.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

class OptionsMenu extends Menu {
    private static final String SERIF      = "Serif";
    private static final String SANSSERIF  = "SansSerif";
    private static final String MONOSPACED = "Monospaced";

    Menu             fontNameMenu;
    Menu             fontStyleMenu;
    CheckboxMenuItem regularCheckbox;
    CheckboxMenuItem italicCheckbox;
    CheckboxMenuItem boldCheckbox;
    CheckboxMenuItem bolditalicCheckbox;
    MenuItem         increaseFontMenuItem;
    MenuItem         decreaseFontMenuItem;
    CheckboxMenuItem acceptPrivateCheckbox;
    CheckboxMenuItem entranceAlertCheckbox;
    CheckboxMenuItem audioAlertCheckbox;
    CheckboxMenuItem countAlertCheckbox;
    CheckboxMenuItem webtouringCheckbox;
    // CheckboxMenuItem reverseNamesCheckbox;

    private CheckboxMenuItem fontNameCheckbox;
    private CheckboxMenuItem fontStyleCheckbox;

    /**
     * Creates a new applet Options menu.
     *
     * @param value  the applet parameter and property values.
     */

    OptionsMenu(Value value) {
        super(value.textMenuOptions);

        fontNameMenu = new Menu(value.textMenuFontName);
        // Can cause a java.lang.NullPointerException on Mac OS X.
        // String[] fontList = Toolkit.getDefaultToolkit().getFontList();
        // for (int i = 0; i < fontList.length; i++)
        //   fontNameMenu.add(new CheckboxMenuItem(fontList[i]));
        fontNameMenu.add(new CheckboxMenuItem(SERIF));
        fontNameMenu.add(new CheckboxMenuItem(SANSSERIF));
        fontNameMenu.add(new CheckboxMenuItem(MONOSPACED));

        fontStyleMenu      = new Menu(value.textMenuFontStyle);
        regularCheckbox    = new CheckboxMenuItem(value.textMenuFontRegular);
        italicCheckbox     = new CheckboxMenuItem(value.textMenuFontItalic);
        boldCheckbox       = new CheckboxMenuItem(value.textMenuFontBold);
        bolditalicCheckbox = new CheckboxMenuItem(value.textMenuFontBolditalic);
        fontStyleMenu.add(regularCheckbox);
        fontStyleMenu.add(italicCheckbox);
        fontStyleMenu.add(boldCheckbox);
        fontStyleMenu.add(bolditalicCheckbox);

        increaseFontMenuItem  = new MenuItem(value.textMenuFontIncrease);
        decreaseFontMenuItem  = new MenuItem(value.textMenuFontDecrease);
        acceptPrivateCheckbox = new CheckboxMenuItem(value.textMenuAcceptPrivate);
        entranceAlertCheckbox = new CheckboxMenuItem(value.textMenuAlertEntrance);
        audioAlertCheckbox    = new CheckboxMenuItem(value.textMenuAlertAudio);
        countAlertCheckbox    = new CheckboxMenuItem(value.textMenuAlertCount);
        webtouringCheckbox    = new CheckboxMenuItem(value.textMenuWebtouring);
        acceptPrivateCheckbox.setState(value.acceptPrivateEnabled);
        entranceAlertCheckbox.setState(value.entranceAlertsEnabled);
        audioAlertCheckbox.setState(value.audioAlertsEnabled);
        countAlertCheckbox.setState(value.countAlertsEnabled);
        webtouringCheckbox.setState(value.webtouringEnabled);

        if (value.textMenuFontName.length() > 0) {
            add(fontNameMenu);
        }
        if (value.textMenuFontStyle.length() > 0) {
            add(fontStyleMenu);
        }
        if (value.textMenuFontIncrease.length() > 0) {
            add(increaseFontMenuItem);
        }
        if (value.textMenuFontDecrease.length() > 0) {
            add(decreaseFontMenuItem);
        }
        if (getItemCount() > 0) {
            addSeparator();
        }
        if (value.textMenuAcceptPrivate.length() > 0) {
            add(acceptPrivateCheckbox);
        }
        if (value.textMenuAlertEntrance.length() > 0) {
            add(entranceAlertCheckbox);
        }
        if (value.textMenuAlertAudio.length() > 0) {
            add(audioAlertCheckbox);
        }
        if (value.textMenuAlertCount.length() > 0) {
            add(countAlertCheckbox);
        }
        if (value.textMenuWebtouring.length() > 0) {
            add(webtouringCheckbox);
        }

        selectFont(value.fontDefault);
    }

    /**
     * Gets the font style defined by the checkbox menu item.
     *
     * @param checkbox  the checkbox menu item.
     * @return  the font style defined by the checkbox menu item.
     */

    int getFontStyle(CheckboxMenuItem checkbox) {
        if (checkbox == regularCheckbox) {
            return Font.PLAIN;
        } else if (checkbox == italicCheckbox) {
            return Font.ITALIC;
        } else if (checkbox == boldCheckbox) {
            return Font.BOLD;
        } else if (checkbox == bolditalicCheckbox) {
            return Font.BOLD + Font.ITALIC;
        } else {
            return Font.PLAIN;
        }
    }

    /**
     * Selects the checkbox menu item defined by the font name.
     *
     * @param name  the font name.
     */

    void selectFontName(String name) {
        CheckboxMenuItem checkbox = null;
        int count = fontNameMenu.getItemCount();
        for (int i = 0; i < count; i++) {
            checkbox = (CheckboxMenuItem) fontNameMenu.getItem(i);
            if (name.equals(checkbox.getLabel())) {
                selectFontName(checkbox);
            }
        }
    }

    /**
     * Marks the specified font name checkbox menu item, clearing any previously
     * marked font name checkbox.  In this way the checkbox menu items act like
     * radio buttons, allowing only one selection at a time.
     *
     * @param checkbox  the font name checkbox menu item to mark.
     */

    void selectFontName(CheckboxMenuItem checkbox) {
        if (fontNameCheckbox != null) {
            fontNameCheckbox.setState(false);
        }
        checkbox.setState(true);
        fontNameCheckbox = checkbox;
    }

    /**
     * Selects the checkbox menu item defined by the font style.
     *
     * @param style  the font style.
     */

    void selectFontStyle(int style) {
        CheckboxMenuItem checkbox = null;
        switch (style) {
        case Font.PLAIN:
            checkbox = regularCheckbox;
            break;
        case Font.ITALIC:
            checkbox = italicCheckbox;
            break;
        case Font.BOLD:
            checkbox = boldCheckbox;
            break;
        case Font.BOLD + Font.ITALIC:
            checkbox = bolditalicCheckbox;
            break;
        default:
            checkbox = regularCheckbox;
            break;
        }
        selectFontStyle(checkbox);
    }

    /**
     * Marks the specified font style checkbox menu item, clearing any previously
     * marked font style checkbox.  In this way the checkbox menu items act like
     * radio buttons, allowing only one selection at a time.
     *
     * @param checkbox  the font style checkbox menu item to mark.
     */

    void selectFontStyle(CheckboxMenuItem checkbox) {
        if (fontStyleCheckbox != null) {
            fontStyleCheckbox.setState(false);
        }
        checkbox.setState(true);
        fontStyleCheckbox = checkbox;
    }

    /**
     * Selects the checkbox menu items defined by specified font.
     *
     * @param font  the font to mark as selected.
     */

    void selectFont(Font font) {
        if (font != null) {
            selectFontName(font.getName());
            selectFontStyle(font.getStyle());
        }
    }
}
