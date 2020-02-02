/*
 * Theme.java - a class for defining a color and font theme.
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
import  COM.volano.awt.HyperLabel;
import  COM.volano.awt.TextPanel;
import  java.awt.*;

/**
 * This class defines a color and font theme.
 *
 * @author  John Neffenger
 * @version 24 May 2002
 */

public class Theme implements Cloneable {
    public static final int DEFAULT = 0;

    // Microsoft (R) VM for Java, 5.0 Release 5.0.0.3805
    //   Default background color = java.awt.Color[r=192,g=192,b=192]
    //   Default foreground color = java.awt.Color[r=0,g=0,b=0]
    //   Default font = java.awt.Font[family=Dialog,name=Dialog,style=plain,size=12]
    // Editable text field is lightened to java.awt.Color[r=255,g=255,b=255].

    // Sun Java(TM) 2 Runtime Environment, Standard Edition (build 1.4.0_01-b03)
    //   Default background color = java.awt.Color[r=255,g=255,b=255]
    //   Default foreground color = java.awt.Color[r=0,g=0,b=0]
    //   Default font = java.awt.Font[family=dialog,name=Dialog,style=plain,size=12]
    // Non-editable text field is set to java.awt.SystemColor[i=17].

    private Color colorBackground                     = Color.white;  // #FFFFFF
    private Color colorBackgroundButton               = Color.white;
    private Color colorBackgroundList                 = Color.white;
    private Color colorBackgroundText                 = Color.white;
    private Color colorBackgroundTextEditable         = Color.white;

    private Color colorForeground                     = Color.black;  // #000000
    private Color colorForegroundButton               = Color.black;
    private Color colorForegroundList                 = Color.black;
    private Color colorForegroundText                 = Color.black;
    private Color colorForegroundTextEditable         = Color.black;
    private Color colorForegroundTextEditableInactive = Color.gray;   // #808080
    private Color colorForegroundTextLink             = Color.blue;   // #0000FF

    private Font  fontDefault = new Font("Dialog", Font.PLAIN, 12);

    private int   index = DEFAULT;

    /**
     * Sets the color and font theme of the specified component, including any
     * contained subcomponents.
     *
     * @param component the component.
     * @param theme the color and font theme.
     */

    public static void setTheme(Component component, Theme theme) {
        setColors(component, theme);
        setFont(component, theme.getFontDefault());
    }

    /**
     * Sets the font of the specified component if the component is not a
     * container. Sets the font of the subcomponents, and not the component
     * itself, if the component is a container.
     *
     * @param component the component.
     * @param font the new font.
     */

    public static void setFont(Component component, Font font) {
        // TextPanel is a Container, so check this first.
        if (component instanceof TextPanel) {
            TextPanel panel = (TextPanel) component;
            panel.setFont(font);
            panel.invalidate();
        } else if (component instanceof Container) {
            // Don't set the container font, since that ends up changing the menu
            // font of window frames in Sun Java 1.4.
            Container container = (Container) component;
            Component[] list = container.getComponents();
            for (int i = 0; i < list.length; i++) {
                setFont(list[i], font);
            }
            container.invalidate();
            if (container instanceof Window) {
                Window window = (Window) container;
                window.pack();
            }
        } else {
            component.setFont(font);
            component.invalidate();
        }
    }

    /**
     * Sets the editable text component to the active theme colors.
     *
     * @param component the text component.
     * @param theme the color and font theme.
     */

    public static void setActive(TextComponent component, Theme theme) {
        component.setForeground(theme.getColorForegroundTextEditable());
    }

    /**
     * Sets the editable text component to the inactive theme colors.
     *
     * @param component the text component.
     * @param theme the color and font theme.
     */

    public static void setInactive(TextComponent component, Theme theme) {
        component.setForeground(theme.getColorForegroundTextEditableInactive());
    }

    /**
     * Sets the background and foreground colors of the specified component,
     * including any contained subcomponents.
     *
     * @param component the component.
     * @param theme the color and font theme.
     */

    private static void setColors(Component component, Theme theme) {
        // JTextArea is not a TextComponent, but we need to set the color here
        // depending on whether it's editable and without checking for a
        // JTextComponent instance.  TextPanel is a Container so check this first.
        if (component instanceof TextPanel) {
            TextPanel panel = (TextPanel) component;
            if (panel.isEditable()) {
                panel.setBackground(theme.getColorBackgroundTextEditable());
                panel.setForeground(theme.getColorForegroundTextEditable());
            } else {
                panel.setBackground(theme.getColorBackgroundText());
                panel.setForeground(theme.getColorForegroundText());
            }
        } else if (component instanceof Container) {
            Container container = (Container) component;
            container.setBackground(theme.getColorBackground());
            container.setForeground(theme.getColorForeground());
            Component[] list = container.getComponents();
            for (int i = 0; i < list.length; i++) {
                setColors(list[i], theme);
            }
        } else if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent) component;
            if (textComponent.isEditable()) {
                textComponent.setBackground(theme.getColorBackgroundTextEditable());
                textComponent.setForeground(theme.getColorForegroundTextEditable());
            } else {
                textComponent.setBackground(theme.getColorBackgroundText());
                textComponent.setForeground(theme.getColorForegroundText());
            }
        } else if (component instanceof List) {
            component.setBackground(theme.getColorBackgroundList());
            component.setForeground(theme.getColorForegroundList());
        } else if (component instanceof Button) {
            component.setBackground(theme.getColorBackgroundButton());
            component.setForeground(theme.getColorForegroundButton());
        } else if (component instanceof HyperLabel) {
            HyperLabel label = (HyperLabel) component;
            label.setBackground(theme.getColorBackground());
            label.setForeground(theme.getColorForeground());
            label.setLinkColor(theme.getColorForegroundTextLink());
        } else {
            component.setBackground(theme.getColorBackground());
            component.setForeground(theme.getColorForeground());
        }
        component.repaint();
    }

    /**
     * Creates a Theme object with the specified index.
     *
     * @param index the index of this theme.
     */

    Theme(int index) {
        this.index = index;
    }

    /**
     * Creates a default Theme object.
     */

    Theme() {
        this(DEFAULT);
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not
     *     support the Cloneable interface. This class does not throw the
     *     exception.
     */

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // Gets the theme index.

    int getIndex() {
        return index;
    }

    // Background colors

    Color getColorBackground() {
        return colorBackground;
    }

    void setColorBackground(Color color) {
        colorBackground = color;
    }

    Color getColorBackgroundButton() {
        return colorBackgroundButton;
    }

    void setColorBackgroundButton(Color color) {
        colorBackgroundButton = color;
    }

    Color getColorBackgroundList() {
        return colorBackgroundList;
    }

    void setColorBackgroundList(Color color) {
        colorBackgroundList = color;
    }

    Color getColorBackgroundText() {
        return colorBackgroundText;
    }

    void setColorBackgroundText(Color color) {
        colorBackgroundText = color;
    }

    Color getColorBackgroundTextEditable() {
        return colorBackgroundTextEditable;
    }

    void setColorBackgroundTextEditable(Color color) {
        colorBackgroundTextEditable = color;
    }

    // Foreground colors

    Color getColorForeground() {
        return colorForeground;
    }

    void setColorForeground(Color color) {
        colorForeground = color;
    }

    Color getColorForegroundButton() {
        return colorForegroundButton;
    }

    void setColorForegroundButton(Color color) {
        colorForegroundButton = color;
    }

    Color getColorForegroundList() {
        return colorForegroundList;
    }

    void setColorForegroundList(Color color) {
        colorForegroundList = color;
    }

    Color getColorForegroundText() {
        return colorForegroundText;
    }

    void setColorForegroundText(Color color) {
        colorForegroundText = color;
    }

    Color getColorForegroundTextEditable() {
        return colorForegroundTextEditable;
    }

    void setColorForegroundTextEditable(Color color) {
        colorForegroundTextEditable = color;
    }

    Color getColorForegroundTextEditableInactive() {
        return colorForegroundTextEditableInactive;
    }

    void setColorForegroundTextEditableInactive(Color color) {
        colorForegroundTextEditableInactive = color;
    }

    Color getColorForegroundTextLink() {
        return colorForegroundTextLink;
    }

    void setColorForegroundTextLink(Color color) {
        colorForegroundTextLink = color;
    }

    // Font

    Font getFontDefault() {
        return fontDefault;
    }

    void setFontDefault(Font font) {
        fontDefault = font;
    }
}
